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

import com.atlassian.migration.agent.entity.ProductEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@ProductEntity
@Entity
@Table(name="SPACES")
public class Space {
    @Id
    @Column(name="SPACEID")
    private long id;
    @Column(name="SPACENAME")
    private String name;
    @Column(name="SPACEKEY")
    private String key;
    @Column(name="SPACETYPE")
    private String type;
    @Column(name="SPACESTATUS")
    private String status;
    @Column(name="CREATOR")
    private String creator;

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public String getType() {
        return this.type;
    }

    public String getStatus() {
        return this.status;
    }
}

