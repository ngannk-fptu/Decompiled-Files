/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.coroutines.Continuation
 *  kotlin.reflect.KFunction
 *  kotlinx.coroutines.reactive.AwaitKt
 *  org.reactivestreams.Publisher
 *  org.springframework.core.KotlinDetector
 *  org.springframework.lang.Nullable
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;
import kotlin.coroutines.Continuation;
import kotlin.reflect.KFunction;
import kotlinx.coroutines.reactive.AwaitKt;
import org.reactivestreams.Publisher;
import org.springframework.core.KotlinDetector;
import org.springframework.data.repository.core.support.RepositoryInvocationMulticaster;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.repository.util.ReactiveWrappers;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

abstract class RepositoryMethodInvoker {
    private final Method method;
    private final Class<?> returnedType;
    private final Invokable invokable;
    private final boolean suspendedDeclaredMethod;
    private final boolean returnsReactiveType;

    protected RepositoryMethodInvoker(Method method, Invokable invokable) {
        this.method = method;
        this.invokable = invokable;
        if (KotlinDetector.isKotlinReflectPresent()) {
            this.suspendedDeclaredMethod = KotlinReflectionUtils.isSuspend(method);
            this.returnedType = this.suspendedDeclaredMethod ? KotlinReflectionUtils.getReturnType(method) : method.getReturnType();
        } else {
            this.suspendedDeclaredMethod = false;
            this.returnedType = method.getReturnType();
        }
        this.returnsReactiveType = ReactiveWrappers.supports(this.returnedType);
    }

    static RepositoryQueryMethodInvoker forRepositoryQuery(Method declaredMethod, RepositoryQuery query) {
        return new RepositoryQueryMethodInvoker(declaredMethod, query);
    }

    static RepositoryMethodInvoker forFragmentMethod(Method declaredMethod, Object instance, Method baseMethod) {
        return new RepositoryFragmentMethodInvoker(declaredMethod, instance, baseMethod);
    }

    public static boolean canInvoke(Method declaredMethod, Method baseClassMethod) {
        return RepositoryFragmentMethodInvoker.CoroutineAdapterInformation.create(declaredMethod, baseClassMethod).canInvoke();
    }

    @Nullable
    public Object invoke(Class<?> repositoryInterface, RepositoryInvocationMulticaster multicaster, Object[] args) throws Exception {
        return this.shouldAdaptReactiveToSuspended() ? this.doInvokeReactiveToSuspended(repositoryInterface, multicaster, args) : this.doInvoke(repositoryInterface, multicaster, args);
    }

    protected boolean shouldAdaptReactiveToSuspended() {
        return this.suspendedDeclaredMethod;
    }

    @Nullable
    private Object doInvoke(Class<?> repositoryInterface, RepositoryInvocationMulticaster multicaster, Object[] args) throws Exception {
        RepositoryMethodInvocationCaptor invocationResultCaptor = RepositoryMethodInvocationCaptor.captureInvocationOn(repositoryInterface);
        try {
            Object result = this.invokable.invoke(args);
            if (result != null && ReactiveWrappers.supports(result.getClass())) {
                return new ReactiveInvocationListenerDecorator().decorate(repositoryInterface, multicaster, args, result);
            }
            if (result instanceof Stream) {
                return ((Stream)result).onClose(() -> multicaster.notifyListeners(this.method, args, this.computeInvocationResult(invocationResultCaptor.success())));
            }
            multicaster.notifyListeners(this.method, args, this.computeInvocationResult(invocationResultCaptor.success()));
            return result;
        }
        catch (Exception e) {
            multicaster.notifyListeners(this.method, args, this.computeInvocationResult(invocationResultCaptor.error(e)));
            throw e;
        }
    }

    @Nullable
    private Object doInvokeReactiveToSuspended(Class<?> repositoryInterface, RepositoryInvocationMulticaster multicaster, Object[] args) throws Exception {
        Continuation continuation = (Continuation)args[args.length - 1];
        args[args.length - 1] = null;
        RepositoryMethodInvocationCaptor invocationResultCaptor = RepositoryMethodInvocationCaptor.captureInvocationOn(repositoryInterface);
        try {
            Publisher result = new ReactiveInvocationListenerDecorator().decorate(repositoryInterface, multicaster, args, this.invokable.invoke(args));
            if (this.returnsReactiveType) {
                return ReactiveWrapperConverters.toWrapper(result, this.returnedType);
            }
            if (Collection.class.isAssignableFrom(this.returnedType)) {
                result = (Publisher)RepositoryMethodInvoker.collectToList(result);
            }
            return AwaitKt.awaitSingleOrNull(result, (Continuation)continuation);
        }
        catch (Exception e) {
            multicaster.notifyListeners(this.method, args, this.computeInvocationResult(invocationResultCaptor.error(e)));
            throw e;
        }
    }

