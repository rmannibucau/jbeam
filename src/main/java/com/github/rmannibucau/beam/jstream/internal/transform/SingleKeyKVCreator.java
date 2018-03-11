package com.github.rmannibucau.beam.jstream.internal.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;

public final class SingleKeyKVCreator<T> extends DoFn<T, KV<Integer, T>> {
    @ProcessElement
    public void onElement(final ProcessContext context) {
        context.output(KV.of(1, context.element()));
    }
}
