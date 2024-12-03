/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.caldav.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.caldav.server.CaldavCalNode;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.webdav.servlet.common.Headers;
import org.bedework.webdav.servlet.common.PropPatchMethod;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.w3c.dom.Document;

public class MkcalendarMethod
extends PropPatchMethod {
    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        if (this.debug) {
            this.debug("MkcalendarMethod: doMethod");
        }
        WebdavNsIntf intf = this.getNsIntf();
        Headers.IfHeaders ifHeaders = Headers.processIfHeaders(req);
        if (ifHeaders.ifHeader != null && !intf.syncTokenMatch(ifHeaders.ifHeader)) {
            intf.rollback();
            throw new WebdavException(412);
        }
        Document doc = this.parseContent(req, resp);
        String resourceUri = this.getResourceUri(req);
        CaldavCalNode node = (CaldavCalNode)this.getNsIntf().getNode(resourceUri, 0, 0, false);
        node.setDefaults(CaldavTags.mkcalendar);
        if (doc != null) {
            this.processDoc(req, resp, doc, node, CaldavTags.mkcalendar, true);
        }
        this.getNsIntf().makeCollection(req, resp, node);
    }
}

