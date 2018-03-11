package com.github.rmannibucau.beam.jstream.internal.transform;

import org.apache.beam.sdk.transforms.DoFn;

public class SkipFn<T> extends DoFn<T, T> {
    private long count;

    protected SkipFn() {
        // no-op
    }

    public SkipFn(final long count) {
        this.count = count;
    }

    @ProcessElement
    public void onElement(final ProcessContext context) {
        if (count == 0) {
            context.output(context.element());
            return;
        }
        count--;
    }
}
