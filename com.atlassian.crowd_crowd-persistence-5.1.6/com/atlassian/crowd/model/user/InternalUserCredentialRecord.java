/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.model.user.InternalUser;
import java.io.Serializable;

public class InternalUserCredentialRecord
implements Serializable {
    private Long id;
    private InternalUser user;
    private String passwordHash;

    protected InternalUserCredentialRecord() {
    }

    public InternalUserCredentialRecord(Long id, InternalUser user, String passwordHash) {
        this.id = id;
        this.user = user;
        this.passwordHash = passwordHash;
    }

    public InternalUserCredentialRecord(InternalUser user, String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public InternalUser getUser() {
        return this.user;
    }

    private void setUser(InternalUser user) {
        this.user = user;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    private void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public PasswordCredential getCredential() {
        return new PasswordCredential(this.passwordHash, true);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalUserCredentialRecord)) {
            return false;
        }
        InternalUserCredentialRecord that = (InternalUserCredentialRecord)o;
        if (this.getPasswordHash() != null ? !this.getPasswordHash().equals(that.getPasswordHash()) : that.getPasswordHash() != null) {
            return false;
        }
        return !(this.getUser().getId() != null ? !this.getUser().getId().equals(that.getUser().getId()) : that.getUser().getId() != null);
    }

    public int hashCode() {
        int result = this.getUser().getId() != null ? this.getUser().getId().hashCode() : 0;
        result = 31 * result + (this.getPasswordHash() != null ? this.getPasswordHash().hashCode() : 0);
        return result;
    }
}

