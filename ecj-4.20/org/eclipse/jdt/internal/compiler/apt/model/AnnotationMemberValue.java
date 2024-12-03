/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationValueImpl;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class AnnotationMemberValue
extends AnnotationValueImpl {
    private final MethodBinding _methodBinding;

    public AnnotationMemberValue(BaseProcessingEnvImpl env, Object value, MethodBinding methodBinding) {
        super(env, value, methodBinding.returnType);
        this._methodBinding = methodBinding;
    }

    public MethodBinding getMethodBinding() {
        return this._methodBinding;
    }
}

