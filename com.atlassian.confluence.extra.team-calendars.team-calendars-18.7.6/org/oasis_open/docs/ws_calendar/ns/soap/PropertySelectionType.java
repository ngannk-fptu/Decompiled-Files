/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParametersSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertyReferenceType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PropertySelectionType", propOrder={"baseProperty", "parameters", "change"})
public class PropertySelectionType {
    @XmlElementRef(name="baseProperty", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)
    protected JAXBElement<? extends BasePropertyType> baseProperty;
    protected ParametersSelectionType parameters;
    protected PropertyReferenceType change;

    public JAXBElement<? extends BasePropertyType> getBaseProperty() {
        return this.baseProperty;
    }

    public void setBaseProperty(JAXBElement<? extends BasePropertyType> value) {
        this.baseProperty = value;
    }

    public ParametersSelectionType getParameters() {
        return this.parameters;
    }

    public void setParameters(ParametersSelectionType value) {
        this.parameters = value;
    }

    public PropertyReferenceType getChange() {
        return this.change;
    }

    public void setChange(PropertyReferenceType value) {
        this.change = value;
    }
}

