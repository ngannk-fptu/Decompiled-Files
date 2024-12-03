/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.caldav.server;

import ietf.params.xml.ns.caldav.CalendarDataType;
import ietf.params.xml.ns.caldav.CompType;
import ietf.params.xml.ns.caldav.ExpandType;
import ietf.params.xml.ns.caldav.FilterType;
import ietf.params.xml.ns.caldav.LimitRecurrenceSetType;
import ietf.params.xml.ns.caldav.PropType;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import net.fortuna.ical4j.model.TimeZone;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.CaldavCalNode;
import org.bedework.caldav.server.CaldavComponentNode;
import org.bedework.caldav.server.SysiIcalendar;
import org.bedework.caldav.server.calquery.CalData;
import org.bedework.caldav.server.calquery.FreeBusyQuery;
import org.bedework.caldav.server.sysinterface.RetrievalMode;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.DumpUtil;
import org.bedework.caldav.util.filter.parse.EventQuery;
import org.bedework.caldav.util.filter.parse.Filters;
import org.bedework.util.misc.Util;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.PropFindMethod;
import org.bedework.webdav.servlet.common.ReportMethod;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CaldavReportMethod
extends ReportMethod {
    private FreeBusyQuery freeBusy;
    protected CalendarQueryPars cqpars;
    private ArrayList<String> hrefs;
    private static final int reportTypeQuery = 0;
    private static final int reportTypeMultiGet = 1;
    private static final int reportTypeFreeBusy = 2;
    private int reportType;

    @Override
    public void init() {
    }

    @Override
    protected void process(Document doc, int depth, HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        this.reportType = this.getCaldavReportType(doc);
        if (this.reportType < 0) {
            super.process(doc, depth, req, resp);
            return;
        }
        this.processDoc(doc);
        if (this.reportType == 2) {
            this.processFbResp(req, resp, depth);
        } else {
            this.processResp(req, resp, depth);
        }
    }

    protected int getCaldavReportType(Document doc) throws WebdavException {
        try {
            Element root = doc.getDocumentElement();
            if (XmlUtil.nodeMatches(root, CaldavTags.calendarQuery)) {
                return 0;
            }
            if (XmlUtil.nodeMatches(root, CaldavTags.calendarMultiget)) {
                return 1;
            }
            if (XmlUtil.nodeMatches(root, CaldavTags.freeBusyQuery)) {
                return 2;
            }
            return -1;
        }
        catch (Throwable t) {
            System.err.println(t.getMessage());
            if (this.debug) {
                t.printStackTrace();
            }
            throw new WebdavException(500);
        }
    }

    protected void processDoc(Document doc) throws WebdavException {
        try {
            CaldavBWIntf intf = (CaldavBWIntf)this.getNsIntf();
            Element root = doc.getDocumentElement();
            if (this.reportType == 2) {
                this.freeBusy = new FreeBusyQuery();
                this.freeBusy.parse(this.getOnlyChild(root));
                if (this.debug) {
                    this.debug("REPORT: free-busy");
                    this.freeBusy.dump();
                }
                return;
            }
            Collection<Element> children = this.getChildren(root);
            if (children.isEmpty()) {
                throw new WebdavBadRequest();
            }
            Iterator<Element> chiter = children.iterator();
            Element curnode = chiter.next();
            if (this.reportType == 0) {
                this.preq = this.pm.tryPropRequest(curnode);
                if (this.preq != null) {
                    if (!chiter.hasNext()) {
                        throw new WebdavBadRequest();
                    }
                    curnode = chiter.next();
                }
                this.cqpars = new CalendarQueryPars();
                if (!XmlUtil.nodeMatches(curnode, CaldavTags.filter)) {
                    throw new WebdavForbidden(CaldavTags.validFilter, "Expected filter");
                }
                this.cqpars.filter = Filters.parse(curnode);
                if (this.debug) {
                    this.debug("REPORT: query");
                    DumpUtil.dumpFilter(this.cqpars.filter, this.getLogger());
                }
                curnode = chiter.hasNext() ? chiter.next() : null;
                if (this.preq == null && curnode != null) {
                    this.preq = this.pm.tryPropRequest(curnode);
                    curnode = this.preq != null && chiter.hasNext() ? chiter.next() : null;
                }
                if (curnode != null) {
                    SysiIcalendar ical;
                    if (intf.getSysi().getSystemProperties().getTimezonesByReference() && XmlUtil.nodeMatches(curnode, CaldavTags.timezoneId)) {
                        this.cqpars.tzid = this.getElementContent(curnode);
                        TimeZone tz = Timezones.getTz(this.cqpars.tzid);
                        if (tz == null) {
                            throw new WebdavForbidden(CaldavTags.validTimezone, "Unknown timezone " + this.cqpars.tzid);
                        }
                        return;
                    }
                    if (!XmlUtil.nodeMatches(curnode, CaldavTags.timezone)) {
                        throw new WebdavForbidden(CaldavTags.validTimezone, "Missing timezone");
                    }
                    String tzdef = this.getElementContent(curnode);
                    try {
                        ical = intf.getSysi().fromIcal(null, new StringReader(tzdef), "text/calendar", SysIntf.IcalResultType.TimeZone, false);
                    }
                    catch (Throwable t) {
                        throw new WebdavForbidden(CaldavTags.validCalendarData, t.getLocalizedMessage());
                    }
                    Collection<TimeZone> tzs = ical.getTimeZones();
                    this.cqpars.tzid = tzs.iterator().next().getID();
                }
                return;
            }
            if (this.reportType == 1) {
                this.preq = this.pm.tryPropRequest(curnode);
                if (this.preq != null) {
                    if (!chiter.hasNext()) {
                        throw new WebdavBadRequest();
                    }
                    curnode = chiter.next();
                }
                while (true) {
                    if (!XmlUtil.nodeMatches(curnode, WebdavTags.href)) {
                        throw new WebdavBadRequest("Expected href");
                    }
                    String href = XmlUtil.getElementContent(curnode);
                    if (href != null) {
                        String decoded;
                        try {
                            decoded = URLDecoder.decode(href, "UTF8");
                        }
                        catch (Throwable t) {
                            throw new WebdavBadRequest("bad href: " + href);
                        }
                        href = decoded;
                    }
                    if (href == null || href.length() == 0) {
                        throw new WebdavBadRequest("Bad href");
                    }
                    if (this.hrefs == null) {
                        this.hrefs = new ArrayList();
                    }
                    this.hrefs.add(href);
                    if (!chiter.hasNext()) break;
                    curnode = chiter.next();
                }
                if (this.hrefs == null) {
                    throw new WebdavBadRequest("Expected href");
                }
                if (this.debug) {
                    this.debug("REPORT: multi-get");
                    for (String href : this.hrefs) {
                        this.debug("    <DAV:href>" + href + "</DAV:href>");
                    }
                }
                return;
            }
            if (this.debug) {
                this.debug("REPORT: unexpected element " + curnode.getNodeName() + " with type " + curnode.getNodeType());
            }
            throw new WebdavBadRequest("REPORT: unexpected element " + curnode.getNodeName() + " with type " + curnode.getNodeType());
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            this.error(t);
            throw new WebdavException(500);
        }
    }

    public void processResp(HttpServletRequest req, HttpServletResponse resp, int depth) throws WebdavException {
        resp.setStatus(207);
        resp.setContentType("text/xml;charset=utf-8");
        this.startEmit(resp);
        String resourceUri = this.getResourceUri(req);
        if (this.reportType == 0) {
            this.cqpars.depth = depth;
        }
        this.process(this.cqpars, resourceUri);
    }

    protected void process(CalendarQueryPars cqp, String resourceUri) throws WebdavException {
        CaldavBWIntf intf = (CaldavBWIntf)this.getNsIntf();
        WebdavNsNode node = intf.getNode(resourceUri, 1, 3, false);
        int status = 200;
        Collection<WebdavNsNode> nodes = null;
        ArrayList<String> badHrefs = new ArrayList<String>();
        if (this.reportType == 0) {
            nodes = this.doNodeAndChildren(cqp, node);
        } else if (this.reportType == 1) {
            nodes = this.getMgetNodes(this.hrefs, badHrefs);
        }
        this.openTag(WebdavTags.multistatus);
        if (status != 200) {
            if (this.debug) {
                this.debug("REPORT status " + status);
            }
            node.setStatus(status);
            this.doNodeProperties(node);
        } else if (nodes != null) {
            for (WebdavNsNode curnode : nodes) {
                this.doNodeProperties(curnode);
            }
        }
        if (!Util.isEmpty(badHrefs)) {
            for (String hr : badHrefs) {
                this.openTag(WebdavTags.response);
                this.property(WebdavTags.href, intf.getSysi().getUrlHandler().prefix(hr));
                this.property(WebdavTags.status, "HTTP/1.1 404");
                this.closeTag(WebdavTags.response);
            }
        }
        this.closeTag(WebdavTags.multistatus);
        this.flush();
    }

    public Collection<WebdavNsNode> getMgetNodes(Collection<String> hrefs, Collection<String> badHrefs) throws WebdavException {
        ArrayList<WebdavNsNode> nodes = new ArrayList<WebdavNsNode>();
        CaldavBWIntf intf = (CaldavBWIntf)this.getNsIntf();
        if (hrefs == null) {
            return nodes;
        }
        for (String hr : hrefs) {
            WebdavNsNode n = null;
            try {
                n = intf.getNode(intf.getUri(hr), 1, 3, false);
            }
            catch (WebdavException we) {
                n = hr.endsWith("/") ? new CaldavCalNode(intf.getSysi(), we.getStatusCode(), intf.getUri(hr)) : new CaldavComponentNode(intf.getSysi(), we.getStatusCode(), intf.getUri(hr));
            }
            if (n != null) {
                nodes.add(n);
                continue;
            }
            badHrefs.add(hr);
        }
        return nodes;
    }

    protected Collection<WebdavNsNode> doNodeAndChildren(CalendarQueryPars cqp, WebdavNsNode node) throws WebdavException {
        ArrayList<String> retrieveList = null;
        CalData caldata = null;
        if (this.preq != null) {
            if (this.debug) {
                this.debug("REPORT: preq not null");
            }
            if (this.preq.reqType == PropFindMethod.PropRequest.ReqType.prop) {
                for (WebdavProperty prop : this.preq.props) {
                    if (retrieveList == null) {
                        retrieveList = new ArrayList<String>();
                    }
                    if (prop instanceof CalData) {
                        caldata = (CalData)prop;
                        continue;
                    }
                    if (this.addPropname(prop.getTag(), retrieveList)) continue;
                    retrieveList = null;
                    break;
                }
            }
        }
        CompType comp = null;
        ExpandType expand = null;
        LimitRecurrenceSetType lrs = null;
        if (caldata != null) {
            CalendarDataType cd = caldata.getCalendarData();
            comp = cd.getComp();
            expand = cd.getExpand();
            lrs = cd.getLimitRecurrenceSet();
        }
        if (comp == null) {
            if (caldata != null) {
                retrieveList = null;
            }
        } else if (comp.getAllcomp() != null) {
            retrieveList = null;
        } else if (comp.getName().toUpperCase().equals("VCALENDAR")) {
            if (comp.getComp().isEmpty()) {
                retrieveList = null;
            } else {
                for (CompType calcomp : comp.getComp()) {
                    String nm = calcomp.getName().toUpperCase();
                    if (!nm.equals("VEVENT") && !nm.equals("VTODO") && !nm.equals("VJOURNAL") && !nm.equals("VAVAILABILITY")) continue;
                    if (calcomp.getAllprop() != null || Util.isEmpty(calcomp.getProp())) {
                        retrieveList = null;
                        break;
                    }
                    if (retrieveList == null) {
                        retrieveList = new ArrayList();
                    }
                    for (PropType p : calcomp.getProp()) {
                        if (retrieveList.contains(p.getName())) continue;
                        retrieveList.add(p.getName());
                    }
                }
            }
        }
        if (Util.isEmpty(retrieveList)) {
            retrieveList = null;
        }
        return this.doNodeAndChildren(cqp, node, expand, lrs, retrieveList);
    }

    protected Collection<WebdavNsNode> doNodeAndChildren(CalendarQueryPars cqp, WebdavNsNode node, ExpandType expand, LimitRecurrenceSetType lrs, List<String> retrieveList) throws WebdavException {
        RetrievalMode rm = null;
        if (expand != null) {
            rm = new RetrievalMode();
            rm.setExpand(expand);
        } else if (lrs != null) {
            rm = new RetrievalMode();
            rm.setLimitRecurrenceSet(lrs);
        }
        return this.doNodeAndChildren(cqp, node, 0, this.defaultDepth(cqp.depth, 0), rm, retrieveList);
    }

    private Collection<WebdavNsNode> doNodeAndChildren(CalendarQueryPars cqp, WebdavNsNode node, int curDepth, int maxDepth, RetrievalMode rm, List<String> retrieveList) throws WebdavException {
        if (this.debug) {
            this.debug("doNodeAndChildren: curDepth=" + curDepth + " maxDepth=" + maxDepth + " uri=" + node.getUri());
        }
        ArrayList<WebdavNsNode> nodes = new ArrayList<WebdavNsNode>();
        if (node instanceof CaldavComponentNode) {
            nodes.add(node);
            return nodes;
        }
        if (!(node instanceof CaldavCalNode)) {
            throw new WebdavBadRequest();
        }
        CaldavCalNode calnode = (CaldavCalNode)node;
        if (++curDepth > maxDepth) {
            return nodes;
        }
        if (calnode.isCalendarCollection()) {
            return this.getNodes(cqp, node, rm, retrieveList);
        }
        EventQuery eq = cqp.filter == null ? null : Filters.getQuery(cqp.filter);
        Supplier<Object> filters = () -> {
            if (eq == null) {
                return null;
            }
            return eq.filter;
        };
        for (WebdavNsNode child : this.getNsIntf().getChildren(node, filters)) {
            nodes.addAll(this.doNodeAndChildren(cqp, child, curDepth, maxDepth, rm, retrieveList));
        }
        return nodes;
    }

    private Collection<WebdavNsNode> getNodes(CalendarQueryPars cqp, WebdavNsNode node, RetrievalMode rm, List<String> retrieveList) throws WebdavException {
        if (this.debug) {
            this.debug("getNodes: " + node.getUri());
        }
        CaldavBWIntf intf = (CaldavBWIntf)this.getNsIntf();
        return intf.query(node, retrieveList, rm, cqp.filter);
    }

    private boolean addPropname(QName tag, List<String> retrieveList) {
        if (tag.equals(WebdavTags.getetag)) {
            retrieveList.add(tag.toString());
            return true;
        }
        return false;
    }

    public void processFbResp(HttpServletRequest req, HttpServletResponse resp, int depth) throws WebdavException {
        resp.setStatus(200);
        resp.setContentType("text/calendar;charset=utf-8");
        String resourceUri = this.getResourceUri(req);
        CaldavBWIntf intf = (CaldavBWIntf)this.getNsIntf();
        WebdavNsNode node = intf.getNode(resourceUri, 1, 0, false);
        if (!(node instanceof CaldavCalNode)) {
            if (this.debug) {
                this.debug("Expected CaldavCalNode - got " + node);
            }
            throw new WebdavBadRequest();
        }
        CaldavCalNode cnode = (CaldavCalNode)node;
        intf.getFreeBusy(cnode, this.freeBusy, this.defaultDepth(depth, 0));
        resp.setContentLength(-1);
        try {
            cnode.writeContent(null, resp.getWriter(), "text/calendar");
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected static class CalendarQueryPars {
        public FilterType filter;
        public String tzid;
        public int depth;
    }
}

