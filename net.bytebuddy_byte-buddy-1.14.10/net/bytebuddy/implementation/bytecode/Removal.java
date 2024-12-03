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
public enum Removal implements StackManipulation
{
    ZERO(StackSize.ZERO, 0){

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return StackManipulation.Size.ZERO;
        }
    }
    ,
    SINGLE(StackSize.SINGLE, 87),
    DOUBLE(StackSize.DOUBLE, 88);

    private final StackManipulation.Size size;
    private final int opcode;

    private Removal(StackSize stackSize, int opcode) {
        this.size = stackSize.toDecreasingSize();
        this.opcode = opcode;
    }

    public static StackManipulation of(TypeDefinition typeDefinition) {
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
        throw new AssertionError();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitInsn(this.opcode);
        return this.size;
    }
}

