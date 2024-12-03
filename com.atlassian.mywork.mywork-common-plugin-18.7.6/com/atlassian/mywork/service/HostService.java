/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.fugue.Option
 */
package com.atlassian.mywork.service;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.fugue.Option;

public interface HostService {
    public Option<ApplicationLink> getActiveHost();

    public Option<ApplicationLink> getRegisteredHost();

    public Iterable<ApplicationLink> getAvailableHosts();

    public void setSelectedHost(ApplicationId var1);

    public void enable();

    public void disable();

    public void resetHosts();
}

