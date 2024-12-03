/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.sql.GroovyResultSet;
import groovy.sql.GroovyResultSetExtension;
import groovy.sql.GroovyResultSetProxy;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class CallResultSet
extends GroovyResultSetExtension {
    int indx;
    CallableStatement call;
    ResultSet resultSet;
    boolean firstCall = true;

    CallResultSet(CallableStatement call, int indx) {
        super(null);
        this.call = call;
        this.indx = indx;
    }

    @Override
    protected ResultSet getResultSet() throws SQLException {
        if (this.firstCall) {
            this.resultSet = (ResultSet)this.call.getObject(this.indx + 1);
            this.firstCall = false;
        }
        return this.resultSet;
    }

    protected static GroovyResultSet getImpl(CallableStatement call, int idx) {
        GroovyResultSetProxy proxy = new GroovyResultSetProxy(new CallResultSet(call, idx));
        return proxy.getImpl();
    }
}

