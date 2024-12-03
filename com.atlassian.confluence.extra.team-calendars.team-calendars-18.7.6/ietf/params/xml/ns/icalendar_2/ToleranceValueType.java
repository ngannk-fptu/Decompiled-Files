/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ToleranceValueType", propOrder={"startbefore", "startafter", "endbefore", "endafter", "durationlong", "durationshort", "precision"})
public class ToleranceValueType {
    protected String startbefore;
    protected String startafter;
    protected String endbefore;
    protected String endafter;
    protected String durationlong;
    protected String durationshort;
    protected String precision;

    public String getStartbefore() {
        return this.startbefore;
    }

    public void setStartbefore(String value) {
        this.startbefore = value;
    }

    public String getStartafter() {
        return this.startafter;
    }

    public void setStartafter(String value) {
        this.startafter = value;
    }

    public String getEndbefore() {
        return this.endbefore;
    }

    public void setEndbefore(String value) {
        this.endbefore = value;
    }

    public String getEndafter() {
        return this.endafter;
    }

    public void setEndafter(String value) {
        this.endafter = value;
    }

    public String getDurationlong() {
        return this.durationlong;
    }

    public void setDurationlong(String value) {
        this.durationlong = value;
    }

    public String getDurationshort() {
        return this.durationshort;
    }

    public void setDurationshort(String value) {
        this.durationshort = value;
    }

    public String getPrecision() {
        return this.precision;
    }

    public void setPrecision(String value) {
        this.precision = value;
    }
}

