/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.ClientService
 */
package com.atlassian.mywork.host.service;

import com.atlassian.mywork.service.ClientService;

public interface LocalClientService
extends ClientService {
    public void verifyAuth(String var1);

    public void clientPong(String var1, String var2);

    public void updatePotentialClient(String var1);
}

