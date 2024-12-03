/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.assign.primitive;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveBoxingDelegate;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveUnboxingDelegate;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveWideningDelegate;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public class PrimitiveTypeAwareAssigner
implements Assigner {
    private final Assigner referenceTypeAwareAssigner;

    public PrimitiveTypeAwareAssigner(Assigner referenceTypeAwareAssigner) {
        this.referenceTypeAwareAssigner = referenceTypeAwareAssigner;
    }

    public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Assigner.Typing typing) {
        if (source.isPrimitive() && target.isPrimitive()) {
            return PrimitiveWideningDelegate.forPrimitive(source).widenTo(target);
        }
        if (source.isPrimitive()) {
            return PrimitiveBoxingDelegate.forPrimitive(source).assignBoxedTo(target, this.referenceTypeAwareAssigner, typing);
        }
        if (target.isPrimitive()) {
            return PrimitiveUnboxingDelegate.forReferenceType(source).assignUnboxedTo(target, this.referenceTypeAwareAssigner, typing);
        }
        return this.referenceTypeAwareAssigner.assign(source, target, typing);
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
        return this.referenceTypeAwareAssigner.equals(((PrimitiveTypeAwareAssigner)object).referenceTypeAwareAssigner);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.referenceTypeAwareAssigner.hashCode();
    }
}

