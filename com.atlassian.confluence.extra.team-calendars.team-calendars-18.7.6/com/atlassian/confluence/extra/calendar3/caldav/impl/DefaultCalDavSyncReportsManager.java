/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import com.atlassian.confluence.extra.calendar3.caldav.CalDavSyncReportsManager;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.springframework.stereotype.Component;

@Component(value="calDavSyncReportsManager")
public class DefaultCalDavSyncReportsManager
implements CalDavSyncReportsManager {
    @Override
    public String getSyncToken(CalDAVCollection col) throws WebdavException {
        return null;
    }

    @Override
    public SysIntf.SynchReportData getSyncReport(String path, String token, int limit, boolean recurse) throws WebdavException {
        return null;
    }
}

