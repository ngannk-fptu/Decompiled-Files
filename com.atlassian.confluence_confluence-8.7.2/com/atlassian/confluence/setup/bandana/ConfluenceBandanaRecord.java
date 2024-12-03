/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.setup.bandana;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ConfluenceBandanaRecord
implements Serializable {
    private long id;
    private String context;
    private String key;
    private String value;

    public ConfluenceBandanaRecord() {
    }

    public ConfluenceBandanaRecord(String context, String key, String value) {
        this.context = context;
        this.key = key;
        this.value = value;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfluenceBandanaRecord that = (ConfluenceBandanaRecord)o;
        if (this.context != null ? !this.context.equals(that.context) : that.context != null) {
            return false;
        }
        return !(this.key != null ? !this.key.equals(that.key) : that.key != null);
    }

    public int hashCode() {
        int result = this.context != null ? this.context.hashCode() : 0;
        result = 29 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", this.id).append("context", (Object)this.context).append("key", (Object)this.key).append("value", (Object)this.value).toString();
    }
}

