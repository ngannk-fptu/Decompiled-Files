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
import javax.xml.namespace.QName;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PropPatchMethod
extends MethodBase {
    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        if (this.debug) {
            this.debug("PropPatchMethod: doMethod");
        }
        Document doc = this.parseContent(req, resp);
        String resourceUri = this.getResourceUri(req);
        WebdavNsNode node = this.getNsIntf().getNode(resourceUri, 1, 3, false);
        if (node == null || !node.getExists()) {
            resp.setStatus(404);
            return;
        }
        if (doc != null) {
            this.processDoc(req, resp, doc, node, WebdavTags.propertyUpdate, false);
            node.update();
        }
    }

    /*
     * WARNING - void declaration
     */
    protected void processDoc(HttpServletRequest req, HttpServletResponse resp, Document doc, WebdavNsNode node, QName expectedRoot, boolean onlySet) throws WebdavException {
        try {
            int status;
            Element root = doc.getDocumentElement();
            if (!XmlUtil.nodeMatches(root, expectedRoot)) {
                throw new WebdavBadRequest();
            }
            Collection<? extends Collection<Element>> setRemoveList = this.processUpdate(root);
            ArrayList<WebdavNsNode.SetPropertyResult> failures = new ArrayList<WebdavNsNode.SetPropertyResult>();
            ArrayList<WebdavNsNode.SetPropertyResult> successes = new ArrayList<WebdavNsNode.SetPropertyResult>();
            for (Collection<Element> collection : setRemoveList) {
                boolean setting = collection instanceof PropertySetList;
                for (Element prop : collection) {
                    boolean recognized;
                    WebdavNsNode.SetPropertyResult spr = new WebdavNsNode.SetPropertyResult(prop, expectedRoot);
                    if (setting) {
                        recognized = node.setProperty(prop, spr);
                    } else {
                        if (onlySet) {
                            throw new WebdavBadRequest();
                        }
                        recognized = node.removeProperty(prop, spr);
                    }
                    if (!recognized) {
                        spr.status = 404;
                    }
                    if (spr.status != 200) {
                        failures.add(spr);
                        continue;
                    }
                    successes.add(spr);
                }
            }
            resp.setStatus(207);
            resp.setContentType("text/xml; charset=UTF-8");
            this.startEmit(resp);
            this.openTag(WebdavTags.multistatus);
            this.openTag(WebdavTags.response);
            node.generateHref(this.xml);
            Object var12_16 = null;
            if (failures.isEmpty()) {
                status = 200;
            } else {
                void var12_18;
                status = 424;
                String string = "Failed Dependency";
                this.openTag(WebdavTags.propstat);
                for (WebdavNsNode.SetPropertyResult spr : failures) {
                    this.openTag(WebdavTags.prop);
                    this.emptyTag(spr.prop);
                    this.closeTag(WebdavTags.prop);
                    status = spr.status;
                    String string2 = spr.message;
                }
                this.addStatus(status, (String)var12_18);
                this.closeTag(WebdavTags.propstat);
            }
            if (!successes.isEmpty()) {
                void var12_20;
                this.openTag(WebdavTags.propstat);
                for (WebdavNsNode.SetPropertyResult spr : successes) {
                    this.openTag(WebdavTags.prop);
                    this.emptyTag(spr.prop);
                    this.closeTag(WebdavTags.prop);
                }
                this.addStatus(status, (String)var12_20);
                this.closeTag(WebdavTags.propstat);
            }
            this.closeTag(WebdavTags.response);
            this.closeTag(WebdavTags.multistatus);
            this.flush();
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

    private Collection<? extends Collection<Element>> processUpdate(Element node) throws WebdavException {
        ArrayList<PropertySetList> res = new ArrayList<PropertySetList>();
        try {
            Element[] children;
            for (Element srnode : children = this.getChildrenArray(node)) {
                ArrayList plist;
                Element propnode = this.getOnlyChild(srnode);
                if (!XmlUtil.nodeMatches(propnode, WebdavTags.prop)) {
                    throw new WebdavBadRequest();
                }
                if (XmlUtil.nodeMatches(srnode, WebdavTags.set)) {
                    plist = new PropertySetList();
                    this.processPlist(plist, propnode, false);
                } else if (XmlUtil.nodeMatches(srnode, WebdavTags.remove)) {
                    plist = new PropertyRemoveList();
                    this.processPlist(plist, propnode, true);
                } else {
                    throw new WebdavBadRequest();
                }
                res.add((PropertySetList)plist);
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
        return res;
    }

    private void processPlist(Collection<Element> plist, Element node, boolean remove) throws WebdavException {
        Element[] props;
        for (Element prop : props = this.getChildrenArray(node)) {
            if (remove && !this.isEmpty(prop)) {
                throw new WebdavBadRequest();
            }
            plist.add(prop);
        }
    }

    private static class PropertyRemoveList
    extends ArrayList<Element> {
        private PropertyRemoveList() {
        }
    }

    private static class PropertySetList
    extends ArrayList<Element> {
        private PropertySetList() {
        }
    }
}

