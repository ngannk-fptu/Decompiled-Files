/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode;

import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum Duplication implements StackManipulation
{
    ZERO(StackSize.ZERO, 0){

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return this.size;
        }

        public StackManipulation flipOver(TypeDefinition typeDefinition) {
            throw new IllegalStateException("Cannot flip zero value");
        }
    }
    ,
    SINGLE(StackSize.SINGLE, 89){

        public StackManipulation flipOver(TypeDefinition typeDefinition) {
            switch (typeDefinition.getStackSize()) {
                case SINGLE: {
                    return WithFlip.SINGLE_SINGLE;
                }
                case DOUBLE: {
                    return WithFlip.SINGLE_DOUBLE;
                }
            }
            throw new IllegalArgumentException("Cannot flip: " + typeDefinition);
        }
    }
    ,
    DOUBLE(StackSize.DOUBLE, 92){

        public StackManipulation flipOver(TypeDefinition typeDefinition) {
            switch (typeDefinition.getStackSize()) {
                case SINGLE: {
                    return WithFlip.DOUBLE_SINGLE;
                }
                case DOUBLE: {
                    return WithFlip.DOUBLE_DOUBLE;
                }
            }
            throw new IllegalArgumentException("Cannot flip: " + typeDefinition);
        }
    };

    protected final StackManipulation.Size size;
    private final int opcode;

    private Duplication(StackSize stackSize, int opcode) {
        this.size = stackSize.toIncreasingSize();
        this.opcode = opcode;
    }

    public static Duplication of(TypeDefinition typeDefinition) {
        switch (typeDefinition.getStackSize()) {
            case SINGLE: {
                return SINGLE;
            }
            case DOUBLE: {
                return DOUBLE;
            }
            case ZERO: {
                return ZERO;
            }
        }
        throw new AssertionError((Object)("Unexpected type: " + typeDefinition));
    }

    public abstract StackManipulation flipOver(TypeDefinition var1);

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitInsn(this.opcode);
        return this.size;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum WithFlip implements StackManipulation
    {
        SINGLE_SINGLE(90, StackSize.SINGLE),
        SINGLE_DOUBLE(91, StackSize.SINGLE),
        DOUBLE_SINGLE(93, StackSize.DOUBLE),
        DOUBLE_DOUBLE(94, StackSize.DOUBLE);

        private final int opcode;
        private final StackSize stackSize;

        private WithFlip(int opcode, StackSize stackSize) {
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
            return this.stackSize.toIncreasingSize();
        }
    }
}

