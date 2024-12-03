/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.manager.sso;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;

public class ApplicationSsoDisabledException
extends RuntimeException {
    private final ImmutableApplication application;

    public ApplicationSsoDisabledException(Application application) {
        super(String.format("SSO is disabled for application: %s", application.getName()));
        this.application = ImmutableApplication.from(application);
    }

    public Application getApplication() {
        return this.application;
    }
}

