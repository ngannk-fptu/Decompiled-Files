/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.sysinterface;

import ietf.params.xml.ns.caldav.ExpandType;
import ietf.params.xml.ns.caldav.LimitFreebusySetType;
import ietf.params.xml.ns.caldav.LimitRecurrenceSetType;
import java.io.Serializable;

public class RetrievalMode
implements Serializable {
    private ExpandType expand;
    private LimitRecurrenceSetType limitRecurrenceSet;
    private LimitFreebusySetType limitFreebusySet;

    public void setExpand(ExpandType val) {
        this.expand = val;
    }

    public ExpandType getExpand() {
        return this.expand;
    }

    public void setLimitRecurrenceSet(LimitRecurrenceSetType val) {
        this.limitRecurrenceSet = val;
    }

    public LimitRecurrenceSetType getLimitRecurrenceSet() {
        return this.limitRecurrenceSet;
    }

    public void setLimitFreebusySet(LimitFreebusySetType val) {
        this.limitFreebusySet = val;
    }

    public LimitFreebusySetType getLimitFreebusySet() {
        return this.limitFreebusySet;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("RetrievalMode{");
        String start = null;
        String end = null;
        String name = null;
        if (this.expand != null) {
            name = "expand";
            start = this.expand.getStart().toString();
            end = this.expand.getEnd().toString();
        } else if (this.limitFreebusySet != null) {
            name = "limit-freebusy-set";
            start = this.limitFreebusySet.getStart().toString();
            end = this.limitFreebusySet.getEnd().toString();
        } else if (this.limitRecurrenceSet != null) {
            name = "limit-recurrence-set";
            start = this.limitRecurrenceSet.getStart().toString();
            end = this.limitRecurrenceSet.getEnd().toString();
        }
        sb.append(name);
        sb.append(", ");
        sb.append(", start=");
        sb.append(start);
        sb.append(", end=");
        sb.append(end);
        sb.append("}");
        return sb.toString();
    }
}