    private static Object collectToList(Object result) {
        return Flux.from((Publisher)((Publisher)result)).collectList();
    }

    private RepositoryMethodInvocationListener.RepositoryMethodInvocation computeInvocationResult(RepositoryMethodInvocationCaptor captured) {
        return new RepositoryMethodInvocationListener.RepositoryMethodInvocation(captured.getRepositoryInterface(), this.method, captured.getCapturedResult(), captured.getDuration());
    }

    private static class RepositoryMethodInvocationCaptor {
        private final Class<?> repositoryInterface;
        private long startTime;
        @Nullable
        private Long endTime;
        private final RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State state;
        @Nullable
        private final Throwable error;

        protected RepositoryMethodInvocationCaptor(Class<?> repositoryInterface, long startTime, Long endTime, RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State state, @Nullable Throwable exception) {
            this.repositoryInterface = repositoryInterface;
            this.startTime = startTime;
            this.endTime = endTime;
            this.state = state;
            this.error = exception instanceof InvocationTargetException ? exception.getCause() : exception;
        }

        public static RepositoryMethodInvocationCaptor captureInvocationOn(Class<?> repositoryInterface) {
            return new RepositoryMethodInvocationCaptor(repositoryInterface, System.nanoTime(), null, RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State.RUNNING, null);
        }

        public RepositoryMethodInvocationCaptor error(Throwable exception) {
            return new RepositoryMethodInvocationCaptor(this.repositoryInterface, this.startTime, System.nanoTime(), RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State.ERROR, exception);
        }

        public RepositoryMethodInvocationCaptor success() {
            return new RepositoryMethodInvocationCaptor(this.repositoryInterface, this.startTime, System.nanoTime(), RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State.SUCCESS, null);
        }

        public RepositoryMethodInvocationCaptor canceled() {
            return new RepositoryMethodInvocationCaptor(this.repositoryInterface, this.startTime, System.nanoTime(), RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State.CANCELED, null);
        }

        Class<?> getRepositoryInterface() {
            return this.repositoryInterface;
        }

        void trackStart() {
            this.startTime = System.nanoTime();
        }

        public RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State getState() {
            return this.state;
        }

        @Nullable
        public Throwable getError() {
            return this.error;
        }

        long getDuration() {
            return (this.endTime != null ? this.endTime : System.nanoTime()) - this.startTime;
        }

        RepositoryMethodInvocationListener.RepositoryMethodInvocationResult getCapturedResult() {
            return new RepositoryMethodInvocationListener.RepositoryMethodInvocationResult(){

                @Override
                public RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State getState() {
                    return this.getState();
                }

                @Override
                @Nullable
                public Throwable getError() {
                    return this.getError();
                }
            };
        }
    }

