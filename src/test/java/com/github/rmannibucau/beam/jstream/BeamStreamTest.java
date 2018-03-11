package com.github.rmannibucau.beam.jstream;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.TestPipeline;
import org.junit.Rule;
import org.junit.Test;

public class BeamStreamTest implements Serializable {
    @Rule
    public final TestPipeline testPipeline = TestPipeline.create();

    private static final Collection<String> mapFilterForEach = new ArrayList<>();

    @Test
    public void mapFilterForEach() {
        BeamStream.of(PipelineOptionsFactory.create(), Stream.of("a", "bb"))
                .map((Serializable & Function<String, String>) s -> s + " (" + s.length() + ")")
                .filter((Serializable & Predicate<String>) s -> !s.startsWith("a"))
                .forEach((Serializable & Consumer<String>) it -> {
                    synchronized (mapFilterForEach) {
                        mapFilterForEach.add(it);
                    }
                });

        final int retries = 60; // workaround for waituntilfinish bug
        for (int i = 0; i < retries; i++) {
            try {
                synchronized (mapFilterForEach) {
                    assertEquals(1, mapFilterForEach.size());
                    assertEquals("bb (2)", mapFilterForEach.iterator().next());
                }
            } catch (final AssertionError ae) {
                if (i == retries - 1) {
                    throw ae;
                }
                try {
                    sleep(100);
                } catch (final InterruptedException e) {
                    fail(e.getMessage());
                }
            }
        }
    }
}
