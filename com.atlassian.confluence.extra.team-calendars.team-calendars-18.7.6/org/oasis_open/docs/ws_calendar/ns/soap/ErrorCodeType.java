/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.AfterMaxDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.BeforeMinDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.ExceedsMaxResourceSizeType;
import org.oasis_open.docs.ws_calendar.ns.soap.ForbiddenType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarCollectionLocationType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarDataType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarObjectResourceType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidFilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.MismatchedChangeTokenType;
import org.oasis_open.docs.ws_calendar.ns.soap.MissingChangeTokenType;
import org.oasis_open.docs.ws_calendar.ns.soap.NotCalendarDataType;
import org.oasis_open.docs.ws_calendar.ns.soap.PartialSuccessType;
import org.oasis_open.docs.ws_calendar.ns.soap.TargetDoesNotExistType;
import org.oasis_open.docs.ws_calendar.ns.soap.TargetExistsType;
import org.oasis_open.docs.ws_calendar.ns.soap.TargetNotEntityType;
import org.oasis_open.docs.ws_calendar.ns.soap.TooManyAttendeesPerInstanceType;
import org.oasis_open.docs.ws_calendar.ns.soap.TooManyInstancesType;
import org.oasis_open.docs.ws_calendar.ns.soap.UidConflictType;
import org.oasis_open.docs.ws_calendar.ns.soap.UnsupportedCalendarComponentType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ErrorCodeType")
@XmlSeeAlso(value={InvalidCalendarCollectionLocationType.class, InvalidCalendarDataType.class, BeforeMinDateTimeType.class, TooManyAttendeesPerInstanceType.class, TargetNotEntityType.class, TooManyInstancesType.class, TargetDoesNotExistType.class, PartialSuccessType.class, InvalidFilterType.class, MismatchedChangeTokenType.class, MissingChangeTokenType.class, AfterMaxDateTimeType.class, TargetExistsType.class, InvalidCalendarObjectResourceType.class, UnsupportedCalendarComponentType.class, ForbiddenType.class, UidConflictType.class, NotCalendarDataType.class, ExceedsMaxResourceSizeType.class})
public class ErrorCodeType {
}

