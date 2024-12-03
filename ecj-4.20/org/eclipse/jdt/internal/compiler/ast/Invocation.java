/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public interface Invocation
extends InvocationSite {
    public Expression[] arguments();

    public MethodBinding binding();

    public void registerInferenceContext(ParameterizedGenericMethodBinding var1, InferenceContext18 var2);

    public InferenceContext18 getInferenceContext(ParameterizedMethodBinding var1);

    public void cleanUpInferenceContexts();

    public void registerResult(TypeBinding var1, MethodBinding var2);
}

