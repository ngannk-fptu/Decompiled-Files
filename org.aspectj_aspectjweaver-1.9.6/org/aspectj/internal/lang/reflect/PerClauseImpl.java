/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;

public class PerClauseImpl
implements PerClause {
    private final PerClauseKind kind;

    protected PerClauseImpl(PerClauseKind kind) {
        this.kind = kind;
    }

    @Override
    public PerClauseKind getKind() {
        return this.kind;
    }

    public String toString() {
        return "issingleton()";
    }
}

