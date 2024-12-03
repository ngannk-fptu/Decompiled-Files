/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.google.common.base.Preconditions;

public final class ValidatePasswordRequest {
    private final PasswordCredential password;
    private final User user;

    public ValidatePasswordRequest(PasswordCredential password, User user) {
        Preconditions.checkNotNull((Object)password, (Object)"password");
        Preconditions.checkNotNull((Object)user, (Object)"user");
        Preconditions.checkArgument((!password.isEncryptedCredential() ? 1 : 0) != 0, (Object)"password must not be encrypted");
        this.password = password;
        this.user = user;
    }

    public PasswordCredential getPassword() {
        return this.password;
    }

    public User getUser() {
        return this.user;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ValidatePasswordRequest that = (ValidatePasswordRequest)o;
        if (!this.password.equals(that.password)) {
            return false;
        }
        return this.user.equals(that.user);
    }

    public int hashCode() {
        int result = this.password.hashCode();
        result = 31 * result + this.user.hashCode();
        return result;
    }

    public String toString() {
        return "ValidatePasswordRequest{password=" + this.password + ", user=" + this.user + '}';
    }
}

