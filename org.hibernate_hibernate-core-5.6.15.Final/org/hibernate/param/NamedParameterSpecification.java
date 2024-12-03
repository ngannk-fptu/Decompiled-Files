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

public class NamedParameterSpecification
extends AbstractExplicitParameterSpecification {
    private final String name;

    public NamedParameterSpecification(int sourceLine, int sourceColumn, String name) {
        super(sourceLine, sourceColumn);
        this.name = name;
    }

    @Override
    public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position) throws SQLException {
        TypedValue typedValue = qp.getNamedParameters().get(this.name);
        typedValue.getType().nullSafeSet(statement, typedValue.getValue(), position, session);
        return typedValue.getType().getColumnSpan(session.getFactory());
    }

    @Override
    public String renderDisplayInfo() {
        return "name=" + this.name + ", expectedType=" + this.getExpectedType();
    }

    public String getName() {
        return this.name;
    }
}

