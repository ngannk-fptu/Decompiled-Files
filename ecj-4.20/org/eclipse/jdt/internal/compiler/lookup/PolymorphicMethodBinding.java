/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class PolymorphicMethodBinding
extends MethodBinding {
    protected MethodBinding polymorphicMethod;

    public PolymorphicMethodBinding(MethodBinding polymorphicMethod, TypeBinding[] parameterTypes) {
        super(polymorphicMethod.modifiers, polymorphicMethod.selector, polymorphicMethod.returnType, parameterTypes, polymorphicMethod.thrownExceptions, polymorphicMethod.declaringClass);
        this.polymorphicMethod = polymorphicMethod;
        this.tagBits = polymorphicMethod.tagBits;
    }

    public PolymorphicMethodBinding(MethodBinding polymorphicMethod, TypeBinding returnType, TypeBinding[] parameterTypes) {
        super(polymorphicMethod.modifiers, polymorphicMethod.selector, returnType, parameterTypes, polymorphicMethod.thrownExceptions, polymorphicMethod.declaringClass);
        this.polymorphicMethod = polymorphicMethod;
        this.tagBits = polymorphicMethod.tagBits;
    }

    @Override
    public MethodBinding original() {
        return this.polymorphicMethod;
    }

    @Override
    public boolean isPolymorphic() {
        return true;
    }

    public boolean matches(TypeBinding[] matchingParameters, TypeBinding matchingReturnType) {
        int matchingParametersLength;
        int cachedParametersLength = this.parameters == null ? 0 : this.parameters.length;
        int n = matchingParametersLength = matchingParameters == null ? 0 : matchingParameters.length;
        if (matchingParametersLength != cachedParametersLength) {
            return false;
        }
        int j = 0;
        while (j < cachedParametersLength) {
            if (TypeBinding.notEquals(this.parameters[j], matchingParameters[j])) {
                return false;
            }
            ++j;
        }
        TypeBinding cachedReturnType = this.returnType;
        if (matchingReturnType == null) {
            if (cachedReturnType != null) {
                return false;
            }
        } else {
            if (cachedReturnType == null) {
                return false;
            }
            if (TypeBinding.notEquals(matchingReturnType, cachedReturnType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isVarargs() {
        return false;
    }
}

