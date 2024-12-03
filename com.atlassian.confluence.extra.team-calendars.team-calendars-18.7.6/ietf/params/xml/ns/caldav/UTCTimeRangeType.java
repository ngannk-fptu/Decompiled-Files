/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.caldav;

import ietf.params.xml.ns.caldav.ExpandType;
import ietf.params.xml.ns.caldav.LimitFreebusySetType;
import ietf.params.xml.ns.caldav.LimitRecurrenceSetType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="UTCTimeRangeType")
@XmlSeeAlso(value={LimitRecurrenceSetType.class, ExpandType.class, LimitFreebusySetType.class})
public class UTCTimeRangeType {
    @XmlAttribute
    protected String start;
    @XmlAttribute
    protected String end;

    public String getStart() {
        return this.start;
    }

    public void setStart(String value) {
        this.start = value;
    }

    public String getEnd() {
        return this.end;
    }

    public void setEnd(String value) {
        this.end = value;
    }
}

