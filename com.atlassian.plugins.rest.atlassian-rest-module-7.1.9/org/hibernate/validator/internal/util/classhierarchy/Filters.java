/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.classhierarchy;

import org.hibernate.validator.internal.util.classhierarchy.Filter;

public class Filters {
    private static final Filter PROXY_FILTER = new WeldProxyFilter();
    private static final Filter INTERFACES_FILTER = new InterfacesFilter();

    private Filters() {
    }

    public static Filter excludeInterfaces() {
        return INTERFACES_FILTER;
    }

    public static Filter excludeProxies() {
        return PROXY_FILTER;
    }

    private static class WeldProxyFilter
    implements Filter {
        private static final String WELD_PROXY_INTERFACE_NAME = "org.jboss.weld.bean.proxy.ProxyObject";

        private WeldProxyFilter() {
        }

        @Override
        public boolean accepts(Class<?> clazz) {
            return !this.isWeldProxy(clazz);
        }

        private boolean isWeldProxy(Class<?> clazz) {
            for (Class<?> implementedInterface : clazz.getInterfaces()) {
                if (!implementedInterface.getName().equals(WELD_PROXY_INTERFACE_NAME)) continue;
                return true;
            }
            return false;
        }
    }

    private static class InterfacesFilter
    implements Filter {
        private InterfacesFilter() {
        }

        @Override
        public boolean accepts(Class<?> clazz) {
            return !clazz.isInterface();
        }
    }
}

