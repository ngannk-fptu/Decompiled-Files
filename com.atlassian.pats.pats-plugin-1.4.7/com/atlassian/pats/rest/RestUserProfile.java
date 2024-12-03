/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.pats.rest;

import com.atlassian.sal.api.user.UserProfile;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class RestUserProfile {
    @XmlElement
    private String userKey;
    @XmlElement
    private String username;
    @XmlElement
    private String fullName;
    @XmlElement
    private String email;

    public static RestUserProfile valueOf(UserProfile userProfile) {
        return RestUserProfile.builder().userKey(userProfile.getUserKey().getStringValue()).username(userProfile.getUsername()).fullName(userProfile.getFullName()).email(userProfile.getEmail()).build();
    }

    public static RestUserProfileBuilder builder() {
        return new RestUserProfileBuilder();
    }

    public String getUserKey() {
        return this.userKey;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestUserProfile)) {
            return false;
        }
        RestUserProfile other = (RestUserProfile)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$userKey = this.getUserKey();
        String other$userKey = other.getUserKey();
        if (this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$fullName = this.getFullName();
        String other$fullName = other.getFullName();
        if (this$fullName == null ? other$fullName != null : !this$fullName.equals(other$fullName)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        return !(this$email == null ? other$email != null : !this$email.equals(other$email));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestUserProfile;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $fullName = this.getFullName();
        result = result * 59 + ($fullName == null ? 43 : $fullName.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        return result;
    }

    public String toString() {
        return "RestUserProfile(userKey=" + this.getUserKey() + ", username=" + this.getUsername() + ", fullName=" + this.getFullName() + ", email=" + this.getEmail() + ")";
    }

    public RestUserProfile() {
    }

    public RestUserProfile(String userKey, String username, String fullName, String email) {
        this.userKey = userKey;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
    }

    public static class RestUserProfileBuilder {
        private String userKey;
        private String username;
        private String fullName;
        private String email;

        RestUserProfileBuilder() {
        }

        public RestUserProfileBuilder userKey(String userKey) {
            this.userKey = userKey;
            return this;
        }

        public RestUserProfileBuilder username(String username) {
            this.username = username;
            return this;
        }

        public RestUserProfileBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public RestUserProfileBuilder email(String email) {
            this.email = email;
            return this;
        }

        public RestUserProfile build() {
            return new RestUserProfile(this.userKey, this.username, this.fullName, this.email);
        }

        public String toString() {
            return "RestUserProfile.RestUserProfileBuilder(userKey=" + this.userKey + ", username=" + this.username + ", fullName=" + this.fullName + ", email=" + this.email + ")";
        }
    }
}

