/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.caldav.server;

import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.ObjectFactory;
import ietf.params.xml.ns.icalendar_2.VavailabilityType;
import ietf.params.xml.ns.icalendar_2.VeventType;
import ietf.params.xml.ns.icalendar_2.VtodoType;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Acl;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CaldavBwNode;
import org.bedework.caldav.server.CaldavURI;
import org.bedework.caldav.server.sysinterface.CalPrincipalInfo;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.caldav.util.sharing.InviteType;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.misc.Util;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.AppleIcalTags;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.CalWSSoapTags;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarCollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ChildCollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.CollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.InboxType;
import org.oasis_open.docs.ws_calendar.ns.soap.IntegerPropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.LastModifiedDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxAttendeesPerInstanceType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxInstancesType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxResourceSizeType;
import org.oasis_open.docs.ws_calendar.ns.soap.OutboxType;
import org.oasis_open.docs.ws_calendar.ns.soap.PrincipalHomeType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceDescriptionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceTimezoneIdType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceTypeType;
import org.oasis_open.docs.ws_calendar.ns.soap.StringPropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.SupportedCalendarComponentSetType;
import org.w3c.dom.Element;

public class CaldavCalNode
extends CaldavBwNode {
    private CalDAVEvent ical;
    private AccessPrincipal owner;
    private Acl.CurrentAccess currentAccess;
    private static final HashMap<QName, WebdavNsNode.PropertyTagEntry> propertyNames = new HashMap();
    private static final HashMap<String, CaldavBwNode.PropertyTagXrdEntry> xrdNames = new HashMap();
    private static final HashMap<QName, WebdavNsNode.PropertyTagEntry> calWSSoapNames = new HashMap();

    public CaldavCalNode(SysIntf sysi, int status, String uri) {
        super(true, sysi, uri);
        this.setStatus(status);
    }

    public CaldavCalNode(CaldavURI cdURI, SysIntf sysi) throws WebdavException {
        super(cdURI, sysi);
        this.col = cdURI.getCol();
        this.collection = true;
        this.allowsGet = false;
        this.exists = cdURI.getExists();
    }

    @Override
    public AccessPrincipal getOwner() throws WebdavException {
        if (this.owner == null) {
            if (this.col == null) {
                return null;
            }
            this.owner = this.col.getOwner();
        }
        return this.owner;
    }

    @Override
    public void init(boolean content) throws WebdavException {
        if (!content) {
            return;
        }
    }

    @Override
    public String getEtagValue(boolean strong) throws WebdavException {
        CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
        if (c == null) {
            return null;
        }
        String val = c.getEtag();
        if (strong) {
            return val;
        }
        return "W/" + val;
    }

    @Override
    public String getEtokenValue() throws WebdavException {
        return this.concatEtoken(this.getEtagValue(true), "");
    }

    public boolean getSchedulingAllowed() throws WebdavException {
        CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
        if (c == null) {
            return false;
        }
        int type = c.getCalType();
        if (type == 2) {
            return true;
        }
        if (type == 3) {
            return true;
        }
        return type == 1;
    }

    public String getSharingStatus() throws WebdavException {
        return this.getCollection(false).getProperty(AppleServerTags.invite);
    }

    @Override
    public void setDefaults(QName methodTag) throws WebdavException {
        if (!CaldavTags.mkcalendar.equals(methodTag)) {
            return;
        }
        CalDAVCollection c = (CalDAVCollection)this.getCollection(false);
        c.setCalType(1);
    }

    @Override
    public Collection<? extends WdEntity> getChildren(Supplier<Object> filterGetter) throws WebdavException {
        try {
            CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
            if (c == null) {
                return null;
            }
            if (!c.entitiesAllowed()) {
                if (this.debug) {
                    this.debugMsg("POSSIBLE SEARCH: getChildren for cal " + c.getPath());
                }
                ArrayList<WdEntity> ch = new ArrayList<WdEntity>();
                ch.addAll(this.getSysi().getCollections(c));
                ch.addAll(this.getSysi().getFiles(c));
                return ch;
            }
            c = (CalDAVCollection)this.getCollection(false);
            if (this.debug) {
                this.debugMsg("Get all resources in calendar " + c.getPath());
            }
            FilterBase filter = filterGetter == null ? null : (FilterBase)filterGetter.get();
            return this.getSysi().getEvents(c, filter, null, null);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void setFreeBusy(CalDAVEvent fbcal) throws WebdavException {
        try {
            this.ical = fbcal;
            this.allowsGet = true;
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new WebdavException(t);
        }
    }

    @Override
    public String writeContent(XmlEmit xml, Writer wtr, String contentType) throws WebdavException {
        try {
            ArrayList<CalDAVEvent> evs = new ArrayList<CalDAVEvent>();
            evs.add(this.ical);
            return this.getSysi().writeCalendar(evs, SysIntf.MethodEmitted.noMethod, xml, wtr, contentType);
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public String getContentString(String contentType) throws WebdavException {
        this.init(true);
        if (this.ical == null) {
            return null;
        }
        return this.ical.toString();
    }

    @Override
    public void update() throws WebdavException {
        if (this.col != null) {
            this.getSysi().updateCollection(this.col);
        }
    }

    @Override
    public String getContentLang() throws WebdavException {
        return "en";
    }

    @Override
    public long getContentLen() throws WebdavException {
        String s = this.getContentString(this.getContentType());
        if (s == null) {
            return 0L;
        }
        return s.getBytes().length;
    }

    @Override
    public String getContentType() throws WebdavException {
        if (this.ical != null) {
            return "text/calendar;charset=utf-8";
        }
        return null;
    }

    @Override
    public String getCreDate() throws WebdavException {
        return null;
    }

    @Override
    public String getDisplayname() throws WebdavException {
        if (this.col == null) {
            return null;
        }
        return this.col.getDisplayName();
    }

    @Override
    public String getLastmodDate() throws WebdavException {
        this.init(false);
        if (this.col == null) {
            return null;
        }
        try {
            return DateTimeUtil.fromISODateTimeUTCtoRfc822(this.col.getLastmod());
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public boolean allowsSyncReport() throws WebdavException {
        return this.getSysi().allowsSyncReport(this.col);
    }

    @Override
    public boolean getDeleted() throws WebdavException {
        return this.col.getDeleted() | ((CalDAVCollection)this.getCollection(true)).getDeleted();
    }

    @Override
    public String getSyncToken() throws WebdavException {
        return this.getSysi().getSyncToken(this.col);
    }

    @Override
    public Acl.CurrentAccess getCurrentAccess() throws WebdavException {
        if (this.currentAccess != null) {
            return this.currentAccess;
        }
        CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
        if (c == null) {
            return null;
        }
        try {
            this.currentAccess = this.getSysi().checkAccess(c, 25, true);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        return this.currentAccess;
    }

    @Override
    public boolean trailSlash() {
        return true;
    }

    @Override
    public boolean removeProperty(Element val, WebdavNsNode.SetPropertyResult spr) throws WebdavException {
        if (super.removeProperty(val, spr)) {
            return true;
        }
        try {
            if (XmlUtil.nodeMatches(val, WebdavTags.description)) {
                if (this.checkCalForSetProp(spr)) {
                    this.col.setDescription(null);
                }
                return true;
            }
            if (XmlUtil.nodeMatches(val, CaldavTags.calendarTimezone)) {
                this.col.setTimezone(null);
                return true;
            }
            if (XmlUtil.nodeMatches(val, CaldavTags.defaultAlarmVeventDate) || XmlUtil.nodeMatches(val, CaldavTags.defaultAlarmVeventDatetime) || XmlUtil.nodeMatches(val, CaldavTags.defaultAlarmVtodoDate) || XmlUtil.nodeMatches(val, CaldavTags.defaultAlarmVtodoDatetime)) {
                this.col.setProperty(new QName(val.getNamespaceURI(), val.getLocalName()), null);
                return true;
            }
            return false;
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public boolean setProperty(Element val, WebdavNsNode.SetPropertyResult spr) throws WebdavException {
        if (super.setProperty(val, spr)) {
            return true;
        }
        try {
            if (XmlUtil.nodeMatches(val, WebdavTags.description)) {
                if (this.checkCalForSetProp(spr)) {
                    this.col.setDescription(XmlUtil.getElementContent(val));
                }
                return true;
            }
            if (XmlUtil.nodeMatches(val, CaldavTags.calendarDescription)) {
                if (this.checkCalForSetProp(spr)) {
                    this.col.setDescription(XmlUtil.getElementContent(val));
                }
                return true;
            }
            if (XmlUtil.nodeMatches(val, AppleIcalTags.calendarOrder)) {
                if (this.checkCalForSetProp(spr)) {
                    this.col.setProperty(AppleIcalTags.calendarOrder, XmlUtil.getElementContent(val));
                }
                return true;
            }
            if (XmlUtil.nodeMatches(val, WebdavTags.displayname)) {
                if (this.checkCalForSetProp(spr)) {
                    this.col.setDisplayName(XmlUtil.getElementContent(val));
                }
                return true;
            }
            if (XmlUtil.nodeMatches(val, WebdavTags.resourcetype)) {
                List<Element> propVals = XmlUtil.getElements(val);
                for (Element pval : propVals) {
                    if (XmlUtil.nodeMatches(pval, WebdavTags.collection)) continue;
                    if (XmlUtil.nodeMatches(pval, CaldavTags.calendar)) {
                        CalDAVCollection c = (CalDAVCollection)this.getCollection(false);
                        if (WebdavTags.mkcol.equals(spr.rootElement) || CaldavTags.mkcalendar.equals(spr.rootElement)) {
                            c.setCalType(1);
                            continue;
                        }
                        if (c.getCalType() == 1) continue;
                        throw new WebdavForbidden();
                    }
                    if (!XmlUtil.nodeMatches(pval, AppleServerTags.sharedOwner)) continue;
                    return false;
                }
                return true;
            }
            if (XmlUtil.nodeMatches(val, CaldavTags.supportedCalendarComponentSet)) {
                if (!WebdavTags.mkcol.equals(spr.rootElement) && !CaldavTags.mkcalendar.equals(spr.rootElement)) {
                    throw new WebdavForbidden();
                }
                List<Element> propVals = XmlUtil.getElements(val);
                ArrayList<String> comps = new ArrayList<String>();
                for (Element pval : XmlUtil.getElements(val)) {
                    if (!XmlUtil.nodeMatches(pval, CaldavTags.comp)) {
                        throw new WebdavBadRequest("Only comp allowed");
                    }
                    comps.add(pval.getAttribute("name"));
                }
                this.col.setSupportedComponents(comps);
                return true;
            }
            if (XmlUtil.nodeMatches(val, CaldavTags.scheduleCalendarTransp)) {
                Element cval = XmlUtil.getOnlyElement(val);
                if (XmlUtil.nodeMatches(cval, CaldavTags.opaque)) {
                    this.col.setAffectsFreeBusy(true);
                } else if (XmlUtil.nodeMatches(cval, CaldavTags.transparent)) {
                    this.col.setAffectsFreeBusy(true);
                } else {
                    throw new WebdavBadRequest();
                }
                return true;
            }
            if (XmlUtil.nodeMatches(val, CaldavTags.calendarFreeBusySet)) {
                if (this.col.getCalType() != 2) {
                    throw new WebdavForbidden("Not on inbox");
                }
                spr.status = 501;
                spr.message = "Unimplemented - calendarFreeBusySet";
                this.warn("Unimplemented - calendarFreeBusySet");
                return true;
            }
            if (XmlUtil.nodeMatches(val, CaldavTags.calendarTimezone)) {
                try {
                    this.col.setTimezone(this.getSysi().tzidFromTzdef(XmlUtil.getElementContent(val)));
                }
                catch (Throwable t) {
                    spr.status = 400;
                    spr.message = t.getLocalizedMessage();
                }
                return true;
            }
            if (XmlUtil.nodeMatches(val, AppleIcalTags.calendarColor)) {
                this.col.setColor(XmlUtil.getElementContent(val));
                return true;
            }
            if (XmlUtil.nodeMatches(val, CaldavTags.defaultAlarmVeventDate) || XmlUtil.nodeMatches(val, CaldavTags.defaultAlarmVeventDatetime) || XmlUtil.nodeMatches(val, CaldavTags.defaultAlarmVtodoDate) || XmlUtil.nodeMatches(val, CaldavTags.defaultAlarmVtodoDatetime)) {
                String al = XmlUtil.getElementContent(val, false);
                if (al == null) {
                    return false;
                }
                if (al.length() > 0 && !this.getSysi().validateAlarm(al)) {
                    return false;
                }
                this.col.setProperty(new QName(val.getNamespaceURI(), val.getLocalName()), al);
                return true;
            }
            if (XmlUtil.nodeMatches(val, BedeworkServerTags.aliasUri)) {
                this.col.setAliasUri(XmlUtil.getElementContent(val));
                return true;
            }
            if (XmlUtil.nodeMatches(val, BedeworkServerTags.remoteId)) {
                this.col.setRemoteId(XmlUtil.getElementContent(val));
                return true;
            }
            if (XmlUtil.nodeMatches(val, BedeworkServerTags.remotePw)) {
                this.col.setRemotePw(XmlUtil.getElementContent(val));
                return true;
            }
            return false;
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public boolean knownProperty(QName tag) {
        if (propertyNames.get(tag) != null) {
            return true;
        }
        return super.knownProperty(tag);
    }

    @Override
    public boolean generatePropertyValue(QName tag, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        XmlEmit xml = intf.getXmlEmit();
        try {
            int calType;
            CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
            CalDAVCollection cundereffed = (CalDAVCollection)this.getCollection(false);
            if (c == null) {
                calType = 0;
                c = cundereffed;
            } else {
                calType = c.getCalType();
            }
            if (tag.equals(WebdavTags.owner)) {
                xml.openTag(tag);
                String href = intf.makeUserHref(c.getOwner().getPrincipalRef());
                if (!href.endsWith("/")) {
                    href = href + "/";
                }
                xml.property(WebdavTags.href, href);
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(WebdavTags.description)) {
                xml.property(tag, this.col.getDescription());
                return true;
            }
            if (tag.equals(WebdavTags.resourcetype)) {
                xml.openTag(tag);
                xml.emptyTag(WebdavTags.collection);
                if (this.debug) {
                    this.debugMsg("generateProp resourcetype for " + this.col);
                }
                if (calType == 2) {
                    xml.emptyTag(CaldavTags.scheduleInbox);
                } else if (calType == 3) {
                    xml.emptyTag(CaldavTags.scheduleOutbox);
                } else if (calType == 1) {
                    xml.emptyTag(CaldavTags.calendar);
                } else if (calType == 4) {
                    xml.emptyTag(AppleServerTags.notification);
                }
                String s = cundereffed.getProperty(AppleServerTags.shared);
                if (s != null && Boolean.valueOf(s).booleanValue()) {
                    AccessPrincipal owner = c == null ? cundereffed.getOwner() : c.getOwner();
                    if (owner.equals(this.getSysi().getPrincipal())) {
                        xml.emptyTag(AppleServerTags.sharedOwner);
                    } else {
                        xml.emptyTag(AppleServerTags.shared);
                    }
                }
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(AppleServerTags.invite)) {
                CalDAVCollection imm = (CalDAVCollection)this.getImmediateTargetCollection();
                InviteType inv = this.getSysi().getInviteStatus(imm);
                if (inv == null) {
                    return false;
                }
                inv.toXml(xml);
                return true;
            }
            if (tag.equals(CaldavTags.scheduleCalendarTransp)) {
                xml.openTag(tag);
                if (this.col.getAffectsFreeBusy()) {
                    xml.emptyTag(CaldavTags.opaque);
                } else {
                    xml.emptyTag(CaldavTags.transparent);
                }
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(CaldavTags.scheduleDefaultCalendarURL) && calType == 2) {
                xml.openTag(tag);
                CalPrincipalInfo cinfo = this.getSysi().getCalPrincipalInfo(this.getOwner());
                if (cinfo.defaultCalendarPath != null) {
                    this.generateHref(xml, cinfo.defaultCalendarPath);
                }
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(AppleServerTags.getctag)) {
                if (c != null) {
                    xml.property(tag, c.getEtag());
                } else {
                    xml.property(tag, this.col.getEtag());
                }
                return true;
            }
            if (tag.equals(AppleServerTags.sharedUrl)) {
                if (!cundereffed.isAlias()) {
                    return false;
                }
                xml.openTag(tag);
                xml.property(WebdavTags.href, cundereffed.getAliasUri());
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(AppleServerTags.allowedSharingModes)) {
                if (!this.col.getCanShare()) {
                    return false;
                }
                xml.openTag(tag);
                if (this.col.getCanShare()) {
                    xml.emptyTag(AppleServerTags.canBeShared);
                }
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(AppleIcalTags.calendarColor)) {
                String val = this.col.getColor();
                if (val == null) {
                    return false;
                }
                xml.property(tag, val);
                return true;
            }
            if (tag.equals(CaldavTags.calendarDescription)) {
                xml.property(tag, this.col.getDescription());
                return true;
            }
            if (tag.equals(AppleIcalTags.calendarOrder)) {
                xml.property(tag, this.col.getProperty(tag));
                return true;
            }
            if (this.col.getCalType() == 2 && tag.equals(CaldavTags.calendarFreeBusySet)) {
                xml.openTag(tag);
                Collection<String> hrefs = this.getSysi().getFreebusySet();
                for (String href : hrefs) {
                    xml.property(WebdavTags.href, href);
                }
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(CaldavTags.maxAttendeesPerInstance)) {
                if (calType != 1 && calType != 2 && calType != 3) {
                    return false;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxAttendeesPerInstance();
                if (val == null) {
                    return false;
                }
                xml.property(tag, String.valueOf(val));
                return true;
            }
            if (tag.equals(CaldavTags.maxDateTime)) {
                if (calType != 1 && calType != 2 && calType != 3) {
                    return false;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxAttendeesPerInstance();
                if (val == null) {
                    return false;
                }
                xml.property(tag, String.valueOf(val));
                return true;
            }
            if (tag.equals(CaldavTags.maxInstances)) {
                if (calType != 1 && calType != 2 && calType != 3) {
                    return false;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxInstances();
                if (val == null) {
                    return false;
                }
                xml.property(tag, String.valueOf(val));
                return true;
            }
            if (tag.equals(CaldavTags.maxResourceSize)) {
                if (calType != 1 && calType != 2 && calType != 3) {
                    return false;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxUserEntitySize();
                if (val == null) {
                    return false;
                }
                xml.property(tag, String.valueOf(val));
                return true;
            }
            if (tag.equals(CaldavTags.minDateTime)) {
                if (calType != 1 && calType != 2 && calType != 3) {
                    return false;
                }
                String val = this.getSysi().getAuthProperties().getMinDateTime();
                if (val == null) {
                    return false;
                }
                xml.property(tag, val);
                return true;
            }
            if (tag.equals(CaldavTags.supportedCalendarComponentSet)) {
                List<String> comps = c.getSupportedComponents();
                if (Util.isEmpty(comps)) {
                    return false;
                }
                xml.openTag(tag);
                for (String s : comps) {
                    xml.startTag(CaldavTags.comp);
                    xml.attribute("name", s);
                    xml.endEmptyTag();
                }
                xml.newline();
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(CaldavTags.supportedCalendarData)) {
                xml.openTag(tag);
                xml.startTag(CaldavTags.calendarData);
                xml.attribute("content-type", "text/calendar");
                xml.attribute("version", "2.0");
                xml.endEmptyTag();
                xml.newline();
                xml.startTag(CaldavTags.calendarData);
                xml.attribute("content-type", "application/calendar+xml");
                xml.attribute("version", "2.0");
                xml.endEmptyTag();
                xml.newline();
                xml.startTag(CaldavTags.calendarData);
                xml.attribute("content-type", "application/calendar+json");
                xml.attribute("version", "2.0");
                xml.endEmptyTag();
                xml.newline();
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(CaldavTags.timezoneServiceSet)) {
                xml.openTag(tag);
                String href = this.getSysi().getSystemProperties().getTzServeruri();
                xml.property(WebdavTags.href, href);
                xml.newline();
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(CaldavTags.calendarTimezone)) {
                String tzid = this.col.getTimezone();
                if (tzid == null) {
                    return false;
                }
                String val = this.getSysi().toStringTzCalendar(tzid);
                if (val == null) {
                    return false;
                }
                xml.cdataProperty(tag, val);
                return true;
            }
            if (tag.equals(CaldavTags.defaultAlarmVeventDate) || tag.equals(CaldavTags.defaultAlarmVeventDatetime) || tag.equals(CaldavTags.defaultAlarmVtodoDate) || tag.equals(CaldavTags.defaultAlarmVtodoDatetime)) {
                if (cundereffed == null) {
                    return false;
                }
                String val = cundereffed.getProperty(tag);
                if (val == null) {
                    return false;
                }
                xml.cdataProperty(tag, val);
                return true;
            }
            if (tag.equals(CaldavTags.vpollMaxActive)) {
                if (calType != 1 && calType != 2 && calType != 3) {
                    return false;
                }
                Integer val = this.getSysi().getSystemProperties().getVpollMaxActive();
                if (val == null) {
                    return false;
                }
                xml.property(tag, String.valueOf(val));
                return true;
            }
            if (tag.equals(CaldavTags.vpollMaxItems)) {
                if (calType != 1 && calType != 2 && calType != 3) {
                    return false;
                }
                Integer val = this.getSysi().getSystemProperties().getVpollMaxItems();
                if (val == null) {
                    return false;
                }
                xml.property(tag, String.valueOf(val));
                return true;
            }
            if (tag.equals(CaldavTags.vpollMaxVoters)) {
                if (calType != 1 && calType != 2 && calType != 3) {
                    return false;
                }
                Integer val = this.getSysi().getSystemProperties().getVpollMaxVoters();
                if (val == null) {
                    return false;
                }
                xml.property(tag, String.valueOf(val));
                return true;
            }
            if (tag.equals(CaldavTags.vpollSupportedComponentSet)) {
                List<String> comps = c.getVpollSupportedComponents();
                if (Util.isEmpty(comps)) {
                    return false;
                }
                xml.openTag(tag);
                for (String s : comps) {
                    xml.startTag(CaldavTags.comp);
                    xml.attribute("name", s);
                    xml.endEmptyTag();
                }
                xml.newline();
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(BedeworkServerTags.aliasUri)) {
                String alias = this.col.getAliasUri();
                if (alias == null) {
                    return false;
                }
                xml.property(tag, alias);
                return true;
            }
            if (tag.equals(BedeworkServerTags.remoteId)) {
                String id = this.col.getRemoteId();
                if (id == null) {
                    return false;
                }
                xml.property(tag, id);
                return true;
            }
            if (tag.equals(BedeworkServerTags.remotePw)) {
                String pw = this.col.getRemotePw();
                if (pw == null) {
                    return false;
                }
                xml.property(tag, pw);
                return true;
            }
            if (tag.equals(BedeworkServerTags.deletionSuppressed)) {
                xml.property(tag, String.valueOf(this.col.getSynchDeleteSuppressed()));
                return true;
            }
            return super.generatePropertyValue(tag, intf, allProp);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public boolean generateCalWsProperty(List<GetPropertiesBasePropertyType> props, QName tag, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        try {
            if (tag.equals(CalWSSoapTags.childCollection)) {
                for (WebdavNsNode child : intf.getChildren(this, null)) {
                    CaldavBwNode cn = (CaldavBwNode)child;
                    ChildCollectionType cc = new ChildCollectionType();
                    cc.setHref(cn.getUrlValue());
                    List<Object> rtypes = cc.getCalendarCollectionOrCollection();
                    if (!cn.isCollection()) continue;
                    rtypes.add(new CollectionType());
                    if (cn.isCalendarCollection()) {
                        rtypes.add(new CalendarCollectionType());
                    }
                    props.add(cc);
                }
                return true;
            }
            if (tag.equals(CalWSSoapTags.lastModifiedDateTime)) {
                String val = this.col.getLastmod();
                if (val == null) {
                    return true;
                }
                LastModifiedDateTimeType lmdt = new LastModifiedDateTimeType();
                lmdt.setDateTime(XcalUtil.fromDtval(val));
                props.add(lmdt);
                return true;
            }
            if (tag.equals(CalWSSoapTags.maxAttendeesPerInstance)) {
                if (!this.rootNode) {
                    return true;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxAttendeesPerInstance();
                if (val != null) {
                    props.add(this.intProp(new MaxAttendeesPerInstanceType(), val));
                }
                return true;
            }
            if (tag.equals(CalWSSoapTags.maxDateTime)) {
                return true;
            }
            if (tag.equals(CalWSSoapTags.maxInstances)) {
                if (!this.rootNode) {
                    return true;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxInstances();
                if (val != null) {
                    props.add(this.intProp(new MaxInstancesType(), val));
                }
                return true;
            }
            if (tag.equals(CalWSSoapTags.maxResourceSize)) {
                if (!this.rootNode) {
                    return true;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxUserEntitySize();
                if (val != null) {
                    props.add(this.intProp(new MaxResourceSizeType(), val));
                }
                return true;
            }
            if (tag.equals(CalWSSoapTags.minDateTime)) {
                return true;
            }
            if (tag.equals(CalWSSoapTags.principalHome)) {
                if (!this.rootNode || intf.getAnonymous()) {
                    return true;
                }
                SysIntf si = this.getSysi();
                CalPrincipalInfo cinfo = si.getCalPrincipalInfo(si.getPrincipal());
                if (cinfo.userHomePath != null) {
                    props.add(this.strProp(new PrincipalHomeType(), cinfo.userHomePath));
                }
                return true;
            }
            if (tag.equals(CalWSSoapTags.resourceDescription)) {
                String s = this.col.getDescription();
                if (s != null) {
                    props.add(this.strProp(new ResourceDescriptionType(), s));
                }
                return true;
            }
            if (tag.equals(CalWSSoapTags.resourceType)) {
                ResourceTypeType rt = new ResourceTypeType();
                CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
                int calType = c == null ? 0 : c.getCalType();
                List<Object> rtypes = rt.getCalendarCollectionOrCollectionOrInbox();
                rtypes.add(new CollectionType());
                if (calType == 2) {
                    rtypes.add(new InboxType());
                } else if (calType == 3) {
                    rtypes.add(new OutboxType());
                } else if (calType == 1) {
                    rtypes.add(new CalendarCollectionType());
                }
                props.add(rt);
                return true;
            }
            if (tag.equals(CalWSSoapTags.resourceTimezoneId)) {
                String tzid = this.col.getTimezone();
                if (tzid != null) {
                    props.add(this.strProp(new ResourceTimezoneIdType(), tzid));
                }
                return true;
            }
            if (tag.equals(CalWSSoapTags.supportedCalendarComponentSet)) {
                SupportedCalendarComponentSetType sccs = new SupportedCalendarComponentSetType();
                CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
                List<String> comps = c.getSupportedComponents();
                if (Util.isEmpty(comps)) {
                    return false;
                }
                ObjectFactory of = new ObjectFactory();
                for (String s : comps) {
                    Object el = null;
                    if (s.equals("VEVENT")) {
                        el = of.createVevent(new VeventType());
                    } else if (s.equals("VTODO")) {
                        el = of.createVtodo(new VtodoType());
                    } else if (s.equals("VAVAILABILITY")) {
                        el = of.createVavailability(new VavailabilityType());
                    }
                    if (el == null) continue;
                    sccs.getBaseComponent().add((JAXBElement<? extends BaseComponentType>)el);
                }
                props.add(sccs);
                return true;
            }
            return super.generateCalWsProperty(props, tag, intf, allProp);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private GetPropertiesBasePropertyType intProp(IntegerPropertyType prop, Integer val) {
        prop.setInteger(BigInteger.valueOf(val.longValue()));
        return prop;
    }

    private GetPropertiesBasePropertyType strProp(StringPropertyType prop, String val) {
        prop.setString(val);
        return prop;
    }

    @Override
    public boolean generateXrdProperties(List<Object> props, String name, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        try {
            CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
            int calType = c == null ? 0 : c.getCalType();
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/collection")) {
                props.add(this.xrdEmptyProperty(name));
                if (calType == 2) {
                    props.add(this.xrdEmptyProperty("http://docs.oasis-open.org/ws-calendar/ns/rest/inbox"));
                } else if (calType == 3) {
                    props.add(this.xrdEmptyProperty("http://docs.oasis-open.org/ws-calendar/ns/rest/outbox"));
                } else if (calType == 1) {
                    props.add(this.xrdEmptyProperty("http://docs.oasis-open.org/ws-calendar/ns/rest/calendar-collection"));
                }
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/description")) {
                String s = this.col.getDescription();
                if (s == null) {
                    return true;
                }
                props.add(this.xrdProperty(name, s));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/principal-home")) {
                if (!this.rootNode || intf.getAnonymous()) {
                    return true;
                }
                SysIntf si = this.getSysi();
                CalPrincipalInfo cinfo = si.getCalPrincipalInfo(si.getPrincipal());
                if (cinfo.userHomePath == null) {
                    return true;
                }
                props.add(this.xrdProperty(name, this.getUrlValue(cinfo.userHomePath, true)));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/max-attendees-per-instance")) {
                if (!this.rootNode) {
                    return true;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxAttendeesPerInstance();
                if (val == null) {
                    return true;
                }
                props.add(this.xrdProperty(name, String.valueOf(val)));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/max-date-time")) {
                if (!this.rootNode) {
                    return true;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxAttendeesPerInstance();
                if (val == null) {
                    return false;
                }
                props.add(this.xrdProperty(name, String.valueOf(val)));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/max-instances")) {
                if (!this.rootNode) {
                    return true;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxInstances();
                if (val == null) {
                    return false;
                }
                props.add(this.xrdProperty(name, String.valueOf(val)));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/max-resource-size")) {
                if (!this.rootNode) {
                    return true;
                }
                Integer val = this.getSysi().getAuthProperties().getMaxUserEntitySize();
                if (val == null) {
                    return false;
                }
                props.add(this.xrdProperty(name, String.valueOf(val)));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/min-date-time")) {
                if (!this.rootNode) {
                    return true;
                }
                String val = this.getSysi().getAuthProperties().getMinDateTime();
                if (val == null) {
                    return false;
                }
                props.add(this.xrdProperty(name, val));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/timezone")) {
                String tzid = this.col.getTimezone();
                if (tzid == null) {
                    return false;
                }
                props.add(this.xrdProperty(name, tzid));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/supported-calendar-component-set")) {
                SupportedCalendarComponentSetType sccs = new SupportedCalendarComponentSetType();
                List<String> comps = c.getSupportedComponents();
                if (Util.isEmpty(comps)) {
                    return false;
                }
                ObjectFactory of = new ObjectFactory();
                for (String s : comps) {
                    Object el = null;
                    if (s.equals("VEVENT")) {
                        el = of.createVevent(new VeventType());
                    } else if (s.equals("VTODO")) {
                        el = of.createVtodo(new VtodoType());
                    } else if (s.equals("VAVAILABILITY")) {
                        el = of.createVavailability(new VavailabilityType());
                    }
                    if (el == null) continue;
                    sccs.getBaseComponent().add((JAXBElement<? extends BaseComponentType>)el);
                }
                JAXBElement el = new JAXBElement(CalWSSoapTags.supportedCalendarComponentSet, SupportedCalendarComponentSetType.class, (Object)sccs);
                props.add(el);
                return true;
            }
            return super.generateXrdProperties(props, name, intf, allProp);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public Collection<WebdavNsNode.PropertyTagEntry> getPropertyNames() throws WebdavException {
        ArrayList<WebdavNsNode.PropertyTagEntry> res = new ArrayList<WebdavNsNode.PropertyTagEntry>();
        res.addAll(super.getPropertyNames());
        res.addAll(propertyNames.values());
        return res;
    }

    @Override
    public Collection<WebdavNsNode.PropertyTagEntry> getCalWSSoapNames() throws WebdavException {
        ArrayList<WebdavNsNode.PropertyTagEntry> res = new ArrayList<WebdavNsNode.PropertyTagEntry>();
        res.addAll(super.getCalWSSoapNames());
        res.addAll(calWSSoapNames.values());
        return res;
    }

    @Override
    public Collection<CaldavBwNode.PropertyTagXrdEntry> getXrdNames() throws WebdavException {
        ArrayList<CaldavBwNode.PropertyTagXrdEntry> res = new ArrayList<CaldavBwNode.PropertyTagXrdEntry>();
        res.addAll(super.getXrdNames());
        res.addAll(xrdNames.values());
        return res;
    }

    @Override
    public Collection<QName> getSupportedReports() throws WebdavException {
        ArrayList<QName> res = new ArrayList<QName>();
        CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
        if (c == null) {
            return res;
        }
        res.addAll(super.getSupportedReports());
        if (c.freebusyAllowed()) {
            res.add(CaldavTags.freeBusyQuery);
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CaldavCalNode{cduri=");
        sb.append("path=");
        sb.append(this.getPath());
        sb.append(", isCalendarCollection()=");
        try {
            sb.append(this.isCalendarCollection());
        }
        catch (Throwable t) {
            sb.append("exception(" + t.getMessage() + ")");
        }
        sb.append("}");
        return sb.toString();
    }

    private boolean checkCalForSetProp(WebdavNsNode.SetPropertyResult spr) {
        if (this.col != null) {
            return true;
        }
        spr.status = 404;
        spr.message = "Not found";
        return false;
    }

    static {
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.calendarDescription);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.calendarFreeBusySet);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.calendarTimezone);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.maxAttendeesPerInstance);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.maxDateTime);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.maxInstances);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.maxResourceSize);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.minDateTime);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.scheduleCalendarTransp);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.scheduleDefaultCalendarURL);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.supportedCalendarComponentSet);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.supportedCalendarData);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.timezoneServiceSet);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.vpollMaxActive);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.vpollMaxItems);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.vpollMaxVoters);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.vpollSupportedComponentSet);
        CaldavCalNode.addPropEntry(propertyNames, AppleServerTags.allowedSharingModes);
        CaldavCalNode.addPropEntry(propertyNames, AppleServerTags.getctag);
        CaldavCalNode.addPropEntry(propertyNames, AppleServerTags.invite);
        CaldavCalNode.addPropEntry(propertyNames, AppleServerTags.sharedUrl);
        CaldavCalNode.addPropEntry(propertyNames, AppleIcalTags.calendarColor);
        CaldavCalNode.addPropEntry(propertyNames, BedeworkServerTags.aliasUri);
        CaldavCalNode.addPropEntry(propertyNames, BedeworkServerTags.refreshRate);
        CaldavCalNode.addPropEntry(propertyNames, BedeworkServerTags.remoteId);
        CaldavCalNode.addPropEntry(propertyNames, BedeworkServerTags.remotePw);
        CaldavCalNode.addPropEntry(propertyNames, BedeworkServerTags.deletionSuppressed);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.defaultAlarmVeventDate);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.defaultAlarmVeventDatetime);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.defaultAlarmVtodoDate);
        CaldavCalNode.addPropEntry(propertyNames, CaldavTags.defaultAlarmVtodoDatetime);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/collection", true, true);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/description", true, false);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/max-attendees-per-instance", true, false);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/max-date-time", true, false);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/max-instances", true, false);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/max-resource-size", true, false);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/min-date-time", true, false);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/principal-home", true, true);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/timezone", true, false);
        CaldavCalNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/supported-calendar-component-set", true, false);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.childCollection, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.maxAttendeesPerInstance, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.maxDateTime, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.maxInstances, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.maxResourceSize, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.minDateTime, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.principalHome, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.resourceDescription, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.resourceType, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.resourceTimezoneId, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.supportedCalendarComponentSet, true);
        CaldavCalNode.addCalWSSoapName(CalWSSoapTags.timezoneServer, true);
    }
}

