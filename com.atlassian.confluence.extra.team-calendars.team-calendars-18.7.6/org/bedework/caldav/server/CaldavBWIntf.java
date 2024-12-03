/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.Marshaller
 */
package org.bedework.caldav.server;

import ietf.params.xml.ns.caldav.FilterType;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.bedework.access.AccessException;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.AccessXmlUtil;
import org.bedework.access.Ace;
import org.bedework.access.AceWho;
import org.bedework.access.Acl;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CalDAVResource;
import org.bedework.caldav.server.CalDavHeaders;
import org.bedework.caldav.server.CaldavBwNode;
import org.bedework.caldav.server.CaldavCalNode;
import org.bedework.caldav.server.CaldavComponentNode;
import org.bedework.caldav.server.CaldavGroupNode;
import org.bedework.caldav.server.CaldavResourceNode;
import org.bedework.caldav.server.CaldavURI;
import org.bedework.caldav.server.CaldavUserNode;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.SysiIcalendar;
import org.bedework.caldav.server.calquery.CalData;
import org.bedework.caldav.server.calquery.FreeBusyQuery;
import org.bedework.caldav.server.filter.FilterHandler;
import org.bedework.caldav.server.get.FreeBusyGetHandler;
import org.bedework.caldav.server.get.GetHandler;
import org.bedework.caldav.server.get.IscheduleGetHandler;
import org.bedework.caldav.server.get.ServerInfoGetHandler;
import org.bedework.caldav.server.get.WebcalGetHandler;
import org.bedework.caldav.server.soap.synch.SynchConnections;
import org.bedework.caldav.server.soap.synch.SynchConnectionsMBean;
import org.bedework.caldav.server.sysinterface.CalPrincipalInfo;
import org.bedework.caldav.server.sysinterface.RetrievalMode;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.jmx.AnnotatedMBean;
import org.bedework.util.jmx.ManagementContext;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.AccessUtil;
import org.bedework.webdav.servlet.common.Headers;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.common.WebdavServlet;
import org.bedework.webdav.servlet.common.WebdavUtils;
import org.bedework.webdav.servlet.shared.PrincipalPropertySearch;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WdSynchReport;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNotFound;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavPrincipalNode;
import org.bedework.webdav.servlet.shared.WebdavProperty;
import org.bedework.webdav.servlet.shared.WebdavServerError;
import org.bedework.webdav.servlet.shared.WebdavUnauthorized;
import org.bedework.webdav.servlet.shared.WebdavUnsupportedMediaType;
import org.bedework.webdav.servlet.shared.serverInfo.Application;
import org.bedework.webdav.servlet.shared.serverInfo.Feature;
import org.bedework.webdav.servlet.shared.serverInfo.ServerInfo;
import org.oasis_open.docs.ns.xri.xrd_1.AnyURI;
import org.oasis_open.docs.ns.xri.xrd_1.LinkType;
import org.oasis_open.docs.ns.xri.xrd_1.XRDType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.w3c.dom.Element;

