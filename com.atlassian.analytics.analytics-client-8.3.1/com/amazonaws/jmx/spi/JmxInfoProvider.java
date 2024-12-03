/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.jmx.spi;

import org.apache.commons.logging.LogFactory;

public interface JmxInfoProvider {
    public static final JmxInfoProvider NONE = new JmxInfoProvider(){

        @Override
        public long[] getFileDecriptorInfo() {
            return null;
        }

        @Override
        public int getThreadCount() {
            return 0;
        }

        @Override
        public int getDaemonThreadCount() {
            return 0;
        }

        @Override
        public int getPeakThreadCount() {
            return 0;
        }

        @Override
        public long getTotalStartedThreadCount() {
            return 0L;
        }

        @Override
        public long[] findDeadlockedThreads() {
            return null;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };

    public long[] getFileDecriptorInfo();

    public int getThreadCount();

    public int getDaemonThreadCount();

    public int getPeakThreadCount();

    public long getTotalStartedThreadCount();

    public long[] findDeadlockedThreads();

    public boolean isEnabled();

    public static class Factory {
        private static final JmxInfoProvider provider;

        public static JmxInfoProvider getJmxInfoProvider() {
            return provider;
        }

        static {
            JmxInfoProvider p;
            try {
                Class<?> c = Class.forName("com.amazonaws.jmx.JmxInfoProviderSupport");
                p = (JmxInfoProvider)c.newInstance();
            }
            catch (Exception e) {
                LogFactory.getLog(JmxInfoProvider.class).debug((Object)"Failed to load the JMX implementation module - JMX is disabled", (Throwable)e);
                p = NONE;
            }
            provider = p;
        }
    }
}

