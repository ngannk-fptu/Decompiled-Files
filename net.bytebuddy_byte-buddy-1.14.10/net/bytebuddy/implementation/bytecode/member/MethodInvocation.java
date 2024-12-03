/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.member;

import java.lang.reflect.Type;
import java.util.List;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.implementation.bytecode.constant.JavaConstantValue;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MethodInvocation {
    VIRTUAL(182, 5, 182, 5),
    INTERFACE(185, 9, 185, 9),
    STATIC(184, 6, 184, 6),
    SPECIAL(183, 7, 183, 7),
    SPECIAL_CONSTRUCTOR(183, 8, 183, 8),
    VIRTUAL_PRIVATE(182, 5, 183, 7),
    INTERFACE_PRIVATE(185, 9, 183, 7);

    private final int opcode;
    private final int handle;
    private final int legacyOpcode;
    private final int legacyHandle;

    private MethodInvocation(int opcode, int handle, int legacyOpcode, int legacyHandle) {
        this.opcode = opcode;
        this.handle = handle;
        this.legacyOpcode = legacyOpcode;
        this.legacyHandle = legacyHandle;
    }

    public static WithImplicitInvocationTargetType invoke(MethodDescription.InDefinedShape methodDescription) {
        if (methodDescription.isTypeInitializer()) {
            return IllegalInvocation.INSTANCE;
        }
        if (methodDescription.isStatic()) {
            MethodInvocation methodInvocation = STATIC;
            ((Object)((Object)methodInvocation)).getClass();
            return methodInvocation.new Invocation(methodDescription);
        }
        if (methodDescription.isConstructor()) {
            MethodInvocation methodInvocation = SPECIAL_CONSTRUCTOR;
            ((Object)((Object)methodInvocation)).getClass();
            return methodInvocation.new Invocation(methodDescription);
        }
        if (methodDescription.isPrivate()) {
            MethodInvocation methodInvocation = methodDescription.getDeclaringType().isInterface() ? INTERFACE_PRIVATE : VIRTUAL_PRIVATE;
            ((Object)((Object)methodInvocation)).getClass();
            return methodInvocation.new Invocation(methodDescription);
        }
        if (methodDescription.getDeclaringType().isInterface()) {
            MethodInvocation methodInvocation = INTERFACE;
            ((Object)((Object)methodInvocation)).getClass();
            return methodInvocation.new Invocation(methodDescription);
        }
        MethodInvocation methodInvocation = VIRTUAL;
        ((Object)((Object)methodInvocation)).getClass();
        return methodInvocation.new Invocation(methodDescription);
    }

    public static WithImplicitInvocationTargetType invoke(MethodDescription methodDescription) {
        MethodDescription.InDefinedShape declaredMethod = (MethodDescription.InDefinedShape)methodDescription.asDefined();
        return declaredMethod.getReturnType().asErasure().equals(methodDescription.getReturnType().asErasure()) ? MethodInvocation.invoke(declaredMethod) : OfGenericMethod.of(methodDescription, MethodInvocation.invoke(declaredMethod));
    }

    public static StackManipulation lookup() {
        return MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLES.getTypeStub(), new MethodDescription.Token("lookup", 9, JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().asGenericType())));
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum HandleType {
        EXACT("invokeExact"),
        REGULAR("invoke");

        private final String methodName;

        private HandleType(String methodName) {
            this.methodName = methodName;
        }

        protected String getMethodName() {
            return this.methodName;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class HandleInvocation
    extends StackManipulation.AbstractBase {
        private static final String METHOD_HANDLE = "java/lang/invoke/MethodHandle";
        private final MethodDescription.InDefinedShape methodDescription;
        private final HandleType type;

        protected HandleInvocation(MethodDescription.InDefinedShape methodDescription, HandleType type) {
            this.methodDescription = methodDescription;
            this.type = type;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitMethodInsn(182, METHOD_HANDLE, this.type.getMethodName(), this.methodDescription.isStatic() || this.methodDescription.isConstructor() ? this.methodDescription.getDescriptor() : "(" + this.methodDescription.getDeclaringType().getDescriptor() + this.methodDescription.getDescriptor().substring(1), false);
            int parameterSize = 1 + this.methodDescription.getStackSize();
            int returnValueSize = this.methodDescription.getReturnType().getStackSize().getSize();
            return new StackManipulation.Size(returnValueSize - parameterSize, Math.max(0, returnValueSize - parameterSize));
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
            if (!this.type.equals((Object)((HandleInvocation)object).type)) {
                return false;
            }
            return this.methodDescription.equals(((HandleInvocation)object).methodDescription);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + this.type.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class DynamicInvocation
    extends StackManipulation.AbstractBase {
        private final String methodName;
        private final TypeDescription returnType;
        private final List<? extends TypeDescription> parameterTypes;
        private final MethodDescription.InDefinedShape bootstrapMethod;
        private final List<? extends JavaConstant> arguments;

        public DynamicInvocation(String methodName, TypeDescription returnType, List<? extends TypeDescription> parameterTypes, MethodDescription.InDefinedShape bootstrapMethod, List<? extends JavaConstant> arguments) {
            this.methodName = methodName;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
            this.bootstrapMethod = bootstrapMethod;
            this.arguments = arguments;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            StringBuilder stringBuilder = new StringBuilder("(");
            for (TypeDescription typeDescription : this.parameterTypes) {
                stringBuilder.append(typeDescription.getDescriptor());
            }
            String methodDescriptor = stringBuilder.append(')').append(this.returnType.getDescriptor()).toString();
            Object[] objectArray = new Object[this.arguments.size()];
            int index = 0;
            for (JavaConstant javaConstant : this.arguments) {
                objectArray[index++] = javaConstant.accept(JavaConstantValue.Visitor.INSTANCE);
            }
            methodVisitor.visitInvokeDynamicInsn(this.methodName, methodDescriptor, new Handle(MethodInvocation.this.handle == MethodInvocation.this.legacyHandle || implementationContext.getClassFileVersion().isAtLeast(ClassFileVersion.JAVA_V11) ? MethodInvocation.this.handle : MethodInvocation.this.legacyHandle, this.bootstrapMethod.getDeclaringType().getInternalName(), this.bootstrapMethod.getInternalName(), this.bootstrapMethod.getDescriptor(), this.bootstrapMethod.getDeclaringType().isInterface()), objectArray);
            int stackSize = this.returnType.getStackSize().getSize() - StackSize.of(this.parameterTypes);
            return new StackManipulation.Size(stackSize, Math.max(stackSize, 0));
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
            if (!MethodInvocation.this.equals((Object)((DynamicInvocation)object).MethodInvocation.this)) {
                return false;
            }
            if (!this.methodName.equals(((DynamicInvocation)object).methodName)) {
                return false;
            }
            if (!this.returnType.equals(((DynamicInvocation)object).returnType)) {
                return false;
            }
            if (!((Object)this.parameterTypes).equals(((DynamicInvocation)object).parameterTypes)) {
                return false;
            }
            if (!this.bootstrapMethod.equals(((DynamicInvocation)object).bootstrapMethod)) {
                return false;
            }
            return ((Object)this.arguments).equals(((DynamicInvocation)object).arguments);
        }

        public int hashCode() {
            return (((((this.getClass().hashCode() * 31 + this.methodName.hashCode()) * 31 + this.returnType.hashCode()) * 31 + ((Object)this.parameterTypes).hashCode()) * 31 + this.bootstrapMethod.hashCode()) * 31 + ((Object)this.arguments).hashCode()) * 31 + MethodInvocation.this.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class Invocation
    extends StackManipulation.AbstractBase
    implements WithImplicitInvocationTargetType {
        private final TypeDescription typeDescription;
        private final MethodDescription.InDefinedShape methodDescription;

        protected Invocation(MethodDescription.InDefinedShape methodDescription) {
            this(methodDescription, methodDescription.getDeclaringType());
        }

        protected Invocation(MethodDescription.InDefinedShape methodDescription, TypeDescription typeDescription) {
            this.typeDescription = typeDescription;
            this.methodDescription = methodDescription;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitMethodInsn(MethodInvocation.this.opcode == MethodInvocation.this.legacyOpcode || implementationContext.getClassFileVersion().isAtLeast(ClassFileVersion.JAVA_V11) ? MethodInvocation.this.opcode : MethodInvocation.this.legacyOpcode, this.typeDescription.getInternalName(), this.methodDescription.getInternalName(), this.methodDescription.getDescriptor(), this.typeDescription.isInterface());
            int parameterSize = this.methodDescription.getStackSize();
            int returnValueSize = this.methodDescription.getReturnType().getStackSize().getSize();
            return new StackManipulation.Size(returnValueSize - parameterSize, Math.max(0, returnValueSize - parameterSize));
        }

        @Override
        public StackManipulation virtual(TypeDescription invocationTarget) {
            if (this.methodDescription.isConstructor() || this.methodDescription.isStatic()) {
                return StackManipulation.Illegal.INSTANCE;
            }
            if (this.methodDescription.isPrivate()) {
                return this.methodDescription.getDeclaringType().equals(invocationTarget) ? this : StackManipulation.Illegal.INSTANCE;
            }
            if (invocationTarget.isInterface()) {
                Invocation invocation;
                if (this.methodDescription.getDeclaringType().represents((Type)((Object)Object.class))) {
                    invocation = this;
                } else {
                    MethodInvocation methodInvocation = INTERFACE;
                    ((Object)((Object)methodInvocation)).getClass();
                    invocation = methodInvocation.new Invocation(this.methodDescription, invocationTarget);
                }
                return invocation;
            }
            MethodInvocation methodInvocation = VIRTUAL;
            ((Object)((Object)methodInvocation)).getClass();
            return methodInvocation.new Invocation(this.methodDescription, invocationTarget);
        }

        @Override
        public StackManipulation special(TypeDescription invocationTarget) {
            StackManipulation stackManipulation;
            if (this.methodDescription.isSpecializableFor(invocationTarget)) {
                MethodInvocation methodInvocation = SPECIAL;
                ((Object)((Object)methodInvocation)).getClass();
                stackManipulation = methodInvocation.new Invocation(this.methodDescription, invocationTarget);
            } else {
                stackManipulation = StackManipulation.Illegal.INSTANCE;
            }
            return stackManipulation;
        }

        @Override
        public StackManipulation dynamic(String methodName, TypeDescription returnType, List<? extends TypeDescription> methodType, List<? extends JavaConstant> arguments) {
            return this.methodDescription.isInvokeBootstrap(TypeList.Explicit.of(arguments)) ? new DynamicInvocation(methodName, returnType, new TypeList.Explicit(methodType), (MethodDescription.InDefinedShape)this.methodDescription.asDefined(), arguments) : StackManipulation.Illegal.INSTANCE;
        }

        @Override
        public StackManipulation onHandle(HandleType type) {
            return new HandleInvocation(this.methodDescription, type);
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
            if (!MethodInvocation.this.equals((Object)((Invocation)object).MethodInvocation.this)) {
                return false;
            }
            if (!this.typeDescription.equals(((Invocation)object).typeDescription)) {
                return false;
            }
            return this.methodDescription.equals(((Invocation)object).methodDescription);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + this.methodDescription.hashCode()) * 31 + MethodInvocation.this.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class OfGenericMethod
    implements WithImplicitInvocationTargetType {
        private final TypeDescription targetType;
        private final WithImplicitInvocationTargetType invocation;

        protected OfGenericMethod(TypeDescription targetType, WithImplicitInvocationTargetType invocation) {
            this.targetType = targetType;
            this.invocation = invocation;
        }

        protected static WithImplicitInvocationTargetType of(MethodDescription methodDescription, WithImplicitInvocationTargetType invocation) {
            return new OfGenericMethod(methodDescription.getReturnType().asErasure(), invocation);
        }

        @Override
        public StackManipulation virtual(TypeDescription invocationTarget) {
            return new StackManipulation.Compound(this.invocation.virtual(invocationTarget), TypeCasting.to(this.targetType));
        }

        @Override
        public StackManipulation special(TypeDescription invocationTarget) {
            return new StackManipulation.Compound(this.invocation.special(invocationTarget), TypeCasting.to(this.targetType));
        }

        @Override
        public StackManipulation dynamic(String methodName, TypeDescription returnType, List<? extends TypeDescription> methodType, List<? extends JavaConstant> arguments) {
            return this.invocation.dynamic(methodName, returnType, methodType, arguments);
        }

        @Override
        public StackManipulation onHandle(HandleType type) {
            return new StackManipulation.Compound(this.invocation.onHandle(type), TypeCasting.to(this.targetType));
        }

        @Override
        public boolean isValid() {
            return this.invocation.isValid();
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return new StackManipulation.Compound(this.invocation, TypeCasting.to(this.targetType)).apply(methodVisitor, implementationContext);
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
            if (!this.targetType.equals(((OfGenericMethod)object).targetType)) {
                return false;
            }
            return this.invocation.equals(((OfGenericMethod)object).invocation);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31 + this.invocation.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface WithImplicitInvocationTargetType
    extends StackManipulation {
        public StackManipulation virtual(TypeDescription var1);

        public StackManipulation special(TypeDescription var1);

        public StackManipulation dynamic(String var1, TypeDescription var2, List<? extends TypeDescription> var3, List<? extends JavaConstant> var4);

        public StackManipulation onHandle(HandleType var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum IllegalInvocation implements WithImplicitInvocationTargetType
    {
        INSTANCE;


        @Override
        public StackManipulation virtual(TypeDescription invocationTarget) {
            return StackManipulation.Illegal.INSTANCE;
        }

        @Override
        public StackManipulation special(TypeDescription invocationTarget) {
            return StackManipulation.Illegal.INSTANCE;
        }

        @Override
        public StackManipulation dynamic(String methodName, TypeDescription returnType, List<? extends TypeDescription> methodType, List<? extends JavaConstant> arguments) {
            return StackManipulation.Illegal.INSTANCE;
        }

        @Override
        public StackManipulation onHandle(HandleType type) {
            return StackManipulation.Illegal.INSTANCE;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return StackManipulation.Illegal.INSTANCE.apply(methodVisitor, implementationContext);
        }
    }
}

