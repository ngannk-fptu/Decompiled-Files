/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.persistence.Column
 *  javax.persistence.Convert
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.dto.CloudType;
import com.atlassian.migration.agent.entity.CloudEdition;
import com.atlassian.migration.agent.service.encryption.EncryptedContainerTokenConverter;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Table(name="MIG_CLOUD_SITE")
@Entity
public class CloudSite {
    @Id
    @Column(name="cloudId", nullable=false, unique=true)
    private String cloudId;
    @Column(name="cloudUrl", nullable=false, length=1024, unique=true)
    private String cloudUrl;
    @Column(name="containerToken", nullable=false, unique=true)
    @Convert(converter=EncryptedContainerTokenConverter.class)
    private String containerToken;
    @Column(name="isFailing", nullable=false)
    private boolean isFailing;
    @Column(name="mediaClientId")
    private String mediaClientId;
    @Column(name="createdTime")
    private Instant createdTime = Instant.now();
    @Enumerated(value=EnumType.STRING)
    @Column(name="edition")
    private CloudEdition edition;
    @Column(name="cloudType", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private CloudType cloudType;

    public CloudSite() {
    }

    public CloudSite(CloudSite other) {
        this.cloudId = other.cloudId;
        this.cloudUrl = other.cloudUrl;
        this.cloudType = other.cloudType;
    }

    public CloudSite(String cloudId, String cloudUrl, String containerToken, CloudType cloudType) {
        this.cloudId = cloudId;
        this.cloudUrl = cloudUrl;
        this.containerToken = containerToken;
        this.cloudType = cloudType;
    }

    public CloudSite(String cloudId, String cloudUrl, String containerToken) {
        this.cloudId = cloudId;
        this.cloudUrl = cloudUrl;
        this.containerToken = containerToken;
        this.cloudType = CloudType.STANDARD;
    }

    public Instant getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public String getCloudId() {
        return this.cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getCloudUrl() {
        return this.cloudUrl;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public String getContainerToken() {
        return this.containerToken;
    }

    public void setContainerToken(String containerToken) {
        this.containerToken = containerToken;
    }

    public boolean isFailing() {
        return this.isFailing;
    }

    public void setFailing(boolean failing) {
        this.isFailing = failing;
    }

    public String getMediaClientId() {
        return this.mediaClientId;
    }

    public void setMediaClientId(String mediaClientId) {
        this.mediaClientId = mediaClientId;
    }

    @Nullable
    public CloudEdition getEdition() {
        return this.edition;
    }

    public void setEdition(CloudEdition edition) {
        this.edition = edition;
    }

    public CloudType getCloudType() {
        return this.cloudType;
    }

    public void setCloudType(CloudType cloudType) {
        this.cloudType = cloudType;
    }

    public CloudSite withEdition(Optional<CloudEdition> maybeCloudEdition) {
        maybeCloudEdition.ifPresent(this::setEdition);
        return this;
    }

    public CloudSite withMediaClientId(@Nullable String mediaClientId) {
        this.setMediaClientId(mediaClientId);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CloudSite cloudSite = (CloudSite)o;
        return Objects.equals(this.cloudId, cloudSite.cloudId);
    }

    public int hashCode() {
        return Objects.hash(this.cloudId);
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.NO_CLASS_NAME_STYLE).append("cloudId", (Object)this.cloudId).append("cloudUrl", (Object)this.cloudUrl).build();
    }
}