public class CaldavBWIntf
extends WebdavNsIntf {
    private String namespacePrefix;
    private AccessUtil accessUtil;
    private String namespace;
    SysIntf sysi;
    private boolean calWs;
    private static ServerInfo serverInfo;
    private boolean synchWs;
    private boolean notifyWs;
    private boolean socketWs;
    private static final Set<ObjectName> registeredMBeans;
    private static ManagementContext managementContext;
    private static SynchConnections synchConn;
    private static final QName[] knownProperties;

    public static void registerMbean(ObjectName key, Object bean) {
        try {
            AnnotatedMBean.registerMBean(CaldavBWIntf.getManagementContext(), bean, key);
            registeredMBeans.add(key);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void unregister(ObjectName key) {
        if (registeredMBeans.remove(key)) {
            try {
                CaldavBWIntf.getManagementContext().unregisterMBean(key);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static ManagementContext getManagementContext() {
        if (managementContext == null) {
            managementContext = new ManagementContext(ManagementContext.DEFAULT_DOMAIN);
        }
        return managementContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void contextInitialized(ServletContextEvent sce) {
        try {
            Set<ObjectName> set = registeredMBeans;
            synchronized (set) {
                if (managementContext != null) {
                    return;
                }
                ServletContext sc = sce.getServletContext();
                synchConn = new SynchConnections();
                CaldavBWIntf.registerMbean(new ObjectName(synchConn.getServiceName()), synchConn);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void contextDestroyed(ServletContextEvent sce) {
        Set<ObjectName> set = registeredMBeans;
        synchronized (set) {
            if (managementContext == null) {
                return;
            }
            try {
                for (ObjectName on : registeredMBeans) {
                    CaldavBWIntf.unregister(on);
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            finally {
                try {
                    managementContext.stop();
                }
                catch (Throwable throwable) {}
                managementContext = null;
            }
        }
    }

    @Override
    public void init(WebdavServlet servlet, HttpServletRequest req, HashMap<String, MethodBase.MethodInfo> methods, boolean dumpContent) throws WebdavException {
        try {
            this.calWs = Boolean.parseBoolean(servlet.getInitParameter("calws"));
            this.synchWs = Boolean.parseBoolean(servlet.getInitParameter("synchws"));
            this.notifyWs = Boolean.parseBoolean(servlet.getInitParameter("notifyws"));
            this.socketWs = Boolean.parseBoolean(servlet.getInitParameter("socketws"));
            this.sysi = this.getSysi(servlet.getInitParameter("sysintfImpl"));
            super.init(servlet, req, methods, dumpContent);
            this.namespacePrefix = WebdavUtils.getUrlPrefix(req);
            this.namespace = this.namespacePrefix + "/schema";
            this.account = this.sysi.init(req, this.account, false, this.calWs, this.synchWs, this.notifyWs, this.socketWs, null);
            this.accessUtil = new AccessUtil(this.namespacePrefix, this.xml, (AccessXmlUtil.AccessXmlCb)new CalDavAccessXmlCb(this.sysi));
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public SynchConnectionsMBean getActiveConnections() throws Throwable {
        return synchConn;
    }

    public void reAuth(HttpServletRequest req, String account, boolean service, String opaqueData) throws WebdavException {
        try {
            if (this.sysi != null) {
                try {
                    this.sysi.close();
                }
                catch (Throwable t) {
                    throw new WebdavException(t);
                }
            } else {
                this.sysi = this.getSysi(this.servlet.getInitParameter("sysintfImpl"));
            }
            this.account = account;
            this.sysi.init(req, account, service, this.calWs, this.synchWs, this.notifyWs, this.socketWs, opaqueData);
            this.accessUtil = new AccessUtil(this.namespacePrefix, this.xml, (AccessXmlUtil.AccessXmlCb)new CalDavAccessXmlCb(this.sysi));
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public boolean getCalWS() {
        return this.calWs;
    }

    public boolean getSynchWs() {
        return this.synchWs;
    }

    public boolean getNotifyWs() {
        return this.notifyWs;
    }

    @Override
    public String getDavHeader(WebdavNsNode node) throws WebdavException {
        if (this.account == null) {
            return super.getDavHeader(node) + ", calendar-access";
        }
        String hdr = super.getDavHeader(node) + ", calendar-access, calendar-schedule, calendar-auto-schedule, calendar-default-alarms, calendarserver-sharing";
        if (this.getSysi().getSystemProperties().getTimezonesByReference()) {
            hdr = hdr + ", calendar-no-timezone";
        }
        return hdr;
    }

    @Override
    public ServerInfo getServerInfo() {
        if (serverInfo != null) {
            return serverInfo;
        }
        serverInfo = super.getServerInfo();
        Application app = new Application("caldav");
        app.addFeature(new Feature(CaldavTags.calendarAccess));
        app.addFeature(new Feature(CaldavTags.calendarAutoschedule));
        app.addFeature(new Feature(CaldavTags.calendarDefaultAlarms));
        app.addFeature(new Feature(CaldavTags.calendarNoTimezone));
        app.addFeature(new Feature(AppleServerTags.calendarServerSharing));
        serverInfo.addApplication(app);
        return serverInfo;
    }

    @Override
    public void emitError(QName errorTag, String extra, XmlEmit xml) throws Throwable {
        if (errorTag.equals(CaldavTags.noUidConflict)) {
            xml.openTag(errorTag);
            if (extra != null) {
                xml.property(WebdavTags.href, this.sysi.getUrlHandler().prefix(extra));
            }
            xml.closeTag(errorTag);
        } else {
            super.emitError(errorTag, extra, xml);
        }
    }

    @Override
    public AccessUtil getAccessUtil() throws WebdavException {
        return this.accessUtil;
    }

    @Override
    public boolean canPut(WebdavNsNode node) throws WebdavException {
        CalDAVEvent ev = null;
        if (node instanceof CaldavComponentNode) {
            CaldavComponentNode comp = (CaldavComponentNode)node;
            ev = comp.getEvent();
        } else if (!(node instanceof CaldavResourceNode)) {
            return false;
        }
        if (ev != null) {
            return this.sysi.checkAccess(ev, 8, true).getAccessAllowed();
        }
        return this.sysi.checkAccess(node.getCollection(true), 9, true).getAccessAllowed();
    }

    @Override
    public String getAddMemberSuffix() throws WebdavException {
        return ";add-member";
    }

    @Override
    public boolean getDirectoryBrowsingDisallowed() throws WebdavException {
        return this.sysi.getAuthProperties().getDirectoryBrowsingDisallowed();
    }

    @Override
    public void rollback() {
        this.sysi.rollback();
    }

    @Override
    public void close() throws WebdavException {
        this.sysi.close();
    }

    public SysIntf getSysi() {
        return this.sysi;
    }

    @Override
    public String getSupportedLocks() {
        return null;
    }

    @Override
    public boolean getAccessControl() {
        return true;
    }

    @Override
    public void addNamespace(XmlEmit xml) throws WebdavException {
        try {
            if (this.calWs) {
                xml.addNs(new XmlEmit.NameSpace("http://docs.oasis-open.org/ws-calendar/ns/rest", "CalWS"), true);
                xml.addNs(new XmlEmit.NameSpace("http://www.w3.org/2001/XMLSchema-instance", "xsi"), false);
                xml.addNs(new XmlEmit.NameSpace("http://docs.oasis-open.org/ns/xri/xrd-1.0", "xrd"), false);
                xml.addNs(new XmlEmit.NameSpace("DAV:", "DAV"), false);
                xml.addNs(new XmlEmit.NameSpace("urn:ietf:params:xml:ns:caldav", "C"), false);
                return;
            }
            super.addNamespace(xml);
            xml.addNs(new XmlEmit.NameSpace("urn:ietf:params:xml:ns:caldav", "C"), true);
            xml.addNs(new XmlEmit.NameSpace("http://apple.com/ns/ical/", "AI"), false);
            xml.addNs(new XmlEmit.NameSpace("http://www.w3.org/2002/12/cal/ical#", "ical"), false);
            xml.addNs(new XmlEmit.NameSpace("http://calendarserver.org/ns/", "CS"), false);
            xml.addNs(new XmlEmit.NameSpace("http://bedeworkcalserver.org/ns/", "BSS"), false);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public WebdavNsNode getNode(String uri, int existence, int nodeType, boolean addMember) throws WebdavException {
        return this.getNodeInt(uri, existence, nodeType, addMember, null, null, null);
    }

    @Override
    public void putNode(WebdavNsNode node) throws WebdavException {
    }

    @Override
    public void delete(WebdavNsNode node) throws WebdavException {
        try {
            if (node instanceof CaldavResourceNode) {
                CaldavResourceNode rnode = (CaldavResourceNode)node;
                this.sysi.deleteFile(rnode.getResource());
            } else if (node instanceof CaldavComponentNode) {
                CaldavComponentNode cnode = (CaldavComponentNode)node;
                CalDAVEvent ev = cnode.getEvent();
                if (ev != null) {
                    String userAgent;
                    if (this.debug) {
                        this.debug("About to delete event " + ev);
                    }
                    boolean sendSchedulingMessage = true;
                    if (this.sysi.testMode() && (userAgent = this.getRequest().getHeader("user-agent")) != null && (userAgent.contains("| END_REQUESTS") || userAgent.contains("| START_REQUESTS")) && userAgent.contains("| DELETEALL")) {
                        sendSchedulingMessage = false;
                    }
                    if (!CalDavHeaders.scheduleReply(this.getRequest())) {
                        sendSchedulingMessage = false;
                    }
                    this.sysi.deleteEvent(ev, sendSchedulingMessage);
                } else if (this.debug) {
                    this.debug("No event object available");
                }
            } else {
                String userAgent;
                if (!(node instanceof CaldavCalNode)) {
                    throw new WebdavUnauthorized();
                }
                CaldavCalNode cnode = (CaldavCalNode)node;
                CalDAVCollection col = (CalDAVCollection)cnode.getCollection(false);
                boolean sendSchedulingMessage = true;
                if (this.sysi.testMode() && (userAgent = this.getRequest().getHeader("user-agent")) != null && userAgent.contains("| END_REQUESTS") && userAgent.contains("| DELETEALL")) {
                    sendSchedulingMessage = false;
                }
                this.sysi.deleteCollection(col, sendSchedulingMessage);
            }
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public Collection<WebdavNsNode> getChildren(WebdavNsNode node, Supplier<Object> filterGetter) throws WebdavException {
        try {
            Collection<? extends WdEntity> children;
            ArrayList<WebdavNsNode> al = new ArrayList<WebdavNsNode>();
            if (!node.isCollection()) {
                return al;
            }
            if (this.debug) {
                this.debug("About to get children for " + node.getUri());
            }
            if ((children = node.getChildren(filterGetter)) == null) {
                return al;
            }
            String uri = node.getUri();
            CalDAVCollection parent = (CalDAVCollection)node.getCollection(false);
            for (WdEntity wdEntity : children) {
                int nodeType;
                CalDAVCollection col = null;
                CalDAVResource r = null;
                CalDAVEvent ev = null;
                String name = wdEntity.getName();
                if (wdEntity instanceof CalDAVCollection) {
                    col = (CalDAVCollection)wdEntity;
                    nodeType = 0;
                    if (this.debug) {
                        this.debug("Found child " + col);
                    }
                } else if (wdEntity instanceof CalDAVResource) {
                    col = parent;
                    r = (CalDAVResource)wdEntity;
                    nodeType = 1;
                } else if (wdEntity instanceof CalDAVEvent) {
                    col = parent;
                    ev = (CalDAVEvent)wdEntity;
                    nodeType = 1;
                } else {
                    throw new WebdavException("Unexpected return type");
                }
                al.add(this.getNodeInt(Util.buildPath(false, uri, "/", name), 2, nodeType, false, col, ev, r));
            }
            return al;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public WebdavNsNode getParent(WebdavNsNode node) throws WebdavException {
        return null;
    }

    @Override
    public boolean prefetch(HttpServletRequest req, HttpServletResponse resp, WebdavNsNode node) throws WebdavException {
        if (!super.prefetch(req, resp, node)) {
            return false;
        }
        if (this.calWs) {
            return true;
        }
        if (!(node instanceof CaldavComponentNode)) {
            return true;
        }
        CaldavComponentNode cnode = (CaldavComponentNode)node;
        if (!cnode.getEvent().getOrganizerSchedulingObject() && !cnode.getEvent().getAttendeeSchedulingObject()) {
            return true;
        }
        resp.setHeader("Schedule-Tag", cnode.getStagValue());
        return true;
    }

    @Override
    public WebdavNsIntf.Content getContent(HttpServletRequest req, HttpServletResponse resp, String contentType, WebdavNsNode node) throws WebdavException {
        try {
            WebdavNsIntf.Content c;
            String accept;
            String ctype = contentType;
            if (ctype == null && (accept = req.getHeader("ACCEPT")) != null) {
                ctype = accept.trim();
            }
            if (node.isCollection() && (ctype == null || ctype.contains("text/html"))) {
                if (this.getDirectoryBrowsingDisallowed()) {
                    throw new WebdavException(403);
                }
                c = new WebdavNsIntf.Content();
                String content = this.generateHtml(req, node);
                c.rdr = new CharArrayReader(content.toCharArray());
                c.contentType = "text/html";
                c.contentLength = content.getBytes().length;
                return c;
            }
            if (this.calWs && ctype != null && "application/xrd+xml".equals(ctype)) {
                return this.doXrd(req, resp, (CaldavBwNode)node);
            }
            if (node.isCollection() && ctype != null && "text/calendar".equals(ctype)) {
                WebcalGetHandler handler = new WebcalGetHandler(this);
                RequestPars pars = new RequestPars(req, this, this.getResourceUri(req));
                pars.setWebcalGetAccept(true);
                ((GetHandler)handler).process(req, resp, pars);
                WebdavNsIntf.Content c2 = new WebdavNsIntf.Content();
                c2.written = true;
                return c2;
            }
            if (node.isCollection()) {
                return null;
            }
            if (!node.getAllowsGet()) {
                return null;
            }
            if (ctype == null || !ctype.equals("text/calendar") && !ctype.equals("application/calendar+json") & !ctype.equals("application/calendar+xml")) {
                ctype = this.sysi.getDefaultContentType();
            }
            resp.setContentType(ctype + ";charset=utf-8");
            c = new WebdavNsIntf.Content();
            c.written = true;
            c.contentType = node.writeContent(null, resp.getWriter(), ctype);
            if (c.contentType.indexOf(59) < 0) {
                c.contentType = c.contentType + ";charset=utf-8";
            }
            return c;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public WebdavNsIntf.Content getBinaryContent(WebdavNsNode node) throws WebdavException {
        try {
            if (!node.getAllowsGet()) {
                return null;
            }
            if (!(node instanceof CaldavResourceNode)) {
                throw new WebdavException("Unexpected node type");
            }
            CaldavResourceNode bwnode = (CaldavResourceNode)node;
            WebdavNsIntf.Content c = new WebdavNsIntf.Content();
            c.stream = bwnode.getContentStream();
            c.contentType = node.getContentType();
            c.contentLength = node.getContentLen();
            return c;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public String getAcceptContentType(HttpServletRequest req) throws WebdavException {
        String accept = req.getHeader("Accept");
        if (accept != null) {
            return accept;
        }
        String[] contentTypePars = null;
        String contentType = req.getContentType();
        String ctype = null;
        if (contentType != null) {
            contentTypePars = contentType.split(";");
            ctype = contentTypePars[0];
        }
        if (ctype == null) {
            return ctype;
        }
        return this.sysi.getDefaultContentType();
    }

    @Override
    public WebdavNsIntf.PutContentResult putContent(HttpServletRequest req, HttpServletResponse resp, WebdavNsNode node, String[] contentTypePars, Reader contentRdr, Headers.IfHeaders ifHeaders) throws WebdavException {
        try {
            WebdavNsIntf.PutContentResult pcr = new WebdavNsIntf.PutContentResult();
            pcr.node = node;
            if (node instanceof CaldavResourceNode) {
                throw new WebdavException(412);
            }
            CaldavComponentNode bwnode = (CaldavComponentNode)node;
            CalDAVCollection col = (CalDAVCollection)node.getCollection(true);
            boolean calContent = false;
            if (contentTypePars != null && contentTypePars.length > 0) {
                boolean bl = calContent = contentTypePars[0].equals("text/calendar") || contentTypePars[0].equals("application/calendar+xml") || contentTypePars[0].equals("application/calendar+json");
            }
            if (col.getCalType() != 1 || !calContent) {
                throw new WebdavForbidden(CaldavTags.supportedCalendarData);
            }
            pcr.created = this.putEvent(req, resp, bwnode, contentRdr, contentTypePars[0], ifHeaders);
            return pcr;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public WebdavNsIntf.PutContentResult putBinaryContent(HttpServletRequest req, WebdavNsNode node, String[] contentTypePars, InputStream contentStream, Headers.IfHeaders ifHeaders) throws WebdavException {
        try {
            WebdavNsIntf.PutContentResult pcr = new WebdavNsIntf.PutContentResult();
            pcr.node = node;
            if (!(node instanceof CaldavResourceNode)) {
                throw new WebdavException(412);
            }
            CaldavResourceNode bwnode = (CaldavResourceNode)node;
            CalDAVCollection col = (CalDAVCollection)node.getCollection(true);
            if (col == null || col.getCalType() == 1) {
                throw new WebdavException(412);
            }
            CalDAVResource r = bwnode.getResource();
            if (r.isNew()) {
                ifHeaders.create = true;
            }
            String contentType = null;
            if (contentTypePars != null && contentTypePars.length > 0) {
                for (String c : contentTypePars) {
                    if (c == null) continue;
                    contentType = contentType != null ? contentType + ";" + c : c;
                }
            }
            r.setContentType(contentType);
            r.setBinaryContent(contentStream);
            if (ifHeaders.create) {
                this.sysi.putFile(col, r);
            } else {
                this.sysi.updateFile(r, true);
            }
            return pcr;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private boolean putEvent(HttpServletRequest req, HttpServletResponse resp, CaldavComponentNode bwnode, Reader contentRdr, String contentType, Headers.IfHeaders ifHeaders) throws WebdavException {
        String ifStag = Headers.ifScheduleTagMatch(req);
        boolean noInvites = req.getHeader("Bw-NoInvites") != null;
        String entityName = bwnode.getEntityName();
        CalDAVCollection col = (CalDAVCollection)bwnode.getCollection(true);
        boolean created = false;
        SysiIcalendar cal = this.sysi.fromIcal(col, contentRdr, contentType, SysIntf.IcalResultType.OneComponent, true);
        if (cal.getMethod() != null) {
            throw new WebdavForbidden(CaldavTags.validCalendarObjectResource, "No method on PUT");
        }
        CalDAVEvent ev = (CalDAVEvent)cal.iterator().next();
        ev.setParentPath(col.getPath());
        if (entityName == null) {
            entityName = ev.getUid() + ".ics";
            bwnode.setEntityName(entityName);
        }
        if (this.debug) {
            this.debug("putContent: intf has event with name " + entityName + " and summary " + ev.getSummary() + " new event = " + ev.isNew());
        }
        if (ev.isNew()) {
            created = true;
            ev.setName(entityName);
            this.sysi.addEvent(ev, noInvites, true);
            bwnode.setEvent(ev);
        } else {
            if (ifHeaders.create) {
                throw new WebdavException(412);
            }
            if (!entityName.equals(ev.getName())) {
                throw new WebdavForbidden(CaldavTags.noUidConflict);
            }
            if (ifHeaders.ifEtag != null && !ifHeaders.ifEtag.equals(bwnode.getPrevEtagValue(true))) {
                if (this.debug) {
                    this.debug("putContent: etag mismatch if=" + ifHeaders.ifEtag + "prev=" + bwnode.getPrevEtagValue(true));
                }
                this.rollback();
                throw new WebdavException(412);
            }
            if (ifStag != null && !ifStag.equals(bwnode.getPrevStagValue())) {
                if (this.debug) {
                    this.debug("putContent: stag mismatch if=" + ifStag + "prev=" + bwnode.getPrevStagValue());
                }
                this.rollback();
                throw new WebdavException(412);
            }
            if (this.debug) {
                this.debug("putContent: update event " + ev);
            }
            this.sysi.updateEvent(ev);
            bwnode.setEvent(ev);
        }
        if (ev.getOrganizerSchedulingObject() || ev.getAttendeeSchedulingObject()) {
            resp.setHeader("Schedule-Tag", ev.getScheduleTag());
        }
        return created;
    }

    public boolean putEvent(HttpServletResponse resp, CaldavComponentNode bwnode, IcalendarType ical, boolean create, boolean noInvites, String ifStag, String ifEtag) throws WebdavException {
        String entityName = bwnode.getEntityName();
        CalDAVCollection col = (CalDAVCollection)bwnode.getCollection(true);
        boolean created = false;
        SysiIcalendar cal = this.sysi.fromIcal(col, ical, SysIntf.IcalResultType.OneComponent);
        if (cal.getMethod() != null) {
            throw new WebdavForbidden(CaldavTags.validCalendarObjectResource, "No method on PUT");
        }
        CalDAVEvent ev = (CalDAVEvent)cal.iterator().next();
        ev.setParentPath(col.getPath());
        if (entityName == null) {
            entityName = ev.getUid() + ".ics";
            bwnode.setEntityName(entityName);
        }
        if (this.debug) {
            this.debug("putContent: intf has event with name " + entityName + " and summary " + ev.getSummary() + " new event = " + ev.isNew());
        }
        if (ev.isNew()) {
            created = true;
            ev.setName(entityName);
            this.sysi.addEvent(ev, noInvites, true);
            bwnode.setEvent(ev);
        } else {
            if (create) {
                throw new WebdavException(412);
            }
            if (!entityName.equals(ev.getName())) {
                throw new WebdavForbidden(CaldavTags.noUidConflict);
            }
            if (ifEtag != null && !ifEtag.equals(bwnode.getPrevEtagValue(true))) {
                if (this.debug) {
                    this.debug("putContent: etag mismatch if=" + ifEtag + "prev=" + bwnode.getPrevEtagValue(true));
                }
                this.rollback();
                throw new WebdavException(412);
            }
            if (ifStag != null && !ifStag.equals(bwnode.getPrevStagValue())) {
                if (this.debug) {
                    this.debug("putContent: stag mismatch if=" + ifStag + "prev=" + bwnode.getPrevStagValue());
                }
                this.rollback();
                throw new WebdavException(412);
            }
            if (this.debug) {
                this.debug("putContent: update event " + ev);
            }
            this.sysi.updateEvent(ev);
        }
        if (ev.getOrganizerSchedulingObject() || ev.getAttendeeSchedulingObject()) {
            resp.setHeader("Schedule-Tag", ev.getScheduleTag());
        }
        return created;
    }

    @Override
    public void create(WebdavNsNode node) throws WebdavException {
    }

    @Override
    public void createAlias(WebdavNsNode alias) throws WebdavException {
    }

    @Override
    public void acceptMkcolContent(HttpServletRequest req) throws WebdavException {
        throw new WebdavUnsupportedMediaType();
    }

    @Override
    public void makeCollection(HttpServletRequest req, HttpServletResponse resp, WebdavNsNode node) throws WebdavException {
        try {
            if (!(node instanceof CaldavCalNode)) {
                throw new WebdavBadRequest("Not a valid node object " + node.getClass().getName());
            }
            CaldavCalNode bwnode = (CaldavCalNode)node;
            CalDAVCollection newCol = (CalDAVCollection)bwnode.getCollection(false);
            CalDAVCollection parent = this.getSysi().getCollection(newCol.getParentPath());
            if (parent.getCalType() == 1) {
                throw new WebdavForbidden(CaldavTags.calendarCollectionLocationOk);
            }
            if (newCol.getName() == null) {
                throw new WebdavForbidden("Forbidden: Null name");
            }
            resp.setStatus(this.sysi.makeCollection(newCol));
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public void copyMove(HttpServletRequest req, HttpServletResponse resp, WebdavNsNode from, WebdavNsNode to, boolean copy, boolean overwrite, int depth) throws WebdavException {
        if (from instanceof CaldavCalNode) {
            this.copyMoveCollection(resp, (CaldavCalNode)from, to, copy, overwrite, depth);
            return;
        }
        if (depth != Integer.MIN_VALUE && depth != 0) {
            throw new WebdavBadRequest();
        }
        if (from instanceof CaldavComponentNode) {
            this.copyMoveComponent(resp, (CaldavComponentNode)from, to, copy, overwrite);
            return;
        }
        if (from instanceof CaldavResourceNode) {
            this.copyMoveResource(resp, (CaldavResourceNode)from, to, copy, overwrite);
            return;
        }
        throw new WebdavBadRequest();
    }

    private WebdavNsIntf.Content doXrd(HttpServletRequest req, HttpServletResponse resp, CaldavBwNode node) throws WebdavException {
        resp.setContentType("application/xrd+xml;charset=utf-8");
        try {
            XRDType xrd = this.getXRD(node);
            JAXBContext jc = JAXBContext.newInstance((String)xrd.getClass().getPackage().getName());
            Marshaller m = jc.createMarshaller();
            m.setProperty("jaxb.formatted.output", (Object)Boolean.TRUE);
            m.marshal((Object)xrd, (OutputStream)resp.getOutputStream());
            WebdavNsIntf.Content c = new WebdavNsIntf.Content();
            c.written = true;
            return c;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void getCalWSProperties(CaldavBwNode node, List<GetPropertiesBasePropertyType> props) throws WebdavException {
        for (WebdavNsNode.PropertyTagEntry pte : node.getCalWSSoapNames()) {
            if (!pte.inPropAll) continue;
            node.generateCalWsProperty(props, pte.tag, this, true);
        }
    }

    public XRDType getXRD(CaldavBwNode node) throws WebdavException {
        try {
            XRDType xrd = new XRDType();
            AnyURI uri = new AnyURI();
            uri.setValue(node.getUrlValue());
            xrd.setSubject(uri);
            for (CaldavBwNode.PropertyTagXrdEntry pxe : node.getXrdNames()) {
                if (!pxe.inPropAll) continue;
                node.generateXrdProperties(xrd.getAliasOrPropertyOrLink(), pxe.xrdName, this, true);
            }
            if (node.isCollection()) {
                for (WebdavNsNode child : this.getChildren(node, null)) {
                    CaldavBwNode cn = (CaldavBwNode)child;
                    LinkType l = new LinkType();
                    l.setRel("http://docs.oasis-open.org/ws-calendar/ns/rest/child-collection");
                    l.setHref(cn.getUrlValue());
                    for (CaldavBwNode.PropertyTagXrdEntry pxe : node.getXrdNames()) {
                        if (!pxe.inLink) continue;
                        cn.generateXrdProperties(l.getTitleOrPropertyOrAny(), pxe.xrdName, this, true);
                    }
                }
            }
            return xrd;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void copyMoveCollection(HttpServletResponse resp, CaldavCalNode from, WebdavNsNode to, boolean copy, boolean overwrite, int depth) throws WebdavException {
        if (!(to instanceof CaldavCalNode)) {
            throw new WebdavBadRequest();
        }
        if (depth != Integer.MIN_VALUE && depth != Integer.MAX_VALUE) {
            throw new WebdavBadRequest();
        }
        CaldavCalNode fromCalNode = from;
        CaldavCalNode toCalNode = (CaldavCalNode)to;
        if (toCalNode.getExists() && !overwrite) {
            resp.setStatus(412);
            return;
        }
        CalDAVCollection fromCol = (CalDAVCollection)fromCalNode.getCollection(true);
        CalDAVCollection toCol = (CalDAVCollection)toCalNode.getCollection(true);
        if (fromCol == null || toCol == null) {
            resp.setStatus(412);
            return;
        }
        this.getSysi().copyMove(fromCol, toCol, copy, overwrite);
        if (toCalNode.getExists()) {
            resp.setStatus(204);
        } else {
            resp.setStatus(201);
            Headers.makeLocation(resp, this.getLocation(to));
        }
    }

    private void copyMoveComponent(HttpServletResponse resp, CaldavComponentNode from, WebdavNsNode to, boolean copy, boolean overwrite) throws WebdavException {
        if (!(to instanceof CaldavComponentNode)) {
            throw new WebdavBadRequest();
        }
        CaldavComponentNode toNode = (CaldavComponentNode)to;
        if (toNode.getExists() && !overwrite) {
            resp.setStatus(412);
            return;
        }
        CalDAVCollection toCol = (CalDAVCollection)toNode.getCollection(true);
        if (!this.getSysi().copyMove(from.getEvent(), toCol, toNode.getEntityName(), copy, overwrite)) {
            resp.setStatus(204);
        } else {
            resp.setStatus(201);
            Headers.makeLocation(resp, this.getLocation(to));
        }
    }

    private void copyMoveResource(HttpServletResponse resp, CaldavResourceNode from, WebdavNsNode to, boolean copy, boolean overwrite) throws WebdavException {
        if (!(to instanceof CaldavResourceNode)) {
            throw new WebdavForbidden(CaldavTags.supportedCalendarData);
        }
        CaldavResourceNode toNode = (CaldavResourceNode)to;
        if (toNode.getExists() && !overwrite) {
            resp.setStatus(412);
            return;
        }
        if (!this.getSysi().copyMoveFile(from.getResource(), toNode.getPath(), toNode.getEntityName(), copy, overwrite)) {
            resp.setStatus(204);
        } else {
            resp.setStatus(201);
            Headers.makeLocation(resp, this.getLocation(to));
        }
    }

    @Override
    public boolean specialUri(HttpServletRequest req, HttpServletResponse resp, String resourceUri) throws WebdavException {
        RequestPars pars = new RequestPars(req, this, resourceUri);
        GetHandler handler = null;
        if (pars.isiSchedule()) {
            handler = new IscheduleGetHandler(this);
        } else if (pars.isServerInfo()) {
            handler = new ServerInfoGetHandler(this);
        } else if (pars.isFreeBusy()) {
            handler = new FreeBusyGetHandler(this);
        } else if (pars.isWebcal()) {
            handler = new WebcalGetHandler(this);
        }
        if (handler == null) {
            return false;
        }
        ((GetHandler)handler).process(req, resp, pars);
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public WdSynchReport getSynchReport(String path, String token, int limit, boolean recurse) throws WebdavException {
        SysIntf.SynchReportData srd = this.getSysi().getSyncReport(path, token, limit, recurse);
        if (srd == null) {
            return null;
        }
        WdSynchReport wsr = new WdSynchReport();
        wsr.token = srd.token;
        wsr.truncated = srd.truncated;
        wsr.items = new ArrayList<WdSynchReport.WdSynchReportItem>();
        HashMap<String, WebdavNsNode> parents = new HashMap<String, WebdavNsNode>();
        for (SysIntf.SynchReportData.SynchReportDataItem srdi : srd.items) {
            String name;
            int nodeType;
            boolean canSync;
            CalDAVCollection col = null;
            CalDAVResource r = null;
            CalDAVEvent ev = null;
            WdSynchReport.WdSynchReportItem wri = null;
            WebdavNsNode parent = null;
            if (srdi.getCol() == null) {
                parent = (WebdavNsNode)parents.get(srdi.getVpath());
                if (parent == null) {
                    parent = this.getNode(srdi.getVpath(), 1, 0, false);
                    parents.put(srdi.getVpath(), parent);
                }
                col = (CalDAVCollection)parent.getCollection(false);
                canSync = true;
                if (srdi.getEntity() != null) {
                    nodeType = 1;
                    ev = srdi.getEntity();
                    name = ev.getName();
                } else {
                    if (srdi.getResource() == null) throw new WebdavException("Unexpected return type");
                    nodeType = 1;
                    r = srdi.getResource();
                    name = r.getName();
                }
            } else {
                nodeType = 0;
                col = srdi.getCol();
                name = col.getName();
                canSync = srdi.getCanSync();
            }
            wri = new WdSynchReport.WdSynchReportItem(this.getNodeInt(Util.buildPath(false, srdi.getVpath(), "/", name), 2, nodeType, false, col, ev, r), srdi.getToken(), canSync);
            wsr.items.add(wri);
        }
        return wsr;
    }

    @Override
    public String getSyncToken(String path) throws WebdavException {
        String url = this.sysi.getUrlHandler().unprefix(CaldavBWIntf.fixPath(path));
        CalDAVCollection col = this.getSysi().getCollection(url);
        if (col == null) {
            throw new WebdavException(412, "Bad If header - unknown resource");
        }
        return this.getSysi().getSyncToken(col);
    }

    @Override
    public Collection<WebdavNsNode> getGroups(String resourceUri, String principalUrl) throws WebdavException {
        ArrayList<WebdavNsNode> res = new ArrayList<WebdavNsNode>();
        Collection<String> hrefs = this.getSysi().getGroups(resourceUri, principalUrl);
        for (String href : hrefs) {
            if (href.endsWith("/")) {
                href = href.substring(0, href.length());
            }
            res.add(new CaldavUserNode(new CaldavURI(this.getSysi().getPrincipal(href)), this.getSysi(), null));
        }
        return res;
    }

    @Override
    public Collection<String> getPrincipalCollectionSet(String resourceUri) throws WebdavException {
        ArrayList<String> al = new ArrayList<String>();
        for (String s : this.getSysi().getPrincipalCollectionSet(resourceUri)) {
            al.add(this.sysi.getUrlHandler().prefix(s));
        }
        return al;
    }

    public Collection<WebdavPrincipalNode> getPrincipals(String resourceUri, PrincipalPropertySearch pps) throws WebdavException {
        ArrayList<WebdavPrincipalNode> pnodes = new ArrayList<WebdavPrincipalNode>();
        for (CalPrincipalInfo cui : this.sysi.getPrincipals(resourceUri, pps)) {
            pnodes.add(new CaldavUserNode(new CaldavURI(cui.principal), this.getSysi(), cui));
        }
        return pnodes;
    }

    @Override
    public String makeUserHref(String id) throws WebdavException {
        return this.getSysi().makeHref(id, 1);
    }

    @Override
    public void updateAccess(WebdavNsIntf.AclInfo info) throws WebdavException {
        CaldavBwNode node = (CaldavBwNode)this.getNode(info.what, 1, 3, false);
        this.updateAccess(info, node);
    }

    public void updateAccess(WebdavNsIntf.AclInfo info, CaldavBwNode node) throws WebdavException {
        block5: {
            try {
                if (node instanceof CaldavCalNode) {
                    this.sysi.updateAccess((CalDAVCollection)node.getCollection(false), info.acl);
                    break block5;
                }
                if (node instanceof CaldavComponentNode) {
                    this.sysi.updateAccess(((CaldavComponentNode)node).getEvent(), info.acl);
                    break block5;
                }
                throw new WebdavException(501);
            }
            catch (WebdavException wi) {
                throw wi;
            }
            catch (Throwable t) {
                throw new WebdavException(t);
            }
        }
    }

    @Override
    public void emitAcl(WebdavNsNode node) throws WebdavException {
        Acl acl = null;
        try {
            if (node.isCollection()) {
                acl = node.getCurrentAccess().getAcl();
            } else if (node instanceof CaldavComponentNode) {
                acl = ((CaldavComponentNode)node).getCurrentAccess().getAcl();
            }
            if (acl != null) {
                this.accessUtil.emitAcl(acl, true);
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public Collection<String> getAclPrincipalInfo(WebdavNsNode node) throws WebdavException {
        try {
            TreeSet<String> hrefs = new TreeSet<String>();
            for (Ace ace : node.getCurrentAccess().getAcl().getAces()) {
                AceWho who = ace.getWho();
                if (who.getWhoType() == 1) {
                    hrefs.add(this.accessUtil.makeUserHref(who.getWho()));
                    continue;
                }
                if (who.getWhoType() != 2) continue;
                hrefs.add(this.accessUtil.makeGroupHref(who.getWho()));
            }
            return hrefs;
        }
        catch (AccessException ae) {
            if (this.debug) {
                this.error(ae);
            }
            throw new WebdavServerError();
        }
    }

    @Override
    public WebdavProperty makeProp(Element propnode) throws WebdavException {
        if (!XmlUtil.nodeMatches(propnode, CaldavTags.calendarData)) {
            return super.makeProp(propnode);
        }
        CalData caldata = new CalData(new QName(propnode.getNamespaceURI(), propnode.getLocalName()));
        caldata.parse(propnode);
        return caldata;
    }

    @Override
    public boolean knownProperty(WebdavNsNode node, WebdavProperty pr) {
        QName tag = pr.getTag();
        if (node.knownProperty(tag)) {
            return true;
        }
        for (QName knownProperty : knownProperties) {
            if (!tag.equals(knownProperty)) continue;
            return true;
        }
        return super.knownProperty(node, pr);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean generatePropValue(WebdavNsNode node, WebdavProperty pr, boolean allProp) throws WebdavException {
        QName tag = pr.getTag();
        String ns = tag.getNamespaceURI();
        try {
            if (ns.equals("DAV:")) {
                return super.generatePropValue(node, pr, allProp);
            }
            if (tag.equals(CaldavTags.calendarData)) {
                if (!(pr instanceof CalData)) {
                    pr = new CalData(tag);
                }
                CalData caldata = (CalData)pr;
                if (this.debug) {
                    this.debug("do CalendarData for " + node.getUri());
                }
                String contentType = caldata.getCalendarData().getContentType();
                String[] contentTypePars = null;
                if (contentType != null) {
                    contentTypePars = contentType.split(";");
                }
                String ctype = null;
                if (contentTypePars != null) {
                    ctype = contentTypePars[0];
                }
                int status = 200;
                try {
                    if (ctype != null) {
                        this.xml.openTagNoNewline(CaldavTags.calendarData, "content-type", ctype);
                    } else {
                        this.xml.openTagNoNewline(CaldavTags.calendarData);
                    }
                    caldata.process(node, this.xml, ctype);
                    boolean bl = true;
                    return bl;
                }
                catch (WebdavException wde) {
                    status = wde.getStatusCode();
                    if (this.debug && status != 404) {
                        this.error(wde);
                    }
                    boolean bl = false;
                    return bl;
                }
                finally {
                    this.xml.closeTagNoblanks(CaldavTags.calendarData);
                }
            }
            if (tag.equals(CaldavTags.maxAttendeesPerInstance)) {
                return false;
            }
            if (tag.equals(CaldavTags.maxDateTime)) {
                return false;
            }
            if (tag.equals(CaldavTags.maxInstances)) {
                return false;
            }
            if (tag.equals(CaldavTags.maxResourceSize)) {
                this.xml.property(tag, String.valueOf(this.sysi.getAuthProperties().getMaxUserEntitySize()));
                return true;
            }
            if (!tag.equals(CaldavTags.minDateTime)) return node.generatePropertyValue(tag, this, allProp);
            return false;
        }
        catch (WebdavException wie) {
            throw wie;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public Collection<WebdavNsNode> query(WebdavNsNode wdnode, List<String> retrieveList, RetrievalMode retrieveRecur, FilterType fltr) throws WebdavException {
        CaldavBwNode node = (CaldavBwNode)wdnode;
        FilterHandler fh = new FilterHandler(fltr);
        Collection<CalDAVEvent> events = fh.query(node, retrieveList, retrieveRecur);
        ArrayList<WebdavNsNode> evnodes = new ArrayList<WebdavNsNode>();
        if (events == null) {
            return evnodes;
        }
        try {
            for (CalDAVEvent ev : events) {
                CalDAVCollection col = this.getSysi().getCollection(ev.getParentPath());
                String uri = col.getPath();
                String evName = ev.getName();
                if (evName == null) {
                    evName = ev.getUid() + ".ics";
                }
                String evuri = Util.buildPath(false, uri, "/", evName);
                CaldavComponentNode evnode = (CaldavComponentNode)this.getNodeInt(evuri, 2, 1, false, col, ev, null);
                evnodes.add(evnode);
            }
            return fh.postFilter(evnodes);
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            this.error(t);
            throw new WebdavServerError();
        }
    }

    public void getFreeBusy(CaldavCalNode cnode, FreeBusyQuery freeBusy, int depth) throws WebdavException {
        try {
            CalDAVCollection c = (CalDAVCollection)cnode.getCollection(true);
            if (c == null) {
                return;
            }
            cnode.setFreeBusy(freeBusy.getFreeBusy(this.sysi, c, depth));
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private SysIntf getSysi(String className) throws WebdavException {
        try {
            Object o = Class.forName(className).newInstance();
            if (o == null) {
                throw new WebdavException("Class " + className + " not found");
            }
            if (!SysIntf.class.isInstance(o)) {
                throw new WebdavException("Class " + className + " is not a subclass of " + SysIntf.class.getName());
            }
            return (SysIntf)o;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private WebdavNsNode getNodeInt(String uri, int existance, int nodeType, boolean addMember, CalDAVCollection col, CalDAVEvent ev, CalDAVResource r) throws WebdavException {
        if (this.debug) {
            this.debug("About to get node for " + uri);
        }
        if (uri == null) {
            return null;
        }
        try {
            CaldavURI wi = this.findURI(uri, existance, nodeType, col, ev, r);
            if (wi == null) {
                return null;
            }
            WebdavNsNode nd = null;
            AccessPrincipal ap = wi.getPrincipal();
            if (ap != null) {
                if (ap.getKind() == 1) {
                    nd = new CaldavUserNode(wi, this.sysi, this.sysi.getCalPrincipalInfo(ap));
                } else if (ap.getKind() == 2) {
                    nd = new CaldavGroupNode(wi, this.sysi, this.sysi.getCalPrincipalInfo(ap));
                }
            } else {
                nd = wi.isCollection() ? new CaldavCalNode(wi, this.sysi) : (wi.isResource() ? new CaldavResourceNode(wi, this.sysi) : new CaldavComponentNode(wi, this.sysi));
            }
            return nd;
        }
        catch (WebdavNotFound wnf) {
            throw wnf;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private CaldavURI findURI(String uri, int existance, int nodeType, CalDAVCollection collection, CalDAVEvent ev, CalDAVResource rsrc) throws WebdavException {
        try {
            CalDAVCollection col;
            block26: {
                if (nodeType == 3 && existance != 1) {
                    throw new WebdavServerError();
                }
                if (!(uri = this.normalizeUri(uri)).startsWith("/")) {
                    return null;
                }
                CaldavURI curi = null;
                boolean isPrincipal = this.sysi.isPrincipal(uri);
                if (nodeType == 2 && !isPrincipal) {
                    throw new WebdavNotFound(uri);
                }
                if (isPrincipal) {
                    AccessPrincipal p = this.getSysi().getPrincipal(uri);
                    if (p != null) return new CaldavURI(p);
                    throw new WebdavNotFound(uri);
                }
                if (existance == 2) {
                    if (ev != null) {
                        return new CaldavURI(collection, ev, ev.getName(), true, false);
                    }
                    if (rsrc == null) return new CaldavURI(collection, null, null, true, false);
                    return new CaldavURI(collection, rsrc, true);
                }
                if (this.debug) {
                    this.debug("search for collection uri \"" + uri + "\"");
                }
                col = this.sysi.getCollection(uri);
                if (nodeType == 0 || nodeType == 3) {
                    if (col == null) {
                        if (nodeType == 0 && existance != 0 && existance != 3) {
                            throw new WebdavNotFound(uri);
                        }
                        break block26;
                    } else {
                        if (existance == 0) {
                            if (!this.debug) throw new WebdavForbidden(WebdavTags.resourceMustBeNull);
                            this.debug("collection already exists - col=\"" + col.getPath() + "\"");
                            throw new WebdavForbidden(WebdavTags.resourceMustBeNull);
                        }
                        if (!this.debug) return new CaldavURI(col, true);
                        this.debug("create collection uri - cal=\"" + col.getPath() + "\"");
                        return new CaldavURI(col, true);
                    }
                }
                if (col != null) {
                    throw new WebdavForbidden(WebdavTags.resourceMustBeNull);
                }
            }
            String entityName = null;
            SplitResult split = this.splitUri(uri);
            if (split.name == null) {
                throw new WebdavNotFound(uri);
            }
            String parentPath = split.path;
            entityName = split.name;
            col = this.sysi.getCollection(parentPath);
            if (col == null) {
                if (nodeType != 0) throw new WebdavNotFound(uri);
                throw new WebdavException(409);
            }
            if (nodeType == 0) {
                CalDAVCollection newCol = this.getSysi().newCollectionObject(false, col.getPath());
                newCol.setName(entityName);
                newCol.setPath(Util.buildPath(false, col.getPath(), "/", newCol.getName()));
                return new CaldavURI(newCol, false);
            }
            int ctype = col.getCalType();
            if (ctype == 1 || ctype == 2 || ctype == 3) {
                if (entityName != null) {
                    if (this.debug) {
                        this.debug("find event(s) - cal=\"" + col.getPath() + "\" name=\"" + entityName + "\"");
                    }
                    ev = this.sysi.getEvent(col, entityName);
                    if (existance == 1 && ev == null) {
                        throw new WebdavNotFound(uri);
                    }
                }
                return new CaldavURI(col, ev, entityName, ev != null, entityName == null);
            }
            if (entityName != null) {
                if (this.debug) {
                    this.debug("find resource - cal=\"" + col.getPath() + "\" name=\"" + entityName + "\"");
                }
                rsrc = this.sysi.getFile(col, entityName);
                if (existance == 1 && rsrc == null) {
                    throw new WebdavNotFound(uri);
                }
            }
            boolean exists = rsrc != null;
            if (exists) return new CaldavURI(col, rsrc, exists);
            rsrc = this.getSysi().newResourceObject(col.getPath());
            rsrc.setName(entityName);
            return new CaldavURI(col, rsrc, exists);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private SplitResult splitUri(String uri) throws WebdavException {
        int pos = uri.lastIndexOf("/");
        if (pos < 0) {
            throw new WebdavBadRequest("Invalid uri: " + uri);
        }
        if (pos == 0) {
            return new SplitResult(uri, null);
        }
        return new SplitResult(uri.substring(0, pos), uri.substring(pos + 1));
    }

    static {
        registeredMBeans = new CopyOnWriteArraySet<ObjectName>();
        knownProperties = new QName[]{CaldavTags.calendarData, CaldavTags.calendarTimezone, CaldavTags.maxResourceSize};
    }

    private static class SplitResult {
        String path;
        String name;

        SplitResult(String path, String name) {
            this.path = path;
            this.name = name;
        }
    }

    private static class CalDavAccessXmlCb
    implements AccessXmlUtil.AccessXmlCb,
    Serializable {
        private SysIntf sysi;
        private QName errorTag;
        private String errorMsg;

        CalDavAccessXmlCb(SysIntf sysi) {
            this.sysi = sysi;
        }

        @Override
        public String makeHref(String id, int whoType) throws AccessException {
            try {
                return this.sysi.makeHref(id, whoType);
            }
            catch (Throwable t) {
                throw new AccessException(t);
            }
        }

        @Override
        public AccessPrincipal getPrincipal() throws AccessException {
            try {
                return this.sysi.getPrincipal();
            }
            catch (Throwable t) {
                throw new AccessException(t);
            }
        }

        @Override
        public AccessPrincipal getPrincipal(String href) throws AccessException {
            try {
                return this.sysi.getPrincipal(this.sysi.getUrlHandler().unprefix(href));
            }
            catch (Throwable t) {
                throw new AccessException(t);
            }
        }

        @Override
        public void setErrorTag(QName tag) throws AccessException {
            this.errorTag = tag;
        }

        @Override
        public QName getErrorTag() throws AccessException {
            return this.errorTag;
        }

        @Override
        public void setErrorMsg(String val) throws AccessException {
            this.errorMsg = val;
        }

        @Override
        public String getErrorMsg() throws AccessException {
            return this.errorMsg;
        }
    }
}

