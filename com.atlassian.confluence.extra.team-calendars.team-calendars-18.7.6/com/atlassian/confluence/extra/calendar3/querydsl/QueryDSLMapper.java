/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.querydsl;

import com.atlassian.confluence.extra.calendar3.exception.InfrastructureException;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.JiraReminderEventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderUsersEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.CustomEventTypeTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.EventRecurrenceExclusionTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.EventTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.InviteeTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.JiraReminderEventTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.ReminderSettingTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.ReminderUserTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.SubCalendarTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal.CwdUser;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal.CwdUserTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal.UserMapping;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal.UserMappingTable;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.querydsl.sql.RelationalPathBase;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryDSLMapper {
    private final Map<Class, Function<String, RelationalPathBase>> mappingSuppliersMap;
    private final DatabaseNameHelper databaseNameHelper;

    @Autowired
    public QueryDSLMapper(DatabaseNameHelper databaseNameHelper) {
        this.databaseNameHelper = databaseNameHelper;
        this.mappingSuppliersMap = this.initializeMappingSuppliers();
    }

    public boolean isReady() {
        return this.databaseNameHelper.isQueryDslReady();
    }

    public <T> RelationalPathBase<T> getMapping(Class<T> clazz) {
        return this.getMapping(clazz, null);
    }

    public <T> RelationalPathBase<T> getMapping(Class<T> clazz, String variable) {
        Function<String, RelationalPathBase> mappingSupplier = this.mappingSuppliersMap.get(clazz);
        if (mappingSupplier == null) {
            throw new InfrastructureException("There is no QueryDSL mapping for this class. You need to create one.");
        }
        return (RelationalPathBase)mappingSupplier.apply((Object)variable);
    }

    public InviteeTable getInviteeTable() {
        return (InviteeTable)this.getMapping(InviteeEntity.class);
    }

    public UserMappingTable getUserMappingTable() {
        return (UserMappingTable)this.getMapping(UserMapping.class);
    }

    public CwdUserTable getCwdUserTable() {
        return (CwdUserTable)this.getMapping(CwdUser.class);
    }

    public EventTable getEventsTable() {
        return (EventTable)this.getMapping(EventEntity.class);
    }

    public SubCalendarTable getSubCalendarTable() {
        return (SubCalendarTable)this.getMapping(SubCalendarEntity.class);
    }

    private ImmutableMap<Class, Function<String, RelationalPathBase>> initializeMappingSuppliers() {
        return ImmutableMap.builder().put(CustomEventTypeEntity.class, input -> new CustomEventTypeTable(this.databaseNameHelper, (String)input)).put(SubCalendarEntity.class, input -> new SubCalendarTable(this.databaseNameHelper, (String)input)).put(EventEntity.class, input -> new EventTable(this.databaseNameHelper, (String)input)).put(ReminderSettingEntity.class, input -> new ReminderSettingTable(this.databaseNameHelper, (String)input)).put(ReminderUsersEntity.class, input -> new ReminderUserTable(this.databaseNameHelper, (String)input)).put(InviteeEntity.class, input -> new InviteeTable(this.databaseNameHelper, (String)input)).put(EventRecurrenceExclusionEntity.class, input -> new EventRecurrenceExclusionTable(this.databaseNameHelper, (String)input)).put(JiraReminderEventEntity.class, input -> new JiraReminderEventTable(this.databaseNameHelper, (String)input)).put(UserMapping.class, input -> new UserMappingTable(this.databaseNameHelper, (String)input)).put(CwdUser.class, input -> new CwdUserTable(this.databaseNameHelper, (String)input)).build();
    }
}

