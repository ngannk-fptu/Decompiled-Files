/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.bean;

import com.atlassian.core.util.Clock;
import java.util.Date;

public class EntityObject
implements Cloneable {
    private long id = 0L;
    private Date creationDate;
    private Date lastModificationDate;
    private Clock clock;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate != null ? this.lastModificationDate : this.creationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public Date getCurrentDate() {
        if (this.clock != null) {
            return this.clock.getCurrentDate();
        }
        return new Date();
    }

    public int hashCode() {
        return (int)(this.id ^ this.id >>> 32);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityObject)) {
            return false;
        }
        EntityObject entityObject = (EntityObject)o;
        return this.id == entityObject.getId();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

