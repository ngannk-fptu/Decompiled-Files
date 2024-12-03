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
import org.bedework.webdav.servlet.common.Headers;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.common.PostRequestPars;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;

public class PostMethod
extends MethodBase {
    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        PostRequestPars pars = new PostRequestPars(req, this.getNsIntf(), this.getResourceUri(req));
        if (pars.isAddMember()) {
            this.handleAddMember(pars, resp);
            return;
        }
        throw new WebdavBadRequest();
    }

    protected void handleAddMember(PostRequestPars pars, HttpServletResponse resp) throws WebdavException {
        if (this.debug) {
            this.debug("PostMethod: doMethod");
        }
        WebdavNsIntf intf = this.getNsIntf();
        Headers.IfHeaders ifHeaders = Headers.processIfHeaders(pars.getReq());
        if (ifHeaders.ifHeader != null && !intf.syncTokenMatch(ifHeaders.ifHeader)) {
            intf.rollback();
            throw new WebdavException(412);
        }
        intf.putContent(pars.getReq(), null, resp, true, ifHeaders);
    }
}

