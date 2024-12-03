/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 */
package net.sf.ehcache.util;

import java.lang.reflect.InvocationTargetException;
import javax.transaction.TransactionManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.TransactionController;

public class CacheTransactionHelper {
    private static final int XA_STATUS_NO_TRANSACTION = 6;

    public static void beginTransactionIfNeeded(Ehcache cache) throws CacheException {
        try {
            switch (cache.getCacheConfiguration().getTransactionalMode()) {
                case LOCAL: {
                    TransactionController ctrl = cache.getCacheManager().getTransactionController();
                    ctrl.begin();
                    break;
                }
                case XA: 
                case XA_STRICT: {
                    TransactionManager tm = ((Cache)cache).getTransactionManagerLookup().getTransactionManager();
                    tm.getClass().getMethod("begin", new Class[0]).invoke((Object)tm, new Object[0]);
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new CacheException("error beginning transaction:" + e);
        }
    }

    public static void commitTransactionIfNeeded(Ehcache cache) throws CacheException {
        try {
            switch (cache.getCacheConfiguration().getTransactionalMode()) {
                case LOCAL: {
                    TransactionController ctrl = cache.getCacheManager().getTransactionController();
                    ctrl.commit();
                    break;
                }
                case XA: 
                case XA_STRICT: {
                    TransactionManager tm = ((Cache)cache).getTransactionManagerLookup().getTransactionManager();
                    tm.getClass().getMethod("commit", new Class[0]).invoke((Object)tm, new Object[0]);
                    break;
                }
            }
        }
        catch (Exception e) {
            Throwable t = e;
            if (t instanceof InvocationTargetException) {
                t = ((InvocationTargetException)e).getCause();
            }
            throw new CacheException("error committing transaction: " + t);
        }
    }

    public static boolean isTransactionStarted(Ehcache cache) throws CacheException {
        try {
            switch (cache.getCacheConfiguration().getTransactionalMode()) {
                case LOCAL: {
                    TransactionController ctrl = cache.getCacheManager().getTransactionController();
                    return ctrl.getCurrentTransactionContext() != null;
                }
                case XA: 
                case XA_STRICT: {
                    TransactionManager tm = ((Cache)cache).getTransactionManagerLookup().getTransactionManager();
                    return (Integer)tm.getClass().getMethod("getStatus", new Class[0]).invoke((Object)tm, new Object[0]) != 6;
                }
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new CacheException("error checking if transaction started: " + e);
        }
    }
}

