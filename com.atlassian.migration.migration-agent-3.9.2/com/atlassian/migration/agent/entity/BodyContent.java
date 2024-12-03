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
@Table(name="BODYCONTENT")
public class BodyContent {
    @Id
    @Column(name="BODYCONTENTID", nullable=false)
    private long bodyContentId;
    @Column(name="CONTENTID")
    private long contentId;
    @Column(name="BODY")
    private String body;
}

