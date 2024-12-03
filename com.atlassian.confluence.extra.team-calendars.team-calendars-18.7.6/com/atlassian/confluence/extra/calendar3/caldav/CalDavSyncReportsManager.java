/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface CalDavSyncReportsManager {
    public String getSyncToken(CalDAVCollection var1) throws WebdavException;

    public SysIntf.SynchReportData getSyncReport(String var1, String var2, int var3, boolean var4) throws WebdavException;
}

