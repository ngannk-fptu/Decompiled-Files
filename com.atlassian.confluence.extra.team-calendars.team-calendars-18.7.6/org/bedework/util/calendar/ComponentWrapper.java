/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar;

import java.sql.Timestamp;
import java.util.Date;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.Sequence;

public class ComponentWrapper {
    private PropertyList pl;

    public ComponentWrapper(Component comp) {
        this.pl = comp.getProperties();
    }

    public boolean getPublic() {
        return "PUBLIC".equals(this.getPval("CLASS"));
    }

    public String getCreated() {
        return this.getPval("CREATED");
    }

    public String getDescription() {
        return this.getPval("DESCRIPTION");
    }

    public Timestamp getDtend() {
        Property prop = this.getProp("DTEND");
        if (prop == null) {
            return null;
        }
        return this.makeSqlTimestamp(((DtEnd)prop).getDate());
    }

    public String getDtStamp() {
        return this.getPval("DTSTAMP");
    }

    public String getDtstart() {
        return this.getPval("DTSTART");
    }

    public String getDue() {
        return this.getPval("DUE");
    }

    public String getDuration() {
        return this.getPval("DURATION");
    }

    public String getLastModified() {
        return this.getPval("CREATED");
    }

    public Integer getSequence() {
        Property prop = this.getProp("SEQUENCE");
        if (prop == null) {
            return null;
        }
        return new Integer(((Sequence)prop).getSequenceNo());
    }

    public String getStatus() {
        return this.getPval("STATUS");
    }

    public String getSummary() {
        return this.getPval("SUMMARY");
    }

    public String getTransp() {
        return this.getPval("TRANSP");
    }

    public String getUid() {
        return this.getPval("UID");
    }

    private Property getProp(String name) {
        if (this.pl == null) {
            return null;
        }
        return this.pl.getProperty(name);
    }

    private String getPval(String name) {
        Property prop = this.getProp(name);
        if (prop == null) {
            return null;
        }
        return prop.getValue();
    }

    private Timestamp makeSqlTimestamp(Date dtTm) {
        return new Timestamp(dtTm.getTime());
    }
}

