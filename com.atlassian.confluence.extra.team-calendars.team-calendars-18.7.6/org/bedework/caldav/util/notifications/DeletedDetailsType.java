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

public class DeletedDetailsType {
    private String deletedComponent;
    private String deletedSummary;
    private String deletedNextInstance;
    private String deletedNextInstanceTzid;
    private boolean deletedHadMoreInstances;
    private String deletedDisplayname;
    private List<ChangedPropertyType> deletedProps = new ArrayList<ChangedPropertyType>();

    public void setDeletedComponent(String val) {
        this.deletedComponent = val;
    }

    public String getDeletedComponent() {
        return this.deletedComponent;
    }

    public void setDeletedSummary(String val) {
        this.deletedSummary = val;
    }

    public String getDeletedSummary() {
        return this.deletedSummary;
    }

    public void setDeletedNextInstance(String val) {
        this.deletedNextInstance = val;
    }

    public String getDeletedNextInstance() {
        return this.deletedNextInstance;
    }

    public void setDeletedNextInstanceTzid(String val) {
        this.deletedNextInstanceTzid = val;
    }

    public String getDeletedNextInstanceTzid() {
        return this.deletedNextInstanceTzid;
    }

    public void setDeletedHadMoreInstances(boolean val) {
        this.deletedHadMoreInstances = val;
    }

    public boolean getDeletedHadMoreInstances() {
        return this.deletedHadMoreInstances;
    }

    public void setDeletedDisplayname(String val) {
        this.deletedDisplayname = val;
    }

    public String getDeletedDisplayname() {
        return this.deletedDisplayname;
    }

    public void setDeletedProps(List<ChangedPropertyType> val) {
        this.deletedProps = val;
    }

    public List<ChangedPropertyType> getDeletedProps() {
        return this.deletedProps;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.deletedDetails);
        if (this.getDeletedDisplayname() != null) {
            xml.property(AppleServerTags.deletedDisplayname, this.getDeletedDisplayname());
            return;
        }
        xml.property(AppleServerTags.deletedComponent, this.getDeletedComponent());
        xml.property(AppleServerTags.deletedSummary, this.getDeletedSummary());
        if (this.getDeletedNextInstance() != null) {
            if (this.getDeletedNextInstanceTzid() == null) {
                xml.property(AppleServerTags.deletedNextInstance, this.getDeletedNextInstance());
            } else {
                xml.openTagNoNewline(AppleServerTags.deletedNextInstance, "tzid", this.getDeletedNextInstanceTzid());
                xml.value(this.getDeletedNextInstance());
                xml.closeTagNoblanks(AppleServerTags.deletedNextInstance);
            }
        }
        if (this.getDeletedHadMoreInstances()) {
            xml.emptyTag(AppleServerTags.deletedHadMoreInstances);
        }
        for (ChangedPropertyType cp : this.getDeletedProps()) {
            cp.toXml(xml);
        }
        xml.closeTag(AppleServerTags.deletedDetails);
    }

    protected void toStringSegment(ToString ts) {
        if (this.getDeletedDisplayname() != null) {
            ts.append("deletedDisplayname", this.getDeletedDisplayname());
            return;
        }
        ts.append("deletedComponent", this.getDeletedComponent());
        ts.append("deletedSummary", this.getDeletedSummary());
        if (this.getDeletedNextInstance() != null) {
            ts.append("deletedNextInstance", this.getDeletedNextInstance());
            ts.append("deletedNextInstanceTzid", this.getDeletedNextInstanceTzid());
        }
        if (this.getDeletedHadMoreInstances()) {
            ts.append("deletedHadMoreInstances", this.getDeletedHadMoreInstances());
        }
        for (ChangedPropertyType cp : this.getDeletedProps()) {
            ts.append("deletedProp", cp);
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

