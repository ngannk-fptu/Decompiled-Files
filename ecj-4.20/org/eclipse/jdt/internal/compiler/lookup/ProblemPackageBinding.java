/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;

public class ProblemPackageBinding
extends PlainPackageBinding {
    private int problemId;

    ProblemPackageBinding(char[][] compoundName, int problemId, LookupEnvironment environment) {
        super(compoundName, environment);
        this.problemId = problemId;
    }

    ProblemPackageBinding(char[] name, int problemId, LookupEnvironment environment) {
        this(new char[][]{name}, problemId, environment);
    }

    @Override
    public final int problemId() {
        return this.problemId;
    }
}

