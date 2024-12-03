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
import com.atlassian.migration.agent.entity.SpaceContent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@ProductEntity
@Entity
@Table(name="CONTENTPROPERTIES")
public class ContentProperty {
    @Id
    @Column(name="PROPERTYID")
    private long id;
    @Column(name="PROPERTYNAME")
    private String name;
    @Column(name="LONGVAL")
    private long longval;
    @Column(name="STRINGVAL")
    private String stringval;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CONTENTID")
    private SpaceContent content;
}

