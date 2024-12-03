/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.notifications.RecurrenceType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class CalendarChangesType {
    private List<RecurrenceType> recurrence;

    public List<RecurrenceType> getRecurrence() {
        if (this.recurrence == null) {
            this.recurrence = new ArrayList<RecurrenceType>();
        }
        return this.recurrence;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.calendarChanges);
        for (RecurrenceType r : this.getRecurrence()) {
            r.toXml(xml);
        }
        xml.closeTag(AppleServerTags.calendarChanges);
    }

    protected void toStringSegment(ToString ts) {
        for (RecurrenceType r : this.getRecurrence()) {
            r.toStringSegment(ts);
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

