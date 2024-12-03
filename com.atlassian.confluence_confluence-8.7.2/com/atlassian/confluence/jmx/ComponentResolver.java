/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.jmx;

import com.atlassian.spring.container.ContainerManager;

interface ComponentResolver {
    public static final ComponentResolver DEFAULT_RESOLVER = new DefaultComponentResolver();

    public Object resolveComponent(String var1);

    public static class DefaultComponentResolver
    implements ComponentResolver {
        @Override
        public Object resolveComponent(String key) {
            if (!ContainerManager.isContainerSetup()) {
                return null;
            }
            return ContainerManager.getComponent((String)key);
        }
    }
}

