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
@Table(name="CONTENT_PERM")
public class ContentPerm {
    @Id
    @Column(name="ID", nullable=false)
    private long id;
    @Column(name="CPS_ID")
    private long cpsId;
}

