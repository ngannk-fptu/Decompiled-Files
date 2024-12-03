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

import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ComponentReferenceType", propOrder={"baseComponent"})
public class ComponentReferenceType {
    @XmlElementRef(name="baseComponent", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)
    protected JAXBElement<? extends BaseComponentType> baseComponent;

    public JAXBElement<? extends BaseComponentType> getBaseComponent() {
        return this.baseComponent;
    }

    public void setBaseComponent(JAXBElement<? extends BaseComponentType> value) {
        this.baseComponent = value;
    }
}

