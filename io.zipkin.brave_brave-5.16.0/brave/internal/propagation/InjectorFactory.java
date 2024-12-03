/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.propagation;

import brave.Request;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public final class InjectorFactory {
    final InjectorFunction injectorFunction;
    final InjectorFunction clientInjectorFunction;
    final InjectorFunction producerInjectorFunction;
    final InjectorFunction consumerInjectorFunction;
    final List<String> keyNames;

    public static Builder newBuilder(InjectorFunction injectorFunction) {
        if (injectorFunction == null) {
            throw new NullPointerException("injectorFunction == null");
        }
        return new Builder(injectorFunction);
    }

    InjectorFactory(Builder builder) {
        this.injectorFunction = builder.injectorFunction;
        this.clientInjectorFunction = builder.clientInjectorFunction;
        this.producerInjectorFunction = builder.producerInjectorFunction;
        this.consumerInjectorFunction = builder.consumerInjectorFunction;
        LinkedHashSet<String> keyNames = new LinkedHashSet<String>();
        keyNames.addAll(builder.consumerInjectorFunction.keyNames());
        keyNames.addAll(builder.producerInjectorFunction.keyNames());
        keyNames.addAll(builder.clientInjectorFunction.keyNames());
        keyNames.addAll(builder.injectorFunction.keyNames());
        this.keyNames = Collections.unmodifiableList(new ArrayList(keyNames));
    }

    public List<String> keyNames() {
        return this.keyNames;
    }

    public <R> TraceContext.Injector<R> newInjector(Propagation.Setter<R, String> setter) {
        if (setter == null) {
            throw new NullPointerException("setter == null");
        }
        if (setter instanceof Propagation.RemoteSetter) {
            Propagation.RemoteSetter remoteSetter = (Propagation.RemoteSetter)setter;
            switch (remoteSetter.spanKind()) {
                case CLIENT: {
                    return new RemoteInjector<R>(setter, this.clientInjectorFunction);
                }
                case PRODUCER: {
                    return new RemoteInjector<R>(setter, this.producerInjectorFunction);
                }
                case CONSUMER: {
                    return new RemoteInjector<R>(setter, this.consumerInjectorFunction);
                }
            }
        }
        return new DeferredInjector<R>(setter, this);
    }

    public int hashCode() {
        int h = 1000003;
        h ^= this.injectorFunction.hashCode();
        h *= 1000003;
        h ^= this.clientInjectorFunction.hashCode();
        h *= 1000003;
        h ^= this.producerInjectorFunction.hashCode();
        h *= 1000003;
        return h ^= this.consumerInjectorFunction.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InjectorFactory)) {
            return false;
        }
        InjectorFactory that = (InjectorFactory)o;
        return this.injectorFunction.equals(that.injectorFunction) && this.clientInjectorFunction.equals(that.clientInjectorFunction) && this.producerInjectorFunction.equals(that.producerInjectorFunction) && this.consumerInjectorFunction.equals(that.consumerInjectorFunction);
    }

    public String toString() {
        return "InjectorFactory{injectorFunction=" + this.injectorFunction + ", clientInjectorFunction=" + this.clientInjectorFunction + ", producerInjectorFunction=" + this.producerInjectorFunction + ", consumerInjectorFunction=" + this.consumerInjectorFunction + "}";
    }

    static InjectorFunction injectorFunction(InjectorFunction existing, InjectorFunction ... update) {
        if (update == null) {
            throw new NullPointerException("injectorFunctions == null");
        }
        LinkedHashSet<InjectorFunction> injectorFunctionSet = new LinkedHashSet<InjectorFunction>(Arrays.asList(update));
        if (injectorFunctionSet.contains(null)) {
            throw new NullPointerException("injectorFunction == null");
        }
        injectorFunctionSet.remove(InjectorFunction.NOOP);
        if (injectorFunctionSet.isEmpty()) {
            return existing;
        }
        if (injectorFunctionSet.size() == 1) {
            return (InjectorFunction)injectorFunctionSet.iterator().next();
        }
        return new CompositeInjectorFunction(injectorFunctionSet.toArray(new InjectorFunction[0]));
    }

    static final class CompositeInjectorFunction
    implements InjectorFunction {
        final InjectorFunction[] injectorFunctions;
        final List<String> keyNames;

        CompositeInjectorFunction(InjectorFunction[] injectorFunctions) {
            this.injectorFunctions = injectorFunctions;
            LinkedHashSet<String> keyNames = new LinkedHashSet<String>();
            for (InjectorFunction injectorFunction : injectorFunctions) {
                keyNames.addAll(injectorFunction.keyNames());
            }
            this.keyNames = Collections.unmodifiableList(new ArrayList(keyNames));
        }

        @Override
        public List<String> keyNames() {
            return this.keyNames;
        }

        @Override
        public <R> void inject(Propagation.Setter<R, String> setter, TraceContext context, R request) {
            for (InjectorFunction injectorFunction : this.injectorFunctions) {
                injectorFunction.inject(setter, context, request);
            }
        }

        public int hashCode() {
            int h = 1000003;
            return h ^= Arrays.hashCode(this.injectorFunctions);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CompositeInjectorFunction)) {
                return false;
            }
            return Arrays.equals(this.injectorFunctions, ((CompositeInjectorFunction)o).injectorFunctions);
        }

        public String toString() {
            return Arrays.toString(this.injectorFunctions);
        }
    }

    static final class RemoteInjector<R>
    implements TraceContext.Injector<R> {
        final InjectorFunction injectorFunction;
        final Propagation.Setter<R, String> setter;

        RemoteInjector(Propagation.Setter<R, String> setter, InjectorFunction injectorFunction) {
            this.injectorFunction = injectorFunction;
            this.setter = setter;
        }

        @Override
        public void inject(TraceContext context, R request) {
            this.injectorFunction.inject(this.setter, context, request);
        }

        public int hashCode() {
            int h = 1000003;
            h ^= this.setter.hashCode();
            h *= 1000003;
            return h ^= this.injectorFunction.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RemoteInjector)) {
                return false;
            }
            RemoteInjector that = (RemoteInjector)o;
            return this.setter.equals(that.setter) && this.injectorFunction.equals(that.injectorFunction);
        }

        public String toString() {
            return "Injector{setter=" + this.setter + ", injectorFunction=" + this.injectorFunction + "}";
        }
    }

    static final class DeferredInjector<R>
    implements TraceContext.Injector<R> {
        final Propagation.Setter<R, String> setter;
        final InjectorFactory injectorFactory;

        DeferredInjector(Propagation.Setter<R, String> setter, InjectorFactory injectorFactory) {
            this.setter = setter;
            this.injectorFactory = injectorFactory;
        }

        @Override
        public void inject(TraceContext context, R request) {
            if (request instanceof Request) {
                switch (((Request)request).spanKind()) {
                    case CLIENT: {
                        this.injectorFactory.clientInjectorFunction.inject(this.setter, context, request);
                        return;
                    }
                    case PRODUCER: {
                        this.injectorFactory.producerInjectorFunction.inject(this.setter, context, request);
                        return;
                    }
                    case CONSUMER: {
                        this.injectorFactory.consumerInjectorFunction.inject(this.setter, context, request);
                        return;
                    }
                }
            }
            this.injectorFactory.injectorFunction.inject(this.setter, context, request);
        }

        public int hashCode() {
            int h = 1000003;
            h ^= this.setter.hashCode();
            h *= 1000003;
            return h ^= this.injectorFactory.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof DeferredInjector)) {
                return false;
            }
            DeferredInjector that = (DeferredInjector)o;
            return this.setter.equals(that.setter) && this.injectorFactory.equals(that.injectorFactory);
        }

        public String toString() {
            return "DeferredInjector{setter=" + this.setter + ", injectorFactory=" + this.injectorFactory + "}";
        }
    }

    public static final class Builder {
        InjectorFunction injectorFunction;
        InjectorFunction clientInjectorFunction;
        InjectorFunction producerInjectorFunction;
        InjectorFunction consumerInjectorFunction;

        Builder(InjectorFunction defaultInjectorFunction) {
            this.injectorFunction = defaultInjectorFunction;
            this.clientInjectorFunction = defaultInjectorFunction;
            this.producerInjectorFunction = defaultInjectorFunction;
            this.consumerInjectorFunction = defaultInjectorFunction;
        }

        public Builder injectorFunctions(InjectorFunction ... injectorFunctions) {
            this.injectorFunction = InjectorFactory.injectorFunction(this.injectorFunction, injectorFunctions);
            return this;
        }

        public Builder clientInjectorFunctions(InjectorFunction ... injectorFunctions) {
            this.clientInjectorFunction = InjectorFactory.injectorFunction(this.clientInjectorFunction, injectorFunctions);
            return this;
        }

        public Builder producerInjectorFunctions(InjectorFunction ... injectorFunctions) {
            this.producerInjectorFunction = InjectorFactory.injectorFunction(this.producerInjectorFunction, injectorFunctions);
            return this;
        }

        public Builder consumerInjectorFunctions(InjectorFunction ... injectorFunctions) {
            this.consumerInjectorFunction = InjectorFactory.injectorFunction(this.consumerInjectorFunction, injectorFunctions);
            return this;
        }

        public InjectorFactory build() {
            return new InjectorFactory(this);
        }
    }

    public static interface InjectorFunction {
        public static final InjectorFunction NOOP = new InjectorFunction(){

            @Override
            public List<String> keyNames() {
                return Collections.emptyList();
            }

            @Override
            public <R> void inject(Propagation.Setter<R, String> setter, TraceContext context, R request) {
            }
        };

        public List<String> keyNames();

        public <R> void inject(Propagation.Setter<R, String> var1, TraceContext var2, R var3);
    }
}

