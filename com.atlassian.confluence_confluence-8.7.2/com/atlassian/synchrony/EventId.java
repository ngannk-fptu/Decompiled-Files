/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.synchrony;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class EventId
implements Serializable {
    private static final long serialVersionUID = 42L;
    private String rev;
    private String history;

    public String getHistory() {
        return this.history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getRev() {
        return this.rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EventId eventId = (EventId)o;
        return Objects.equals(this.rev, eventId.rev) && Objects.equals(this.history, eventId.history);
    }

    public int hashCode() {
        return Objects.hash(this.rev, this.history);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("history", (Object)this.history).append("rev", (Object)this.rev).toString();
    }
}

