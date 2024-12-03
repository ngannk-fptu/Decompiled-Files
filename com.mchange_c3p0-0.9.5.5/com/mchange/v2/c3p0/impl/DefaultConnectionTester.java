/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.ResultSetUtils
 *  com.mchange.v1.db.sql.StatementUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v1.db.sql.ResultSetUtils;
import com.mchange.v1.db.sql.StatementUtils;
import com.mchange.v2.c3p0.AbstractConnectionTester;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.impl.ThreadLocalQuerylessTestRunner;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class DefaultConnectionTester
extends AbstractConnectionTester {
    private static final String PROP_KEY = "com.mchange.v2.c3p0.impl.DefaultConnectionTester.querylessTestRunner";
    private static final String IS_VALID_TIMEOUT_KEY = "com.mchange.v2.c3p0.impl.DefaultConnectionTester.isValidTimeout";
    static final MLogger logger;
    static final int IS_VALID_TIMEOUT;
    static final String CONNECTION_TESTING_URL = "http://www.mchange.com/projects/c3p0/#configuring_connection_testing";
    static final int HASH_CODE;
    static final Set INVALID_DB_STATES;
    static final QuerylessTestRunner METADATA_TABLESEARCH;
    static final QuerylessTestRunner IS_VALID;
    static final QuerylessTestRunner SWITCH;
    static final QuerylessTestRunner THREAD_LOCAL;
    private final QuerylessTestRunner querylessTestRunner;

    public static boolean probableInvalidDb(SQLException sqle) {
        return INVALID_DB_STATES.contains(sqle.getSQLState());
    }

    private static QuerylessTestRunner reflectTestRunner(String propval) {
        try {
            if (propval.indexOf(46) >= 0) {
                return (QuerylessTestRunner)Class.forName(propval).newInstance();
            }
            Field staticField = DefaultConnectionTester.class.getDeclaredField(propval);
            return (QuerylessTestRunner)staticField.get(null);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Specified QuerylessTestRunner '" + propval + "' could not be found or instantiated. Reverting to default 'SWITCH'", (Throwable)e);
            }
            return null;
        }
    }

    public DefaultConnectionTester() {
        QuerylessTestRunner reflected;
        QuerylessTestRunner defaultQuerylessTestRunner = SWITCH;
        String prop = C3P0Config.getMultiPropertiesConfig().getProperty(PROP_KEY);
        this.querylessTestRunner = prop == null ? defaultQuerylessTestRunner : ((reflected = DefaultConnectionTester.reflectTestRunner(prop.trim())) != null ? reflected : defaultQuerylessTestRunner);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    public int activeCheckConnection(Connection c, String query, Throwable[] rootCauseOutParamHolder) {
        int n;
        if (query == null) {
            return this.querylessTestRunner.activeCheckConnectionNoQuery(c, rootCauseOutParamHolder);
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery(query);
            n = 0;
        }
        catch (SQLException e) {
            block13: {
                String state;
                if (logger.isLoggable(MLevel.FINE)) {
                    logger.log(MLevel.FINE, "Connection " + c + " failed Connection test with an Exception! [query=" + query + "]", (Throwable)e);
                }
                if (rootCauseOutParamHolder != null) {
                    rootCauseOutParamHolder[0] = e;
                }
                if (!INVALID_DB_STATES.contains(state = e.getSQLState())) break block13;
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.log(MLevel.WARNING, "SQL State '" + state + "' of Exception which occurred during a Connection test (test with query '" + query + "') implies that the database is invalid, and the pool should refill itself with fresh Connections.", (Throwable)e);
                }
                int n2 = -8;
                ResultSetUtils.attemptClose((ResultSet)rs);
                StatementUtils.attemptClose((Statement)stmt);
                return n2;
            }
            int n3 = -1;
            ResultSetUtils.attemptClose((ResultSet)rs);
            StatementUtils.attemptClose((Statement)stmt);
            return n3;
        }
        catch (Exception e2) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Connection " + c + " failed Connection test with an Exception!", (Throwable)e2);
            }
            if (rootCauseOutParamHolder != null) {
                rootCauseOutParamHolder[0] = e2;
            }
            int n4 = -1;
            {
                catch (Throwable throwable) {
                    ResultSetUtils.attemptClose(rs);
                    StatementUtils.attemptClose((Statement)stmt);
                    throw throwable;
                }
            }
            ResultSetUtils.attemptClose((ResultSet)rs);
            StatementUtils.attemptClose((Statement)stmt);
            return n4;
        }
        ResultSetUtils.attemptClose((ResultSet)rs);
        StatementUtils.attemptClose((Statement)stmt);
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int statusOnException(Connection c, Throwable t, String query, Throwable[] rootCauseOutParamHolder) {
        block12: {
            block13: {
                if (logger.isLoggable(MLevel.FINER)) {
                    logger.log(MLevel.FINER, "Testing a Connection in response to an Exception:", t);
                }
                if (!(t instanceof SQLException)) break block12;
                String state = ((SQLException)t).getSQLState();
                if (!INVALID_DB_STATES.contains(state)) break block13;
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.log(MLevel.WARNING, "SQL State '" + state + "' of Exception tested by statusOnException() implies that the database is invalid, and the pool should refill itself with fresh Connections.", t);
                }
                int n = -8;
                return n;
            }
            int n = this.activeCheckConnection(c, query, rootCauseOutParamHolder);
            return n;
        }
        try {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Connection test failed because test-provoking Throwable is an unexpected, non-SQLException.", t);
            }
            if (rootCauseOutParamHolder != null) {
                rootCauseOutParamHolder[0] = t;
            }
            int state = -1;
            return state;
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Connection " + c + " failed Connection test with an Exception!", (Throwable)e);
            }
            if (rootCauseOutParamHolder != null) {
                rootCauseOutParamHolder[0] = e;
            }
            int n = -1;
            return n;
        }
    }

    private static String queryInfo(String query) {
        return query == null ? "[using Connection.isValid(...) if supported, or else traditional default query]" : "[query=" + query + "]";
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == DefaultConnectionTester.class;
    }

    @Override
    public int hashCode() {
        return HASH_CODE;
    }

    static {
        int isValidTimeout;
        block6: {
            logger = MLog.getLogger(DefaultConnectionTester.class);
            HASH_CODE = DefaultConnectionTester.class.getName().hashCode();
            METADATA_TABLESEARCH = new QuerylessTestRunner(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Loose catch block
                 */
                @Override
                public int activeCheckConnectionNoQuery(Connection c, Throwable[] rootCauseOutParamHolder) {
                    int n;
                    ResultSet rs = null;
                    try {
                        rs = c.getMetaData().getTables(null, null, "PROBABLYNOT", new String[]{"TABLE"});
                        n = 0;
                    }
                    catch (SQLException e) {
                        block12: {
                            String state;
                            if (logger.isLoggable(MLevel.FINE)) {
                                logger.log(MLevel.FINE, "Connection " + c + " failed default system-table Connection test with an Exception!", (Throwable)e);
                            }
                            if (rootCauseOutParamHolder != null) {
                                rootCauseOutParamHolder[0] = e;
                            }
                            if (!INVALID_DB_STATES.contains(state = e.getSQLState())) break block12;
                            if (logger.isLoggable(MLevel.WARNING)) {
                                logger.log(MLevel.WARNING, "SQL State '" + state + "' of Exception which occurred during a Connection test (fallback DatabaseMetaData test) implies that the database is invalid, and the pool should refill itself with fresh Connections.", (Throwable)e);
                            }
                            int n2 = -8;
                            ResultSetUtils.attemptClose((ResultSet)rs);
                            return n2;
                        }
                        int n3 = -1;
                        ResultSetUtils.attemptClose((ResultSet)rs);
                        return n3;
                    }
                    catch (Exception e2) {
                        if (logger.isLoggable(MLevel.FINE)) {
                            logger.log(MLevel.FINE, "Connection " + c + " failed default system-table Connection test with an Exception!", (Throwable)e2);
                        }
                        if (rootCauseOutParamHolder != null) {
                            rootCauseOutParamHolder[0] = e2;
                        }
                        int n4 = -1;
                        {
                            catch (Throwable throwable) {
                                ResultSetUtils.attemptClose(rs);
                                throw throwable;
                            }
                        }
                        ResultSetUtils.attemptClose((ResultSet)rs);
                        return n4;
                    }
                    ResultSetUtils.attemptClose((ResultSet)rs);
                    return n;
                }
            };
            IS_VALID = new QuerylessTestRunner(){

                @Override
                public int activeCheckConnectionNoQuery(Connection c, Throwable[] rootCauseOutParamHolder) {
                    try {
                        boolean okay = c.isValid(IS_VALID_TIMEOUT);
                        if (okay) {
                            return 0;
                        }
                        if (rootCauseOutParamHolder != null) {
                            rootCauseOutParamHolder[0] = new SQLException("Connection.isValid(" + IS_VALID_TIMEOUT + ") returned false.");
                        }
                        return -1;
                    }
                    catch (SQLException e) {
                        String state;
                        if (rootCauseOutParamHolder != null) {
                            rootCauseOutParamHolder[0] = e;
                        }
                        if (INVALID_DB_STATES.contains(state = e.getSQLState())) {
                            if (logger.isLoggable(MLevel.WARNING)) {
                                logger.log(MLevel.WARNING, "SQL State '" + state + "' of Exception which occurred during a Connection test (fallback DatabaseMetaData test) implies that the database is invalid, and the pool should refill itself with fresh Connections.", (Throwable)e);
                            }
                            return -8;
                        }
                        return -1;
                    }
                    catch (Exception e) {
                        if (rootCauseOutParamHolder != null) {
                            rootCauseOutParamHolder[0] = e;
                        }
                        return -1;
                    }
                }
            };
            SWITCH = new QuerylessTestRunner(){

                @Override
                public int activeCheckConnectionNoQuery(Connection c, Throwable[] rootCauseOutParamHolder) {
                    int out;
                    try {
                        out = IS_VALID.activeCheckConnectionNoQuery(c, rootCauseOutParamHolder);
                    }
                    catch (AbstractMethodError e) {
                        out = METADATA_TABLESEARCH.activeCheckConnectionNoQuery(c, rootCauseOutParamHolder);
                    }
                    return out;
                }
            };
            THREAD_LOCAL = new ThreadLocalQuerylessTestRunner();
            HashSet<String> temp = new HashSet<String>();
            temp.add("08001");
            temp.add("08007");
            INVALID_DB_STATES = Collections.unmodifiableSet(temp);
            isValidTimeout = -1;
            String timeoutStr = C3P0Config.getMultiPropertiesConfig().getProperty(IS_VALID_TIMEOUT_KEY);
            try {
                if (timeoutStr != null) {
                    isValidTimeout = Integer.parseInt(timeoutStr);
                }
            }
            catch (NumberFormatException e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block6;
                logger.log(MLevel.WARNING, "Could not parse value set for com.mchange.v2.c3p0.impl.DefaultConnectionTester.isValidTimeout ['" + timeoutStr + "'] into int.", (Throwable)e);
            }
        }
        if (isValidTimeout <= 0) {
            isValidTimeout = 0;
        } else if (logger.isLoggable(MLevel.INFO)) {
            logger.log(MLevel.INFO, "Connection.isValid(...) based Connection tests will timeout and fail after " + isValidTimeout + " seconds.");
        }
        IS_VALID_TIMEOUT = isValidTimeout;
    }

    public static interface QuerylessTestRunner
    extends Serializable {
        public int activeCheckConnectionNoQuery(Connection var1, Throwable[] var2);
    }
}

