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
@Table(name="CONTENT_PERM_SET")
public class ContentPermSet {
    @Id
    @Column(name="ID", nullable=false)
    private long id;
    @Column(name="CONTENT_ID")
    private long contentId;
}

