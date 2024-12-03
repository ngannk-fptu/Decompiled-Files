/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.assign.primitive;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum PrimitiveWideningDelegate {
    BOOLEAN(StackManipulation.Trivial.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE),
    BYTE(StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE, StackManipulation.Trivial.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE, new WideningStackManipulation(133, StackSize.SINGLE.toIncreasingSize()), new WideningStackManipulation(134, StackSize.ZERO.toIncreasingSize()), new WideningStackManipulation(133, StackSize.SINGLE.toIncreasingSize())),
    SHORT(StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE, new WideningStackManipulation(133, StackSize.SINGLE.toIncreasingSize()), new WideningStackManipulation(134, StackSize.ZERO.toIncreasingSize()), new WideningStackManipulation(135, StackSize.SINGLE.toIncreasingSize())),
    CHARACTER(StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE, StackManipulation.Trivial.INSTANCE, new WideningStackManipulation(133, StackSize.SINGLE.toIncreasingSize()), new WideningStackManipulation(134, StackSize.ZERO.toIncreasingSize()), new WideningStackManipulation(135, StackSize.SINGLE.toIncreasingSize())),
    INTEGER(StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE, new WideningStackManipulation(133, StackSize.SINGLE.toIncreasingSize()), new WideningStackManipulation(134, StackSize.ZERO.toIncreasingSize()), new WideningStackManipulation(135, StackSize.SINGLE.toIncreasingSize())),
    LONG(StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE, new WideningStackManipulation(137, StackSize.SINGLE.toDecreasingSize()), new WideningStackManipulation(138, StackSize.ZERO.toIncreasingSize())),
    FLOAT(StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE, new WideningStackManipulation(141, StackSize.SINGLE.toIncreasingSize())),
    DOUBLE(StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Illegal.INSTANCE, StackManipulation.Trivial.INSTANCE);

    private final StackManipulation toBooleanStackManipulation;
    private final StackManipulation toByteStackManipulation;
    private final StackManipulation toShortStackManipulation;
    private final StackManipulation toCharacterStackManipulation;
    private final StackManipulation toIntegerStackManipulation;
    private final StackManipulation toLongStackManipulation;
    private final StackManipulation toFloatStackManipulation;
    private final StackManipulation toDoubleStackManipulation;

    private PrimitiveWideningDelegate(StackManipulation toBooleanStackManipulation, StackManipulation toByteStackManipulation, StackManipulation toShortStackManipulation, StackManipulation toCharacterStackManipulation, StackManipulation toIntegerStackManipulation, StackManipulation toLongStackManipulation, StackManipulation toFloatStackManipulation, StackManipulation toDoubleStackManipulation) {
        this.toBooleanStackManipulation = toBooleanStackManipulation;
        this.toByteStackManipulation = toByteStackManipulation;
        this.toShortStackManipulation = toShortStackManipulation;
        this.toCharacterStackManipulation = toCharacterStackManipulation;
        this.toIntegerStackManipulation = toIntegerStackManipulation;
        this.toLongStackManipulation = toLongStackManipulation;
        this.toFloatStackManipulation = toFloatStackManipulation;
        this.toDoubleStackManipulation = toDoubleStackManipulation;
    }

    public static PrimitiveWideningDelegate forPrimitive(TypeDefinition typeDefinition) {
        if (typeDefinition.represents(Boolean.TYPE)) {
            return BOOLEAN;
        }
        if (typeDefinition.represents(Byte.TYPE)) {
            return BYTE;
        }
        if (typeDefinition.represents(Short.TYPE)) {
            return SHORT;
        }
        if (typeDefinition.represents(Character.TYPE)) {
            return CHARACTER;
        }
        if (typeDefinition.represents(Integer.TYPE)) {
            return INTEGER;
        }
        if (typeDefinition.represents(Long.TYPE)) {
            return LONG;
        }
        if (typeDefinition.represents(Float.TYPE)) {
            return FLOAT;
        }
        if (typeDefinition.represents(Double.TYPE)) {
            return DOUBLE;
        }
        throw new IllegalArgumentException("Not a primitive, non-void type: " + typeDefinition);
    }

    public StackManipulation widenTo(TypeDefinition typeDefinition) {
        if (typeDefinition.represents(Boolean.TYPE)) {
            return this.toBooleanStackManipulation;
        }
        if (typeDefinition.represents(Byte.TYPE)) {
            return this.toByteStackManipulation;
        }
        if (typeDefinition.represents(Short.TYPE)) {
            return this.toShortStackManipulation;
        }
        if (typeDefinition.represents(Character.TYPE)) {
            return this.toCharacterStackManipulation;
        }
        if (typeDefinition.represents(Integer.TYPE)) {
            return this.toIntegerStackManipulation;
        }
        if (typeDefinition.represents(Long.TYPE)) {
            return this.toLongStackManipulation;
        }
        if (typeDefinition.represents(Float.TYPE)) {
            return this.toFloatStackManipulation;
        }
        if (typeDefinition.represents(Double.TYPE)) {
            return this.toDoubleStackManipulation;
        }
        throw new IllegalArgumentException("Not a primitive non-void type: " + typeDefinition);
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class WideningStackManipulation
    extends StackManipulation.AbstractBase {
        private final int conversionOpcode;
        private final StackManipulation.Size size;

        protected WideningStackManipulation(int conversionOpcode, StackManipulation.Size size) {
            this.conversionOpcode = conversionOpcode;
            this.size = size;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitInsn(this.conversionOpcode);
            return this.size;
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
            if (this.conversionOpcode != ((WideningStackManipulation)object).conversionOpcode) {
                return false;
            }
            return this.size.equals(((WideningStackManipulation)object).size);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.conversionOpcode) * 31 + this.size.hashCode();
        }
    }
}

