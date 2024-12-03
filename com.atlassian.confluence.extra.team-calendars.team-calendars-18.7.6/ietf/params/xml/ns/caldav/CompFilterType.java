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
import ietf.params.xml.ns.caldav.PropFilterType;
import ietf.params.xml.ns.caldav.UTCTimeRangeType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CompFilterType", propOrder={"isNotDefined", "timeRange", "propFilter", "compFilter"})
public class CompFilterType {
    @XmlElement(name="is-not-defined")
    protected IsNotDefinedType isNotDefined;
    @XmlElement(name="time-range")
    protected UTCTimeRangeType timeRange;
    @XmlElement(name="prop-filter")
    protected List<PropFilterType> propFilter;
    @XmlElement(name="comp-filter")
    protected List<CompFilterType> compFilter;
    @XmlAttribute(required=true)
    protected String name;
    @XmlAttribute
    protected String test;

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

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
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

