/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.internal.lang.reflect.PerClauseImpl;
import org.aspectj.internal.lang.reflect.TypePatternImpl;
import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.TypePattern;
import org.aspectj.lang.reflect.TypePatternBasedPerClause;

public class TypePatternBasedPerClauseImpl
extends PerClauseImpl
implements TypePatternBasedPerClause {
    private TypePattern typePattern;

    public TypePatternBasedPerClauseImpl(PerClauseKind kind, String pattern) {
        super(kind);
        this.typePattern = new TypePatternImpl(pattern);
    }

    @Override
    public TypePattern getTypePattern() {
        return this.typePattern;
    }

    @Override
    public String toString() {
        return "pertypewithin(" + this.typePattern.asString() + ")";
    }
}

