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
import com.atlassian.crowd.model.authentication.ValidationFactor;
import java.io.Serializable;
import java.util.Arrays;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class AuthenticationContext
implements Serializable {
    private String name;
    private PasswordCredential credential;
    private ValidationFactor[] validationFactors;

    protected AuthenticationContext() {
    }

    protected AuthenticationContext(String name, @Nullable PasswordCredential credential, ValidationFactor[] validationFactors) {
        this.name = name;
        this.credential = AuthenticationContext.checkNotEncrypted(credential);
        this.validationFactors = validationFactors;
    }

    private static PasswordCredential checkNotEncrypted(PasswordCredential credential) {
        if (credential != null && credential.isEncryptedCredential()) {
            throw new IllegalArgumentException("Password credentials must not be encrypted");
        }
        return credential;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public PasswordCredential getCredential() {
        return this.credential;
    }

    public void setCredential(PasswordCredential credential) {
        this.credential = AuthenticationContext.checkNotEncrypted(credential);
    }

    public ValidationFactor[] getValidationFactors() {
        return this.validationFactors;
    }

    public void setValidationFactors(ValidationFactor[] validationFactors) {
        this.validationFactors = validationFactors;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuthenticationContext that = (AuthenticationContext)o;
        if (this.credential != null ? !this.credential.equals((Object)that.credential) : that.credential != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return Arrays.equals(this.validationFactors, that.validationFactors);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.credential != null ? this.credential.hashCode() : 0);
        result = 31 * result + (this.validationFactors != null ? Arrays.hashCode(this.validationFactors) : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.name).append("credential", (Object)this.credential).append("validationFactors", (Object[])this.validationFactors).toString();
    }
}

