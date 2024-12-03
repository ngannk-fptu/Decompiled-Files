/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.custom.JdbcResultMetadata;
import org.hibernate.loader.custom.ResultColumnProcessor;
import org.hibernate.loader.custom.ScalarResultColumnProcessor;

public class ResultRowProcessor {
    private final boolean hasScalars;
    private ResultColumnProcessor[] columnProcessors;

    public ResultRowProcessor(boolean hasScalars, ResultColumnProcessor[] columnProcessors) {
        this.hasScalars = hasScalars || columnProcessors == null || columnProcessors.length == 0;
        this.columnProcessors = columnProcessors;
    }

    public ResultColumnProcessor[] getColumnProcessors() {
        return this.columnProcessors;
    }

    public void prepareForAutoDiscovery(JdbcResultMetadata metadata) throws SQLException {
        if (this.columnProcessors == null || this.columnProcessors.length == 0) {
            int columns = metadata.getColumnCount();
            this.columnProcessors = new ResultColumnProcessor[columns];
            for (int i = 1; i <= columns; ++i) {
                this.columnProcessors[i - 1] = new ScalarResultColumnProcessor(i);
            }
        }
    }

    public Object buildResultRow(Object[] data, ResultSet resultSet, boolean hasTransformer, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Object[] resultRow = this.buildResultRow(data, resultSet, session);
        if (hasTransformer) {
            return resultRow;
        }
        return resultRow.length == 1 ? resultRow[0] : resultRow;
    }

    public Object[] buildResultRow(Object[] data, ResultSet resultSet, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Object[] resultRow;
        if (!this.hasScalars) {
            resultRow = data;
        } else {
            resultRow = new Object[this.columnProcessors.length];
            for (int i = 0; i < this.columnProcessors.length; ++i) {
                resultRow[i] = this.columnProcessors[i].extract(data, resultSet, session);
            }
        }
        return resultRow;
    }
}

