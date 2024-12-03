/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class NullTypeBinding
extends BaseTypeBinding {
    NullTypeBinding() {
        super(12, TypeConstants.NULL, new char[]{'N'});
    }

    @Override
    public TypeBinding clone(TypeBinding enclosingType) {
        return this;
    }

    @Override
    public void setTypeAnnotations(AnnotationBinding[] annotations, boolean evalNullAnnotations) {
    }

    @Override
    public TypeBinding unannotated() {
        return this;
    }
}

