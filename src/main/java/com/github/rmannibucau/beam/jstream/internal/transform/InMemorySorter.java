package com.github.rmannibucau.beam.jstream.internal.transform;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

public final class InMemorySorter<T> extends PTransform<PCollection<T>, PCollection<T>> {
    private Comparator<? super T> comparator;

    protected InMemorySorter() {
        // no-op
    }

    public InMemorySorter(final Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public PCollection<T> expand(final PCollection<T> input) {
        return input.apply(ParDo.of(new SingleKeyKVCreator<>()))
                .apply(GroupByKey.create())
                .apply(ParDo.of(new Sort<>(comparator)))
                .apply(Flatten.iterables());
    }

    protected final class Sort<T> extends DoFn<KV<Integer, Iterable<T>>, List<T>> {
        private Comparator<? super T> comparator;

        protected Sort() {
            // no-op
        }

        protected Sort(final Comparator<? super T> comparator) {
            this.comparator = comparator;
        }

        @ProcessElement
        public void onElement(final ProcessContext context) {
            final Iterable<? extends T> value = context.element().getValue();
            final List<T> list = StreamSupport.stream(value.spliterator(), false).collect(toList());
            list.sort(comparator);
            context.output(list);
        }
    }
}
