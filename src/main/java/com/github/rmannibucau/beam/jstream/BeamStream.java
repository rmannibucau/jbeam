package com.github.rmannibucau.beam.jstream;

import static com.github.rmannibucau.beam.jstream.internal.Globals.assertSerializable;
import static com.github.rmannibucau.beam.jstream.internal.Globals.serializable;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.github.rmannibucau.beam.jstream.configuration.RunOptions;
import com.github.rmannibucau.beam.jstream.internal.LambdaFunction;
import com.github.rmannibucau.beam.jstream.internal.transform.InMemorySorter;
import com.github.rmannibucau.beam.jstream.internal.transform.LimitFn;
import com.github.rmannibucau.beam.jstream.internal.transform.SkipFn;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.Distinct;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PCollection;

// todo: revisit names
public class BeamStream<T> implements Stream<T>, Serializable {
    private static final Coder<?> GLOBAL_CODER = SerializableCoder.of(Serializable.class);

    private transient final PipelineOptions pipelineOptions;
    private transient final Pipeline pipeline;
    private transient final PCollection<T> output;
    private transient final Collection<Runnable> closeCallbacks;

    private BeamStream(final PipelineOptions pipelineOptions, final Pipeline pipeline, final PCollection<T> output,
                       final Collection<Runnable> closeCallbacks) {
        this.pipelineOptions = pipelineOptions;
        this.pipeline = pipeline;
        this.output = output.setCoder((Coder<T>) GLOBAL_CODER); // todo
        this.closeCallbacks = closeCallbacks;
    }

    // todo: from is a bad parameter, use a source?
    public static <T> BeamStream<T> of(final PipelineOptions options, final Stream<T> from) {
        final Pipeline pipeline = Pipeline.create(options);
        return new BeamStream<>(options, pipeline, pipeline.apply(Create.of(from.collect(toList()))), new ArrayList<>());
    }

    @Override // todo: add Serializable constraint
    public BeamStream<T> filter(final Predicate<? super T> predicate) {
        assertSerializable(predicate);
        final SerializedLambda lambda = serializable(predicate);
        return sfilter(new LambdaFunction<T, Boolean, Predicate<? super T>>(lambda) {
            @Override
            protected Boolean doApply(final Predicate<? super T> delegate, final T input) {
                return delegate.test(input);
            }
        });
    }

    private BeamStream<T> sfilter(final SimpleFunction<T, Boolean> predicate) {
        return new BeamStream<>(
                pipelineOptions, pipeline,
                output.apply("filter_" + predicate, Filter.by(predicate)),
                closeCallbacks);
    }

    @Override
    public <R> BeamStream<R> map(final Function<? super T, ? extends R> mapper) {
        final SerializedLambda lambda = serializable(mapper);
        return smap(new LambdaFunction<T, R, Function<? super T, ? extends R>>(lambda) {
            @Override
            protected R doApply(final Function<? super T, ? extends R> delegate, final T input) {
                return delegate.apply(input);
            }
        });
    }

    private <R> BeamStream<R> smap(final SimpleFunction<? super T, ? extends R> mapper) {
        return new BeamStream<>(
                pipelineOptions, pipeline,
                output.apply(
                        "map_" + mapper,
                        MapElements.via(new SimpleFunction<T, R>() {
                            @Override
                            public R apply(final T input) {
                                return mapper.apply(input);
                            }
                        })),
                closeCallbacks);
    }

    @Override
    public BeamIntStream mapToInt(final ToIntFunction<? super T> mapper) {
        assertSerializable(mapper);
        return new BeamIntStream(
                pipelineOptions, pipeline,
                output.apply(
                        "mapToInt_" + mapper,
                        MapElements.via(new SimpleFunction<T, Integer>() {
                            @Override
                            public Integer apply(final T input) {
                                return mapper.applyAsInt(input);
                            }
                        })),
                closeCallbacks);
    }

    @Override
    public BeamLongStream mapToLong(final ToLongFunction<? super T> mapper) {
        assertSerializable(mapper);
        return new BeamLongStream(
                pipelineOptions, pipeline,
                output.apply(
                        "mapToLong_" + mapper,
                        MapElements.via(new SimpleFunction<T, Long>() {
                            @Override
                            public Long apply(final T input) {
                                return mapper.applyAsLong(input);
                            }
                        })),
                closeCallbacks);
    }

