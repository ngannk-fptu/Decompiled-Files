/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.param.ParameterBinder;

public class NamedParamBinder
implements ParameterBinder {
    private final String name;

    public NamedParamBinder(String name) {
        this.name = name;
    }

    @Override
    public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position) throws SQLException {
        TypedValue typedValue = qp.getNamedParameters().get(this.name);
        typedValue.getType().nullSafeSet(statement, typedValue.getValue(), position, session);
        return typedValue.getType().getColumnSpan(session.getFactory());
    }
}

