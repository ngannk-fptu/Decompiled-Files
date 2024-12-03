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
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;

public class PutMethod
extends MethodBase {
    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        if (this.debug) {
            this.debug("PutMethod: doMethod");
        }
        WebdavNsIntf intf = this.getNsIntf();
        Headers.IfHeaders ifHeaders = Headers.processIfHeaders(req);
        if (ifHeaders.ifHeader != null && !intf.syncTokenMatch(ifHeaders.ifHeader)) {
            intf.rollback();
            throw new WebdavException(412);
        }
        intf.putContent(req, null, resp, false, ifHeaders);
    }
}

