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
import org.hibernate.type.VersionType;

public class VersionTypeSeedParameterSpecification
implements ParameterSpecification {
    private final VersionType type;

    public VersionTypeSeedParameterSpecification(VersionType type) {
        this.type = type;
    }

    @Override
    public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position) throws SQLException {
        this.type.nullSafeSet(statement, this.type.seed(session), position, session);
        return 1;
    }

    @Override
    public Type getExpectedType() {
        return this.type;
    }

    @Override
    public void setExpectedType(Type expectedType) {
    }

    @Override
    public String renderDisplayInfo() {
        return "version-seed, type=" + this.type;
    }
}

