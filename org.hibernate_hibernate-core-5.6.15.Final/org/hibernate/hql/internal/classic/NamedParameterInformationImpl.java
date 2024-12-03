/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.hql.internal.classic.AbstractParameterInformation;
import org.hibernate.hql.spi.NamedParameterInformation;

public class NamedParameterInformationImpl
extends AbstractParameterInformation
implements NamedParameterInformation {
    private final String name;

    NamedParameterInformationImpl(String name) {
        this.name = name;
    }

    @Override
    public String getSourceName() {
        return this.name;
    }

    @Override
    public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position) throws SQLException {
        TypedValue typedValue = qp.getNamedParameters().get(this.name);
        typedValue.getType().nullSafeSet(statement, typedValue.getValue(), position, session);
        return typedValue.getType().getColumnSpan(session.getFactory());
    }
}

