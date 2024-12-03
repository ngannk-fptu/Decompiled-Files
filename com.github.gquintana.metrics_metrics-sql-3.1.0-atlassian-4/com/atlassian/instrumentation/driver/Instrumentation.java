/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.driver;

import com.atlassian.instrumentation.instruments.Context;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Instrumentation {
    private static final CopyOnWriteArrayList<SplitFactory> splitFactories = new CopyOnWriteArrayList();

    public static void registerFactory(SplitFactory factory) {
        splitFactories.addIfAbsent(factory);
    }

    public static void unregisterFactory(SplitFactory factory) {
        splitFactories.remove(factory);
    }

    public static Split startSplit(Context context) {
        int splitFactoryCount = splitFactories.size();
        if (splitFactoryCount == 0) {
            return () -> {};
        }
        ArrayList<Split> splits = new ArrayList<Split>(splitFactoryCount);
        for (SplitFactory factory : splitFactories) {
            splits.add(factory.startSplit(context));
        }
        return new CombinedSplit(splits);
    }

    private static class CombinedSplit
    implements Split {
        private final Iterable<Split> splits;

        private CombinedSplit(Iterable<Split> splits) {
            this.splits = splits;
        }

        @Override
        public void stop() {
            this.splits.forEach(Split::stop);
        }
    }

    public static interface SplitFactory {
        public Split startSplit(Context var1);
    }

    public static interface Split {
        public void stop();
    }
}

