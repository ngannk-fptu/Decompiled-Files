/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import groovy.sql.BatchingPreparedStatementWrapper;
import groovy.sql.Sql;
import groovy.sql.SqlOrderByVisitor;
import groovy.sql.SqlWhereVisitor;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.MethodNode;

public class DataSet
extends Sql {
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private Closure where;
    private Closure sort;
    private boolean reversed = false;
    private DataSet parent;
    private String table;
    private SqlWhereVisitor visitor;
    private SqlOrderByVisitor sortVisitor;
    private String sql;
    private List<Object> params;
    private List<Object> batchData;
    private Set<String> batchKeys;
    private Sql delegate;
    private boolean withinDataSetBatch = false;

    public DataSet(Sql sql, Class type) {
        super(sql);
        this.delegate = sql;
        String table = type.getName();
        int idx = table.lastIndexOf(46);
        if (idx > 0) {
            table = table.substring(idx + 1);
        }
        this.table = table.toLowerCase();
    }

    public DataSet(Sql sql, String table) {
        super(sql);
        this.delegate = sql;
        this.table = table;
    }

    private DataSet(DataSet parent, Closure where) {
        super(parent);
        this.delegate = parent.delegate;
        this.table = parent.table;
        this.parent = parent;
        this.where = where;
    }

    private DataSet(DataSet parent, Closure where, Closure sort) {
        super(parent);
        this.delegate = parent.delegate;
        this.table = parent.table;
        this.parent = parent;
        this.where = where;
        this.sort = sort;
    }

    private DataSet(DataSet parent) {
        super(parent);
        this.delegate = parent.delegate;
        this.table = parent.table;
        this.parent = parent;
        this.reversed = true;
    }

    @Override
    protected Connection createConnection() throws SQLException {
        return this.delegate.createConnection();
    }

    @Override
    protected void closeResources(Connection connection, Statement statement, ResultSet results) {
        this.delegate.closeResources(connection, statement, results);
    }

    @Override
    protected void closeResources(Connection connection, Statement statement) {
        this.delegate.closeResources(connection, statement);
    }

    @Override
    public void cacheConnection(Closure closure) throws SQLException {
        this.delegate.cacheConnection(closure);
    }

    @Override
    public void withTransaction(Closure closure) throws SQLException {
        this.delegate.withTransaction(closure);
    }

    @Override
    public void commit() throws SQLException {
        this.delegate.commit();
    }

    @Override
    public void rollback() throws SQLException {
        this.delegate.rollback();
    }

    @Override
    public int[] withBatch(Closure closure) throws SQLException {
        return this.withBatch(0, closure);
    }

    @Override
    public int[] withBatch(int batchSize, Closure closure) throws SQLException {
        this.batchData = new ArrayList<Object>();
        this.withinDataSetBatch = true;
        closure.call((Object)this);
        this.withinDataSetBatch = false;
        if (this.batchData.isEmpty()) {
            return EMPTY_INT_ARRAY;
        }
        Closure transformedClosure = new Closure(null){

            public void doCall(BatchingPreparedStatementWrapper stmt) throws SQLException {
                for (Object next : DataSet.this.batchData) {
                    stmt.addBatch(new Object[]{next});
                }
            }
        };
        return super.withBatch(batchSize, this.buildMapQuery(), transformedClosure);
    }

    public void add(Map<String, Object> map) throws SQLException {
        if (this.withinDataSetBatch) {
            if (this.batchData.isEmpty()) {
                this.batchKeys = map.keySet();
            } else if (!map.keySet().equals(this.batchKeys)) {
                throw new IllegalArgumentException("Inconsistent keys found for batch add!");
            }
            this.batchData.add(map);
            return;
        }
        int answer = this.executeUpdate(this.buildListQuery(map), new ArrayList<Object>(map.values()));
        if (answer != 1) {
            LOG.warning("Should have updated 1 row not " + answer + " when trying to add: " + map);
        }
    }

    private String buildListQuery(Map<String, Object> map) {
        StringBuilder buffer = new StringBuilder("insert into ");
        buffer.append(this.table);
        buffer.append(" (");
        StringBuilder paramBuffer = new StringBuilder();
        boolean first = true;
        for (String column : map.keySet()) {
            if (first) {
                first = false;
                paramBuffer.append("?");
            } else {
                buffer.append(", ");
                paramBuffer.append(", ?");
            }
            buffer.append(column);
        }
        buffer.append(") values (");
        buffer.append(paramBuffer.toString());
        buffer.append(")");
        return buffer.toString();
    }

    private String buildMapQuery() {
        StringBuilder buffer = new StringBuilder("insert into ");
        buffer.append(this.table);
        buffer.append(" (");
        StringBuilder paramBuffer = new StringBuilder();
        boolean first = true;
        for (String column : this.batchKeys) {
            if (first) {
                first = false;
                paramBuffer.append(":");
            } else {
                buffer.append(", ");
                paramBuffer.append(", :");
            }
            paramBuffer.append(column);
            buffer.append(column);
        }
        buffer.append(") values (");
        buffer.append(paramBuffer.toString());
        buffer.append(")");
        return buffer.toString();
    }

    public DataSet findAll(Closure where) {
        return new DataSet(this, where);
    }

    public DataSet sort(Closure sort) {
        return new DataSet(this, null, sort);
    }

    public DataSet reverse() {
        if (this.sort == null) {
            throw new GroovyRuntimeException("reverse() only allowed immediately after a sort()");
        }
        return new DataSet(this);
    }

    public void each(@ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(this.getSql(), this.getParameters(), closure);
    }

    public void each(int offset, int maxRows, @ClosureParams(value=SimpleType.class, options={"groovy.sql.GroovyResultSet"}) Closure closure) throws SQLException {
        this.eachRow(this.getSql(), this.getParameters(), offset, maxRows, closure);
    }

    private String getSqlWhere() {
        String whereClaus = "";
        String parentClaus = "";
        if (this.parent != null) {
            parentClaus = this.parent.getSqlWhere();
        }
        if (this.where != null) {
            whereClaus = whereClaus + this.getSqlWhereVisitor().getWhere();
        }
        if (parentClaus.length() == 0) {
            return whereClaus;
        }
        if (whereClaus.length() == 0) {
            return parentClaus;
        }
        return parentClaus + " and " + whereClaus;
    }

    private String getSqlOrderBy() {
        String sortByClaus = "";
        String parentClaus = "";
        if (this.parent != null) {
            parentClaus = this.parent.getSqlOrderBy();
        }
        if (this.reversed && parentClaus.length() > 0) {
            parentClaus = parentClaus + " DESC";
        }
        if (this.sort != null) {
            sortByClaus = sortByClaus + this.getSqlOrderByVisitor().getOrderBy();
        }
        if (parentClaus.length() == 0) {
            return sortByClaus;
        }
        if (sortByClaus.length() == 0) {
            return parentClaus;
        }
        return parentClaus + ", " + sortByClaus;
    }

    public String getSql() {
        if (this.sql == null) {
            String orderByClaus;
            this.sql = "select * from " + this.table;
            String whereClaus = this.getSqlWhere();
            if (whereClaus.length() > 0) {
                this.sql = this.sql + " where " + whereClaus;
            }
            if ((orderByClaus = this.getSqlOrderBy()).length() > 0) {
                this.sql = this.sql + " order by " + orderByClaus;
            }
        }
        return this.sql;
    }

    public List<Object> getParameters() {
        if (this.params == null) {
            this.params = new ArrayList<Object>();
            if (this.parent != null) {
                this.params.addAll(this.parent.getParameters());
            }
            this.params.addAll(this.getSqlWhereVisitor().getParameters());
        }
        return this.params;
    }

    protected SqlWhereVisitor getSqlWhereVisitor() {
        if (this.visitor == null) {
            this.visitor = new SqlWhereVisitor();
            DataSet.visit(this.where, this.visitor);
        }
        return this.visitor;
    }

    protected SqlOrderByVisitor getSqlOrderByVisitor() {
        if (this.sortVisitor == null) {
            this.sortVisitor = new SqlOrderByVisitor();
            DataSet.visit(this.sort, this.sortVisitor);
        }
        return this.sortVisitor;
    }

    private static void visit(Closure closure, CodeVisitorSupport visitor) {
        if (closure != null) {
            org.codehaus.groovy.ast.stmt.Statement statement;
            MethodNode method;
            ClassNode classNode = closure.getMetaClass().getClassNode();
            if (classNode == null) {
                throw new GroovyRuntimeException("DataSet unable to evaluate expression. AST not available for closure: " + closure.getMetaClass().getTheClass().getName() + ". Is the source code on the classpath?");
            }
            List<MethodNode> methods = classNode.getDeclaredMethods("doCall");
            if (!methods.isEmpty() && (method = methods.get(0)) != null && (statement = method.getCode()) != null) {
                statement.visit(visitor);
            }
        }
    }

    public DataSet createView(Closure criteria) {
        return new DataSet(this, criteria);
    }

    public List rows() throws SQLException {
        return this.rows(this.getSql(), this.getParameters());
    }

    public List rows(int offset, int maxRows) throws SQLException {
        return this.rows(this.getSql(), this.getParameters(), offset, maxRows);
    }

    public Object firstRow() throws SQLException {
        List rows = this.rows();
        if (rows.isEmpty()) {
            return null;
        }
        return rows.get(0);
    }

    @Override
    public void close() {
        this.delegate.close();
        super.close();
    }
}

