/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.webdav.servlet.common;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.Headers;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PropFindMethod
extends MethodBase {
    private PropRequest parsedReq;

    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        int depth;
        Document doc;
        if (this.debug) {
            this.debug("PropFindMethod: doMethod");
        }
        if ((doc = this.parseContent(req, resp)) == null) {
            this.parsedReq = new PropRequest(PropRequest.ReqType.propAll);
        }
        if (doc != null) {
            this.processDoc(doc);
        }
        if ((depth = Headers.depth(req)) == Integer.MIN_VALUE) {
            depth = Integer.MAX_VALUE;
        }
        if (this.parsedReq == null) {
            throw new WebdavBadRequest("PROPFIND: unexpected element");
        }
        if (this.debug) {
            this.debug("PropFindMethod: depth=" + depth);
            this.debug("                type=" + (Object)((Object)this.parsedReq.reqType));
        }
        this.processResp(req, resp, depth);
    }

    private void processDoc(Document doc) throws WebdavException {
        try {
            Element root = doc.getDocumentElement();
            if (!XmlUtil.nodeMatches(root, WebdavTags.propfind)) {
                throw new WebdavBadRequest();
            }
            Element curnode = this.getOnlyChild(root);
            String ns = curnode.getNamespaceURI();
            this.addNs(ns);
            if (this.debug) {
                String nm = curnode.getLocalName();
                this.debug("reqtype: " + nm + " ns: " + ns);
            }
            this.parsedReq = this.tryPropRequest(curnode);
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

    public PropRequest tryPropRequest(Node nd) throws WebdavException {
        if (XmlUtil.nodeMatches(nd, WebdavTags.allprop)) {
            return new PropRequest(PropRequest.ReqType.propAll);
        }
        if (XmlUtil.nodeMatches(nd, WebdavTags.prop)) {
            return this.parseProps(nd);
        }
        if (XmlUtil.nodeMatches(nd, WebdavTags.propname)) {
            return new PropRequest(PropRequest.ReqType.propName);
        }
        return null;
    }

    public PropRequest parseProps(Node nd) throws WebdavException {
        PropRequest pr = new PropRequest(PropRequest.ReqType.prop);
        pr.props = this.getNsIntf().parseProp(nd);
        return pr;
    }

    public void processResp(HttpServletRequest req, HttpServletResponse resp, int depth) throws WebdavException {
        resp.setStatus(207);
        resp.setContentType("text/xml; charset=UTF-8");
        this.startEmit(resp);
        String resourceUri = this.getResourceUri(req);
        if (this.debug) {
            this.debug("About to get node at " + resourceUri);
        }
        WebdavNsNode node = this.getNsIntf().getNode(resourceUri, 1, 3, false);
        this.addHeaders(req, resp, node);
        this.openTag(WebdavTags.multistatus);
        if (node == null) {
            this.openTag(WebdavTags.response);
            this.property(WebdavTags.href, resourceUri);
            this.addStatus(404, null);
            this.closeTag(WebdavTags.response);
        } else {
            this.doNodeAndChildren(node, 0, depth);
        }
        this.closeTag(WebdavTags.multistatus);
        this.flush();
    }

    public void doNodeProperties(WebdavNsNode node, PropRequest pr) throws WebdavException {
        node.generateHref(this.xml);
        if (pr == null || Util.isEmpty(pr.props) || !node.getExists()) {
            this.openTag(WebdavTags.propstat);
            this.addStatus(node.getStatus(), null);
            this.closeTag(WebdavTags.propstat);
            return;
        }
        if (pr.reqType == PropRequest.ReqType.propName || pr.reqType == PropRequest.ReqType.propAll) {
            this.openTag(WebdavTags.propstat);
            if (this.debug) {
                this.debug("doNodeProperties type=" + (Object)((Object)pr.reqType));
            }
            if (pr.reqType == PropRequest.ReqType.propName) {
                this.doPropNames(node);
            } else if (pr.reqType == PropRequest.ReqType.propAll) {
                this.doPropAll(node);
            }
            this.addStatus(node.getStatus(), null);
            this.closeTag(WebdavTags.propstat);
            return;
        }
        if (pr.reqType != PropRequest.ReqType.prop) {
            throw new WebdavBadRequest();
        }
        this.doPropFind(node, pr.props);
    }

    private void doNodeAndChildren(WebdavNsNode node, int curDepth, int maxDepth) throws WebdavException {
        this.openTag(WebdavTags.response);
        this.doNodeProperties(node, this.parsedReq);
        this.closeTag(WebdavTags.response);
        this.flush();
        if (++curDepth > maxDepth) {
            return;
        }
        for (WebdavNsNode child : this.getNsIntf().getChildren(node, null)) {
            this.doNodeAndChildren(child, curDepth, maxDepth);
        }
    }

    private void doPropNames(WebdavNsNode node) throws WebdavException {
        this.openTag(WebdavTags.prop);
        for (WebdavNsNode.PropertyTagEntry pte : node.getPropertyNames()) {
            if (!pte.inPropAll) continue;
            this.emptyTag(pte.tag);
        }
        this.closeTag(WebdavTags.prop);
    }

    private int doPropAll(WebdavNsNode node) throws WebdavException {
        WebdavNsIntf intf = this.getNsIntf();
        this.openTag(WebdavTags.prop);
        this.doLockDiscovery(node);
        String sl = this.getNsIntf().getSupportedLocks();
        if (sl != null) {
            this.property(WebdavTags.supportedlock, sl);
        }
        for (WebdavNsNode.PropertyTagEntry pte : node.getPropertyNames()) {
            if (!pte.inPropAll) continue;
            intf.generatePropValue(node, new WebdavProperty(pte.tag, null), true);
        }
        this.closeTag(WebdavTags.prop);
        return 200;
    }

    private void doLockDiscovery(WebdavNsNode node) throws WebdavException {
    }

    public static class PropRequest {
        public ReqType reqType;
        public List<WebdavProperty> props;

        PropRequest(ReqType reqType) {
            this.reqType = reqType;
        }

        public static enum ReqType {
            prop,
            propName,
            propAll;

        }
    }
}

