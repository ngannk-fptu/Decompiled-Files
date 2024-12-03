/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.internal.api;

import com.atlassian.zdu.internal.api.NodeInfo;

public interface ZduNodeRepositoryService {
    public void removeAllNodeInfo();

    public void removeNodeInfo(String var1);

    public void addNodeInfo(NodeInfo var1);
}

