/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface ConstantValue {
    public TypeDescription getTypeDescription();

    public StackManipulation toStackManipulation();

    @HashCodeAndEqualsPlugin.Enhance
    public static class Simple
    implements ConstantValue {
        private final StackManipulation stackManipulation;
        private final TypeDescription typeDescription;

        protected Simple(StackManipulation stackManipulation, TypeDescription typeDescription) {
            this.stackManipulation = stackManipulation;
            this.typeDescription = typeDescription;
        }

        public static ConstantValue wrap(Object value) {
            ConstantValue constant = Simple.wrapOrNull(value);
            if (constant == null) {
                throw new IllegalArgumentException("Not a constant value: " + value);
            }
            return constant;
        }

        @MaybeNull
        public static ConstantValue wrapOrNull(Object value) {
            if (value instanceof ConstantValue) {
                return (ConstantValue)value;
            }
            if (value instanceof TypeDescription) {
                return ((TypeDescription)value).isPrimitive() ? new Simple(ClassConstant.of((TypeDescription)value), TypeDescription.ForLoadedType.of(Class.class)) : JavaConstant.Simple.of((TypeDescription)value);
            }
            if (value instanceof EnumerationDescription) {
                return new Simple(FieldAccess.forEnumeration((EnumerationDescription)value), ((EnumerationDescription)value).getEnumerationType());
            }
            if (value instanceof Boolean) {
                return new Simple(IntegerConstant.forValue((Boolean)value), TypeDescription.ForLoadedType.of(Boolean.TYPE));
            }
            if (value instanceof Byte) {
                return new Simple(IntegerConstant.forValue(((Byte)value).byteValue()), TypeDescription.ForLoadedType.of(Byte.TYPE));
            }
            if (value instanceof Short) {
                return new Simple(IntegerConstant.forValue(((Short)value).shortValue()), TypeDescription.ForLoadedType.of(Short.TYPE));
            }
            if (value instanceof Character) {
                return new Simple(IntegerConstant.forValue(((Character)value).charValue()), TypeDescription.ForLoadedType.of(Character.TYPE));
            }
            if (value instanceof Class) {
                return ((Class)value).isPrimitive() ? new Simple(ClassConstant.of(TypeDescription.ForLoadedType.of((Class)value)), TypeDescription.ForLoadedType.of(Class.class)) : JavaConstant.Simple.of(TypeDescription.ForLoadedType.of((Class)value));
            }
            if (value instanceof Enum) {
                return new Simple(FieldAccess.forEnumeration(new EnumerationDescription.ForLoadedEnumeration((Enum)value)), TypeDescription.ForLoadedType.of(((Enum)value).getDeclaringClass()));
            }
            return JavaConstant.Simple.ofLoadedOrNull(value);
        }

        public TypeDescription getTypeDescription() {
            return this.typeDescription;
        }

        public StackManipulation toStackManipulation() {
            return this.stackManipulation;
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
            if (!this.stackManipulation.equals(((Simple)object).stackManipulation)) {
                return false;
            }
            return this.typeDescription.equals(((Simple)object).typeDescription);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.typeDescription.hashCode();
        }
    }
}

