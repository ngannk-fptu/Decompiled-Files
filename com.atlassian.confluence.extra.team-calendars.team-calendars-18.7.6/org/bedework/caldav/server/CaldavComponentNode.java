/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.xml.namespace.QName;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Acl;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CaldavBwNode;
import org.bedework.caldav.server.CaldavURI;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.ICalTags;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.w3c.dom.Element;

public class CaldavComponentNode
extends CaldavBwNode {
    private CalDAVEvent event;
    private AccessPrincipal owner;
    private Acl.CurrentAccess currentAccess;
    private String entityName;
    private boolean isTimezone;
    private Calendar ical;
    private Component comp;
    private String compContentType;
    private String compString;
    private static final HashMap<QName, WebdavNsNode.PropertyTagEntry> propertyNames = new HashMap();

    public CaldavComponentNode(SysIntf sysi, int status, String uri) {
        super(true, sysi, uri);
        this.setStatus(status);
    }

    public CaldavComponentNode(CaldavURI cdURI, SysIntf sysi) throws WebdavException {
        super(cdURI, sysi);
        this.col = cdURI.getCol();
        this.collection = false;
        this.allowsGet = true;
        this.entityName = cdURI.getEntityName();
        this.event = cdURI.getEntity();
    }

    public CaldavComponentNode(CalDAVEvent event, SysIntf sysi) throws WebdavException {
        super(sysi, event.getParentPath(), false, event.getPath());
        this.allowsGet = true;
        this.entityName = event.getName();
        this.event = event;
    }

    @Override
    public void init(boolean content) throws WebdavException {
        if (!content) {
            return;
        }
        try {
            if (this.event == null && this.exists && this.entityName == null) {
                this.exists = false;
                return;
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public AccessPrincipal getOwner() throws WebdavException {
        if (this.owner == null) {
            if (this.event == null) {
                return null;
            }
            this.owner = this.event.getOwner();
        }
        return this.owner;
    }

    @Override
    public boolean removeProperty(Element val, WebdavNsNode.SetPropertyResult spr) throws WebdavException {
        this.warn("Unimplemented - removeProperty");
        return false;
    }

    @Override
    public boolean setProperty(Element val, WebdavNsNode.SetPropertyResult spr) throws WebdavException {
        return super.setProperty(val, spr);
    }

    public Component getComponent() throws WebdavException {
        this.init(true);
        try {
            if (this.event != null && this.comp == null) {
                ComponentList<CalendarComponent> cl;
                if (this.ical == null) {
                    this.ical = this.getSysi().toCalendar(this.event, this.col.getCalType() == 2 || this.col.getCalType() == 3);
                }
                if ((cl = this.ical.getComponents()) == null || cl.isEmpty()) {
                    return null;
                }
                this.comp = (Component)cl.get(0);
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        return this.comp;
    }

    @Override
    public void update() throws WebdavException {
        if (this.event != null) {
            this.getSysi().updateEvent(this.event);
        }
    }

    public void setEntityName(String val) throws WebdavException {
        if (this.entityName != null) {
            throw new WebdavException("Cannot change entity name");
        }
        this.entityName = val;
        this.uri = this.uri + "/" + val;
    }

    public String getEntityName() {
        return this.entityName;
    }

    @Override
    public boolean trailSlash() {
        return false;
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
        WebdavNsNode.PropVal pv = new WebdavNsNode.PropVal();
        XmlEmit xml = intf.getXmlEmit();
        if (propertyNames.get(tag) == null) {
            return super.generatePropertyValue(tag, intf, allProp);
        }
        if (this.isTimezone) {
            return this.generateTZPropertyValue(tag, intf, allProp);
        }
        try {
            CalDAVEvent ev = this.checkEv(pv);
            if (ev == null) {
                return true;
            }
            return ev.generatePropertyValue(tag, xml);
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

    public void setEvent(CalDAVEvent val) {
        this.event = val;
    }

    public CalDAVEvent getEvent() throws WebdavException {
        this.init(true);
        return this.event;
    }

    public Calendar getIcal() throws WebdavException {
        this.init(true);
        try {
            if (this.ical == null) {
                this.ical = this.getSysi().toCalendar(this.event, this.col.getCalType() == 2 || this.col.getCalType() == 3);
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        return this.ical;
    }

    @Override
    public String writeContent(XmlEmit xml, Writer wtr, String contentType) throws WebdavException {
        try {
            ArrayList<CalDAVEvent> evs = new ArrayList<CalDAVEvent>();
            evs.add(this.event);
            SysIntf.MethodEmitted method = this.col.getCalType() == 2 || this.col.getCalType() == 3 ? SysIntf.MethodEmitted.eventMethod : SysIntf.MethodEmitted.noMethod;
            return this.getSysi().writeCalendar(evs, method, xml, wtr, contentType);
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
        return this.getCompString(contentType);
    }

    @Override
    public Acl.CurrentAccess getCurrentAccess() throws WebdavException {
        if (this.currentAccess != null) {
            return this.currentAccess;
        }
        if (this.event == null) {
            return null;
        }
        try {
            this.currentAccess = this.getSysi().checkAccess(this.event, 25, true);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        return this.currentAccess;
    }

    public String getStagValue() throws WebdavException {
        this.init(true);
        CalDAVEvent ev = this.getEvent();
        if (ev == null) {
            return null;
        }
        return ev.getScheduleTag();
    }

    public String getPrevStagValue() throws WebdavException {
        this.init(true);
        CalDAVEvent ev = this.getEvent();
        if (ev == null) {
            return null;
        }
        return ev.getPrevScheduleTag();
    }

    @Override
    public String getEtagValue(boolean strong) throws WebdavException {
        this.init(true);
        CalDAVEvent ev = this.getEvent();
        if (ev == null) {
            return null;
        }
        String val = ev.getEtag();
        if (strong) {
            return val;
        }
        return "W/" + val;
    }

    public String getPrevEtagValue(boolean strong) throws WebdavException {
        this.init(true);
        CalDAVEvent ev = this.getEvent();
        if (ev == null) {
            return null;
        }
        String val = ev.getPreviousEtag();
        if (strong) {
            return val;
        }
        return "W/" + val;
    }

    @Override
    public String getEtokenValue() throws WebdavException {
        return this.concatEtoken(this.getEtagValue(true), this.getStagValue());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CaldavComponentNode{");
        sb.append("path=");
        sb.append(this.getPath());
        sb.append(", entityName=");
        sb.append(String.valueOf(this.entityName));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String getContentLang() throws WebdavException {
        return "en";
    }

    @Override
    public long getContentLen() throws WebdavException {
        return this.getCompString(this.getContentType()).length();
    }

    @Override
    public String getContentType() throws WebdavException {
        return "text/calendar;charset=utf-8";
    }

    @Override
    public String getCreDate() throws WebdavException {
        this.init(false);
        CalDAVEvent ev = this.getEvent();
        if (ev == null) {
            return null;
        }
        return ev.getCreated();
    }

    @Override
    public String getDisplayname() throws WebdavException {
        return this.getEntityName();
    }

    @Override
    public String getLastmodDate() throws WebdavException {
        this.init(false);
        CalDAVEvent ev = this.getEvent();
        if (ev == null) {
            return null;
        }
        try {
            return DateTimeUtil.fromISODateTimeUTCtoRfc822(ev.getLastmod());
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public boolean allowsSyncReport() throws WebdavException {
        return false;
    }

    @Override
    public boolean getDeleted() throws WebdavException {
        return this.getEvent().getDeleted();
    }

    private String getCompString(String contentType) throws WebdavException {
        String ctype = contentType;
        if (ctype == null) {
            ctype = this.getSysi().getDefaultContentType();
        }
        if (ctype.equals(this.compContentType)) {
            return this.compString;
        }
        this.getIcal();
        this.compContentType = ctype;
        this.compString = this.getSysi().toIcalString(this.ical, ctype);
        return this.compString;
    }

    private boolean generateTZPropertyValue(QName tag, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        if (tag.equals(ICalTags.tzid)) {
            return true;
        }
        if (tag.equals(ICalTags.tzname)) {
            return true;
        }
        if (tag.equals(ICalTags.tzoffsetfrom)) {
            return true;
        }
        if (tag.equals(ICalTags.tzoffsetto)) {
            return true;
        }
        return tag.equals(ICalTags.tzurl);
    }

    private CalDAVEvent checkEv(WebdavNsNode.PropVal pv) throws WebdavException {
        CalDAVEvent ev = this.getEvent();
        if (ev == null) {
            pv.notFound = true;
            return null;
        }
        return ev;
    }

    static {
        CaldavComponentNode.addPropEntry(propertyNames, CaldavTags.calendarData);
        CaldavComponentNode.addPropEntry(propertyNames, CaldavTags.originator);
        CaldavComponentNode.addPropEntry(propertyNames, CaldavTags.recipient);
        CaldavComponentNode.addPropEntry(propertyNames, CaldavTags.scheduleTag, true);
        CaldavComponentNode.addPropEntry(propertyNames, AppleServerTags.scheduleChanges);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.attach);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.attendee);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.categories);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags._class);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.comment);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.contact);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.created);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.description);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.dtend);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.dtstamp);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.dtstart);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.duration);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.exdate);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.exrule);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.geo);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.lastModified);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.location);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.organizer);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.priority);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.rdate);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.recurrenceId);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.relatedTo);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.resources);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.requestStatus);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.rrule);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.sequence);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.status);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.summary);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.transp);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.trigger);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.uid);
        CaldavComponentNode.addPropEntry(propertyNames, ICalTags.url);
    }
}

