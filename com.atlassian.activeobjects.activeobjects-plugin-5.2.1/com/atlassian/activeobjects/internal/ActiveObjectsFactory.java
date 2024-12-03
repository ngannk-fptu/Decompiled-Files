/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.external.ActiveObjects;

public interface ActiveObjectsFactory {
    public boolean accept(ActiveObjectsConfiguration var1);

    public ActiveObjects create(ActiveObjectsConfiguration var1);
}

