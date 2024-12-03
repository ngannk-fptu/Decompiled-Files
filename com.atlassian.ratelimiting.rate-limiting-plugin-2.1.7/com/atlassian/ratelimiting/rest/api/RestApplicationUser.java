/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.sal.api.user.UserProfile;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class RestApplicationUser {
    private String username;
    private String displayName;
    private String emailAddress;
    private String profilePictureUrl;
    private String profileUrl;

    public RestApplicationUser(UserProfile userProfile) {
        this.displayName = userProfile.getFullName();
        this.emailAddress = userProfile.getEmail();
        this.username = userProfile.getUsername();
        this.profilePictureUrl = Objects.isNull(userProfile.getProfilePictureUri()) ? "" : userProfile.getProfilePictureUri().toString();
        this.profileUrl = Objects.isNull(userProfile.getProfilePageUri()) ? "" : userProfile.getProfilePageUri().toString();
    }

    public String getUsername() {
        return this.username;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    public String getProfileUrl() {
        return this.profileUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestApplicationUser)) {
            return false;
        }
        RestApplicationUser other = (RestApplicationUser)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
            return false;
        }
        String this$emailAddress = this.getEmailAddress();
        String other$emailAddress = other.getEmailAddress();
        if (this$emailAddress == null ? other$emailAddress != null : !this$emailAddress.equals(other$emailAddress)) {
            return false;
        }
        String this$profilePictureUrl = this.getProfilePictureUrl();
        String other$profilePictureUrl = other.getProfilePictureUrl();
        if (this$profilePictureUrl == null ? other$profilePictureUrl != null : !this$profilePictureUrl.equals(other$profilePictureUrl)) {
            return false;
        }
        String this$profileUrl = this.getProfileUrl();
        String other$profileUrl = other.getProfileUrl();
        return !(this$profileUrl == null ? other$profileUrl != null : !this$profileUrl.equals(other$profileUrl));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestApplicationUser;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        String $emailAddress = this.getEmailAddress();
        result = result * 59 + ($emailAddress == null ? 43 : $emailAddress.hashCode());
        String $profilePictureUrl = this.getProfilePictureUrl();
        result = result * 59 + ($profilePictureUrl == null ? 43 : $profilePictureUrl.hashCode());
        String $profileUrl = this.getProfileUrl();
        result = result * 59 + ($profileUrl == null ? 43 : $profileUrl.hashCode());
        return result;
    }

    public String toString() {
        return "RestApplicationUser(username=" + this.getUsername() + ", displayName=" + this.getDisplayName() + ", emailAddress=" + this.getEmailAddress() + ", profilePictureUrl=" + this.getProfilePictureUrl() + ", profileUrl=" + this.getProfileUrl() + ")";
    }

    public RestApplicationUser() {
    }

    public RestApplicationUser(String username, String displayName, String emailAddress, String profilePictureUrl, String profileUrl) {
        this.username = username;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.profilePictureUrl = profilePictureUrl;
        this.profileUrl = profileUrl;
    }
}

