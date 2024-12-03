/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class ChildUpdatedType {
    private int count;

    public void setCount(int val) {
        this.count = val;
    }

    public int getCount() {
        return this.count;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.property(AppleServerTags.childUpdated, String.valueOf(this.getCount()));
    }

    protected void toStringSegment(ToString ts) {
        ts.append("childUpdated", this.getCount());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

