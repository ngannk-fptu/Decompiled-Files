/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.embedded.api;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PasswordCredential
implements Serializable {
    public static final PasswordCredential NONE = PasswordCredential.encrypted("X");
    public static final String SANITISED_PASSWORD = "********";
    private boolean encryptedCredential = false;
    protected String credential;

    public static PasswordCredential encrypted(String encryptedCredential) {
        return new PasswordCredential(encryptedCredential, true);
    }

    public static PasswordCredential unencrypted(String unencryptedCredential) {
        return new PasswordCredential(unencryptedCredential, false);
    }

    @Deprecated
    public PasswordCredential() {
        this.encryptedCredential = true;
    }

    public PasswordCredential(PasswordCredential passwordCredential) {
        this.credential = passwordCredential.credential;
        this.encryptedCredential = passwordCredential.encryptedCredential;
    }

    public PasswordCredential(String unencryptedCredential) {
        this.encryptedCredential = false;
        this.credential = unencryptedCredential;
    }

    public PasswordCredential(String credential, boolean encryptedCredential) {
        this.credential = credential;
        this.encryptedCredential = encryptedCredential;
    }

    public String getCredential() {
        return this.credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public boolean isEncryptedCredential() {
        return this.encryptedCredential;
    }

    @Deprecated
    public void setEncryptedCredential(boolean encryptedCredential) {
        this.encryptedCredential = encryptedCredential;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PasswordCredential that = (PasswordCredential)o;
        if (this.encryptedCredential != that.encryptedCredential) {
            return false;
        }
        return this.credential != null ? this.credential.equals(that.credential) : that.credential == null;
    }

    public int hashCode() {
        return this.credential != null ? this.credential.hashCode() : 0;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("credential", (Object)SANITISED_PASSWORD).append("encryptedCredential", this.encryptedCredential).toString();
    }
}

