/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.webdav.servlet.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.access.AccessException;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.AccessUtil;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavServerError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AclMethod
extends MethodBase {
    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        Document doc;
        if (this.debug) {
            this.debug("AclMethod: doMethod");
        }
        if ((doc = this.parseContent(req, resp)) == null) {
            return;
        }
        WebdavNsIntf.AclInfo ainfo = this.processDoc(doc, this.getResourceUri(req));
        this.processResp(req, resp, ainfo);
    }

    private WebdavNsIntf.AclInfo processDoc(Document doc, String uri) throws WebdavException {
        try {
            WebdavNsIntf intf = this.getNsIntf();
            WebdavNsIntf.AclInfo ainfo = new WebdavNsIntf.AclInfo(uri);
            Element root = doc.getDocumentElement();
            AccessUtil autil = intf.getAccessUtil();
            ainfo.acl = autil.getAcl(root, true);
            if (autil.getErrorTag() != null) {
                ainfo.errorTag = autil.getErrorTag();
            }
            return ainfo;
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (AccessException ae) {
            throw new WebdavBadRequest(ae.getMessage());
        }
        catch (Throwable t) {
            this.error(t.getMessage());
            if (this.debug) {
                t.printStackTrace();
            }
            throw new WebdavServerError();
        }
    }

    private void processResp(HttpServletRequest req, HttpServletResponse resp, WebdavNsIntf.AclInfo ainfo) throws WebdavException {
        WebdavNsIntf intf = this.getNsIntf();
        if (ainfo.errorTag == null) {
            intf.updateAccess(ainfo);
            return;
        }
        this.startEmit(resp);
        resp.setStatus(403);
        this.openTag(WebdavTags.error);
        this.emptyTag(ainfo.errorTag);
        this.closeTag(WebdavTags.error);
        this.flush();
    }
}

