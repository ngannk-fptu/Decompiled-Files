/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation.bind.annotation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Default;
import net.bytebuddy.implementation.bind.annotation.DefaultCall;
import net.bytebuddy.implementation.bind.annotation.DefaultCallHandle;
import net.bytebuddy.implementation.bind.annotation.DefaultMethod;
import net.bytebuddy.implementation.bind.annotation.DefaultMethodHandle;
import net.bytebuddy.implementation.bind.annotation.Empty;
import net.bytebuddy.implementation.bind.annotation.FieldGetterHandle;
import net.bytebuddy.implementation.bind.annotation.FieldSetterHandle;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.StubValue;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.SuperCallHandle;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.SuperMethodHandle;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.utility.ConstantValue;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class TargetMethodAnnotationDrivenBinder
implements MethodDelegationBinder {
    private final DelegationProcessor delegationProcessor;

    protected TargetMethodAnnotationDrivenBinder(DelegationProcessor delegationProcessor) {
        this.delegationProcessor = delegationProcessor;
    }

    public static MethodDelegationBinder of(List<? extends ParameterBinder<?>> parameterBinders) {
        return new TargetMethodAnnotationDrivenBinder(DelegationProcessor.of(parameterBinders));
    }

    @Override
    public MethodDelegationBinder.Record compile(MethodDescription candidate) {
        if (IgnoreForBinding.Verifier.check(candidate)) {
            return MethodDelegationBinder.Record.Illegal.INSTANCE;
        }
        ArrayList<DelegationProcessor.Handler> handlers = new ArrayList<DelegationProcessor.Handler>(candidate.getParameters().size());
        for (ParameterDescription parameterDescription : candidate.getParameters()) {
            handlers.add(this.delegationProcessor.prepare(parameterDescription));
        }
        return new Record(candidate, handlers, RuntimeType.Verifier.check(candidate));
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        return this.delegationProcessor.equals(((TargetMethodAnnotationDrivenBinder)object).delegationProcessor);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.delegationProcessor.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class DelegationProcessor {
        private final Map<? extends TypeDescription, ? extends ParameterBinder<?>> parameterBinders;

        protected DelegationProcessor(Map<? extends TypeDescription, ? extends ParameterBinder<?>> parameterBinders) {
            this.parameterBinders = parameterBinders;
        }

        protected static DelegationProcessor of(List<? extends ParameterBinder<?>> parameterBinders) {
            HashMap parameterBinderMap = new HashMap();
            for (ParameterBinder<?> parameterBinder : parameterBinders) {
                if (parameterBinderMap.put(TypeDescription.ForLoadedType.of(parameterBinder.getHandledType()), parameterBinder) == null) continue;
                throw new IllegalArgumentException("Attempt to bind two handlers to " + parameterBinder.getHandledType());
            }
            return new DelegationProcessor(parameterBinderMap);
        }

        protected Handler prepare(ParameterDescription target) {
            Assigner.Typing typing = RuntimeType.Verifier.check(target);
            Handler handler = new Handler.Unbound(target, typing);
            for (AnnotationDescription annotation : target.getDeclaredAnnotations()) {
                ParameterBinder<?> parameterBinder = this.parameterBinders.get(annotation.getAnnotationType());
                if (parameterBinder != null && handler.isBound()) {
                    throw new IllegalStateException("Ambiguous binding for parameter annotated with two handled annotation types");
                }
                if (parameterBinder == null) continue;
                handler = Handler.Bound.of(target, parameterBinder, annotation, typing);
            }
            return handler;
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return ((Object)this.parameterBinders).equals(((DelegationProcessor)object).parameterBinders);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.parameterBinders).hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface Handler {
            public boolean isBound();

            public MethodDelegationBinder.ParameterBinding<?> bind(MethodDescription var1, Implementation.Target var2, Assigner var3);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Bound<T extends Annotation>
            implements Handler {
                private final ParameterDescription target;
                private final ParameterBinder<T> parameterBinder;
                private final AnnotationDescription.Loadable<T> annotation;
                private final Assigner.Typing typing;

                protected Bound(ParameterDescription target, ParameterBinder<T> parameterBinder, AnnotationDescription.Loadable<T> annotation, Assigner.Typing typing) {
                    this.target = target;
                    this.parameterBinder = parameterBinder;
                    this.annotation = annotation;
                    this.typing = typing;
                }

                protected static Handler of(ParameterDescription target, ParameterBinder<?> parameterBinder, AnnotationDescription annotation, Assigner.Typing typing) {
                    return new Bound(target, parameterBinder, annotation.prepare(parameterBinder.getHandledType()), typing);
                }

                @Override
                public boolean isBound() {
                    return true;
                }

                @Override
                public MethodDelegationBinder.ParameterBinding<?> bind(MethodDescription source, Implementation.Target implementationTarget, Assigner assigner) {
                    return this.parameterBinder.bind(this.annotation, source, this.target, implementationTarget, assigner, this.typing);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    if (!this.typing.equals((Object)((Bound)object).typing)) {
                        return false;
                    }
                    if (!this.target.equals(((Bound)object).target)) {
                        return false;
                    }
                    if (!this.parameterBinder.equals(((Bound)object).parameterBinder)) {
                        return false;
                    }
                    return this.annotation.equals(((Bound)object).annotation);
                }

                public int hashCode() {
                    return (((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.parameterBinder.hashCode()) * 31 + this.annotation.hashCode()) * 31 + this.typing.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Unbound
            implements Handler {
                private final ParameterDescription target;
                private final Assigner.Typing typing;

                protected Unbound(ParameterDescription target, Assigner.Typing typing) {
                    this.target = target;
                    this.typing = typing;
                }

                @Override
                public boolean isBound() {
                    return false;
                }

                @Override
                public MethodDelegationBinder.ParameterBinding<?> bind(MethodDescription source, Implementation.Target implementationTarget, Assigner assigner) {
                    return Argument.Binder.INSTANCE.bind(AnnotationDescription.ForLoadedAnnotation.of(new DefaultArgument(this.target.getIndex())), source, this.target, implementationTarget, assigner, this.typing);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    if (!this.typing.equals((Object)((Unbound)object).typing)) {
                        return false;
                    }
                    return this.target.equals(((Unbound)object).target);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.typing.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class DefaultArgument
                implements Argument {
                    private static final String VALUE = "value";
                    private static final String BINDING_MECHANIC = "bindingMechanic";
                    private final int parameterIndex;

                    protected DefaultArgument(int parameterIndex) {
                        this.parameterIndex = parameterIndex;
                    }

                    @Override
                    public int value() {
                        return this.parameterIndex;
                    }

                    @Override
                    public Argument.BindingMechanic bindingMechanic() {
                        return Argument.BindingMechanic.UNIQUE;
                    }

                    public Class<Argument> annotationType() {
                        return Argument.class;
                    }

                    @Override
                    public int hashCode() {
                        return (127 * BINDING_MECHANIC.hashCode() ^ Argument.BindingMechanic.UNIQUE.hashCode()) + (127 * VALUE.hashCode() ^ this.parameterIndex);
                    }

                    @Override
                    public boolean equals(@MaybeNull Object other) {
                        return this == other || other instanceof Argument && this.parameterIndex == ((Argument)other).value();
                    }

                    @Override
                    public String toString() {
                        return "@" + Argument.class.getName() + "(bindingMechanic=" + (Object)((Object)Argument.BindingMechanic.UNIQUE) + ", value=" + this.parameterIndex + ")";
                    }
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @SuppressFBWarnings(value={"IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION"}, justification="Safe initialization is implied.")
    public static interface ParameterBinder<T extends Annotation> {
        public static final List<ParameterBinder<?>> DEFAULTS = Collections.unmodifiableList(Arrays.asList(Argument.Binder.INSTANCE, AllArguments.Binder.INSTANCE, Origin.Binder.INSTANCE, This.Binder.INSTANCE, Super.Binder.INSTANCE, Default.Binder.INSTANCE, SuperCall.Binder.INSTANCE, SuperCallHandle.Binder.INSTANCE, DefaultCall.Binder.INSTANCE, DefaultCallHandle.Binder.INSTANCE, SuperMethod.Binder.INSTANCE, SuperMethodHandle.Binder.INSTANCE, DefaultMethod.Binder.INSTANCE, DefaultMethodHandle.Binder.INSTANCE, FieldValue.Binder.INSTANCE, FieldGetterHandle.Binder.INSTANCE, FieldSetterHandle.Binder.INSTANCE, StubValue.Binder.INSTANCE, Empty.Binder.INSTANCE));

        public Class<T> getHandledType();

        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<T> var1, MethodDescription var2, ParameterDescription var3, Implementation.Target var4, Assigner var5, Assigner.Typing var6);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class ForFieldBinding<S extends Annotation>
        implements ParameterBinder<S> {
            protected static final String BEAN_PROPERTY = "";

            @Override
            public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<S> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
                if (!this.declaringType(annotation).represents(Void.TYPE)) {
                    if (this.declaringType(annotation).isPrimitive() || this.declaringType(annotation).isArray()) {
                        throw new IllegalStateException("A primitive type or array type cannot declare a field: " + source);
                    }
                    if (!implementationTarget.getInstrumentedType().isAssignableTo(this.declaringType(annotation))) {
                        return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
                    }
                }
                FieldLocator.AbstractBase fieldLocator = this.declaringType(annotation).represents(Void.TYPE) ? new FieldLocator.ForClassHierarchy(implementationTarget.getInstrumentedType()) : new FieldLocator.ForExactType(this.declaringType(annotation), implementationTarget.getInstrumentedType());
                FieldLocator.Resolution resolution = this.fieldName(annotation).equals(BEAN_PROPERTY) ? FieldLocator.Resolution.Simple.ofBeanAccessor(fieldLocator, source) : fieldLocator.locate(this.fieldName(annotation));
                return resolution.isResolved() && (!source.isStatic() || resolution.getField().isStatic()) ? this.bind(resolution.getField(), annotation, source, target, implementationTarget, assigner) : MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }

            protected abstract String fieldName(AnnotationDescription.Loadable<S> var1);

            protected abstract TypeDescription declaringType(AnnotationDescription.Loadable<S> var1);

            protected abstract MethodDelegationBinder.ParameterBinding<?> bind(FieldDescription var1, AnnotationDescription.Loadable<S> var2, MethodDescription var3, ParameterDescription var4, Implementation.Target var5, Assigner var6);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class ForFixedValue<S extends Annotation>
        implements ParameterBinder<S> {
            @Override
            public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<S> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
                Object value = this.bind(annotation, source, target);
                if (value == null) {
                    return new MethodDelegationBinder.ParameterBinding.Anonymous(DefaultValue.of(target.getType()));
                }
                ConstantValue constant = ConstantValue.Simple.wrap(value);
                return new MethodDelegationBinder.ParameterBinding.Anonymous(new StackManipulation.Compound(constant.toStackManipulation(), assigner.assign(constant.getTypeDescription().asGenericType(), target.getType(), typing)));
            }

            @MaybeNull
            protected abstract Object bind(AnnotationDescription.Loadable<S> var1, MethodDescription var2, ParameterDescription var3);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class OfConstant<U extends Annotation>
            extends ForFixedValue<U> {
                private final Class<U> type;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final Object value;

                protected OfConstant(Class<U> type, @MaybeNull Object value) {
                    this.type = type;
                    this.value = value;
                }

                public static <V extends Annotation> ParameterBinder<V> of(Class<V> type, @MaybeNull Object value) {
                    return new OfConstant<V>(type, value);
                }

                @Override
                public Class<U> getHandledType() {
                    return this.type;
                }

                @Override
                @MaybeNull
                protected Object bind(AnnotationDescription.Loadable<U> annotation, MethodDescription source, ParameterDescription target) {
                    return this.value;
                }

                public boolean equals(@MaybeNull Object object) {
                    block11: {
                        block10: {
                            Object object2;
                            block9: {
                                Object object3;
                                if (this == object) {
                                    return true;
                                }
                                if (object == null) {
                                    return false;
                                }
                                if (this.getClass() != object.getClass()) {
                                    return false;
                                }
                                if (!this.type.equals(((OfConstant)object).type)) {
                                    return false;
                                }
                                Object object4 = ((OfConstant)object).value;
                                object2 = object3 = this.value;
                                if (object4 == null) break block9;
                                if (object2 == null) break block10;
                                if (!object3.equals(object4)) {
                                    return false;
                                }
                                break block11;
                            }
                            if (object2 == null) break block11;
                        }
                        return false;
                    }
                    return true;
                }

                public int hashCode() {
                    int n = (this.getClass().hashCode() * 31 + this.type.hashCode()) * 31;
                    Object object = this.value;
                    if (object != null) {
                        n = n + object.hashCode();
                    }
                    return n;
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class Record
    implements MethodDelegationBinder.Record {
        private final MethodDescription candidate;
        private final List<DelegationProcessor.Handler> handlers;
        private final Assigner.Typing typing;

        protected Record(MethodDescription candidate, List<DelegationProcessor.Handler> handlers, Assigner.Typing typing) {
            this.candidate = candidate;
            this.handlers = handlers;
            this.typing = typing;
        }

        @Override
        public MethodDelegationBinder.MethodBinding bind(Implementation.Target implementationTarget, MethodDescription source, MethodDelegationBinder.TerminationHandler terminationHandler, MethodDelegationBinder.MethodInvoker methodInvoker, Assigner assigner) {
            if (!this.candidate.isAccessibleTo(implementationTarget.getInstrumentedType())) {
                return MethodDelegationBinder.MethodBinding.Illegal.INSTANCE;
            }
            StackManipulation methodTermination = terminationHandler.resolve(assigner, this.typing, source, this.candidate);
            if (!methodTermination.isValid()) {
                return MethodDelegationBinder.MethodBinding.Illegal.INSTANCE;
            }
            MethodDelegationBinder.MethodBinding.Builder methodDelegationBindingBuilder = new MethodDelegationBinder.MethodBinding.Builder(methodInvoker, this.candidate);
            for (DelegationProcessor.Handler handler : this.handlers) {
                MethodDelegationBinder.ParameterBinding<?> parameterBinding = handler.bind(source, implementationTarget, assigner);
                if (parameterBinding.isValid() && methodDelegationBindingBuilder.append(parameterBinding)) continue;
                return MethodDelegationBinder.MethodBinding.Illegal.INSTANCE;
            }
            return methodDelegationBindingBuilder.build(methodTermination);
        }

        public String toString() {
            return this.candidate.toString();
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            if (!this.typing.equals((Object)((Record)object).typing)) {
                return false;
            }
            if (!this.candidate.equals(((Record)object).candidate)) {
                return false;
            }
            return ((Object)this.handlers).equals(((Record)object).handlers);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + this.candidate.hashCode()) * 31 + ((Object)this.handlers).hashCode()) * 31 + this.typing.hashCode();
        }
    }
}

