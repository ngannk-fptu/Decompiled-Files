/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.Tuple;
import groovy.sql.BatchingStatementWrapper;
import groovy.sql.Sql;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class BatchingPreparedStatementWrapper
extends BatchingStatementWrapper {
    private PreparedStatement delegate;
    private List<Tuple> indexPropList;
    private Sql sql;

    public BatchingPreparedStatementWrapper(PreparedStatement delegate, List<Tuple> indexPropList, int batchSize, Logger log, Sql sql) {
        super(delegate, batchSize, log);
        this.delegate = delegate;
        this.indexPropList = indexPropList;
        this.sql = sql;
    }

    public void addBatch(Object[] parameters) throws SQLException {
        this.addBatch(Arrays.asList(parameters));
    }

    public void addBatch(List<Object> parameters) throws SQLException {
        if (this.indexPropList != null) {
            this.sql.setParameters(this.sql.getUpdatedParams(parameters, this.indexPropList), this.delegate);
        } else {
            this.sql.setParameters(parameters, this.delegate);
        }
        this.delegate.addBatch();
        this.incrementBatchCount();
    }
}

