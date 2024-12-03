/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model.aliases;

import java.util.Date;
import java.util.List;
import org.bedework.util.misc.ToString;
import org.bedework.util.timezones.model.aliases.TimezoneAliasInfoType;

public class TimezonesAliasInfoType {
    protected Date dtstamp;
    protected List<TimezoneAliasInfoType> timezoneAliasInfo;

    public Date getDtstamp() {
        return this.dtstamp;
    }

    public void setDtstamp(Date value) {
        this.dtstamp = value;
    }

    public List<TimezoneAliasInfoType> getTimezoneAliasInfo() {
        return this.timezoneAliasInfo;
    }

    public void setTimezoneAliasInfo(List<TimezoneAliasInfoType> value) {
        this.timezoneAliasInfo = value;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("dtstamp", this.getDtstamp());
        ts.append("timezoneAliasInfo", this.getTimezoneAliasInfo(), true);
        return ts.toString();
    }
}

