package com.github.rmannibucau.beam.jstream.internal.transform;

import org.apache.beam.sdk.transforms.DoFn;

public class LimitFn<T> extends DoFn<T, T> {
    private long count;

    protected LimitFn() {
        // no-op
    }

    public LimitFn(final long count) {
        this.count = count;
    }

    @ProcessElement
    public void onElement(final ProcessContext context) {
        if (count == 0) {
            return;
        }
        context.output(context.element());
    }
}
