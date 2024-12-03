/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.persistence;

import com.atlassian.confluence.core.NotExportable;
import java.util.Date;

public abstract class AbstractPluginData
implements NotExportable {
    private long id;
    private String key;
    private String fileName;
    private Date lastModificationDate;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String toString() {
        return "PluginData: { fileName: " + this.fileName + ", lastModificationDate: " + this.lastModificationDate + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractPluginData that = (AbstractPluginData)o;
        if (this.id != that.id) {
            return false;
        }
        if (this.fileName != null ? !this.fileName.equals(that.fileName) : that.fileName != null) {
            return false;
        }
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        if (this.lastModificationDate != null && that.lastModificationDate != null) {
            if (this.lastModificationDate.getTime() != that.lastModificationDate.getTime()) {
                return false;
            }
        } else {
            if (this.lastModificationDate != null) {
                return false;
            }
            if (that.lastModificationDate != null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        result = 31 * result + (this.fileName != null ? this.fileName.hashCode() : 0);
        result = 31 * result + (this.lastModificationDate != null ? this.lastModificationDate.hashCode() : 0);
        return result;
    }
}

