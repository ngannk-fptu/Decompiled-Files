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
import java.util.LinkedHashMap;
import java.util.Map;
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
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
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
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Pipe {
    public boolean serializableProxy() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Binder
    implements TargetMethodAnnotationDrivenBinder.ParameterBinder<Pipe> {
        private static final MethodDescription.InDefinedShape SERIALIZABLE_PROXY = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Pipe.class).getDeclaredMethods().filter(ElementMatchers.named("serializableProxy"))).getOnly();
        private final MethodDescription forwardingMethod;

        protected Binder(MethodDescription forwardingMethod) {
            this.forwardingMethod = forwardingMethod;
        }

        public static TargetMethodAnnotationDrivenBinder.ParameterBinder<Pipe> install(Class<?> type) {
            return Binder.install(TypeDescription.ForLoadedType.of(type));
        }

        public static TargetMethodAnnotationDrivenBinder.ParameterBinder<Pipe> install(TypeDescription typeDescription) {
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
            if (methodDescription.getParameters().size() != 1 || !((ParameterDescription)methodDescription.getParameters().getOnly()).getType().asErasure().represents((Type)((Object)Object.class))) {
                throw new IllegalArgumentException(methodDescription + " does not take a single Object-typed argument");
            }
            return methodDescription;
        }

        @Override
        public Class<Pipe> getHandledType() {
            return Pipe.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<Pipe> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (!target.getType().asErasure().equals(this.forwardingMethod.getDeclaringType())) {
                throw new IllegalStateException("Illegal use of @Pipe for " + target + " which was installed for " + this.forwardingMethod.getDeclaringType());
            }
            if (source.isStatic()) {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            return new MethodDelegationBinder.ParameterBinding.Anonymous(new RedirectionProxy(this.forwardingMethod.getDeclaringType().asErasure(), source, assigner, annotation.getValue(SERIALIZABLE_PROXY).resolve(Boolean.class)));
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

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class RedirectionProxy
        extends StackManipulation.AbstractBase
        implements AuxiliaryType {
            private static final String FIELD_NAME_PREFIX = "argument";
            private final TypeDescription forwardingType;
            private final MethodDescription sourceMethod;
            private final Assigner assigner;
            private final boolean serializableProxy;

            protected RedirectionProxy(TypeDescription forwardingType, MethodDescription sourceMethod, Assigner assigner, boolean serializableProxy) {
                this.forwardingType = forwardingType;
                this.sourceMethod = sourceMethod;
                this.assigner = assigner;
                this.serializableProxy = serializableProxy;
            }

            private static LinkedHashMap<String, TypeDescription> extractFields(MethodDescription methodDescription) {
                TypeList parameterTypes = methodDescription.getParameters().asTypeList().asErasures();
                LinkedHashMap<String, TypeDescription> typeDescriptions = new LinkedHashMap<String, TypeDescription>();
                int currentIndex = 0;
                for (TypeDescription parameterType : parameterTypes) {
                    typeDescriptions.put(RedirectionProxy.fieldName(currentIndex++), parameterType);
                }
                return typeDescriptions;
            }

            private static String fieldName(int index) {
                return FIELD_NAME_PREFIX + index;
            }

            @Override
            public String getSuffix() {
                return RandomString.hashOf(this.forwardingType.hashCode()) + RandomString.hashOf(this.sourceMethod.hashCode()) + (this.serializableProxy ? "S" : "0");
            }

            @Override
            public DynamicType make(String auxiliaryTypeName, ClassFileVersion classFileVersion, MethodAccessorFactory methodAccessorFactory) {
                Type[] typeArray;
                LinkedHashMap<String, TypeDescription> parameterFields = RedirectionProxy.extractFields(this.sourceMethod);
                DynamicType.Builder<?> builder = new ByteBuddy(classFileVersion).with(TypeValidation.DISABLED).subclass(this.forwardingType, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).name(auxiliaryTypeName).modifiers(DEFAULT_TYPE_MODIFIER);
                if (this.serializableProxy) {
                    Class[] classArray = new Class[1];
                    typeArray = classArray;
                    classArray[0] = Serializable.class;
                } else {
                    typeArray = new Class[]{};
                }
                DynamicType.Builder<Object> builder2 = builder.implement(typeArray).method(ElementMatchers.isAbstract().and(ElementMatchers.isDeclaredBy(this.forwardingType))).intercept(new MethodCall(this.sourceMethod, this.assigner)).defineConstructor(new ModifierContributor.ForMethod[0]).withParameters(parameterFields.values()).intercept(ConstructorCall.INSTANCE);
                for (Map.Entry<String, TypeDescription> field : parameterFields.entrySet()) {
                    builder2 = builder2.defineField(field.getKey(), (TypeDefinition)field.getValue(), Visibility.PRIVATE);
                }
                return builder2.make();
            }

            @Override
            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                TypeDescription forwardingType = implementationContext.register(this);
                return new StackManipulation.Compound(TypeCreation.of(forwardingType), Duplication.SINGLE, MethodVariableAccess.allArgumentsOf(this.sourceMethod), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)forwardingType.getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly())).apply(methodVisitor, implementationContext);
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
                if (!this.forwardingType.equals(((RedirectionProxy)object).forwardingType)) {
                    return false;
                }
                if (!this.sourceMethod.equals(((RedirectionProxy)object).sourceMethod)) {
                    return false;
                }
                return this.assigner.equals(((RedirectionProxy)object).assigner);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.forwardingType.hashCode()) * 31 + this.sourceMethod.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.serializableProxy;
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class MethodCall
            implements Implementation {
                private final MethodDescription redirectedMethod;
                private final Assigner assigner;

                private MethodCall(MethodDescription redirectedMethod, Assigner assigner) {
                    this.redirectedMethod = redirectedMethod;
                    this.assigner = assigner;
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    if (!this.redirectedMethod.isAccessibleTo(implementationTarget.getInstrumentedType())) {
                        throw new IllegalStateException("Cannot invoke " + this.redirectedMethod + " from outside of class via @Pipe proxy");
                    }
                    return new Appender(implementationTarget.getInstrumentedType());
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
                    if (!this.redirectedMethod.equals(((MethodCall)object).redirectedMethod)) {
                        return false;
                    }
                    return this.assigner.equals(((MethodCall)object).assigner);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.redirectedMethod.hashCode()) * 31 + this.assigner.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                private class Appender
                implements ByteCodeAppender {
                    private final TypeDescription instrumentedType;

                    private Appender(TypeDescription instrumentedType) {
                        this.instrumentedType = instrumentedType;
                    }

                    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                        FieldList<FieldDescription.InDefinedShape> fieldList = this.instrumentedType.getDeclaredFields();
                        StackManipulation[] fieldLoading = new StackManipulation[fieldList.size()];
                        int index = 0;
                        for (FieldDescription fieldDescription : fieldList) {
                            fieldLoading[index++] = new StackManipulation.Compound(MethodVariableAccess.loadThis(), FieldAccess.forField(fieldDescription).read());
                        }
                        StackManipulation.Size stackSize = new StackManipulation.Compound(MethodVariableAccess.REFERENCE.loadFrom(1), MethodCall.this.assigner.assign(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), MethodCall.this.redirectedMethod.getDeclaringType().asGenericType(), Assigner.Typing.DYNAMIC), new StackManipulation.Compound(fieldLoading), MethodInvocation.invoke(MethodCall.this.redirectedMethod), MethodCall.this.assigner.assign(MethodCall.this.redirectedMethod.getReturnType(), instrumentedMethod.getReturnType(), Assigner.Typing.DYNAMIC), MethodReturn.REFERENCE).apply(methodVisitor, implementationContext);
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
                        if (!this.instrumentedType.equals(((Appender)object).instrumentedType)) {
                            return false;
                        }
                        return MethodCall.this.equals(((Appender)object).MethodCall.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + MethodCall.this.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum ConstructorCall implements Implementation
            {
                INSTANCE;

                private final transient MethodDescription.InDefinedShape objectTypeDefaultConstructor = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly();

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    return new Appender(implementationTarget.getInstrumentedType());
                }

                @HashCodeAndEqualsPlugin.Enhance
                private static class Appender
                implements ByteCodeAppender {
                    private final TypeDescription instrumentedType;

                    private Appender(TypeDescription instrumentedType) {
                        this.instrumentedType = instrumentedType;
                    }

                    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                        FieldList<FieldDescription.InDefinedShape> fieldList = this.instrumentedType.getDeclaredFields();
                        StackManipulation[] fieldLoading = new StackManipulation[fieldList.size()];
                        int index = 0;
                        for (FieldDescription fieldDescription : fieldList) {
                            fieldLoading[index] = new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodVariableAccess.load((ParameterDescription)instrumentedMethod.getParameters().get(index)), FieldAccess.forField(fieldDescription).write());
                            ++index;
                        }
                        StackManipulation.Size stackSize = new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodInvocation.invoke(INSTANCE.objectTypeDefaultConstructor), new StackManipulation.Compound(fieldLoading), MethodReturn.VOID).apply(methodVisitor, implementationContext);
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
                        return this.instrumentedType.equals(((Appender)object).instrumentedType);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
                    }
                }
            }
        }
    }
}

