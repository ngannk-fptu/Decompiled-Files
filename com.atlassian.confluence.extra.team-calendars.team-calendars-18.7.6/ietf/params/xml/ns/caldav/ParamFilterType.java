/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.caldav;

import ietf.params.xml.ns.caldav.IsNotDefinedType;
import ietf.params.xml.ns.caldav.TextMatchType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ParamFilterType", propOrder={"isNotDefined", "textMatch"})
public class ParamFilterType {
    @XmlElement(name="is-not-defined")
    protected IsNotDefinedType isNotDefined;
    @XmlElement(name="text-match")
    protected TextMatchType textMatch;
    @XmlAttribute(required=true)
    protected String name;

    public IsNotDefinedType getIsNotDefined() {
        return this.isNotDefined;
    }

    public void setIsNotDefined(IsNotDefinedType value) {
        this.isNotDefined = value;
    }

    public TextMatchType getTextMatch() {
        return this.textMatch;
    }

    public void setTextMatch(TextMatchType value) {
        this.textMatch = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

