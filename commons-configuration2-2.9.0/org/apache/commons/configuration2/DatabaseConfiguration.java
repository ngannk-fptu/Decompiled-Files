/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.sql.DataSource;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.convert.DisabledListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.io.ConfigurationLogger;

public class DatabaseConfiguration
extends AbstractConfiguration {
    private static final String SQL_GET_PROPERTY = "SELECT * FROM %s WHERE %s =?";
    private static final String SQL_IS_EMPTY = "SELECT count(*) FROM %s WHERE 1 = 1";
    private static final String SQL_CLEAR_PROPERTY = "DELETE FROM %s WHERE %s =?";
    private static final String SQL_CLEAR = "DELETE FROM %s WHERE 1 = 1";
    private static final String SQL_GET_KEYS = "SELECT DISTINCT %s FROM %s WHERE 1 = 1";
    private DataSource dataSource;
    private String table;
    private String configurationNameColumn;
    private String keyColumn;
    private String valueColumn;
    private String configurationName;
    private boolean autoCommit;

    public DatabaseConfiguration() {
        this.initLogger(new ConfigurationLogger(DatabaseConfiguration.class));
        this.addErrorLogListener();
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getConfigurationNameColumn() {
        return this.configurationNameColumn;
    }

    public void setConfigurationNameColumn(String configurationNameColumn) {
        this.configurationNameColumn = configurationNameColumn;
    }

    public String getKeyColumn() {
        return this.keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public String getValueColumn() {
        return this.valueColumn;
    }

    public void setValueColumn(String valueColumn) {
        this.valueColumn = valueColumn;
    }

    public String getConfigurationName() {
        return this.configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public boolean isAutoCommit() {
        return this.autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Override
    protected Object getPropertyInternal(final String key) {
        JdbcOperation<Object> op = new JdbcOperation<Object>(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null){

            @Override
            protected Object performOperation() throws SQLException {
                ArrayList results = new ArrayList();
                try (ResultSet rs = this.openResultSet(String.format(DatabaseConfiguration.SQL_GET_PROPERTY, DatabaseConfiguration.this.table, DatabaseConfiguration.this.keyColumn), true, key);){
                    while (rs.next()) {
                        DatabaseConfiguration.this.getListDelimiterHandler().parse(DatabaseConfiguration.this.extractPropertyValue(rs)).forEach(results::add);
                    }
                }
                if (!results.isEmpty()) {
                    return results.size() > 1 ? results : results.get(0);
                }
                return null;
            }
        };
        return op.execute();
    }

    @Override
    protected void addPropertyDirect(final String key, final Object obj) {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE, ConfigurationEvent.ADD_PROPERTY, key, obj){

            @Override
            protected Void performOperation() throws SQLException {
                StringBuilder query = new StringBuilder("INSERT INTO ");
                query.append(DatabaseConfiguration.this.table).append(" (");
                query.append(DatabaseConfiguration.this.keyColumn).append(", ");
                query.append(DatabaseConfiguration.this.valueColumn);
                if (DatabaseConfiguration.this.configurationNameColumn != null) {
                    query.append(", ").append(DatabaseConfiguration.this.configurationNameColumn);
                }
                query.append(") VALUES (?, ?");
                if (DatabaseConfiguration.this.configurationNameColumn != null) {
                    query.append(", ?");
                }
                query.append(")");
                try (PreparedStatement pstmt = this.initStatement(query.toString(), false, key, String.valueOf(obj));){
                    if (DatabaseConfiguration.this.configurationNameColumn != null) {
                        pstmt.setString(3, DatabaseConfiguration.this.configurationName);
                    }
                    pstmt.executeUpdate();
                    Void void_ = null;
                    return void_;
                }
            }
        }.execute();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void addPropertyInternal(String key, Object value) {
        ListDelimiterHandler oldHandler = this.getListDelimiterHandler();
        try {
            this.setListDelimiterHandler(DisabledListDelimiterHandler.INSTANCE);
            super.addPropertyInternal(key, value);
        }
        finally {
            this.setListDelimiterHandler(oldHandler);
        }
    }

    @Override
    protected boolean isEmptyInternal() {
        JdbcOperation<Integer> op = new JdbcOperation<Integer>(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null){

            @Override
            protected Integer performOperation() throws SQLException {
                try (ResultSet rs = this.openResultSet(String.format(DatabaseConfiguration.SQL_IS_EMPTY, DatabaseConfiguration.this.table), true, new Object[0]);){
                    Integer n = rs.next() ? Integer.valueOf(rs.getInt(1)) : null;
                    return n;
                }
            }
        };
        Integer count = (Integer)op.execute();
        return count == null || count == 0;
    }

    @Override
    protected boolean containsKeyInternal(final String key) {
        JdbcOperation<Boolean> op = new JdbcOperation<Boolean>(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null){

            @Override
            protected Boolean performOperation() throws SQLException {
                try (ResultSet rs = this.openResultSet(String.format(DatabaseConfiguration.SQL_GET_PROPERTY, DatabaseConfiguration.this.table, DatabaseConfiguration.this.keyColumn), true, key);){
                    Boolean bl = rs.next();
                    return bl;
                }
            }
        };
        Boolean result = (Boolean)op.execute();
        return result != null && result != false;
    }

    @Override
    protected void clearPropertyDirect(final String key) {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE, ConfigurationEvent.CLEAR_PROPERTY, key, null){

            @Override
            protected Void performOperation() throws SQLException {
                try (PreparedStatement ps = this.initStatement(String.format(DatabaseConfiguration.SQL_CLEAR_PROPERTY, DatabaseConfiguration.this.table, DatabaseConfiguration.this.keyColumn), true, key);){
                    ps.executeUpdate();
                    Void void_ = null;
                    return void_;
                }
            }
        }.execute();
    }

    @Override
    protected void clearInternal() {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE, ConfigurationEvent.CLEAR, null, null){

            @Override
            protected Void performOperation() throws SQLException {
                try (PreparedStatement statement = this.initStatement(String.format(DatabaseConfiguration.SQL_CLEAR, DatabaseConfiguration.this.table), true, new Object[0]);){
                    statement.executeUpdate();
                }
                return null;
            }
        }.execute();
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        final ArrayList keys = new ArrayList();
        new JdbcOperation<Collection<String>>(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null){

            @Override
            protected Collection<String> performOperation() throws SQLException {
                try (ResultSet rs = this.openResultSet(String.format(DatabaseConfiguration.SQL_GET_KEYS, DatabaseConfiguration.this.keyColumn, DatabaseConfiguration.this.table), true, new Object[0]);){
                    while (rs.next()) {
                        keys.add(rs.getString(1));
                    }
                    Collection collection = keys;
                    return collection;
                }
            }
        }.execute();
        return keys.iterator();
    }

    public DataSource getDatasource() {
        return this.dataSource;
    }

    protected void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            this.getLogger().error("An error occurred on closing the result set", e);
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (SQLException e) {
            this.getLogger().error("An error occurred on closing the statement", e);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            this.getLogger().error("An error occurred on closing the connection", e);
        }
    }

    protected Object extractPropertyValue(ResultSet rs) throws SQLException {
        Object value = rs.getObject(this.valueColumn);
        if (value instanceof Clob) {
            value = DatabaseConfiguration.convertClob((Clob)value);
        }
        return value;
    }

    private static Object convertClob(Clob clob) throws SQLException {
        int len = (int)clob.length();
        return len > 0 ? clob.getSubString(1L, len) : "";
    }

    private abstract class JdbcOperation<T> {
        private Connection conn;
        private PreparedStatement pstmt;
        private ResultSet resultSet;
        private final EventType<? extends ConfigurationErrorEvent> errorEventType;
        private final EventType<?> operationEventType;
        private final String errorPropertyName;
        private final Object errorPropertyValue;

        protected JdbcOperation(EventType<? extends ConfigurationErrorEvent> errEvType, EventType<?> opType, String errPropName, Object errPropVal) {
            this.errorEventType = errEvType;
            this.operationEventType = opType;
            this.errorPropertyName = errPropName;
            this.errorPropertyValue = errPropVal;
        }

        public T execute() {
            T result = null;
            try {
                this.conn = DatabaseConfiguration.this.getDatasource().getConnection();
                result = this.performOperation();
                if (DatabaseConfiguration.this.isAutoCommit()) {
                    this.conn.commit();
                }
            }
            catch (SQLException e) {
                DatabaseConfiguration.this.fireError(this.errorEventType, this.operationEventType, this.errorPropertyName, this.errorPropertyValue, e);
            }
            finally {
                DatabaseConfiguration.this.close(this.conn, this.pstmt, this.resultSet);
            }
            return result;
        }

        protected Connection getConnection() {
            return this.conn;
        }

        protected PreparedStatement createStatement(String sql, boolean nameCol) throws SQLException {
            String statement;
            if (nameCol && DatabaseConfiguration.this.configurationNameColumn != null) {
                StringBuilder buf = new StringBuilder(sql);
                buf.append(" AND ").append(DatabaseConfiguration.this.configurationNameColumn).append("=?");
                statement = buf.toString();
            } else {
                statement = sql;
            }
            this.pstmt = this.getConnection().prepareStatement(statement);
            return this.pstmt;
        }

        protected PreparedStatement initStatement(String sql, boolean nameCol, Object ... params) throws SQLException {
            PreparedStatement ps = this.createStatement(sql, nameCol);
            int idx = 1;
            for (Object param : params) {
                ps.setObject(idx++, param);
            }
            if (nameCol && DatabaseConfiguration.this.configurationNameColumn != null) {
                ps.setString(idx, DatabaseConfiguration.this.configurationName);
            }
            return ps;
        }

        protected ResultSet openResultSet(String sql, boolean nameCol, Object ... params) throws SQLException {
            this.resultSet = this.initStatement(sql, nameCol, params).executeQuery();
            return this.resultSet;
        }

        protected abstract T performOperation() throws SQLException;
    }
}

