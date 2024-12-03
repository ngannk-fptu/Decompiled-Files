/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.bedework.access.Acl;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.sysinterface.RetrievalMode;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;

public interface CalDavEventManager {
    public Collection<CalDAVEvent> addEvent(CalDAVEvent var1, boolean var2, boolean var3) throws WebdavException;

    public void updateEvent(CalDAVEvent var1) throws WebdavException;

    public SysIntf.UpdateResult updateEvent(CalDAVEvent var1, List<ComponentSelectionType> var2) throws WebdavException;

    public Collection<CalDAVEvent> getEvents(CalDAVCollection var1, FilterBase var2, List<String> var3, RetrievalMode var4) throws WebdavException;

    public CalDAVEvent getEvent(CalDAVCollection var1, String var2) throws WebdavException;

    public void deleteEvent(CalDAVEvent var1, boolean var2) throws WebdavException;

    public Collection<SysIntf.SchedRecipientResult> requestFreeBusy(CalDAVEvent var1, boolean var2) throws WebdavException;

    public void getSpecialFreeBusy(String var1, Set<String> var2, String var3, TimeRange var4, Writer var5) throws WebdavException;

    public CalDAVEvent getFreeBusy(CalDAVCollection var1, int var2, TimeRange var3) throws WebdavException;

    public Acl.CurrentAccess checkAccess(WdEntity var1, int var2, boolean var3) throws WebdavException;

    public void updateAccess(CalDAVEvent var1, Acl var2) throws WebdavException;

    public boolean copyMove(CalDAVEvent var1, CalDAVCollection var2, String var3, boolean var4, boolean var5) throws WebdavException;
}

