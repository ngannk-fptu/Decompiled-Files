/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;

public class ParameterizedFieldBinding
extends FieldBinding {
    public FieldBinding originalField;

    public ParameterizedFieldBinding(ParameterizedTypeBinding parameterizedDeclaringClass, FieldBinding originalField) {
        super(originalField.name, (originalField.modifiers & 0x4000) != 0 ? parameterizedDeclaringClass : ((originalField.modifiers & 8) != 0 ? originalField.type : Scope.substitute((Substitution)parameterizedDeclaringClass, originalField.type)), originalField.modifiers, parameterizedDeclaringClass, null);
        this.originalField = originalField;
        this.tagBits = originalField.tagBits;
        this.id = originalField.id;
    }

    @Override
    public Constant constant() {
        return this.originalField.constant();
    }

    @Override
    public FieldBinding original() {
        return this.originalField.original();
    }

    @Override
    public void setConstant(Constant constant) {
        this.originalField.setConstant(constant);
    }
}

