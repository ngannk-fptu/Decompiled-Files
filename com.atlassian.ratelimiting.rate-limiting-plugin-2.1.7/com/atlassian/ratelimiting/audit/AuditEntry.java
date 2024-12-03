/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.ratelimiting.audit;

import com.atlassian.ratelimiting.audit.AuditChangedValue;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AuditEntry {
    private final String summary;
    private final Optional<UserProfile> userProfile;
    private final List<AuditChangedValue> changes;

    private static Optional<UserProfile> $default$userProfile() {
        return Optional.empty();
    }

    private static List<AuditChangedValue> $default$changes() {
        return Collections.emptyList();
    }

    AuditEntry(String summary, Optional<UserProfile> userProfile, List<AuditChangedValue> changes) {
        this.summary = summary;
        this.userProfile = userProfile;
        this.changes = changes;
    }

    public static AuditEntryBuilder builder() {
        return new AuditEntryBuilder();
    }

    public String getSummary() {
        return this.summary;
    }

    public Optional<UserProfile> getUserProfile() {
        return this.userProfile;
    }

    public List<AuditChangedValue> getChanges() {
        return this.changes;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuditEntry)) {
            return false;
        }
        AuditEntry other = (AuditEntry)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$summary = this.getSummary();
        String other$summary = other.getSummary();
        if (this$summary == null ? other$summary != null : !this$summary.equals(other$summary)) {
            return false;
        }
        Optional<UserProfile> this$userProfile = this.getUserProfile();
        Optional<UserProfile> other$userProfile = other.getUserProfile();
        if (this$userProfile == null ? other$userProfile != null : !((Object)this$userProfile).equals(other$userProfile)) {
            return false;
        }
        List<AuditChangedValue> this$changes = this.getChanges();
        List<AuditChangedValue> other$changes = other.getChanges();
        return !(this$changes == null ? other$changes != null : !((Object)this$changes).equals(other$changes));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AuditEntry;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $summary = this.getSummary();
        result = result * 59 + ($summary == null ? 43 : $summary.hashCode());
        Optional<UserProfile> $userProfile = this.getUserProfile();
        result = result * 59 + ($userProfile == null ? 43 : ((Object)$userProfile).hashCode());
        List<AuditChangedValue> $changes = this.getChanges();
        result = result * 59 + ($changes == null ? 43 : ((Object)$changes).hashCode());
        return result;
    }

    public String toString() {
        return "AuditEntry(summary=" + this.getSummary() + ", userProfile=" + this.getUserProfile() + ", changes=" + this.getChanges() + ")";
    }

    public static class AuditEntryBuilder {
        private String summary;
        private boolean userProfile$set;
        private Optional<UserProfile> userProfile$value;
        private boolean changes$set;
        private List<AuditChangedValue> changes$value;

        AuditEntryBuilder() {
        }

        public AuditEntryBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public AuditEntryBuilder userProfile(Optional<UserProfile> userProfile) {
            this.userProfile$value = userProfile;
            this.userProfile$set = true;
            return this;
        }

        public AuditEntryBuilder changes(List<AuditChangedValue> changes) {
            this.changes$value = changes;
            this.changes$set = true;
            return this;
        }

        public AuditEntry build() {
            Optional userProfile$value = this.userProfile$value;
            if (!this.userProfile$set) {
                userProfile$value = AuditEntry.$default$userProfile();
            }
            List changes$value = this.changes$value;
            if (!this.changes$set) {
                changes$value = AuditEntry.$default$changes();
            }
            return new AuditEntry(this.summary, userProfile$value, changes$value);
        }

        public String toString() {
            return "AuditEntry.AuditEntryBuilder(summary=" + this.summary + ", userProfile$value=" + this.userProfile$value + ", changes$value=" + this.changes$value + ")";
        }
    }
}

