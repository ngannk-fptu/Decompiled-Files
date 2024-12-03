/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.PrimaryKey;

public class EventTable
extends AbstractRelationalPathBase<EventEntity> {
    public static final String TABLE_NAME = "AO_950DC3_TC_EVENTS";
    public final BooleanPath ALL_DAY = this.createBoolean("ALL_DAY");
    public final NumberPath<Long> CREATED = this.createNumber("CREATED", Long.class);
    public final StringPath DESCRIPTION = this.createString("DESCRIPTION");
    public final NumberPath<Long> END = this.createNumber("END", Long.class);
    public final NumberPath<Long> UTC_START = this.createNumber("UTC_START", Long.class);
    public final NumberPath<Long> UTC_END = this.createNumber("UTC_END", Long.class);
    public final NumberPath<Integer> ID = this.createNumber("ID", Integer.class);
    public final NumberPath<Long> LAST_MODIFIED = this.createNumber("LAST_MODIFIED", Long.class);
    public final StringPath LOCATION = this.createString("LOCATION");
    public final StringPath ORGANISER = this.createString("ORGANISER");
    public final NumberPath<Long> RECURRENCE_ID_TIMESTAMP = this.createNumber("RECURRENCE_ID_TIMESTAMP", Long.class);
    public final StringPath RECURRENCE_RULE = this.createString("RECURRENCE_RULE");
    public final NumberPath<Integer> SEQUENCE = this.createNumber("SEQUENCE", Integer.class);
    public final NumberPath<Long> START = this.createNumber("START", Long.class);
    public final StringPath SUB_CALENDAR_ID = this.createString("SUB_CALENDAR_ID");
    public final StringPath REMINDER_SETTING_ID = this.createString("REMINDER_SETTING_ID");
    public final StringPath SUMMARY = this.createString("SUMMARY");
    public final StringPath URL = this.createString("URL");
    public final StringPath VEVENT_UID = this.createString("VEVENT_UID");
    public final PrimaryKey<EventEntity> TC_EVENTS_PK = this.createPrimaryKey(this.ID);

    public EventTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(EventEntity.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

