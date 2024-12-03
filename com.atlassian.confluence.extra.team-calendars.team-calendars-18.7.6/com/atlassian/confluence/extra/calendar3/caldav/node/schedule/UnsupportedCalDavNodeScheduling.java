/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node.schedule;

import com.atlassian.confluence.extra.calendar3.caldav.node.schedule.CalDavNodeSchedulingSupport;
import java.util.Set;
import org.bedework.caldav.server.Organizer;
import org.bedework.webdav.servlet.shared.WebdavException;

public final class UnsupportedCalDavNodeScheduling
implements CalDavNodeSchedulingSupport {
    @Override
    public String getScheduleTag() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getOrganizerSchedulingObject() throws WebdavException {
        return false;
    }

    @Override
    public boolean getAttendeeSchedulingObject() throws WebdavException {
        return false;
    }

    @Override
    public String getPrevScheduleTag() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOrganizer(Organizer value) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Organizer getOrganizer() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOriginator(String value) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRecipients(Set<String> value) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getRecipients() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRecipient(String value) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setScheduleMethod(int value) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getScheduleMethod() throws WebdavException {
        throw new UnsupportedOperationException();
    }
}

