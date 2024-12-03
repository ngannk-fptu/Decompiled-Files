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
 *  org.hibernate.annotations.Type
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.CrowdDirectory;
import com.atlassian.migration.agent.entity.ProductEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@ProductEntity
@Entity
@Table(name="cwd_user")
public class CrowdUser {
    @Id
    @Column(name="id")
    private long id;
    @Column(name="user_name")
    private String username;
    @Column(name="lower_user_name")
    private String lowerUsername;
    @Type(type="org.hibernate.type.TrueFalseType")
    @Column(name="active")
    private boolean active;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="directory_id", nullable=false)
    private CrowdDirectory crowdDirectory;
}

