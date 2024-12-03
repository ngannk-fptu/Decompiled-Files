/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.token.Token
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.token.Token;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Objects;

public class UserTokenVerificationSucceededEvent {
    private final ImmutableApplication application;
    private final Token token;

    public UserTokenVerificationSucceededEvent(Application application, Token token) {
        this(ImmutableApplication.from((Application)application), token);
    }

    public UserTokenVerificationSucceededEvent(ImmutableApplication application, Token token) {
        this.application = (ImmutableApplication)Preconditions.checkNotNull((Object)application);
        this.token = (Token)Preconditions.checkNotNull((Object)token);
    }

    public ImmutableApplication getApplication() {
        return this.application;
    }

    public Token getToken() {
        return this.token;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserTokenVerificationSucceededEvent that = (UserTokenVerificationSucceededEvent)o;
        return Objects.equals(this.getApplication(), that.getApplication()) && Objects.equals(this.getToken(), that.getToken());
    }

    public int hashCode() {
        return Objects.hash(this.getApplication(), this.getToken());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("application", (Object)this.getApplication()).add("token", (Object)this.getToken()).toString();
    }
}

