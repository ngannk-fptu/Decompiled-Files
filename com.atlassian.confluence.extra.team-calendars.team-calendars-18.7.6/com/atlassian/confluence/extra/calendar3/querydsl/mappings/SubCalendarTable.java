/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class SubCalendarTable
extends AbstractRelationalPathBase<SubCalendarEntity> {
    public static final String TABLE_NAME = "AO_950DC3_TC_SUBCALS";
    public final NumberPath<Long> CREATED = this.createNumber("CREATED", Long.class);
    public final StringPath COLOUR = this.createString("COLOUR");
    public final StringPath CREATOR = this.createString("CREATOR");
    public final StringPath DESCRIPTION = this.createString("DESCRIPTION");
    public final StringPath ID = this.createString("ID");
    public final StringPath PARENT_ID = this.createString("PARENT_ID");
    public final StringPath NAME = this.createString("NAME");
    public final StringPath SPACE_KEY = this.createString("SPACE_KEY");
    public final StringPath STORE_KEY = this.createString("STORE_KEY");
    public final StringPath SUBSCRIPTION_ID = this.createString("SUBSCRIPTION_ID");
    public final StringPath TIME_ZONE_ID = this.createString("TIME_ZONE_ID");
    public final StringPath USING_CUSTOM_EVENT_TYPE_ID = this.createString("USING_CUSTOM_EVENT_TYPE_ID");

    public SubCalendarTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(SubCalendarEntity.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

