/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.jmx.spi;

import org.apache.commons.logging.LogFactory;

public interface SdkMBeanRegistry {
    public static final SdkMBeanRegistry NONE = new SdkMBeanRegistry(){

        @Override
        public boolean registerMetricAdminMBean(String objectName) {
            return false;
        }

        @Override
        public boolean unregisterMBean(String objectName) {
            return false;
        }

        @Override
        public boolean isMBeanRegistered(String objectName) {
            return false;
        }
    };

    public boolean registerMetricAdminMBean(String var1);

    public boolean unregisterMBean(String var1);

    public boolean isMBeanRegistered(String var1);

    public static class Factory {
        private static final SdkMBeanRegistry registry;

        public static SdkMBeanRegistry getMBeanRegistry() {
            return registry;
        }

        static {
            SdkMBeanRegistry rego;
            try {
                Class<?> c = Class.forName("com.amazonaws.jmx.SdkMBeanRegistrySupport");
                rego = (SdkMBeanRegistry)c.newInstance();
            }
            catch (Exception e) {
                LogFactory.getLog(SdkMBeanRegistry.class).debug((Object)"Failed to load the JMX implementation module - JMX is disabled", (Throwable)e);
                rego = NONE;
            }
            registry = rego;
        }
    }
}

