/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.auxiliary;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.Throw;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public class TypeProxy
implements AuxiliaryType {
    public static final String REFLECTION_METHOD = "make";
    public static final String INSTANCE_FIELD = "target";
    private final TypeDescription proxiedType;
    private final Implementation.Target implementationTarget;
    private final InvocationFactory invocationFactory;
    private final boolean ignoreFinalizer;
    private final boolean serializableProxy;

    public TypeProxy(TypeDescription proxiedType, Implementation.Target implementationTarget, InvocationFactory invocationFactory, boolean ignoreFinalizer, boolean serializableProxy) {
        this.proxiedType = proxiedType;
        this.implementationTarget = implementationTarget;
        this.invocationFactory = invocationFactory;
        this.ignoreFinalizer = ignoreFinalizer;
        this.serializableProxy = serializableProxy;
    }

    public String getSuffix() {
        return RandomString.hashOf(this.proxiedType.hashCode()) + (this.ignoreFinalizer ? "I" : "0") + (this.serializableProxy ? "S" : "0");
    }

    public DynamicType make(String auxiliaryTypeName, ClassFileVersion classFileVersion, MethodAccessorFactory methodAccessorFactory) {
        Type[] typeArray;
        DynamicType.Builder<?> builder = new ByteBuddy(classFileVersion).with(TypeValidation.DISABLED).ignore(this.ignoreFinalizer ? ElementMatchers.isFinalizer() : ElementMatchers.none()).subclass(this.proxiedType).name(auxiliaryTypeName).modifiers(DEFAULT_TYPE_MODIFIER);
        if (this.serializableProxy) {
            Class[] classArray = new Class[1];
            typeArray = classArray;
            classArray[0] = Serializable.class;
        } else {
            typeArray = new Class[]{};
        }
        return builder.implement(typeArray).method(ElementMatchers.any()).intercept(new MethodCall(methodAccessorFactory)).defineMethod(REFLECTION_METHOD, (Type)((Object)TargetType.class), Ownership.STATIC).intercept(SilentConstruction.INSTANCE).make();
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
        if (this.ignoreFinalizer != ((TypeProxy)object).ignoreFinalizer) {
            return false;
        }
        if (this.serializableProxy != ((TypeProxy)object).serializableProxy) {
            return false;
        }
        if (!this.proxiedType.equals(((TypeProxy)object).proxiedType)) {
            return false;
        }
        if (!this.implementationTarget.equals(((TypeProxy)object).implementationTarget)) {
            return false;
        }
        return this.invocationFactory.equals(((TypeProxy)object).invocationFactory);
    }

    public int hashCode() {
        return ((((this.getClass().hashCode() * 31 + this.proxiedType.hashCode()) * 31 + this.implementationTarget.hashCode()) * 31 + this.invocationFactory.hashCode()) * 31 + this.ignoreFinalizer) * 31 + this.serializableProxy;
    }

    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class MethodCall
    implements Implementation {
        private final MethodAccessorFactory methodAccessorFactory;

        protected MethodCall(MethodAccessorFactory methodAccessorFactory) {
            this.methodAccessorFactory = methodAccessorFactory;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType.withField(new FieldDescription.Token(TypeProxy.INSTANCE_FIELD, 65, TypeProxy.this.implementationTarget.getInstrumentedType().asGenericType()));
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
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
            if (!this.methodAccessorFactory.equals(((MethodCall)object).methodAccessorFactory)) {
                return false;
            }
            return TypeProxy.this.equals(((MethodCall)object).TypeProxy.this);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.methodAccessorFactory.hashCode()) * 31 + TypeProxy.this.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Appender
        implements ByteCodeAppender {
            private final StackManipulation fieldLoadingInstruction;

            protected Appender(TypeDescription instrumentedType) {
                this.fieldLoadingInstruction = FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(TypeProxy.INSTANCE_FIELD))).getOnly()).read();
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                Implementation.SpecialMethodInvocation specialMethodInvocation = TypeProxy.this.invocationFactory.invoke(TypeProxy.this.implementationTarget, TypeProxy.this.proxiedType, instrumentedMethod);
                StackManipulation.Size size = (specialMethodInvocation.isValid() ? new AccessorMethodInvocation(instrumentedMethod, specialMethodInvocation) : AbstractMethodErrorThrow.INSTANCE).apply(methodVisitor, implementationContext);
                return new ByteCodeAppender.Size(size.getMaximalSize(), instrumentedMethod.getStackSize());
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
                if (!this.fieldLoadingInstruction.equals(((Appender)object).fieldLoadingInstruction)) {
                    return false;
                }
                return MethodCall.this.equals(((Appender)object).MethodCall.this);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.fieldLoadingInstruction.hashCode()) * 31 + MethodCall.this.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class AccessorMethodInvocation
            implements StackManipulation {
                private final MethodDescription instrumentedMethod;
                private final Implementation.SpecialMethodInvocation specialMethodInvocation;

                protected AccessorMethodInvocation(MethodDescription instrumentedMethod, Implementation.SpecialMethodInvocation specialMethodInvocation) {
                    this.instrumentedMethod = instrumentedMethod;
                    this.specialMethodInvocation = specialMethodInvocation;
                }

                public boolean isValid() {
                    return this.specialMethodInvocation.isValid();
                }

                public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                    MethodDescription.InDefinedShape proxyMethod = MethodCall.this.methodAccessorFactory.registerAccessorFor(this.specialMethodInvocation, MethodAccessorFactory.AccessType.DEFAULT);
                    return new StackManipulation.Compound(MethodVariableAccess.loadThis(), Appender.this.fieldLoadingInstruction, MethodVariableAccess.allArgumentsOf(this.instrumentedMethod).asBridgeOf(proxyMethod), MethodInvocation.invoke(proxyMethod), MethodReturn.of(this.instrumentedMethod.getReturnType())).apply(methodVisitor, implementationContext);
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
                    if (!this.instrumentedMethod.equals(((AccessorMethodInvocation)object).instrumentedMethod)) {
                        return false;
                    }
                    if (!this.specialMethodInvocation.equals(((AccessorMethodInvocation)object).specialMethodInvocation)) {
                        return false;
                    }
                    return Appender.this.equals(((AccessorMethodInvocation)object).Appender.this);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.instrumentedMethod.hashCode()) * 31 + this.specialMethodInvocation.hashCode()) * 31 + Appender.this.hashCode();
                }
            }
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class ForDefaultMethod
    extends StackManipulation.AbstractBase {
        private final TypeDescription proxiedType;
        private final Implementation.Target implementationTarget;
        private final boolean serializableProxy;

        public ForDefaultMethod(TypeDescription proxiedType, Implementation.Target implementationTarget, boolean serializableProxy) {
            this.proxiedType = proxiedType;
            this.implementationTarget = implementationTarget;
            this.serializableProxy = serializableProxy;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            TypeDescription proxyType = implementationContext.register(new TypeProxy(this.proxiedType, this.implementationTarget, InvocationFactory.Default.DEFAULT_METHOD, true, this.serializableProxy));
            return new StackManipulation.Compound(TypeCreation.of(proxyType), Duplication.SINGLE, MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)proxyType.getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly()), Duplication.SINGLE, MethodVariableAccess.loadThis(), FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)proxyType.getDeclaredFields().filter(ElementMatchers.named(TypeProxy.INSTANCE_FIELD))).getOnly()).write()).apply(methodVisitor, implementationContext);
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
            if (this.serializableProxy != ((ForDefaultMethod)object).serializableProxy) {
                return false;
            }
            if (!this.proxiedType.equals(((ForDefaultMethod)object).proxiedType)) {
                return false;
            }
            return this.implementationTarget.equals(((ForDefaultMethod)object).implementationTarget);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + this.proxiedType.hashCode()) * 31 + this.implementationTarget.hashCode()) * 31 + this.serializableProxy;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class ForSuperMethodByReflectionFactory
    extends StackManipulation.AbstractBase {
        private final TypeDescription proxiedType;
        private final Implementation.Target implementationTarget;
        private final boolean ignoreFinalizer;
        private final boolean serializableProxy;

        public ForSuperMethodByReflectionFactory(TypeDescription proxiedType, Implementation.Target implementationTarget, boolean ignoreFinalizer, boolean serializableProxy) {
            this.proxiedType = proxiedType;
            this.implementationTarget = implementationTarget;
            this.ignoreFinalizer = ignoreFinalizer;
            this.serializableProxy = serializableProxy;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            TypeDescription proxyType = implementationContext.register(new TypeProxy(this.proxiedType, this.implementationTarget, InvocationFactory.Default.SUPER_METHOD, this.ignoreFinalizer, this.serializableProxy));
            return new StackManipulation.Compound(MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)proxyType.getDeclaredMethods().filter(ElementMatchers.named(TypeProxy.REFLECTION_METHOD).and(ElementMatchers.takesArguments(0)))).getOnly()), Duplication.SINGLE, MethodVariableAccess.loadThis(), FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)proxyType.getDeclaredFields().filter(ElementMatchers.named(TypeProxy.INSTANCE_FIELD))).getOnly()).write()).apply(methodVisitor, implementationContext);
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
            if (this.ignoreFinalizer != ((ForSuperMethodByReflectionFactory)object).ignoreFinalizer) {
                return false;
            }
            if (this.serializableProxy != ((ForSuperMethodByReflectionFactory)object).serializableProxy) {
                return false;
            }
            if (!this.proxiedType.equals(((ForSuperMethodByReflectionFactory)object).proxiedType)) {
                return false;
            }
            return this.implementationTarget.equals(((ForSuperMethodByReflectionFactory)object).implementationTarget);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.proxiedType.hashCode()) * 31 + this.implementationTarget.hashCode()) * 31 + this.ignoreFinalizer) * 31 + this.serializableProxy;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForSuperMethodByConstructor
    extends StackManipulation.AbstractBase {
        private final TypeDescription proxiedType;
        private final Implementation.Target implementationTarget;
        private final List<TypeDescription> constructorParameters;
        private final boolean ignoreFinalizer;
        private final boolean serializableProxy;

        public ForSuperMethodByConstructor(TypeDescription proxiedType, Implementation.Target implementationTarget, List<TypeDescription> constructorParameters, boolean ignoreFinalizer, boolean serializableProxy) {
            this.proxiedType = proxiedType;
            this.implementationTarget = implementationTarget;
            this.constructorParameters = constructorParameters;
            this.ignoreFinalizer = ignoreFinalizer;
            this.serializableProxy = serializableProxy;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            TypeDescription proxyType = implementationContext.register(new TypeProxy(this.proxiedType, this.implementationTarget, InvocationFactory.Default.SUPER_METHOD, this.ignoreFinalizer, this.serializableProxy));
            StackManipulation[] constructorValue = new StackManipulation[this.constructorParameters.size()];
            int index = 0;
            for (TypeDescription parameterType : this.constructorParameters) {
                constructorValue[index++] = DefaultValue.of(parameterType);
            }
            return new StackManipulation.Compound(TypeCreation.of(proxyType), Duplication.SINGLE, new StackManipulation.Compound(constructorValue), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)proxyType.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(this.constructorParameters)))).getOnly()), Duplication.SINGLE, MethodVariableAccess.loadThis(), FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)proxyType.getDeclaredFields().filter(ElementMatchers.named(TypeProxy.INSTANCE_FIELD))).getOnly()).write()).apply(methodVisitor, implementationContext);
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
            if (this.ignoreFinalizer != ((ForSuperMethodByConstructor)object).ignoreFinalizer) {
                return false;
            }
            if (this.serializableProxy != ((ForSuperMethodByConstructor)object).serializableProxy) {
                return false;
            }
            if (!this.proxiedType.equals(((ForSuperMethodByConstructor)object).proxiedType)) {
                return false;
            }
            if (!this.implementationTarget.equals(((ForSuperMethodByConstructor)object).implementationTarget)) {
                return false;
            }
            return ((Object)this.constructorParameters).equals(((ForSuperMethodByConstructor)object).constructorParameters);
        }

        public int hashCode() {
            return ((((this.getClass().hashCode() * 31 + this.proxiedType.hashCode()) * 31 + this.implementationTarget.hashCode()) * 31 + ((Object)this.constructorParameters).hashCode()) * 31 + this.ignoreFinalizer) * 31 + this.serializableProxy;
        }
    }

    public static interface InvocationFactory {
        public Implementation.SpecialMethodInvocation invoke(Implementation.Target var1, TypeDescription var2, MethodDescription var3);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements InvocationFactory
        {
            SUPER_METHOD{

                public Implementation.SpecialMethodInvocation invoke(Implementation.Target implementationTarget, TypeDescription proxiedType, MethodDescription instrumentedMethod) {
                    return implementationTarget.invokeDominant(instrumentedMethod.asSignatureToken());
                }
            }
            ,
            DEFAULT_METHOD{

                public Implementation.SpecialMethodInvocation invoke(Implementation.Target implementationTarget, TypeDescription proxiedType, MethodDescription instrumentedMethod) {
                    return implementationTarget.invokeDefault(instrumentedMethod.asSignatureToken(), proxiedType);
                }
            };

        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum SilentConstruction implements Implementation
    {
        INSTANCE;


        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(implementationTarget.getInstrumentedType());
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class Appender
        implements ByteCodeAppender {
            public static final String REFLECTION_FACTORY_INTERNAL_NAME = "sun/reflect/ReflectionFactory";
            public static final String GET_REFLECTION_FACTORY_METHOD_NAME = "getReflectionFactory";
            public static final String GET_REFLECTION_FACTORY_METHOD_DESCRIPTOR = "()Lsun/reflect/ReflectionFactory;";
            public static final String NEW_CONSTRUCTOR_FOR_SERIALIZATION_METHOD_NAME = "newConstructorForSerialization";
            public static final String NEW_CONSTRUCTOR_FOR_SERIALIZATION_METHOD_DESCRIPTOR = "(Ljava/lang/Class;Ljava/lang/reflect/Constructor;)Ljava/lang/reflect/Constructor;";
            public static final String JAVA_LANG_OBJECT_DESCRIPTOR = "Ljava/lang/Object;";
            public static final String JAVA_LANG_OBJECT_INTERNAL_NAME = "java/lang/Object";
            public static final String JAVA_LANG_CONSTRUCTOR_INTERNAL_NAME = "java/lang/reflect/Constructor";
            public static final String NEW_INSTANCE_METHOD_NAME = "newInstance";
            public static final String NEW_INSTANCE_METHOD_DESCRIPTOR = "([Ljava/lang/Object;)Ljava/lang/Object;";
            public static final String JAVA_LANG_CLASS_INTERNAL_NAME = "java/lang/Class";
            public static final String GET_DECLARED_CONSTRUCTOR_METHOD_NAME = "getDeclaredConstructor";
            public static final String GET_DECLARED_CONSTRUCTOR_METHOD_DESCRIPTOR = "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;";
            private final TypeDescription instrumentedType;

            private Appender(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                methodVisitor.visitMethodInsn(184, REFLECTION_FACTORY_INTERNAL_NAME, GET_REFLECTION_FACTORY_METHOD_NAME, GET_REFLECTION_FACTORY_METHOD_DESCRIPTOR, false);
                methodVisitor.visitLdcInsn(net.bytebuddy.jar.asm.Type.getType(this.instrumentedType.getDescriptor()));
                methodVisitor.visitLdcInsn(net.bytebuddy.jar.asm.Type.getType(JAVA_LANG_OBJECT_DESCRIPTOR));
                methodVisitor.visitInsn(3);
                methodVisitor.visitTypeInsn(189, JAVA_LANG_CLASS_INTERNAL_NAME);
                methodVisitor.visitMethodInsn(182, JAVA_LANG_CLASS_INTERNAL_NAME, GET_DECLARED_CONSTRUCTOR_METHOD_NAME, GET_DECLARED_CONSTRUCTOR_METHOD_DESCRIPTOR, false);
                methodVisitor.visitMethodInsn(182, REFLECTION_FACTORY_INTERNAL_NAME, NEW_CONSTRUCTOR_FOR_SERIALIZATION_METHOD_NAME, NEW_CONSTRUCTOR_FOR_SERIALIZATION_METHOD_DESCRIPTOR, false);
                methodVisitor.visitInsn(3);
                methodVisitor.visitTypeInsn(189, JAVA_LANG_OBJECT_INTERNAL_NAME);
                methodVisitor.visitMethodInsn(182, JAVA_LANG_CONSTRUCTOR_INTERNAL_NAME, NEW_INSTANCE_METHOD_NAME, NEW_INSTANCE_METHOD_DESCRIPTOR, false);
                methodVisitor.visitTypeInsn(192, this.instrumentedType.getInternalName());
                methodVisitor.visitInsn(176);
                return new ByteCodeAppender.Size(4, 0);
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum AbstractMethodErrorThrow implements StackManipulation
    {
        INSTANCE;

        private final transient StackManipulation implementation;

        private AbstractMethodErrorThrow() {
            TypeDescription abstractMethodError = TypeDescription.ForLoadedType.of(AbstractMethodError.class);
            MethodDescription constructor = (MethodDescription)((MethodList)abstractMethodError.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(0)))).getOnly();
            this.implementation = new StackManipulation.Compound(TypeCreation.of(abstractMethodError), Duplication.SINGLE, MethodInvocation.invoke(constructor), Throw.INSTANCE);
        }

        @Override
        public boolean isValid() {
            return this.implementation.isValid();
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return this.implementation.apply(methodVisitor, implementationContext);
        }
    }
}

