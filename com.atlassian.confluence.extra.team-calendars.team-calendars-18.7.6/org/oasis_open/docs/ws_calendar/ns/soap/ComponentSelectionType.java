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

import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentsSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertiesSelectionType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ComponentSelectionType", propOrder={"vcalendar", "baseComponent", "properties", "components"})
public class ComponentSelectionType {
    @XmlElement(namespace="urn:ietf:params:xml:ns:icalendar-2.0")
    protected VcalendarType vcalendar;
    @XmlElementRef(name="baseComponent", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)
    protected JAXBElement<? extends BaseComponentType> baseComponent;
    protected PropertiesSelectionType properties;
    protected ComponentsSelectionType components;

    public VcalendarType getVcalendar() {
        return this.vcalendar;
    }

    public void setVcalendar(VcalendarType value) {
        this.vcalendar = value;
    }

    public JAXBElement<? extends BaseComponentType> getBaseComponent() {
        return this.baseComponent;
    }

    public void setBaseComponent(JAXBElement<? extends BaseComponentType> value) {
        this.baseComponent = value;
    }

    public PropertiesSelectionType getProperties() {
        return this.properties;
    }

    public void setProperties(PropertiesSelectionType value) {
        this.properties = value;
    }

    public ComponentsSelectionType getComponents() {
        return this.components;
    }

    public void setComponents(ComponentsSelectionType value) {
        this.components = value;
    }
}

