/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model;

import java.util.List;
import org.bedework.util.misc.ToString;
import org.bedework.util.timezones.model.BaseResultType;
import org.bedework.util.timezones.model.ObservanceType;

public class ExpandedTimezoneType
extends BaseResultType {
    protected String tzid;
    protected String dtstamp;
    protected List<ObservanceType> observances;

    public String getTzid() {
        return this.tzid;
    }

    public void setTzid(String value) {
        this.tzid = value;
    }

    public String getDtstamp() {
        return this.dtstamp;
    }

    public void setDtstamp(String value) {
        this.dtstamp = value;
    }

    public List<ObservanceType> getObservances() {
        return this.observances;
    }

    public void setObservances(List<ObservanceType> val) {
        this.observances = val;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("dtstamp", this.getDtstamp());
        ts.append("tzid", this.getTzid());
        ts.append("observances", this.getObservances(), true);
        return ts.toString();
    }
}

