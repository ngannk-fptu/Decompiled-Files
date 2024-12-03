/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.IsNotDefinedType;
import org.oasis_open.docs.ws_calendar.ns.soap.TextMatchType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ParamFilterType", propOrder={"baseParameter", "isNotDefined", "textMatch"})
public class ParamFilterType {
    @XmlElementRef(name="baseParameter", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)
    protected JAXBElement<? extends BaseParameterType> baseParameter;
    @XmlElement(name="is-not-defined")
    protected IsNotDefinedType isNotDefined;
    protected TextMatchType textMatch;

    public JAXBElement<? extends BaseParameterType> getBaseParameter() {
        return this.baseParameter;
    }

    public void setBaseParameter(JAXBElement<? extends BaseParameterType> value) {
        this.baseParameter = value;
    }

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
}

