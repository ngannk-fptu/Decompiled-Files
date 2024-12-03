/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.sf.ehcache.CacheException;

public final class ConcurrencyUtil {
    private static final int DOUG_LEA_BLACK_MAGIC_OPERAND_1 = 20;
    private static final int DOUG_LEA_BLACK_MAGIC_OPERAND_2 = 12;
    private static final int DOUG_LEA_BLACK_MAGIC_OPERAND_3 = 7;
    private static final int DOUG_LEA_BLACK_MAGIC_OPERAND_4 = 4;

    private ConcurrencyUtil() {
    }

    public static int hash(Object object) {
        int h = object.hashCode();
        h ^= h >>> 20 ^ h >>> 12;
        return h ^ h >>> 7 ^ h >>> 4;
    }

    public static int selectLock(Object key, int numberOfLocks) throws CacheException {
        int number = numberOfLocks & numberOfLocks - 1;
        if (number != 0) {
            throw new CacheException("Lock number must be a power of two: " + numberOfLocks);
        }
        if (key == null) {
            return 0;
        }
        int hash = ConcurrencyUtil.hash(key) & numberOfLocks - 1;
        return hash;
    }

    public static void shutdownAndWaitForTermination(ExecutorService pool, int waitSeconds) throws TimeoutException {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
                    throw new TimeoutException("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