    @Override
    public BeamDoubleStream mapToDouble(final ToDoubleFunction<? super T> mapper) {
        assertSerializable(mapper);
        return new BeamDoubleStream(
                pipelineOptions, pipeline,
                output.apply(
                        "mapToDouble_" + mapper,
                        MapElements.via(new SimpleFunction<T, Double>() {
                            @Override
                            public Double apply(final T input) {
                                return mapper.applyAsDouble(input);
                            }
                        })),
                closeCallbacks);
    }

    @Override
    public <R> BeamStream<R> flatMap(final Function<? super T, ? extends Stream<? extends R>> mapper) {
        assertSerializable(mapper);
        return serializableFlatMap(new SimpleFunction<T, Stream<? extends R>>() {
            @Override
            public Stream<? extends R> apply(final T input) {
                return mapper.apply(input);
            }
        });
    }

    private  <R> BeamStream<R> serializableFlatMap(final SimpleFunction<? super T, ? extends Stream<? extends R>> mapper) {
        final SimpleFunction<T, Iterable<R>> fn = new SimpleFunction<T, Iterable<R>>() {
            @Override
            public Iterable<R> apply(final T input) {
                return ((Stream<R>) mapper.apply(input))::iterator;
            }
        };
        return new BeamStream<>(
                pipelineOptions, pipeline,
                output.apply("flatMap_" + mapper, FlatMapElements.into(fn.getOutputTypeDescriptor()).via(fn)),
                closeCallbacks);
    }

