/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.XmlValue
 */
package ietf.params.xml.ns.caldav;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="TextMatchType", propOrder={"value"})
public class TextMatchType {
    @XmlValue
    protected String value;
    @XmlAttribute
    protected String collation;
    @XmlAttribute(name="negate-condition")
    protected String negateCondition;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCollation() {
        if (this.collation == null) {
            return "i;ascii-casemap";
        }
        return this.collation;
    }

    public void setCollation(String value) {
        this.collation = value;
    }

    public String getNegateCondition() {
        if (this.negateCondition == null) {
            return "no";
        }
        return this.negateCondition;
    }

    public void setNegateCondition(String value) {
        this.negateCondition = value;
    }
}

