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
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNotFound;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;

public class CopyMethod
extends MethodBase {
    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        this.process(req, resp, true);
    }

    protected void process(HttpServletRequest req, HttpServletResponse resp, boolean copy) throws WebdavException {
        if (this.debug) {
            if (copy) {
                this.debug("CopyMethod: doMethod");
            } else {
                this.debug("MoveMethod: doMethod");
            }
        }
        try {
            boolean overwrite;
            String dest = req.getHeader("Destination");
            if (dest == null) {
                if (this.debug) {
                    this.debug("No Destination");
                }
                throw new WebdavNotFound("No Destination");
            }
            int depth = Headers.depth(req);
            String ow = req.getHeader("Overwrite");
            if (ow == null) {
                overwrite = true;
            } else if ("T".equals(ow)) {
                overwrite = true;
            } else if ("F".equals(ow)) {
                overwrite = false;
            } else {
                resp.setStatus(400);
                return;
            }
            WebdavNsIntf intf = this.getNsIntf();
            WebdavNsNode from = intf.getNode(this.getResourceUri(req), 1, 3, false);
            if (from == null || !from.getExists()) {
                resp.setStatus(404);
                return;
            }
            int toNodeType = from.isCollection() ? 0 : 1;
            WebdavNsNode to = intf.getNode(intf.getUri(dest), 3, toNodeType, false);
            if (from.equals(to)) {
                throw new WebdavForbidden("source and destination equal");
            }
            intf.copyMove(req, resp, from, to, copy, overwrite, depth);
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