    @Override
    public BeamIntStream flatMapToInt(final Function<? super T, ? extends IntStream> mapper) {
        assertSerializable(mapper);
        final SimpleFunction<T, Iterable<Integer>> fn = new SimpleFunction<T, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(final T input) {
                final IntStream stream = mapper.apply(input);
                return stream::iterator;
            }
        };
        return new BeamIntStream(
                pipelineOptions, pipeline,
                output.apply("flatMapToInt_" + mapper, FlatMapElements.into(fn.getOutputTypeDescriptor()).via(fn)),
                closeCallbacks);
    }

    @Override
    public BeamLongStream flatMapToLong(final Function<? super T, ? extends LongStream> mapper) {
        assertSerializable(mapper);
        final SimpleFunction<T, Iterable<Long>> fn = new SimpleFunction<T, Iterable<Long>>() {
            @Override
            public Iterable<Long> apply(final T input) {
                final LongStream stream = mapper.apply(input);
                return stream::iterator;
            }
        };
        return new BeamLongStream(
                pipelineOptions, pipeline,
                output.apply("flatMapToLong_" + mapper, FlatMapElements.into(fn.getOutputTypeDescriptor()).via(fn)),
                closeCallbacks);
    }

    @Override
    public BeamDoubleStream flatMapToDouble(final Function<? super T, ? extends DoubleStream> mapper) {
        assertSerializable(mapper);
        final SimpleFunction<T, Iterable<Double>> fn = new SimpleFunction<T, Iterable<Double>>() {
            @Override
            public Iterable<Double> apply(final T input) {
                final DoubleStream stream = mapper.apply(input);
                return stream::iterator;
            }
        };
        return new BeamDoubleStream(
                pipelineOptions, pipeline,
                output.apply("flatMapToDouble" + mapper, FlatMapElements.into(fn.getOutputTypeDescriptor()).via(fn)),
                closeCallbacks);
    }

    @Override
    public BeamStream<T> distinct() {
        return new BeamStream<>(
                pipelineOptions, pipeline,
                output.apply("distinct", Distinct.create()),
                closeCallbacks);
    }

    @Override
    public BeamStream<T> sorted() {
        return sorted((Comparator<T>) Comparator.naturalOrder());
    }

    @Override
    public BeamStream<T> sorted(final Comparator<? super T> comparator) {
        assertSerializable(comparator);
        return new BeamStream<>(
                pipelineOptions, pipeline,
                output.apply("sorted", new InMemorySorter<>(comparator)),
                closeCallbacks);
    }

    @Override
    public BeamStream<T> peek(final Consumer<? super T> action) {
        assertSerializable(action);
        return new BeamStream<>(
                pipelineOptions, pipeline,
                output.apply(
                        "peek_" + action,
                        MapElements.via(new SimpleFunction<T, T>() {
                            @Override
                            public T apply(final T input) {
                                action.accept(input);
                                return input;
                            }
                        })),
                closeCallbacks);
    }

    @Override
    public BeamStream<T> limit(final long maxSize) {
        return new BeamStream<>(
                pipelineOptions, pipeline,
                output.apply(
                        "limit_" + maxSize,
                        ParDo.of(new LimitFn<>(maxSize))),
                closeCallbacks);
    }

    @Override
    public BeamStream<T> skip(final long n) {
        return new BeamStream<>(
                pipelineOptions, pipeline,
                output.apply(
                        "skip_" + n,
                        ParDo.of(new SkipFn<>(n))),
                closeCallbacks);
    }

    @Override
    public void forEach(final Consumer<? super T> action) {
        assertSerializable(action);
        peek(action);
        doRun();
    }

    @Override
    public void forEachOrdered(final Consumer<? super T> action) {
        assertSerializable(action);
        // todo: do we want to skip sorted which will likely always/often fail?
        sorted().forEach(action);
    }

    @Override
    public Object[] toArray() {
        return localExecution();
    }

    @Override
    public <A> A[] toArray(final IntFunction<A[]> generator) {
        return localExecution();
    }

    @Override
    public T reduce(final T identity, final BinaryOperator<T> accumulator) {
        assertSerializable(accumulator);
        return reduce(identity, accumulator, accumulator);
    }

    @Override
    public Optional<T> reduce(final BinaryOperator<T> accumulator) {
        return ofNullable(reduce(null, accumulator));
    }

    @Override
    public <U> U reduce(final U identity, final BiFunction<U, ? super T, U> accumulator, final BinaryOperator<U> combiner) {
        /* todo: once we can get back a local value after pipeline execution
        assertSerializable(identity);
        assertSerializable(accumulator);
        assertSerializable(combiner);
        return new BeamDoubleStream<>(
                pipelineOptions, pipeline,
                output.apply(ParDo.of(new SingleKeyKVCreator<>()))
                        .apply(GroupByKey.create())
                        .apply("reduce_" + identity + "-" + accumulator, ParDo.of(new ReduceFn<>(identity, accumulator, combiner))),
                closeCallbacks);
        */
        return localExecution();
    }

    @Override
    public <R> R collect(final Supplier<R> supplier, final BiConsumer<R, ? super T> accumulator, final BiConsumer<R, R> combiner) {
        assertSerializable(supplier);
        assertSerializable(accumulator);
        assertSerializable(combiner);
        return localExecution();
    }

    @Override
    public <R, A> R collect(final Collector<? super T, A, R> collector) {
        assertSerializable(collector);
        return localExecution();
    }

    @Override
    public Optional<T> min(final Comparator<? super T> comparator) {
        assertSerializable(comparator);
        return localExecution();
    }

    @Override
    public Optional<T> max(final Comparator<? super T> comparator) {
        assertSerializable(comparator);
        return localExecution();
    }

    @Override
    public long count() {
        return localExecution();
    }

    @Override
    public boolean anyMatch(final Predicate<? super T> predicate) {
        assertSerializable(predicate);
        return localExecution();
    }

    @Override
    public boolean allMatch(final Predicate<? super T> predicate) {
        assertSerializable(predicate);
        return localExecution();
    }

    @Override
    public boolean noneMatch(final Predicate<? super T> predicate) {
        assertSerializable(predicate);
        return localExecution();
    }

    @Override
    public Optional<T> findFirst() {
        return localExecution();
    }

    @Override
    public Optional<T> findAny() {
        return localExecution();
    }

    @Override
    public Iterator<T> iterator() {
        return localExecution();
    }

    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE | Spliterator.CONCURRENT);
    }

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public BeamStream<T> sequential() {
        return this;
    }

    @Override
    public BeamStream<T> parallel() {
        return this;
    }

    @Override
    public BeamStream<T> unordered() {
        return this;
    }

    @Override
    public BeamStream<T> onClose(final Runnable closeHandler) {
        assertSerializable(closeHandler);
        return new BeamStream<>(
                pipelineOptions, pipeline, output,
                Stream.concat(closeCallbacks.stream(), Stream.of(closeHandler)).collect(toList()));
    }

    @Override
    public void close() {
        doRun(); // TODO: should it be?

        // do we want to add them in the pipeline as a single sink?
        RuntimeException re = null;
        for (final Runnable r : closeCallbacks) {
            try {
                r.run();
            } catch (final RuntimeException e) {
                if (re == null) {
                    re = e;
                } else {
                    re.addSuppressed(e);
                }
            }
        }
        closeCallbacks.clear();
        if (re != null) {
            throw re;
        }
    }

    private PipelineResult doRun() {
        final PipelineResult run = pipeline.run();
        if (pipelineOptions.as(RunOptions.class).getStreamRunAwaitUntilFinish()) {
            run.waitUntilFinish();
        }
        return run;
    }

    // this is however doable in a few cases, do we want it part of the API for these cases?
    private <T> T localExecution() {
        throw new IllegalArgumentException("Local execution not yet supported");
    }
}
