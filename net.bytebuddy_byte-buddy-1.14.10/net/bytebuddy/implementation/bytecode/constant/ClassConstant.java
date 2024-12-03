/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.constant;

import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ClassConstant implements StackManipulation
{
    VOID(Void.class),
    BOOLEAN(Boolean.class),
    BYTE(Byte.class),
    SHORT(Short.class),
    CHARACTER(Character.class),
    INTEGER(Integer.class),
    LONG(Long.class),
    FLOAT(Float.class),
    DOUBLE(Double.class);

    private static final StackManipulation.Size SIZE;
    private static final String PRIMITIVE_TYPE_FIELD = "TYPE";
    private static final String CLASS_TYPE_INTERNAL_NAME = "Ljava/lang/Class;";
    private final String fieldOwnerInternalName;

    private ClassConstant(Class<?> type) {
        this.fieldOwnerInternalName = Type.getInternalName(type);
    }

    public static StackManipulation of(TypeDescription typeDescription) {
        if (!typeDescription.isPrimitive()) {
            return new ForReferenceType(typeDescription);
        }
        if (typeDescription.represents(Boolean.TYPE)) {
            return BOOLEAN;
        }
        if (typeDescription.represents(Byte.TYPE)) {
            return BYTE;
        }
        if (typeDescription.represents(Short.TYPE)) {
            return SHORT;
        }
        if (typeDescription.represents(Character.TYPE)) {
            return CHARACTER;
        }
        if (typeDescription.represents(Integer.TYPE)) {
            return INTEGER;
        }
        if (typeDescription.represents(Long.TYPE)) {
            return LONG;
        }
        if (typeDescription.represents(Float.TYPE)) {
            return FLOAT;
        }
        if (typeDescription.represents(Double.TYPE)) {
            return DOUBLE;
        }
        return VOID;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitFieldInsn(178, this.fieldOwnerInternalName, PRIMITIVE_TYPE_FIELD, CLASS_TYPE_INTERNAL_NAME);
        return SIZE;
    }

    static {
        SIZE = StackSize.SINGLE.toIncreasingSize();
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ForReferenceType
    implements StackManipulation {
        private final TypeDescription typeDescription;

        protected ForReferenceType(TypeDescription typeDescription) {
            this.typeDescription = typeDescription;
        }

        public boolean isValid() {
            return true;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            if (implementationContext.getClassFileVersion().isAtLeast(ClassFileVersion.JAVA_V5) && this.typeDescription.isVisibleTo(implementationContext.getInstrumentedType())) {
                methodVisitor.visitLdcInsn(Type.getType(this.typeDescription.getDescriptor()));
            } else {
                methodVisitor.visitLdcInsn(this.typeDescription.getName());
                methodVisitor.visitMethodInsn(184, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            }
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
            return this.typeDescription.equals(((ForReferenceType)object).typeDescription);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
        }
    }
}

