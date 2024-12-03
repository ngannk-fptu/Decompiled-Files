/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MissingPropertyException;
import groovy.lang.Tuple;
import groovy.sql.BatchingPreparedStatementWrapper;
import groovy.sql.BatchingStatementWrapper;
import groovy.sql.CallResultSet;
import groovy.sql.DataSet;
import groovy.sql.ExpandedVariable;
import groovy.sql.ExtractIndexAndSql;
import groovy.sql.GroovyResultSet;
import groovy.sql.GroovyResultSetProxy;
import groovy.sql.GroovyRowResult;
import groovy.sql.InOutParameter;
import groovy.sql.InParameter;
import groovy.sql.OutParameter;
import groovy.sql.ResultSetOutParameter;
import groovy.sql.SqlWithParams;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.SqlGroovyMethods;

public class Sql {
    protected static final Logger LOG = Logger.getLogger(Sql.class.getName());
    private static final List<Object> EMPTY_LIST = Collections.emptyList();
    private static final int USE_COLUMN_NAMES = -1;
    private DataSource dataSource;
    private Connection useConnection;
    private int resultSetType = 1003;
    private int resultSetConcurrency = 1007;
    private int resultSetHoldability = -1;
    private int updateCount = 0;
    private Closure configureStatement;
    private boolean cacheConnection;
    private boolean cacheStatements;
    private boolean cacheNamedQueries = true;
    private boolean enableNamedQueries = true;
    private boolean withinBatch;
    private boolean enableMetaDataChecking = false;
    private final Map<String, Statement> statementCache = new HashMap<String, Statement>();
    private final Map<String, String> namedParamSqlCache = new HashMap<String, String>();
    private final Map<String, List<Tuple>> namedParamIndexPropCache = new HashMap<String, List<Tuple>>();
    private List<String> keyColumnNames;
    public static final OutParameter ARRAY = new OutParameter(){

        @Override
        public int getType() {
            return 2003;
        }
    };
    public static final OutParameter BIGINT = new OutParameter(){

        @Override
        public int getType() {
            return -5;
        }
    };
    public static final OutParameter BINARY = new OutParameter(){

        @Override
        public int getType() {
            return -2;
        }
    };
    public static final OutParameter BIT = new OutParameter(){

        @Override
        public int getType() {
            return -7;
        }
    };
    public static final OutParameter BLOB = new OutParameter(){

        @Override
        public int getType() {
            return 2004;
        }
    };
    public static final OutParameter BOOLEAN = new OutParameter(){

        @Override
        public int getType() {
            return 16;
        }
    };
    public static final OutParameter CHAR = new OutParameter(){

        @Override
        public int getType() {
            return 1;
        }
    };
    public static final OutParameter CLOB = new OutParameter(){

        @Override
        public int getType() {
            return 2005;
        }
    };
    public static final OutParameter DATALINK = new OutParameter(){

        @Override
        public int getType() {
            return 70;
        }
    };
    public static final OutParameter DATE = new OutParameter(){

        @Override
        public int getType() {
            return 91;
        }
    };
    public static final OutParameter DECIMAL = new OutParameter(){

        @Override
        public int getType() {
            return 3;
        }
    };
    public static final OutParameter DISTINCT = new OutParameter(){

        @Override
        public int getType() {
            return 2001;
        }
    };
    public static final OutParameter DOUBLE = new OutParameter(){

        @Override
        public int getType() {
            return 8;
        }
    };
    public static final OutParameter FLOAT = new OutParameter(){

        @Override
        public int getType() {
            return 6;
        }
    };
    public static final OutParameter INTEGER = new OutParameter(){

        @Override
        public int getType() {
            return 4;
        }
    };
    public static final OutParameter JAVA_OBJECT = new OutParameter(){

        @Override
        public int getType() {
            return 2000;
        }
    };
    public static final OutParameter LONGVARBINARY = new OutParameter(){

        @Override
        public int getType() {
            return -4;
        }
    };
    public static final OutParameter LONGVARCHAR = new OutParameter(){

        @Override
        public int getType() {
            return -1;
        }
    };
    public static final OutParameter NULL = new OutParameter(){

        @Override
        public int getType() {
            return 0;
        }
    };
    public static final OutParameter NUMERIC = new OutParameter(){

        @Override
        public int getType() {
            return 2;
        }
    };
    public static final OutParameter OTHER = new OutParameter(){

        @Override
        public int getType() {
            return 1111;
        }
    };
    public static final OutParameter REAL = new OutParameter(){

        @Override
        public int getType() {
            return 7;
        }
    };
    public static final OutParameter REF = new OutParameter(){

        @Override
        public int getType() {
            return 2006;
        }
    };
    public static final OutParameter SMALLINT = new OutParameter(){

        @Override
        public int getType() {
            return 5;
        }
    };
    public static final OutParameter STRUCT = new OutParameter(){

        @Override
        public int getType() {
            return 2002;
        }
    };
    public static final OutParameter TIME = new OutParameter(){

        @Override
        public int getType() {
            return 92;
        }
    };
    public static final OutParameter TIMESTAMP = new OutParameter(){

        @Override
        public int getType() {
            return 93;
        }
    };
    public static final OutParameter TINYINT = new OutParameter(){

        @Override
        public int getType() {
            return -6;
        }
    };
    public static final OutParameter VARBINARY = new OutParameter(){

        @Override
        public int getType() {
            return -3;
        }
    };
    public static final OutParameter VARCHAR = new OutParameter(){

        @Override
        public int getType() {
            return 12;
        }
    };
    public static final int NO_RESULT_SETS = 0;
    public static final int FIRST_RESULT_SET = 1;
    public static final int ALL_RESULT_SETS = 2;

    public boolean isEnableMetaDataChecking() {
        return this.enableMetaDataChecking;
    }

    public void setEnableMetaDataChecking(boolean enableMetaDataChecking) {
        this.enableMetaDataChecking = enableMetaDataChecking;
    }

