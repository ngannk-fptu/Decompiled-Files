/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 */
package com.atlassian.mywork.service;

import com.atlassian.applinks.api.ApplicationId;

public interface ServiceSelector {
    public Target getEffectiveTarget();

    public Target getTarget();

    public void setTarget(Target var1, ApplicationId var2);

    public boolean isHostAvailable();

    public static enum Target {
        LOCAL,
        REMOTE,
        NONE,
        AUTO;

    }
}

