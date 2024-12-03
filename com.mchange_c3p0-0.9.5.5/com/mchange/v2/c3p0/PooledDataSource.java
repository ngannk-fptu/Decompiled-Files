/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import javax.sql.DataSource;

public interface PooledDataSource
extends DataSource {
    public String getIdentityToken();

    public String getDataSourceName();

    public void setDataSourceName(String var1);

    public Map getExtensions();

    public void setExtensions(Map var1);

    public int getNumConnections() throws SQLException;

    public int getNumIdleConnections() throws SQLException;

    public int getNumBusyConnections() throws SQLException;

    public int getNumUnclosedOrphanedConnections() throws SQLException;

    public int getNumConnectionsDefaultUser() throws SQLException;

    public int getNumIdleConnectionsDefaultUser() throws SQLException;

    public int getNumBusyConnectionsDefaultUser() throws SQLException;

    public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException;

    public int getStatementCacheNumStatementsDefaultUser() throws SQLException;

    public int getStatementCacheNumCheckedOutDefaultUser() throws SQLException;

    public int getStatementCacheNumConnectionsWithCachedStatementsDefaultUser() throws SQLException;

    public long getStartTimeMillisDefaultUser() throws SQLException;

    public long getUpTimeMillisDefaultUser() throws SQLException;

    public long getNumFailedCheckinsDefaultUser() throws SQLException;

    public long getNumFailedCheckoutsDefaultUser() throws SQLException;

    public long getNumFailedIdleTestsDefaultUser() throws SQLException;

    public float getEffectivePropertyCycleDefaultUser() throws SQLException;

    public int getNumThreadsAwaitingCheckoutDefaultUser() throws SQLException;

    public void softResetDefaultUser() throws SQLException;

    public int getNumConnections(String var1, String var2) throws SQLException;

    public int getNumIdleConnections(String var1, String var2) throws SQLException;

    public int getNumBusyConnections(String var1, String var2) throws SQLException;

    public int getNumUnclosedOrphanedConnections(String var1, String var2) throws SQLException;

    public int getStatementCacheNumStatements(String var1, String var2) throws SQLException;

    public int getStatementCacheNumCheckedOut(String var1, String var2) throws SQLException;

    public int getStatementCacheNumConnectionsWithCachedStatements(String var1, String var2) throws SQLException;

    public float getEffectivePropertyCycle(String var1, String var2) throws SQLException;

    public int getNumThreadsAwaitingCheckout(String var1, String var2) throws SQLException;

    public void softReset(String var1, String var2) throws SQLException;

    public int getNumBusyConnectionsAllUsers() throws SQLException;

    public int getNumIdleConnectionsAllUsers() throws SQLException;

    public int getNumConnectionsAllUsers() throws SQLException;

    public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException;

    public int getStatementCacheNumStatementsAllUsers() throws SQLException;

    public int getStatementCacheNumCheckedOutStatementsAllUsers() throws SQLException;

    public int getStatementCacheNumConnectionsWithCachedStatementsAllUsers() throws SQLException;

    public int getStatementDestroyerNumConnectionsInUseAllUsers() throws SQLException;

    public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers() throws SQLException;

    public int getStatementDestroyerNumDeferredDestroyStatementsAllUsers() throws SQLException;

    public int getThreadPoolSize() throws SQLException;

    public int getThreadPoolNumActiveThreads() throws SQLException;

    public int getThreadPoolNumIdleThreads() throws SQLException;

    public int getThreadPoolNumTasksPending() throws SQLException;

    public int getStatementDestroyerNumThreads() throws SQLException;

    public int getStatementDestroyerNumActiveThreads() throws SQLException;

    public int getStatementDestroyerNumIdleThreads() throws SQLException;

    public int getStatementDestroyerNumTasksPending() throws SQLException;

    public String sampleThreadPoolStackTraces() throws SQLException;

    public String sampleThreadPoolStatus() throws SQLException;

    public String sampleStatementDestroyerStackTraces() throws SQLException;

    public String sampleStatementDestroyerStatus() throws SQLException;

    public String sampleStatementCacheStatusDefaultUser() throws SQLException;

    public String sampleStatementCacheStatus(String var1, String var2) throws SQLException;

    public Throwable getLastAcquisitionFailureDefaultUser() throws SQLException;

    public Throwable getLastCheckinFailureDefaultUser() throws SQLException;

    public Throwable getLastCheckoutFailureDefaultUser() throws SQLException;

    public Throwable getLastIdleTestFailureDefaultUser() throws SQLException;

    public Throwable getLastConnectionTestFailureDefaultUser() throws SQLException;

    public int getStatementDestroyerNumConnectionsInUseDefaultUser() throws SQLException;

    public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser() throws SQLException;

    public int getStatementDestroyerNumDeferredDestroyStatementsDefaultUser() throws SQLException;

    public Throwable getLastAcquisitionFailure(String var1, String var2) throws SQLException;

    public Throwable getLastCheckinFailure(String var1, String var2) throws SQLException;

    public Throwable getLastCheckoutFailure(String var1, String var2) throws SQLException;

    public Throwable getLastIdleTestFailure(String var1, String var2) throws SQLException;

    public Throwable getLastConnectionTestFailure(String var1, String var2) throws SQLException;

    public int getStatementDestroyerNumConnectionsInUse(String var1, String var2) throws SQLException;

    public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatements(String var1, String var2) throws SQLException;

    public int getStatementDestroyerNumDeferredDestroyStatements(String var1, String var2) throws SQLException;

    public String sampleLastAcquisitionFailureStackTraceDefaultUser() throws SQLException;

    public String sampleLastCheckinFailureStackTraceDefaultUser() throws SQLException;

    public String sampleLastCheckoutFailureStackTraceDefaultUser() throws SQLException;

    public String sampleLastIdleTestFailureStackTraceDefaultUser() throws SQLException;

    public String sampleLastConnectionTestFailureStackTraceDefaultUser() throws SQLException;

    public String sampleLastAcquisitionFailureStackTrace(String var1, String var2) throws SQLException;

    public String sampleLastCheckinFailureStackTrace(String var1, String var2) throws SQLException;

    public String sampleLastCheckoutFailureStackTrace(String var1, String var2) throws SQLException;

    public String sampleLastIdleTestFailureStackTrace(String var1, String var2) throws SQLException;

    public String sampleLastConnectionTestFailureStackTrace(String var1, String var2) throws SQLException;

    public void softResetAllUsers() throws SQLException;

    public int getNumUserPools() throws SQLException;

    public int getNumHelperThreads() throws SQLException;

    public Collection getAllUsers() throws SQLException;

    public void hardReset() throws SQLException;

    public void close() throws SQLException;

    public void close(boolean var1) throws SQLException;
}

