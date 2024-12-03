/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import ietf.params.xml.ns.icalendar_2.IcalendarType;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import net.fortuna.ical4j.model.Calendar;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.SysiIcalendar;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface CalDavMisc {
    public Calendar toCalendar(CalDAVEvent var1, boolean var2) throws WebdavException;

    public IcalendarType toIcalendar(CalDAVEvent var1, boolean var2, IcalendarType var3) throws WebdavException;

    public String toJcal(CalDAVEvent var1, boolean var2, IcalendarType var3) throws WebdavException;

    public String toIcalString(Calendar var1, String var2) throws WebdavException;

    public String writeCalendar(Collection<CalDAVEvent> var1, SysIntf.MethodEmitted var2, XmlEmit var3, Writer var4, String var5) throws WebdavException;

    public SysiIcalendar fromIcal(CalDAVCollection var1, Reader var2, String var3, SysIntf.IcalResultType var4, boolean var5) throws WebdavException;

    public SysiIcalendar fromIcal(CalDAVCollection var1, IcalendarType var2, SysIntf.IcalResultType var3) throws WebdavException;

    public String toStringTzCalendar(String var1) throws WebdavException;

    public String tzidFromTzdef(String var1) throws WebdavException;

    public boolean validateAlarm(String var1) throws WebdavException;

    public void rollback();

    public void close() throws WebdavException;
}