    public static Sql newInstance(String url) throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        return new Sql(connection);
    }

    public static void withInstance(String url, Closure c) throws SQLException {
        Sql sql = null;
        try {
            sql = Sql.newInstance(url);
            c.call((Object)sql);
        }
        finally {
            if (sql != null) {
                sql.close();
            }
        }
    }

    public static Sql newInstance(String url, Properties properties) throws SQLException {
        Connection connection = DriverManager.getConnection(url, properties);
        return new Sql(connection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void withInstance(String url, Properties properties, Closure c) throws SQLException {
        Sql sql = null;
        try {
            sql = Sql.newInstance(url, properties);
            c.call((Object)sql);
        }
        finally {
            if (sql != null) {
                sql.close();
            }
        }
    }

    public static Sql newInstance(String url, Properties properties, String driverClassName) throws SQLException, ClassNotFoundException {
        Sql.loadDriver(driverClassName);
        return Sql.newInstance(url, properties);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void withInstance(String url, Properties properties, String driverClassName, Closure c) throws SQLException, ClassNotFoundException {
        Sql sql = null;
        try {
            sql = Sql.newInstance(url, properties, driverClassName);
            c.call((Object)sql);
        }
        finally {
            if (sql != null) {
                sql.close();
            }
        }
    }

    public static Sql newInstance(String url, String user, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        return new Sql(connection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void withInstance(String url, String user, String password, Closure c) throws SQLException {
        Sql sql = null;
        try {
            sql = Sql.newInstance(url, user, password);
            c.call((Object)sql);
        }
        finally {
            if (sql != null) {
                sql.close();
            }
        }
    }

    public static Sql newInstance(String url, String user, String password, String driverClassName) throws SQLException, ClassNotFoundException {
        Sql.loadDriver(driverClassName);
        return Sql.newInstance(url, user, password);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void withInstance(String url, String user, String password, String driverClassName, Closure c) throws SQLException, ClassNotFoundException {
        Sql sql = null;
        try {
            sql = Sql.newInstance(url, user, password, driverClassName);
            c.call((Object)sql);
        }
        finally {
            if (sql != null) {
                sql.close();
            }
        }
    }

    public static Sql newInstance(String url, String driverClassName) throws SQLException, ClassNotFoundException {
        Sql.loadDriver(driverClassName);
        return Sql.newInstance(url);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void withInstance(String url, String driverClassName, Closure c) throws SQLException, ClassNotFoundException {
        Sql sql = null;
        try {
            sql = Sql.newInstance(url, driverClassName);
            c.call((Object)sql);
        }
        finally {
            if (sql != null) {
                sql.close();
            }
        }
    }

    public static Sql newInstance(Map<String, Object> args) throws SQLException, ClassNotFoundException {
        Connection connection;
        Properties props;
        if (!args.containsKey("url")) {
            throw new IllegalArgumentException("Argument 'url' is required");
        }
        if (args.get("url") == null) {
            throw new IllegalArgumentException("Argument 'url' must not be null");
        }
        if (args.containsKey("driverClassName") && args.containsKey("driver")) {
            throw new IllegalArgumentException("Only one of 'driverClassName' and 'driver' should be provided");
        }
        HashMap<String, Object> sqlArgs = new HashMap<String, Object>(args);
        Object driverClassName = sqlArgs.remove("driverClassName");
        if (driverClassName == null) {
            driverClassName = sqlArgs.remove("driver");
        }
        if (driverClassName != null) {
            Sql.loadDriver(driverClassName.toString());
        }
        if ((props = (Properties)sqlArgs.remove("properties")) != null && sqlArgs.containsKey("user")) {
            throw new IllegalArgumentException("Only one of 'properties' and 'user' should be supplied");
        }
        if (props != null && sqlArgs.containsKey("password")) {
            throw new IllegalArgumentException("Only one of 'properties' and 'password' should be supplied");
        }
        if (sqlArgs.containsKey("user") ^ sqlArgs.containsKey("password")) {
            throw new IllegalArgumentException("Found one but not both of 'user' and 'password'");
        }
        Object url = sqlArgs.remove("url");
        LOG.fine("url = " + url);
        if (props != null) {
            connection = DriverManager.getConnection(url.toString(), props);
            if (LOG.isLoggable(Level.FINE)) {
                if (!props.containsKey("password")) {
                    LOG.fine("props = " + props);
                } else {
                    Properties propsCopy = new Properties();
                    propsCopy.putAll((Map<?, ?>)props);
                    propsCopy.setProperty("password", "***");
                    LOG.fine("props = " + propsCopy);
                }
            }
        } else if (sqlArgs.containsKey("user")) {
            Object user = sqlArgs.remove("user");
            LOG.fine("user = " + user);
            Object password = sqlArgs.remove("password");
            LOG.fine("password = " + (password == null ? "null" : "***"));
            connection = DriverManager.getConnection(url.toString(), user == null ? null : user.toString(), password == null ? null : password.toString());
        } else {
            LOG.fine("No user/password specified");
            connection = DriverManager.getConnection(url.toString());
        }
        Sql result = (Sql)InvokerHelper.invokeConstructorOf(Sql.class, sqlArgs);
        result.setConnection(connection);
        return result;
    }

    public static void withInstance(Map<String, Object> args, Closure c) throws SQLException, ClassNotFoundException {
        Sql sql = null;
        try {
            sql = Sql.newInstance(args);
            c.call((Object)sql);
        }
        finally {
            if (sql != null) {
                sql.close();
            }
        }
    }

    public int getResultSetType() {
        return this.resultSetType;
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public int getResultSetConcurrency() {
        return this.resultSetConcurrency;
    }

    public void setResultSetConcurrency(int resultSetConcurrency) {
        this.resultSetConcurrency = resultSetConcurrency;
    }

    public int getResultSetHoldability() {
        return this.resultSetHoldability;
    }

    public void setResultSetHoldability(int resultSetHoldability) {
        this.resultSetHoldability = resultSetHoldability;
    }

    public static void loadDriver(String driverClassName) throws ClassNotFoundException {
        try {
            Class.forName(driverClassName);
        }
        catch (ClassNotFoundException e) {
            try {
                Thread.currentThread().getContextClassLoader().loadClass(driverClassName);
            }
            catch (ClassNotFoundException e2) {
                try {
                    Sql.class.getClassLoader().loadClass(driverClassName);
                }
                catch (ClassNotFoundException e3) {
                    throw e;
                }
            }
        }
    }

    public static InParameter ARRAY(Object value) {
        return Sql.in(2003, value);
    }

    public static InParameter BIGINT(Object value) {
        return Sql.in(-5, value);
    }

    public static InParameter BINARY(Object value) {
        return Sql.in(-2, value);
    }

    public static InParameter BIT(Object value) {
        return Sql.in(-7, value);
    }

    public static InParameter BLOB(Object value) {
        return Sql.in(2004, value);
    }

    public static InParameter BOOLEAN(Object value) {
        return Sql.in(16, value);
    }

    public static InParameter CHAR(Object value) {
        return Sql.in(1, value);
    }

    public static InParameter CLOB(Object value) {
        return Sql.in(2005, value);
    }

    public static InParameter DATALINK(Object value) {
        return Sql.in(70, value);
    }

    public static InParameter DATE(Object value) {
        return Sql.in(91, value);
    }

    public static InParameter DECIMAL(Object value) {
        return Sql.in(3, value);
    }

    public static InParameter DISTINCT(Object value) {
        return Sql.in(2001, value);
    }

    public static InParameter DOUBLE(Object value) {
        return Sql.in(8, value);
    }

    public static InParameter FLOAT(Object value) {
        return Sql.in(6, value);
    }

    public static InParameter INTEGER(Object value) {
        return Sql.in(4, value);
    }

    public static InParameter JAVA_OBJECT(Object value) {
        return Sql.in(2000, value);
    }

    public static InParameter LONGVARBINARY(Object value) {
        return Sql.in(-4, value);
    }

    public static InParameter LONGVARCHAR(Object value) {
        return Sql.in(-1, value);
    }

    public static InParameter NULL(Object value) {
        return Sql.in(0, value);
    }

    public static InParameter NUMERIC(Object value) {
        return Sql.in(2, value);
    }

    public static InParameter OTHER(Object value) {
        return Sql.in(1111, value);
    }

    public static InParameter REAL(Object value) {
        return Sql.in(7, value);
    }

    public static InParameter REF(Object value) {
        return Sql.in(2006, value);
    }

    public static InParameter SMALLINT(Object value) {
        return Sql.in(5, value);
    }

    public static InParameter STRUCT(Object value) {
        return Sql.in(2002, value);
    }

    public static InParameter TIME(Object value) {
        return Sql.in(92, value);
    }

    public static InParameter TIMESTAMP(Object value) {
        return Sql.in(93, value);
    }

    public static InParameter TINYINT(Object value) {
        return Sql.in(-6, value);
    }

    public static InParameter VARBINARY(Object value) {
        return Sql.in(-3, value);
    }

    public static InParameter VARCHAR(Object value) {
        return Sql.in(12, value);
    }

    public static InParameter in(final int type, final Object value) {
        return new InParameter(){

            @Override
            public int getType() {
                return type;
            }

            @Override
            public Object getValue() {
                return value;
            }
        };
    }

    public static OutParameter out(final int type) {
        return new OutParameter(){

            @Override
            public int getType() {
                return type;
            }
        };
    }

    public static InOutParameter inout(final InParameter in) {
        return new InOutParameter(){

            @Override
            public int getType() {
                return in.getType();
            }

            @Override
            public Object getValue() {
                return in.getValue();
            }
        };
    }

    public static ResultSetOutParameter resultSet(final int type) {
        return new ResultSetOutParameter(){

            @Override
            public int getType() {
                return type;
            }
        };
    }

    public static ExpandedVariable expand(final Object object) {
        return new ExpandedVariable(){

            @Override
            public Object getObject() {
                return object;
            }
        };
    }

    public Sql(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Sql(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("Must specify a non-null Connection");
        }
        this.useConnection = connection;
    }

    public Sql(Sql parent) {
        this.dataSource = parent.dataSource;
        this.useConnection = parent.useConnection;
    }

    private Sql() {
    }

    public DataSet dataSet(String table) {
        return new DataSet(this, table);
    }

    public DataSet dataSet(Class<?> type) {
        return new DataSet(this, type);
    }

    public void query(String sql, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSet"}) Closure closure) throws SQLException {
        Connection connection = this.createConnection();
        Statement statement = null;
        ResultSet results = null;
        try {
            statement = this.getStatement(connection, sql);
            results = statement.executeQuery(sql);
            closure.call((Object)results);
            this.closeResources(connection, statement, results);
        }
        catch (SQLException e) {
            try {
                LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
                throw e;
            }
            catch (Throwable throwable) {
                this.closeResources(connection, statement, results);
                throw throwable;
            }
        }
    }

    public void query(String sql, List<Object> params, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSet"}) Closure closure) throws SQLException {
        Connection connection = this.createConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        try {
            statement = this.getPreparedStatement(connection, sql, params);
            results = statement.executeQuery();
            closure.call((Object)results);
            this.closeResources(connection, statement, results);
        }
        catch (SQLException e) {
            try {
                LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
                throw e;
            }
            catch (Throwable throwable) {
                this.closeResources(connection, statement, results);
                throw throwable;
            }
        }
    }

    public void query(String sql, Map map, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSet"}) Closure closure) throws SQLException {
        this.query(sql, Sql.singletonList(map), closure);
    }

    public void query(Map map, String sql, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSet"}) Closure closure) throws SQLException {
        this.query(sql, Sql.singletonList(map), closure);
    }

    private static ArrayList<Object> singletonList(Object item) {
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(item);
        return params;
    }

    public void query(GString gstring, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSet"}) Closure closure) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        this.query(sql, params, closure);
    }

    public void eachRow(String sql, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(sql, (Closure)null, closure);
    }

    public void eachRow(String sql, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(sql, (Closure)null, offset, maxRows, closure);
    }

    public void eachRow(String sql, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        this.eachRow(sql, metaClosure, 0, 0, rowClosure);
    }

    public void eachRow(String sql, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        ResultSet results;
        Statement statement;
        Connection connection;
        block7: {
            boolean cursorAtRow;
            connection = this.createConnection();
            statement = null;
            results = null;
            statement = this.getStatement(connection, sql);
            results = statement.executeQuery(sql);
            if (metaClosure != null) {
                metaClosure.call((Object)results.getMetaData());
            }
            if (cursorAtRow = Sql.moveCursor(results, offset)) break block7;
            this.closeResources(connection, statement, results);
            return;
        }
        try {
            GroovyResultSet groovyRS = new GroovyResultSetProxy(results).getImpl();
            int i = 0;
            while ((maxRows <= 0 || i++ < maxRows) && groovyRS.next()) {
                rowClosure.call((Object)groovyRS);
            }
            this.closeResources(connection, statement, results);
        }
        catch (SQLException e) {
            try {
                LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
                throw e;
            }
            catch (Throwable throwable) {
                this.closeResources(connection, statement, results);
                throw throwable;
            }
        }
    }

    private static boolean moveCursor(ResultSet results, int offset) throws SQLException {
        boolean cursorAtRow = true;
        if (results.getType() == 1003) {
            int i = 1;
            while (i++ < offset && cursorAtRow) {
                cursorAtRow = results.next();
            }
        } else if (offset > 1) {
            cursorAtRow = results.absolute(offset - 1);
        }
        return cursorAtRow;
    }

    public void eachRow(String sql, List<Object> params, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        ResultSet results;
        PreparedStatement statement;
        Connection connection;
        block7: {
            boolean cursorAtRow;
            connection = this.createConnection();
            statement = null;
            results = null;
            statement = this.getPreparedStatement(connection, sql, params);
            results = statement.executeQuery();
            if (metaClosure != null) {
                metaClosure.call((Object)results.getMetaData());
            }
            if (cursorAtRow = Sql.moveCursor(results, offset)) break block7;
            this.closeResources(connection, statement, results);
            return;
        }
        try {
            GroovyResultSet groovyRS = new GroovyResultSetProxy(results).getImpl();
            int i = 0;
            while ((maxRows <= 0 || i++ < maxRows) && groovyRS.next()) {
                rowClosure.call((Object)groovyRS);
            }
            this.closeResources(connection, statement, results);
        }
        catch (SQLException e) {
            try {
                LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
                throw e;
            }
            catch (Throwable throwable) {
                this.closeResources(connection, statement, results);
                throw throwable;
            }
        }
    }

    public void eachRow(String sql, Map map, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        this.eachRow(sql, Sql.singletonList(map), metaClosure, offset, maxRows, rowClosure);
    }

    public void eachRow(Map map, String sql, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        this.eachRow(sql, Sql.singletonList(map), metaClosure, offset, maxRows, rowClosure);
    }

    public void eachRow(String sql, List<Object> params, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        this.eachRow(sql, params, metaClosure, 0, 0, rowClosure);
    }

    public void eachRow(String sql, Map params, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        this.eachRow(sql, Sql.singletonList(params), metaClosure, rowClosure);
    }

    public void eachRow(Map params, String sql, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        this.eachRow(sql, Sql.singletonList(params), metaClosure, rowClosure);
    }

    public void eachRow(String sql, List<Object> params, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(sql, params, null, closure);
    }

    public void eachRow(String sql, Map params, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(sql, Sql.singletonList(params), closure);
    }

    public void eachRow(Map params, String sql, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(sql, Sql.singletonList(params), closure);
    }

    public void eachRow(String sql, List<Object> params, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(sql, params, null, offset, maxRows, closure);
    }

    public void eachRow(String sql, Map params, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(sql, Sql.singletonList(params), offset, maxRows, closure);
    }

    public void eachRow(Map params, String sql, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(sql, Sql.singletonList(params), offset, maxRows, closure);
    }

    public void eachRow(GString gstring, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        this.eachRow(sql, params, metaClosure, rowClosure);
    }

    public void eachRow(GString gstring, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure rowClosure) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        this.eachRow(sql, params, metaClosure, offset, maxRows, rowClosure);
    }

    public void eachRow(GString gstring, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        this.eachRow(sql, params, offset, maxRows, closure);
    }

    public void eachRow(GString gstring, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(gstring, null, closure);
    }

    public List<GroovyRowResult> rows(String sql) throws SQLException {
        return this.rows(sql, 0, 0, null);
    }

    public List<GroovyRowResult> rows(String sql, int offset, int maxRows) throws SQLException {
        return this.rows(sql, offset, maxRows, null);
    }

    public List<GroovyRowResult> rows(String sql, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        return this.rows(sql, 0, 0, metaClosure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<GroovyRowResult> rows(String sql, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        AbstractQueryCommand command = this.createQueryCommand(sql);
        command.setMaxRows(offset + maxRows);
        ResultSet rs = null;
        try {
            rs = command.execute();
            List<GroovyRowResult> result = this.asList(sql, rs, offset, maxRows, metaClosure);
            rs = null;
            List<GroovyRowResult> list = result;
            return list;
        }
        finally {
            command.closeResources(rs);
        }
    }

    public List<GroovyRowResult> rows(String sql, List<Object> params) throws SQLException {
        return this.rows(sql, params, null);
    }

    public List<GroovyRowResult> rows(Map params, String sql) throws SQLException {
        return this.rows(sql, Sql.singletonList(params));
    }

    public List<GroovyRowResult> rows(String sql, List<Object> params, int offset, int maxRows) throws SQLException {
        return this.rows(sql, params, offset, maxRows, null);
    }

    public List<GroovyRowResult> rows(String sql, Map params, int offset, int maxRows) throws SQLException {
        return this.rows(sql, Sql.singletonList(params), offset, maxRows);
    }

    public List<GroovyRowResult> rows(Map params, String sql, int offset, int maxRows) throws SQLException {
        return this.rows(sql, Sql.singletonList(params), offset, maxRows);
    }

    public List<GroovyRowResult> rows(String sql, Object[] params) throws SQLException {
        return this.rows(sql, params, 0, 0);
    }

    public List<GroovyRowResult> rows(String sql, Object[] params, int offset, int maxRows) throws SQLException {
        return this.rows(sql, Arrays.asList(params), offset, maxRows, null);
    }

    public List<GroovyRowResult> rows(String sql, List<Object> params, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        return this.rows(sql, params, 0, 0, metaClosure);
    }

    public List<GroovyRowResult> rows(String sql, Map params, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        return this.rows(sql, Sql.singletonList(params), metaClosure);
    }

    public List<GroovyRowResult> rows(Map params, String sql, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        return this.rows(sql, Sql.singletonList(params), metaClosure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<GroovyRowResult> rows(String sql, List<Object> params, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        AbstractQueryCommand command = this.createPreparedQueryCommand(sql, params);
        command.setMaxRows(offset + maxRows);
        try {
            List<GroovyRowResult> list = this.asList(sql, command.execute(), offset, maxRows, metaClosure);
            return list;
        }
        finally {
            command.closeResources();
        }
    }

    public List<GroovyRowResult> rows(String sql, Map params, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        return this.rows(sql, Sql.singletonList(params), offset, maxRows, metaClosure);
    }

    public List<GroovyRowResult> rows(Map params, String sql, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        return this.rows(sql, Sql.singletonList(params), offset, maxRows, metaClosure);
    }

    public List<GroovyRowResult> rows(GString sql, int offset, int maxRows) throws SQLException {
        return this.rows(sql, offset, maxRows, null);
    }

    public List<GroovyRowResult> rows(GString gstring) throws SQLException {
        return this.rows(gstring, null);
    }

    public List<GroovyRowResult> rows(GString gstring, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.rows(sql, params, metaClosure);
    }

    public List<GroovyRowResult> rows(GString gstring, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.rows(sql, params, offset, maxRows, metaClosure);
    }

    public GroovyRowResult firstRow(String sql) throws SQLException {
        List<GroovyRowResult> rows = null;
        try {
            rows = this.rows(sql, 1, 1, null);
        }
        catch (SQLException featureNotSupportedException) {
            rows = this.rows(sql);
        }
        if (rows.isEmpty()) {
            return null;
        }
        return rows.get(0);
    }

    public GroovyRowResult firstRow(GString gstring) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.firstRow(sql, params);
    }

    public GroovyRowResult firstRow(String sql, List<Object> params) throws SQLException {
        List<GroovyRowResult> rows = null;
        try {
            rows = this.rows(sql, params, 1, 1, null);
        }
        catch (SQLException featureNotSupportedException) {
            rows = this.rows(sql, params);
        }
        if (rows.isEmpty()) {
            return null;
        }
        return rows.get(0);
    }

    public GroovyRowResult firstRow(Map params, String sql) throws SQLException {
        return this.firstRow(sql, Sql.singletonList(params));
    }

    public GroovyRowResult firstRow(String sql, Object[] params) throws SQLException {
        return this.firstRow(sql, Arrays.asList(params));
    }

    public boolean execute(String sql) throws SQLException {
        Connection connection = this.createConnection();
        Statement statement = null;
        try {
            statement = this.getStatement(connection, sql);
            boolean isResultSet = statement.execute(sql);
            this.updateCount = statement.getUpdateCount();
            boolean bl = isResultSet;
            return bl;
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public void execute(String sql, Closure processResults) throws SQLException {
        Connection connection = this.createConnection();
        Statement statement = null;
        try {
            statement = this.getStatement(connection, sql);
            boolean isResultSet = statement.execute(sql);
            int updateCount = statement.getUpdateCount();
            while (isResultSet || updateCount != -1) {
                if (processResults.getMaximumNumberOfParameters() != 2) {
                    throw new SQLException("Incorrect number of parameters for processResults Closure");
                }
                if (isResultSet) {
                    ResultSet resultSet = statement.getResultSet();
                    List<GroovyRowResult> rowResult = resultSet == null ? null : this.asList(sql, resultSet);
                    processResults.call(isResultSet, rowResult);
                } else {
                    processResults.call(isResultSet, updateCount);
                }
                isResultSet = statement.getMoreResults();
                updateCount = statement.getUpdateCount();
            }
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public boolean execute(String sql, List<Object> params) throws SQLException {
        Connection connection = this.createConnection();
        PreparedStatement statement = null;
        try {
            statement = this.getPreparedStatement(connection, sql, params);
            boolean isResultSet = statement.execute();
            this.updateCount = statement.getUpdateCount();
            boolean bl = isResultSet;
            return bl;
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public void execute(String sql, List<Object> params, Closure processResults) throws SQLException {
        Connection connection = this.createConnection();
        PreparedStatement statement = null;
        try {
            statement = this.getPreparedStatement(connection, sql, params);
            boolean isResultSet = statement.execute();
            int updateCount = statement.getUpdateCount();
            while (isResultSet || updateCount != -1) {
                if (processResults.getMaximumNumberOfParameters() != 2) {
                    throw new SQLException("Incorrect number of parameters for processResults Closure");
                }
                if (isResultSet) {
                    ResultSet resultSet = statement.getResultSet();
                    List<GroovyRowResult> rowResult = resultSet == null ? null : this.asList(sql, resultSet);
                    processResults.call(isResultSet, rowResult);
                } else {
                    processResults.call(isResultSet, updateCount);
                }
                isResultSet = statement.getMoreResults();
                updateCount = statement.getUpdateCount();
            }
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public boolean execute(Map params, String sql) throws SQLException {
        return this.execute(sql, Sql.singletonList(params));
    }

    public void execute(Map params, String sql, Closure processResults) throws SQLException {
        this.execute(sql, Sql.singletonList(params), processResults);
    }

    public boolean execute(String sql, Object[] params) throws SQLException {
        return this.execute(sql, Arrays.asList(params));
    }

    public void execute(String sql, Object[] params, Closure processResults) throws SQLException {
        this.execute(sql, Arrays.asList(params), processResults);
    }

    public boolean execute(GString gstring) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.execute(sql, params);
    }

    public void execute(GString gstring, Closure processResults) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        this.execute(sql, params, processResults);
    }

    public List<List<Object>> executeInsert(String sql) throws SQLException {
        Connection connection = this.createConnection();
        Statement statement = null;
        try {
            statement = this.getStatement(connection, sql);
            this.updateCount = statement.executeUpdate(sql, 1);
            ResultSet keys = statement.getGeneratedKeys();
            List<List<Object>> list = Sql.calculateKeys(keys);
            return list;
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public List<List<Object>> executeInsert(String sql, List<Object> params) throws SQLException {
        Connection connection = this.createConnection();
        PreparedStatement statement = null;
        try {
            statement = this.getPreparedStatement(connection, sql, params, 1);
            this.updateCount = statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            List<List<Object>> list = Sql.calculateKeys(keys);
            this.closeResources(connection, statement);
            return list;
        }
        catch (SQLException e) {
            try {
                LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
                throw e;
            }
            catch (Throwable throwable) {
                this.closeResources(connection, statement);
                throw throwable;
            }
        }
    }

    public List<GroovyRowResult> executeInsert(String sql, List<Object> params, List<String> keyColumnNames) throws SQLException {
        Connection connection = this.createConnection();
        PreparedStatement statement = null;
        try {
            this.keyColumnNames = keyColumnNames;
            statement = this.getPreparedStatement(connection, sql, params, -1);
            this.keyColumnNames = null;
            this.updateCount = statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            List<GroovyRowResult> list = this.asList(sql, keys);
            this.closeResources(connection, statement);
            return list;
        }
        catch (SQLException e) {
            try {
                LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
                throw e;
            }
            catch (Throwable throwable) {
                this.closeResources(connection, statement);
                throw throwable;
            }
        }
    }

    public List<List<Object>> executeInsert(Map params, String sql) throws SQLException {
        return this.executeInsert(sql, Sql.singletonList(params));
    }

    public List<GroovyRowResult> executeInsert(Map params, String sql, List<String> keyColumnNames) throws SQLException {
        return this.executeInsert(sql, Sql.singletonList(params), keyColumnNames);
    }

    public List<List<Object>> executeInsert(String sql, Object[] params) throws SQLException {
        return this.executeInsert(sql, Arrays.asList(params));
    }

    public List<GroovyRowResult> executeInsert(String sql, String[] keyColumnNames) throws SQLException {
        Connection connection = this.createConnection();
        Statement statement = null;
        try {
            statement = this.getStatement(connection, sql);
            this.updateCount = statement.executeUpdate(sql, keyColumnNames);
            ResultSet keys = statement.getGeneratedKeys();
            List<GroovyRowResult> list = this.asList(sql, keys);
            return list;
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public List<GroovyRowResult> executeInsert(String sql, String[] keyColumnNames, Object[] params) throws SQLException {
        return this.executeInsert(sql, Arrays.asList(params), Arrays.asList(keyColumnNames));
    }

    public List<List<Object>> executeInsert(GString gstring) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.executeInsert(sql, params);
    }

    public List<GroovyRowResult> executeInsert(GString gstring, List<String> keyColumnNames) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.executeInsert(sql, params, keyColumnNames);
    }

    public int executeUpdate(String sql) throws SQLException {
        Connection connection = this.createConnection();
        Statement statement = null;
        try {
            statement = this.getStatement(connection, sql);
            int n = this.updateCount = statement.executeUpdate(sql);
            return n;
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public int executeUpdate(String sql, List<Object> params) throws SQLException {
        Connection connection = this.createConnection();
        PreparedStatement statement = null;
        try {
            statement = this.getPreparedStatement(connection, sql, params);
            int n = this.updateCount = statement.executeUpdate();
            return n;
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public int executeUpdate(Map params, String sql) throws SQLException {
        return this.executeUpdate(sql, Sql.singletonList(params));
    }

    public int executeUpdate(String sql, Object[] params) throws SQLException {
        return this.executeUpdate(sql, Arrays.asList(params));
    }

    public int executeUpdate(GString gstring) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.executeUpdate(sql, params);
    }

    public int call(String sql) throws Exception {
        return this.call(sql, EMPTY_LIST);
    }

    public int call(GString gstring) throws Exception {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.call(sql, params);
    }

    public int call(String sql, List<Object> params) throws Exception {
        Connection connection = this.createConnection();
        CallableStatement statement = null;
        try {
            statement = this.getCallableStatement(connection, sql, params);
            int n = statement.executeUpdate();
            return n;
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            this.closeResources(connection, statement);
        }
    }

    public int call(String sql, Object[] params) throws Exception {
        return this.call(sql, Arrays.asList(params));
    }

    public void call(String sql, List<Object> params, Closure closure) throws Exception {
        this.callWithRows(sql, params, 0, closure);
    }

    public void call(GString gstring, Closure closure) throws Exception {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        this.call(sql, params, closure);
    }

    public List<GroovyRowResult> callWithRows(GString gstring, Closure closure) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.callWithRows(sql, params, closure);
    }

    public List<GroovyRowResult> callWithRows(String sql, List<Object> params, Closure closure) throws SQLException {
        return this.callWithRows(sql, params, 1, closure).get(0);
    }

    public List<List<GroovyRowResult>> callWithAllRows(GString gstring, Closure closure) throws SQLException {
        List<Object> params = this.getParameters(gstring);
        String sql = this.asSql(gstring, params);
        return this.callWithAllRows(sql, params, closure);
    }

    public List<List<GroovyRowResult>> callWithAllRows(String sql, List<Object> params, Closure closure) throws SQLException {
        return this.callWithRows(sql, params, 2, closure);
    }

    protected List<List<GroovyRowResult>> callWithRows(String sql, List<Object> params, int processResultsSets, Closure closure) throws SQLException {
        Connection connection = this.createConnection();
        CallableStatement statement = null;
        ArrayList<GroovyResultSet> resultSetResources = new ArrayList<GroovyResultSet>();
        try {
            ArrayList<List<GroovyRowResult>> arrayList;
            statement = this.getCallableStatement(connection, sql, params);
            boolean hasResultSet = statement.execute();
            ArrayList<Object> results = new ArrayList<Object>();
            int indx = 0;
            int inouts = 0;
            for (Object value : params) {
                if (value instanceof OutParameter) {
                    if (value instanceof ResultSetOutParameter) {
                        GroovyResultSet resultSet = CallResultSet.getImpl(statement, indx);
                        resultSetResources.add(resultSet);
                        results.add(resultSet);
                    } else {
                        Object o = statement.getObject(indx + 1);
                        if (o instanceof ResultSet) {
                            GroovyResultSet resultSet = new GroovyResultSetProxy((ResultSet)o).getImpl();
                            results.add(resultSet);
                            resultSetResources.add(resultSet);
                        } else {
                            results.add(o);
                        }
                    }
                    ++inouts;
                }
                ++indx;
            }
            closure.call(results.toArray(new Object[inouts]));
            ArrayList<List<GroovyRowResult>> resultSets = new ArrayList<List<GroovyRowResult>>();
            if (processResultsSets == 0) {
                resultSets.add(new ArrayList());
                arrayList = resultSets;
                return arrayList;
            }
            if (!hasResultSet) {
                hasResultSet = statement.getMoreResults();
            }
            while (hasResultSet && processResultsSets != 0) {
                resultSets.add(this.asList(sql, statement.getResultSet()));
                if (processResultsSets == 1) break;
                hasResultSet = statement.getMoreResults();
            }
            arrayList = resultSets;
            return arrayList;
        }
        catch (SQLException e) {
            LOG.warning("Failed to execute: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            for (GroovyResultSet rs : resultSetResources) {
                this.closeResources(null, null, rs);
            }
            this.closeResources(connection, statement);
        }
    }

    public void close() {
        this.namedParamSqlCache.clear();
        this.namedParamIndexPropCache.clear();
        this.clearStatementCache();
        if (this.useConnection != null) {
            try {
                this.useConnection.close();
            }
            catch (SQLException e) {
                LOG.finest("Caught exception closing connection: " + e.getMessage());
            }
        }
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void commit() throws SQLException {
        if (this.useConnection == null) {
            LOG.info("Commit operation not supported when using datasets unless using withTransaction or cacheConnection - attempt to commit ignored");
            return;
        }
        try {
            this.useConnection.commit();
        }
        catch (SQLException e) {
            LOG.warning("Caught exception committing connection: " + e.getMessage());
            throw e;
        }
    }

    public void rollback() throws SQLException {
        if (this.useConnection == null) {
            LOG.info("Rollback operation not supported when using datasets unless using withTransaction or cacheConnection - attempt to rollback ignored");
            return;
        }
        try {
            this.useConnection.rollback();
        }
        catch (SQLException e) {
            LOG.warning("Caught exception rolling back connection: " + e.getMessage());
            throw e;
        }
    }

    public int getUpdateCount() {
        return this.updateCount;
    }

    public Connection getConnection() {
        return this.useConnection;
    }

    private void setConnection(Connection connection) {
        this.useConnection = connection;
    }

    public void withStatement(@ClosureParams(value=SimpleType.class, options={"java.sql.Statement"}) Closure configureStatement) {
        this.configureStatement = configureStatement;
    }

    public synchronized void setCacheStatements(boolean cacheStatements) {
        this.cacheStatements = cacheStatements;
        if (!cacheStatements) {
            this.clearStatementCache();
        }
    }

    public boolean isCacheStatements() {
        return this.cacheStatements;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void cacheConnection(Closure closure) throws SQLException {
        boolean savedCacheConnection = this.cacheConnection;
        this.cacheConnection = true;
        Connection connection = null;
        try {
            connection = this.createConnection();
            Sql.callClosurePossiblyWithConnection(closure, connection);
        }
        finally {
            this.cacheConnection = false;
            this.closeResources(connection, null);
            this.cacheConnection = savedCacheConnection;
            if (this.dataSource != null && !this.cacheConnection) {
                this.useConnection = null;
            }
        }
    }

    public synchronized void withTransaction(Closure closure) throws SQLException {
        boolean savedCacheConnection = this.cacheConnection;
        this.cacheConnection = true;
        Connection connection = null;
        boolean savedAutoCommit = true;
        try {
            connection = this.createConnection();
            savedAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            Sql.callClosurePossiblyWithConnection(closure, connection);
            connection.commit();
        }
        catch (SQLException e) {
            Sql.handleError(connection, e);
            throw e;
        }
        catch (RuntimeException e) {
            Sql.handleError(connection, e);
            throw e;
        }
        catch (Error e) {
            Sql.handleError(connection, e);
            throw e;
        }
        catch (Exception e) {
            Sql.handleError(connection, e);
            throw new SQLException("Unexpected exception during transaction", e);
        }
        finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(savedAutoCommit);
                }
                catch (SQLException e) {
                    LOG.finest("Caught exception resetting auto commit: " + e.getMessage() + " - continuing");
                }
            }
            this.cacheConnection = false;
            this.closeResources(connection, null);
            this.cacheConnection = savedCacheConnection;
            if (this.dataSource != null && !this.cacheConnection) {
                this.useConnection = null;
            }
        }
    }

    public boolean isWithinBatch() {
        return this.withinBatch;
    }

    public int[] withBatch(Closure closure) throws SQLException {
        return this.withBatch(0, closure);
    }

    public int[] withBatch(int batchSize, Closure closure) throws SQLException {
        int[] nArray;
        Connection connection = this.createConnection();
        BatchingStatementWrapper statement = null;
        boolean savedWithinBatch = this.withinBatch;
        try {
            this.withinBatch = true;
            statement = new BatchingStatementWrapper(this.createStatement(connection), batchSize, LOG);
            closure.call((Object)statement);
            nArray = statement.executeBatch();
        }
        catch (SQLException e) {
            try {
                LOG.warning("Error during batch execution: " + e.getMessage());
                throw e;
            }
            catch (Throwable throwable) {
                Sql.closeResources(statement);
                this.closeResources(connection);
                this.withinBatch = savedWithinBatch;
                throw throwable;
            }
        }
        Sql.closeResources(statement);
        this.closeResources(connection);
        this.withinBatch = savedWithinBatch;
        return nArray;
    }

    public int[] withBatch(String sql, Closure closure) throws SQLException {
        return this.withBatch(0, sql, closure);
    }

    public int[] withBatch(int batchSize, String sql, Closure closure) throws SQLException {
        Connection connection = this.createConnection();
        ArrayList<Tuple> indexPropList = null;
        SqlWithParams preCheck = this.buildSqlWithIndexedProps(sql);
        boolean savedWithinBatch = this.withinBatch;
        BatchingPreparedStatementWrapper psWrapper = null;
        if (preCheck != null) {
            indexPropList = new ArrayList<Tuple>();
            for (Object next : preCheck.getParams()) {
                indexPropList.add((Tuple)next);
            }
            sql = preCheck.getSql();
        }
        try {
            this.withinBatch = true;
            PreparedStatement statement = (PreparedStatement)this.getAbstractStatement(new CreatePreparedStatementCommand(0), connection, sql);
            this.configure(statement);
            psWrapper = new BatchingPreparedStatementWrapper(statement, indexPropList, batchSize, LOG, this);
            closure.call((Object)psWrapper);
            int[] nArray = psWrapper.executeBatch();
            this.closeResources(psWrapper);
            this.closeResources(connection);
            this.withinBatch = savedWithinBatch;
            return nArray;
        }
        catch (SQLException e) {
            try {
                LOG.warning("Error during batch execution of '" + sql + "' with message: " + e.getMessage());
                throw e;
            }
            catch (Throwable throwable) {
                this.closeResources(psWrapper);
                this.closeResources(connection);
                this.withinBatch = savedWithinBatch;
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void cacheStatements(Closure closure) throws SQLException {
        boolean savedCacheStatements = this.cacheStatements;
        this.cacheStatements = true;
        Connection connection = null;
        try {
            connection = this.createConnection();
            Sql.callClosurePossiblyWithConnection(closure, connection);
        }
        finally {
            this.cacheStatements = false;
            this.closeResources(connection, null);
            this.cacheStatements = savedCacheStatements;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final ResultSet executeQuery(String sql) throws SQLException {
        AbstractQueryCommand command = this.createQueryCommand(sql);
        ResultSet rs = null;
        try {
            rs = command.execute();
        }
        finally {
            command.closeResources();
        }
        return rs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final ResultSet executePreparedQuery(String sql, List<Object> params) throws SQLException {
        AbstractQueryCommand command = this.createPreparedQueryCommand(sql, params);
        ResultSet rs = null;
        try {
            rs = command.execute();
        }
        finally {
            command.closeResources();
        }
        return rs;
    }

    protected List<GroovyRowResult> asList(String sql, ResultSet rs) throws SQLException {
        return this.asList(sql, rs, null);
    }

    protected List<GroovyRowResult> asList(String sql, ResultSet rs, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        return this.asList(sql, rs, 0, 0, metaClosure);
    }

    protected List<GroovyRowResult> asList(String sql, ResultSet rs, int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"java.sql.ResultSetMetaData"}) Closure metaClosure) throws SQLException {
        ArrayList<GroovyRowResult> results = new ArrayList<GroovyRowResult>();
        try {
            boolean cursorAtRow;
            if (metaClosure != null) {
                metaClosure.call((Object)rs.getMetaData());
            }
            if (!(cursorAtRow = Sql.moveCursor(rs, offset))) {
                List<GroovyRowResult> list = null;
                return list;
            }
            int i = 0;
            while ((maxRows <= 0 || i++ < maxRows) && rs.next()) {
                results.add(SqlGroovyMethods.toRowResult(rs));
            }
            ArrayList<GroovyRowResult> arrayList = results;
            return arrayList;
        }
        catch (SQLException e) {
            LOG.warning("Failed to retrieve row from ResultSet for: " + sql + " because: " + e.getMessage());
            throw e;
        }
        finally {
            rs.close();
        }
    }

    protected String asSql(GString gstring, List<Object> values) {
        String[] strings = gstring.getStrings();
        if (strings.length <= 0) {
            throw new IllegalArgumentException("No SQL specified in GString: " + gstring);
        }
        boolean nulls = false;
        StringBuilder buffer = new StringBuilder();
        boolean warned = false;
        Iterator<Object> iter = values.iterator();
        for (int i = 0; i < strings.length; ++i) {
            String text = strings[i];
            if (text != null) {
                buffer.append(text);
            }
            if (!iter.hasNext()) continue;
            Object value = iter.next();
            if (value != null) {
                if (value instanceof ExpandedVariable) {
                    buffer.append(((ExpandedVariable)value).getObject());
                    iter.remove();
                    continue;
                }
                boolean validBinding = true;
                if (i < strings.length - 1) {
                    String nextText = strings[i + 1];
                    if ((text.endsWith("\"") || text.endsWith("'")) && (nextText.startsWith("'") || nextText.startsWith("\""))) {
                        if (!warned) {
                            LOG.warning("In Groovy SQL please do not use quotes around dynamic expressions (which start with $) as this means we cannot use a JDBC PreparedStatement and so is a security hole. Groovy has worked around your mistake but the security hole is still there. The expression so far is: " + buffer.toString() + "?" + nextText);
                            warned = true;
                        }
                        buffer.append(value);
                        iter.remove();
                        validBinding = false;
                    }
                }
                if (!validBinding) continue;
                buffer.append("?");
                continue;
            }
            nulls = true;
            iter.remove();
            buffer.append("?'\"?");
        }
        String sql = buffer.toString();
        if (nulls) {
            sql = this.nullify(sql);
        }
        return sql;
    }

    protected String nullify(String sql) {
        int firstWhere = this.findWhereKeyword(sql);
        if (firstWhere >= 0) {
            Pattern[] patterns = new Pattern[]{Pattern.compile("(?is)^(.{" + firstWhere + "}.*?)!=\\s{0,1}(\\s*)\\?'\"\\?(.*)"), Pattern.compile("(?is)^(.{" + firstWhere + "}.*?)<>\\s{0,1}(\\s*)\\?'\"\\?(.*)"), Pattern.compile("(?is)^(.{" + firstWhere + "}.*?[^<>])=\\s{0,1}(\\s*)\\?'\"\\?(.*)")};
            String[] replacements = new String[]{"$1 is not $2null$3", "$1 is not $2null$3", "$1 is $2null$3"};
            for (int i = 0; i < patterns.length; ++i) {
                Matcher matcher = patterns[i].matcher(sql);
                while (matcher.matches()) {
                    sql = matcher.replaceAll(replacements[i]);
                    matcher = patterns[i].matcher(sql);
                }
            }
        }
        return sql.replaceAll("\\?'\"\\?", "null");
    }

    protected int findWhereKeyword(String sql) {
        char[] chars = sql.toLowerCase().toCharArray();
        char[] whereChars = "where".toCharArray();
        boolean inString = false;
        int inWhere = 0;
        block3: for (int i = 0; i < chars.length; ++i) {
            switch (chars[i]) {
                case '\'': {
                    inString = !inString;
                    continue block3;
                }
                default: {
                    if (!inString && chars[i] == whereChars[inWhere]) {
                        if (++inWhere != whereChars.length) continue block3;
                        return i;
                    }
                    inWhere = 0;
                }
            }
        }
        return -1;
    }

    protected List<Object> getParameters(GString gstring) {
        return new ArrayList<Object>(Arrays.asList(gstring.getValues()));
    }

    protected void setParameters(List<Object> params, PreparedStatement statement) throws SQLException {
        int i = 1;
        ParameterMetaData metaData = this.getParameterMetaDataSafe(statement);
        if (metaData != null) {
            Map paramsMap;
            if (metaData.getParameterCount() == 0 && params.size() == 1 && params.get(0) instanceof Map && (paramsMap = (Map)params.get(0)).isEmpty()) {
                return;
            }
            if (metaData.getParameterCount() != params.size()) {
                LOG.warning("Found " + metaData.getParameterCount() + " parameter placeholders but supplied with " + params.size() + " parameters");
            }
        }
        for (Object value : params) {
            this.setObject(statement, i++, value);
        }
    }

    private ParameterMetaData getParameterMetaDataSafe(PreparedStatement statement) throws SQLException {
        if (!this.enableMetaDataChecking) {
            return null;
        }
        try {
            return statement.getParameterMetaData();
        }
        catch (SQLException se) {
            LOG.fine("Unable to retrieve parameter metadata - reduced checking will occur: " + se.getMessage());
            return null;
        }
    }

    protected void setObject(PreparedStatement statement, int i, Object value) throws SQLException {
        if (value instanceof InParameter || value instanceof OutParameter) {
            if (value instanceof InParameter) {
                InParameter in = (InParameter)value;
                Object val = in.getValue();
                if (null == val) {
                    statement.setNull(i, in.getType());
                } else {
                    statement.setObject(i, val, in.getType());
                }
            }
            if (value instanceof OutParameter) {
                try {
                    OutParameter out = (OutParameter)value;
                    ((CallableStatement)statement).registerOutParameter(i, out.getType());
                }
                catch (ClassCastException e) {
                    throw new SQLException("Cannot register out parameter.");
                }
            }
        } else {
            try {
                statement.setObject(i, value);
            }
            catch (SQLException e) {
                if (value == null) {
                    SQLException se = new SQLException("Your JDBC driver may not support null arguments for setObject. Consider using Groovy's InParameter feature." + (e.getMessage() == null ? "" : " (CAUSE: " + e.getMessage() + ")"));
                    se.setNextException(e);
                    throw se;
                }
                throw e;
            }
        }
    }

    protected Connection createConnection() throws SQLException {
        if ((this.cacheStatements || this.cacheConnection) && this.useConnection != null) {
            return this.useConnection;
        }
        if (this.dataSource != null) {
            Connection con;
            try {
                con = AccessController.doPrivileged(new PrivilegedExceptionAction<Connection>(){

                    @Override
                    public Connection run() throws SQLException {
                        return Sql.this.dataSource.getConnection();
                    }
                });
            }
            catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof SQLException) {
                    throw (SQLException)e;
                }
                throw (RuntimeException)e;
            }
            if (this.cacheStatements || this.cacheConnection) {
                this.useConnection = con;
            }
            return con;
        }
        return this.useConnection;
    }

    protected void closeResources(Connection connection, Statement statement, ResultSet results) {
        if (results != null) {
            try {
                results.close();
            }
            catch (SQLException e) {
                LOG.finest("Caught exception closing resultSet: " + e.getMessage() + " - continuing");
            }
        }
        this.closeResources(connection, statement);
    }

    protected void closeResources(Connection connection, Statement statement) {
        if (this.cacheStatements) {
            return;
        }
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException e) {
                LOG.finest("Caught exception closing statement: " + e.getMessage() + " - continuing");
            }
        }
        this.closeResources(connection);
    }

    private void closeResources(BatchingPreparedStatementWrapper statement) {
        if (this.cacheStatements) {
            return;
        }
        Sql.closeResources(statement);
    }

    private static void closeResources(BatchingStatementWrapper statement) {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException e) {
                LOG.finest("Caught exception closing statement: " + e.getMessage() + " - continuing");
            }
        }
    }

    protected void closeResources(Connection connection) {
        if (this.cacheConnection) {
            return;
        }
        if (connection != null && this.dataSource != null) {
            try {
                connection.close();
            }
            catch (SQLException e) {
                LOG.finest("Caught exception closing connection: " + e.getMessage() + " - continuing");
            }
        }
    }

    protected void configure(Statement statement) {
        Closure configureStatement = this.configureStatement;
        if (configureStatement != null) {
            configureStatement.call((Object)statement);
        }
    }

    private static List<List<Object>> calculateKeys(ResultSet keys) throws SQLException {
        ArrayList<List<Object>> autoKeys = new ArrayList<List<Object>>();
        int count = keys.getMetaData().getColumnCount();
        while (keys.next()) {
            ArrayList<Object> rowKeys = new ArrayList<Object>(count);
            for (int i = 1; i <= count; ++i) {
                rowKeys.add(keys.getObject(i));
            }
            autoKeys.add(rowKeys);
        }
        return autoKeys;
    }

    private Statement createStatement(Connection connection) throws SQLException {
        if (this.resultSetHoldability == -1) {
            return connection.createStatement(this.resultSetType, this.resultSetConcurrency);
        }
        return connection.createStatement(this.resultSetType, this.resultSetConcurrency, this.resultSetHoldability);
    }

    private static void handleError(Connection connection, Throwable t) throws SQLException {
        if (connection != null) {
            LOG.warning("Rolling back due to: " + t.getMessage());
            connection.rollback();
        }
    }

    private static void callClosurePossiblyWithConnection(Closure closure, Connection connection) {
        if (closure.getMaximumNumberOfParameters() == 1) {
            closure.call((Object)connection);
        } else {
            closure.call();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clearStatementCache() {
        Statement[] statementArray = this.statementCache;
        synchronized (this.statementCache) {
            if (this.statementCache.isEmpty()) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
            Statement[] statements = new Statement[this.statementCache.size()];
            this.statementCache.values().toArray(statements);
            this.statementCache.clear();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            for (Statement s : statements) {
                try {
                    s.close();
                }
                catch (Exception e) {
                    LOG.info("Failed to close statement. Already closed? Exception message: " + e.getMessage());
                }
            }
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Statement getAbstractStatement(AbstractStatementCommand cmd, Connection connection, String sql) throws SQLException {
        Statement stmt;
        if (this.cacheStatements) {
            Map<String, Statement> map = this.statementCache;
            synchronized (map) {
                stmt = this.statementCache.get(sql);
                if (stmt == null) {
                    stmt = cmd.execute(connection, sql);
                    this.statementCache.put(sql, stmt);
                }
            }
        } else {
            stmt = cmd.execute(connection, sql);
        }
        return stmt;
    }

    private Statement getStatement(Connection connection, String sql) throws SQLException {
        LOG.fine(sql);
        Statement stmt = this.getAbstractStatement(new CreateStatementCommand(), connection, sql);
        this.configure(stmt);
        return stmt;
    }

    private PreparedStatement getPreparedStatement(Connection connection, String sql, List<Object> params, int returnGeneratedKeys) throws SQLException {
        SqlWithParams updated = this.checkForNamedParams(sql, params);
        LOG.fine(updated.getSql() + " | " + updated.getParams());
        PreparedStatement statement = (PreparedStatement)this.getAbstractStatement(new CreatePreparedStatementCommand(returnGeneratedKeys), connection, updated.getSql());
        this.setParameters(updated.getParams(), statement);
        this.configure(statement);
        return statement;
    }

    private CallableStatement getCallableStatement(Connection connection, String sql, List<Object> params) throws SQLException {
        LOG.fine(sql + " | " + params);
        CallableStatement statement = (CallableStatement)this.getAbstractStatement(new CreateCallableStatementCommand(), connection, sql);
        this.setParameters(params, statement);
        this.configure(statement);
        return statement;
    }

    public SqlWithParams checkForNamedParams(String sql, List<Object> params) {
        SqlWithParams preCheck = this.buildSqlWithIndexedProps(sql);
        if (preCheck == null) {
            return new SqlWithParams(sql, params);
        }
        ArrayList<Tuple> indexPropList = new ArrayList<Tuple>();
        for (Object next : preCheck.getParams()) {
            indexPropList.add((Tuple)next);
        }
        return new SqlWithParams(preCheck.getSql(), this.getUpdatedParams(params, indexPropList));
    }

    @Deprecated
    public SqlWithParams preCheckForNamedParams(String sql) {
        return this.buildSqlWithIndexedProps(sql);
    }

    protected SqlWithParams buildSqlWithIndexedProps(String sql) {
        List<Tuple> propList;
        String newSql;
        if (!this.enableNamedQueries || !ExtractIndexAndSql.hasNamedParameters(sql)) {
            return null;
        }
        if (this.cacheNamedQueries && this.namedParamSqlCache.containsKey(sql)) {
            newSql = this.namedParamSqlCache.get(sql);
            propList = this.namedParamIndexPropCache.get(sql);
        } else {
            ExtractIndexAndSql extractIndexAndSql = ExtractIndexAndSql.from(sql);
            newSql = extractIndexAndSql.getNewSql();
            propList = extractIndexAndSql.getIndexPropList();
            this.namedParamSqlCache.put(sql, newSql);
            this.namedParamIndexPropCache.put(sql, propList);
        }
        if (sql.equals(newSql)) {
            return null;
        }
        ArrayList<Object> indexPropList = new ArrayList<Object>(propList);
        return new SqlWithParams(newSql, indexPropList);
    }

    public List<Object> getUpdatedParams(List<Object> params, List<Tuple> indexPropList) {
        ArrayList<Object> updatedParams = new ArrayList<Object>();
        for (Tuple tuple : indexPropList) {
            int index = (Integer)tuple.get(0);
            String prop = (String)tuple.get(1);
            if (index < 0 || index >= params.size()) {
                throw new IllegalArgumentException("Invalid index " + index + " should be in range 1.." + params.size());
            }
            try {
                updatedParams.add(prop.equals("<this>") ? params.get(index) : InvokerHelper.getProperty(params.get(index), prop));
            }
            catch (MissingPropertyException mpe) {
                throw new IllegalArgumentException("Property '" + prop + "' not found for parameter " + index);
            }
        }
        return updatedParams;
    }

    private PreparedStatement getPreparedStatement(Connection connection, String sql, List<Object> params) throws SQLException {
        return this.getPreparedStatement(connection, sql, params, 0);
    }

    public boolean isCacheNamedQueries() {
        return this.cacheNamedQueries;
    }

    public void setCacheNamedQueries(boolean cacheNamedQueries) {
        this.cacheNamedQueries = cacheNamedQueries;
    }

    public boolean isEnableNamedQueries() {
        return this.enableNamedQueries;
    }

    public void setEnableNamedQueries(boolean enableNamedQueries) {
        this.enableNamedQueries = enableNamedQueries;
    }

    protected AbstractQueryCommand createQueryCommand(String sql) {
        return new QueryCommand(sql);
    }

    protected AbstractQueryCommand createPreparedQueryCommand(String sql, List<Object> queryParams) {
        return new PreparedQueryCommand(sql, queryParams);
    }

    protected void setInternalConnection(Connection conn) {
    }

    private final class QueryCommand
    extends AbstractQueryCommand {
        private QueryCommand(String sql2) {
            super(sql2);
        }

        @Override
        protected ResultSet runQuery(Connection connection) throws SQLException {
            this.statement = Sql.this.getStatement(connection, this.sql);
            if (this.getMaxRows() != 0) {
                this.statement.setMaxRows(this.getMaxRows());
            }
            return this.statement.executeQuery(this.sql);
        }
    }

    private final class PreparedQueryCommand
    extends AbstractQueryCommand {
        private List<Object> params;

        private PreparedQueryCommand(String sql2, List<Object> queryParams) {
            super(sql2);
            this.params = queryParams;
        }

        @Override
        protected ResultSet runQuery(Connection connection) throws SQLException {
            PreparedStatement s = Sql.this.getPreparedStatement(connection, this.sql, this.params);
            this.statement = s;
            if (this.getMaxRows() != 0) {
                this.statement.setMaxRows(this.getMaxRows());
            }
            return s.executeQuery();
        }
    }

    protected abstract class AbstractQueryCommand {
        protected final String sql;
        protected Statement statement;
        private Connection connection;
        private int maxRows = 0;

        protected AbstractQueryCommand(String sql) {
            this.sql = sql;
        }

        protected final ResultSet execute() throws SQLException {
            this.connection = Sql.this.createConnection();
            Sql.this.setInternalConnection(this.connection);
            this.statement = null;
            try {
                ResultSet result = this.runQuery(this.connection);
                assert (null != this.statement);
                return result;
            }
            catch (SQLException e) {
                LOG.warning("Failed to execute: " + this.sql + " because: " + e.getMessage());
                this.closeResources();
                this.connection = null;
                this.statement = null;
                throw e;
            }
        }

        protected final void closeResources() {
            Sql.this.closeResources(this.connection, this.statement);
        }

        protected final void closeResources(ResultSet rs) {
            Sql.this.closeResources(this.connection, this.statement, rs);
        }

        protected abstract ResultSet runQuery(Connection var1) throws SQLException;

        protected void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        protected int getMaxRows() {
            return this.maxRows;
        }
    }

    private class CreateStatementCommand
    extends AbstractStatementCommand {
        private CreateStatementCommand() {
        }

        @Override
        protected Statement execute(Connection conn, String sql) throws SQLException {
            return Sql.this.createStatement(conn);
        }
    }

    private class CreateCallableStatementCommand
    extends AbstractStatementCommand {
        private CreateCallableStatementCommand() {
        }

        @Override
        protected CallableStatement execute(Connection connection, String sql) throws SQLException {
            return connection.prepareCall(sql);
        }
    }

    private class CreatePreparedStatementCommand
    extends AbstractStatementCommand {
        private final int returnGeneratedKeys;

        private CreatePreparedStatementCommand(int returnGeneratedKeys) {
            this.returnGeneratedKeys = returnGeneratedKeys;
        }

        @Override
        protected PreparedStatement execute(Connection connection, String sql) throws SQLException {
            if (this.returnGeneratedKeys == -1 && Sql.this.keyColumnNames != null) {
                return connection.prepareStatement(sql, Sql.this.keyColumnNames.toArray(new String[Sql.this.keyColumnNames.size()]));
            }
            if (this.returnGeneratedKeys != 0) {
                return connection.prepareStatement(sql, this.returnGeneratedKeys);
            }
            if (this.appearsLikeStoredProc(sql)) {
                if (Sql.this.resultSetHoldability == -1) {
                    return connection.prepareCall(sql, Sql.this.resultSetType, Sql.this.resultSetConcurrency);
                }
                return connection.prepareCall(sql, Sql.this.resultSetType, Sql.this.resultSetConcurrency, Sql.this.resultSetHoldability);
            }
            if (Sql.this.resultSetHoldability == -1) {
                return connection.prepareStatement(sql, Sql.this.resultSetType, Sql.this.resultSetConcurrency);
            }
            return connection.prepareStatement(sql, Sql.this.resultSetType, Sql.this.resultSetConcurrency, Sql.this.resultSetHoldability);
        }

        private boolean appearsLikeStoredProc(String sql) {
            return sql.matches("\\s*[{]?\\s*[?]?\\s*[=]?\\s*[cC][aA][lL][lL].*");
        }
    }

    private abstract class AbstractStatementCommand {
        private AbstractStatementCommand() {
        }

        protected abstract Statement execute(Connection var1, String var2) throws SQLException;
    }
}

