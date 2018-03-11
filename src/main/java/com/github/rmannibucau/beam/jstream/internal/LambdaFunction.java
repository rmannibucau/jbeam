package com.github.rmannibucau.beam.jstream.internal;

import static com.github.rmannibucau.beam.jstream.internal.Globals.deserializable;
import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.stream.IntStream;

import org.apache.beam.sdk.transforms.SimpleFunction;

public abstract class LambdaFunction<A, B, L> extends SimpleFunction<A, B> {
    private Lambda lambda;
    private transient L delegate;

    protected LambdaFunction() {
        // no-op
    }

    public LambdaFunction(final SerializedLambda lambda) {
        try {
            this.lambda = new Lambda(
                    ofNullable(Thread.currentThread().getContextClassLoader()).orElseGet(ClassLoader::getSystemClassLoader)
                            .loadClass(lambda.getCapturingClass().replace('/', '.')),
                    lambda.getFunctionalInterfaceClass(),
                    lambda.getFunctionalInterfaceMethodName(),
                    lambda.getFunctionalInterfaceMethodSignature(),
                    lambda.getImplClass(),
                    lambda.getImplMethodName(),
                    lambda.getImplMethodSignature(),
                    lambda.getImplMethodKind(),
                    lambda.getInstantiatedMethodType(),
                    IntStream.range(0, lambda.getCapturedArgCount())
                            .mapToObj(lambda::getCapturedArg)
                            .toArray(Object[]::new));
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public final B apply(final A input) {
        ensureDelegate();
        return doApply(delegate, input);
    }

    protected void ensureDelegate() {
        if (delegate == null) {
            delegate = (L) deserializable(new SerializedLambda(
                    lambda.capturingClass,
                    lambda.functionalInterfaceClass,
                    lambda.functionalInterfaceMethodName,
                    lambda.functionalInterfaceMethodSignature,
                    lambda.implMethodKind,
                    lambda.implClass,
                    lambda.implMethodName,
                    lambda.implMethodSignature,
                    lambda.instantiatedMethodType,
                    lambda.capturedArgs
            ));
        }
    }

    protected abstract B doApply(L delegate, A input);

    private static class Lambda implements Serializable {
        private Class<?> capturingClass;
        private String functionalInterfaceClass;
        private String functionalInterfaceMethodName;
        private String functionalInterfaceMethodSignature;
        private String implClass;
        private String implMethodName;
        private String implMethodSignature;
        private int implMethodKind;
        private String instantiatedMethodType;
        private Object[] capturedArgs;

        protected Lambda() {
            // no-op
        }

        protected Lambda(final Class<?> capturingClass,
                         final String functionalInterfaceClass,
                         final String functionalInterfaceMethodName,
                         final String functionalInterfaceMethodSignature,
                         final String implClass,
                         final String implMethodName,
                         final String implMethodSignature,
                         final int implMethodKind,
                         final String instantiatedMethodType,
                         final Object[] capturedArgs) {
            this.capturingClass = capturingClass;
            this.functionalInterfaceClass = functionalInterfaceClass;
            this.functionalInterfaceMethodName = functionalInterfaceMethodName;
            this.functionalInterfaceMethodSignature = functionalInterfaceMethodSignature;
            this.implClass = implClass;
            this.implMethodName = implMethodName;
            this.implMethodSignature = implMethodSignature;
            this.implMethodKind = implMethodKind;
            this.instantiatedMethodType = instantiatedMethodType;
            this.capturedArgs = capturedArgs;
        }
    }
}
