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
@Table(name="cwd_membership")
public class CrowdMembership {
    @Id
    @Column(name="id")
    private long id;
    @Column(name="parent_id")
    private String parent;
    @Column(name="child_user_id")
    private String child;
}

