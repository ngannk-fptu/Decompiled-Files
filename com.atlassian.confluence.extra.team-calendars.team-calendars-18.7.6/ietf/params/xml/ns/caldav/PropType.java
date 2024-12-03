/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.caldav;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PropType")
public class PropType {
    @XmlAttribute(required=true)
    protected String name;
    @XmlAttribute
    protected String novalue;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getNovalue() {
        if (this.novalue == null) {
            return "no";
        }
        return this.novalue;
    }

    public void setNovalue(String value) {
        this.novalue = value;
    }
}

