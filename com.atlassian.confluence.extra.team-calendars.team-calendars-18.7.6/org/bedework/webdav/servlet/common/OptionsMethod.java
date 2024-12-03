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
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsNode;

public class OptionsMethod
extends MethodBase {
    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        if (this.debug) {
            this.debug("OptionsMethod: doMethod");
        }
        try {
            WebdavNsNode node;
            String resourceUri = this.getResourceUri(req);
            if ("*".equals(resourceUri)) {
                node = null;
            } else {
                node = this.getNsIntf().getNode(resourceUri, 1, 3, false);
                if (node == null || !node.getExists()) {
                    resp.setStatus(404);
                    return;
                }
            }
            this.addHeaders(req, resp, node);
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    protected void addDavHeader(HttpServletResponse resp, WebdavNsNode node) throws WebdavException {
        resp.addHeader("DAV", this.getNsIntf().getDavHeader(node));
    }
}

