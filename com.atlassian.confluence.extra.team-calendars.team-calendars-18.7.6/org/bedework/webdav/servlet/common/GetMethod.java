/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.webdav.servlet.common;

import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;

public class GetMethod
extends MethodBase {
    protected boolean doContent;

    @Override
    public void init() {
        this.doContent = true;
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        if (this.debug) {
            this.debug("GetMethod: doMethod");
        }
        try {
            WebdavNsIntf.Content c;
            WebdavNsIntf intf = this.getNsIntf();
            if (intf.specialUri(req, resp, this.getResourceUri(req))) {
                return;
            }
            WebdavNsNode node = intf.getNode(this.getResourceUri(req), 1, 3, false);
            if (node == null || !node.getExists()) {
                resp.setStatus(404);
                return;
            }
            if (!intf.prefetch(req, resp, node)) {
                return;
            }
            resp.setHeader("ETag", node.getEtagValue(true));
            if (node.getLastmodDate() != null) {
                resp.addHeader("Last-Modified", node.getLastmodDate());
            }
            if ((c = node.getContentBinary() ? intf.getBinaryContent(node) : intf.getContent(req, resp, null, node)) == null) {
                if (this.debug) {
                    this.debug("status: 204");
                }
                resp.setStatus(204);
                return;
            }
            if (c.written) {
                resp.setStatus(200);
            } else if (c.contentType != null) {
                resp.setContentType(c.contentType);
            }
            if (c.contentLength > Integer.MAX_VALUE) {
                resp.setContentLength(-1);
            } else {
                resp.setContentLength((int)c.contentLength);
            }
            if (c.written || !this.doContent) {
                return;
            }
            if (c.stream == null && c.rdr == null) {
                if (this.debug) {
                    this.debug("status: 204");
                }
                resp.setStatus(204);
            } else {
                if (this.debug) {
                    this.debug("send content - length=" + c.contentLength);
                }
                if (c.stream != null) {
                    intf.streamContent(c.stream, (OutputStream)resp.getOutputStream());
                } else {
                    intf.writeContent(c.rdr, resp.getWriter());
                }
            }
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

