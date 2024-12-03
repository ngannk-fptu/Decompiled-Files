/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.member;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum FieldAccess {
    STATIC(179, 178, StackSize.ZERO),
    INSTANCE(181, 180, StackSize.SINGLE);

    private final int putterOpcode;
    private final int getterOpcode;
    private final int targetSizeChange;

    private FieldAccess(int putterOpcode, int getterOpcode, StackSize targetSizeChange) {
        this.putterOpcode = putterOpcode;
        this.getterOpcode = getterOpcode;
        this.targetSizeChange = targetSizeChange.getSize();
    }

    public static StackManipulation forEnumeration(EnumerationDescription enumerationDescription) {
        StackManipulation stackManipulation;
        FieldList fieldList = (FieldList)enumerationDescription.getEnumerationType().getDeclaredFields().filter(ElementMatchers.named(enumerationDescription.getValue()));
        if (!(fieldList.size() == 1 && ((FieldDescription.InDefinedShape)fieldList.getOnly()).isStatic() && ((FieldDescription.InDefinedShape)fieldList.getOnly()).isPublic() && ((FieldDescription.InDefinedShape)fieldList.getOnly()).isEnum())) {
            stackManipulation = StackManipulation.Illegal.INSTANCE;
        } else {
            FieldAccess fieldAccess = STATIC;
            ((Object)((Object)fieldAccess)).getClass();
            stackManipulation = fieldAccess.new AccessDispatcher((FieldDescription.InDefinedShape)fieldList.getOnly()).read();
        }
        return stackManipulation;
    }

    public static Defined forField(FieldDescription.InDefinedShape fieldDescription) {
        AccessDispatcher accessDispatcher;
        if (fieldDescription.isStatic()) {
            FieldAccess fieldAccess = STATIC;
            ((Object)((Object)fieldAccess)).getClass();
            accessDispatcher = fieldAccess.new AccessDispatcher(fieldDescription);
        } else {
            FieldAccess fieldAccess = INSTANCE;
            ((Object)((Object)fieldAccess)).getClass();
            accessDispatcher = fieldAccess.new AccessDispatcher(fieldDescription);
        }
        return accessDispatcher;
    }

    public static Defined forField(FieldDescription fieldDescription) {
        FieldDescription.InDefinedShape declaredField = (FieldDescription.InDefinedShape)fieldDescription.asDefined();
        return fieldDescription.getType().asErasure().equals(declaredField.getType().asErasure()) ? FieldAccess.forField(declaredField) : OfGenericField.of(fieldDescription, FieldAccess.forField(declaredField));
    }

    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class AccessDispatcher
    implements Defined {
        private final FieldDescription.InDefinedShape fieldDescription;

        protected AccessDispatcher(FieldDescription.InDefinedShape fieldDescription) {
            this.fieldDescription = fieldDescription;
        }

        public StackManipulation read() {
            return new FieldGetInstruction();
        }

        public StackManipulation write() {
            return new FieldPutInstruction();
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
            if (!FieldAccess.this.equals((Object)((AccessDispatcher)object).FieldAccess.this)) {
                return false;
            }
            return this.fieldDescription.equals(((AccessDispatcher)object).fieldDescription);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + FieldAccess.this.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class FieldPutInstruction
        extends AbstractFieldInstruction {
            protected FieldPutInstruction() {
            }

            protected int getOpcode() {
                return FieldAccess.this.putterOpcode;
            }

            protected StackManipulation.Size resolveSize(StackSize fieldSize) {
                return new StackManipulation.Size(-1 * (fieldSize.getSize() + FieldAccess.this.targetSizeChange), 0);
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
                return AccessDispatcher.this.equals(((FieldPutInstruction)object).AccessDispatcher.this);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + AccessDispatcher.this.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class FieldGetInstruction
        extends AbstractFieldInstruction {
            protected FieldGetInstruction() {
            }

            protected int getOpcode() {
                return FieldAccess.this.getterOpcode;
            }

            protected StackManipulation.Size resolveSize(StackSize fieldSize) {
                int sizeChange = fieldSize.getSize() - FieldAccess.this.targetSizeChange;
                return new StackManipulation.Size(sizeChange, sizeChange);
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
                return AccessDispatcher.this.equals(((FieldGetInstruction)object).AccessDispatcher.this);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + AccessDispatcher.this.hashCode();
            }
        }

        private abstract class AbstractFieldInstruction
        extends StackManipulation.AbstractBase {
            private AbstractFieldInstruction() {
            }

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitFieldInsn(this.getOpcode(), AccessDispatcher.this.fieldDescription.getDeclaringType().getInternalName(), AccessDispatcher.this.fieldDescription.getInternalName(), AccessDispatcher.this.fieldDescription.getDescriptor());
                return this.resolveSize(AccessDispatcher.this.fieldDescription.getType().getStackSize());
            }

            protected abstract int getOpcode();

            protected abstract StackManipulation.Size resolveSize(StackSize var1);
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class OfGenericField
    implements Defined {
        private final TypeDefinition targetType;
        private final Defined defined;

        protected OfGenericField(TypeDefinition targetType, Defined defined) {
            this.targetType = targetType;
            this.defined = defined;
        }

        protected static Defined of(FieldDescription fieldDescription, Defined fieldAccess) {
            return new OfGenericField(fieldDescription.getType(), fieldAccess);
        }

        public StackManipulation read() {
            return new StackManipulation.Compound(this.defined.read(), TypeCasting.to(this.targetType));
        }

        public StackManipulation write() {
            return this.defined.write();
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
            if (!this.targetType.equals(((OfGenericField)object).targetType)) {
                return false;
            }
            return this.defined.equals(((OfGenericField)object).defined);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31 + this.defined.hashCode();
        }
    }

    public static interface Defined {
        public StackManipulation read();

        public StackManipulation write();
    }
}

