/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util;

import java.util.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.DateProperty;
import org.bedework.util.misc.Logged;
import org.bedework.util.misc.ToString;

public class TimeRange
extends Logged {
    private final DateTime start;
    private final DateTime end;
    private DateTime startExpanded;
    private DateTime endExpanded;
    private String tzid;
    private static final Dur oneDayForward = new Dur(1, 0, 0, 0);
    private static final Dur oneDayBack = new Dur(-1, 0, 0, 0);

    public TimeRange(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
        if (start != null) {
            this.startExpanded = this.inc(start, oneDayBack);
        }
        if (end != null) {
            this.endExpanded = this.inc(end, oneDayForward);
        }
    }

    private DateTime inc(DateTime dt, Dur dur) {
        Date jdt = dur.getTime(dt);
        return new DateTime(jdt);
    }

    public DateTime getStart() {
        return this.start;
    }

    public DateTime getEnd() {
        return this.end;
    }

    public DateTime getStartExpanded() {
        return this.startExpanded;
    }

    public DateTime getEndExpanded() {
        return this.endExpanded;
    }

    public void setTzid(String val) {
        this.tzid = val;
    }

    public String getTzid() {
        return this.tzid;
    }

    public boolean matches(Property candidate) {
        return candidate instanceof DateProperty;
    }

    protected void toStringSegment(ToString ts) {
        ts.append("start", this.start);
        ts.append("end", this.end);
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

