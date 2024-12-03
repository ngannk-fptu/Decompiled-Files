/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Optional
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavCollectionManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalendarAccessPrincipal;
import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.caldav.node.HomeCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.bedework.access.Acl;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavUnauthorized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="calDavCollectionManager")
public final class DefaultCalDavCollectionManager
implements CalDavCollectionManager {
    private static Logger LOG = LoggerFactory.getLogger(DefaultCalDavCollectionManager.class);
    private final CalendarManager calendarManager;
    private final UserAccessor userAccessor;
    private final CalendarPermissionManager calendarPermissionManager;

    @Autowired
    public DefaultCalDavCollectionManager(CalendarPermissionManager calendarPermissionManager, CalendarManager calendarManager, @ComponentImport UserAccessor userAccessor) {
        this.calendarManager = calendarManager;
        this.userAccessor = userAccessor;
        this.calendarPermissionManager = calendarPermissionManager;
    }

    @Override
    public CalDAVCollection newCollectionObject(boolean isCalendarCollection, String parentPath) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAccess(CalDAVCollection col, Acl acl) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int makeCollection(CalDAVCollection col) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyMove(CalDAVCollection from, CalDAVCollection to, boolean copy, boolean overwrite) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public CalDAVCollection getCollection(String path) throws WebdavException {
        if (StringUtils.isBlank((CharSequence)path)) {
            return null;
        }
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        if (path.matches("/calendars(/)?")) {
            ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
            if (confluenceUser == null) {
                throw new WebdavUnauthorized();
            }
            List<PersistedSubCalendar> calendars = this.calendarManager.getUserPreference(confluenceUser).getSubCalendarsInView().stream().map(this.calendarManager::getPersistedSubCalendar).filter(Optional::isPresent).map(Optional::get).filter(persistedSubCalendar -> this.calendarPermissionManager.hasViewEventPrivilege((PersistedSubCalendar)persistedSubCalendar, loginUser)).collect(Collectors.toList());
            return new HomeCalDAVCollection(path, new CalendarAccessPrincipal(confluenceUser), calendars);
        }
        LOG.info("CalDav path {}", (Object)path);
        int indexOfQuestionMark = path.indexOf(63);
        String subPath = indexOfQuestionMark > 0 ? path.substring(0, indexOfQuestionMark) : path;
        String[] calendarIds = subPath.split("/");
        String subCalendarId = calendarIds[calendarIds.length - 1];
        PersistedSubCalendar subCalendar = (PersistedSubCalendar)this.calendarManager.getPersistedSubCalendar(subCalendarId).orNull();
        if (subCalendar == null) {
            LOG.debug("Could not load Persisted Sub Calendar for ID {}", (Object)subCalendarId);
            return null;
        }
        if (!this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, loginUser)) {
            LOG.warn("Login User does not have view permission on calendar {}", (Object)subCalendar.getName());
            return null;
        }
        if (subCalendar.getStoreKey().equals("com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore")) {
            LOG.debug("Get collection for Internal Subscription Calendar");
            InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar internalSubscriptionSubCalendar = (InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)subCalendar;
            subCalendar = internalSubscriptionSubCalendar.getSourceSubCalendar();
        }
        ConfluenceUser confluenceUser = this.userAccessor.getUserByKey(new UserKey(subCalendar.getCreator()));
        CalendarAccessPrincipal owner = new CalendarAccessPrincipal(confluenceUser);
        this.setSubCalendarHexColor(subCalendar);
        return new CalendarCalDAVCollection(subPath, subCalendar, owner, this.calendarManager);
    }

    @Override
    public void updateCollection(CalDAVCollection value) {
    }

    @Override
    public void deleteCollection(CalDAVCollection col, boolean sendSchedulingMessage) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<CalDAVCollection> getCollections(CalDAVCollection collection) throws WebdavException {
        if (!(collection instanceof HomeCalDAVCollection)) {
            return Collections.emptyList();
        }
        HomeCalDAVCollection homeCollection = (HomeCalDAVCollection)collection;
        ArrayList<CalDAVCollection> collections = new ArrayList<CalDAVCollection>();
        for (PersistedSubCalendar calendar : homeCollection.getCalendars()) {
            this.setSubCalendarHexColor(calendar);
            ConfluenceUser owner = this.userAccessor.getUserByKey(new UserKey(calendar.getCreator()));
            collections.add(new CalendarCalDAVCollection(String.format("/%s", calendar.getId()), calendar, new CalendarAccessPrincipal(owner), this.calendarManager));
        }
        return collections;
    }

    @Override
    public void setSubCalendarHexColor(SubCalendar subCalendar) {
        if (subCalendar == null || StringUtils.isEmpty((CharSequence)subCalendar.getColor())) {
            LOG.warn("Could not set hex color code for SubCalendar");
            return;
        }
        if (subCalendar.getColor().startsWith("#")) {
            LOG.warn("SubCalendar color value is hex");
            return;
        }
        String colorHex = this.calendarManager.getSubCalendarColorAsHexValue(subCalendar.getColor());
        subCalendar.setColor("#" + colorHex);
    }
}

