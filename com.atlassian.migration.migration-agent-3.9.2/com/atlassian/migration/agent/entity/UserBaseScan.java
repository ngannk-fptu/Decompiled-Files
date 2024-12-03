/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  org.apache.commons.lang3.builder.ReflectionToStringBuilder
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ScanStatus;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
@Table(name="MIG_USERBASE_SCAN")
public class UserBaseScan {
    @Id
    @Column(name="id", nullable=false)
    private String id;
    @Column(name="status", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private ScanStatus status;
    @Column(name="invalidUsers", nullable=false)
    private long invalidUsers;
    @Column(name="duplicateUsers", nullable=false)
    private long duplicateUsers;
    @Column(name="started", nullable=false)
    private Instant started;
    @Column(name="finished")
    private Instant finished;
    @Column(name="created", nullable=false)
    private Instant created;

    public UserBaseScan() {
    }

    public UserBaseScan(String id, ScanStatus status, long invalidUsers, long duplicateUsers, Instant started) {
        this.id = id;
        this.status = status;
        this.invalidUsers = invalidUsers;
        this.duplicateUsers = duplicateUsers;
        this.started = started;
        this.created = started;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ScanStatus getStatus() {
        return this.status;
    }

    public void setStatus(ScanStatus status) {
        this.status = status;
    }

    public long getInvalidUsers() {
        return this.invalidUsers;
    }

    public void setInvalidUsers(long invalidUsers) {
        this.invalidUsers = invalidUsers;
    }

    public long getDuplicateUsers() {
        return this.duplicateUsers;
    }

    public void setDuplicateUsers(long duplicateUsers) {
        this.duplicateUsers = duplicateUsers;
    }

    public Instant getStarted() {
        return this.started;
    }

    public void setStarted(Instant started) {
        this.started = started;
    }

    public Instant getFinished() {
        return this.finished;
    }

    public void setFinished(Instant finished) {
        this.finished = finished;
    }

    public Instant getCreated() {
        return this.created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString((Object)this);
    }
}

