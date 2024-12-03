/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ParameterLengthResolver implements MethodDelegationBinder.AmbiguityResolver
{
    INSTANCE;


    @Override
    public MethodDelegationBinder.AmbiguityResolver.Resolution resolve(MethodDescription source, MethodDelegationBinder.MethodBinding left, MethodDelegationBinder.MethodBinding right) {
        int rightLength;
        int leftLength = left.getTarget().getParameters().size();
        if (leftLength == (rightLength = right.getTarget().getParameters().size())) {
            return MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
        }
        if (leftLength < rightLength) {
            return MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;
        }
        return MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
    }
}

