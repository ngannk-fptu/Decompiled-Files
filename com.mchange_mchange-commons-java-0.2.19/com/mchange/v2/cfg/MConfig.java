/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.CachedStoreFactory;
import com.mchange.v1.cachedstore.CachedStoreUtils;
import com.mchange.v1.util.ArrayUtils;
import com.mchange.v2.cfg.ConfigUtils;
import com.mchange.v2.cfg.DelayedLogItem;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MConfig {
    private static final MLogger logger = MLog.getLogger(MConfig.class);
    private static final Map<DelayedLogItem.Level, MLevel> levelMap;
    static final CachedStore cache;

    public static MultiPropertiesConfig readVmConfig(String[] stringArray, String[] stringArray2) {
        try {
            return (MultiPropertiesConfig)cache.find(new PathsKey(stringArray, stringArray2));
        }
        catch (CachedStoreException cachedStoreException) {
            throw new RuntimeException(cachedStoreException);
        }
    }

    public static MultiPropertiesConfig readVmConfig() {
        return MConfig.readVmConfig(ConfigUtils.NO_PATHS, ConfigUtils.NO_PATHS);
    }

    public static MultiPropertiesConfig readConfig(String[] stringArray) {
        try {
            return (MultiPropertiesConfig)cache.find(new PathsKey(stringArray));
        }
        catch (CachedStoreException cachedStoreException) {
            throw new RuntimeException(cachedStoreException);
        }
    }

    public static MultiPropertiesConfig combine(MultiPropertiesConfig[] multiPropertiesConfigArray) {
        return ConfigUtils.combine(multiPropertiesConfigArray);
    }

    public static void dumpToLogger(List<DelayedLogItem> list, MLogger mLogger) {
        for (DelayedLogItem delayedLogItem : list) {
            MConfig.dumpToLogger(delayedLogItem, mLogger);
        }
    }

    public static void dumpToLogger(DelayedLogItem delayedLogItem, MLogger mLogger) {
        mLogger.log(levelMap.get((Object)delayedLogItem.getLevel()), delayedLogItem.getText(), delayedLogItem.getException());
    }

    private MConfig() {
    }

    static {
        try {
            HashMap<DelayedLogItem.Level, MLevel> hashMap = new HashMap<DelayedLogItem.Level, MLevel>();
            for (DelayedLogItem.Level level : DelayedLogItem.Level.values()) {
                hashMap.put(level, (MLevel)MLevel.class.getField(level.toString()).get(null));
            }
            levelMap = Collections.unmodifiableMap(hashMap);
        }
        catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
            throw runtimeException;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        cache = CachedStoreUtils.synchronizedCachedStore(CachedStoreFactory.createNoCleanupCachedStore(new CSManager()));
    }

    private static class CSManager
    implements CachedStore.Manager {
        private CSManager() {
        }

        @Override
        public boolean isDirty(Object object, Object object2) throws Exception {
            return false;
        }

        @Override
        public Object recreateFromKey(Object object) throws Exception {
            PathsKey pathsKey = (PathsKey)object;
            ArrayList<DelayedLogItem> arrayList = new ArrayList<DelayedLogItem>();
            arrayList.addAll(pathsKey.delayedLogItems);
            MultiPropertiesConfig multiPropertiesConfig = ConfigUtils.read(pathsKey.paths, arrayList);
            MConfig.dumpToLogger(arrayList, logger);
            return multiPropertiesConfig;
        }
    }

    private static final class PathsKey {
        String[] paths;
        List delayedLogItems;

        public boolean equals(Object object) {
            if (object instanceof PathsKey) {
                return Arrays.equals(this.paths, ((PathsKey)object).paths);
            }
            return false;
        }

        public int hashCode() {
            return ArrayUtils.hashArray(this.paths);
        }

        PathsKey(String[] stringArray, String[] stringArray2) {
            this.delayedLogItems = new ArrayList();
            List list = ConfigUtils.vmCondensedPaths(stringArray, stringArray2, this.delayedLogItems);
            this.paths = list.toArray(new String[list.size()]);
        }

        PathsKey(String[] stringArray) {
            this.delayedLogItems = Collections.emptyList();
            this.paths = stringArray;
        }
    }
}

