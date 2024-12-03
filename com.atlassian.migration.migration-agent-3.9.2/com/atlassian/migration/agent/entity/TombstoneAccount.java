/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Table
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.WithId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name="MIG_TOMBSTONE_ACCOUNT")
public class TombstoneAccount
extends WithId {
    @Column(name="userKey", nullable=false)
    private String userKey;
    @Column(name="aaid", nullable=false)
    private String aaid;

    public TombstoneAccount() {
    }

    public TombstoneAccount(String userKey, String aaid) {
        this.userKey = userKey;
        this.aaid = aaid;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getAaid() {
        return this.aaid;
    }

    public void setAaid(String aaid) {
        this.aaid = aaid;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}

