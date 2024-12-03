/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node.freebusy;

import java.util.Set;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface CalDavNodeFreeBusySupport {
    public void setAffectsFreeBusy(boolean var1) throws WebdavException;

    public boolean getAffectsFreeBusy() throws WebdavException;

    public boolean freeBusyAllowed() throws WebdavException;

    public Set<String> getAttendeeUris() throws WebdavException;
}