    private static class RepositoryFragmentMethodInvoker
    extends RepositoryMethodInvoker {
        private final CoroutineAdapterInformation adapterInformation;

        public RepositoryFragmentMethodInvoker(Method declaredMethod, Object instance, Method baseClassMethod) {
            this(CoroutineAdapterInformation.create(declaredMethod, baseClassMethod), declaredMethod, instance, baseClassMethod);
        }

        public RepositoryFragmentMethodInvoker(CoroutineAdapterInformation adapterInformation, Method declaredMethod, Object instance, Method baseClassMethod) {
            super(declaredMethod, args -> {
                if (adapterInformation.isAdapterMethod()) {
                    Object[] invocationArguments = new Object[args.length - 1];
                    System.arraycopy(args, 0, invocationArguments, 0, invocationArguments.length);
                    return baseClassMethod.invoke(instance, invocationArguments);
                }
                return baseClassMethod.invoke(instance, args);
            });
            this.adapterInformation = adapterInformation;
        }

        @Override
        protected boolean shouldAdaptReactiveToSuspended() {
            return this.adapterInformation.shouldAdaptReactiveToSuspended();
        }

        static class CoroutineAdapterInformation {
            private final boolean suspendedDeclaredMethod;
            private final boolean suspendedBaseClassMethod;
            private final boolean reactiveBaseClassMethod;
            private final int declaredMethodParameterCount;
            private final int baseClassMethodParameterCount;

            private CoroutineAdapterInformation(boolean suspendedDeclaredMethod, boolean suspendedBaseClassMethod, boolean reactiveBaseClassMethod, int declaredMethodParameterCount, int baseClassMethodParameterCount) {
                this.suspendedDeclaredMethod = suspendedDeclaredMethod;
                this.suspendedBaseClassMethod = suspendedBaseClassMethod;
                this.reactiveBaseClassMethod = reactiveBaseClassMethod;
                this.declaredMethodParameterCount = declaredMethodParameterCount;
                this.baseClassMethodParameterCount = baseClassMethodParameterCount;
            }

            public static CoroutineAdapterInformation create(Method declaredMethod, Method baseClassMethod) {
                if (!KotlinDetector.isKotlinReflectPresent()) {
                    return new CoroutineAdapterInformation(false, false, false, declaredMethod.getParameterCount(), baseClassMethod.getParameterCount());
                }
                KFunction<?> declaredFunction = KotlinDetector.isKotlinType(declaredMethod.getDeclaringClass()) ? KotlinReflectionUtils.findKotlinFunction(declaredMethod) : null;
                KFunction<?> baseClassFunction = KotlinDetector.isKotlinType(baseClassMethod.getDeclaringClass()) ? KotlinReflectionUtils.findKotlinFunction(baseClassMethod) : null;
                boolean suspendedDeclaredMethod = declaredFunction != null && declaredFunction.isSuspend();
                boolean suspendedBaseClassMethod = baseClassFunction != null && baseClassFunction.isSuspend();
                boolean reactiveBaseClassMethod = !suspendedBaseClassMethod && ReactiveWrapperConverters.supports(baseClassMethod.getReturnType());
                return new CoroutineAdapterInformation(suspendedDeclaredMethod, suspendedBaseClassMethod, reactiveBaseClassMethod, declaredMethod.getParameterCount(), baseClassMethod.getParameterCount());
            }

            boolean canInvoke() {
                if (this.suspendedDeclaredMethod == this.suspendedBaseClassMethod) {
                    return this.declaredMethodParameterCount == this.baseClassMethodParameterCount;
                }
                if (this.isAdapterMethod()) {
                    return this.declaredMethodParameterCount - 1 == this.baseClassMethodParameterCount;
                }
                return false;
            }

            boolean isAdapterMethod() {
                return this.suspendedDeclaredMethod && this.reactiveBaseClassMethod;
            }

            public boolean shouldAdaptReactiveToSuspended() {
                return this.suspendedDeclaredMethod && !this.suspendedBaseClassMethod && this.reactiveBaseClassMethod;
            }
        }
    }

    class ReactiveInvocationListenerDecorator {
        ReactiveInvocationListenerDecorator() {
        }

        Publisher<Object> decorate(Class<?> repositoryInterface, RepositoryInvocationMulticaster multicaster, Object[] args, Object result) {
            if (result instanceof Mono) {
                return Mono.usingWhen((Publisher)Mono.fromSupplier(() -> RepositoryMethodInvocationCaptor.captureInvocationOn(repositoryInterface)), it -> {
                    it.trackStart();
                    return ReactiveWrapperConverters.toWrapper(result, Mono.class);
                }, it -> {
                    multicaster.notifyListeners(RepositoryMethodInvoker.this.method, args, RepositoryMethodInvoker.this.computeInvocationResult(it.success()));
                    return Mono.empty();
                }, (it, e) -> {
                    multicaster.notifyListeners(RepositoryMethodInvoker.this.method, args, RepositoryMethodInvoker.this.computeInvocationResult(it.error((Throwable)e)));
                    return Mono.empty();
                }, it -> {
                    multicaster.notifyListeners(RepositoryMethodInvoker.this.method, args, RepositoryMethodInvoker.this.computeInvocationResult(it.canceled()));
                    return Mono.empty();
                });
            }
            return Flux.usingWhen((Publisher)Mono.fromSupplier(() -> RepositoryMethodInvocationCaptor.captureInvocationOn(repositoryInterface)), it -> {
                it.trackStart();
                return result instanceof Publisher ? (Publisher)result : ReactiveWrapperConverters.toWrapper(result, Publisher.class);
            }, it -> {
                multicaster.notifyListeners(RepositoryMethodInvoker.this.method, args, RepositoryMethodInvoker.this.computeInvocationResult(it.success()));
                return Mono.empty();
            }, (it, e) -> {
                multicaster.notifyListeners(RepositoryMethodInvoker.this.method, args, RepositoryMethodInvoker.this.computeInvocationResult(it.error((Throwable)e)));
                return Mono.empty();
            }, it -> {
                multicaster.notifyListeners(RepositoryMethodInvoker.this.method, args, RepositoryMethodInvoker.this.computeInvocationResult(it.canceled()));
                return Mono.empty();
            });
        }
    }

    private static class RepositoryQueryMethodInvoker
    extends RepositoryMethodInvoker {
        public RepositoryQueryMethodInvoker(Method method, RepositoryQuery repositoryQuery) {
            super(method, repositoryQuery::execute);
        }
    }

    static interface Invokable {
        @Nullable
        public Object invoke(Object[] var1) throws ReflectiveOperationException;
    }
}

