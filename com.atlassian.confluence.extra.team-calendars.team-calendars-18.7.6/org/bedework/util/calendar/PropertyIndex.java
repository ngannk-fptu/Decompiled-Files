/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar;

import ietf.params.xml.ns.icalendar_2.AcceptResponsePropType;
import ietf.params.xml.ns.icalendar_2.ActionPropType;
import ietf.params.xml.ns.icalendar_2.AttachPropType;
import ietf.params.xml.ns.icalendar_2.AttendeePropType;
import ietf.params.xml.ns.icalendar_2.BusytypePropType;
import ietf.params.xml.ns.icalendar_2.CalscalePropType;
import ietf.params.xml.ns.icalendar_2.CategoriesPropType;
import ietf.params.xml.ns.icalendar_2.ClassPropType;
import ietf.params.xml.ns.icalendar_2.CommentPropType;
import ietf.params.xml.ns.icalendar_2.CompletedPropType;
import ietf.params.xml.ns.icalendar_2.ContactPropType;
import ietf.params.xml.ns.icalendar_2.CreatedPropType;
import ietf.params.xml.ns.icalendar_2.DescriptionPropType;
import ietf.params.xml.ns.icalendar_2.DtendPropType;
import ietf.params.xml.ns.icalendar_2.DtstampPropType;
import ietf.params.xml.ns.icalendar_2.DtstartPropType;
import ietf.params.xml.ns.icalendar_2.DuePropType;
import ietf.params.xml.ns.icalendar_2.DurationPropType;
import ietf.params.xml.ns.icalendar_2.ExdatePropType;
import ietf.params.xml.ns.icalendar_2.ExrulePropType;
import ietf.params.xml.ns.icalendar_2.FreebusyPropType;
import ietf.params.xml.ns.icalendar_2.GeoPropType;
import ietf.params.xml.ns.icalendar_2.LanguageParamType;
import ietf.params.xml.ns.icalendar_2.LastModifiedPropType;
import ietf.params.xml.ns.icalendar_2.LocationPropType;
import ietf.params.xml.ns.icalendar_2.MethodPropType;
import ietf.params.xml.ns.icalendar_2.OrganizerPropType;
import ietf.params.xml.ns.icalendar_2.PercentCompletePropType;
import ietf.params.xml.ns.icalendar_2.PollItemIdPropType;
import ietf.params.xml.ns.icalendar_2.PollModePropType;
import ietf.params.xml.ns.icalendar_2.PollPropertiesPropType;
import ietf.params.xml.ns.icalendar_2.PriorityPropType;
import ietf.params.xml.ns.icalendar_2.ProdidPropType;
import ietf.params.xml.ns.icalendar_2.RdatePropType;
import ietf.params.xml.ns.icalendar_2.RecurrenceIdPropType;
import ietf.params.xml.ns.icalendar_2.RelatedToPropType;
import ietf.params.xml.ns.icalendar_2.RepeatPropType;
import ietf.params.xml.ns.icalendar_2.RequestStatusPropType;
import ietf.params.xml.ns.icalendar_2.ResourcesPropType;
import ietf.params.xml.ns.icalendar_2.RrulePropType;
import ietf.params.xml.ns.icalendar_2.SequencePropType;
import ietf.params.xml.ns.icalendar_2.StatusPropType;
import ietf.params.xml.ns.icalendar_2.SummaryPropType;
import ietf.params.xml.ns.icalendar_2.TranspPropType;
import ietf.params.xml.ns.icalendar_2.TriggerPropType;
import ietf.params.xml.ns.icalendar_2.TzidParamType;
import ietf.params.xml.ns.icalendar_2.TzidPropType;
import ietf.params.xml.ns.icalendar_2.TznamePropType;
import ietf.params.xml.ns.icalendar_2.TzoffsetfromPropType;
import ietf.params.xml.ns.icalendar_2.TzoffsettoPropType;
import ietf.params.xml.ns.icalendar_2.TzurlPropType;
import ietf.params.xml.ns.icalendar_2.UidPropType;
import ietf.params.xml.ns.icalendar_2.UrlPropType;
import ietf.params.xml.ns.icalendar_2.ValarmType;
import ietf.params.xml.ns.icalendar_2.VersionPropType;
import ietf.params.xml.ns.icalendar_2.VeventType;
import ietf.params.xml.ns.icalendar_2.VfreebusyType;
import ietf.params.xml.ns.icalendar_2.VjournalType;
import ietf.params.xml.ns.icalendar_2.VoterPropType;
import ietf.params.xml.ns.icalendar_2.VtimezoneType;
import ietf.params.xml.ns.icalendar_2.VtodoType;
import ietf.params.xml.ns.icalendar_2.XBedeworkCostPropType;
import ietf.params.xml.ns.icalendar_2.XBwCategoriesPropType;
import ietf.params.xml.ns.icalendar_2.XBwContactPropType;
import ietf.params.xml.ns.icalendar_2.XBwLocationPropType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.util.xml.tagdefs.XcalTags;

