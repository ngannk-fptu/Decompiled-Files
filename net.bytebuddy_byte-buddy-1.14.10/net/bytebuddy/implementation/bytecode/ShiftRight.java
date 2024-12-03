/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode;

import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ShiftRight implements StackManipulation
{
    INTEGER(122, StackSize.SINGLE, Unsigned.INTEGER),
    LONG(123, StackSize.DOUBLE, Unsigned.LONG);

    private final int opcode;
    private final StackSize stackSize;
    private final StackManipulation unsigned;

    private ShiftRight(int opcode, StackSize stackSize, StackManipulation unsigned) {
        this.opcode = opcode;
        this.stackSize = stackSize;
        this.unsigned = unsigned;
    }

    public StackManipulation toUnsigned() {
        return this.unsigned;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitInsn(this.opcode);
        return this.stackSize.toDecreasingSize();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum Unsigned implements StackManipulation
    {
        INTEGER(124, StackSize.SINGLE),
        LONG(125, StackSize.DOUBLE);

        private final int opcode;
        private final StackSize stackSize;

        private Unsigned(int opcode, StackSize stackSize) {
            this.opcode = opcode;
            this.stackSize = stackSize;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitInsn(this.opcode);
            return this.stackSize.toDecreasingSize();
        }
    }
}

