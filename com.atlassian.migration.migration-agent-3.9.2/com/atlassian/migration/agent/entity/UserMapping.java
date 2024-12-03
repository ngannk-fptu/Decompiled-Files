/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ProductEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Generated;

@ProductEntity
@Entity
@Table(name="user_mapping")
public class UserMapping {
    @Id
    @Column(name="user_key")
    private String userKey;
    @Column(name="username")
    private String username;
    @Column(name="lower_username")
    private String lowerUsername;

    @Generated
    public String getUserKey() {
        return this.userKey;
    }

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public String getLowerUsername() {
        return this.lowerUsername;
    }

    @Generated
    public String toString() {
        return "UserMapping(userKey=" + this.getUserKey() + ", username=" + this.getUsername() + ", lowerUsername=" + this.getLowerUsername() + ")";
    }

    @Generated
    public UserMapping() {
    }

    @Generated
    public UserMapping(String userKey, String username, String lowerUsername) {
        this.userKey = userKey;
        this.username = username;
        this.lowerUsername = lowerUsername;
    }
}

