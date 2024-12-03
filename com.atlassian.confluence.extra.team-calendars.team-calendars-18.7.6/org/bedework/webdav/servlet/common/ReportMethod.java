/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.webdav.servlet.common;

import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.Headers;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.common.PrincipalMatchReport;
import org.bedework.webdav.servlet.common.PropFindMethod;
import org.bedework.webdav.servlet.shared.PrincipalPropertySearch;
import org.bedework.webdav.servlet.shared.WdSynchReport;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReportMethod
extends MethodBase {
    private static final int reportTypeExpandProperty = 0;
    private static final int reportTypePrincipalPropertySearch = 1;
    private static final int reportTypePrincipalMatch = 2;
    private static final int reportTypeAclPrincipalPropSet = 3;
    private static final int reportTypePrincipalSearchPropertySet = 4;
    private static final int reportTypeSync = 5;
    private int reportType;
    private PrincipalMatchReport pmatch;
    private PrincipalPropertySearch pps;
    protected PropFindMethod.PropRequest preq;
    protected PropFindMethod pm;
    private PropFindMethod.PropRequest propReq;
    private String syncToken;
    private int syncLevel;
    private int syncLimit;
    private boolean syncRecurse;

    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        if (this.debug) {
            this.debug("ReportMethod: doMethod");
        }
        this.pm = new PropFindMethod();
        this.pm.init(this.getNsIntf(), true);
        Document doc = this.parseContent(req, resp);
        if (doc == null) {
            return;
        }
        int depth = Headers.depth(req, 0);
        if (this.debug) {
            this.debug("ReportMethod: depth=" + depth);
        }
        this.process(doc, depth, req, resp);
    }

    protected void process(Document doc, int depth, HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        this.reportType = this.getReportType(doc);
        if (this.reportType < 0) {
            throw new WebdavBadRequest();
        }
        this.processDoc(doc, depth);
        this.processResp(req, resp, depth);
    }

    protected void doNodeProperties(WebdavNsNode node) throws WebdavException {
        int status = node.getStatus();
        this.openTag(WebdavTags.response);
        if (status != 200) {
            node.generateHref(this.xml);
            this.addStatus(status, null);
        } else {
            this.pm.doNodeProperties(node, this.preq);
        }
        this.closeTag(WebdavTags.response);
        this.flush();
    }

    private void processDoc(Document doc, int depth) throws WebdavException {
        try {
            WebdavNsIntf intf = this.getNsIntf();
            Element root = doc.getDocumentElement();
            if (this.reportType == 5) {
                this.parseSyncReport(root, depth, intf);
                return;
            }
            if (this.reportType == 3) {
                depth = this.defaultDepth(depth, 0);
                this.checkDepth(depth, 0);
                this.parseAclPrincipalProps(root, intf);
                return;
            }
            if (this.reportType == 0) {
                return;
            }
            if (this.reportType == 4) {
                return;
            }
            if (this.reportType == 2) {
                depth = this.defaultDepth(depth, 0);
                this.checkDepth(depth, 0);
                this.pmatch = new PrincipalMatchReport(this, intf);
                this.pmatch.parse(root, depth);
                return;
            }
            if (this.reportType == 1) {
                depth = this.defaultDepth(depth, 0);
                this.checkDepth(depth, 0);
                this.parsePrincipalPropertySearch(root, depth, intf);
                return;
            }
            throw new WebdavBadRequest();
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            System.err.println(t.getMessage());
            if (this.debug) {
                t.printStackTrace();
            }
            throw new WebdavException(500);
        }
    }

    private void parseAclPrincipalProps(Element root, WebdavNsIntf intf) throws WebdavException {
        try {
            Element[] children = this.getChildrenArray(root);
            boolean hadProp = false;
            for (int i = 0; i < children.length; ++i) {
                Element curnode = children[i];
                if (!XmlUtil.nodeMatches(curnode, WebdavTags.prop)) continue;
                if (hadProp) {
                    throw new WebdavBadRequest("More than one DAV:prop element");
                }
                this.propReq = this.pm.parseProps(curnode);
                hadProp = true;
            }
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            System.err.println(t.getMessage());
            if (this.debug) {
                t.printStackTrace();
            }
            throw new WebdavException(500);
        }
    }

    private void parseSyncReport(Element root, int depth, WebdavNsIntf intf) throws WebdavException {
        try {
            Element[] children = this.getChildrenArray(root);
            if (children.length < 2 || children.length > 4) {
                throw new WebdavBadRequest("Expect 2 - 4 child elements");
            }
            if (!XmlUtil.nodeMatches(children[0], WebdavTags.syncToken)) {
                throw new WebdavBadRequest("Expect " + WebdavTags.syncToken);
            }
            this.syncToken = XmlUtil.getOneNodeVal(children[0]);
            int childI = 1;
            this.syncLimit = -1;
            if (XmlUtil.nodeMatches(children[1], WebdavTags.synclevel)) {
                String lvl = XmlUtil.getElementContent(children[1]);
                if (lvl.equals("1")) {
                    this.syncLevel = 1;
                } else if (lvl.equals("infinity")) {
                    this.syncLevel = Integer.MAX_VALUE;
                } else {
                    throw new WebdavBadRequest("Bad sync-level " + lvl);
                }
                ++childI;
            } else {
                if (depth != Integer.MAX_VALUE && depth != 1) {
                    throw new WebdavBadRequest("Bad depth");
                }
                this.syncLevel = depth;
            }
            boolean bl = this.syncRecurse = this.syncLevel == Integer.MAX_VALUE;
            if (XmlUtil.nodeMatches(children[childI], WebdavTags.limit)) {
                this.syncLimit = Integer.valueOf(XmlUtil.getElementContent(children[childI]));
                ++childI;
            }
            if (!XmlUtil.nodeMatches(children[childI], WebdavTags.prop)) {
                throw new WebdavBadRequest("Expect " + WebdavTags.prop);
            }
            this.propReq = this.pm.parseProps(children[childI]);
        }
        catch (NumberFormatException nfe) {
            throw new WebdavBadRequest("Invalid value");
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            System.err.println(t.getMessage());
            if (this.debug) {
                t.printStackTrace();
            }
            throw new WebdavException(500);
        }
    }

    private void parsePrincipalPropertySearch(Element root, int depth, WebdavNsIntf intf) throws WebdavException {
        try {
            Element[] children = this.getChildrenArray(root);
            this.pps = new PrincipalPropertySearch();
            for (int i = 0; i < children.length; ++i) {
                Element curnode = children[i];
                if (XmlUtil.nodeMatches(curnode, WebdavTags.propertySearch)) {
                    Element[] pschildren = this.getChildrenArray(curnode);
                    if (pschildren.length != 2) {
                        throw new WebdavBadRequest();
                    }
                    String match = XmlUtil.getElementContent(pschildren[1]);
                    List<WebdavProperty> props = intf.parseProp(pschildren[0]);
                    if (Util.isEmpty(props)) continue;
                    for (WebdavProperty wd : props) {
                        wd.setPval(match);
                        this.pps.props.add(wd);
                    }
                    continue;
                }
                if (!XmlUtil.nodeMatches(curnode, WebdavTags.prop)) continue;
                this.preq = this.pps.pr = this.pm.parseProps(curnode);
                if (++i < children.length) {
                    if (!XmlUtil.nodeMatches(children[i], WebdavTags.applyToPrincipalCollectionSet)) {
                        throw new WebdavBadRequest();
                    }
                    this.pps.applyToPrincipalCollectionSet = true;
                    ++i;
                }
                if (i < children.length) {
                    throw new WebdavBadRequest();
                }
                break;
            }
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            System.err.println(t.getMessage());
            if (this.debug) {
                t.printStackTrace();
            }
            throw new WebdavException(500);
        }
    }

    private void processResp(HttpServletRequest req, HttpServletResponse resp, int depth) throws WebdavException {
        WebdavNsIntf intf = this.getNsIntf();
        if (this.reportType == 5) {
            this.processSyncReport(req, resp, intf);
            return;
        }
        if (this.reportType == 3) {
            this.processAclPrincipalPropSet(req, resp, intf);
            return;
        }
        if (this.reportType == 4) {
            return;
        }
        if (this.reportType == 0) {
            this.processExpandProperty(req, resp, depth, intf);
            return;
        }
        if (this.reportType == 2) {
            this.pmatch.process(req, resp, this.defaultDepth(depth, 0));
            return;
        }
        if (this.reportType == 1) {
            this.processPrincipalPropertySearch(req, resp, this.defaultDepth(depth, 0), intf);
            return;
        }
        throw new WebdavBadRequest();
    }

    private void processExpandProperty(HttpServletRequest req, HttpServletResponse resp, int depth, WebdavNsIntf intf) throws WebdavException {
        resp.setStatus(207);
        resp.setContentType("text/xml; charset=UTF-8");
        this.startEmit(resp);
        this.openTag(WebdavTags.multistatus);
        this.closeTag(WebdavTags.multistatus);
        this.flush();
    }

    private void processSyncReport(HttpServletRequest req, HttpServletResponse resp, WebdavNsIntf intf) throws WebdavException {
        WdSynchReport wsr = intf.getSynchReport(this.getResourceUri(req), this.syncToken, this.syncLimit, this.syncRecurse);
        if (wsr == null) {
            resp.setStatus(404);
            return;
        }
        resp.setStatus(207);
        resp.setContentType("text/xml; charset=UTF-8");
        this.startEmit(resp);
        this.openTag(WebdavTags.multistatus);
        if (!Util.isEmpty(wsr.items)) {
            for (WdSynchReport.WdSynchReportItem wsri : wsr.items) {
                this.openTag(WebdavTags.response);
                if (wsri.getCanSync()) {
                    if (wsri.getNode().getDeleted()) {
                        wsri.getNode().generateHref(this.xml);
                        this.addStatus(404, null);
                    } else {
                        this.pm.doNodeProperties(wsri.getNode(), this.propReq);
                    }
                } else {
                    wsri.getNode().generateHref(this.xml);
                    this.addStatus(403, null);
                    this.propertyTagVal(WebdavTags.error, WebdavTags.syncTraversalSupported);
                }
                this.closeTag(WebdavTags.response);
            }
        }
        this.property(WebdavTags.syncToken, wsr.token);
        this.closeTag(WebdavTags.multistatus);
        this.flush();
    }

    private void processAclPrincipalPropSet(HttpServletRequest req, HttpServletResponse resp, WebdavNsIntf intf) throws WebdavException {
        String resourceUri = this.getResourceUri(req);
        WebdavNsNode node = intf.getNode(resourceUri, 1, 3, false);
        Collection<String> hrefs = intf.getAclPrincipalInfo(node);
        resp.setStatus(207);
        resp.setContentType("text/xml; charset=UTF-8");
        this.startEmit(resp);
        this.openTag(WebdavTags.multistatus);
        if (!hrefs.isEmpty()) {
            this.openTag(WebdavTags.response);
            for (String href : hrefs) {
                WebdavNsNode pnode = this.getNsIntf().getNode(this.getNsIntf().getUri(href), 3, 2, false);
                if (pnode == null) continue;
                this.pm.doNodeProperties(pnode, this.propReq);
            }
            this.closeTag(WebdavTags.response);
        }
        this.closeTag(WebdavTags.multistatus);
        this.flush();
    }

    private void processPrincipalPropertySearch(HttpServletRequest req, HttpServletResponse resp, int depth, WebdavNsIntf intf) throws WebdavException {
        resp.setStatus(207);
        resp.setContentType("text/xml; charset=UTF-8");
        this.startEmit(resp);
        String resourceUri = this.getResourceUri(req);
        Collection<? extends WebdavNsNode> principals = intf.getPrincipals(resourceUri, this.pps);
        this.openTag(WebdavTags.multistatus);
        for (WebdavNsNode webdavNsNode : principals) {
            this.doNodeProperties(webdavNsNode);
        }
        this.closeTag(WebdavTags.multistatus);
        this.flush();
    }

    private int getReportType(Document doc) throws WebdavException {
        try {
            Element root = doc.getDocumentElement();
            if (XmlUtil.nodeMatches(root, WebdavTags.expandProperty)) {
                return 0;
            }
            if (XmlUtil.nodeMatches(root, WebdavTags.syncCollection)) {
                return 5;
            }
            if (XmlUtil.nodeMatches(root, WebdavTags.principalPropertySearch)) {
                return 1;
            }
            if (XmlUtil.nodeMatches(root, WebdavTags.principalMatch)) {
                return 2;
            }
            if (XmlUtil.nodeMatches(root, WebdavTags.aclPrincipalPropSet)) {
                return 3;
            }
            if (XmlUtil.nodeMatches(root, WebdavTags.principalSearchPropertySet)) {
                return 4;
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
}

