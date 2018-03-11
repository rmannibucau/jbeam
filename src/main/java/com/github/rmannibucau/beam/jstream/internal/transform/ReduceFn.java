package com.github.rmannibucau.beam.jstream.internal.transform;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.GlobalWindow;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Instant;

// todo: make it actually distributed properly but ATM beam misses that level
public class ReduceFn<T, U> extends DoFn<KV<?, T>, U> {
    private U identity;
    private U current;
    private BiFunction<U, ? super T, U> accumulator;
    private BinaryOperator<U> combiner;

    protected ReduceFn() {
        // no-op
    }

    public ReduceFn(final U identity, final BiFunction<U, ? super T, U> accumulator, final BinaryOperator<U> combiner) {
        this.identity = identity;
        this.current = identity;
        this.accumulator = accumulator;
        this.combiner = combiner;
    }

    @ProcessElement
    public void onElement(final ProcessContext context) {
        current = accumulator.apply(current, context.element().getValue());
    }

    @FinishBundle
    public void onFinishBundle(final FinishBundleContext context) {
        if (identity != current) {
            context.output(current, Instant.now(), GlobalWindow.INSTANCE);
        }
    }
}
