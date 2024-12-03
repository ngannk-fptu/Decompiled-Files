/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.querydsl.core.types.dsl.StringPath;

public class CustomEventTypeTable
extends AbstractRelationalPathBase<CustomEventTypeEntity> {
    public static final String TABLE_NAME = "AO_950DC3_TC_CUSTOM_EV_TYPES";
    public final StringPath ID = this.createString("ID");
    public final StringPath TITLE = this.createString("TITLE");
    public final StringPath ICON = this.createString("ICON");

    public CustomEventTypeTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(CustomEventTypeEntity.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

