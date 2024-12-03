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
public enum IntegerConstant implements StackManipulation
{
    MINUS_ONE(2),
    ZERO(3),
    ONE(4),
    TWO(5),
    THREE(6),
    FOUR(7),
    FIVE(8);

    private static final StackManipulation.Size SIZE;
    private final int opcode;

    private IntegerConstant(int opcode) {
        this.opcode = opcode;
    }

    public static StackManipulation forValue(boolean value) {
        return value ? ONE : ZERO;
    }

    public static StackManipulation forValue(int value) {
        switch (value) {
            case -1: {
                return MINUS_ONE;
            }
            case 0: {
                return ZERO;
            }
            case 1: {
                return ONE;
            }
            case 2: {
                return TWO;
            }
            case 3: {
                return THREE;
            }
            case 4: {
                return FOUR;
            }
            case 5: {
                return FIVE;
            }
        }
        if (value >= -128 && value <= 127) {
            return new SingleBytePush((byte)value);
        }
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            return new TwoBytePush((short)value);
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
        private final int value;

        protected ConstantPool(int value) {
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
            return this.getClass().hashCode() * 31 + this.value;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class TwoBytePush
    extends StackManipulation.AbstractBase {
        private final short value;

        protected TwoBytePush(short value) {
            this.value = value;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitIntInsn(17, this.value);
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
            return this.value == ((TwoBytePush)object).value;
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.value;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class SingleBytePush
    extends StackManipulation.AbstractBase {
        private final byte value;

        protected SingleBytePush(byte value) {
            this.value = value;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitIntInsn(16, this.value);
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
            return this.value == ((SingleBytePush)object).value;
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.value;
        }
    }
}

