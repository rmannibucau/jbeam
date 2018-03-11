package com.github.rmannibucau.beam.jstream.configuration;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface RunOptions extends PipelineOptions {
    @Description("Should stream leaf wait until finish.")
    @Default.Boolean(true)
    boolean getStreamRunAwaitUntilFinish();

    void setStreamRunAwaitUntilFinish(boolean waitUntilFinish);
}
