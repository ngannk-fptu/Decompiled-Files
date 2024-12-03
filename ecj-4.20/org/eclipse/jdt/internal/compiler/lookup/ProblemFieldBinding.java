/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class ProblemFieldBinding
extends FieldBinding {
    private int problemId;
    public FieldBinding closestMatch;

    public ProblemFieldBinding(ReferenceBinding declaringClass, char[] name, int problemId) {
        this(null, declaringClass, name, problemId);
    }

    public ProblemFieldBinding(FieldBinding closestMatch, ReferenceBinding declaringClass, char[] name, int problemId) {
        this.closestMatch = closestMatch;
        this.declaringClass = declaringClass;
        this.name = name;
        this.problemId = problemId;
    }

    @Override
    public final int problemId() {
        return this.problemId;
    }
}

