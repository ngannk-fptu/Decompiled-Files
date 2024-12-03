/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="date")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class DateEntity {
    @XmlAttribute(name="friendly")
    private String friendly;
    @XmlAttribute(name="date")
    private String date;

    public String getFriendly() {
        return this.friendly;
    }

    public void setFriendly(String friendly) {
        this.friendly = friendly;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString() {
        return new StringJoiner(", ", DateEntity.class.getSimpleName() + "[", "]").add("friendly='" + this.friendly + "'").add("date='" + this.date + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateEntity)) {
            return false;
        }
        DateEntity that = (DateEntity)o;
        return Objects.equals(this.date, that.date);
    }

    public int hashCode() {
        return Objects.hash(this.date);
    }
}

