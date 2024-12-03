/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.type.Type;

public class CollectionFilterKeyParameterSpecification
implements ParameterSpecification {
    public static final String PARAM_KEY = "{collection_key}";
    private final String collectionRole;
    private final Type keyType;

    public CollectionFilterKeyParameterSpecification(String collectionRole, Type keyType) {
        this.collectionRole = collectionRole;
        this.keyType = keyType;
    }

    @Override
    public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position) throws SQLException {
        Object value = qp.getNamedParameters().get(PARAM_KEY).getValue();
        this.keyType.nullSafeSet(statement, value, position, session);
        return this.keyType.getColumnSpan(session.getFactory());
    }

    @Override
    public Type getExpectedType() {
        return this.keyType;
    }

    @Override
    public void setExpectedType(Type expectedType) {
    }

    @Override
    public String renderDisplayInfo() {
        return "collection-filter-key=" + this.collectionRole;
    }
}

