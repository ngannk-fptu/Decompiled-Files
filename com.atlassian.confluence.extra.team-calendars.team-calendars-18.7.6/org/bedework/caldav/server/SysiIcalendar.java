/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import net.fortuna.ical4j.model.TimeZone;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.Organizer;
import org.bedework.util.calendar.IcalDefs;
import org.bedework.util.calendar.ScheduleMethods;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavException;

public abstract class SysiIcalendar
implements ScheduleMethods,
Iterator<WdEntity>,
Iterable<WdEntity>,
Serializable {
    public abstract String getProdid();

    public abstract String getVersion();

    public abstract String getCalscale();

    public abstract String getMethod();

    public abstract Collection<TimeZone> getTimeZones();

    public abstract Collection<Object> getComponents();

    public abstract IcalDefs.IcalComponentType getComponentType();

    public abstract int getMethodType();

    public abstract int getMethodType(String var1);

    public abstract String getMethodName(int var1);

    public abstract Organizer getOrganizer();

    public abstract CalDAVEvent getEvent() throws WebdavException;

    @Override
    public abstract Iterator<WdEntity> iterator();

    public abstract int size();

    public abstract boolean validItipMethodType();

    public abstract boolean requestMethodType();

    public abstract boolean replyMethodType();

    public abstract boolean itipRequestMethodType(int var1);

    public abstract boolean itipReplyMethodType(int var1);

    public abstract boolean validItipMethodType(int var1);

    public String toString() {
        StringBuilder sb = new StringBuilder("SysiIcalendar{prodid=");
        sb.append(this.getProdid());
        sb.append(", version=");
        sb.append(this.getVersion());
        sb.append("\n, method=");
        sb.append(String.valueOf(this.getMethod()));
        sb.append(", methodType=");
        sb.append(this.getMethodType());
        sb.append(", componentType=");
        sb.append((Object)this.getComponentType());
        sb.append("}");
        return sb.toString();
    }
}

