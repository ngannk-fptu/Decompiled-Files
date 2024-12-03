/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.BundledUpdateInfo;

public interface BundledUpdateInfoStore {
    public Option<BundledUpdateInfo> getUpdateInfo();

    public void setUpdateInfo(Option<BundledUpdateInfo> var1);
}

