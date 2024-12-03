/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import java.util.Collection;
import org.bedework.access.Acl;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface CalDavCollectionManager {
    public CalDAVCollection newCollectionObject(boolean var1, String var2) throws WebdavException;

    public void updateAccess(CalDAVCollection var1, Acl var2) throws WebdavException;

    public int makeCollection(CalDAVCollection var1) throws WebdavException;

    public void copyMove(CalDAVCollection var1, CalDAVCollection var2, boolean var3, boolean var4) throws WebdavException;

    public CalDAVCollection getCollection(String var1) throws WebdavException;

    public void updateCollection(CalDAVCollection var1) throws WebdavException;

    public void deleteCollection(CalDAVCollection var1, boolean var2) throws WebdavException;

    public Collection<CalDAVCollection> getCollections(CalDAVCollection var1) throws WebdavException;

    public void setSubCalendarHexColor(SubCalendar var1);
}

