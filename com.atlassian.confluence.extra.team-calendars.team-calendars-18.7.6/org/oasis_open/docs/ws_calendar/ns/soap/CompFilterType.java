/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.AnyCompType;
import org.oasis_open.docs.ws_calendar.ns.soap.IsNotDefinedType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropFilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.UTCTimeRangeType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CompFilterType", propOrder={"anyComp", "vcalendar", "baseComponent", "isNotDefined", "timeRange", "propFilter", "compFilter"})
public class CompFilterType {
    protected AnyCompType anyComp;
    @XmlElement(namespace="urn:ietf:params:xml:ns:icalendar-2.0")
    protected VcalendarType vcalendar;
    @XmlElementRef(name="baseComponent", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)
    protected JAXBElement<? extends BaseComponentType> baseComponent;
    @XmlElement(name="is-not-defined")
    protected IsNotDefinedType isNotDefined;
    protected UTCTimeRangeType timeRange;
    protected List<PropFilterType> propFilter;
    protected List<CompFilterType> compFilter;
    @XmlAttribute
    protected String test;

    public AnyCompType getAnyComp() {
        return this.anyComp;
    }

    public void setAnyComp(AnyCompType value) {
        this.anyComp = value;
    }

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

    public IsNotDefinedType getIsNotDefined() {
        return this.isNotDefined;
    }

    public void setIsNotDefined(IsNotDefinedType value) {
        this.isNotDefined = value;
    }

    public UTCTimeRangeType getTimeRange() {
        return this.timeRange;
    }

    public void setTimeRange(UTCTimeRangeType value) {
        this.timeRange = value;
    }

    public List<PropFilterType> getPropFilter() {
        if (this.propFilter == null) {
            this.propFilter = new ArrayList<PropFilterType>();
        }
        return this.propFilter;
    }

    public List<CompFilterType> getCompFilter() {
        if (this.compFilter == null) {
            this.compFilter = new ArrayList<CompFilterType>();
        }
        return this.compFilter;
    }

    public String getTest() {
        if (this.test == null) {
            return "anyof";
        }
        return this.test;
    }

    public void setTest(String value) {
        this.test = value;
    }
}

