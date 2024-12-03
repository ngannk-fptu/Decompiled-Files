/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.assign.primitive;

import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.jar.asm.MethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum PrimitiveBoxingDelegate {
    BOOLEAN(Boolean.class, StackSize.ZERO, "valueOf", "(Z)Ljava/lang/Boolean;"),
    BYTE(Byte.class, StackSize.ZERO, "valueOf", "(B)Ljava/lang/Byte;"),
    SHORT(Short.class, StackSize.ZERO, "valueOf", "(S)Ljava/lang/Short;"),
    CHARACTER(Character.class, StackSize.ZERO, "valueOf", "(C)Ljava/lang/Character;"),
    INTEGER(Integer.class, StackSize.ZERO, "valueOf", "(I)Ljava/lang/Integer;"),
    LONG(Long.class, StackSize.SINGLE, "valueOf", "(J)Ljava/lang/Long;"),
    FLOAT(Float.class, StackSize.ZERO, "valueOf", "(F)Ljava/lang/Float;"),
    DOUBLE(Double.class, StackSize.SINGLE, "valueOf", "(D)Ljava/lang/Double;");

    private final TypeDescription wrapperType;
    private final StackManipulation.Size size;
    private final String boxingMethodName;
    private final String boxingMethodDescriptor;

    private PrimitiveBoxingDelegate(Class<?> wrapperType, StackSize sizeDifference, String boxingMethodName, String boxingMethodDescriptor) {
        this.wrapperType = TypeDescription.ForLoadedType.of(wrapperType);
        this.size = sizeDifference.toDecreasingSize();
        this.boxingMethodName = boxingMethodName;
        this.boxingMethodDescriptor = boxingMethodDescriptor;
    }

    public static PrimitiveBoxingDelegate forPrimitive(TypeDefinition typeDefinition) {
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
        throw new IllegalArgumentException("Not a non-void, primitive type: " + typeDefinition);
    }

    public StackManipulation assignBoxedTo(TypeDescription.Generic target, Assigner chainedAssigner, Assigner.Typing typing) {
        return new BoxingStackManipulation(chainedAssigner.assign(this.wrapperType.asGenericType(), target, typing));
    }

    private class BoxingStackManipulation
    implements StackManipulation {
        private final StackManipulation stackManipulation;

        public BoxingStackManipulation(StackManipulation stackManipulation) {
            this.stackManipulation = stackManipulation;
        }

        public boolean isValid() {
            return this.stackManipulation.isValid();
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitMethodInsn(184, PrimitiveBoxingDelegate.this.wrapperType.getInternalName(), PrimitiveBoxingDelegate.this.boxingMethodName, PrimitiveBoxingDelegate.this.boxingMethodDescriptor, false);
            return PrimitiveBoxingDelegate.this.size.aggregate(this.stackManipulation.apply(methodVisitor, implementationContext));
        }
    }
}

