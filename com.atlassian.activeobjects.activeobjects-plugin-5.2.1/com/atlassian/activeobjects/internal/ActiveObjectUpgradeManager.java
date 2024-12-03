/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.internal.Prefix;
import com.google.common.base.Supplier;
import java.util.List;

public interface ActiveObjectUpgradeManager {
    public void upgrade(Prefix var1, List<ActiveObjectsUpgradeTask> var2, Supplier<ActiveObjects> var3);
}

