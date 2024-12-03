/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 */
package com.atlassian.plugin.hostcontainer;

import com.atlassian.plugin.hostcontainer.HostContainer;

public class DefaultHostContainer
implements HostContainer {
    public <T> T create(Class<T> moduleClass) {
        try {
            return moduleClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Unable to instantiate constructor", e);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to access constructor", e);
        }
    }
}

