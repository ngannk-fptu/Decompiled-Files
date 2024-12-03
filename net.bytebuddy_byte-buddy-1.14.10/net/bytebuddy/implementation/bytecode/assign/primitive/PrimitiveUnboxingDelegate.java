/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.assign.primitive;

import java.lang.reflect.Type;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveWideningDelegate;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum PrimitiveUnboxingDelegate implements StackManipulation
{
    BOOLEAN(Boolean.class, Boolean.TYPE, StackSize.ZERO, "booleanValue", "()Z"),
    BYTE(Byte.class, Byte.TYPE, StackSize.ZERO, "byteValue", "()B"),
    SHORT(Short.class, Short.TYPE, StackSize.ZERO, "shortValue", "()S"),
    CHARACTER(Character.class, Character.TYPE, StackSize.ZERO, "charValue", "()C"),
    INTEGER(Integer.class, Integer.TYPE, StackSize.ZERO, "intValue", "()I"),
    LONG(Long.class, Long.TYPE, StackSize.SINGLE, "longValue", "()J"),
    FLOAT(Float.class, Float.TYPE, StackSize.ZERO, "floatValue", "()F"),
    DOUBLE(Double.class, Double.TYPE, StackSize.SINGLE, "doubleValue", "()D");

    private final TypeDescription wrapperType;
    private final TypeDescription primitiveType;
    private final StackManipulation.Size size;
    private final String unboxingMethodName;
    private final String unboxingMethodDescriptor;

    private PrimitiveUnboxingDelegate(Class<?> wrapperType, Class<?> primitiveType, StackSize sizeDifference, String unboxingMethodName, String unboxingMethodDescriptor) {
        this.size = sizeDifference.toIncreasingSize();
        this.wrapperType = TypeDescription.ForLoadedType.of(wrapperType);
        this.primitiveType = TypeDescription.ForLoadedType.of(primitiveType);
        this.unboxingMethodName = unboxingMethodName;
        this.unboxingMethodDescriptor = unboxingMethodDescriptor;
    }

    public static PrimitiveUnboxingDelegate forPrimitive(TypeDefinition typeDefinition) {
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
        throw new IllegalArgumentException("Expected non-void primitive type instead of " + typeDefinition);
    }

    public static UnboxingResponsible forReferenceType(TypeDefinition typeDefinition) {
        if (typeDefinition.isPrimitive()) {
            throw new IllegalArgumentException("Expected reference type instead of " + typeDefinition);
        }
        if (typeDefinition.represents((Type)((Object)Boolean.class))) {
            return ExplicitlyTypedUnboxingResponsible.BOOLEAN;
        }
        if (typeDefinition.represents((Type)((Object)Byte.class))) {
            return ExplicitlyTypedUnboxingResponsible.BYTE;
        }
        if (typeDefinition.represents((Type)((Object)Short.class))) {
            return ExplicitlyTypedUnboxingResponsible.SHORT;
        }
        if (typeDefinition.represents((Type)((Object)Character.class))) {
            return ExplicitlyTypedUnboxingResponsible.CHARACTER;
        }
        if (typeDefinition.represents((Type)((Object)Integer.class))) {
            return ExplicitlyTypedUnboxingResponsible.INTEGER;
        }
        if (typeDefinition.represents((Type)((Object)Long.class))) {
            return ExplicitlyTypedUnboxingResponsible.LONG;
        }
        if (typeDefinition.represents((Type)((Object)Float.class))) {
            return ExplicitlyTypedUnboxingResponsible.FLOAT;
        }
        if (typeDefinition.represents((Type)((Object)Double.class))) {
            return ExplicitlyTypedUnboxingResponsible.DOUBLE;
        }
        return new ImplicitlyTypedUnboxingResponsible(typeDefinition.asGenericType());
    }

    protected TypeDescription.Generic getWrapperType() {
        return this.wrapperType.asGenericType();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitMethodInsn(182, this.wrapperType.asErasure().getInternalName(), this.unboxingMethodName, this.unboxingMethodDescriptor, false);
        return this.size;
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ImplicitlyTypedUnboxingResponsible
    implements UnboxingResponsible {
        private final TypeDescription.Generic originalType;

        protected ImplicitlyTypedUnboxingResponsible(TypeDescription.Generic originalType) {
            this.originalType = originalType;
        }

        public StackManipulation assignUnboxedTo(TypeDescription.Generic target, Assigner assigner, Assigner.Typing typing) {
            PrimitiveUnboxingDelegate primitiveUnboxingDelegate = PrimitiveUnboxingDelegate.forPrimitive(target);
            return new StackManipulation.Compound(assigner.assign(this.originalType, primitiveUnboxingDelegate.getWrapperType(), typing), primitiveUnboxingDelegate);
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
            return this.originalType.equals(((ImplicitlyTypedUnboxingResponsible)object).originalType);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.originalType.hashCode();
        }
    }

    public static interface UnboxingResponsible {
        public StackManipulation assignUnboxedTo(TypeDescription.Generic var1, Assigner var2, Assigner.Typing var3);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum ExplicitlyTypedUnboxingResponsible implements UnboxingResponsible
    {
        BOOLEAN(BOOLEAN),
        BYTE(BYTE),
        SHORT(SHORT),
        CHARACTER(CHARACTER),
        INTEGER(INTEGER),
        LONG(LONG),
        FLOAT(FLOAT),
        DOUBLE(DOUBLE);

        private final PrimitiveUnboxingDelegate primitiveUnboxingDelegate;

        private ExplicitlyTypedUnboxingResponsible(PrimitiveUnboxingDelegate primitiveUnboxingDelegate) {
            this.primitiveUnboxingDelegate = primitiveUnboxingDelegate;
        }

        @Override
        public StackManipulation assignUnboxedTo(TypeDescription.Generic targetType, Assigner assigner, Assigner.Typing typing) {
            return new StackManipulation.Compound(this.primitiveUnboxingDelegate, PrimitiveWideningDelegate.forPrimitive(this.primitiveUnboxingDelegate.primitiveType).widenTo(targetType));
        }
    }
}

