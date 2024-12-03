/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc.internal;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.jdbc.ResourceRegistry;
import org.hibernate.resource.jdbc.spi.JdbcObserver;

public final class ResourceRegistryStandardImpl
implements ResourceRegistry {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(ResourceRegistryStandardImpl.class);
    private static final Object PRESENT = new Object();
    private static final HashMap<ResultSet, Object> EMPTY = new HashMap(1, 0.2f);
    private final JdbcObserver jdbcObserver;
    private final HashMap<Statement, HashMap<ResultSet, Object>> xref = new HashMap();
    private HashMap<ResultSet, Object> unassociatedResultSets;
    private ArrayList<Blob> blobs;
    private ArrayList<Clob> clobs;
    private ArrayList<NClob> nclobs;
    private Statement lastQuery;

    public ResourceRegistryStandardImpl() {
        this(null);
    }

    public ResourceRegistryStandardImpl(JdbcObserver jdbcObserver) {
        this.jdbcObserver = jdbcObserver;
    }

    @Override
    public boolean hasRegisteredResources() {
        return this.hasRegistered(this.xref) || this.hasRegistered(this.unassociatedResultSets) || this.hasRegistered(this.blobs) || this.hasRegistered(this.clobs) || this.hasRegistered(this.nclobs);
    }

    @Override
    public void register(Statement statement, boolean cancelable) {
        log.tracef("Registering statement [%s]", statement);
        HashMap<ResultSet, Object> previousValue = this.xref.putIfAbsent(statement, EMPTY);
        if (previousValue != null) {
            throw new HibernateException("JDBC Statement already registered");
        }
        if (cancelable) {
            this.lastQuery = statement;
        }
    }

    @Override
    public void release(Statement statement) {
        log.tracev("Releasing statement [{0}]", statement);
        HashMap<ResultSet, Object> resultSets = this.xref.remove(statement);
        if (resultSets != null) {
            ResourceRegistryStandardImpl.closeAll(resultSets);
        } else {
            log.unregisteredStatement();
        }
        ResourceRegistryStandardImpl.close(statement);
        if (this.lastQuery == statement) {
            this.lastQuery = null;
        }
    }

    @Override
    public void release(ResultSet resultSet, Statement statement) {
        log.tracef("Releasing result set [%s]", resultSet);
        if (statement == null) {
            try {
                statement = resultSet.getStatement();
            }
            catch (SQLException e) {
                throw this.convert(e, "unable to access Statement from ResultSet");
            }
        }
        if (statement != null) {
            HashMap<ResultSet, Object> resultSets = this.xref.get(statement);
            if (resultSets == null) {
                log.unregisteredStatement();
            } else {
                resultSets.remove(resultSet);
                if (resultSets.isEmpty()) {
                    this.xref.remove(statement);
                }
            }
        } else {
            Object removed;
            Object object = removed = this.unassociatedResultSets == null ? null : this.unassociatedResultSets.remove(resultSet);
            if (removed == null) {
                log.unregisteredResultSetWithoutStatement();
            }
        }
        ResourceRegistryStandardImpl.close(resultSet);
    }

    private static void closeAll(HashMap<ResultSet, Object> resultSets) {
        if (resultSets == null) {
            return;
        }
        resultSets.forEach((resultSet, o) -> ResourceRegistryStandardImpl.close(resultSet));
        resultSets.clear();
    }

    private static void releaseXref(Statement s, HashMap<ResultSet, Object> r) {
        ResourceRegistryStandardImpl.closeAll(r);
        ResourceRegistryStandardImpl.close(s);
    }

    private static void close(ResultSet resultSet) {
        log.tracef("Closing result set [%s]", resultSet);
        try {
            resultSet.close();
        }
        catch (SQLException e) {
            log.debugf("Unable to release JDBC result set [%s]", e.getMessage());
        }
        catch (Exception e) {
            log.debugf("Unable to release JDBC result set [%s]", e.getMessage());
        }
    }

    public static void close(Statement statement) {
        log.tracef("Closing prepared statement [%s]", statement);
        try {
            try {
                if (statement.getMaxRows() != 0) {
                    statement.setMaxRows(0);
                }
                if (statement.getQueryTimeout() != 0) {
                    statement.setQueryTimeout(0);
                }
            }
            catch (SQLException sqle) {
                if (log.isDebugEnabled()) {
                    log.debugf("Exception clearing maxRows/queryTimeout [%s]", sqle.getMessage());
                }
                return;
            }
            statement.close();
        }
        catch (SQLException e) {
            log.debugf("Unable to release JDBC statement [%s]", e.getMessage());
        }
        catch (Exception e) {
            log.debugf("Unable to release JDBC statement [%s]", e.getMessage());
        }
    }

    @Override
    public void register(ResultSet resultSet, Statement statement) {
        log.tracef("Registering result set [%s]", resultSet);
        if (statement == null) {
            try {
                statement = resultSet.getStatement();
            }
            catch (SQLException e) {
                throw this.convert(e, "unable to access Statement from ResultSet");
            }
        }
        if (statement != null) {
            HashMap<ResultSet, Object> resultSets = this.xref.get(statement);
            if (resultSets == null) {
                log.debug("ResultSet statement was not registered (on register)");
            }
            if (resultSets == null || resultSets == EMPTY) {
                resultSets = new HashMap();
                this.xref.put(statement, resultSets);
            }
            resultSets.put(resultSet, PRESENT);
        } else {
            if (this.unassociatedResultSets == null) {
                this.unassociatedResultSets = new HashMap();
            }
            this.unassociatedResultSets.put(resultSet, PRESENT);
        }
    }

    private JDBCException convert(SQLException e, String s) {
        return new JDBCException(s, e);
    }

    @Override
    public void register(Blob blob) {
        if (this.blobs == null) {
            this.blobs = new ArrayList();
        }
        this.blobs.add(blob);
    }

    @Override
    public void release(Blob blob) {
        if (this.blobs == null) {
            log.debug("Request to release Blob, but appears no Blobs have ever been registered");
            return;
        }
        this.blobs.remove(blob);
    }

    @Override
    public void register(Clob clob) {
        if (this.clobs == null) {
            this.clobs = new ArrayList();
        }
        this.clobs.add(clob);
    }

    @Override
    public void release(Clob clob) {
        if (this.clobs == null) {
            log.debug("Request to release Clob, but appears no Clobs have ever been registered");
            return;
        }
        this.clobs.remove(clob);
    }

    @Override
    public void register(NClob nclob) {
        if (this.nclobs == null) {
            this.nclobs = new ArrayList();
        }
        this.nclobs.add(nclob);
    }

    @Override
    public void release(NClob nclob) {
        if (this.nclobs == null) {
            log.debug("Request to release NClob, but appears no NClobs have ever been registered");
            return;
        }
        this.nclobs.remove(nclob);
    }

    @Override
    public void cancelLastQuery() {
        try {
            if (this.lastQuery != null) {
                this.lastQuery.cancel();
            }
        }
        catch (SQLException e) {
            throw this.convert(e, "Cannot cancel query");
        }
        finally {
            this.lastQuery = null;
        }
    }

    @Override
    public void releaseResources() {
        log.trace("Releasing JDBC resources");
        if (this.jdbcObserver != null) {
            this.jdbcObserver.jdbcReleaseRegistryResourcesStart();
        }
        this.xref.forEach(ResourceRegistryStandardImpl::releaseXref);
        this.xref.clear();
        ResourceRegistryStandardImpl.closeAll(this.unassociatedResultSets);
        if (this.blobs != null) {
            this.blobs.forEach(blob -> {
                try {
                    blob.free();
                }
                catch (SQLException e) {
                    log.debugf("Unable to free JDBC Blob reference [%s]", e.getMessage());
                }
            });
            this.blobs = null;
        }
        if (this.clobs != null) {
            this.clobs.forEach(clob -> {
                try {
                    clob.free();
                }
                catch (SQLException e) {
                    log.debugf("Unable to free JDBC Clob reference [%s]", e.getMessage());
                }
            });
            this.clobs = null;
        }
        if (this.nclobs != null) {
            this.nclobs.forEach(nclob -> {
                try {
                    nclob.free();
                }
                catch (SQLException e) {
                    log.debugf("Unable to free JDBC NClob reference [%s]", e.getMessage());
                }
            });
            this.nclobs = null;
        }
    }

    private boolean hasRegistered(HashMap resource) {
        return resource != null && !resource.isEmpty();
    }

    private boolean hasRegistered(ArrayList resource) {
        return resource != null && !resource.isEmpty();
    }
}

