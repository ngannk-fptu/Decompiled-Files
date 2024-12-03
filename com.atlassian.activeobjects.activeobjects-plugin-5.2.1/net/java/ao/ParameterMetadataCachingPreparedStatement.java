/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.java.ao.ForwardingPreparedStatement;

public class ParameterMetadataCachingPreparedStatement
extends ForwardingPreparedStatement {
    private ParameterMetaData parameterMetaData;

    ParameterMetadataCachingPreparedStatement(PreparedStatement statement) {
        super(statement);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        if (this.parameterMetaData == null) {
            this.parameterMetaData = this.statement.getParameterMetaData();
        }
        return this.parameterMetaData;
    }
}

