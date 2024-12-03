/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import com.atlassian.confluence.extra.calendar3.caldav.CalDavSchedulingsManager;
import java.util.Collection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.springframework.stereotype.Component;

@Component(value="calDavSchedulingsManager")
public class DefaultCalDavSchedulingsManager
implements CalDavSchedulingsManager {
    @Override
    public Collection<String> getFreebusySet() throws WebdavException {
        return null;
    }

    @Override
    public Collection<SysIntf.SchedRecipientResult> schedule(CalDAVEvent ev) throws WebdavException {
        return null;
    }
}

