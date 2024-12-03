/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 */
package org.springframework.jdbc.object;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.object.SqlUpdate;

public class BatchSqlUpdate
extends SqlUpdate {
    public static final int DEFAULT_BATCH_SIZE = 5000;
    private int batchSize = 5000;
    private boolean trackRowsAffected = true;
    private final Deque<Object[]> parameterQueue = new ArrayDeque<Object[]>();
    private final List<Integer> rowsAffected = new ArrayList<Integer>();

    public BatchSqlUpdate() {
    }

    public BatchSqlUpdate(DataSource ds, String sql) {
        super(ds, sql);
    }

    public BatchSqlUpdate(DataSource ds, String sql, int[] types) {
        super(ds, sql, types);
    }

    public BatchSqlUpdate(DataSource ds, String sql, int[] types, int batchSize) {
        super(ds, sql, types);
        this.setBatchSize(batchSize);
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setTrackRowsAffected(boolean trackRowsAffected) {
        this.trackRowsAffected = trackRowsAffected;
    }

    @Override
    protected boolean supportsLobParameters() {
        return false;
    }

    @Override
    public int update(Object ... params) throws DataAccessException {
        this.validateParameters(params);
        this.parameterQueue.add((Object[])params.clone());
        if (this.parameterQueue.size() == this.batchSize) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Triggering auto-flush because queue reached batch size of " + this.batchSize));
            }
            this.flush();
        }
        return -1;
    }

    public int[] flush() {
        int[] rowsAffected;
        if (this.parameterQueue.isEmpty()) {
            return new int[0];
        }
        for (int rowCount : rowsAffected = this.getJdbcTemplate().batchUpdate(this.resolveSql(), new BatchPreparedStatementSetter(){

            @Override
            public int getBatchSize() {
                return BatchSqlUpdate.this.parameterQueue.size();
            }

            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException {
                Object[] params = (Object[])BatchSqlUpdate.this.parameterQueue.removeFirst();
                BatchSqlUpdate.this.newPreparedStatementSetter(params).setValues(ps);
            }
        })) {
            this.checkRowsAffected(rowCount);
            if (!this.trackRowsAffected) continue;
            this.rowsAffected.add(rowCount);
        }
        return rowsAffected;
    }

    public int getQueueCount() {
        return this.parameterQueue.size();
    }

    public int getExecutionCount() {
        return this.rowsAffected.size();
    }

    public int[] getRowsAffected() {
        int[] result = new int[this.rowsAffected.size()];
        for (int i = 0; i < this.rowsAffected.size(); ++i) {
            result[i] = this.rowsAffected.get(i);
        }
        return result;
    }

    public void reset() {
        this.parameterQueue.clear();
        this.rowsAffected.clear();
    }
}

