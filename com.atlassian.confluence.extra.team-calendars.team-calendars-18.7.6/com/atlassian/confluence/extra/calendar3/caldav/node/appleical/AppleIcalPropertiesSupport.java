/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node.appleical;

import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVEvent;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface AppleIcalPropertiesSupport {
    public boolean getCanShare() throws WebdavException;

    public boolean getCanPublish() throws WebdavException;

    public boolean isAlias() throws WebdavException;

    public String getAliasUri() throws WebdavException;

    public CalendarCalDAVEvent resolveAlias(boolean var1) throws WebdavException;
}

