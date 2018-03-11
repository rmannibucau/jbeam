package com.github.rmannibucau.beam.jstream;

import java.util.Collection;
import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.values.PCollection;

// TODO
public class BeamLongStream implements LongStream {
    private final PipelineOptions pipelineOptions;
    private final Pipeline pipeline;
    private final PCollection<Long> output;
    private final Collection<Runnable> closeCallbacks;

    BeamLongStream(final PipelineOptions pipelineOptions, final Pipeline pipeline, final PCollection<Long> output,
                       final Collection<Runnable> closeCallbacks) {
        this.pipelineOptions = pipelineOptions;
        this.pipeline = pipeline;
        this.output = output;
        this.closeCallbacks = closeCallbacks;
    }

    @Override
    public LongStream filter(LongPredicate predicate) {
        return null;
    }

    @Override
    public LongStream map(LongUnaryOperator mapper) {
        return null;
    }

    @Override
    public <U> Stream<U> mapToObj(LongFunction<? extends U> mapper) {
        return null;
    }

    @Override
    public IntStream mapToInt(LongToIntFunction mapper) {
        return null;
    }

    @Override
    public DoubleStream mapToDouble(LongToDoubleFunction mapper) {
        return null;
    }

    @Override
    public LongStream flatMap(LongFunction<? extends LongStream> mapper) {
        return null;
    }

    @Override
    public LongStream distinct() {
        return null;
    }

    @Override
    public LongStream sorted() {
        return null;
    }

    @Override
    public LongStream peek(LongConsumer action) {
        return null;
    }

    @Override
    public LongStream limit(long maxSize) {
        return null;
    }

    @Override
    public LongStream skip(long n) {
        return null;
    }

    @Override
    public void forEach(LongConsumer action) {

    }

    @Override
    public void forEachOrdered(LongConsumer action) {

    }

    @Override
    public long[] toArray() {
        return new long[0];
    }

    @Override
    public long reduce(long identity, LongBinaryOperator op) {
        return 0;
    }

    @Override
    public OptionalLong reduce(LongBinaryOperator op) {
        return null;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        return null;
    }

    @Override
    public long sum() {
        return 0;
    }

    @Override
    public OptionalLong min() {
        return null;
    }

    @Override
    public OptionalLong max() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public OptionalDouble average() {
        return null;
    }

    @Override
    public LongSummaryStatistics summaryStatistics() {
        return null;
    }

    @Override
    public boolean anyMatch(LongPredicate predicate) {
        return false;
    }

    @Override
    public boolean allMatch(LongPredicate predicate) {
        return false;
    }

    @Override
    public boolean noneMatch(LongPredicate predicate) {
        return false;
    }

    @Override
    public OptionalLong findFirst() {
        return null;
    }

    @Override
    public OptionalLong findAny() {
        return null;
    }

    @Override
    public DoubleStream asDoubleStream() {
        return null;
    }

    @Override
    public Stream<Long> boxed() {
        return null;
    }

    @Override
    public LongStream sequential() {
        return null;
    }

    @Override
    public LongStream parallel() {
        return null;
    }

    @Override
    public LongStream unordered() {
        return null;
    }

    @Override
    public LongStream onClose(Runnable closeHandler) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public PrimitiveIterator.OfLong iterator() {
        return null;
    }

    @Override
    public Spliterator.OfLong spliterator() {
        return null;
    }

    @Override
    public boolean isParallel() {
        return false;
    }
}
