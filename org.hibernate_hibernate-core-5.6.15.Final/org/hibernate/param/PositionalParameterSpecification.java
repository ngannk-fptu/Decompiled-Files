/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.param.AbstractExplicitParameterSpecification;

public class PositionalParameterSpecification
extends AbstractExplicitParameterSpecification {
    private final int label;
    private final int bindingPosition;

    public PositionalParameterSpecification(int sourceLine, int sourceColumn, int label, int bindingPosition) {
        super(sourceLine, sourceColumn);
        this.label = label;
        this.bindingPosition = bindingPosition;
    }

    @Override
    public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position) throws SQLException {
        TypedValue typedValue = qp.getNamedParameters().get(Integer.toString(this.label));
        typedValue.getType().nullSafeSet(statement, typedValue.getValue(), position, session);
        return typedValue.getType().getColumnSpan(session.getFactory());
    }

    @Override
    public String renderDisplayInfo() {
        return "label=" + this.label + ", expectedType=" + this.getExpectedType();
    }

    public int getLabel() {
        return this.label;
    }
}

