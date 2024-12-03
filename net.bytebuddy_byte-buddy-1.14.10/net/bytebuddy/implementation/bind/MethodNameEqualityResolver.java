/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MethodNameEqualityResolver implements MethodDelegationBinder.AmbiguityResolver
{
    INSTANCE;


    @Override
    public MethodDelegationBinder.AmbiguityResolver.Resolution resolve(MethodDescription source, MethodDelegationBinder.MethodBinding left, MethodDelegationBinder.MethodBinding right) {
        boolean rightEquals;
        boolean leftEquals = left.getTarget().getName().equals(source.getName());
        if (leftEquals ^ (rightEquals = right.getTarget().getName().equals(source.getName()))) {
            return leftEquals ? MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT : MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;
        }
        return MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
    }
}

