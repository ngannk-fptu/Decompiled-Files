/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.assign.primitive;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public class VoidAwareAssigner
implements Assigner {
    private final Assigner chained;

    public VoidAwareAssigner(Assigner chained) {
        this.chained = chained;
    }

    public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Assigner.Typing typing) {
        if (source.represents(Void.TYPE) && target.represents(Void.TYPE)) {
            return StackManipulation.Trivial.INSTANCE;
        }
        if (source.represents(Void.TYPE)) {
            return typing.isDynamic() ? DefaultValue.of(target) : StackManipulation.Illegal.INSTANCE;
        }
        if (target.represents(Void.TYPE)) {
            return Removal.of(source);
        }
        return this.chained.assign(source, target, typing);
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
        return this.chained.equals(((VoidAwareAssigner)object).chained);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.chained.hashCode();
    }
}

