/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.expose.jmx;

interface InstrumentMXBean {

    public static interface CacheInstrumentMXBean
    extends InstrumentMXBean {
        public String getName();

        public long getMisses();

        public long getMissTime();

        public long getHits();

        public long getCacheSize();

        public double getHitMissRatio();
    }

    public static interface OpInstrumentMXBean
    extends InstrumentMXBean {
        public String getName();

        public long getInvocationCount();

        @Deprecated
        public long getMillisecondsTaken();

        public long getElapsedTotalTime();

        public long getElapsedMinTime();

        public long getElapsedMaxTime();

        @Deprecated
        public long getCpuTime();

        public long getCpuTotalTime();

        public long getCpuMinTime();

        public long getCpuMaxTime();

        public long getResultSetSize();
    }

    public static interface CounterMXBean
    extends InstrumentMXBean {
        public long getValue();

        public String getName();
    }

    public static interface GaugeMXBean
    extends InstrumentMXBean {
        public long getValue();

        public String getName();
    }
}

