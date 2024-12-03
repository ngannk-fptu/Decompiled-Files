/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.notifications.ChangedPropertyType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class ChangesType {
    private List<ChangedPropertyType> changedProperty;

    public List<ChangedPropertyType> getChangedProperty() {
        if (this.changedProperty == null) {
            this.changedProperty = new ArrayList<ChangedPropertyType>();
        }
        return this.changedProperty;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.changes);
        for (ChangedPropertyType cp : this.getChangedProperty()) {
            cp.toXml(xml);
        }
        xml.closeTag(AppleServerTags.changes);
    }

    protected void toStringSegment(ToString ts) {
        for (ChangedPropertyType cp : this.getChangedProperty()) {
            cp.toStringSegment(ts);
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

