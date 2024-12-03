/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.pats.rest;

import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.rest.DateAdapter;
import com.atlassian.pats.rest.RestUserProfile;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class RestToken {
    @XmlElement
    private Long id;
    @XmlElement
    private RestUserProfile userProfileResource;
    @XmlElement
    private String name;
    @XmlElement
    @XmlJavaTypeAdapter(value=DateAdapter.class)
    private Date createdAt;
    @XmlElement
    @XmlJavaTypeAdapter(value=DateAdapter.class)
    private Date lastAccessedAt;
    @XmlElement
    @XmlJavaTypeAdapter(value=DateAdapter.class)
    private Date expiringAt;
    @XmlElement
    private String rawToken;

    public String getUsername() {
        return Optional.ofNullable(this.userProfileResource).map(RestUserProfile::getUsername).orElse(null);
    }

    public String getUserKey() {
        return Optional.ofNullable(this.userProfileResource).map(RestUserProfile::getUserKey).orElse(null);
    }

    public static RestToken valueOf(TokenDTO token) {
        return RestToken.valueOf(token, null);
    }

    public static RestToken valueOf(TokenDTO token, UserProfile userProfile) {
        RestTokenBuilder restToken = RestToken.builder().id(token.getId()).rawToken(token.getRawToken()).createdAt(token.getCreatedAt()).expiringAt(token.getExpiringAt()).lastAccessedAt(token.getLastAccessedAt()).name(token.getName());
        return Objects.nonNull(userProfile) ? restToken.userProfileResource(RestUserProfile.valueOf(userProfile)).build() : restToken.build();
    }

    public static RestTokenBuilder builder() {
        return new RestTokenBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public RestUserProfile getUserProfileResource() {
        return this.userProfileResource;
    }

    public String getName() {
        return this.name;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public Date getLastAccessedAt() {
        return this.lastAccessedAt;
    }

    public Date getExpiringAt() {
        return this.expiringAt;
    }

    public String getRawToken() {
        return this.rawToken;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserProfileResource(RestUserProfile userProfileResource) {
        this.userProfileResource = userProfileResource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastAccessedAt(Date lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public void setExpiringAt(Date expiringAt) {
        this.expiringAt = expiringAt;
    }

    public void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestToken)) {
            return false;
        }
        RestToken other = (RestToken)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        RestUserProfile this$userProfileResource = this.getUserProfileResource();
        RestUserProfile other$userProfileResource = other.getUserProfileResource();
        if (this$userProfileResource == null ? other$userProfileResource != null : !((Object)this$userProfileResource).equals(other$userProfileResource)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        Date this$createdAt = this.getCreatedAt();
        Date other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        Date this$lastAccessedAt = this.getLastAccessedAt();
        Date other$lastAccessedAt = other.getLastAccessedAt();
        if (this$lastAccessedAt == null ? other$lastAccessedAt != null : !((Object)this$lastAccessedAt).equals(other$lastAccessedAt)) {
            return false;
        }
        Date this$expiringAt = this.getExpiringAt();
        Date other$expiringAt = other.getExpiringAt();
        if (this$expiringAt == null ? other$expiringAt != null : !((Object)this$expiringAt).equals(other$expiringAt)) {
            return false;
        }
        String this$rawToken = this.getRawToken();
        String other$rawToken = other.getRawToken();
        return !(this$rawToken == null ? other$rawToken != null : !this$rawToken.equals(other$rawToken));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestToken;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        RestUserProfile $userProfileResource = this.getUserProfileResource();
        result = result * 59 + ($userProfileResource == null ? 43 : ((Object)$userProfileResource).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Date $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        Date $lastAccessedAt = this.getLastAccessedAt();
        result = result * 59 + ($lastAccessedAt == null ? 43 : ((Object)$lastAccessedAt).hashCode());
        Date $expiringAt = this.getExpiringAt();
        result = result * 59 + ($expiringAt == null ? 43 : ((Object)$expiringAt).hashCode());
        String $rawToken = this.getRawToken();
        result = result * 59 + ($rawToken == null ? 43 : $rawToken.hashCode());
        return result;
    }

    public String toString() {
        return "RestToken(id=" + this.getId() + ", userProfileResource=" + this.getUserProfileResource() + ", name=" + this.getName() + ", createdAt=" + this.getCreatedAt() + ", lastAccessedAt=" + this.getLastAccessedAt() + ", expiringAt=" + this.getExpiringAt() + ", rawToken=" + this.getRawToken() + ")";
    }

    public RestToken() {
    }

    public RestToken(Long id, RestUserProfile userProfileResource, String name, Date createdAt, Date lastAccessedAt, Date expiringAt, String rawToken) {
        this.id = id;
        this.userProfileResource = userProfileResource;
        this.name = name;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;
        this.expiringAt = expiringAt;
        this.rawToken = rawToken;
    }

    public static class RestTokenBuilder {
        private Long id;
        private RestUserProfile userProfileResource;
        private String name;
        private Date createdAt;
        private Date lastAccessedAt;
        private Date expiringAt;
        private String rawToken;

        RestTokenBuilder() {
        }

        public RestTokenBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RestTokenBuilder userProfileResource(RestUserProfile userProfileResource) {
            this.userProfileResource = userProfileResource;
            return this;
        }

        public RestTokenBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RestTokenBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RestTokenBuilder lastAccessedAt(Date lastAccessedAt) {
            this.lastAccessedAt = lastAccessedAt;
            return this;
        }

        public RestTokenBuilder expiringAt(Date expiringAt) {
            this.expiringAt = expiringAt;
            return this;
        }

        public RestTokenBuilder rawToken(String rawToken) {
            this.rawToken = rawToken;
            return this;
        }

        public RestToken build() {
            return new RestToken(this.id, this.userProfileResource, this.name, this.createdAt, this.lastAccessedAt, this.expiringAt, this.rawToken);
        }

        public String toString() {
            return "RestToken.RestTokenBuilder(id=" + this.id + ", userProfileResource=" + this.userProfileResource + ", name=" + this.name + ", createdAt=" + this.createdAt + ", lastAccessedAt=" + this.lastAccessedAt + ", expiringAt=" + this.expiringAt + ", rawToken=" + this.rawToken + ")";
        }
    }
}

