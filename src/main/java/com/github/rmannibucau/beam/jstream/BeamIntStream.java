package com.github.rmannibucau.beam.jstream;

import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.values.PCollection;

// TODO
public class BeamIntStream implements IntStream {
    private final PipelineOptions pipelineOptions;
    private final Pipeline pipeline;
    private final PCollection<Integer> output;
    private final Collection<Runnable> closeCallbacks;

    BeamIntStream(final PipelineOptions pipelineOptions, final Pipeline pipeline, final PCollection<Integer> output,
                       final Collection<Runnable> closeCallbacks) {
        this.pipelineOptions = pipelineOptions;
        this.pipeline = pipeline;
        this.output = output;
        this.closeCallbacks = closeCallbacks;
    }

    @Override
    public IntStream filter(final IntPredicate predicate) {
        return null;
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
        return null;
    }

    @Override
    public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
        return null;
    }

    @Override
    public LongStream mapToLong(IntToLongFunction mapper) {
        return null;
    }

    @Override
    public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
        return null;
    }

    @Override
    public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
        return null;
    }

    @Override
    public IntStream distinct() {
        return null;
    }

    @Override
    public IntStream sorted() {
        return null;
    }

    @Override
    public IntStream peek(IntConsumer action) {
        return null;
    }

    @Override
    public IntStream limit(long maxSize) {
        return null;
    }

    @Override
    public IntStream skip(long n) {
        return null;
    }

    @Override
    public void forEach(IntConsumer action) {

    }

    @Override
    public void forEachOrdered(IntConsumer action) {

    }

    @Override
    public int[] toArray() {
        return new int[0];
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
        return 0;
    }

    @Override
    public OptionalInt reduce(IntBinaryOperator op) {
        return null;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        return null;
    }

    @Override
    public int sum() {
        return 0;
    }

    @Override
    public OptionalInt min() {
        return null;
    }

    @Override
    public OptionalInt max() {
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
    public IntSummaryStatistics summaryStatistics() {
        return null;
    }

    @Override
    public boolean anyMatch(IntPredicate predicate) {
        return false;
    }

    @Override
    public boolean allMatch(IntPredicate predicate) {
        return false;
    }

    @Override
    public boolean noneMatch(IntPredicate predicate) {
        return false;
    }

    @Override
    public OptionalInt findFirst() {
        return null;
    }

    @Override
    public OptionalInt findAny() {
        return null;
    }

    @Override
    public LongStream asLongStream() {
        return null;
    }

    @Override
    public DoubleStream asDoubleStream() {
        return null;
    }

    @Override
    public Stream<Integer> boxed() {
        return null;
    }

    @Override
    public IntStream sequential() {
        return null;
    }

    @Override
    public IntStream parallel() {
        return null;
    }

    @Override
    public IntStream unordered() {
        return null;
    }

    @Override
    public IntStream onClose(Runnable closeHandler) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
        return null;
    }

    @Override
    public Spliterator.OfInt spliterator() {
        return null;
    }

    @Override
    public boolean isParallel() {
        return false;
    }
}
