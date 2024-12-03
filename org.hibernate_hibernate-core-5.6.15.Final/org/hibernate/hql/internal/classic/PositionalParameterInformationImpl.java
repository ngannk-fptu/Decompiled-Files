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
import org.hibernate.hql.spi.PositionalParameterInformation;

public class PositionalParameterInformationImpl
extends AbstractParameterInformation
implements PositionalParameterInformation {
    private final int label;

    public PositionalParameterInformationImpl(int label) {
        this.label = label;
    }

    @Override
    public int getLabel() {
        return this.label;
    }

    @Override
    public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position) throws SQLException {
        TypedValue typedValue = qp.getNamedParameters().get(Integer.toString(this.label));
        typedValue.getType().nullSafeSet(statement, typedValue.getValue(), position, session);
        return typedValue.getType().getColumnSpan(session.getFactory());
    }
}

