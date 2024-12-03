/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.internal.api;

import java.util.List;

public interface UpgradeTaskError {
    public String getTaskName();

    public String getExceptionMessage();

    public boolean isClusterUpgradeTask();

    public List<String> getErrors();
}

