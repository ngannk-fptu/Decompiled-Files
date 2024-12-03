/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.param;

import org.hibernate.param.ExplicitParameterSpecification;
import org.hibernate.type.Type;

public abstract class AbstractExplicitParameterSpecification
implements ExplicitParameterSpecification {
    private final int sourceLine;
    private final int sourceColumn;
    private Type expectedType;

    protected AbstractExplicitParameterSpecification(int sourceLine, int sourceColumn) {
        this.sourceLine = sourceLine;
        this.sourceColumn = sourceColumn;
    }

    @Override
    public int getSourceLine() {
        return this.sourceLine;
    }

    @Override
    public int getSourceColumn() {
        return this.sourceColumn;
    }

    @Override
    public Type getExpectedType() {
        return this.expectedType;
    }

    @Override
    public void setExpectedType(Type expectedType) {
        this.expectedType = expectedType;
    }
}

