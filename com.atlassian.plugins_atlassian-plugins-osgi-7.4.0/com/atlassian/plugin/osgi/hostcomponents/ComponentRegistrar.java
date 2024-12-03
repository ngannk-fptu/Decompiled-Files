/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.hostcomponents;

import com.atlassian.plugin.osgi.hostcomponents.InstanceBuilder;

public interface ComponentRegistrar {
    public static final String HOST_COMPONENT_FLAG = "plugins-host";

    public InstanceBuilder register(Class<?> ... var1);
}