public class PropertyIndex
implements Serializable {
    static final ComponentFlags noComponent = new ComponentFlags(false, false, false, false, false, false, false, false, false);
    static final ComponentFlags eventOnly = new ComponentFlags(true, false, false, false, false, false, false, false, false);
    static final ComponentFlags todoOnly = new ComponentFlags(false, true, false, false, false, false, false, false, false);
    static final ComponentFlags freebusyOnly = new ComponentFlags(false, false, false, true, false, false, false, false, false);
    static final ComponentFlags timezoneOnly = new ComponentFlags(false, false, false, false, true, false, false, false, false);
    static final ComponentFlags alarmOnly = new ComponentFlags(false, false, false, false, false, true, false, false, false);
    static final ComponentFlags vavailabilityOnly = new ComponentFlags(false, false, false, false, false, false, true, false, false);
    static final ComponentFlags availableOnly = new ComponentFlags(false, false, false, false, false, false, false, true, false);
    static final ComponentFlags vpollOnly = new ComponentFlags(false, false, false, false, false, false, false, false, true);
    static final ComponentFlags event_Todo = new ComponentFlags(true, true, false, false, false, false, false, false, false);
    static final ComponentFlags event_Todo_Journal = new ComponentFlags(true, true, true, false, false, false, false, false, false);
    static final ComponentFlags event_Todo_Freebusy_Alarm = new ComponentFlags(true, true, false, true, false, true, false, false, false);
    static final ComponentFlags event_Todo_Freebusy = new ComponentFlags(true, true, false, true, false, false, false, false, false);
    static final ComponentFlags event_Freebusy = new ComponentFlags(true, false, false, true, false, false, false, false, false);
    static final ComponentFlags event_Todo_Journal_Freebusy = new ComponentFlags(true, true, true, true, false, false, false, false, false);
    static final ComponentFlags event_Todo_Journal_Timezone = new ComponentFlags(true, true, true, false, true, false, false, false, false);
    static final ComponentFlags event_Todo_Journal_Alarm = new ComponentFlags(true, true, true, false, false, true, false, false, false);
    static final ComponentFlags notTimezone = new ComponentFlags(true, true, true, true, false, true, false, false, false);
    static final ComponentFlags notAlarm = new ComponentFlags(true, true, true, true, true, false, false, false, true);
    static final ComponentFlags allComponents = new ComponentFlags(true, true, true, true, true, true, false, false, true);
    static final ComponentFlags vcalendarOnly = new ComponentFlags(true);
    private static final boolean IS_MULTI = true;
    private static final boolean IS_SINGLE = false;
    private static final boolean IS_PARAM = true;
    private static final boolean NOT_PARAM = false;
    private static final boolean IS_IMMUTABLE = true;
    private static final boolean NOT_IMMUTABLE = false;

    private PropertyIndex() {
    }

    public static enum PropertyInfoIndex {
        UNKNOWN_PROPERTY(null, null, false, noComponent),
        ACTION(XcalTags.action, ActionPropType.class, false, alarmOnly),
        ATTACH(XcalTags.attach, AttachPropType.class, DataType.SPECIAL, true, event_Todo_Journal_Alarm),
        ATTENDEE(XcalTags.attendee, AttendeePropType.class, DataType.CUA, true, notTimezone),
        BUSYTYPE(XcalTags.busytype, BusytypePropType.class, false, vavailabilityOnly),
        CATEGORIES(XcalTags.categories, CategoriesPropType.class, true, event_Todo_Journal_Alarm),
        X_BEDEWORK_CATEGORIES(XcalTags.xBedeworkCategories, XBwCategoriesPropType.class, true, event_Todo_Journal_Alarm),
        CLASS(XcalTags._class, ClassPropType.class, null, false, event_Todo_Journal),
        COMMENT(XcalTags.comment, CommentPropType.class, true, notAlarm),
        COMPLETED(XcalTags.completed, CompletedPropType.class, DataType.DATE_TIME, false, todoOnly),
        CONTACT(XcalTags.contact, ContactPropType.class, true, event_Todo_Journal_Freebusy),
        X_BEDEWORK_CONTACT(XcalTags.xBedeworkContact, XBwContactPropType.class, true, event_Todo_Journal_Freebusy),
        CREATED(XcalTags.created, CreatedPropType.class, DataType.DATE_TIME, false, event_Todo_Journal_Freebusy),
        DESCRIPTION(XcalTags.description, DescriptionPropType.class, false, true, event_Todo_Journal_Alarm),
        DTEND(XcalTags.dtend, DtendPropType.class, DataType.DATE_TIME, false, event_Freebusy),
        DTSTAMP(XcalTags.dtstamp, DtstampPropType.class, DataType.DATE_TIME, false, event_Todo_Journal_Freebusy, false, false),
        DTSTART(XcalTags.dtstart, DtstartPropType.class, DataType.DATE_TIME, false, notAlarm),
        DUE(XcalTags.due, DuePropType.class, DataType.DATE_TIME, false, todoOnly),
        DURATION(XcalTags.duration, DurationPropType.class, DataType.DURATION, false, event_Todo_Freebusy_Alarm),
        EXDATE(XcalTags.exdate, ExdatePropType.class, DataType.DATE_TIME, true, event_Todo_Journal_Timezone),
        EXRULE(XcalTags.exrule, ExrulePropType.class, DataType.RECUR, true, event_Todo_Journal_Timezone),
        FREEBUSY(XcalTags.freebusy, FreebusyPropType.class, DataType.PERIOD, false, freebusyOnly),
        GEO(XcalTags.geo, GeoPropType.class, false, event_Todo),
        LAST_MODIFIED(XcalTags.lastModified, LastModifiedPropType.class, DataType.DATE_TIME, false, event_Todo_Journal_Timezone, false, false),
        LOCATION(XcalTags.location, LocationPropType.class, false, event_Todo),
        X_BEDEWORK_LOCATION(XcalTags.xBedeworkLocation, XBwLocationPropType.class, false, event_Todo),
        ORGANIZER(XcalTags.organizer, OrganizerPropType.class, DataType.CUA, false, event_Todo_Journal_Freebusy),
        PERCENT_COMPLETE(XcalTags.percentComplete, PercentCompletePropType.class, false, todoOnly),
        PRIORITY(XcalTags.priority, PriorityPropType.class, DataType.INTEGER, false, event_Todo),
        PUBLISH_URL(XcalTags.url, UrlPropType.class, DataType.URI, false, event_Todo_Journal_Freebusy),
        RDATE(XcalTags.rdate, RdatePropType.class, DataType.DATE_TIME, true, event_Todo_Journal_Timezone),
        RECURRENCE_ID(XcalTags.recurrenceId, RecurrenceIdPropType.class, DataType.DATE_TIME, false, event_Todo_Journal_Freebusy),
        RELATED_TO(XcalTags.relatedTo, RelatedToPropType.class, true, event_Todo_Journal),
        REPEAT(XcalTags.repeat, RepeatPropType.class, DataType.INTEGER, false, alarmOnly),
        REQUEST_STATUS(XcalTags.requestStatus, RequestStatusPropType.class, true, event_Todo_Journal_Freebusy),
        RESOURCES(XcalTags.resources, ResourcesPropType.class, true, event_Todo),
        RRULE(XcalTags.rrule, RrulePropType.class, DataType.RECUR, true, event_Todo_Journal_Timezone),
        SEQUENCE(XcalTags.sequence, SequencePropType.class, DataType.INTEGER, false, event_Todo_Journal, false, false),
        STATUS(XcalTags.status, StatusPropType.class, false, event_Todo_Journal),
        SUMMARY(XcalTags.summary, SummaryPropType.class, false, true, event_Todo_Journal_Alarm),
        TRIGGER(XcalTags.trigger, TriggerPropType.class, DataType.DURATION, false, alarmOnly),
        TRANSP(XcalTags.transp, TranspPropType.class, false, eventOnly),
        TZID(XcalTags.tzid, TzidPropType.class, false, timezoneOnly),
        TZNAME(XcalTags.tzname, TznamePropType.class, false, timezoneOnly),
        TZOFFSETFROM(XcalTags.tzoffsetfrom, TzoffsetfromPropType.class, DataType.UTC_OFFSET, false, timezoneOnly),
        TZOFFSETTO(XcalTags.tzoffsetto, TzoffsettoPropType.class, DataType.UTC_OFFSET, false, timezoneOnly),
        TZURL(XcalTags.tzurl, TzurlPropType.class, DataType.URI, false, timezoneOnly),
        UID(XcalTags.uid, UidPropType.class, false, event_Todo_Journal_Freebusy),
        URL(XcalTags.url, UrlPropType.class, DataType.URI, false, event_Todo_Journal_Freebusy),
        XPROP(BedeworkServerTags.xprop, null, true, allComponents),
        ACCEPT_RESPONSE(XcalTags.acceptResponse, AcceptResponsePropType.class, false, vpollOnly),
        POLL_WINNER(BedeworkServerTags.xprop, null, DataType.INTEGER, false, vpollOnly),
        POLL_ITEM_ID(XcalTags.pollItemId, PollItemIdPropType.class, DataType.INTEGER, false, event_Todo_Journal_Freebusy),
        POLL_ITEM(BedeworkServerTags.xprop, null, true, vpollOnly),
        VVOTER(BedeworkServerTags.xprop, null, true, vpollOnly),
        VOTE(BedeworkServerTags.xprop, null, true, vpollOnly),
        POLL_MODE(XcalTags.pollMode, PollModePropType.class, false, vpollOnly),
        POLL_PROPERTIES(XcalTags.pollProperties, PollPropertiesPropType.class, true, vpollOnly),
        VOTER(XcalTags.voter, VoterPropType.class, DataType.CUA, true, notTimezone),
        COLLECTION(BedeworkServerTags.collection, null, false, event_Todo_Journal),
        COST(BedeworkServerTags.cost, null, false, event_Todo),
        CREATOR(BedeworkServerTags.creator, null, DataType.HREF, false, event_Todo_Journal, false, true),
        DELETED(BedeworkServerTags.deleted, null, false, event_Todo),
        END_TYPE(BedeworkServerTags.endType, null, false, event_Todo_Journal),
        ETAG(BedeworkServerTags.etag, null, DataType.TEXT, false, noComponent, false, true),
        LASTMODSEQ(BedeworkServerTags.xprop, null, DataType.TEXT, false, noComponent, false, true),
        ENTITY_TYPE(BedeworkServerTags.entityType, null, DataType.INTEGER, false, event_Todo_Journal, false, true),
        HREF(WebdavTags.href, null, DataType.HREF, false, allComponents, false, true),
        OWNER(BedeworkServerTags.owner, null, DataType.HREF, false, event_Todo_Journal, false, true),
        TOMBSTONED(BedeworkServerTags.tombstoned, null, false, notAlarm),
        VALARM(XcalTags.valarm, ValarmType.class, true, notAlarm),
        LANG(BedeworkServerTags.language, LanguageParamType.class, DataType.TEXT, false, noComponent, true, false),
        RANGE(XcalTags.range, null, DataType.DURATION, false, noComponent, true, false),
        TZIDPAR(XcalTags.tzid, TzidParamType.class, DataType.TEXT, false, noComponent, true, false),
        XBEDEWORK_COST(XcalTags.xBedeworkCost, XBedeworkCostPropType.class, false, event_Todo),
        CALSCALE(XcalTags.calscale, CalscalePropType.class, false, vcalendarOnly),
        METHOD(XcalTags.method, MethodPropType.class, false, vcalendarOnly),
        PRODID(XcalTags.prodid, ProdidPropType.class, false, vcalendarOnly),
        VERSION(XcalTags.version, VersionPropType.class, false, vcalendarOnly),
        ACL(BedeworkServerTags.xprop, null, true, allComponents),
        AFFECTS_FREE_BUSY(BedeworkServerTags.xprop, null, false, allComponents),
        ALIAS_URI(BedeworkServerTags.xprop, null, false, allComponents),
        ATTENDEE_SCHEDULING_OBJECT(BedeworkServerTags.xprop, null, false, allComponents),
        CALSUITE(BedeworkServerTags.xprop, null, false, allComponents),
        CALTYPE(BedeworkServerTags.xprop, null, false, allComponents),
        COL_PROPERTIES(BedeworkServerTags.xprop, null, false, allComponents),
        COLPATH(BedeworkServerTags.xprop, null, true, allComponents),
        CTAG(BedeworkServerTags.ctag, null, DataType.TEXT, false, noComponent, false, true),
        CTOKEN(BedeworkServerTags.xprop, null, DataType.TEXT, false, noComponent, false, true),
        DBID(BedeworkServerTags.xprop, null, false, allComponents),
        DISPLAY(BedeworkServerTags.xprop, null, false, allComponents),
        DOCTYPE(BedeworkServerTags.xprop, null, false, allComponents),
        EVENTREG_END(BedeworkServerTags.xprop, null, false, allComponents),
        EVENTREG_MAX_TICKETS(BedeworkServerTags.xprop, null, false, allComponents),
        EVENTREG_MAX_TICKETS_PER_USER(BedeworkServerTags.xprop, null, false, allComponents),
        EVENTREG_START(BedeworkServerTags.xprop, null, false, allComponents),
        EVENTREG_WAIT_LIST_LIMIT(BedeworkServerTags.xprop, null, false, allComponents),
        FILTER_EXPR(BedeworkServerTags.xprop, null, false, allComponents),
        LOCAL(XcalTags.dtstart, null, DataType.DATE_TIME, false, notAlarm),
        FLOATING(XcalTags.dtstart, null, DataType.DATE_TIME, false, notAlarm),
        IGNORE_TRANSP(BedeworkServerTags.xprop, null, false, allComponents),
        IMAGE(BedeworkServerTags.xprop, null, false, allComponents),
        INDEX_END(XcalTags.dtstart, null, DataType.DATE_TIME, false, allComponents),
        INDEX_START(XcalTags.dtstart, null, DataType.DATE_TIME, false, allComponents),
        INSTANCE(BedeworkServerTags.xprop, null, false, allComponents),
        LAST_ETAG(BedeworkServerTags.xprop, null, false, allComponents),
        LAST_REFRESH(BedeworkServerTags.xprop, null, false, allComponents),
        LAST_REFRESH_STATUS(BedeworkServerTags.xprop, null, false, allComponents),
        LOCATION_HREF(BedeworkServerTags.xprop, null, false, event_Todo),
        LOCATION_STR(BedeworkServerTags.xprop, null, false, event_Todo),
        MASTER(BedeworkServerTags.xprop, null, false, allComponents),
        NAME(BedeworkServerTags.xprop, null, false, allComponents),
        ORGANIZER_SCHEDULING_OBJECT(BedeworkServerTags.xprop, null, false, allComponents),
        ORIGINATOR(BedeworkServerTags.xprop, null, false, allComponents),
        OVERRIDE(BedeworkServerTags.xprop, null, false, allComponents),
        PUBLIC(BedeworkServerTags.xprop, null, false, allComponents),
        RECIPIENT(BedeworkServerTags.xprop, null, false, allComponents),
        RECURRING(BedeworkServerTags.xprop, null, false, allComponents),
        REFRESH_RATE(BedeworkServerTags.xprop, null, false, allComponents),
        REMOTE_ID(BedeworkServerTags.xprop, null, false, allComponents),
        REMOTE_PW(BedeworkServerTags.xprop, null, false, allComponents),
        SCHEDULE_METHOD(BedeworkServerTags.xprop, null, false, allComponents),
        SCHEDULE_STATE(BedeworkServerTags.xprop, null, false, allComponents),
        SCHEDULE_TAG(BedeworkServerTags.xprop, null, false, allComponents),
        SUGGESTED_TO(BedeworkServerTags.xprop, null, true, allComponents),
        TARGET(BedeworkServerTags.xprop, null, false, allComponents),
        THUMBIMAGE(BedeworkServerTags.xprop, null, false, allComponents),
        TOPICAL_AREA(BedeworkServerTags.xprop, null, false, allComponents),
        NEXT_TRIGGER_DATE_TIME(BedeworkServerTags.xprop, null, false, allComponents),
        TRIGGER_DATE_TIME(BedeworkServerTags.xprop, null, false, allComponents),
        UNREMOVEABLE(BedeworkServerTags.xprop, null, false, allComponents),
        UTC(XcalTags.dtstart, null, DataType.DATE_TIME, false, notAlarm),
        VPATH(BedeworkServerTags.xprop, null, false, allComponents),
        VIEW(BedeworkServerTags.xprop, null, false, allComponents),
        PARAMETERS(BedeworkServerTags.xprop, null, false, allComponents),
        NO_START(BedeworkServerTags.xprop, null, false, allComponents),
        RELEVANCE(BedeworkServerTags.xprop, null, false, allComponents),
        TAG(BedeworkServerTags.xprop, null, true, allComponents),
        URI(BedeworkServerTags.xprop, null, DataType.URI, false, allComponents),
        VALUE(BedeworkServerTags.xprop, null, false, allComponents),
        CN(BedeworkServerTags.xprop, null, false, allComponents),
        EMAIL(BedeworkServerTags.xprop, null, false, allComponents),
        PHONE(BedeworkServerTags.xprop, null, false, allComponents),
        CONTACT_ALL(BedeworkServerTags.xprop, null, false, allComponents),
        ADDRESS(BedeworkServerTags.xprop, null, false, allComponents),
        SUBADDRESS(BedeworkServerTags.xprop, null, false, allComponents),
        ADDRESS_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        ROOM_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        SUB1_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        SUB2_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        ACCESSIBLE_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        GEOURI_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        STREET_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        CITY_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        STATE_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        ZIP_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        ALTADDRESS_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        CODEIDX_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        LOC_KEYS_FLD(BedeworkServerTags.xprop, null, false, allComponents),
        LOC_ALL(BedeworkServerTags.xprop, null, false, allComponents);

        private final QName qname;
        private final Class xmlClass;
        private DataType ptype;
        private final boolean multiValued;
        private boolean dbMultiValued;
        private boolean param;
        private boolean immutable;
        private final ComponentFlags components;
        private static final Map<QName, PropertyInfoIndex> qnameLookup;
        private static final Map<Class, PropertyInfoIndex> xmlClassLookup;

        private PropertyInfoIndex(QName qname, Class xmlClass, boolean multiValued, ComponentFlags components) {
            this.qname = qname;
            this.xmlClass = xmlClass;
            this.components = components;
            this.multiValued = multiValued;
            this.dbMultiValued = multiValued;
        }

        private PropertyInfoIndex(QName qname, Class xmlClass, DataType ptype, boolean multiValued, ComponentFlags components) {
            this(qname, xmlClass, multiValued, components);
            this.ptype = ptype;
        }

        private PropertyInfoIndex(QName qname, Class xmlClass, boolean multiValued, boolean dbMultiValued, ComponentFlags components) {
            this(qname, xmlClass, DataType.TEXT, multiValued, components, false, false);
            this.dbMultiValued = dbMultiValued;
        }

        private PropertyInfoIndex(QName qname, Class xmlClass, DataType ptype, boolean multiValued, ComponentFlags components, boolean param, boolean immutable) {
            this(qname, xmlClass, multiValued, components);
            this.ptype = ptype;
            this.param = param;
            this.immutable = immutable;
        }

        public static PropertyInfoIndex fromName(String pname) {
            String name = !pname.contains("-") ? pname.toUpperCase() : pname.replace("-", "_").toUpperCase();
            try {
                return PropertyInfoIndex.valueOf(name);
            }
            catch (Throwable ignored) {
                return null;
            }
        }

        public QName getQname() {
            return this.qname;
        }

        public Class getXmlClass() {
            return this.xmlClass;
        }

        public DataType getPtype() {
            return this.ptype;
        }

        public boolean getMultiValued() {
            return this.multiValued;
        }

        public boolean getDbMultiValued() {
            return this.dbMultiValued;
        }

        public boolean getParam() {
            return this.param;
        }

        public boolean getImmutable() {
            return this.immutable;
        }

        public boolean getVcalendarProperty() {
            return this.components.vcalendarProperty;
        }

        public boolean getEventProperty() {
            return this.components.eventProperty;
        }

        public boolean getTodoProperty() {
            return this.components.todoProperty;
        }

        public boolean getJournalProperty() {
            return this.components.journalProperty;
        }

        public boolean getFreeBusyProperty() {
            return this.components.freeBusyProperty;
        }

        public boolean getTimezoneProperty() {
            return this.components.timezoneProperty;
        }

        public boolean getAlarmProperty() {
            return this.components.alarmProperty;
        }

        public boolean getVavailabilityProperty() {
            return this.components.vavailabilityProperty;
        }

        public boolean getAvailableProperty() {
            return this.components.availableProperty;
        }

        public boolean getVpollProperty() {
            return this.components.vpollProperty;
        }

        public static PropertyInfoIndex fromXmlClass(Class cl) {
            return xmlClassLookup.get(cl);
        }

        public static PropertyInfoIndex lookupQname(QName val) {
            return qnameLookup.get(val);
        }

        static {
            qnameLookup = new HashMap<QName, PropertyInfoIndex>();
            xmlClassLookup = new HashMap<Class, PropertyInfoIndex>();
            for (PropertyInfoIndex pii : PropertyInfoIndex.values()) {
                qnameLookup.put(pii.getQname(), pii);
                xmlClassLookup.put(pii.xmlClass, pii);
            }
        }
    }

    public static enum ParameterInfoIndex {
        UNKNOWN_PARAMETER(null),
        ABBREV("ABBREV"),
        ALTREP("ALTREP"),
        CN("CN"),
        CUTYPE("CUTYPE"),
        DELEGATED_FROM("DELEGATED-FROM", "delegatedFrom"),
        DELEGATED_TO("DELEGATED-TO", "delegatedTo"),
        DIR("DIR"),
        ENCODING("ENCODING"),
        FMTTYPE("FMTTYPE"),
        FBTYPE("FBTYPE"),
        LANGUAGE("LANGUAGE"),
        MEMBER("MEMBER"),
        PARTSTAT("PARTSTAT"),
        RANGE("RANGE"),
        RELATED("RELATED"),
        RELTYPE("RELTYPE"),
        ROLE("ROLE"),
        RSVP("RSVP"),
        SCHEDULE_AGENT("SCHEDULE-AGENT", "scheduleAgent"),
        SCHEDULE_STATUS("SCHEDULE-STATUS", "scheduleStatus"),
        SENT_BY("SENT-BY", "sentBy"),
        STAY_INFORMED("STAY-INFORMED", "stayInformed"),
        TYPE("TYPE"),
        TZID("TZID"),
        VALUE("VALUE"),
        UID("UID");

        private final String pname;
        private String pnameLC;
        private String jname;
        private final DataType ptype;
        private static final HashMap<String, ParameterInfoIndex> pnameLookup;

        private ParameterInfoIndex(String pname) {
            this(pname, null, DataType.TEXT);
        }

        private ParameterInfoIndex(String pname, String jname) {
            this(pname, jname, DataType.TEXT);
        }

        private ParameterInfoIndex(String pname, String jname, DataType ptype) {
            this.pname = pname;
            this.jname = jname;
            this.ptype = ptype;
            if (pname != null) {
                this.pnameLC = pname.toLowerCase();
            }
            if (jname == null) {
                this.jname = this.pnameLC;
            }
        }

        public String getPname() {
            return this.pname;
        }

        public String getJname() {
            return this.jname;
        }

        public String getPnameLC() {
            return this.pnameLC;
        }

        public DataType getPtype() {
            return this.ptype;
        }

        public static ParameterInfoIndex lookupPname(String val) {
            return pnameLookup.get(val.toLowerCase());
        }

        static {
            pnameLookup = new HashMap();
            for (ParameterInfoIndex pii : ParameterInfoIndex.values()) {
                String pname = pii.getPnameLC();
                pnameLookup.put(pname, pii);
            }
        }
    }

    public static enum DataType {
        BINARY(XcalTags.binaryVal, "binary"),
        BOOLEAN(XcalTags.booleanVal, "boolean"),
        CUA(XcalTags.calAddressVal, "cal-address"),
        DATE(XcalTags.dateVal, "date"),
        DATE_TIME(XcalTags.dateTimeVal, "date-time"),
        DURATION(XcalTags.duration, "duration"),
        FLOAT(XcalTags.floatVal, "float"),
        INTEGER(XcalTags.integerVal, "integer"),
        PERIOD(XcalTags.periodVal, "period"),
        RECUR(XcalTags.recurVal, "recur"),
        TEXT(XcalTags.textVal, "text"),
        TIME(XcalTags.timeVal, "time"),
        URI(XcalTags.uriVal, "uri"),
        UTC_OFFSET(XcalTags.utcOffsetVal, "utc-offset"),
        SPECIAL(null, null),
        HREF(null, null);

        private final QName xcalType;
        private final String jsonType;

        private DataType(QName xcalType, String jsonType) {
            this.xcalType = xcalType;
            this.jsonType = jsonType;
        }

        public QName getXcalType() {
            return this.xcalType;
        }

        public String getJsonType() {
            return this.jsonType;
        }
    }

    public static enum ComponentInfoIndex {
        UNKNOWN_COMPONENT(null, null, null),
        VALARM(XcalTags.valarm, "VALARM", ValarmType.class),
        VEVENT(XcalTags.vevent, "VEVENT", VeventType.class),
        VFREEBUSY(XcalTags.vfreebusy, "VFREEBUSY", VfreebusyType.class),
        VJOURNAL(XcalTags.vjournal, "VJOURNAL", VjournalType.class),
        VTIMEZONE(XcalTags.vtimezone, "VTIMEZONE", VtimezoneType.class),
        VTODO(XcalTags.vtodo, "VTODO", VtodoType.class);

        private final QName qname;
        private final String pname;
        private String pnameLC;
        private String jname;
        private final Class xmlClass;
        private static final Map<String, ComponentInfoIndex> pnameLookup;
        private static final Map<QName, ComponentInfoIndex> qnameLookup;
        private static final Map<Class, ComponentInfoIndex> xmlClassLookup;

        private ComponentInfoIndex(QName qname, String pname, Class xmlClass) {
            this.qname = qname;
            this.pname = pname;
            this.xmlClass = xmlClass;
            if (pname != null) {
                this.pnameLC = pname.toLowerCase();
            }
            if (this.jname == null) {
                this.jname = this.pnameLC;
            }
        }

        public QName getQname() {
            return this.qname;
        }

        public String getPname() {
            return this.pname;
        }

        public String getJname() {
            return this.jname;
        }

        public String getPnameLC() {
            return this.pnameLC;
        }

        public Class getXmlClass() {
            return this.xmlClass;
        }

        public static ComponentInfoIndex fromXmlClass(Class cl) {
            return xmlClassLookup.get(cl);
        }

        public static ComponentInfoIndex lookupPname(String val) {
            return pnameLookup.get(val.toLowerCase());
        }

        public static ComponentInfoIndex lookupQname(QName val) {
            return qnameLookup.get(val);
        }

        static {
            pnameLookup = new HashMap<String, ComponentInfoIndex>();
            qnameLookup = new HashMap<QName, ComponentInfoIndex>();
            xmlClassLookup = new HashMap<Class, ComponentInfoIndex>();
            for (ComponentInfoIndex cii : ComponentInfoIndex.values()) {
                String pname = cii.getPnameLC();
                pnameLookup.put(pname, cii);
                qnameLookup.put(cii.getQname(), cii);
                xmlClassLookup.put(cii.xmlClass, cii);
            }
        }
    }

    static class ComponentFlags {
        private boolean vcalendarProperty;
        private boolean eventProperty;
        private boolean todoProperty;
        private boolean journalProperty;
        private boolean freeBusyProperty;
        private boolean timezoneProperty;
        private boolean alarmProperty;
        private boolean vavailabilityProperty;
        private boolean availableProperty;
        private boolean vpollProperty;

        ComponentFlags(boolean eventProperty, boolean todoProperty, boolean journalProperty, boolean freeBusyProperty, boolean timezoneProperty, boolean alarmProperty, boolean vavailabilityProperty, boolean availableProperty, boolean vpollProperty) {
            this.eventProperty = eventProperty;
            this.todoProperty = todoProperty;
            this.journalProperty = journalProperty;
            this.freeBusyProperty = freeBusyProperty;
            this.timezoneProperty = timezoneProperty;
            this.alarmProperty = alarmProperty;
            this.vavailabilityProperty = vavailabilityProperty;
            this.availableProperty = availableProperty;
            this.vpollProperty = vpollProperty;
        }

        ComponentFlags(boolean vcalendarProperty) {
            this.vcalendarProperty = vcalendarProperty;
        }
    }
}

