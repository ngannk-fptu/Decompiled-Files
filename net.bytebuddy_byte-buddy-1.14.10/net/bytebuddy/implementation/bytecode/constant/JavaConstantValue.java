/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.constant;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.ConstantDynamic;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public class JavaConstantValue
extends StackManipulation.AbstractBase {
    private final JavaConstant constant;

    public JavaConstantValue(JavaConstant constant) {
        this.constant = constant;
    }

    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitLdcInsn(this.constant.accept(Visitor.INSTANCE));
        return this.constant.getTypeDescription().getStackSize().toIncreasingSize();
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
        return this.constant.equals(((JavaConstantValue)object).constant);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.constant.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Visitor implements JavaConstant.Visitor<Object>
    {
        INSTANCE;


        @Override
        public Object onValue(JavaConstant.Simple<?> constant) {
            return constant.getValue();
        }

        @Override
        public Type onType(JavaConstant.Simple<TypeDescription> constant) {
            return Type.getType(constant.getValue().getDescriptor());
        }

        @Override
        public Type onMethodType(JavaConstant.MethodType constant) {
            StringBuilder stringBuilder = new StringBuilder().append('(');
            for (TypeDescription parameterType : constant.getParameterTypes()) {
                stringBuilder.append(parameterType.getDescriptor());
            }
            return Type.getMethodType(stringBuilder.append(')').append(constant.getReturnType().getDescriptor()).toString());
        }

        @Override
        public Handle onMethodHandle(JavaConstant.MethodHandle constant) {
            return new Handle(constant.getHandleType().getIdentifier(), constant.getOwnerType().getInternalName(), constant.getName(), constant.getDescriptor(), constant.getOwnerType().isInterface());
        }

        @Override
        public ConstantDynamic onDynamic(JavaConstant.Dynamic constant) {
            Object[] argument = new Object[constant.getArguments().size()];
            for (int index = 0; index < argument.length; ++index) {
                argument[index] = constant.getArguments().get(index).accept(this);
            }
            return new ConstantDynamic(constant.getName(), constant.getTypeDescription().getDescriptor(), this.onMethodHandle(constant.getBootstrap()), argument);
        }
    }
}

