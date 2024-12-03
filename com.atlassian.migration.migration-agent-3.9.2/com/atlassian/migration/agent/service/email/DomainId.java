/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import java.util.Locale;
import java.util.UUID;
import lombok.Generated;

public class DomainId {
    private final String domain;
    private final String standardizedDomain;

    public DomainId(String domain) {
        this.domain = domain;
        this.standardizedDomain = IdentityAcceptedEmailValidator.cleanse((String)domain).toLowerCase(Locale.ENGLISH);
    }

    public String generateRandomEmail() {
        String id = UUID.randomUUID().toString().replace("-", "");
        String formattedDomain = this.standardizedDomain.startsWith("@") ? this.standardizedDomain : "@" + this.standardizedDomain;
        return id + formattedDomain;
    }

    public static DomainId fromEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String domain = email.substring(email.lastIndexOf("@") + 1);
        if (domain.trim().isEmpty()) {
            return null;
        }
        return new DomainId(domain);
    }

    public String getStandardizedDomain() {
        return this.standardizedDomain;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DomainId)) {
            return false;
        }
        DomainId other = (DomainId)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$standardizedDomain = this.getStandardizedDomain();
        String other$standardizedDomain = other.getStandardizedDomain();
        return !(this$standardizedDomain == null ? other$standardizedDomain != null : !this$standardizedDomain.equals(other$standardizedDomain));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof DomainId;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $standardizedDomain = this.getStandardizedDomain();
        result = result * 59 + ($standardizedDomain == null ? 43 : $standardizedDomain.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "DomainId(domain=" + this.domain + ", standardizedDomain=" + this.getStandardizedDomain() + ")";
    }
}

