/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Table
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.WithId;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.google.common.base.Strings;
import java.time.Instant;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name="MIG_INCORRECT_EMAIL")
public class IncorrectEmail
extends WithId {
    @Column(name="userKey")
    private String userKey;
    @Column(name="userName", nullable=false)
    private String userName;
    @Column(name="newEmail")
    private String newEmail;
    @Column(name="email")
    private String email;
    @Column(name="checkType", nullable=false)
    private String checkType;
    @Column(name="created", nullable=false)
    private Instant created;
    @Column(name="scanId", nullable=false)
    private String scanId;
    @Column(name="directoryId", nullable=false)
    private Long directoryId;
    @Column(name="directoryName", nullable=false)
    private String directoryName;
    @Column(name="lastAuthenticated")
    private Long lastAuthenticated;

    public IncorrectEmail() {
    }

    public IncorrectEmail(String userKey, String userName, @Nullable String newEmail, @Nullable String email, CheckType checkType, Instant created, String scanId, Long directoryId, String directoryName, @Nullable Long lastAuthenticated) {
        this.userKey = userKey;
        this.userName = userName;
        this.newEmail = newEmail;
        this.email = email;
        this.checkType = checkType.value();
        this.created = created;
        this.scanId = scanId;
        this.directoryId = directoryId;
        this.directoryName = directoryName;
        this.lastAuthenticated = lastAuthenticated;
    }

    public MigrationUser toMigrationUser() {
        return new MigrationUser(Strings.nullToEmpty((String)this.userKey), this.getUserName(), "", this.email, true);
    }

    public String getUserKey() {
        return this.userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Nullable
    public String getNewEmail() {
        return this.newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    @Nullable
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CheckType getCheckType() {
        return CheckType.fromString(this.checkType);
    }

    public void setCheckType(CheckType checkType) {
        this.checkType = checkType.value();
    }

    public Instant getCreated() {
        return this.created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getScanId() {
        return this.scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public Long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    @Nullable
    public Long getLastAuthenticated() {
        return this.lastAuthenticated;
    }

    public void setLastAuthenticated(@Nullable Long lastAuthenticated) {
        this.lastAuthenticated = lastAuthenticated;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}

