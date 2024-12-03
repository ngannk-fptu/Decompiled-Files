/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.model.application.Application;

public interface ApplicationCache {
    public Application getApplication(String var1, Loader var2) throws ApplicationNotFoundException;

    public void removeApplication(String var1);

    default public void removeApplication(Application application) {
        this.removeApplication(application.getName());
    }

    public void removeAll();

    @FunctionalInterface
    public static interface Loader {
        public Application getApplication(String var1) throws ApplicationNotFoundException;
    }
}

