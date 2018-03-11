package com.github.rmannibucau.beam.jstream.internal;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Globals {
    private Globals() {
        // no-op
    }

    public static SerializedLambda serializable(final Object lambda) {
        final Method writeReplace;
        try {
            writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
        } catch (final NoSuchMethodException e) {
            // use java.lang.invoke.InnerClassLambdaMetafactory() like to generate our flavor enforce serializable?
            throw new IllegalArgumentException(lambda + " is not serializable");
        }
        if (!writeReplace.isAccessible()) {
            writeReplace.setAccessible(true);
        }
        try {
            return SerializedLambda.class.cast(writeReplace.invoke(lambda));
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (final InvocationTargetException e) {
            throw new IllegalStateException(e.getTargetException());
        }
    }

    public static Object deserializable(final SerializedLambda lambda) {
        final Method readResolve;
        try {
            readResolve = lambda.getClass().getDeclaredMethod("readResolve");
        } catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException(lambda + " is not deserializable");
        }
        if (!readResolve.isAccessible()) {
            readResolve.setAccessible(true);
        }
        try {
            return readResolve.invoke(lambda);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (final InvocationTargetException e) {
            throw new IllegalStateException(e.getTargetException());
        }
    }

    public static void assertSerializable(final Object instance) {
        if (!Serializable.class.isInstance(instance)) {
            throw new IllegalArgumentException(instance + " is not serializable, maybe use the serializable flavor of the API");
        }
    }
}
