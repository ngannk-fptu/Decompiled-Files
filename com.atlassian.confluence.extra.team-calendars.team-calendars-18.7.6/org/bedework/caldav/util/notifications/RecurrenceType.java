/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.notifications.ChangesType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class RecurrenceType {
    private String recurrenceid;
    private boolean added;
    private boolean removed;
    private List<ChangesType> changes;

    public void setRecurrenceid(String val) {
        this.recurrenceid = val;
    }

    public String getRecurrenceid() {
        return this.recurrenceid;
    }

    public void setAdded(boolean val) {
        this.added = val;
    }

    public boolean getAdded() {
        return this.added;
    }

    public void setRemoved(boolean val) {
        this.removed = val;
    }

    public boolean getRemoved() {
        return this.removed;
    }

    public List<ChangesType> getChanges() {
        if (this.changes == null) {
            this.changes = new ArrayList<ChangesType>();
        }
        return this.changes;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.recurrence);
        if (this.getRecurrenceid() == null) {
            xml.emptyTag(AppleServerTags.master);
        } else {
            xml.property(AppleServerTags.recurrenceid, this.getRecurrenceid());
        }
        if (this.getAdded()) {
            xml.emptyTag(AppleServerTags.added);
        } else if (this.getRemoved()) {
            xml.emptyTag(AppleServerTags.removed);
        } else {
            for (ChangesType c : this.getChanges()) {
                c.toXml(xml);
            }
        }
        xml.closeTag(AppleServerTags.recurrence);
    }

    protected void toStringSegment(ToString ts) {
        if (this.getRecurrenceid() == null) {
            ts.append("master");
        } else {
            ts.append("recurrenceid", this.getRecurrenceid());
        }
        if (this.getAdded()) {
            ts.append("added");
        } else if (this.getRemoved()) {
            ts.append("removed");
        } else {
            for (ChangesType c : this.getChanges()) {
                c.toStringSegment(ts);
            }
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

