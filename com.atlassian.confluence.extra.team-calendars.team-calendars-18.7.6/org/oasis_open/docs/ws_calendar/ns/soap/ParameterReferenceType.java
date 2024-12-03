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

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ParameterReferenceType", propOrder={"baseParameter"})
public class ParameterReferenceType {
    @XmlElementRef(name="baseParameter", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)
    protected JAXBElement<? extends BaseParameterType> baseParameter;

    public JAXBElement<? extends BaseParameterType> getBaseParameter() {
        return this.baseParameter;
    }

    public void setBaseParameter(JAXBElement<? extends BaseParameterType> value) {
        this.baseParameter = value;
    }
}

