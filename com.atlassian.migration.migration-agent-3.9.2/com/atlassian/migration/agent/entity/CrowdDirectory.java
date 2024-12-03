/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  org.hibernate.annotations.Type
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ProductEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@ProductEntity
@Entity
@Table(name="cwd_directory")
public class CrowdDirectory {
    @Id
    @Column(name="id")
    private long id;
    @Type(type="org.hibernate.type.TrueFalseType")
    @Column(name="active")
    private boolean active;
}

