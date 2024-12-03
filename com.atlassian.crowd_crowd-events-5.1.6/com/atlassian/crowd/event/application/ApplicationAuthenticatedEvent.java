/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.token.Token
 */
package com.atlassian.crowd.event.application;

import com.atlassian.crowd.event.Event;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.token.Token;

public class ApplicationAuthenticatedEvent
extends Event {
    private final ImmutableApplication application;
    private final Token token;

    public ApplicationAuthenticatedEvent(Object source, Application application, Token token) {
        super(source);
        this.application = ImmutableApplication.from((Application)application);
        this.token = token;
    }

    public Application getApplication() {
        return this.application;
    }

    public Token getToken() {
        return this.token;
    }

    public Long getApplicationId() {
        return this.application.getId();
    }
}

