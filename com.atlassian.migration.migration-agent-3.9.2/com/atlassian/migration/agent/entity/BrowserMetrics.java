/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Index
 *  javax.persistence.Inheritance
 *  javax.persistence.InheritanceType
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.WithId;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="BROWSER_METRICS", indexes={@Index(name="BROWSER_METRICS_USERKEY_IDX", columnList="userKey")})
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class BrowserMetrics
extends WithId {
    @Column(name="createdAt", nullable=false)
    private long createdAt;
    @Column(name="userKey", nullable=false)
    private String userKey;
    @Column(name="metricsJson", nullable=false)
    private String metricsJson;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BrowserMetrics that = (BrowserMetrics)o;
        return this.createdAt == that.createdAt && Objects.equals(this.userKey, that.userKey) && Objects.equals(this.metricsJson, that.metricsJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.createdAt, this.userKey, this.metricsJson);
    }

    @Generated
    public long getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public String getUserKey() {
        return this.userKey;
    }

    @Generated
    public String getMetricsJson() {
        return this.metricsJson;
    }

    @Generated
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    @Generated
    public void setMetricsJson(String metricsJson) {
        this.metricsJson = metricsJson;
    }

    @Generated
    public BrowserMetrics(long createdAt, String userKey, String metricsJson) {
        this.createdAt = createdAt;
        this.userKey = userKey;
        this.metricsJson = metricsJson;
    }

    @Generated
    public BrowserMetrics() {
    }
}

