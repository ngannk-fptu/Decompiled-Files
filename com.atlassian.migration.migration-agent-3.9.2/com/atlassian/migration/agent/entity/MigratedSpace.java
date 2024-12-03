/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MIG_SPACES")
public class MigratedSpace {
    @Id
    @Column(name="spaceId")
    private long id;
    @Column(name="spaceName")
    private String name;
    @Column(name="spaceKey")
    private String key;
    @Column(name="cloud")
    private String cloud;

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public String getCloud() {
        return this.cloud;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }
}

