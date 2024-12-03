/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.DefaultSaveContext$Builder
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.event.events.permission.SpacePermissionChangeEvent
 *  com.atlassian.confluence.event.events.permission.SpacePermissionRemoveEvent
 *  com.atlassian.confluence.event.events.permission.SpacePermissionSaveEvent
 *  com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForGroupEvent
 *  com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForUserEvent
 *  com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveFromSpaceEvent
 *  com.atlassian.confluence.event.events.space.SpaceRemoveEvent
 *  com.atlassian.confluence.event.events.space.SpaceUpdateEvent
 *  com.atlassian.confluence.search.ConfluenceIndexer
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.contenttype;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.event.events.permission.SpacePermissionChangeEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionRemoveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionSaveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForGroupEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForUserEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveFromSpaceEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.space.SpaceUpdateEvent;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.contenttype.CalendarContentTypeManager;
import com.atlassian.confluence.extra.calendar3.contenttype.hibernatequery.CalendarContentQueryFactory;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarCreated;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarIndexOutOfSynch;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarRemoved;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarRestrictionsUpdated;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarUpdated;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="calendarContentTypeManager")
@ExportAsService
public class DefaultCalendarContentTypeManager
implements CalendarContentTypeManager,
InitializingBean,
DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCalendarContentTypeManager.class);
    @VisibleForTesting
    public static final SaveContext RETAIN_MODIFICATION_DATA = ((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)DefaultSaveContext.builder().suppressNotifications(false)).updateLastModifier(true).suppressEvents(false)).build();
    @VisibleForTesting
    public static final SaveContext MINOR_MODIFICATION_DATA = ((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)DefaultSaveContext.builder().suppressNotifications(true)).updateLastModifier(true).suppressEvents(false)).build();
    private final CustomContentManager customContentManager;
    private final SpaceManager spaceManager;
    private final ConfluenceIndexer indexer;
    private final EventPublisher eventPublisher;
    private final CalendarManager calendarManager;
    private final TransactionalHostContextAccessor hostContextAccessor;

    @Autowired
    DefaultCalendarContentTypeManager(@ComponentImport CustomContentManager customContentManager, @ComponentImport EventPublisher eventPublisher, @Qualifier(value="indexer") ConfluenceIndexer indexer, @ComponentImport SpaceManager spaceManager, CalendarManager calendarManager, @ComponentImport TransactionalHostContextAccessor hostContextAccessor) {
        this.customContentManager = customContentManager;
        this.eventPublisher = eventPublisher;
        this.indexer = indexer;
        this.spaceManager = spaceManager;
        this.calendarManager = calendarManager;
        this.hostContextAccessor = hostContextAccessor;
    }

    @EventListener
    public void handleSpaceUpdated(SpaceUpdateEvent event) {
        Space oldSpace = event.getOriginalSpace();
        Space newSpace = event.getSpace();
        if (oldSpace != null && newSpace != null & !oldSpace.getDisplayTitle().equals(newSpace.getDisplayTitle())) {
            String oldSpaceKey = oldSpace.getKey();
            CustomContentEntityObject cceo = this.loadCalendarContentBySpaceKey(oldSpaceKey);
            if (cceo != null) {
                this.removeCalendarContentEntity(cceo);
            }
            this.createCalendarContentTypeFor(newSpace);
        }
    }

    @EventListener
    public void handleSpaceRemoved(SpaceRemoveEvent event) {
        CustomContentEntityObject cceo;
        if (event.getSpace() != null && (cceo = this.loadCalendarContentBySpaceKey(event.getSpace().getKey())) != null) {
            this.removeCalendarContentEntity(cceo);
        }
    }

    @EventListener
    public void handleUserViewRestriction(SubCalendarRestrictionsUpdated.UserViewRestrictionsUpdated event) {
        this.updateViewContentPermission((PersistedSubCalendar)event.getSubCalendar());
    }

    @EventListener
    public void handleGroupViewRestriction(SubCalendarRestrictionsUpdated.GroupViewRestrictionsUpdated event) {
        this.updateViewContentPermission((PersistedSubCalendar)event.getSubCalendar());
    }

    @EventListener
    public void handleSpacePermissionRemoveEvent(SpacePermissionRemoveEvent event) {
        if (StreamSupport.stream(event.getPermissions().spliterator(), false).anyMatch(permission -> permission.getType().equals("VIEWSPACE"))) {
            this.reIndexCalendarsOnSpaces(Collections.singletonList(event.getSpace()));
        }
    }

    @EventListener
    public void handleSpacePermissionsRemoveFromSpaceEvent(SpacePermissionsRemoveFromSpaceEvent event) {
        if (StreamSupport.stream(event.getPermissions().spliterator(), false).anyMatch(permission -> permission.getType().equals("VIEWSPACE"))) {
            this.reIndexCalendarsOnSpaces(Collections.singletonList(event.getSpace()));
        }
    }

    @EventListener
    public void handleSpacePermissionSaveEvent(SpacePermissionSaveEvent event) {
        this.reIndexCalendarsOnSpaces(this.getSpacesFromViewSpacePermissions((SpacePermissionChangeEvent)event));
    }

    @EventListener
    public void handleSpacePermissionsRemoveForGroupEvent(SpacePermissionsRemoveForGroupEvent event) {
        this.reIndexCalendarsOnSpaces(this.getSpacesFromViewSpacePermissions((SpacePermissionChangeEvent)event));
    }

    @EventListener
    public void handleSpacePermissionsRemoveForUserEvent(SpacePermissionsRemoveForUserEvent event) {
        this.reIndexCalendarsOnSpaces(this.getSpacesFromViewSpacePermissions((SpacePermissionChangeEvent)event));
    }

    @VisibleForTesting
    void reIndexCalendarsOnSpaces(List<Space> spaces) {
        int batchSize = 100;
        int start = 0;
        List spaceKeys = spaces.stream().map(Space::getKey).collect(Collectors.toList());
        if (spaceKeys.isEmpty()) {
            return;
        }
        boolean hasMore = true;
        while (hasMore) {
            SimplePageRequest request = new SimplePageRequest(start, batchSize);
            hasMore = (Boolean)this.hostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> this.lambda$reIndexCalendarsOnSpaces$2((PageRequest)request, spaceKeys));
            start += batchSize;
        }
        List spaceSubCalendarIDs = spaceKeys.stream().flatMap(spaceKey -> this.calendarManager.getSubCalendarsOnSpace((String)spaceKey).stream()).collect(Collectors.toList());
        if (spaceSubCalendarIDs.isEmpty()) {
            return;
        }
        hasMore = true;
        start = 0;
        while (hasMore) {
            SimplePageRequest request = new SimplePageRequest(start, batchSize);
            hasMore = (Boolean)this.hostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> this.lambda$reIndexCalendarsOnSpaces$4((PageRequest)request, spaceSubCalendarIDs));
            start += batchSize;
        }
    }

    @VisibleForTesting
    List<Space> getSpacesFromViewSpacePermissions(SpacePermissionChangeEvent event) {
        return StreamSupport.stream(event.getPermissions().spliterator(), false).filter(permission -> permission != null && permission.getType().equals("VIEWSPACE")).map(SpacePermission::getSpace).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    @EventListener
    public void handleSubCalendarCreated(SubCalendarCreated subCalendarCreated) {
        Object subCalendar = subCalendarCreated.getSubCalendar();
        if (this.isChildCalendar((PersistedSubCalendar)subCalendar) || this.isInternalSubscriptionCalendar((PersistedSubCalendar)subCalendar)) {
            return;
        }
        CustomContentEntityObject calendarContentEntity = this.loadCalendarContent(((PersistedSubCalendar)subCalendar).getId());
        if (calendarContentEntity == null) {
            LOGGER.debug("Going to create new Calendar Content Type");
            calendarContentEntity = this.customContentManager.newPluginContentEntityObject("com.atlassian.confluence.extra.team-calendars:calendar-content-type");
            this.customContentManager.saveContentEntity((ContentEntityObject)this.updateCalendarContent((PersistedSubCalendar)subCalendar, calendarContentEntity), RETAIN_MODIFICATION_DATA);
        } else {
            LOGGER.debug("Going to update Calendar Content Type because we already have a Calendar Content Type with same ID");
            this.customContentManager.saveContentEntity((ContentEntityObject)this.updateCalendarContent((PersistedSubCalendar)subCalendar, calendarContentEntity), MINOR_MODIFICATION_DATA);
        }
    }

    @EventListener
    public void handleSubCalendarUpdated(SubCalendarUpdated subCalendarUpdated) {
        Object subCalendar = subCalendarUpdated.getSubCalendar();
        if (this.isChildCalendar((PersistedSubCalendar)subCalendar) || this.isInternalSubscriptionCalendar((PersistedSubCalendar)subCalendar)) {
            return;
        }
        String subCalendarId = ((PersistedSubCalendar)subCalendar).getId();
        CustomContentEntityObject calendarContentEntity = this.loadCalendarContent(subCalendarId);
        if (calendarContentEntity == null) {
            LOGGER.warn("Could not find CalendarCustomContent entity for SubCalendarUpdated by calendar id {}", (Object)subCalendarId);
            return;
        }
        this.customContentManager.saveContentEntity((ContentEntityObject)this.updateCalendarContent((PersistedSubCalendar)subCalendar, calendarContentEntity), MINOR_MODIFICATION_DATA);
    }

    @EventListener
    public void handleSubCalendarRemoved(SubCalendarRemoved subCalendarRemoved) {
        Object subCalendar = subCalendarRemoved.getSubCalendar();
        if (this.isChildCalendar((PersistedSubCalendar)subCalendar) || this.isInternalSubscriptionCalendar((PersistedSubCalendar)subCalendar)) {
            return;
        }
        String subCalendarId = ((PersistedSubCalendar)subCalendar).getId();
        CustomContentEntityObject calendarContentEntity = this.loadCalendarContent(subCalendarId);
        if (calendarContentEntity == null) {
            LOGGER.warn("Could not find CalendarCustomContent entity for SubCalendarRemoved by calendar id {}", (Object)subCalendarId);
            return;
        }
        this.customContentManager.removeContentEntity((ContentEntityObject)calendarContentEntity);
    }

    @EventListener
    public void handleSubCalendarIndexOutOfSynch(SubCalendarIndexOutOfSynch subCalendarReviewFailed) {
        CustomContentEntityObject calendarContentEntity;
        String subCalendarId = (String)subCalendarReviewFailed.getSource();
        if (StringUtils.isEmpty(subCalendarId)) {
            LOGGER.warn("Could not get SubCalendarId from SubCalendarReviewFailed");
        }
        if ((calendarContentEntity = this.loadCalendarContent(subCalendarId)) != null) {
            LOGGER.debug("Going to remove stale Calendar Content for calendar id {}", (Object)subCalendarId);
            this.customContentManager.removeContentEntity((ContentEntityObject)calendarContentEntity);
        }
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @Override
    public CustomContentEntityObject loadCalendarContent(String subCalendarId) {
        return (CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(CalendarContentQueryFactory.findCalendarById(subCalendarId));
    }

    @Override
    public void createCalendarContentTypeFor(Space space) {
        try {
            CustomContentEntityObject calendarContentEntity = this.customContentManager.newPluginContentEntityObject("com.atlassian.confluence.extra.team-calendars:space-calendars-view-content-type");
            calendarContentEntity.setTitle(space.getDisplayTitle() + "'s Calendars");
            calendarContentEntity.getProperties().setStringProperty("spaceKey", space.getKey());
            this.customContentManager.saveContentEntity((ContentEntityObject)calendarContentEntity, RETAIN_MODIFICATION_DATA);
        }
        catch (Exception exception) {
            LOGGER.error("Error creating CalendarContentType entity", (Throwable)exception);
        }
    }

    @Override
    public void createCalendarContentTypeFor(SubCalendarEntity subCalendarEntity) {
        try {
            Space space;
            CustomContentEntityObject calendarContentEntity = this.customContentManager.newPluginContentEntityObject("com.atlassian.confluence.extra.team-calendars:calendar-content-type");
            calendarContentEntity.setCreationDate(new Date(subCalendarEntity.getCreated()));
            calendarContentEntity.setBodyAsString(subCalendarEntity.getDescription());
            calendarContentEntity.setTitle(subCalendarEntity.getName());
            calendarContentEntity.getProperties().setStringProperty("subCalendarId", subCalendarEntity.getID());
            String spaceKey = subCalendarEntity.getSpaceKey();
            if (StringUtils.isNotEmpty(spaceKey) && (space = this.spaceManager.getSpace(spaceKey)) != null) {
                calendarContentEntity.setSpace(space);
            }
            this.customContentManager.saveContentEntity((ContentEntityObject)calendarContentEntity, RETAIN_MODIFICATION_DATA);
        }
        catch (Exception exception) {
            LOGGER.error("Error creating CalendarContentType entity", (Throwable)exception);
        }
    }

    @Override
    public void removeCalendarContentEntity(CustomContentEntityObject customContentEntityObject) {
        this.customContentManager.removeContentEntity((ContentEntityObject)customContentEntityObject);
    }

    @Override
    public Iterator<CustomContentEntityObject> getAllSubCalendarContent() {
        return this.customContentManager.findByQuery(CalendarContentQueryFactory.getAllCalendars(), 0, Integer.MAX_VALUE);
    }

    @Override
    public PageResponse<CustomContentEntityObject> getAllSubCalendarContent(PageRequest request) {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)100);
        return this.customContentManager.findByQuery(CalendarContentQueryFactory.getAllCalendars(), false, limitedRequest, customContentEntityObject -> true);
    }

    @Override
    public CustomContentEntityObject loadCalendarContentBySpaceKey(String spaceKey) {
        return (CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(CalendarContentQueryFactory.findCalendarBySpaceKey(spaceKey));
    }

    @Override
    public PageResponse<CustomContentEntityObject> getAllCalendarContentBySpaceKeys(PageRequest request, List<String> spaceKeys) {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)100);
        PageResponse response = this.customContentManager.findByQuery(CalendarContentQueryFactory.findAllCalendarsBySpaceKeys(spaceKeys), false, limitedRequest, customContentEntityObject -> true);
        return response;
    }

    @Override
    public PageResponse<CustomContentEntityObject> getSubCalendarContentByIds(PageRequest request, List<String> ids) {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)100);
        return this.customContentManager.findByQuery(CalendarContentQueryFactory.findAllCalendarsById(ids), false, limitedRequest, customContentEntityObject -> true);
    }

    @Override
    public PageResponse<CustomContentEntityObject> getAllCalendarContent(PageRequest request) {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)100);
        return this.customContentManager.findByQuery(CalendarContentQueryFactory.getAllSpaceCalendarViewContent(), false, limitedRequest, customContentEntityObject -> true);
    }

    private void updateViewContentPermission(PersistedSubCalendar subCalendar) {
        String subCalendarId = subCalendar.getId();
        CustomContentEntityObject calendarContentEntity = this.loadCalendarContent(subCalendarId);
        if (calendarContentEntity == null) {
            LOGGER.warn("Don't have Calendar Content Type for Calendar {} so could not update permissions accordingly", (Object)subCalendarId);
            return;
        }
        this.indexer.reIndex((Searchable)calendarContentEntity);
    }

    private CustomContentEntityObject updateCalendarContent(PersistedSubCalendar subCalendar, CustomContentEntityObject calendarContentEntity) {
        calendarContentEntity.setTitle(subCalendar.getName());
        calendarContentEntity.setBodyAsString(StringEscapeUtils.escapeHtml(subCalendar.getDescription()));
        calendarContentEntity.getProperties().setStringProperty("subCalendarId", subCalendar.getId());
        return calendarContentEntity;
    }

    private boolean isChildCalendar(PersistedSubCalendar subCalendar) {
        if (subCalendar.getParent() != null) {
            LOGGER.debug("Discard SubCalendarCreated event because it is not parent calendar");
            return true;
        }
        return false;
    }

    private boolean isInternalSubscriptionCalendar(PersistedSubCalendar subCalendar) {
        if ("internal-subscription".equals(subCalendar.getType())) {
            LOGGER.debug("Discard SubCalendarCreated event because it is internal subscription calendar");
            return true;
        }
        return false;
    }

    private /* synthetic */ Boolean lambda$reIndexCalendarsOnSpaces$4(PageRequest request, List spaceSubCalendarIDs) {
        PageResponse<CustomContentEntityObject> spaceSubCalendars = this.getSubCalendarContentByIds(request, spaceSubCalendarIDs);
        spaceSubCalendars.forEach(arg_0 -> ((ConfluenceIndexer)this.indexer).reIndex(arg_0));
        return spaceSubCalendars.hasMore();
    }

    private /* synthetic */ Boolean lambda$reIndexCalendarsOnSpaces$2(PageRequest request, List spaceKeys) {
        PageResponse<CustomContentEntityObject> spaceCalendars = this.getAllCalendarContentBySpaceKeys(request, spaceKeys);
        spaceCalendars.forEach(arg_0 -> ((ConfluenceIndexer)this.indexer).reIndex(arg_0));
        return spaceCalendars.hasMore();
    }
}

