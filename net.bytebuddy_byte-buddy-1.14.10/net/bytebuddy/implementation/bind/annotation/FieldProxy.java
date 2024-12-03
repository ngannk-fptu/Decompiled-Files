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
import net.bytebuddy.implementation.ExceptionMethod;
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
public @interface FieldProxy {
    public boolean serializableProxy() default false;

    public String value() default "";

    public Class<?> declaringType() default void.class;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Binder
    extends TargetMethodAnnotationDrivenBinder.ParameterBinder.ForFieldBinding<FieldProxy> {
        private static final MethodDescription.InDefinedShape DECLARING_TYPE;
        private static final MethodDescription.InDefinedShape FIELD_NAME;
        private static final MethodDescription.InDefinedShape SERIALIZABLE_PROXY;
        private final FieldResolver.Factory fieldResolverFactory;

        public static TargetMethodAnnotationDrivenBinder.ParameterBinder<FieldProxy> install(Class<?> type) {
            return Binder.install(TypeDescription.ForLoadedType.of(type));
        }

        public static TargetMethodAnnotationDrivenBinder.ParameterBinder<FieldProxy> install(TypeDescription typeDescription) {
            if (!typeDescription.isInterface()) {
                throw new IllegalArgumentException(typeDescription + " is not an interface");
            }
            if (!typeDescription.getInterfaces().isEmpty()) {
                throw new IllegalArgumentException(typeDescription + " must not extend other interfaces");
            }
            if (!typeDescription.isPublic()) {
                throw new IllegalArgumentException(typeDescription + " is not public");
            }
            MethodList methodCandidates = (MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.isAbstract());
            if (methodCandidates.size() != 2) {
                throw new IllegalArgumentException(typeDescription + " does not declare exactly two non-abstract methods");
            }
            MethodList getterCandidates = (MethodList)methodCandidates.filter(ElementMatchers.isGetter(Object.class));
            if (getterCandidates.size() != 1) {
                throw new IllegalArgumentException(typeDescription + " does not declare a getter with an Object type");
            }
            MethodList setterCandidates = (MethodList)methodCandidates.filter(ElementMatchers.isSetter(Object.class));
            if (setterCandidates.size() != 1) {
                throw new IllegalArgumentException(typeDescription + " does not declare a setter with an Object type");
            }
            return new Binder(typeDescription, (MethodDescription.InDefinedShape)getterCandidates.getOnly(), (MethodDescription.InDefinedShape)setterCandidates.getOnly());
        }

        public static TargetMethodAnnotationDrivenBinder.ParameterBinder<FieldProxy> install(Class<?> getterType, Class<?> setterType) {
            return Binder.install(TypeDescription.ForLoadedType.of(getterType), TypeDescription.ForLoadedType.of(setterType));
        }

        public static TargetMethodAnnotationDrivenBinder.ParameterBinder<FieldProxy> install(TypeDescription getterType, TypeDescription setterType) {
            MethodDescription.InDefinedShape getterMethod = Binder.onlyMethod(getterType);
            if (!getterMethod.getReturnType().asErasure().represents((Type)((Object)Object.class))) {
                throw new IllegalArgumentException(getterMethod + " must take a single Object-typed parameter");
            }
            if (getterMethod.getParameters().size() != 0) {
                throw new IllegalArgumentException(getterMethod + " must not declare parameters");
            }
            MethodDescription.InDefinedShape setterMethod = Binder.onlyMethod(setterType);
            if (!setterMethod.getReturnType().asErasure().represents(Void.TYPE)) {
                throw new IllegalArgumentException(setterMethod + " must return void");
            }
            if (setterMethod.getParameters().size() != 1 || !((ParameterDescription.InDefinedShape)setterMethod.getParameters().get(0)).getType().asErasure().represents((Type)((Object)Object.class))) {
                throw new IllegalArgumentException(setterMethod + " must declare a single Object-typed parameters");
            }
            return new Binder(getterMethod, setterMethod);
        }

        private static MethodDescription.InDefinedShape onlyMethod(TypeDescription typeDescription) {
            if (!typeDescription.isInterface()) {
                throw new IllegalArgumentException(typeDescription + " is not an interface");
            }
            if (!typeDescription.getInterfaces().isEmpty()) {
                throw new IllegalArgumentException(typeDescription + " must not extend other interfaces");
            }
            if (!typeDescription.isPublic()) {
                throw new IllegalArgumentException(typeDescription + " is not public");
            }
            MethodList methodCandidates = (MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.isAbstract());
            if (methodCandidates.size() != 1) {
                throw new IllegalArgumentException(typeDescription + " must declare exactly one abstract method");
            }
            return (MethodDescription.InDefinedShape)methodCandidates.getOnly();
        }

        protected Binder(MethodDescription.InDefinedShape getterMethod, MethodDescription.InDefinedShape setterMethod) {
            this(new FieldResolver.Factory.Simplex(getterMethod, setterMethod));
        }

        protected Binder(TypeDescription proxyType, MethodDescription.InDefinedShape getterMethod, MethodDescription.InDefinedShape setterMethod) {
            this(new FieldResolver.Factory.Duplex(proxyType, getterMethod, setterMethod));
        }

        protected Binder(FieldResolver.Factory fieldResolverFactory) {
            this.fieldResolverFactory = fieldResolverFactory;
        }

        @Override
        public Class<FieldProxy> getHandledType() {
            return FieldProxy.class;
        }

        @Override
        protected String fieldName(AnnotationDescription.Loadable<FieldProxy> annotation) {
            return annotation.getValue(FIELD_NAME).resolve(String.class);
        }

        @Override
        protected TypeDescription declaringType(AnnotationDescription.Loadable<FieldProxy> annotation) {
            return annotation.getValue(DECLARING_TYPE).resolve(TypeDescription.class);
        }

        @Override
        protected MethodDelegationBinder.ParameterBinding<?> bind(FieldDescription fieldDescription, AnnotationDescription.Loadable<FieldProxy> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner) {
            FieldResolver fieldResolver = this.fieldResolverFactory.resolve(target.getType().asErasure(), fieldDescription);
            if (fieldResolver.isResolved()) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(new AccessorProxy(fieldDescription, implementationTarget.getInstrumentedType(), fieldResolver, assigner, annotation.getValue(SERIALIZABLE_PROXY).resolve(Boolean.class)));
            }
            return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }

