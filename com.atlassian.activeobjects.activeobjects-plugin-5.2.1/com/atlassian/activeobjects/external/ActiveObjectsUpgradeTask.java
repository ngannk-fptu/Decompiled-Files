/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.external;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ModelVersion;

public interface ActiveObjectsUpgradeTask {
    public ModelVersion getModelVersion();

    public void upgrade(ModelVersion var1, ActiveObjects var2);
}

