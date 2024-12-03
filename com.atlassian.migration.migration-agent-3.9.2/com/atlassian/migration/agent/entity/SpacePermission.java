/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.FetchType
 *  javax.persistence.Id
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ProductEntity;
import com.atlassian.migration.agent.entity.Space;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@ProductEntity
@Entity
@Table(name="SPACEPERMISSIONS")
public class SpacePermission {
    @Id
    @Column(name="PERMID")
    private long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SPACEID", nullable=false)
    private Space space;
    @Column(name="PERMGROUPNAME")
    private String permGroupName;
    @Column(name="PERMUSERNAME")
    private String permUsername;
    @Column(name="PERMTYPE")
    private String permType;

    public Space getSpace() {
        return this.space;
    }
}

