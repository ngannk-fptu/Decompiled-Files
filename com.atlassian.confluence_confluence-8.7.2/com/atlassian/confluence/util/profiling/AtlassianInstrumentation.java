/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class AtlassianInstrumentation {
    private static final Object modificationLock = new Object();
    private static final Collection<AtlasSplitFactory> atlasSplitFactories = new CopyOnWriteArrayList<AtlasSplitFactory>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void registerFactory(AtlasSplitFactory factory) {
        Object object = modificationLock;
        synchronized (object) {
            if (factory != null && !atlasSplitFactories.contains(factory)) {
                atlasSplitFactories.add(factory);
            }
        }
    }

    public static void unregisterFactory(AtlasSplitFactory factory) {
        atlasSplitFactories.remove(factory);
    }

    @VisibleForTesting
    static boolean isRegistered(AtlasSplitFactory factory) {
        return atlasSplitFactories.contains(factory);
    }

    public static AtlasSplit startSplit(String name) {
        int splitFactoryCount = atlasSplitFactories.size();
        if (splitFactoryCount == 0) {
            return NopAtlasSplit.INSTANCE;
        }
        ArrayList<AtlasSplit> splits = new ArrayList<AtlasSplit>(splitFactoryCount);
        for (AtlasSplitFactory atlasSplitFactory : atlasSplitFactories) {
            splits.add(atlasSplitFactory.startSplit(name));
        }
        return new CombinedSplit(splits);
    }

    private static class CombinedSplit
    implements AtlasSplit {
        private final Iterable<AtlasSplit> splits;

        private CombinedSplit(Iterable<AtlasSplit> splits) {
            this.splits = splits;
        }

        @Override
        public void stop() {
            for (AtlasSplit split : this.splits) {
                split.stop();
            }
        }
    }

    static final class NopAtlasSplit
    implements AtlasSplit {
        static final NopAtlasSplit INSTANCE = new NopAtlasSplit();

        NopAtlasSplit() {
        }

        @Override
        public void stop() {
        }
    }

    public static interface AtlasSplitFactory {
        public AtlasSplit startSplit(String var1);
    }

    public static interface AtlasSplit {
        public void stop();
    }
}

