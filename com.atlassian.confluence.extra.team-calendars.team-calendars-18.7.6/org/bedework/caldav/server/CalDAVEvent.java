/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.util.Set;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.Organizer;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavException;

public abstract class CalDAVEvent<T>
extends WdEntity<T> {
    public abstract String getScheduleTag() throws WebdavException;

    public abstract boolean getOrganizerSchedulingObject() throws WebdavException;

    public abstract boolean getAttendeeSchedulingObject() throws WebdavException;

    public abstract String getPrevScheduleTag() throws WebdavException;

    public abstract String getSummary() throws WebdavException;

    public abstract boolean isNew() throws WebdavException;

    public abstract boolean getDeleted() throws WebdavException;

    public abstract int getEntityType() throws WebdavException;

    public abstract void setOrganizer(Organizer var1) throws WebdavException;

    public abstract Organizer getOrganizer() throws WebdavException;

    public abstract void setOriginator(String var1) throws WebdavException;

    public abstract void setRecipients(Set<String> var1) throws WebdavException;

    public abstract Set<String> getRecipients() throws WebdavException;

    public abstract void addRecipient(String var1) throws WebdavException;

    public abstract Set<String> getAttendeeUris() throws WebdavException;

    public abstract void setScheduleMethod(int var1) throws WebdavException;

    public abstract int getScheduleMethod() throws WebdavException;

    public abstract String getUid() throws WebdavException;

    public abstract boolean generatePropertyValue(QName var1, XmlEmit var2) throws WebdavException;

    public abstract String toIcalString(int var1, String var2) throws WebdavException;
}

