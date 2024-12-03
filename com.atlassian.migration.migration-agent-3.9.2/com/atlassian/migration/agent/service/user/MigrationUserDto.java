/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  javax.annotation.Nonnull
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import lombok.Generated;

public class MigrationUserDto {
    private final String userKey;
    private final String userName;
    private final String email;
    private final String fullName;
    private final boolean isActive;
    private final Set<String> additionalUserkeys;
    private final Set<String> additionalUsernames;
    private final Set<String> additionalEmails;
    private final Set<String> additionalDisplayNames;

    public static MigrationUserDto from(MigrationUser user) {
        return new MigrationUserDto(user.getUserKey(), user.getUsername(), user.getEmail(), user.getFullName(), user.isActive(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    public static MigrationUserDto from(MigrationUser user, @Nonnull List<MigrationUser> alternatives) {
        HashSet<String> userKeys = new HashSet<String>();
        HashSet<String> userNames = new HashSet<String>();
        HashSet<String> emails = new HashSet<String>();
        HashSet<String> displayNames = new HashSet<String>();
        alternatives.forEach(alternative -> {
            MigrationUserDto.addIfNotEqual(userKeys, user, alternative, MigrationUser::getUserKey);
            MigrationUserDto.addIfNotEqual(userNames, user, alternative, MigrationUser::getUsername);
            MigrationUserDto.addIfNotEqual(emails, user, alternative, v -> IdentityAcceptedEmailValidator.cleanse((String)v.getEmail()));
            MigrationUserDto.addIfNotEqual(displayNames, user, alternative, MigrationUser::getFullName);
        });
        return new MigrationUserDto(user.getUserKey(), user.getUsername(), user.getEmail(), user.getFullName(), user.isActive(), userKeys, userNames, emails, displayNames);
    }

    private static void addIfNotEqual(Set<String> listToAdd, MigrationUser user, MigrationUser alternative, Function<MigrationUser, String> field) {
        if (!Objects.equals(field.apply(user), field.apply(alternative))) {
            listToAdd.add(field.apply(alternative));
        }
    }

    @Generated
    public MigrationUserDto(String userKey, String userName, String email, String fullName, boolean isActive, Set<String> additionalUserkeys, Set<String> additionalUsernames, Set<String> additionalEmails, Set<String> additionalDisplayNames) {
        this.userKey = userKey;
        this.userName = userName;
        this.email = email;
        this.fullName = fullName;
        this.isActive = isActive;
        this.additionalUserkeys = additionalUserkeys;
        this.additionalUsernames = additionalUsernames;
        this.additionalEmails = additionalEmails;
        this.additionalDisplayNames = additionalDisplayNames;
    }

    @Generated
    public String getUserKey() {
        return this.userKey;
    }

    @Generated
    public String getUserName() {
        return this.userName;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getFullName() {
        return this.fullName;
    }

    @Generated
    public boolean isActive() {
        return this.isActive;
    }

    @Generated
    public Set<String> getAdditionalUserkeys() {
        return this.additionalUserkeys;
    }

    @Generated
    public Set<String> getAdditionalUsernames() {
        return this.additionalUsernames;
    }

    @Generated
    public Set<String> getAdditionalEmails() {
        return this.additionalEmails;
    }

    @Generated
    public Set<String> getAdditionalDisplayNames() {
        return this.additionalDisplayNames;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MigrationUserDto)) {
            return false;
        }
        MigrationUserDto other = (MigrationUserDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$userKey = this.getUserKey();
        String other$userKey = other.getUserKey();
        if (this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey)) {
            return false;
        }
        String this$userName = this.getUserName();
        String other$userName = other.getUserName();
        if (this$userName == null ? other$userName != null : !this$userName.equals(other$userName)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$fullName = this.getFullName();
        String other$fullName = other.getFullName();
        if (this$fullName == null ? other$fullName != null : !this$fullName.equals(other$fullName)) {
            return false;
        }
        if (this.isActive() != other.isActive()) {
            return false;
        }
        Set<String> this$additionalUserkeys = this.getAdditionalUserkeys();
        Set<String> other$additionalUserkeys = other.getAdditionalUserkeys();
        if (this$additionalUserkeys == null ? other$additionalUserkeys != null : !((Object)this$additionalUserkeys).equals(other$additionalUserkeys)) {
            return false;
        }
        Set<String> this$additionalUsernames = this.getAdditionalUsernames();
        Set<String> other$additionalUsernames = other.getAdditionalUsernames();
        if (this$additionalUsernames == null ? other$additionalUsernames != null : !((Object)this$additionalUsernames).equals(other$additionalUsernames)) {
            return false;
        }
        Set<String> this$additionalEmails = this.getAdditionalEmails();
        Set<String> other$additionalEmails = other.getAdditionalEmails();
        if (this$additionalEmails == null ? other$additionalEmails != null : !((Object)this$additionalEmails).equals(other$additionalEmails)) {
            return false;
        }
        Set<String> this$additionalDisplayNames = this.getAdditionalDisplayNames();
        Set<String> other$additionalDisplayNames = other.getAdditionalDisplayNames();
        return !(this$additionalDisplayNames == null ? other$additionalDisplayNames != null : !((Object)this$additionalDisplayNames).equals(other$additionalDisplayNames));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MigrationUserDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        String $userName = this.getUserName();
        result = result * 59 + ($userName == null ? 43 : $userName.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $fullName = this.getFullName();
        result = result * 59 + ($fullName == null ? 43 : $fullName.hashCode());
        result = result * 59 + (this.isActive() ? 79 : 97);
        Set<String> $additionalUserkeys = this.getAdditionalUserkeys();
        result = result * 59 + ($additionalUserkeys == null ? 43 : ((Object)$additionalUserkeys).hashCode());
        Set<String> $additionalUsernames = this.getAdditionalUsernames();
        result = result * 59 + ($additionalUsernames == null ? 43 : ((Object)$additionalUsernames).hashCode());
        Set<String> $additionalEmails = this.getAdditionalEmails();
        result = result * 59 + ($additionalEmails == null ? 43 : ((Object)$additionalEmails).hashCode());
        Set<String> $additionalDisplayNames = this.getAdditionalDisplayNames();
        result = result * 59 + ($additionalDisplayNames == null ? 43 : ((Object)$additionalDisplayNames).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "MigrationUserDto(userKey=" + this.getUserKey() + ", userName=" + this.getUserName() + ", email=" + this.getEmail() + ", fullName=" + this.getFullName() + ", isActive=" + this.isActive() + ", additionalUserkeys=" + this.getAdditionalUserkeys() + ", additionalUsernames=" + this.getAdditionalUsernames() + ", additionalEmails=" + this.getAdditionalEmails() + ", additionalDisplayNames=" + this.getAdditionalDisplayNames() + ")";
    }
}

