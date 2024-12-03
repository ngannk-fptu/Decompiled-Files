/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.BodyContent
 */
package com.atlassian.confluence.extra.masterdetail.persistence.entities;

import com.atlassian.confluence.core.BodyContent;
import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class BodyContentTable
extends EnhancedRelationalPathBase<BodyContent> {
    public final NumberPath<Long> BODY_CONTENT_ID = this.createLongCol("bodycontentid").asPrimaryKey().build();
    public final StringPath BODY = this.createStringCol("body").build();
    public final NumberPath<Long> CONTENT_ID = this.createLongCol("contentid").build();
    public final NumberPath<Integer> BODY_TYPE_ID = this.createIntegerCol("bodytypeid").build();

    public BodyContentTable(String logicalTableName) {
        super(BodyContent.class, logicalTableName);
    }
}

