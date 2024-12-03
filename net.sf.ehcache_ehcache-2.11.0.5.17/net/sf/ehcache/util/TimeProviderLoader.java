/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import net.sf.ehcache.util.SlewClock;

final class TimeProviderLoader {
    private static SlewClock.TimeProvider timeProvider = new SlewClock.TimeProvider(){

        @Override
        public final long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    };

    private TimeProviderLoader() {
    }

    public static synchronized SlewClock.TimeProvider getTimeProvider() {
        return timeProvider;
    }

    public static synchronized void setTimeProvider(SlewClock.TimeProvider timeProvider) {
        TimeProviderLoader.timeProvider = timeProvider;
    }
}

