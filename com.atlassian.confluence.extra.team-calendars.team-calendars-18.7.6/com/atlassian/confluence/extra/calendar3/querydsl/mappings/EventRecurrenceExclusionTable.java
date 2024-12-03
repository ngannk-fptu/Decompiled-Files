/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberPath;

public class EventRecurrenceExclusionTable
extends AbstractRelationalPathBase<EventRecurrenceExclusionEntity> {
    public static final String TABLE_NAME = "AO_950DC3_TC_EVENTS_EXCL";
    public final NumberPath<Integer> ID = this.createNumber("ID", Integer.class);
    public final NumberPath<Integer> EVENT_ID = this.createNumber("EVENT_ID", Integer.class);
    public final NumberPath<Long> EXCLUSION = this.createNumber("EXCLUSION", Long.class);
    public final BooleanPath ALL_DAY = this.createBoolean("ALL_DAY");

    public EventRecurrenceExclusionTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(EventRecurrenceExclusionEntity.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

