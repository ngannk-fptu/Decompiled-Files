/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import java.util.Collection;
import org.bedework.access.AccessPrincipal;
import org.bedework.caldav.server.PropertyHandler;
import org.bedework.caldav.server.sysinterface.CalDAVAuthProperties;
import org.bedework.caldav.server.sysinterface.CalDAVSystemProperties;
import org.bedework.caldav.server.sysinterface.CalPrincipalInfo;
import org.bedework.webdav.servlet.shared.PrincipalPropertySearch;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface CalDavPermissionManager {
    public boolean testMode();

    public boolean bedeworkExtensionsEnabled();

    public CalDAVAuthProperties getAuthProperties();

    public CalDAVSystemProperties getSystemProperties();

    public AccessPrincipal getPrincipal();

    public PropertyHandler getPropertyHandler(PropertyHandler.PropertyType var1) throws WebdavException;

    public boolean isPrincipal(String var1) throws WebdavException;

    public AccessPrincipal getPrincipalForUser(String var1) throws WebdavException;

    public AccessPrincipal getPrincipal(String var1) throws WebdavException;

    public byte[] getPublicKey(String var1, String var2) throws WebdavException;

    public String makeHref(UrlHandler var1, String var2, int var3) throws WebdavException;

    public Collection<String> getGroups(String var1, String var2) throws WebdavException;

    public AccessPrincipal caladdrToPrincipal(String var1) throws WebdavException;

    public String principalToCaladdr(AccessPrincipal var1) throws WebdavException;

    public CalPrincipalInfo getCalPrincipalInfo(AccessPrincipal var1) throws WebdavException;

    public Collection<String> getPrincipalCollectionSet(String var1) throws WebdavException;

    public Collection<CalPrincipalInfo> getPrincipals(String var1, PrincipalPropertySearch var2) throws WebdavException;

    public boolean validPrincipal(String var1) throws WebdavException;
}

