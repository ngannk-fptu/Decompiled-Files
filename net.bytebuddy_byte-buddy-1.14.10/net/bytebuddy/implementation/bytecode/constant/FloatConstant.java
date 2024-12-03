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
public enum FloatConstant implements StackManipulation
{
    ZERO(11),
    ONE(12),
    TWO(13);

    private static final StackManipulation.Size SIZE;
    private final int opcode;

    private FloatConstant(int opcode) {
        this.opcode = opcode;
    }

    public static StackManipulation forValue(float value) {
        if (value == 0.0f) {
            return ZERO;
        }
        if (value == 1.0f) {
            return ONE;
        }
        if (value == 2.0f) {
            return TWO;
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
        SIZE = StackSize.SINGLE.toIncreasingSize();
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ConstantPool
    extends StackManipulation.AbstractBase {
        private final float value;

        protected ConstantPool(float value) {
            this.value = value;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitLdcInsn(Float.valueOf(this.value));
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
            return Float.compare(this.value, ((ConstantPool)object).value) == 0;
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + Float.floatToIntBits(this.value);
        }
    }
}

