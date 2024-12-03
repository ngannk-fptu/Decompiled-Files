/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.atlassian.confluence.extra.calendar3.events.ActiveObjectsInitializedEvent;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.EventTable;
import com.atlassian.event.api.EventListener;
import com.google.common.annotations.VisibleForTesting;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.bedework.util.calendar.PropertyIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertyToDBFieldMapperSupplier
implements Supplier<Map<PropertyIndex.PropertyInfoIndex, SimpleExpression>> {
    private final QueryDSLMapper queryDSLMapper;
    private final Map<PropertyIndex.PropertyInfoIndex, SimpleExpression> propertyToDBFieldMapper;
    private EventTable EVENT;

    @Autowired
    public PropertyToDBFieldMapperSupplier(QueryDSLMapper queryDSLMapper) {
        this.queryDSLMapper = queryDSLMapper;
        this.propertyToDBFieldMapper = new HashMap<PropertyIndex.PropertyInfoIndex, SimpleExpression>();
    }

    @VisibleForTesting
    public void setEventTable(EventTable EVENT) {
        this.EVENT = EVENT;
    }

    @EventListener
    public void onActiveObjectsInitialized(ActiveObjectsInitializedEvent event) {
        this.init();
    }

    @Override
    public Map<PropertyIndex.PropertyInfoIndex, SimpleExpression> get() {
        if (this.EVENT == null) {
            this.init();
        }
        return this.propertyToDBFieldMapper;
    }

    private void init() {
        if (this.EVENT == null) {
            this.EVENT = (EventTable)this.queryDSLMapper.getMapping(EventEntity.class);
        }
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.UID, this.EVENT.VEVENT_UID);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.DTSTART, this.EVENT.UTC_START);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.DTEND, this.EVENT.UTC_END);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.SUMMARY, this.EVENT.SUMMARY);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.URL, this.EVENT.URL);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.LOCATION, this.EVENT.LOCATION);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.DESCRIPTION, this.EVENT.DESCRIPTION);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.SEQUENCE, this.EVENT.SEQUENCE);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.LAST_MODIFIED, this.EVENT.LAST_MODIFIED);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.ORGANIZER, this.EVENT.ORGANISER);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.RRULE, this.EVENT.RECURRENCE_RULE);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.RECURRENCE_ID, this.EVENT.RECURRENCE_ID_TIMESTAMP);
        this.propertyToDBFieldMapper.put(PropertyIndex.PropertyInfoIndex.CREATED, this.EVENT.CREATED);
    }
}

