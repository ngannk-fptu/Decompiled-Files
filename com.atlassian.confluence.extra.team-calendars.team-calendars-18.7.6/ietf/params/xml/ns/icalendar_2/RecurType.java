/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.FreqRecurType;
import ietf.params.xml.ns.icalendar_2.UntilRecurType;
import ietf.params.xml.ns.icalendar_2.WeekdayRecurType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="RecurType", propOrder={"freq", "until", "count", "interval", "bysecond", "byminute", "byhour", "byday", "byyearday", "bymonthday", "byweekno", "bymonth", "bysetpos", "wkst"})
public class RecurType {
    @XmlElement(required=true)
    protected FreqRecurType freq;
    protected UntilRecurType until;
    protected BigInteger count;
    protected String interval;
    protected List<String> bysecond;
    protected List<String> byminute;
    protected List<String> byhour;
    protected List<String> byday;
    protected List<String> byyearday;
    @XmlElement(type=Integer.class)
    protected List<Integer> bymonthday;
    protected List<String> byweekno;
    @XmlElement(type=Integer.class)
    protected List<Integer> bymonth;
    protected List<BigInteger> bysetpos;
    protected WeekdayRecurType wkst;

    public FreqRecurType getFreq() {
        return this.freq;
    }

    public void setFreq(FreqRecurType value) {
        this.freq = value;
    }

    public UntilRecurType getUntil() {
        return this.until;
    }

    public void setUntil(UntilRecurType value) {
        this.until = value;
    }

    public BigInteger getCount() {
        return this.count;
    }

    public void setCount(BigInteger value) {
        this.count = value;
    }

    public String getInterval() {
        return this.interval;
    }

    public void setInterval(String value) {
        this.interval = value;
    }

    public List<String> getBysecond() {
        if (this.bysecond == null) {
            this.bysecond = new ArrayList<String>();
        }
        return this.bysecond;
    }

    public List<String> getByminute() {
        if (this.byminute == null) {
            this.byminute = new ArrayList<String>();
        }
        return this.byminute;
    }

    public List<String> getByhour() {
        if (this.byhour == null) {
            this.byhour = new ArrayList<String>();
        }
        return this.byhour;
    }

    public List<String> getByday() {
        if (this.byday == null) {
            this.byday = new ArrayList<String>();
        }
        return this.byday;
    }

    public List<String> getByyearday() {
        if (this.byyearday == null) {
            this.byyearday = new ArrayList<String>();
        }
        return this.byyearday;
    }

    public List<Integer> getBymonthday() {
        if (this.bymonthday == null) {
            this.bymonthday = new ArrayList<Integer>();
        }
        return this.bymonthday;
    }

    public List<String> getByweekno() {
        if (this.byweekno == null) {
            this.byweekno = new ArrayList<String>();
        }
        return this.byweekno;
    }

    public List<Integer> getBymonth() {
        if (this.bymonth == null) {
            this.bymonth = new ArrayList<Integer>();
        }
        return this.bymonth;
    }

    public List<BigInteger> getBysetpos() {
        if (this.bysetpos == null) {
            this.bysetpos = new ArrayList<BigInteger>();
        }
        return this.bysetpos;
    }

    public WeekdayRecurType getWkst() {
        return this.wkst;
    }

    public void setWkst(WeekdayRecurType value) {
        this.wkst = value;
    }
}

