/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.notifications.BaseEntityChangeType;
import org.bedework.caldav.util.notifications.CalendarChangesType;
import org.bedework.caldav.util.notifications.PropType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class UpdatedType
extends BaseEntityChangeType {
    private boolean content;
    private PropType prop;
    private List<CalendarChangesType> calendarChanges;

    public void setContent(boolean val) {
        this.content = val;
    }

    public boolean getContent() {
        return this.content;
    }

    public void setProp(PropType val) {
        this.prop = val;
    }

    public PropType getProp() {
        return this.prop;
    }

    public List<CalendarChangesType> getCalendarChanges() {
        if (this.calendarChanges == null) {
            this.calendarChanges = new ArrayList<CalendarChangesType>();
        }
        return this.calendarChanges;
    }

    public UpdatedType copyForAlias(String collectionHref) {
        UpdatedType copy = new UpdatedType();
        this.copyForAlias(copy, collectionHref);
        copy.content = this.content;
        copy.prop = this.prop;
        if (this.calendarChanges != null) {
            copy.calendarChanges = new ArrayList<CalendarChangesType>(this.calendarChanges);
        }
        return copy;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.updated);
        this.toXmlSegment(xml);
        if (this.getContent()) {
            xml.emptyTag(AppleServerTags.content);
        }
        if (this.getProp() != null) {
            this.getProp().toXml(xml);
        }
        for (CalendarChangesType cc : this.getCalendarChanges()) {
            cc.toXml(xml);
        }
        xml.closeTag(AppleServerTags.updated);
    }

    @Override
    protected void toStringSegment(ToString ts) {
        super.toStringSegment(ts);
        if (this.getContent()) {
            ts.append("content", true);
        }
        if (this.getProp() != null) {
            this.getProp().toStringSegment(ts);
        }
        for (CalendarChangesType cc : this.getCalendarChanges()) {
            cc.toStringSegment(ts);
        }
    }
}

