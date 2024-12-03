/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.caldav.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CaldavURI;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.tagdefs.CalWSSoapTags;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.XrdTags;
import org.bedework.webdav.servlet.shared.WdCollection;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.oasis_open.docs.ns.xri.xrd_1.LinkType;
import org.oasis_open.docs.ns.xri.xrd_1.PropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarAccessFeatureType;
import org.oasis_open.docs.ws_calendar.ns.soap.CreationDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.DisplayNameType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceOwnerType;
import org.oasis_open.docs.ws_calendar.ns.soap.SupportedFeaturesType;

public abstract class CaldavBwNode
extends WebdavNsNode {
    protected boolean rootNode;
    protected CalDAVCollection col;
    private static final HashMap<QName, WebdavNsNode.PropertyTagEntry> propertyNames = new HashMap();
    private static final Collection<QName> supportedReports = new ArrayList<QName>();
    private static final HashMap<QName, WebdavNsNode.PropertyTagEntry> calWSSoapNames = new HashMap();
    private static final HashMap<String, PropertyTagXrdEntry> xrdNames;
    private SysIntf sysi;

    CaldavBwNode(CaldavURI cdURI, SysIntf sysi) throws WebdavException {
        this(sysi, cdURI.getPath(), cdURI.isCollection(), cdURI.getUri());
    }

    CaldavBwNode(SysIntf sysi, String path, boolean collection, String uri) {
        super(sysi, sysi.getUrlHandler(), path, collection, uri);
        this.sysi = sysi;
        this.rootNode = uri != null && uri.equals("/");
    }

    CaldavBwNode(boolean collection, SysIntf sysi, String uri) {
        super(sysi, sysi.getUrlHandler(), null, collection, uri);
        this.sysi = sysi;
        this.rootNode = uri != null && uri.equals("/");
    }

    public abstract String getEtokenValue() throws WebdavException;

    public SysIntf getIntf() {
        return this.sysi;
    }

    @Override
    public WdCollection getCollection(boolean deref) throws WebdavException {
        if (!deref) {
            return this.col;
        }
        return this.col.resolveAlias(true);
    }

    @Override
    public WdCollection getImmediateTargetCollection() throws WebdavException {
        return this.col.resolveAlias(false);
    }

    public boolean isCalendarCollection() throws WebdavException {
        if (!this.isCollection()) {
            return false;
        }
        CalDAVCollection c = (CalDAVCollection)this.getCollection(true);
        if (c == null) {
            return false;
        }
        return c.getCalType() == 1;
    }

    public SysIntf getSysi() {
        return this.sysi;
    }

    @Override
    public Collection<QName> getSupportedReports() throws WebdavException {
        ArrayList<QName> res = new ArrayList<QName>();
        res.addAll(super.getSupportedReports());
        res.addAll(supportedReports);
        return res;
    }

    @Override
    public String getSyncToken() throws WebdavException {
        return null;
    }

    @Override
    public boolean getContentBinary() throws WebdavException {
        return false;
    }

    @Override
    public Collection<? extends WdEntity> getChildren(Supplier<Object> filterGetter) throws WebdavException {
        return null;
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
        try {
            return super.generatePropertyValue(tag, intf, allProp);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public boolean generateCalWsProperty(List<GetPropertiesBasePropertyType> props, QName tag, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        try {
            if (tag.equals(CalWSSoapTags.creationDateTime)) {
                String val = this.getCreDate();
                if (val == null) {
                    return true;
                }
                CreationDateTimeType cdt = new CreationDateTimeType();
                cdt.setDateTime(XcalUtil.fromDtval(val));
                props.add(cdt);
                return true;
            }
            if (tag.equals(CalWSSoapTags.displayName)) {
                String val = this.getDisplayname();
                if (val == null) {
                    return true;
                }
                DisplayNameType dn = new DisplayNameType();
                dn.setString(val);
                props.add(dn);
                return true;
            }
            if (tag.equals(CalWSSoapTags.supportedFeatures)) {
                SupportedFeaturesType sf = new SupportedFeaturesType();
                sf.getCalendarAccessFeature().add(new CalendarAccessFeatureType());
                props.add(sf);
                return true;
            }
            if (tag.equals(CalWSSoapTags.resourceOwner)) {
                String href = intf.makeUserHref(this.getOwner().getPrincipalRef());
                if (!href.endsWith("/")) {
                    href = href + "/";
                }
                ResourceOwnerType ro = new ResourceOwnerType();
                ro.setString(href);
                props.add(ro);
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

    public boolean generateXrdProperties(List<Object> props, String name, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        try {
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/created")) {
                String val = this.getCreDate();
                if (val == null) {
                    return true;
                }
                props.add(this.xrdProperty(name, val));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/displayname")) {
                String val = this.getDisplayname();
                if (val == null) {
                    return true;
                }
                props.add(this.xrdProperty(name, val));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/last-modified")) {
                String val = this.getLastmodDate();
                if (val == null) {
                    return true;
                }
                props.add(this.xrdProperty(name, val));
                return true;
            }
            if (name.equals("http://docs.oasis-open.org/ws-calendar/ns/rest/owner")) {
                String href = intf.makeUserHref(this.getOwner().getPrincipalRef());
                if (!href.endsWith("/")) {
                    href = href + "/";
                }
                props.add(this.xrdProperty(name, href));
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

    public Collection<PropertyTagXrdEntry> getXrdNames() throws WebdavException {
        return xrdNames.values();
    }

    public Collection<WebdavNsNode.PropertyTagEntry> getCalWSSoapNames() throws WebdavException {
        return calWSSoapNames.values();
    }

    protected JAXBElement<PropertyType> xrdProperty(String name, String val) throws WebdavException {
        PropertyType p = new PropertyType();
        p.setType(name);
        p.setValue(val);
        return new JAXBElement(XrdTags.property, PropertyType.class, (Object)p);
    }

    protected JAXBElement<LinkType> xrdLink(String name, Object val) throws WebdavException {
        LinkType l = new LinkType();
        l.setType(name);
        l.getTitleOrPropertyOrAny().add(val);
        return new JAXBElement(XrdTags.link, LinkType.class, (Object)l);
    }

    protected JAXBElement<PropertyType> xrdEmptyProperty(String name) throws WebdavException {
        PropertyType p = new PropertyType();
        p.setType(name);
        return new JAXBElement(XrdTags.property, PropertyType.class, (Object)p);
    }

    public String getUrlValue() throws WebdavException {
        return this.getUrlValue(this.uri, this.exists);
    }

    public String getUrlValue(String uri, boolean exists) throws WebdavException {
        try {
            String prefixed = this.urlHandler.prefix(uri);
            if (exists) {
                if (prefixed.endsWith("/")) {
                    if (!this.trailSlash()) {
                        prefixed = prefixed.substring(0, prefixed.length() - 1);
                    }
                } else if (this.trailSlash()) {
                    prefixed = prefixed + "/";
                }
            }
            return prefixed;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected static void addCalWSSoapName(QName tag, boolean inAllProp) {
        WebdavNsNode.PropertyTagEntry pte = new WebdavNsNode.PropertyTagEntry(tag, inAllProp);
        calWSSoapNames.put(tag, pte);
    }

    protected static void addPropEntry(HashMap<QName, WebdavNsNode.PropertyTagEntry> propertyNames, HashMap<String, PropertyTagXrdEntry> xrdNames, QName tag, String xrdName) {
        PropertyTagXrdEntry pte = new PropertyTagXrdEntry(tag, xrdName, false, false);
        propertyNames.put(tag, pte);
        xrdNames.put(xrdName, pte);
    }

    protected static void addPropEntry(HashMap<QName, WebdavNsNode.PropertyTagEntry> propertyNames, HashMap<String, PropertyTagXrdEntry> xrdNames, QName tag, String xrdName, boolean inAllProp) {
        PropertyTagXrdEntry pte = new PropertyTagXrdEntry(tag, xrdName, inAllProp, false);
        propertyNames.put(tag, pte);
        xrdNames.put(xrdName, pte);
    }

    protected static void addXrdEntry(HashMap<String, PropertyTagXrdEntry> xrdNames, String xrdName) {
        PropertyTagXrdEntry pte = new PropertyTagXrdEntry(null, xrdName, false, false);
        xrdNames.put(xrdName, pte);
    }

    protected static void addXrdEntry(HashMap<String, PropertyTagXrdEntry> xrdNames, String xrdName, boolean inAllProp, boolean inLink) {
        PropertyTagXrdEntry pte = new PropertyTagXrdEntry(null, xrdName, inAllProp, inLink);
        xrdNames.put(xrdName, pte);
    }

    protected String concatEtoken(String ... val) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < val.length; ++i) {
            sb.append(val[i]);
            if (i + 1 >= val.length) continue;
            sb.append('\t');
        }
        return sb.toString();
    }

    protected String[] splitEtoken(String val) {
        return val.split("\t");
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("path", this.getPath());
        return ts.toString();
    }

    static {
        supportedReports.add(CaldavTags.calendarMultiget);
        supportedReports.add(CaldavTags.calendarQuery);
        CaldavBwNode.addCalWSSoapName(CalWSSoapTags.creationDateTime, true);
        CaldavBwNode.addCalWSSoapName(CalWSSoapTags.displayName, true);
        CaldavBwNode.addCalWSSoapName(CalWSSoapTags.lastModifiedDateTime, true);
        CaldavBwNode.addCalWSSoapName(CalWSSoapTags.supportedFeatures, true);
        xrdNames = new HashMap();
        CaldavBwNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/created", true, false);
        CaldavBwNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/displayname", true, true);
        CaldavBwNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/last-modified", true, false);
        CaldavBwNode.addXrdEntry(xrdNames, "http://docs.oasis-open.org/ws-calendar/ns/rest/owner", true, false);
    }

    public static final class PropertyTagXrdEntry
    extends WebdavNsNode.PropertyTagEntry {
        public String xrdName;
        public boolean inLink;

        public PropertyTagXrdEntry(QName tag, String xrdName, boolean inPropAll, boolean inLink) {
            super(tag, inPropAll);
            this.xrdName = xrdName;
            this.inLink = inLink;
        }
    }
}

