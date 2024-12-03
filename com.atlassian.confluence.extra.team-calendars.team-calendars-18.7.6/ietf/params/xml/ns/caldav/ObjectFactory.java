/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package ietf.params.xml.ns.caldav;

import ietf.params.xml.ns.caldav.AllcompType;
import ietf.params.xml.ns.caldav.AllpropType;
import ietf.params.xml.ns.caldav.CalendarDataType;
import ietf.params.xml.ns.caldav.CompFilterType;
import ietf.params.xml.ns.caldav.CompType;
import ietf.params.xml.ns.caldav.ExpandType;
import ietf.params.xml.ns.caldav.FilterType;
import ietf.params.xml.ns.caldav.IsNotDefinedType;
import ietf.params.xml.ns.caldav.LimitFreebusySetType;
import ietf.params.xml.ns.caldav.LimitRecurrenceSetType;
import ietf.params.xml.ns.caldav.ParamFilterType;
import ietf.params.xml.ns.caldav.PropFilterType;
import ietf.params.xml.ns.caldav.PropType;
import ietf.params.xml.ns.caldav.TextMatchType;
import ietf.params.xml.ns.caldav.TimezoneIdType;
import ietf.params.xml.ns.caldav.TimezoneType;
import ietf.params.xml.ns.caldav.UTCTimeRangeType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    private static final QName _LimitFreebusySet_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "limit-freebusy-set");
    private static final QName _IsNotDefined_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "is-not-defined");
    private static final QName _TextMatch_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "text-match");
    private static final QName _Comp_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "comp");
    private static final QName _Allprop_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "allprop");
    private static final QName _PropFilter_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "prop-filter");
    private static final QName _Prop_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "prop");
    private static final QName _LimitRecurrenceSet_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "limit-recurrence-set");
    private static final QName _Filter_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "filter");
    private static final QName _TimeRange_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "time-range");
    private static final QName _TimezoneId_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "timezone-id");
    private static final QName _Allcomp_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "allcomp");
    private static final QName _CompFilter_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "comp-filter");
    private static final QName _Expand_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "expand");
    private static final QName _ParamFilter_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "param-filter");
    private static final QName _Timezone_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "timezone");
    private static final QName _CalendarData_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "calendar-data");

    public IsNotDefinedType createIsNotDefinedType() {
        return new IsNotDefinedType();
    }

    public AllpropType createAllpropType() {
        return new AllpropType();
    }

    public ExpandType createExpandType() {
        return new ExpandType();
    }

    public TimezoneType createTimezoneType() {
        return new TimezoneType();
    }

    public PropType createPropType() {
        return new PropType();
    }

    public CompFilterType createCompFilterType() {
        return new CompFilterType();
    }

    public TextMatchType createTextMatchType() {
        return new TextMatchType();
    }

    public PropFilterType createPropFilterType() {
        return new PropFilterType();
    }

    public CompType createCompType() {
        return new CompType();
    }

    public AllcompType createAllcompType() {
        return new AllcompType();
    }

    public UTCTimeRangeType createUTCTimeRangeType() {
        return new UTCTimeRangeType();
    }

    public TimezoneIdType createTimezoneIdType() {
        return new TimezoneIdType();
    }

    public CalendarDataType createCalendarDataType() {
        return new CalendarDataType();
    }

    public ParamFilterType createParamFilterType() {
        return new ParamFilterType();
    }

    public LimitFreebusySetType createLimitFreebusySetType() {
        return new LimitFreebusySetType();
    }

    public LimitRecurrenceSetType createLimitRecurrenceSetType() {
        return new LimitRecurrenceSetType();
    }

    public FilterType createFilterType() {
        return new FilterType();
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="limit-freebusy-set")
    public JAXBElement<LimitFreebusySetType> createLimitFreebusySet(LimitFreebusySetType value) {
        return new JAXBElement(_LimitFreebusySet_QNAME, LimitFreebusySetType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="is-not-defined")
    public JAXBElement<IsNotDefinedType> createIsNotDefined(IsNotDefinedType value) {
        return new JAXBElement(_IsNotDefined_QNAME, IsNotDefinedType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="text-match")
    public JAXBElement<TextMatchType> createTextMatch(TextMatchType value) {
        return new JAXBElement(_TextMatch_QNAME, TextMatchType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="comp")
    public JAXBElement<CompType> createComp(CompType value) {
        return new JAXBElement(_Comp_QNAME, CompType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="allprop")
    public JAXBElement<AllpropType> createAllprop(AllpropType value) {
        return new JAXBElement(_Allprop_QNAME, AllpropType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="prop-filter")
    public JAXBElement<PropFilterType> createPropFilter(PropFilterType value) {
        return new JAXBElement(_PropFilter_QNAME, PropFilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="prop")
    public JAXBElement<PropType> createProp(PropType value) {
        return new JAXBElement(_Prop_QNAME, PropType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="limit-recurrence-set")
    public JAXBElement<LimitRecurrenceSetType> createLimitRecurrenceSet(LimitRecurrenceSetType value) {
        return new JAXBElement(_LimitRecurrenceSet_QNAME, LimitRecurrenceSetType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="filter")
    public JAXBElement<FilterType> createFilter(FilterType value) {
        return new JAXBElement(_Filter_QNAME, FilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="time-range")
    public JAXBElement<UTCTimeRangeType> createTimeRange(UTCTimeRangeType value) {
        return new JAXBElement(_TimeRange_QNAME, UTCTimeRangeType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="timezone-id")
    public JAXBElement<TimezoneIdType> createTimezoneId(TimezoneIdType value) {
        return new JAXBElement(_TimezoneId_QNAME, TimezoneIdType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="allcomp")
    public JAXBElement<AllcompType> createAllcomp(AllcompType value) {
        return new JAXBElement(_Allcomp_QNAME, AllcompType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="comp-filter")
    public JAXBElement<CompFilterType> createCompFilter(CompFilterType value) {
        return new JAXBElement(_CompFilter_QNAME, CompFilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="expand")
    public JAXBElement<ExpandType> createExpand(ExpandType value) {
        return new JAXBElement(_Expand_QNAME, ExpandType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="param-filter")
    public JAXBElement<ParamFilterType> createParamFilter(ParamFilterType value) {
        return new JAXBElement(_ParamFilter_QNAME, ParamFilterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="timezone")
    public JAXBElement<TimezoneType> createTimezone(TimezoneType value) {
        return new JAXBElement(_Timezone_QNAME, TimezoneType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="urn:ietf:params:xml:ns:caldav", name="calendar-data")
    public JAXBElement<CalendarDataType> createCalendarData(CalendarDataType value) {
        return new JAXBElement(_CalendarData_QNAME, CalendarDataType.class, null, (Object)value);
    }
}

