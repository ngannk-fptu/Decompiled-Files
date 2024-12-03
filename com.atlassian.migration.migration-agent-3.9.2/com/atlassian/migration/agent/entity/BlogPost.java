/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.DiscriminatorValue
 *  javax.persistence.Entity
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.SpaceContent;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="BLOGPOST")
public class BlogPost
extends SpaceContent {
}

