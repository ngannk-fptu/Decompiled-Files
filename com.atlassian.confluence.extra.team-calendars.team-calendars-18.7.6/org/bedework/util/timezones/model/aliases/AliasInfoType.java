/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model.aliases;

import java.util.Date;
import org.bedework.util.misc.ToString;

public class AliasInfoType {
    protected String alias;
    protected Date lastModified;
    protected String description;
    protected String source;

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String value) {
        this.alias = value;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(Date value) {
        this.lastModified = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("alias", this.getAlias());
        ts.append("lastModified", this.getLastModified());
        ts.append("description", this.getDescription());
        ts.append("source", this.getSource());
        return ts.toString();
    }
}

