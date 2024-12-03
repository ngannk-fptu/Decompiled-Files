/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class ReminderSettingTable
extends AbstractRelationalPathBase<ReminderSettingEntity> {
    public static final String TABLE_NAME = "AO_950DC3_TC_REMINDER_SETTINGS";
    public final StringPath ID = this.createString("ID");
    public final StringPath STORE_KEY = this.createString("STORE_KEY");
    public final StringPath CUSTOM_EVENT_TYPE_ID = this.createString("CUSTOM_EVENT_TYPE_ID");
    public final StringPath SUB_CALENDAR_ID = this.createString("SUB_CALENDAR_ID");
    public final NumberPath<Long> PERIOD = this.createNumber("PERIOD", Long.class);
    public final StringPath LAST_MODIFIER = this.createString("LAST_MODIFIER");

    public ReminderSettingTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(ReminderSettingEntity.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

