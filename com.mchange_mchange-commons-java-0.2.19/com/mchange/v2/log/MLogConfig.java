/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import com.mchange.v2.cfg.DelayedLogItem;
import com.mchange.v2.cfg.MConfig;
import com.mchange.v2.cfg.MLogConfigSource;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import com.mchange.v2.log.MLogger;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class MLogConfig {
    private static MultiPropertiesConfig CONFIG = null;
    private static List BOOTSTRAP_LOG_ITEMS = null;
    private static Method delayedDumpToLogger = null;

    public static synchronized void refresh(MultiPropertiesConfig[] multiPropertiesConfigArray, String string) {
        boolean bl;
        String[] stringArray = new String[]{"/com/mchange/v2/log/default-mchange-log.properties"};
        String[] stringArray2 = new String[]{"/mchange-log.properties", "/"};
        ArrayList<DelayedLogItem> arrayList = new ArrayList<DelayedLogItem>();
        MultiPropertiesConfig multiPropertiesConfig = MLogConfigSource.readVmConfig(stringArray, stringArray2, arrayList);
        boolean bl2 = bl = CONFIG == null;
        if (multiPropertiesConfigArray != null) {
            int n = multiPropertiesConfigArray.length;
            MultiPropertiesConfig[] multiPropertiesConfigArray2 = new MultiPropertiesConfig[n + 1];
            multiPropertiesConfigArray2[0] = multiPropertiesConfig;
            for (int i = 0; i < n; ++i) {
                multiPropertiesConfigArray2[i + 1] = multiPropertiesConfigArray[i];
            }
            arrayList.add(new DelayedLogItem(DelayedLogItem.Level.INFO, (bl ? "Loaded" : "Refreshed") + " MLog library log configuration, with overrides" + (string == null ? "." : ": " + string)));
            CONFIG = MConfig.combine(multiPropertiesConfigArray2);
        } else {
            if (!bl) {
                arrayList.add(new DelayedLogItem(DelayedLogItem.Level.INFO, "Refreshed MLog library log configuration, without overrides."));
            }
            CONFIG = multiPropertiesConfig;
        }
        BOOTSTRAP_LOG_ITEMS = arrayList;
    }

    private static void ensureLoad() {
        if (CONFIG == null) {
            MLogConfig.refresh(null, null);
        }
    }

    private static void ensureDelayedDumpToLogger() {
        try {
            if (delayedDumpToLogger == null) {
                Class<?> clazz = Class.forName("com.mchange.v2.cfg.MConfig");
                Class<?> clazz2 = Class.forName("com.mchange.v2.cfg.DelayedLogItem");
                delayedDumpToLogger = clazz.getMethod("dumpToLogger", clazz2, MLogger.class);
            }
        }
        catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
            throw runtimeException;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    public static synchronized String getProperty(String string) {
        MLogConfig.ensureLoad();
        return CONFIG.getProperty(string);
    }

    public static synchronized void logDelayedItems(MLogger mLogger) {
        MLogConfig.ensureLoad();
        MLogConfig.ensureDelayedDumpToLogger();
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(BOOTSTRAP_LOG_ITEMS);
        arrayList.addAll(CONFIG.getDelayedLogItems());
        HashSet hashSet = new HashSet();
        hashSet.addAll(arrayList);
        for (Object e : arrayList) {
            if (!hashSet.contains(e)) continue;
            hashSet.remove(e);
            try {
                delayedDumpToLogger.invoke(null, e, mLogger);
            }
            catch (Exception exception) {
                exception.printStackTrace();
                throw new Error(exception);
            }
        }
    }

    public static synchronized String dump() {
        return CONFIG.toString();
    }

    private MLogConfig() {
    }
}

