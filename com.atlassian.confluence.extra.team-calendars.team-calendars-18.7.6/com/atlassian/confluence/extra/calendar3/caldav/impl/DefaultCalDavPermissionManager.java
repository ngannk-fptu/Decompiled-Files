/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import com.atlassian.confluence.extra.calendar3.caldav.CalDavPermissionManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalendarAccessPrincipal;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.bedework.access.AccessPrincipal;
import org.bedework.caldav.server.PropertyHandler;
import org.bedework.caldav.server.sysinterface.CalDAVAuthProperties;
import org.bedework.caldav.server.sysinterface.CalDAVSystemProperties;
import org.bedework.caldav.server.sysinterface.CalPrincipalInfo;
import org.bedework.webdav.servlet.shared.PrincipalPropertySearch;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavUnauthorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="calDavCollectionsManager")
public final class DefaultCalDavPermissionManager
implements CalDavPermissionManager {
    private final CalDAVAuthProperties calendarCalDAVAuthProperties;
    private final CalDAVSystemProperties calendarCalDavProperties;

    @Autowired
    public DefaultCalDavPermissionManager(CalDAVAuthProperties calendarCalDAVAuthProperties, CalDAVSystemProperties calendarCalDavProperties) {
        this.calendarCalDAVAuthProperties = calendarCalDAVAuthProperties;
        this.calendarCalDavProperties = calendarCalDavProperties;
    }

    @Override
    public boolean testMode() {
        return false;
    }

    @Override
    public boolean bedeworkExtensionsEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CalDAVAuthProperties getAuthProperties() {
        return this.calendarCalDAVAuthProperties;
    }

    @Override
    public CalDAVSystemProperties getSystemProperties() {
        return this.calendarCalDavProperties;
    }

    @Override
    public AccessPrincipal getPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PropertyHandler getPropertyHandler(PropertyHandler.PropertyType ptype) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPrincipal(String value) throws WebdavException {
        return StringUtils.isNotBlank((CharSequence)value) && value.matches("/(principals/users/.*)?");
    }

    @Override
    public AccessPrincipal getPrincipalForUser(String account) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccessPrincipal getPrincipal(String href) throws WebdavException {
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        if (confluenceUser == null) {
            throw new WebdavUnauthorized();
        }
        return new CalendarAccessPrincipal(confluenceUser);
    }

    @Override
    public byte[] getPublicKey(String domain, String service) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String makeHref(UrlHandler urlHandler, String id, int whoType) throws WebdavException {
        if (id.startsWith("mailto") || id.startsWith("http")) {
            return id;
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return urlHandler.prefix("/principals/users/" + (currentUser != null ? currentUser.getName() : ""));
    }

    @Override
    public Collection<String> getGroups(String rootUrl, String principalUrl) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccessPrincipal caladdrToPrincipal(String caladdr) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String principalToCaladdr(AccessPrincipal principal) throws WebdavException {
        return principal.getPrincipalRef();
    }

    @Override
    public CalPrincipalInfo getCalPrincipalInfo(AccessPrincipal principal) throws WebdavException {
        return new CalPrincipalInfo(principal, null, null, "/calendars", "/calendars", null, null, null, 0L);
    }

    @Override
    public Collection<String> getPrincipalCollectionSet(String resourceUri) throws WebdavException {
        return Collections.singletonList("/principals/users");
    }

    @Override
    public Collection<CalPrincipalInfo> getPrincipals(String resourceUri, PrincipalPropertySearch pps) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean validPrincipal(String href) throws WebdavException {
        throw new UnsupportedOperationException();
    }
}

