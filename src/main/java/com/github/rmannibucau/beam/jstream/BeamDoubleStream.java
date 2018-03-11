package com.github.rmannibucau.beam.jstream;

import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.values.PCollection;

// TODO
public class BeamDoubleStream implements DoubleStream {
    private final PipelineOptions pipelineOptions;
    private final Pipeline pipeline;
    private final PCollection<Double> output;
    private final Collection<Runnable> closeCallbacks;

    BeamDoubleStream(final PipelineOptions pipelineOptions, final Pipeline pipeline, final PCollection<Double> output,
                       final Collection<Runnable> closeCallbacks) {
        this.pipelineOptions = pipelineOptions;
        this.pipeline = pipeline;
        this.output = output;
        this.closeCallbacks = closeCallbacks;
    }

    @Override
    public DoubleStream filter(DoublePredicate predicate) {
        return null;
    }

    @Override
    public DoubleStream map(DoubleUnaryOperator mapper) {
        return null;
    }

    @Override
    public <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper) {
        return null;
    }

    @Override
    public IntStream mapToInt(DoubleToIntFunction mapper) {
        return null;
    }

    @Override
    public LongStream mapToLong(DoubleToLongFunction mapper) {
        return null;
    }

    @Override
    public DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
        return null;
    }

    @Override
    public DoubleStream distinct() {
        return null;
    }

    @Override
    public DoubleStream sorted() {
        return null;
    }

    @Override
    public DoubleStream peek(DoubleConsumer action) {
        return null;
    }

    @Override
    public DoubleStream limit(long maxSize) {
        return null;
    }

    @Override
    public DoubleStream skip(long n) {
        return null;
    }

    @Override
    public void forEach(DoubleConsumer action) {

    }

    @Override
    public void forEachOrdered(DoubleConsumer action) {

    }

    @Override
    public double[] toArray() {
        return new double[0];
    }

    @Override
    public double reduce(double identity, DoubleBinaryOperator op) {
        return 0;
    }

    @Override
    public OptionalDouble reduce(DoubleBinaryOperator op) {
        return null;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        return null;
    }

    @Override
    public double sum() {
        return 0;
    }

    @Override
    public OptionalDouble min() {
        return null;
    }

    @Override
    public OptionalDouble max() {
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
    public DoubleSummaryStatistics summaryStatistics() {
        return null;
    }

    @Override
    public boolean anyMatch(DoublePredicate predicate) {
        return false;
    }

    @Override
    public boolean allMatch(DoublePredicate predicate) {
        return false;
    }

    @Override
    public boolean noneMatch(DoublePredicate predicate) {
        return false;
    }

    @Override
    public OptionalDouble findFirst() {
        return null;
    }

    @Override
    public OptionalDouble findAny() {
        return null;
    }

    @Override
    public Stream<Double> boxed() {
        return null;
    }

    @Override
    public DoubleStream sequential() {
        return null;
    }

    @Override
    public DoubleStream parallel() {
        return null;
    }

    @Override
    public DoubleStream unordered() {
        return null;
    }

    @Override
    public DoubleStream onClose(Runnable closeHandler) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public PrimitiveIterator.OfDouble iterator() {
        return null;
    }

    @Override
    public Spliterator.OfDouble spliterator() {
        return null;
    }

    @Override
    public boolean isParallel() {
        return false;
    }
}
