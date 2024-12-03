/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.notifications.ChangedParameterType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class ChangedPropertyType {
    private String name;
    private String dataFrom;
    private String dataTo;
    private List<ChangedParameterType> changedParameter;

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

    public List<ChangedParameterType> getChangedParameter() {
        if (this.changedParameter == null) {
            this.changedParameter = new ArrayList<ChangedParameterType>();
        }
        return this.changedParameter;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.changedProperty, "name", this.getName());
        for (ChangedParameterType cp : this.getChangedParameter()) {
            cp.toXml(xml);
        }
        if (Boolean.parseBoolean(xml.getProperty("withBedeworkElements"))) {
            if (this.getDataFrom() != null) {
                xml.property(BedeworkServerTags.dataFrom, this.getDataFrom());
            }
            if (this.getDataTo() != null) {
                xml.property(BedeworkServerTags.dataTo, this.getDataTo());
            }
        }
        xml.closeTag(AppleServerTags.changedProperty);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("ChangedProperty:name", this.getName());
        for (ChangedParameterType cp : this.getChangedParameter()) {
            cp.toStringSegment(ts);
        }
        if (this.getDataFrom() != null) {
            ts.append("dataFrom", this.getDataFrom());
        }
        if (this.getDataTo() != null) {
            ts.append("dataTo", this.getDataTo());
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

