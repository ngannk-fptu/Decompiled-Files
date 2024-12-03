/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  javax.xml.bind.annotation.XmlElement
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  org.joda.time.DateTime
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.GenericMessage;
import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.atlassian.confluence.extra.calendar3.calendarstore.BaseCacheableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.DelegatableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.SubscribingCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.LightweightPersistentSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarGroupRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarUserRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.util.UUIDGenerate;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.message.Message;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlElement;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="internalSubscriptionCalendarDataStore")
public class InternalSubscriptionCalendarDataStore
extends BaseCacheableCalendarDataStore<InternalSubscriptionSubCalendar>
implements RefreshableCalendarDataStore<InternalSubscriptionSubCalendar>,
SubscribingCalendarDataStore<InternalSubscriptionSubCalendar>,
DelegatableCalendarDataStore<InternalSubscriptionSubCalendar> {
    public static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore";
    public static final String SUB_CALENDAR_TYPE = "internal-subscription";
    private static final String SUBSCRIPTION_SOURCE_LOCATION_PREFIX = "subscription://";
    private static final Pattern SUBSCRIPTION_LOCATION_PATTERN = Pattern.compile("^subscription://(.+)$");
    private final CalendarDataStore<PersistedSubCalendar> calendarDataStoreDelegate;
    private final SubCalendarColorRegistry subCalendarColorRegistry;

    @Autowired
    public InternalSubscriptionCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, @Qualifier(value="internalSubscriptionCalendarDataStoreDelegate") CalendarDataStore<PersistedSubCalendar> calendarDataStoreDelegate, SubCalendarColorRegistry subCalendarColorRegistry) {
        super(dataStoreCommonPropertyAccessor);
        this.calendarDataStoreDelegate = calendarDataStoreDelegate;
        this.subCalendarColorRegistry = subCalendarColorRegistry;
    }

    @Override
    protected SubCalendarSummary toSummary(SubCalendarEntity subCalendarEntity) {
        SubCalendarEntity parentEntity = subCalendarEntity.getParent();
        return new SubscribingSubCalendarSummary(parentEntity == null ? null : parentEntity.getID(), subCalendarEntity.getID(), this.getType(), subCalendarEntity.getName(), subCalendarEntity.getDescription(), subCalendarEntity.getColour(), subCalendarEntity.getCreator(), subCalendarEntity.getSubscription().getID());
    }

    @Override
    protected String getStoreKey() {
        return STORE_KEY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected InternalSubscriptionSubCalendar fromStorageFormat(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)("InternalSubcriptionSubCalendar.fromStorageFormat() - " + subCalendarEntity.getName()));
        try {
            InternalSubscriptionSubCalendar internalSubscriptionSubCalendar = new InternalSubscriptionSubCalendar();
            SubCalendarEntity parentEntity = subCalendarEntity.getParent();
            if (parentEntity != null) {
                internalSubscriptionSubCalendar.setParentResolver(new InternalSubscriptionSubCalendarParentResolver(parentEntity.getID()));
            }
            internalSubscriptionSubCalendar.setId(subCalendarEntity.getID());
            internalSubscriptionSubCalendar.setName(subCalendarEntity.getName());
            internalSubscriptionSubCalendar.setDescription(subCalendarEntity.getDescription());
            internalSubscriptionSubCalendar.setColor(subCalendarEntity.getColour());
            internalSubscriptionSubCalendar.setCreator(subCalendarEntity.getCreator());
            internalSubscriptionSubCalendar.setCreated(subCalendarEntity.getCreated());
            internalSubscriptionSubCalendar.setStoreKey(this.getStoreKey());
            internalSubscriptionSubCalendar.setCreated(subCalendarEntity.getCreated());
            internalSubscriptionSubCalendar.setLastUpdateDate(subCalendarEntity.getLastModified());
            SubCalendarEntity sourceSubCalendarEntity = this.getSubCalendarEntity(subCalendarEntity.getSubscription().getID());
            if (null != sourceSubCalendarEntity) {
                UtilTimerStack.push((String)("InternalSubcriptionSubCalendar.fromStorageFormat() - Target Calendar" + sourceSubCalendarEntity.getName()));
                PersistedSubCalendar sourceSubCalendar = this.convertSubCalendarEntityToPersisted(sourceSubCalendarEntity);
                UtilTimerStack.pop((String)("InternalSubcriptionSubCalendar.fromStorageFormat() - Target Calendar" + sourceSubCalendarEntity.getName()));
                if (internalSubscriptionSubCalendar.getCreated() > 0L) {
                    internalSubscriptionSubCalendar.setName(sourceSubCalendar.getName());
                }
                internalSubscriptionSubCalendar.setTimeZoneId(sourceSubCalendar.getTimeZoneId());
                internalSubscriptionSubCalendar.setSourceSubCalendar(sourceSubCalendar);
                internalSubscriptionSubCalendar.setSourceLocation(SUBSCRIPTION_SOURCE_LOCATION_PREFIX + internalSubscriptionSubCalendar.getSubscriptionId());
                internalSubscriptionSubCalendar.setSpaceKey(sourceSubCalendar.getSpaceKey());
                internalSubscriptionSubCalendar.setSpaceName(sourceSubCalendar.getSpaceName());
                internalSubscriptionSubCalendar.setTypeKey(sourceSubCalendar.getTypeKey());
                if (parentEntity != null) {
                    internalSubscriptionSubCalendar.setChildSubCalendarIds(this.getChildSubCalendarIds(subCalendarEntity));
                } else {
                    internalSubscriptionSubCalendar.setDisableEventTypes(sourceSubCalendar.getDisableEventTypes());
                    internalSubscriptionSubCalendar.setChildSubCalendarIds(this.getFilterChildInternalSubscriptionSubCalendarIds(subCalendarEntity, sourceSubCalendar));
                    internalSubscriptionSubCalendar.setCustomEventTypes(sourceSubCalendar.getCustomEventTypes());
                    internalSubscriptionSubCalendar.setEventTypeReminders(sourceSubCalendar.getEventTypeReminders());
                }
            }
            InternalSubscriptionSubCalendar internalSubscriptionSubCalendar2 = internalSubscriptionSubCalendar;
            return internalSubscriptionSubCalendar2;
        }
        finally {
            UtilTimerStack.pop((String)("InternalSubcriptionSubCalendar.fromStorageFormat() - " + subCalendarEntity.getName()));
        }
    }

    @Override
    protected void loadAdditionalRestrictions(PersistedSubCalendar persistedCalendar, List<SubCalendarGroupRestrictionEntity> groupRestrictionEntities, List<SubCalendarUserRestrictionEntity> userRestrictionEntities) {
        super.loadAdditionalRestrictions(persistedCalendar, groupRestrictionEntities, userRestrictionEntities);
        InternalSubscriptionSubCalendar subscriptionSubCalendar = (InternalSubscriptionSubCalendar)persistedCalendar;
        PersistedSubCalendar persistedSubscriptionCalendar = subscriptionSubCalendar.getSourceSubCalendar();
        if (persistedSubscriptionCalendar != null) {
            this.loadRestrictions(persistedSubscriptionCalendar, groupRestrictionEntities, userRestrictionEntities);
        }
        if (persistedSubscriptionCalendar.getParent() != null) {
            this.loadRestrictions(persistedSubscriptionCalendar.getParent(), groupRestrictionEntities, userRestrictionEntities);
        }
    }

    @Override
    protected Set<String> getChildSubCalendarIds(SubCalendarEntity subCalendarEntity) {
        this.createNewSubscribingSubCalendarsIfNeccessary(subCalendarEntity);
        return super.getChildSubCalendarIds(subCalendarEntity);
    }

    @Override
    protected Set<String> getFilterChildInternalSubscriptionSubCalendarIds(SubCalendarEntity subCalendarEntity, PersistedSubCalendar sourcePersistedSubCalendar) {
        this.createNewSubscribingSubCalendarsIfNeccessary(subCalendarEntity);
        return super.getFilterChildInternalSubscriptionSubCalendarIds(subCalendarEntity, sourcePersistedSubCalendar);
    }

    private void createNewSubscribingSubCalendarsIfNeccessary(SubCalendarEntity subCalendarEntity) {
        SubCalendarEntity[] newSourceSubCalendarEntitySubscriptions;
        if (subCalendarEntity.getParent() != null) {
            return;
        }
        SubCalendarEntity subscriptionEntity = subCalendarEntity.getSubscription();
        Object[] existingSubscribingChildrenEntities = subCalendarEntity.getChildSubCalendarEntities();
        ArrayList existingSubscribingChildrenEntityIds = Collections.emptyList();
        ArrayList querySubstitutions = Lists.newArrayList((Object[])new Object[]{subscriptionEntity.getID()});
        if (existingSubscribingChildrenEntities != null) {
            existingSubscribingChildrenEntityIds = Lists.newArrayList((Iterable)Collections2.transform((Collection)Lists.newArrayList((Object[])existingSubscribingChildrenEntities), subCalendarEntity1 -> subCalendarEntity1.getSubscription().getID()));
            querySubstitutions.addAll(existingSubscribingChildrenEntityIds);
        }
        if ((newSourceSubCalendarEntitySubscriptions = (SubCalendarEntity[])this.getActiveObjects().find(SubCalendarEntity.class, Query.select().where((String)(existingSubscribingChildrenEntityIds.isEmpty() ? "PARENT_ID = ?" : "PARENT_ID = ? AND ID NOT IN (" + StringUtils.repeat("?", ", ", existingSubscribingChildrenEntityIds.size()) + ")"), querySubstitutions.toArray()))) != null) {
            for (SubCalendarEntity newSourceSubCalendarEntitySubscription : newSourceSubCalendarEntitySubscriptions) {
                SubCalendar newChild = new SubCalendar();
                newChild.setName(subCalendarEntity.getName());
                newChild.setParent(new LightweightPersistentSubCalendar(subCalendarEntity.getID()));
                newChild.setDescription(subCalendarEntity.getDescription());
                newChild.setColor(newSourceSubCalendarEntitySubscription.getColour());
                newChild.setTimeZoneId(subCalendarEntity.getTimeZoneId());
                newChild.setSourceLocation(SUBSCRIPTION_SOURCE_LOCATION_PREFIX + newSourceSubCalendarEntitySubscription.getID());
                this.save(newChild);
            }
        }
    }

    @Override
    protected SubCalendarEntity toStorageFormat(SubCalendar subCalendar) {
        SubCalendarEntity sourceCalendarEntity;
        PersistedSubCalendar parentSubCalendar = subCalendar.getParent();
        String parentId = parentSubCalendar != null ? parentSubCalendar.getId() : null;
        SubCalendarEntity parentCalendarEntity = parentId != null ? this.getSubCalendarEntity(parentId) : null;
        String sourceParentId = this.getParentId(parentSubCalendar);
        String spaceKey = StringUtils.defaultIfBlank(subCalendar.getSpaceKey(), null);
        if (!(subCalendar instanceof PersistedSubCalendar)) {
            Matcher subscriptionMatcher = SUBSCRIPTION_LOCATION_PATTERN.matcher(subCalendar.getSourceLocation());
            String subscriptionId = null;
            if (subscriptionMatcher.matches()) {
                subscriptionId = subscriptionMatcher.group(1);
            }
            String userKey = parentCalendarEntity == null ? AuthenticatedUserThreadLocal.get().getKey().toString() : parentCalendarEntity.getCreator();
            SubCalendarEntity[] subCalendarEntities = (SubCalendarEntity[])this.getActiveObjects().find(SubCalendarEntity.class, Query.select().where("CREATOR = ? AND SUBSCRIPTION_ID = ? AND STORE_KEY = ?", new Object[]{userKey, subscriptionId, this.getStoreKey()}));
            if (subCalendarEntities != null && subCalendarEntities.length > 0) {
                this.addCalendarToSpaceView(sourceParentId, subscriptionId, spaceKey);
                return subCalendarEntities[0];
            }
            String subCalendarId = UUIDGenerate.generate();
            SubCalendarEntity subCalendarEntity = (SubCalendarEntity)this.getActiveObjects().create(SubCalendarEntity.class, new DBParam[]{new DBParam("STORE_KEY", (Object)this.getStoreKey()), new DBParam("PARENT_ID", (Object)parentId), new DBParam("ID", (Object)subCalendarId), new DBParam("NAME", (Object)subCalendar.getName()), new DBParam("DESCRIPTION", (Object)StringUtils.defaultString(subCalendar.getDescription())), new DBParam("COLOUR", (Object)subCalendar.getColor()), new DBParam("SPACE_KEY", null), new DBParam("TIME_ZONE_ID", (Object)subCalendar.getTimeZoneId()), new DBParam("SUBSCRIPTION_ID", (Object)subscriptionId), new DBParam("CREATED", (Object)System.currentTimeMillis()), new DBParam("CREATOR", (Object)userKey)});
            this.addCalendarToSpaceView(sourceParentId, subscriptionId, spaceKey);
            return subCalendarEntity;
        }
        String description = StringUtils.defaultString(subCalendar.getDescription());
        PersistedSubCalendar toUpdate = (PersistedSubCalendar)subCalendar;
        PersistedSubCalendar sourceCalendar = ((InternalSubscriptionSubCalendar)toUpdate).getSourceSubCalendar();
        if (sourceCalendar != null && sourceCalendar.getParent() == null && (sourceCalendarEntity = this.getSubCalendarEntity(sourceCalendar.getId())) != null) {
            String oldSpaceKey = StringUtils.defaultIfBlank(sourceCalendarEntity.getSpaceKey(), null);
            sourceCalendarEntity.setName(subCalendar.getName());
            sourceCalendarEntity.setDescription(description);
            sourceCalendarEntity.setColour(subCalendar.getColor());
            sourceCalendarEntity.setSpaceKey(spaceKey);
            sourceCalendarEntity.setTimeZoneId(subCalendar.getTimeZoneId());
            sourceCalendarEntity.save();
            if (!StringUtils.equals(spaceKey, oldSpaceKey)) {
                this.removeSubCalendarFromSpaceView(sourceCalendarEntity, oldSpaceKey);
            }
            this.addCalendarToSpaceView(sourceParentId, sourceCalendar.getId(), spaceKey);
        }
        SubCalendarEntity subCalendarEntity = this.getSubCalendarEntity(toUpdate.getId());
        subCalendarEntity.setParent(parentCalendarEntity);
        subCalendarEntity.setName(subCalendar.getName());
        subCalendarEntity.setDescription(description);
        subCalendarEntity.setColour(subCalendar.getColor());
        subCalendarEntity.setSpaceKey(null);
        subCalendarEntity.setTimeZoneId(subCalendar.getTimeZoneId());
        subCalendarEntity.setLastModified(System.currentTimeMillis());
        subCalendarEntity.save();
        return subCalendarEntity;
    }

    private String getText(String key) {
        return this.getText(key, Collections.emptyList());
    }

    private String getText(String key, List<?> substitutions) {
        return this.getI18NBean().getText(key, substitutions);
    }

    @Override
    public PersistedSubCalendar getSourceSubCalendar(String sourceSubCalendarId) {
        return StringUtils.isBlank(sourceSubCalendarId) ? null : this.calendarDataStoreDelegate.getSubCalendar(sourceSubCalendarId);
    }

    public PersistedSubCalendar convertSubCalendarEntityToPersisted(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"InternalSubscriptionCalendarDataStore.convertSubCalendarEntityToPersisted()");
        try {
            PersistedSubCalendar persistedSubCalendar = this.calendarDataStoreDelegate.getSubCalendar(subCalendarEntity);
            return persistedSubCalendar;
        }
        finally {
            UtilTimerStack.pop((String)"InternalSubscriptionCalendarDataStore.convertSubCalendarEntityToPersisted()");
        }
    }

    @Override
    public int getSubCalendarsCount() {
        return 0;
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(String subCalendarId) {
        return this.calendarDataStoreDelegate.getEventEditUserRestrictions((PersistedSubCalendar)((Object)((SubscribingSubCalendarSummary)this.getSubCalendarSummary(subCalendarId)).getSubscriptionId()));
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(InternalSubscriptionSubCalendar subCalendar) {
        return this.getEventEditUserRestrictions(subCalendar.getId());
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(String subCalendarId) {
        return this.calendarDataStoreDelegate.getEventEditGroupRestrictions((PersistedSubCalendar)((Object)((SubscribingSubCalendarSummary)this.getSubCalendarSummary(subCalendarId)).getSubscriptionId()));
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(InternalSubscriptionSubCalendar subCalendar) {
        return this.getEventEditGroupRestrictions(subCalendar.getId());
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(String subCalendarId) {
        return this.calendarDataStoreDelegate.getEventViewUserRestrictions((PersistedSubCalendar)((Object)((SubscribingSubCalendarSummary)this.getSubCalendarSummary(subCalendarId)).getSubscriptionId()));
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(InternalSubscriptionSubCalendar subCalendar) {
        return this.getEventViewUserRestrictions(subCalendar.getId());
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(String subCalendarId) {
        return this.calendarDataStoreDelegate.getEventViewGroupRestrictions((PersistedSubCalendar)((Object)((SubscribingSubCalendarSummary)this.getSubCalendarSummary(subCalendarId)).getSubscriptionId()));
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(InternalSubscriptionSubCalendar subCalendar) {
        return this.getEventViewGroupRestrictions(subCalendar.getId());
    }

    @Override
    public void restrictEventEditToUsers(String subCalendarId, Set<ConfluenceUser> users) {
        this.calendarDataStoreDelegate.restrictEventEditToUsers(((SubscribingSubCalendarSummary)this.getSubCalendarSummary(subCalendarId)).getSubscriptionId(), users);
    }

    @Override
    public void restrictEventEditToGroups(String subCalendarId, Set<String> groupNames) {
        this.calendarDataStoreDelegate.restrictEventEditToGroups(((SubscribingSubCalendarSummary)this.getSubCalendarSummary(subCalendarId)).getSubscriptionId(), groupNames);
    }

    @Override
    public void restrictEventViewToUsers(String subCalendarId, Set<ConfluenceUser> users) {
        this.calendarDataStoreDelegate.restrictEventViewToUsers(((SubscribingSubCalendarSummary)this.getSubCalendarSummary(subCalendarId)).getSubscriptionId(), users);
    }

    @Override
    public void restrictEventViewToGroups(String subCalendarId, Set<String> groupNames) {
        this.calendarDataStoreDelegate.restrictEventViewToGroups(((SubscribingSubCalendarSummary)this.getSubCalendarSummary(subCalendarId)).getSubscriptionId(), groupNames);
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return StringUtils.equals(this.getType(), subCalendar.getType());
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        super.validate(subCalendar, fieldErrors);
        String location = subCalendar.getSourceLocation();
        if (StringUtils.isBlank(location)) {
            this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.blank"));
        } else if (!StringUtils.startsWith(location, SUBSCRIPTION_SOURCE_LOCATION_PREFIX)) {
            this.addFieldError(fieldErrors, "name", this.getText("calendar3.error.invalidsubscription"));
        }
    }

    @Override
    public boolean hasViewEventPrivilege(String subCalendarId, ConfluenceUser user) {
        if (!this.hasSubCalendar(subCalendarId)) {
            throw new IllegalArgumentException(String.format("Sub-calendar %s is not managed by %s", subCalendarId, this.getClass().getName()));
        }
        return this.calendarDataStoreDelegate.hasViewEventPrivilege(this.getSubCalendarEntity(subCalendarId).getSubscription().getID(), user);
    }

    @Override
    public boolean hasViewEventPrivilege(InternalSubscriptionSubCalendar subCalendar, ConfluenceUser user) {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        return null != sourceSubCalendar && this.calendarDataStoreDelegate.hasViewEventPrivilege(sourceSubCalendar, user);
    }

    @Override
    public boolean hasEditEventPrivilege(InternalSubscriptionSubCalendar subCalendar, ConfluenceUser user) {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        return null != sourceSubCalendar && this.calendarDataStoreDelegate.hasEditEventPrivilege(sourceSubCalendar, user);
    }

    @Override
    public boolean hasDeletePrivilege(InternalSubscriptionSubCalendar subCalendar, ConfluenceUser user) {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        return null != sourceSubCalendar && this.calendarDataStoreDelegate.hasDeletePrivilege(sourceSubCalendar, user);
    }

    @Override
    public boolean hasAdminPrivilege(InternalSubscriptionSubCalendar subCalendar, ConfluenceUser user) {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        return null != sourceSubCalendar && this.calendarDataStoreDelegate.hasAdminPrivilege(sourceSubCalendar, user);
    }

    @Override
    public void setSubCalendarContent(InternalSubscriptionSubCalendar subCalendar, Calendar subCalendarData) throws Exception {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        if (null == sourceSubCalendar) {
            throw new CalendarException("calendar3.error.srcsubcalendarnotfound", subCalendar.getSubscriptionId());
        }
        this.calendarDataStoreDelegate.setSubCalendarContent(sourceSubCalendar, subCalendarData);
    }

    @Override
    public List<Message> getSubCalendarWarnings(InternalSubscriptionSubCalendar subCalendar) {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        if (null == sourceSubCalendar) {
            throw new CalendarException("calendar3.error.srcsubcalendarnotfound", subCalendar.getSubscriptionId());
        }
        return this.calendarDataStoreDelegate.getSubCalendarWarnings(sourceSubCalendar);
    }

    @Override
    public Calendar getSubCalendarContent(InternalSubscriptionSubCalendar subCalendar) throws Exception {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        if (null == sourceSubCalendar) {
            throw new CalendarException("calendar3.error.loadevents.subcalendardeletedmaybe", subCalendar.getName(), subCalendar.getId());
        }
        return this.calendarDataStoreDelegate.getSubCalendarContent(sourceSubCalendar);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, VEvent raw) {
        SubCalendarEvent transformed = super.transform(toBeTransformed, raw);
        PersistedSubCalendar subCalendar = transformed.getSubCalendar();
        if (subCalendar instanceof InternalSubscriptionSubCalendar) {
            PersistedSubCalendar sourceSubCalendar = ((InternalSubscriptionSubCalendar)subCalendar).getSourceSubCalendar();
            try {
                transformed.setSubCalendar(sourceSubCalendar);
                if (sourceSubCalendar.getType() != null && sourceSubCalendar.getType().equals("custom") && sourceSubCalendar.getCustomEventTypes() != null && sourceSubCalendar.getCustomEventTypes().size() > 0) {
                    CustomEventType customEventType = sourceSubCalendar.getCustomEventTypes().iterator().next();
                    transformed.setClassName(customEventType.getIcon());
                    transformed.setCustomEventTypeId(customEventType.getCustomEventTypeId());
                } else {
                    transformed.setClassName(sourceSubCalendar.getType());
                }
                transformed.setEventType(sourceSubCalendar.getType());
                transformed = this.calendarDataStoreDelegate.transform(transformed, raw);
                if (this.subCalendarColorRegistry.isEventMoreLightenedColourScheme(transformed.getColorScheme())) {
                    String subCalendarColour = subCalendar.getColor();
                    transformed.setColorScheme(this.subCalendarColorRegistry.getEventMoreLightenedColourScheme(subCalendarColour));
                    transformed.setBorderColor("#" + this.subCalendarColorRegistry.getEvenMoreLightenedColorHex(subCalendarColour));
                    transformed.setBackgroundColor(transformed.getBorderColor());
                }
            }
            finally {
                transformed.setSubCalendar(subCalendar);
            }
        }
        return transformed;
    }

    @Override
    public Message getTypeSpecificText(InternalSubscriptionSubCalendar subCalendar, Message originalMessage) {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        if (null != sourceSubCalendar) {
            Message typeSpecificMessage = this.calendarDataStoreDelegate.getTypeSpecificText(sourceSubCalendar, originalMessage);
            if (StringUtils.equals("calendar3.jira.error.calendartruncated", typeSpecificMessage.getKey())) {
                return new GenericMessage(typeSpecificMessage.getKey(), new Serializable[]{subCalendar.getName(), Integer.valueOf(JiraCalendarDataStore.getMaxJiraIssuesToDisplay())});
            }
            if (StringUtils.equals("calendar3.jira.error.loadevents.notpermitted", typeSpecificMessage.getKey())) {
                return new GenericMessage(typeSpecificMessage.getKey(), new Serializable[]{subCalendar.getName(), typeSpecificMessage.getArguments()[1]});
            }
        }
        return originalMessage;
    }

    @Override
    public boolean hasReloadEventsPrivilege(InternalSubscriptionSubCalendar subCalendar, ConfluenceUser user) {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        return null != sourceSubCalendar && this.calendarDataStoreDelegate instanceof RefreshableCalendarDataStore && ((RefreshableCalendarDataStore)this.calendarDataStoreDelegate).hasReloadEventsPrivilege(sourceSubCalendar, user);
    }

    @Override
    public void refresh(InternalSubscriptionSubCalendar subCalendar) {
        PersistedSubCalendar sourceSubCalendar = subCalendar.getSourceSubCalendar();
        if (null != sourceSubCalendar && this.calendarDataStoreDelegate instanceof RefreshableCalendarDataStore) {
            ((RefreshableCalendarDataStore)this.calendarDataStoreDelegate).refresh(sourceSubCalendar);
        }
    }

    @Override
    public String getType() {
        return SUB_CALENDAR_TYPE;
    }

    @Override
    public List<VEvent> getEvents(InternalSubscriptionSubCalendar subCalendar, DateTime startTime, DateTime endTime) throws Exception {
        return this.calendarDataStoreDelegate.getEvents(this.getSourceSubCalendar(subCalendar.getSubscriptionId()), startTime, endTime);
    }

    private class InternalSubscriptionSubCalendarParentResolver
    implements ParentSubCalendarResolver {
        private final String parentId;

        private InternalSubscriptionSubCalendarParentResolver(String parentId) {
            this.parentId = parentId;
        }

        @Override
        public String getParentId() {
            return this.parentId;
        }

        @Override
        public PersistedSubCalendar getParentSubCalendar() {
            return InternalSubscriptionCalendarDataStore.this.getSubCalendar(this.parentId);
        }
    }

    public static class InternalSubscriptionSubCalendar
    extends SubscribingSubCalendar
    implements Cloneable {
        private static final Logger LOG = LoggerFactory.getLogger(InternalSubscriptionSubCalendar.class);
        private ParentSubCalendarResolver parentResolver;
        private String id;
        private String creator;
        private String spaceName;
        private PersistedSubCalendar sourceSubCalendar;
        private long created;
        private Set<String> childSubCalendarIds;
        private String typeKey;

        @Override
        @XmlElement
        public String getTypeKey() {
            return this.typeKey;
        }

        public void setTypeKey(String typeKey) {
            this.typeKey = typeKey;
        }

        @Override
        @XmlElement
        public String getParentId() {
            return this.parentResolver == null ? super.getParentId() : this.parentResolver.getParentId();
        }

        @Override
        public PersistedSubCalendar getParent() {
            return this.parentResolver == null ? super.getParent() : this.parentResolver.getParentSubCalendar();
        }

        @Override
        public void setParent(PersistedSubCalendar parent) {
            this.parentResolver = null;
            super.setParent(parent);
        }

        private void setParentResolver(ParentSubCalendarResolver parentResolver) {
            this.parentResolver = parentResolver;
        }

        @Override
        @XmlElement
        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        @XmlElement
        public String getType() {
            return InternalSubscriptionCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        @Override
        @XmlElement
        public String getCreator() {
            return this.creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        @Override
        @XmlElement
        public String getSpaceName() {
            return this.spaceName;
        }

        public void setSpaceName(String spaceName) {
            this.spaceName = spaceName;
        }

        @Override
        @XmlElement
        public String getTimeZoneId() {
            return null == this.getSourceSubCalendar() ? super.getTimeZoneId() : this.getSourceSubCalendar().getTimeZoneId();
        }

        @Override
        @XmlElement
        public boolean isWatchable() {
            PersistedSubCalendar sourceSubCalendar = this.getSourceSubCalendar();
            return null != sourceSubCalendar && sourceSubCalendar.isWatchable();
        }

        @Override
        @XmlElement
        public boolean isRestrictable() {
            return null != this.getSourceSubCalendar() && this.getSourceSubCalendar().isRestrictable();
        }

        @Override
        @XmlElement
        public boolean isEventInviteesSupported() {
            return null != this.getSourceSubCalendar() && this.getSourceSubCalendar().isEventInviteesSupported();
        }

        public PersistedSubCalendar getSourceSubCalendar() {
            return this.sourceSubCalendar;
        }

        public void setSourceSubCalendar(PersistedSubCalendar sourceSubCalendar) {
            this.sourceSubCalendar = sourceSubCalendar;
        }

        @Override
        @XmlElement
        public String getSubscriptionId() {
            return null == this.getSourceSubCalendar() ? null : this.getSourceSubCalendar().getId();
        }

        @Override
        @XmlElement
        public String getSubscriptionType() {
            return null == this.getSourceSubCalendar() ? null : this.getSourceSubCalendar().getType();
        }

        @Override
        @XmlElement
        public Set<CustomEventType> getCustomEventTypes() {
            return null == this.getSourceSubCalendar() ? null : this.getSourceSubCalendar().getCustomEventTypes();
        }

        @Override
        @XmlElement
        public String getSourceLocation() {
            return super.getSourceLocation();
        }

        public long getCreated() {
            return this.created;
        }

        public void setCreated(long created) {
            this.created = created;
        }

        @Override
        @XmlElement
        public Set<String> getChildSubCalendarIds() {
            return this.childSubCalendarIds;
        }

        public void setChildSubCalendarIds(Set<String> childSubCalendarIds) {
            this.childSubCalendarIds = childSubCalendarIds;
        }

        @Override
        public JSONObject toJson() {
            JSONObject thisObject = super.toJson();
            try {
                thisObject.put("sourceLocation", (Object)this.getSourceLocation());
            }
            catch (JSONException jsonE) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
            }
            return thisObject;
        }

        @Override
        public Object clone() {
            InternalSubscriptionSubCalendar copy = new InternalSubscriptionSubCalendar();
            copy.setParent(this.getParent());
            copy.setId(this.getId());
            copy.setName(this.getName());
            copy.setDescription(this.getDescription());
            copy.setColor(this.getColor());
            copy.setCreator(this.getCreator());
            copy.setSpaceKey(this.getSpaceKey());
            copy.setSpaceName(this.getSpaceName());
            copy.setTimeZoneId(this.getTimeZoneId());
            copy.setTypeKey(this.getTypeKey());
            copy.setCreated(this.getCreated());
            copy.setSourceSubCalendar(this.getSourceSubCalendar());
            copy.setSourceLocation(this.getSourceLocation());
            copy.setChildSubCalendarIds(this.getChildSubCalendarIds());
            return copy;
        }
    }

    public static interface ParentSubCalendarResolver {
        public String getParentId();

        public PersistedSubCalendar getParentSubCalendar();
    }
}

