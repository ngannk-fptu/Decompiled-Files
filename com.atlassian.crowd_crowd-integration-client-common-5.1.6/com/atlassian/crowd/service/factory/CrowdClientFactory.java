/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.service.factory;

import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;

public interface CrowdClientFactory {
    public CrowdClient newInstance(String var1, String var2, String var3);

    public CrowdClient newInstance(ClientProperties var1);
}

