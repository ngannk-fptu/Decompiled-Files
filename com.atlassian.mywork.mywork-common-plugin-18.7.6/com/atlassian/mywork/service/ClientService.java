/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.mywork.service;

import com.atlassian.applinks.api.ApplicationLink;

public interface ClientService {
    public Iterable<ApplicationLink> getActiveClients();
}

