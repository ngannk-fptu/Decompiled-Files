/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.authentication;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.model.authentication.AuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserAuthenticationContext
extends AuthenticationContext
implements Serializable {
    private String application;
    private boolean localCrowdWebAppAuthentication;

    public UserAuthenticationContext() {
    }

    public UserAuthenticationContext(String name, @Nullable PasswordCredential credential, ValidationFactor[] validationFactors, String application) {
        super(name, credential, validationFactors);
        this.application = application;
    }

    public String getApplication() {
        return this.application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public boolean isLocalCrowdWebAppAuthentication() {
        return this.localCrowdWebAppAuthentication;
    }

    public void setLocalCrowdWebAppAuthentication(boolean localCrowdWebAppAuthentication) {
        this.localCrowdWebAppAuthentication = localCrowdWebAppAuthentication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UserAuthenticationContext that = (UserAuthenticationContext)o;
        if (this.application != null ? !this.application.equals(that.application) : that.application != null) {
            return false;
        }
        return this.localCrowdWebAppAuthentication == that.localCrowdWebAppAuthentication;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.application != null ? this.application.hashCode() : 0);
        result = 31 * result + Boolean.hashCode(this.localCrowdWebAppAuthentication);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).appendSuper(super.toString()).append("application", (Object)this.application).append("localCrowdWebAppAuthentication", this.localCrowdWebAppAuthentication).toString();
    }

    public UserAuthenticationContext withName(String name) {
        UserAuthenticationContext userAuthenticationContext = new UserAuthenticationContext(name, this.getCredential(), this.getValidationFactors(), this.getApplication());
        userAuthenticationContext.setLocalCrowdWebAppAuthentication(this.isLocalCrowdWebAppAuthentication());
        return userAuthenticationContext;
    }
}

