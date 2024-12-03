/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class SyntheticFieldBinding
extends FieldBinding {
    public int index;

    public SyntheticFieldBinding(char[] name, TypeBinding type, int modifiers, ReferenceBinding declaringClass, Constant constant, int index) {
        super(name, type, modifiers, declaringClass, constant);
        this.index = index;
        this.tagBits |= 0x600000000L;
    }
}

