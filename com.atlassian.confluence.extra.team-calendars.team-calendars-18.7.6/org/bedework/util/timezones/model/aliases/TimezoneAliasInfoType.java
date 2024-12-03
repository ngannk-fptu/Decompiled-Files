/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model.aliases;

import java.util.List;
import org.bedework.util.misc.ToString;
import org.bedework.util.timezones.model.aliases.AliasInfoType;

public class TimezoneAliasInfoType {
    protected String tzid;
    protected List<AliasInfoType> aliases;

    public String getTzid() {
        return this.tzid;
    }

    public void setTzid(String value) {
        this.tzid = value;
    }

    public List<AliasInfoType> getAliases() {
        return this.aliases;
    }

    public void setAliases(List<AliasInfoType> value) {
        this.aliases = value;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("tzid", this.getTzid());
        ts.append("aliases", this.getAliases(), true);
        return ts.toString();
    }
}

