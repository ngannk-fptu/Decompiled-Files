/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.webdav.servlet.common;

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.util.misc.Logged;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavProperty;
import org.w3c.dom.Element;

public class PrincipalMatchReport
extends Logged {
    private final MethodBase mb;
    private final WebdavNsIntf intf;
    public boolean self;
    public boolean owner;
    public boolean whoami;
    public Element principalProperty;
    public Collection<WebdavProperty> props = new ArrayList<WebdavProperty>();

    public PrincipalMatchReport(MethodBase mb, WebdavNsIntf intf) {
        this.mb = mb;
        this.intf = intf;
    }

    public void parse(Element root, int depth) throws WebdavException {
        try {
            Element[] children;
            int numch;
            if (this.debug) {
                this.debug("ReportMethod: parsePrincipalMatch");
            }
            if ((numch = (children = this.intf.getChildren(root)).length) < 1 || numch > 2) {
                throw new WebdavBadRequest();
            }
            Element curnode = children[0];
            if (XmlUtil.nodeMatches(curnode, WebdavTags.principalProperty)) {
                Element[] ppchildren = this.intf.getChildren(curnode);
                if (ppchildren.length != 1) {
                    throw new WebdavBadRequest();
                }
                if (XmlUtil.nodeMatches(ppchildren[0], WebdavTags.owner)) {
                    this.owner = true;
                } else if (XmlUtil.nodeMatches(ppchildren[0], WebdavTags.whoami)) {
                    this.whoami = true;
                } else {
                    this.principalProperty = ppchildren[0];
                }
            } else if (XmlUtil.nodeMatches(curnode, WebdavTags.self)) {
                if (this.debug) {
                    this.debug("ReportMethod: self");
                }
                this.self = true;
            } else {
                throw new WebdavBadRequest();
            }
            if (numch == 1) {
                return;
            }
            curnode = children[1];
            if (!XmlUtil.nodeMatches(curnode, WebdavTags.prop)) {
                throw new WebdavBadRequest();
            }
            if (this.debug) {
                this.debug("ReportMethod: do prop");
            }
            this.props = this.intf.parseProp(curnode);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            this.warn(t.getMessage());
            if (this.debug) {
                t.printStackTrace();
            }
            throw new WebdavException(500);
        }
    }

    public void process(HttpServletRequest req, HttpServletResponse resp, int depth) throws WebdavException {
        try {
            resp.setStatus(207);
            resp.setContentType("text/xml; charset=UTF-8");
            XmlEmit xml = this.intf.getXmlEmit();
            xml.startEmit(resp.getWriter());
            xml.openTag(WebdavTags.multistatus);
            String resourceUri = this.mb.getResourceUri(req);
            Collection<WebdavNsNode> wdnodes = this.self ? this.intf.getGroups(resourceUri, null) : this.doNodeAndChildren(this.intf.getNode(resourceUri, 1, 3, false));
            if (wdnodes != null) {
                for (WebdavNsNode nd : wdnodes) {
                    xml.openTag(WebdavTags.response);
                    nd.generateHref(xml);
                    this.mb.doPropFind(nd, this.props);
                    xml.closeTag(WebdavTags.response);
                }
            }
            xml.closeTag(WebdavTags.multistatus);
            xml.flush();
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            this.warn(t.getMessage());
            if (this.debug) {
                t.printStackTrace();
            }
            throw new WebdavException(500);
        }
    }

    private Collection<WebdavNsNode> doNodeAndChildren(WebdavNsNode node) throws WebdavException {
        ArrayList<WebdavNsNode> nodes = new ArrayList<WebdavNsNode>();
        if (!this.nodeMatches(node)) {
            return nodes;
        }
        if (!node.isCollection()) {
            nodes.add(node);
            return nodes;
        }
        for (WebdavNsNode child : this.intf.getChildren(node, null)) {
            nodes.addAll(this.doNodeAndChildren(child));
        }
        return nodes;
    }

    private boolean nodeMatches(WebdavNsNode node) throws WebdavException {
        if (this.owner) {
            String account = this.intf.getAccount();
            if (account == null) {
                return false;
            }
            return account.equals(node.getOwner().getAccount());
        }
        return false;
    }
}

