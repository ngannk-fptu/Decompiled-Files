/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.coalesce.CoalesceChecker
 *  com.mchange.v2.coalesce.Coalescer
 *  com.mchange.v2.coalesce.CoalescerFactory
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 *  com.mchange.v2.util.DoubleWeakHashMap
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.ConnectionCustomizer;
import com.mchange.v2.c3p0.ConnectionTester;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.cfg.C3P0ConfigUtils;
import com.mchange.v2.c3p0.impl.C3P0Defaults;
import com.mchange.v2.c3p0.impl.IdentityTokenized;
import com.mchange.v2.c3p0.impl.IdentityTokenizedCoalesceChecker;
import com.mchange.v2.c3p0.management.ManagementCoordinator;
import com.mchange.v2.c3p0.management.NullManagementCoordinator;
import com.mchange.v2.coalesce.CoalesceChecker;
import com.mchange.v2.coalesce.Coalescer;
import com.mchange.v2.coalesce.CoalescerFactory;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import com.mchange.v2.util.DoubleWeakHashMap;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class C3P0Registry {
    private static final String MC_PARAM = "com.mchange.v2.c3p0.management.ManagementCoordinator";
    static final MLogger logger = MLog.getLogger(C3P0Registry.class);
    static boolean banner_printed = false;
    static boolean registry_mbean_registered = false;
    private static CoalesceChecker CC = IdentityTokenizedCoalesceChecker.INSTANCE;
    private static Coalescer idtCoalescer = CoalescerFactory.createCoalescer((CoalesceChecker)CC, (boolean)true, (boolean)false);
    private static Map tokensToTokenized = new DoubleWeakHashMap();
    private static HashSet unclosedPooledDataSources = new HashSet();
    private static final Map classNamesToConnectionTesters = new HashMap();
    private static final Map classNamesToConnectionCustomizers = new HashMap();
    private static ManagementCoordinator mc;

    public static void markConfigRefreshed() {
        C3P0Registry.resetConnectionTesterCache();
    }

    public static ConnectionTester getDefaultConnectionTester() {
        return C3P0Registry.getConnectionTester(C3P0Defaults.connectionTesterClassName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ConnectionTester getConnectionTester(String className) {
        try {
            Map map = classNamesToConnectionTesters;
            synchronized (map) {
                ConnectionTester out = (ConnectionTester)classNamesToConnectionTesters.get(className);
                if (out == null) {
                    out = (ConnectionTester)Class.forName(className).newInstance();
                    classNamesToConnectionTesters.put(className, out);
                }
                return out;
            }
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Could not create for find ConnectionTester with class name '" + className + "'. Using default.", (Throwable)e);
            }
            return C3P0Registry.recreateDefaultConnectionTester();
        }
    }

    private static ConnectionTester recreateDefaultConnectionTester() {
        try {
            return (ConnectionTester)Class.forName(C3P0Defaults.connectionTesterClassName()).newInstance();
        }
        catch (Exception e) {
            throw new Error("Huh? We cannot instantiate the hard-coded, default ConnectionTester? We are very broken.", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void resetConnectionTesterCache() {
        Map map = classNamesToConnectionTesters;
        synchronized (map) {
            classNamesToConnectionTesters.clear();
            classNamesToConnectionTesters.put(C3P0Defaults.connectionTesterClassName(), C3P0Registry.recreateDefaultConnectionTester());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ConnectionCustomizer getConnectionCustomizer(String className) throws SQLException {
        if (className == null || className.trim().equals("")) {
            return null;
        }
        try {
            Map map = classNamesToConnectionCustomizers;
            synchronized (map) {
                ConnectionCustomizer out = (ConnectionCustomizer)classNamesToConnectionCustomizers.get(className);
                if (out == null) {
                    out = (ConnectionCustomizer)Class.forName(className).newInstance();
                    classNamesToConnectionCustomizers.put(className, out);
                }
                return out;
            }
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Could not create for find ConnectionCustomizer with class name '" + className + "'.", (Throwable)e);
            }
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    private static void banner() {
        if (!banner_printed) {
            if (logger.isLoggable(MLevel.INFO)) {
                logger.info("Initializing c3p0-0.9.5.5 [built 11-December-2019 22:18:33 -0800; debug? true; trace: 10]");
            }
            banner_printed = true;
        }
    }

    private static void attemptRegisterRegistryMBean() {
        if (!registry_mbean_registered) {
            mc.attemptManageC3P0Registry();
            registry_mbean_registered = true;
        }
    }

    private static boolean isIncorporated(IdentityTokenized idt) {
        return tokensToTokenized.keySet().contains(idt.getIdentityToken());
    }

    private static void incorporate(IdentityTokenized idt) {
        tokensToTokenized.put(idt.getIdentityToken(), idt);
        if (idt instanceof PooledDataSource) {
            unclosedPooledDataSources.add(idt);
            mc.attemptManagePooledDataSource((PooledDataSource)((Object)idt));
        }
    }

    private static synchronized Object findTokenized(String identityToken) {
        return tokensToTokenized.get(identityToken);
    }

    public static Map extensionsForToken(String pooledDataSourceIdentityToken) throws NoSuchElementException, IllegalArgumentException {
        Object o = C3P0Registry.findTokenized(pooledDataSourceIdentityToken);
        if (o == null) {
            throw new NoSuchElementException("No object is known to be identified by token '" + pooledDataSourceIdentityToken + "'. Either it is a bad token, or the object was no longer in use and has been garbage collected.");
        }
        if (!(o instanceof PooledDataSource)) {
            throw new IllegalArgumentException("The object '" + o + "', identified by token '" + pooledDataSourceIdentityToken + "', is not a PooledDataSource and therefore cannot have extensions.");
        }
        return ((PooledDataSource)o).getExtensions();
    }

    public static synchronized IdentityTokenized reregister(IdentityTokenized idt) {
        if (idt instanceof PooledDataSource) {
            C3P0Registry.banner();
            C3P0Registry.attemptRegisterRegistryMBean();
        }
        if (idt.getIdentityToken() == null) {
            throw new RuntimeException("[c3p0 issue] The identityToken of a registered object should be set prior to registration.");
        }
        IdentityTokenized coalesceCheck = (IdentityTokenized)idtCoalescer.coalesce((Object)idt);
        if (!C3P0Registry.isIncorporated(coalesceCheck)) {
            C3P0Registry.incorporate(coalesceCheck);
        }
        return coalesceCheck;
    }

    public static synchronized void markClosed(PooledDataSource pds) {
        unclosedPooledDataSources.remove(pds);
        mc.attemptUnmanagePooledDataSource(pds);
        if (unclosedPooledDataSources.isEmpty()) {
            mc.attemptUnmanageC3P0Registry();
            registry_mbean_registered = false;
        }
    }

    public static synchronized Set getPooledDataSources() {
        return (Set)unclosedPooledDataSources.clone();
    }

    public static synchronized Set pooledDataSourcesByName(String dataSourceName) {
        HashSet<PooledDataSource> out = new HashSet<PooledDataSource>();
        for (PooledDataSource pds : unclosedPooledDataSources) {
            if (!pds.getDataSourceName().equals(dataSourceName)) continue;
            out.add(pds);
        }
        return out;
    }

    public static synchronized PooledDataSource pooledDataSourceByName(String dataSourceName) {
        for (PooledDataSource pds : unclosedPooledDataSources) {
            if (!pds.getDataSourceName().equals(dataSourceName)) continue;
            return pds;
        }
        return null;
    }

    public static synchronized Set allIdentityTokens() {
        Set out = Collections.unmodifiableSet(tokensToTokenized.keySet());
        return out;
    }

    public static synchronized Set allIdentityTokenized() {
        HashSet out = new HashSet();
        out.addAll(tokensToTokenized.values());
        return Collections.unmodifiableSet(out);
    }

    public static synchronized Set allPooledDataSources() {
        Set out = Collections.unmodifiableSet(unclosedPooledDataSources);
        return out;
    }

    public static synchronized int getNumPooledDataSources() {
        return unclosedPooledDataSources.size();
    }

    public static synchronized int getNumPoolsAllDataSources() throws SQLException {
        int count = 0;
        for (PooledDataSource pds : unclosedPooledDataSources) {
            count += pds.getNumUserPools();
        }
        return count;
    }

    public synchronized int getNumThreadsAllThreadPools() throws SQLException {
        int count = 0;
        for (PooledDataSource pds : unclosedPooledDataSources) {
            count += pds.getNumHelperThreads();
        }
        return count;
    }

    public static synchronized Map getConfigExtensionsForPooledDataSource(String identityToken) throws SQLException {
        try {
            PooledDataSource pds = (PooledDataSource)tokensToTokenized.get(identityToken);
            if (pds == null) {
                throw new SQLException("No DataSource or registered IdentityTokenized has identityToken '" + identityToken + "'.");
            }
            return pds.getExtensions();
        }
        catch (ClassCastException e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Tried to get config extensions for an entity that is not a PooledDataSource. (Extensions are available only on PooledDataSources.) Thowing SQLException.", (Throwable)e);
            }
            throw SqlUtils.toSQLException((String)"Tried to get config extensions for an entity that is not a PooledDataSource. (Extensions are available only on PooledDataSources.)", (Throwable)e);
        }
    }

    static {
        C3P0Registry.resetConnectionTesterCache();
        String userManagementCoordinator = C3P0ConfigUtils.getPropsFileConfigProperty(MC_PARAM);
        if (userManagementCoordinator != null) {
            try {
                mc = (ManagementCoordinator)Class.forName(userManagementCoordinator).newInstance();
            }
            catch (Exception e) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.log(MLevel.WARNING, "Could not instantiate user-specified ManagementCoordinator " + userManagementCoordinator + ". Using NullManagementCoordinator (c3p0 JMX management disabled!)", (Throwable)e);
                }
                mc = new NullManagementCoordinator();
            }
        } else {
            try {
                Class.forName("java.lang.management.ManagementFactory");
                mc = (ManagementCoordinator)Class.forName("com.mchange.v2.c3p0.management.ActiveManagementCoordinator").newInstance();
            }
            catch (Exception e) {
                if (logger.isLoggable(MLevel.INFO)) {
                    logger.log(MLevel.INFO, "jdk1.5 management interfaces unavailable... JMX support disabled.", (Throwable)e);
                }
                mc = new NullManagementCoordinator();
            }
        }
    }
}

