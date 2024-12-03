/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.assign.reference;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ReferenceTypeAwareAssigner implements Assigner
{
    INSTANCE;


    @Override
    public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Assigner.Typing typing) {
        if (source.isPrimitive() || target.isPrimitive()) {
            return (StackManipulation)((Object)(source.equals(target) ? StackManipulation.Trivial.INSTANCE : StackManipulation.Illegal.INSTANCE));
        }
        if (source.asErasure().isAssignableTo(target.asErasure())) {
            return StackManipulation.Trivial.INSTANCE;
        }
        if (typing.isDynamic()) {
            return TypeCasting.to(target);
        }
        return StackManipulation.Illegal.INSTANCE;
    }
}

