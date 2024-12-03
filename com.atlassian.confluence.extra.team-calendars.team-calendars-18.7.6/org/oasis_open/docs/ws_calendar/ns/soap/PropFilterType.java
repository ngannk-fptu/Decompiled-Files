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

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.IsNotDefinedType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParamFilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.TextMatchType;
import org.oasis_open.docs.ws_calendar.ns.soap.UTCTimeRangeType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PropFilterType", propOrder={"baseProperty", "isNotDefined", "timeRange", "textMatch", "paramFilter"})
public class PropFilterType {
    @XmlElementRef(name="baseProperty", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)
    protected JAXBElement<? extends BasePropertyType> baseProperty;
    @XmlElement(name="is-not-defined")
    protected IsNotDefinedType isNotDefined;
    protected UTCTimeRangeType timeRange;
    protected TextMatchType textMatch;
    protected List<ParamFilterType> paramFilter;
    @XmlAttribute
    protected String test;

    public JAXBElement<? extends BasePropertyType> getBaseProperty() {
        return this.baseProperty;
    }

    public void setBaseProperty(JAXBElement<? extends BasePropertyType> value) {
        this.baseProperty = value;
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

    public TextMatchType getTextMatch() {
        return this.textMatch;
    }

    public void setTextMatch(TextMatchType value) {
        this.textMatch = value;
    }

    public List<ParamFilterType> getParamFilter() {
        if (this.paramFilter == null) {
            this.paramFilter = new ArrayList<ParamFilterType>();
        }
        return this.paramFilter;
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

