/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.Grantee;
import java.io.Serializable;

public class EmailAddressGrantee
implements Grantee,
Serializable {
    private String emailAddress = null;

    @Override
    public String getTypeIdentifier() {
        return "emailAddress";
    }

    public EmailAddressGrantee(String emailAddress) {
        this.setIdentifier(emailAddress);
    }

    @Override
    public void setIdentifier(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String getIdentifier() {
        return this.emailAddress;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.emailAddress == null ? 0 : this.emailAddress.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        EmailAddressGrantee other = (EmailAddressGrantee)obj;
        return !(this.emailAddress == null ? other.emailAddress != null : !this.emailAddress.equals(other.emailAddress));
    }

    public String toString() {
        return this.emailAddress;
    }
}

