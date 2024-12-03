/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.migration.agent.entity.IncorrectEmail;
import com.atlassian.migration.agent.service.email.ActionOnMigration;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
public class IncorrectEmailDTO {
    @Nullable
    @JsonProperty
    private final String newEmail;
    @Nullable
    @JsonProperty
    private final String currentEmail;
    @JsonProperty
    private final String userName;
    @JsonProperty
    private final ActionOnMigration actionOnMigration;
    @JsonProperty
    private final String directoryName;
    @Nullable
    @JsonProperty
    private final Long lastAuthenticated;

    public IncorrectEmailDTO(@Nullable String newEmail, @Nullable String currentEmail, String userName, ActionOnMigration actionOnMigration, String directoryName, @Nullable Long lastAuthenticated) {
        this.newEmail = newEmail;
        this.currentEmail = currentEmail;
        this.userName = userName;
        this.actionOnMigration = actionOnMigration;
        this.directoryName = directoryName;
        this.lastAuthenticated = lastAuthenticated;
    }

    @Nullable
    public String getNewEmail() {
        return this.newEmail;
    }

    @Nullable
    public String getCurrentEmail() {
        return this.currentEmail;
    }

    public String getUserName() {
        return this.userName;
    }

    public ActionOnMigration getActionOnMigration() {
        return this.actionOnMigration;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    @Nullable
    public Long getLastAuthenticated() {
        return this.lastAuthenticated;
    }

    public static IncorrectEmailDTO fromIncorrectEmail(IncorrectEmail incorrectEmail, ActionOnMigration actionOnMigration) {
        return new IncorrectEmailDTO(incorrectEmail.getNewEmail(), incorrectEmail.getEmail(), incorrectEmail.getUserName(), actionOnMigration, incorrectEmail.getDirectoryName(), incorrectEmail.getLastAuthenticated());
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IncorrectEmailDTO)) {
            return false;
        }
        IncorrectEmailDTO other = (IncorrectEmailDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$newEmail = this.getNewEmail();
        String other$newEmail = other.getNewEmail();
        if (this$newEmail == null ? other$newEmail != null : !this$newEmail.equals(other$newEmail)) {
            return false;
        }
        String this$currentEmail = this.getCurrentEmail();
        String other$currentEmail = other.getCurrentEmail();
        if (this$currentEmail == null ? other$currentEmail != null : !this$currentEmail.equals(other$currentEmail)) {
            return false;
        }
        String this$userName = this.getUserName();
        String other$userName = other.getUserName();
        if (this$userName == null ? other$userName != null : !this$userName.equals(other$userName)) {
            return false;
        }
        ActionOnMigration this$actionOnMigration = this.getActionOnMigration();
        ActionOnMigration other$actionOnMigration = other.getActionOnMigration();
        if (this$actionOnMigration == null ? other$actionOnMigration != null : !((Object)((Object)this$actionOnMigration)).equals((Object)other$actionOnMigration)) {
            return false;
        }
        String this$directoryName = this.getDirectoryName();
        String other$directoryName = other.getDirectoryName();
        if (this$directoryName == null ? other$directoryName != null : !this$directoryName.equals(other$directoryName)) {
            return false;
        }
        Long this$lastAuthenticated = this.getLastAuthenticated();
        Long other$lastAuthenticated = other.getLastAuthenticated();
        return !(this$lastAuthenticated == null ? other$lastAuthenticated != null : !((Object)this$lastAuthenticated).equals(other$lastAuthenticated));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof IncorrectEmailDTO;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $newEmail = this.getNewEmail();
        result = result * 59 + ($newEmail == null ? 43 : $newEmail.hashCode());
        String $currentEmail = this.getCurrentEmail();
        result = result * 59 + ($currentEmail == null ? 43 : $currentEmail.hashCode());
        String $userName = this.getUserName();
        result = result * 59 + ($userName == null ? 43 : $userName.hashCode());
        ActionOnMigration $actionOnMigration = this.getActionOnMigration();
        result = result * 59 + ($actionOnMigration == null ? 43 : ((Object)((Object)$actionOnMigration)).hashCode());
        String $directoryName = this.getDirectoryName();
        result = result * 59 + ($directoryName == null ? 43 : $directoryName.hashCode());
        Long $lastAuthenticated = this.getLastAuthenticated();
        result = result * 59 + ($lastAuthenticated == null ? 43 : ((Object)$lastAuthenticated).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "IncorrectEmailDTO(newEmail=" + this.getNewEmail() + ", currentEmail=" + this.getCurrentEmail() + ", userName=" + this.getUserName() + ", actionOnMigration=" + (Object)((Object)this.getActionOnMigration()) + ", directoryName=" + this.getDirectoryName() + ", lastAuthenticated=" + this.getLastAuthenticated() + ")";
    }
}

