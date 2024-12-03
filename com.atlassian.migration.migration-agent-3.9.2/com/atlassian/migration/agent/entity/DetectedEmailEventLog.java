/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.IdClass
 *  javax.persistence.Table
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.DetectedEventLogId;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name="MIG_DETECTED_EVENT_LOG")
@IdClass(value=DetectedEventLogId.class)
public class DetectedEmailEventLog {
    @Id
    @Column(name="cloudId", nullable=false)
    private String cloudId;
    @Id
    @Column(name="email", nullable=false)
    private String email;
    @Column(name="created", nullable=false)
    private Instant created;

    public DetectedEmailEventLog() {
    }

    public DetectedEmailEventLog(String cloudId, String email) {
        this.created = Instant.now();
        this.cloudId = cloudId;
        this.email = email;
    }

    public Instant getCreated() {
        return this.created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getCloudId() {
        return this.cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}

