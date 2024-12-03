/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.hostcomponents;

import java.util.Dictionary;

public interface HostComponentRegistration {
    public Dictionary<String, String> getProperties();

    public String[] getMainInterfaces();

    public Object getInstance();

    public Class<?>[] getMainInterfaceClasses();
}

