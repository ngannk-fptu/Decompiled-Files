/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.member;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public class HandleInvocation
extends StackManipulation.AbstractBase {
    private static final String METHOD_HANDLE_NAME = "java/lang/invoke/MethodHandle";
    private static final String INVOKE_EXACT = "invokeExact";
    private final JavaConstant.MethodType methodType;

    public HandleInvocation(JavaConstant.MethodType methodType) {
        this.methodType = methodType;
    }

    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitMethodInsn(182, METHOD_HANDLE_NAME, INVOKE_EXACT, this.methodType.getDescriptor(), false);
        int size = this.methodType.getReturnType().getStackSize().getSize() - this.methodType.getParameterTypes().getStackSize();
        return new StackManipulation.Size(size, Math.max(size, 0));
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
        return this.methodType.equals(((HandleInvocation)object).methodType);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.methodType.hashCode();
    }
}

