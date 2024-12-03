/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.activeobjects.internal.Prefix;

public interface ModelVersionManager {
    public ModelVersion getCurrent(Prefix var1);

    public void update(Prefix var1, ModelVersion var2);
}

