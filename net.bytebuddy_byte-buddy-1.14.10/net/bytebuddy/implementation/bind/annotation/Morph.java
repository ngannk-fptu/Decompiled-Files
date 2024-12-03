/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind.annotation;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Morph {
    public boolean serializableProxy() default false;

    public boolean defaultMethod() default false;

    public Class<?> defaultTarget() default void.class;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Binder
    implements TargetMethodAnnotationDrivenBinder.ParameterBinder<Morph> {
        private static final MethodDescription.InDefinedShape SERIALIZABLE_PROXY;
        private static final MethodDescription.InDefinedShape DEFAULT_METHOD;
        private static final MethodDescription.InDefinedShape DEFAULT_TARGET;
        private final MethodDescription forwardingMethod;

        protected Binder(MethodDescription forwardingMethod) {
            this.forwardingMethod = forwardingMethod;
        }

        public static TargetMethodAnnotationDrivenBinder.ParameterBinder<Morph> install(Class<?> type) {
            return Binder.install(TypeDescription.ForLoadedType.of(type));
        }

        public static TargetMethodAnnotationDrivenBinder.ParameterBinder<Morph> install(TypeDescription typeDescription) {
            return new Binder(Binder.onlyMethod(typeDescription));
        }

        private static MethodDescription onlyMethod(TypeDescription typeDescription) {
            if (!typeDescription.isInterface()) {
                throw new IllegalArgumentException(typeDescription + " is not an interface");
            }
            if (!typeDescription.getInterfaces().isEmpty()) {
                throw new IllegalArgumentException(typeDescription + " must not extend other interfaces");
            }
            if (!typeDescription.isPublic()) {
                throw new IllegalArgumentException(typeDescription + " is mot public");
            }
            MethodList methodCandidates = (MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.isAbstract());
            if (methodCandidates.size() != 1) {
                throw new IllegalArgumentException(typeDescription + " must declare exactly one abstract method");
            }
            MethodDescription methodDescription = (MethodDescription)methodCandidates.getOnly();
            if (!methodDescription.getReturnType().asErasure().represents((Type)((Object)Object.class))) {
                throw new IllegalArgumentException(methodDescription + " does not return an Object-type");
            }
            if (methodDescription.getParameters().size() != 1 || !((ParameterDescription)methodDescription.getParameters().get(0)).getType().asErasure().represents((Type)((Object)Object[].class))) {
                throw new IllegalArgumentException(methodDescription + " does not take a single argument of type Object[]");
            }
            return methodDescription;
        }

        @Override
        public Class<Morph> getHandledType() {
            return Morph.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<Morph> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (!target.getType().asErasure().equals(this.forwardingMethod.getDeclaringType())) {
                throw new IllegalStateException("Illegal use of @Morph for " + target + " which was installed for " + this.forwardingMethod.getDeclaringType());
            }
            TypeDescription typeDescription = annotation.getValue(DEFAULT_TARGET).resolve(TypeDescription.class);
            Implementation.SpecialMethodInvocation specialMethodInvocation = typeDescription.represents(Void.TYPE) && annotation.getValue(DEFAULT_METHOD).resolve(Boolean.class) == false ? implementationTarget.invokeSuper(source.asSignatureToken()).withCheckedCompatibilityTo(source.asTypeToken()) : (typeDescription.represents(Void.TYPE) ? DefaultMethodLocator.Implicit.INSTANCE : new DefaultMethodLocator.Explicit(typeDescription)).resolve(implementationTarget, source);
            return specialMethodInvocation.isValid() ? new MethodDelegationBinder.ParameterBinding.Anonymous(new RedirectionProxy(this.forwardingMethod.getDeclaringType().asErasure(), implementationTarget.getInstrumentedType(), specialMethodInvocation, assigner, annotation.getValue(SERIALIZABLE_PROXY).resolve(Boolean.class))) : MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }

        static {
            MethodList<MethodDescription.InDefinedShape> methodList = TypeDescription.ForLoadedType.of(Morph.class).getDeclaredMethods();
            SERIALIZABLE_PROXY = (MethodDescription.InDefinedShape)((MethodList)methodList.filter(ElementMatchers.named("serializableProxy"))).getOnly();
            DEFAULT_METHOD = (MethodDescription.InDefinedShape)((MethodList)methodList.filter(ElementMatchers.named("defaultMethod"))).getOnly();
            DEFAULT_TARGET = (MethodDescription.InDefinedShape)((MethodList)methodList.filter(ElementMatchers.named("defaultTarget"))).getOnly();
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
            return this.forwardingMethod.equals(((Binder)object).forwardingMethod);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.forwardingMethod.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class RedirectionProxy
        extends StackManipulation.AbstractBase
        implements AuxiliaryType {
            protected static final String FIELD_NAME = "target";
            private final TypeDescription morphingType;
            private final TypeDescription instrumentedType;
            private final Implementation.SpecialMethodInvocation specialMethodInvocation;
            private final Assigner assigner;
            private final boolean serializableProxy;

            protected RedirectionProxy(TypeDescription morphingType, TypeDescription instrumentedType, Implementation.SpecialMethodInvocation specialMethodInvocation, Assigner assigner, boolean serializableProxy) {
                this.morphingType = morphingType;
                this.instrumentedType = instrumentedType;
                this.specialMethodInvocation = specialMethodInvocation;
                this.assigner = assigner;
                this.serializableProxy = serializableProxy;
            }

            public String getSuffix() {
                return RandomString.hashOf(this.morphingType.hashCode()) + (this.serializableProxy ? "S" : "0");
            }

            public DynamicType make(String auxiliaryTypeName, ClassFileVersion classFileVersion, MethodAccessorFactory methodAccessorFactory) {
                Type[] typeArray;
                DynamicType.Builder<?> builder = new ByteBuddy(classFileVersion).with(TypeValidation.DISABLED).subclass(this.morphingType, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).name(auxiliaryTypeName).modifiers(DEFAULT_TYPE_MODIFIER);
                if (this.serializableProxy) {
                    Class[] classArray = new Class[1];
                    typeArray = classArray;
                    classArray[0] = Serializable.class;
                } else {
                    typeArray = new Class[]{};
                }
                return builder.implement(typeArray).defineConstructor(new ModifierContributor.ForMethod[0]).withParameters((Collection<TypeDefinition>)(this.specialMethodInvocation.getMethodDescription().isStatic() ? Collections.emptyList() : Collections.singletonList(this.instrumentedType))).intercept(this.specialMethodInvocation.getMethodDescription().isStatic() ? StaticFieldConstructor.INSTANCE : new InstanceFieldConstructor(this.instrumentedType)).method(ElementMatchers.isAbstract().and(ElementMatchers.isDeclaredBy(this.morphingType))).intercept(new MethodCall(methodAccessorFactory.registerAccessorFor(this.specialMethodInvocation, MethodAccessorFactory.AccessType.DEFAULT), this.assigner)).make();
            }

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                TypeDescription forwardingType = implementationContext.register(this);
                return new StackManipulation.Compound(TypeCreation.of(forwardingType), Duplication.SINGLE, this.specialMethodInvocation.getMethodDescription().isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)forwardingType.getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly())).apply(methodVisitor, implementationContext);
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
                if (this.serializableProxy != ((RedirectionProxy)object).serializableProxy) {
                    return false;
                }
                if (!this.morphingType.equals(((RedirectionProxy)object).morphingType)) {
                    return false;
                }
                if (!this.instrumentedType.equals(((RedirectionProxy)object).instrumentedType)) {
                    return false;
                }
                if (!this.specialMethodInvocation.equals(((RedirectionProxy)object).specialMethodInvocation)) {
                    return false;
                }
                return this.assigner.equals(((RedirectionProxy)object).assigner);
            }

            public int hashCode() {
                return ((((this.getClass().hashCode() * 31 + this.morphingType.hashCode()) * 31 + this.instrumentedType.hashCode()) * 31 + this.specialMethodInvocation.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.serializableProxy;
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class MethodCall
            implements Implementation {
                private final MethodDescription accessorMethod;
                private final Assigner assigner;

                protected MethodCall(MethodDescription accessorMethod, Assigner assigner) {
                    this.accessorMethod = accessorMethod;
                    this.assigner = assigner;
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    return new Appender(implementationTarget);
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
                    if (!this.accessorMethod.equals(((MethodCall)object).accessorMethod)) {
                        return false;
                    }
                    return this.assigner.equals(((MethodCall)object).assigner);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.accessorMethod.hashCode()) * 31 + this.assigner.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class Appender
                implements ByteCodeAppender {
                    private final TypeDescription typeDescription;

                    protected Appender(Implementation.Target implementationTarget) {
                        this.typeDescription = implementationTarget.getInstrumentedType();
                    }

                    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                        StackManipulation arrayReference = MethodVariableAccess.REFERENCE.loadFrom(1);
                        StackManipulation[] parameterLoading = new StackManipulation[MethodCall.this.accessorMethod.getParameters().size()];
                        int index = 0;
                        for (TypeDescription.Generic parameterType : MethodCall.this.accessorMethod.getParameters().asTypeList()) {
                            parameterLoading[index] = new StackManipulation.Compound(arrayReference, IntegerConstant.forValue(index), ArrayAccess.REFERENCE.load(), MethodCall.this.assigner.assign(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), parameterType, Assigner.Typing.DYNAMIC));
                            ++index;
                        }
                        StackManipulation.Size stackSize = new StackManipulation.Compound(MethodCall.this.accessorMethod.isStatic() ? StackManipulation.Trivial.INSTANCE : new StackManipulation.Compound(MethodVariableAccess.loadThis(), FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)this.typeDescription.getDeclaredFields().filter(ElementMatchers.named(RedirectionProxy.FIELD_NAME))).getOnly()).read()), new StackManipulation.Compound(parameterLoading), MethodInvocation.invoke(MethodCall.this.accessorMethod), MethodCall.this.assigner.assign(MethodCall.this.accessorMethod.getReturnType(), instrumentedMethod.getReturnType(), Assigner.Typing.DYNAMIC), MethodReturn.REFERENCE).apply(methodVisitor, implementationContext);
                        return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
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
                        if (!this.typeDescription.equals(((Appender)object).typeDescription)) {
                            return false;
                        }
                        return MethodCall.this.equals(((Appender)object).MethodCall.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + MethodCall.this.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class InstanceFieldConstructor
            implements Implementation {
                private final TypeDescription instrumentedType;

                protected InstanceFieldConstructor(TypeDescription instrumentedType) {
                    this.instrumentedType = instrumentedType;
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType.withField(new FieldDescription.Token(RedirectionProxy.FIELD_NAME, 18, this.instrumentedType.asGenericType()));
                }

                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    return new Appender(implementationTarget);
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
                    return this.instrumentedType.equals(((InstanceFieldConstructor)object).instrumentedType);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance
                protected static class Appender
                implements ByteCodeAppender {
                    private final FieldDescription fieldDescription;

                    protected Appender(Implementation.Target implementationTarget) {
                        this.fieldDescription = (FieldDescription)((FieldList)implementationTarget.getInstrumentedType().getDeclaredFields().filter(ElementMatchers.named(RedirectionProxy.FIELD_NAME))).getOnly();
                    }

                    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                        StackManipulation.Size stackSize = new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodInvocation.invoke(StaticFieldConstructor.INSTANCE.objectTypeDefaultConstructor), MethodVariableAccess.allArgumentsOf(instrumentedMethod).prependThisReference(), FieldAccess.forField(this.fieldDescription).write(), MethodReturn.VOID).apply(methodVisitor, implementationContext);
                        return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
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
                        return this.fieldDescription.equals(((Appender)object).fieldDescription);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum StaticFieldConstructor implements Implementation
            {
                INSTANCE;

                private final MethodDescription objectTypeDefaultConstructor = (MethodDescription)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly();

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    return new ByteCodeAppender.Simple(MethodVariableAccess.loadThis(), MethodInvocation.invoke(this.objectTypeDefaultConstructor), MethodReturn.VOID);
                }
            }
        }

        protected static interface DefaultMethodLocator {
            public Implementation.SpecialMethodInvocation resolve(Implementation.Target var1, MethodDescription var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class Explicit
            implements DefaultMethodLocator {
                private final TypeDescription typeDescription;

                public Explicit(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public Implementation.SpecialMethodInvocation resolve(Implementation.Target implementationTarget, MethodDescription source) {
                    if (!this.typeDescription.isInterface()) {
                        throw new IllegalStateException(source + " method carries default method call parameter on non-interface type");
                    }
                    return implementationTarget.invokeDefault(source.asSignatureToken(), this.typeDescription).withCheckedCompatibilityTo(source.asTypeToken());
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
                    return this.typeDescription.equals(((Explicit)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Implicit implements DefaultMethodLocator
            {
                INSTANCE;


                @Override
                public Implementation.SpecialMethodInvocation resolve(Implementation.Target implementationTarget, MethodDescription source) {
                    return implementationTarget.invokeDefault(source.asSignatureToken()).withCheckedCompatibilityTo(source.asTypeToken());
                }
            }
        }
    }
}