        static {
            MethodList<MethodDescription.InDefinedShape> methodList = TypeDescription.ForLoadedType.of(FieldProxy.class).getDeclaredMethods();
            DECLARING_TYPE = (MethodDescription.InDefinedShape)((MethodList)methodList.filter(ElementMatchers.named("declaringType"))).getOnly();
            FIELD_NAME = (MethodDescription.InDefinedShape)((MethodList)methodList.filter(ElementMatchers.named("value"))).getOnly();
            SERIALIZABLE_PROXY = (MethodDescription.InDefinedShape)((MethodList)methodList.filter(ElementMatchers.named("serializableProxy"))).getOnly();
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
            return this.fieldResolverFactory.equals(((Binder)object).fieldResolverFactory);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.fieldResolverFactory.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected static class AccessorProxy
        extends StackManipulation.AbstractBase
        implements AuxiliaryType {
            protected static final String FIELD_NAME = "instance";
            private final FieldDescription fieldDescription;
            private final TypeDescription instrumentedType;
            private final FieldResolver fieldResolver;
            private final Assigner assigner;
            private final boolean serializableProxy;

            protected AccessorProxy(FieldDescription fieldDescription, TypeDescription instrumentedType, FieldResolver fieldResolver, Assigner assigner, boolean serializableProxy) {
                this.fieldDescription = fieldDescription;
                this.instrumentedType = instrumentedType;
                this.fieldResolver = fieldResolver;
                this.assigner = assigner;
                this.serializableProxy = serializableProxy;
            }

            public String getSuffix() {
                return RandomString.hashOf(this.fieldDescription.hashCode()) + (this.serializableProxy ? "S" : "0");
            }

            public DynamicType make(String auxiliaryTypeName, ClassFileVersion classFileVersion, MethodAccessorFactory methodAccessorFactory) {
                Type[] typeArray;
                DynamicType.Builder<?> builder = new ByteBuddy(classFileVersion).with(TypeValidation.DISABLED).subclass(this.fieldResolver.getProxyType(), (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).name(auxiliaryTypeName).modifiers(DEFAULT_TYPE_MODIFIER);
                if (this.serializableProxy) {
                    Class[] classArray = new Class[1];
                    typeArray = classArray;
                    classArray[0] = Serializable.class;
                } else {
                    typeArray = new Class[]{};
                }
                return this.fieldResolver.apply(builder.implement(typeArray).defineConstructor(new ModifierContributor.ForMethod[0]).withParameters((Collection<TypeDefinition>)(this.fieldDescription.isStatic() ? Collections.emptyList() : Collections.singletonList(this.instrumentedType))).intercept(this.fieldDescription.isStatic() ? StaticFieldConstructor.INSTANCE : new InstanceFieldConstructor(this.instrumentedType)), this.fieldDescription, this.assigner, methodAccessorFactory).make();
            }

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                TypeDescription auxiliaryType = implementationContext.register(this);
                return new StackManipulation.Compound(TypeCreation.of(auxiliaryType), Duplication.SINGLE, this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)auxiliaryType.getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly())).apply(methodVisitor, implementationContext);
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
                if (this.serializableProxy != ((AccessorProxy)object).serializableProxy) {
                    return false;
                }
                if (!this.fieldDescription.equals(((AccessorProxy)object).fieldDescription)) {
                    return false;
                }
                if (!this.instrumentedType.equals(((AccessorProxy)object).instrumentedType)) {
                    return false;
                }
                if (!this.fieldResolver.equals(((AccessorProxy)object).fieldResolver)) {
                    return false;
                }
                return this.assigner.equals(((AccessorProxy)object).assigner);
            }

