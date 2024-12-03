/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model;

import java.util.List;
import org.bedework.util.misc.ToString;
import org.bedework.util.timezones.model.BaseResultType;
import org.bedework.util.timezones.model.TimezoneType;

public class TimezoneListType
extends BaseResultType {
    protected String synctoken;
    protected List<TimezoneType> timezones;

    public String getSynctoken() {
        return this.synctoken;
    }

    public void setSynctoken(String value) {
        this.synctoken = value;
    }

    public List<TimezoneType> getTimezones() {
        return this.timezones;
    }

    public void setTimezones(List<TimezoneType> val) {
        this.timezones = val;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("synctoken", this.getSynctoken());
        ts.append("timezones", this.getTimezones(), true);
        return ts.toString();
    }
}

