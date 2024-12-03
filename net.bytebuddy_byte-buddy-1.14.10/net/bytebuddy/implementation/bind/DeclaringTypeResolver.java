/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum DeclaringTypeResolver implements MethodDelegationBinder.AmbiguityResolver
{
    INSTANCE;


    @Override
    public MethodDelegationBinder.AmbiguityResolver.Resolution resolve(MethodDescription source, MethodDelegationBinder.MethodBinding left, MethodDelegationBinder.MethodBinding right) {
        TypeDescription rightType;
        TypeDescription leftType = left.getTarget().getDeclaringType().asErasure();
        if (leftType.equals(rightType = right.getTarget().getDeclaringType().asErasure())) {
            return MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
        }
        if (leftType.isAssignableFrom(rightType)) {
            return MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;
        }
        if (leftType.isAssignableTo(rightType)) {
            return MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
        }
        return MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
    }
}

