/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ProblemMethodBinding
extends MethodBinding {
    private int problemReason;
    public MethodBinding closestMatch;
    public InferenceContext18 inferenceContext;

    public ProblemMethodBinding(char[] selector, TypeBinding[] args, int problemReason) {
        this.selector = selector;
        this.parameters = args == null || args.length == 0 ? Binding.NO_PARAMETERS : args;
        this.problemReason = problemReason;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
    }

    public ProblemMethodBinding(char[] selector, TypeBinding[] args, ReferenceBinding declaringClass, int problemReason) {
        this.selector = selector;
        this.parameters = args == null || args.length == 0 ? Binding.NO_PARAMETERS : args;
        this.declaringClass = declaringClass;
        this.problemReason = problemReason;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
    }

    public ProblemMethodBinding(MethodBinding closestMatch, char[] selector, TypeBinding[] args, int problemReason) {
        this(selector, args, problemReason);
        this.closestMatch = closestMatch;
        if (closestMatch != null && problemReason != 3) {
            this.declaringClass = closestMatch.declaringClass;
            this.returnType = closestMatch.returnType;
            if (problemReason == 23 || problemReason == 25) {
                this.thrownExceptions = closestMatch.thrownExceptions;
                this.typeVariables = closestMatch.typeVariables;
                this.modifiers = closestMatch.modifiers;
                this.tagBits = closestMatch.tagBits;
            }
        }
    }

    @Override
    public MethodBinding computeSubstitutedMethod(MethodBinding method, LookupEnvironment env) {
        return this.closestMatch == null ? this : this.closestMatch.computeSubstitutedMethod(method, env);
    }

    @Override
    public MethodBinding findOriginalInheritedMethod(MethodBinding inheritedMethod) {
        return this.closestMatch == null ? this : this.closestMatch.findOriginalInheritedMethod(inheritedMethod);
    }

    @Override
    public MethodBinding genericMethod() {
        return this.closestMatch == null ? this : this.closestMatch.genericMethod();
    }

    @Override
    public MethodBinding original() {
        return this.closestMatch == null ? this : this.closestMatch.original();
    }

    @Override
    public MethodBinding shallowOriginal() {
        return this.closestMatch == null ? this : this.closestMatch.shallowOriginal();
    }

    @Override
    public MethodBinding tiebreakMethod() {
        return this.closestMatch == null ? this : this.closestMatch.tiebreakMethod();
    }

    @Override
    public boolean hasSubstitutedParameters() {
        if (this.closestMatch != null) {
            return this.closestMatch.hasSubstitutedParameters();
        }
        return false;
    }

    @Override
    public boolean isParameterizedGeneric() {
        return this.closestMatch instanceof ParameterizedGenericMethodBinding;
    }

    @Override
    public final int problemId() {
        return this.problemReason;
    }
}

