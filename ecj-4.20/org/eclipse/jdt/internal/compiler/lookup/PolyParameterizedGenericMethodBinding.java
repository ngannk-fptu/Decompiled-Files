/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;

public class PolyParameterizedGenericMethodBinding
extends ParameterizedGenericMethodBinding {
    private ParameterizedGenericMethodBinding wrappedBinding;

    public PolyParameterizedGenericMethodBinding(ParameterizedGenericMethodBinding applicableMethod) {
        super(applicableMethod.originalMethod, applicableMethod.typeArguments, applicableMethod.environment, applicableMethod.inferredWithUncheckedConversion, false, applicableMethod.targetType);
        this.wrappedBinding = applicableMethod;
    }

    public boolean equals(Object other) {
        if (other instanceof PolyParameterizedGenericMethodBinding) {
            PolyParameterizedGenericMethodBinding ppgmb = (PolyParameterizedGenericMethodBinding)other;
            return this.wrappedBinding.equals(ppgmb.wrappedBinding);
        }
        return false;
    }

    public int hashCode() {
        return this.wrappedBinding.hashCode();
    }
}

