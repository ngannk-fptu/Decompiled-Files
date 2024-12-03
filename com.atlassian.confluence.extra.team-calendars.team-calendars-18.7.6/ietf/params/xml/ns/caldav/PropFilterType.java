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
import ietf.params.xml.ns.caldav.ParamFilterType;
import ietf.params.xml.ns.caldav.TextMatchType;
import ietf.params.xml.ns.caldav.UTCTimeRangeType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PropFilterType", propOrder={"isNotDefined", "timeRange", "textMatch", "paramFilter"})
public class PropFilterType {
    @XmlElement(name="is-not-defined")
    protected IsNotDefinedType isNotDefined;
    @XmlElement(name="time-range")
    protected UTCTimeRangeType timeRange;
    @XmlElement(name="text-match")
    protected TextMatchType textMatch;
    @XmlElement(name="param-filter")
    protected List<ParamFilterType> paramFilter;
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

