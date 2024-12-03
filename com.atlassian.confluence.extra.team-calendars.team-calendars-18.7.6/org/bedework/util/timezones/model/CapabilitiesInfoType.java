/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model;

import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.ToString;
import org.bedework.util.timezones.model.CapabilitiesTruncatedType;

public class CapabilitiesInfoType {
    protected String source;
    protected String primarySource;
    protected List<String> formats;
    protected CapabilitiesTruncatedType truncated;
    protected List<String> contacts;

    public String getSource() {
        return this.source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    public String getPrimarySource() {
        return this.primarySource;
    }

    public void setPrimarySource(String value) {
        this.primarySource = value;
    }

    public List<String> getFormats() {
        if (this.formats == null) {
            this.formats = new ArrayList<String>();
        }
        return this.formats;
    }

    public CapabilitiesTruncatedType getTruncated() {
        return this.truncated;
    }

    public void setTruncated(CapabilitiesTruncatedType value) {
        this.truncated = value;
    }

    public List<String> getContacts() {
        if (this.contacts == null) {
            this.contacts = new ArrayList<String>();
        }
        return this.contacts;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("source", this.getSource());
        ts.append("primarySource", this.getPrimarySource());
        ts.append("formats", this.getFormats());
        ts.append("truncated", this.getTruncated());
        ts.append("contacts", this.getContacts(), true);
        return ts.toString();
    }
}

