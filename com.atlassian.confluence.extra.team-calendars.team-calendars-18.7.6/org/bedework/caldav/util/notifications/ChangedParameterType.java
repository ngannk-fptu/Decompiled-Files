/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class ChangedParameterType {
    private String name;
    private String dataFrom;
    private String dataTo;

    public void setName(String val) {
        this.name = val;
    }

    public String getName() {
        return this.name;
    }

    public void setDataFrom(String val) {
        this.dataFrom = val;
    }

    public String getDataFrom() {
        return this.dataFrom;
    }

    public void setDataTo(String val) {
        this.dataTo = val;
    }

    public String getDataTo() {
        return this.dataTo;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        if (Boolean.parseBoolean(xml.getProperty("withBedeworkElements"))) {
            xml.openTag(AppleServerTags.changedParameter, "name", this.getName());
            if (this.dataFrom != null) {
                xml.property(BedeworkServerTags.dataFrom, this.getDataFrom());
            }
            if (this.dataTo != null) {
                xml.property(BedeworkServerTags.dataTo, this.getDataTo());
            }
            xml.closeTag(AppleServerTags.changedParameter);
        } else {
            xml.emptyTag(AppleServerTags.changedParameter, "name", this.getName());
        }
    }

    protected void toStringSegment(ToString ts) {
        ts.append("ChangedParameter:name", this.getName());
        if (this.dataFrom != null) {
            ts.append("dataFrom", this.getDataFrom());
        }
        if (this.dataTo != null) {
            ts.append("dataTo", this.getDataTo());
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

