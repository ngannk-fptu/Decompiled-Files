/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.GroovyRuntimeException;
import groovy.sql.GroovyResultSet;
import groovy.sql.GroovyRowResult;
import groovy.sql.ResultSetMetaDataWrapper;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class SqlGroovyMethods {
    public static GroovyRowResult toRowResult(ResultSet rs) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        LinkedHashMap<String, Object> lhm = new LinkedHashMap<String, Object>(metadata.getColumnCount(), 1.0f);
        for (int i = 1; i <= metadata.getColumnCount(); ++i) {
            lhm.put(metadata.getColumnLabel(i), rs.getObject(i));
        }
        return new GroovyRowResult(lhm);
    }

    public static Timestamp toTimestamp(Date d) {
        return new Timestamp(d.getTime());
    }

    public static boolean asBoolean(GroovyResultSet grs) {
        return true;
    }

    public static Iterator<ResultSetMetaDataWrapper> iterator(ResultSetMetaData resultSetMetaData) {
        return new ResultSetMetaDataIterator(resultSetMetaData);
    }

    private static class ResultSetMetaDataIterator
    implements Iterator<ResultSetMetaDataWrapper> {
        private ResultSetMetaData target;
        private int index = 1;

        public ResultSetMetaDataIterator(ResultSetMetaData target) {
            this.target = target;
        }

        @Override
        public boolean hasNext() {
            try {
                return this.index <= this.target.getColumnCount();
            }
            catch (SQLException ex) {
                throw new GroovyRuntimeException("Unable to obtain column count from ResultSetMetaData", ex);
            }
        }

        @Override
        public ResultSetMetaDataWrapper next() {
            return new ResultSetMetaDataWrapper(this.target, this.index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from ResultSetMetaData");
        }
    }
}

