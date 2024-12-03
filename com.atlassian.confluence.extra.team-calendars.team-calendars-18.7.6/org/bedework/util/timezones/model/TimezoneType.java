/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package org.bedework.util.timezones.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import org.bedework.util.misc.ToString;
import org.bedework.util.timezones.model.LocalNameType;

public class TimezoneType {
    protected String tzid;
    protected String etag;
    protected Date lastModified;
    protected Boolean inactive;
    protected List<String> aliases;
    protected List<LocalNameType> localNames;

    public String getTzid() {
        return this.tzid;
    }

    public void setTzid(String value) {
        this.tzid = value;
    }

    public void setEtag(String val) {
        this.etag = val;
    }

    public String getEtag() {
        return this.etag;
    }

    @JsonProperty(value="last-modified")
    public Date getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(Date value) {
        this.lastModified = value;
    }

    public Boolean getInactive() {
        return this.inactive;
    }

    public void setInactive(Boolean value) {
        this.inactive = value;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public void setAliases(List<String> val) {
        this.aliases = val;
    }

    @JsonProperty(value="local-names")
    public List<LocalNameType> getLocalNames() {
        return this.localNames;
    }

    public void setLocalNames(List<LocalNameType> val) {
        this.localNames = val;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("tzid", this.getTzid());
        ts.append("etag", this.getEtag());
        ts.append("lastModified", this.getLastModified());
        ts.append("inactive", this.getInactive());
        ts.append("aliases", this.getAliases(), true);
        ts.append("localNames", this.getLocalNames(), true);
        return ts.toString();
    }
}

