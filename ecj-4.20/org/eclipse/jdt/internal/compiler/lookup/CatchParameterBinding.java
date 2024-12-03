/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class CatchParameterBinding
extends LocalVariableBinding {
    TypeBinding[] preciseTypes = Binding.NO_EXCEPTIONS;

    public CatchParameterBinding(LocalDeclaration declaration, TypeBinding type, int modifiers, boolean isArgument) {
        super(declaration, type, modifiers, isArgument);
    }

    public TypeBinding[] getPreciseTypes() {
        return this.preciseTypes;
    }

    public void setPreciseType(TypeBinding raisedException) {
        int length = this.preciseTypes.length;
        int i = 0;
        while (i < length) {
            if (TypeBinding.equalsEquals(this.preciseTypes[i], raisedException)) {
                return;
            }
            ++i;
        }
        this.preciseTypes = new TypeBinding[length + 1];
        System.arraycopy(this.preciseTypes, 0, this.preciseTypes, 0, length);
        this.preciseTypes[length] = raisedException;
    }

    @Override
    public boolean isCatchParameter() {
        return true;
    }
}

