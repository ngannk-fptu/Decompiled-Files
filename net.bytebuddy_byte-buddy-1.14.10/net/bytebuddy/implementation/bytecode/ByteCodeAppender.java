/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface ByteCodeAppender {
    public Size apply(MethodVisitor var1, Implementation.Context var2, MethodDescription var3);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Simple
    implements ByteCodeAppender {
        private final StackManipulation stackManipulation;

        public Simple(StackManipulation ... stackManipulation) {
            this(Arrays.asList(stackManipulation));
        }

        public Simple(List<? extends StackManipulation> stackManipulations) {
            this.stackManipulation = new StackManipulation.Compound(stackManipulations);
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            return new Size(this.stackManipulation.apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
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
            return this.stackManipulation.equals(((Simple)object).stackManipulation);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.stackManipulation.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements ByteCodeAppender {
        private final List<ByteCodeAppender> byteCodeAppenders = new ArrayList<ByteCodeAppender>();

        public Compound(ByteCodeAppender ... byteCodeAppender) {
            this(Arrays.asList(byteCodeAppender));
        }

        public Compound(List<? extends ByteCodeAppender> byteCodeAppenders) {
            for (ByteCodeAppender byteCodeAppender : byteCodeAppenders) {
                if (byteCodeAppender instanceof Compound) {
                    this.byteCodeAppenders.addAll(((Compound)byteCodeAppender).byteCodeAppenders);
                    continue;
                }
                this.byteCodeAppenders.add(byteCodeAppender);
            }
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Size size = new Size(0, instrumentedMethod.getStackSize());
            for (ByteCodeAppender byteCodeAppender : this.byteCodeAppenders) {
                size = size.merge(byteCodeAppender.apply(methodVisitor, implementationContext, instrumentedMethod));
            }
            return size;
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
            return ((Object)this.byteCodeAppenders).equals(((Compound)object).byteCodeAppenders);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.byteCodeAppenders).hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class Size {
        public static final Size ZERO = new Size(0, 0);
        private final int operandStackSize;
        private final int localVariableSize;

        public Size(int operandStackSize, int localVariableSize) {
            this.operandStackSize = operandStackSize;
            this.localVariableSize = localVariableSize;
        }

        public int getOperandStackSize() {
            return this.operandStackSize;
        }

        public int getLocalVariableSize() {
            return this.localVariableSize;
        }

        public Size merge(Size other) {
            return new Size(Math.max(this.operandStackSize, other.operandStackSize), Math.max(this.localVariableSize, other.localVariableSize));
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
            if (this.operandStackSize != ((Size)object).operandStackSize) {
                return false;
            }
            return this.localVariableSize == ((Size)object).localVariableSize;
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.operandStackSize) * 31 + this.localVariableSize;
        }
    }
}

