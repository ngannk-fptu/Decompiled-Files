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
public enum DoubleConstant implements StackManipulation
{
    ZERO(14),
    ONE(15);

    private static final StackManipulation.Size SIZE;
    private final int opcode;

    private DoubleConstant(int opcode) {
        this.opcode = opcode;
    }

    public static StackManipulation forValue(double value) {
        if (value == 0.0) {
            return ZERO;
        }
        if (value == 1.0) {
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
        private final double value;

        protected ConstantPool(double value) {
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
            return Double.compare(this.value, ((ConstantPool)object).value) == 0;
        }

        public int hashCode() {
            long l = Double.doubleToLongBits(this.value);
            return this.getClass().hashCode() * 31 + (int)(l ^ l >>> 32);
        }
    }
}

