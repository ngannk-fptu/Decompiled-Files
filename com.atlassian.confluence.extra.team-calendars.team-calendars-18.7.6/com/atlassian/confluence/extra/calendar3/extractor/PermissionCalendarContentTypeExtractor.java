/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Index
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.Entity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.extractor;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.util.CalendarHelper;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class PermissionCalendarContentTypeExtractor
implements Extractor2 {
    private static final String CONTENT_PERMISSION_SETS = "permissionSets";
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionCalendarContentTypeExtractor.class);
    private CalendarManager calendarManager;
    private SpacePermissionManager spacePermissionManager;
    private SpaceManager spaceManager;
    private final CalendarHelper calendarHelper;

    @Autowired
    public PermissionCalendarContentTypeExtractor(CalendarManager calendarManager, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport SpaceManager spaceManager, CalendarHelper calendarHelper) {
        this.calendarManager = calendarManager;
        this.spacePermissionManager = spacePermissionManager;
        this.spaceManager = spaceManager;
        this.calendarHelper = calendarHelper;
    }

    public StringBuilder extractText(Object searchable) {
        return null;
    }

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof CustomContentEntityObject)) {
            return Collections.emptyList();
        }
        ArrayList<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();
        CustomContentEntityObject calendarContentEntity = (CustomContentEntityObject)searchable;
        boolean isCalendarContentType = "com.atlassian.confluence.extra.team-calendars:calendar-content-type".equals(calendarContentEntity.getPluginModuleKey());
        boolean isSpaceCalendarContentType = "com.atlassian.confluence.extra.team-calendars:space-calendars-view-content-type".equals(calendarContentEntity.getPluginModuleKey());
        if (!isSpaceCalendarContentType && !isCalendarContentType) {
            return Collections.emptyList();
        }
        StringBuilder encodedCredentials = new StringBuilder(500);
        if (isCalendarContentType) {
            String subCalendarId = calendarContentEntity.getProperties().getStringProperty("subCalendarId");
            List<PersistedSubCalendar> subCalendarList = this.calendarManager.getSubCalendarsWithRestriction(subCalendarId);
            if (subCalendarList == null || subCalendarList.size() <= 0) {
                LOGGER.warn("Could not find calendar content type for calendar {}. So could not sync permissions", (Object)subCalendarId);
                return Collections.emptyList();
            }
            PersistedSubCalendar persistedSubCalendar = subCalendarList.get(0);
            Space calendarSpace = this.spaceManager.getSpace(persistedSubCalendar.getSpaceKey());
            String spacePermissions = this.getSpaceCredentialsAsString(calendarSpace);
            String calendarPermissions = this.calendarHelper.getEncodedCalendarCredentialsAsString(persistedSubCalendar);
            encodedCredentials.append(spacePermissions);
            if (!calendarPermissions.isEmpty() && !spacePermissions.isEmpty()) {
                encodedCredentials.append("&");
            }
            encodedCredentials.append(calendarPermissions);
        } else {
            String spaceKey = calendarContentEntity.getProperties().getStringProperty("spaceKey");
            Space calendarSpace = this.spaceManager.getSpace(spaceKey);
            if (calendarSpace == null) {
                LOGGER.warn("Could not find space for space key {}. So could not sync permissions", (Object)spaceKey);
                return Collections.emptyList();
            }
            encodedCredentials.append(this.getSpaceCredentialsAsString(calendarSpace));
        }
        FieldDescriptor fieldDescriptor = new FieldDescriptor(CONTENT_PERMISSION_SETS, encodedCredentials.toString(), FieldDescriptor.Store.NO, FieldDescriptor.Index.NOT_ANALYZED);
        fieldDescriptors.add(fieldDescriptor);
        return fieldDescriptors;
    }

    private String getSpaceCredentialsAsString(Space calendarSpace) {
        List<String> viewSpacePermittedUserList = this.spacePermissionManager.getUsersWithPermissions(calendarSpace).stream().filter(ConfluenceUser.class::isInstance).map(ConfluenceUser.class::cast).map(user -> user.getKey().getStringValue()).collect(Collectors.toList());
        List<String> viewSpacePermittedGroupList = this.spacePermissionManager.getGroupsWithPermissions(calendarSpace).stream().map(Entity::getName).collect(Collectors.toList());
        return this.calendarHelper.getEncodedSpaceCredentialsAsString(viewSpacePermittedUserList, viewSpacePermittedGroupList);
    }
}

