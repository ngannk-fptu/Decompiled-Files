/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.constant;

import java.lang.reflect.Field;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

public class FieldConstant
extends StackManipulation.AbstractBase {
    private final FieldDescription.InDefinedShape fieldDescription;

    public FieldConstant(FieldDescription.InDefinedShape fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public StackManipulation cached() {
        return new Cached(this);
    }

    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        try {
            return new StackManipulation.Compound(ClassConstant.of(this.fieldDescription.getDeclaringType()), new TextConstant(this.fieldDescription.getInternalName()), MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(Class.class.getMethod("getDeclaredField", String.class)))).apply(methodVisitor, implementationContext);
        }
        catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Cannot locate Class::getDeclaredField", exception);
        }
    }

    public int hashCode() {
        return this.fieldDescription.hashCode();
    }

    public boolean equals(@MaybeNull Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        FieldConstant fieldConstant = (FieldConstant)other;
        return this.fieldDescription.equals(fieldConstant.fieldDescription);
    }

    protected static class Cached
    implements StackManipulation {
        private final StackManipulation fieldConstant;

        public Cached(StackManipulation fieldConstant) {
            this.fieldConstant = fieldConstant;
        }

        public boolean isValid() {
            return this.fieldConstant.isValid();
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return FieldAccess.forField(implementationContext.cache(this.fieldConstant, TypeDescription.ForLoadedType.of(Field.class))).read().apply(methodVisitor, implementationContext);
        }

        public int hashCode() {
            return this.fieldConstant.hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            Cached cached = (Cached)other;
            return this.fieldConstant.equals(cached.fieldConstant);
        }
    }
}

