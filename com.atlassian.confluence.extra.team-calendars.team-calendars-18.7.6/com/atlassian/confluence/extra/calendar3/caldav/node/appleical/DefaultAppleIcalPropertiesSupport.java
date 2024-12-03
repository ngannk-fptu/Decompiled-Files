/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node.appleical;

import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVEvent;
import com.atlassian.confluence.extra.calendar3.caldav.node.appleical.AppleIcalPropertiesSupport;
import org.bedework.webdav.servlet.shared.WebdavException;

public class DefaultAppleIcalPropertiesSupport
implements AppleIcalPropertiesSupport {
    @Override
    public boolean getCanShare() throws WebdavException {
        return false;
    }

    @Override
    public boolean getCanPublish() throws WebdavException {
        return false;
    }

    @Override
    public boolean isAlias() throws WebdavException {
        return false;
    }

    @Override
    public String getAliasUri() throws WebdavException {
        return null;
    }

    @Override
    public CalendarCalDAVEvent resolveAlias(boolean resolveSubAlias) throws WebdavException {
        return null;
    }
}

