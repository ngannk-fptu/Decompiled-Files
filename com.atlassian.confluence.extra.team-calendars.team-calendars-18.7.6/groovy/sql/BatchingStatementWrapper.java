/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.GroovyObjectSupport;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;

public class BatchingStatementWrapper
extends GroovyObjectSupport {
    private Statement delegate;
    protected int batchSize;
    protected int batchCount;
    protected Logger log;
    protected List<Integer> results;

    public BatchingStatementWrapper(Statement delegate, int batchSize, Logger log) {
        this.delegate = delegate;
        this.batchSize = batchSize;
        this.log = log;
        this.reset();
    }

    protected void reset() {
        this.batchCount = 0;
        this.results = new ArrayList<Integer>();
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return InvokerHelper.invokeMethod(this.delegate, name, args);
    }

    public void addBatch(String sql) throws SQLException {
        this.delegate.addBatch(sql);
        this.incrementBatchCount();
    }

    protected void incrementBatchCount() throws SQLException {
        ++this.batchCount;
        if (this.batchCount == this.batchSize) {
            int[] result = this.delegate.executeBatch();
            this.processResult(result);
            this.batchCount = 0;
        }
    }

    public void clearBatch() throws SQLException {
        if (this.batchSize != 0) {
            this.reset();
        }
        this.delegate.clearBatch();
    }

    public int[] executeBatch() throws SQLException {
        if (this.shouldCallDelegate()) {
            int[] lastResult = this.delegate.executeBatch();
            this.processResult(lastResult);
        }
        int[] result = new int[this.results.size()];
        for (int i = 0; i < this.results.size(); ++i) {
            result[i] = this.results.get(i);
        }
        this.reset();
        return result;
    }

    private boolean shouldCallDelegate() {
        if (this.batchCount > 0) {
            return true;
        }
        if (this.results.isEmpty()) {
            this.log.warning("Nothing has been added to batch. This might cause the JDBC driver to throw an exception.");
            return true;
        }
        return false;
    }

    protected void processResult(int[] lastResult) {
        boolean foundError = false;
        for (int i : lastResult) {
            if (i == -3) {
                foundError = true;
            }
            this.results.add(i);
        }
        if (this.batchCount != lastResult.length) {
            this.log.warning("Problem executing batch - expected result length of " + this.batchCount + " but got " + lastResult.length);
        } else if (foundError) {
            this.log.warning("Problem executing batch - at least one result failed in: " + DefaultGroovyMethods.toList(lastResult));
        } else {
            this.log.fine("Successfully executed batch with " + lastResult.length + " command(s)");
        }
    }

    public void close() throws SQLException {
        this.delegate.close();
    }
}

