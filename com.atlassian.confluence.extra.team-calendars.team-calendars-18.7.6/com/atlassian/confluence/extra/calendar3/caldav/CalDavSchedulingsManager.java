/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import java.util.Collection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface CalDavSchedulingsManager {
    public Collection<String> getFreebusySet() throws WebdavException;

    public Collection<SysIntf.SchedRecipientResult> schedule(CalDAVEvent var1) throws WebdavException;
}