            public int hashCode() {
                return ((((this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + this.instrumentedType.hashCode()) * 31 + this.fieldResolver.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.serializableProxy;
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class FieldSetter
        implements Implementation {
            private final FieldDescription fieldDescription;
            private final Assigner assigner;
            private final MethodAccessorFactory methodAccessorFactory;

            protected FieldSetter(FieldDescription fieldDescription, Assigner assigner, MethodAccessorFactory methodAccessorFactory) {
                this.fieldDescription = fieldDescription;
                this.assigner = assigner;
                this.methodAccessorFactory = methodAccessorFactory;
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
                if (!this.fieldDescription.equals(((FieldSetter)object).fieldDescription)) {
                    return false;
                }
                if (!this.assigner.equals(((FieldSetter)object).assigner)) {
                    return false;
                }
                return this.methodAccessorFactory.equals(((FieldSetter)object).methodAccessorFactory);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.methodAccessorFactory.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class Appender
            implements ByteCodeAppender {
                private final TypeDescription typeDescription;

                protected Appender(Implementation.Target implementationTarget) {
                    this.typeDescription = implementationTarget.getInstrumentedType();
                }

                public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                    TypeDescription.Generic parameterType = ((ParameterDescription)instrumentedMethod.getParameters().get(0)).getType();
                    MethodDescription.InDefinedShape setterMethod = FieldSetter.this.methodAccessorFactory.registerSetterFor(FieldSetter.this.fieldDescription, MethodAccessorFactory.AccessType.DEFAULT);
                    StackManipulation.Size stackSize = new StackManipulation.Compound(FieldSetter.this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : new StackManipulation.Compound(MethodVariableAccess.loadThis(), FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)this.typeDescription.getDeclaredFields().filter(ElementMatchers.named("instance"))).getOnly()).read()), MethodVariableAccess.of(parameterType).loadFrom(1), FieldSetter.this.assigner.assign(parameterType, ((ParameterDescription)setterMethod.getParameters().get(0)).getType(), Assigner.Typing.DYNAMIC), MethodInvocation.invoke((MethodDescription)setterMethod), MethodReturn.VOID).apply(methodVisitor, implementationContext);
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
                    return FieldSetter.this.equals(((Appender)object).FieldSetter.this);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + FieldSetter.this.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class FieldGetter
        implements Implementation {
            private final FieldDescription fieldDescription;
            private final Assigner assigner;
            private final MethodAccessorFactory methodAccessorFactory;

            protected FieldGetter(FieldDescription fieldDescription, Assigner assigner, MethodAccessorFactory methodAccessorFactory) {
                this.fieldDescription = fieldDescription;
                this.assigner = assigner;
                this.methodAccessorFactory = methodAccessorFactory;
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
                if (!this.fieldDescription.equals(((FieldGetter)object).fieldDescription)) {
                    return false;
                }
                if (!this.assigner.equals(((FieldGetter)object).assigner)) {
                    return false;
                }
                return this.methodAccessorFactory.equals(((FieldGetter)object).methodAccessorFactory);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.methodAccessorFactory.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class Appender
            implements ByteCodeAppender {
                private final TypeDescription typeDescription;

                protected Appender(Implementation.Target implementationTarget) {
                    this.typeDescription = implementationTarget.getInstrumentedType();
                }

                public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                    MethodDescription.InDefinedShape getterMethod = FieldGetter.this.methodAccessorFactory.registerGetterFor(FieldGetter.this.fieldDescription, MethodAccessorFactory.AccessType.DEFAULT);
                    StackManipulation.Size stackSize = new StackManipulation.Compound(FieldGetter.this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : new StackManipulation.Compound(MethodVariableAccess.loadThis(), FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)this.typeDescription.getDeclaredFields().filter(ElementMatchers.named("instance"))).getOnly()).read()), MethodInvocation.invoke((MethodDescription)getterMethod), FieldGetter.this.assigner.assign(getterMethod.getReturnType(), instrumentedMethod.getReturnType(), Assigner.Typing.DYNAMIC), MethodReturn.of(instrumentedMethod.getReturnType().asErasure())).apply(methodVisitor, implementationContext);
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
                    return FieldGetter.this.equals(((Appender)object).FieldGetter.this);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + FieldGetter.this.hashCode();
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
                return instrumentedType.withField(new FieldDescription.Token("instance", 18, this.instrumentedType.asGenericType()));
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
                    this.fieldDescription = (FieldDescription)((FieldList)implementationTarget.getInstrumentedType().getDeclaredFields().filter(ElementMatchers.named("instance"))).getOnly();
                }

                public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                    StackManipulation.Size stackSize = new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodInvocation.invoke(StaticFieldConstructor.INSTANCE.objectTypeDefaultConstructor), MethodVariableAccess.allArgumentsOf((MethodDescription)instrumentedMethod.asDefined()).prependThisReference(), FieldAccess.forField(this.fieldDescription).write(), MethodReturn.VOID).apply(methodVisitor, implementationContext);
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

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface FieldResolver {
            public boolean isResolved();

            public TypeDescription getProxyType();

            public DynamicType.Builder<?> apply(DynamicType.Builder<?> var1, FieldDescription var2, Assigner var3, MethodAccessorFactory var4);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForGetterSetterPair
            implements FieldResolver {
                private final TypeDescription proxyType;
                private final MethodDescription.InDefinedShape getterMethod;
                private final MethodDescription.InDefinedShape setterMethod;

                protected ForGetterSetterPair(TypeDescription proxyType, MethodDescription.InDefinedShape getterMethod, MethodDescription.InDefinedShape setterMethod) {
                    this.proxyType = proxyType;
                    this.getterMethod = getterMethod;
                    this.setterMethod = setterMethod;
                }

                @Override
                public boolean isResolved() {
                    return true;
                }

                @Override
                public TypeDescription getProxyType() {
                    return this.proxyType;
                }

                @Override
                public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, FieldDescription fieldDescription, Assigner assigner, MethodAccessorFactory methodAccessorFactory) {
                    return builder.method(ElementMatchers.is(this.getterMethod)).intercept(new FieldGetter(fieldDescription, assigner, methodAccessorFactory)).method(ElementMatchers.is(this.setterMethod)).intercept(fieldDescription.isFinal() ? ExceptionMethod.throwing(UnsupportedOperationException.class, "Cannot set final field " + fieldDescription) : new FieldSetter(fieldDescription, assigner, methodAccessorFactory));
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
                    if (!this.proxyType.equals(((ForGetterSetterPair)object).proxyType)) {
                        return false;
                    }
                    if (!this.getterMethod.equals(((ForGetterSetterPair)object).getterMethod)) {
                        return false;
                    }
                    return this.setterMethod.equals(((ForGetterSetterPair)object).setterMethod);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.proxyType.hashCode()) * 31 + this.getterMethod.hashCode()) * 31 + this.setterMethod.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForSetter
            implements FieldResolver {
                private final MethodDescription.InDefinedShape setterMethod;

                protected ForSetter(MethodDescription.InDefinedShape setterMethod) {
                    this.setterMethod = setterMethod;
                }

                @Override
                public boolean isResolved() {
                    return true;
                }

                @Override
                public TypeDescription getProxyType() {
                    return this.setterMethod.getDeclaringType();
                }

                @Override
                public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, FieldDescription fieldDescription, Assigner assigner, MethodAccessorFactory methodAccessorFactory) {
                    return builder.method(ElementMatchers.is(this.setterMethod)).intercept(new FieldSetter(fieldDescription, assigner, methodAccessorFactory));
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
                    return this.setterMethod.equals(((ForSetter)object).setterMethod);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.setterMethod.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForGetter
            implements FieldResolver {
                private final MethodDescription.InDefinedShape getterMethod;

                protected ForGetter(MethodDescription.InDefinedShape getterMethod) {
                    this.getterMethod = getterMethod;
                }

                @Override
                public boolean isResolved() {
                    return true;
                }

                @Override
                public TypeDescription getProxyType() {
                    return this.getterMethod.getDeclaringType();
                }

                @Override
                public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, FieldDescription fieldDescription, Assigner assigner, MethodAccessorFactory methodAccessorFactory) {
                    return builder.method(ElementMatchers.definedMethod(ElementMatchers.is(this.getterMethod))).intercept(new FieldGetter(fieldDescription, assigner, methodAccessorFactory));
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
                    return this.getterMethod.equals(((ForGetter)object).getterMethod);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.getterMethod.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Unresolved implements FieldResolver
            {
                INSTANCE;


                @Override
                public boolean isResolved() {
                    return false;
                }

                @Override
                public TypeDescription getProxyType() {
                    throw new IllegalStateException("Cannot read type for unresolved field resolver");
                }

                @Override
                public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, FieldDescription fieldDescription, Assigner assigner, MethodAccessorFactory methodAccessorFactory) {
                    throw new IllegalStateException("Cannot apply unresolved field resolver");
                }
            }

            public static interface Factory {
                public FieldResolver resolve(TypeDescription var1, FieldDescription var2);

                @HashCodeAndEqualsPlugin.Enhance
                public static class Simplex
                implements Factory {
                    private final MethodDescription.InDefinedShape getterMethod;
                    private final MethodDescription.InDefinedShape setterMethod;

                    protected Simplex(MethodDescription.InDefinedShape getterMethod, MethodDescription.InDefinedShape setterMethod) {
                        this.getterMethod = getterMethod;
                        this.setterMethod = setterMethod;
                    }

                    public FieldResolver resolve(TypeDescription parameterType, FieldDescription fieldDescription) {
                        if (parameterType.equals(this.getterMethod.getDeclaringType())) {
                            return new ForGetter(this.getterMethod);
                        }
                        if (parameterType.equals(this.setterMethod.getDeclaringType())) {
                            return fieldDescription.isFinal() ? Unresolved.INSTANCE : new ForSetter(this.setterMethod);
                        }
                        throw new IllegalStateException("Cannot use @FieldProxy on a non-installed type");
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
                        if (!this.getterMethod.equals(((Simplex)object).getterMethod)) {
                            return false;
                        }
                        return this.setterMethod.equals(((Simplex)object).setterMethod);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.getterMethod.hashCode()) * 31 + this.setterMethod.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class Duplex
                implements Factory {
                    private final TypeDescription proxyType;
                    private final MethodDescription.InDefinedShape getterMethod;
                    private final MethodDescription.InDefinedShape setterMethod;

                    protected Duplex(TypeDescription proxyType, MethodDescription.InDefinedShape getterMethod, MethodDescription.InDefinedShape setterMethod) {
                        this.proxyType = proxyType;
                        this.getterMethod = getterMethod;
                        this.setterMethod = setterMethod;
                    }

                    public FieldResolver resolve(TypeDescription parameterType, FieldDescription fieldDescription) {
                        if (parameterType.equals(this.proxyType)) {
                            return new ForGetterSetterPair(this.proxyType, this.getterMethod, this.setterMethod);
                        }
                        throw new IllegalStateException("Cannot use @FieldProxy on a non-installed type");
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
                        if (!this.proxyType.equals(((Duplex)object).proxyType)) {
                            return false;
                        }
                        if (!this.getterMethod.equals(((Duplex)object).getterMethod)) {
                            return false;
                        }
                        return this.setterMethod.equals(((Duplex)object).setterMethod);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.proxyType.hashCode()) * 31 + this.getterMethod.hashCode()) * 31 + this.setterMethod.hashCode();
                    }
                }
            }
        }
    }
}

