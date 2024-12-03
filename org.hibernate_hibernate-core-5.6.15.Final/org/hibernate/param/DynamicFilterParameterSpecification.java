/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.type.Type;

public class DynamicFilterParameterSpecification
implements ParameterSpecification {
    private final String filterName;
    private final String parameterName;
    private final Type definedParameterType;

    public DynamicFilterParameterSpecification(String filterName, String parameterName, Type definedParameterType) {
        this.filterName = filterName;
        this.parameterName = parameterName;
        this.definedParameterType = definedParameterType;
    }

    @Override
    public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int start) throws SQLException {
        int columnSpan = this.definedParameterType.getColumnSpan(session.getFactory());
        String fullParamName = this.filterName + '.' + this.parameterName;
        Object value = session.getLoadQueryInfluencers().getFilterParameterValue(fullParamName);
        Type type = session.getLoadQueryInfluencers().getFilterParameterType(fullParamName);
        if (Collection.class.isInstance(value)) {
            int positions = 0;
            for (Object next : (Collection)value) {
                qp.bindDynamicParameter(type, next);
                this.definedParameterType.nullSafeSet(statement, next, start + positions, session);
                positions += columnSpan;
            }
            return positions;
        }
        qp.bindDynamicParameter(type, value);
        this.definedParameterType.nullSafeSet(statement, value, start, session);
        return columnSpan;
    }

    @Override
    public Type getExpectedType() {
        return this.definedParameterType;
    }

    @Override
    public void setExpectedType(Type expectedType) {
    }

    @Override
    public String renderDisplayInfo() {
        return "dynamic-filter={filterName=" + this.filterName + ",paramName=" + this.parameterName + "}";
    }
}

