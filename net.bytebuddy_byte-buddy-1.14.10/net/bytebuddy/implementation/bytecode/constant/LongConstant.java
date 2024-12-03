/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.constant;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum LongConstant implements StackManipulation
{
    ZERO(9),
    ONE(10);

    private static final StackManipulation.Size SIZE;
    private final int opcode;

    private LongConstant(int opcode) {
        this.opcode = opcode;
    }

    public static StackManipulation forValue(long value) {
        if (value == 0L) {
            return ZERO;
        }
        if (value == 1L) {
            return ONE;
        }
        return new ConstantPool(value);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitInsn(this.opcode);
        return SIZE;
    }

    static {
        SIZE = StackSize.DOUBLE.toIncreasingSize();
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ConstantPool
    extends StackManipulation.AbstractBase {
        private final long value;

        protected ConstantPool(long value) {
            this.value = value;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitLdcInsn(this.value);
            return SIZE;
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
            return this.value == ((ConstantPool)object).value;
        }

        public int hashCode() {
            long l = this.value;
            return this.getClass().hashCode() * 31 + (int)(l ^ l >>> 32);
        }
    }
}

