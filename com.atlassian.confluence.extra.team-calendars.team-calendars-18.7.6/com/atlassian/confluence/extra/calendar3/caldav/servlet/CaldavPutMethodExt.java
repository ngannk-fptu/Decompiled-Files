/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.calendar3.caldav.servlet;

import com.atlassian.confluence.extra.calendar3.caldav.servlet.SecureXmlMethod;
import java.io.Reader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.bedework.webdav.servlet.common.Headers;
import org.bedework.webdav.servlet.common.PutMethod;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavUnauthorized;
import org.w3c.dom.Document;

public class CaldavPutMethodExt
extends PutMethod
implements SecureXmlMethod {
    @Override
    protected final Document parseContent(int contentLength, Reader reader) throws WebdavException {
        return this.parseContentSafe(contentLength, reader);
    }

    @Override
    public final void doMethod(HttpServletRequest request, HttpServletResponse response) throws WebdavException {
        if (request.getRemoteUser() == null) {
            throw new WebdavUnauthorized();
        }
        if (this.debug) {
            this.debug("PutMethod: doMethod");
        }
        WebdavNsIntf intf = this.getNsIntf();
        Headers.IfHeaders ifHeaders = this.getSanitizeIfHeader(request);
        if (ifHeaders.ifHeader != null && !intf.syncTokenMatch(ifHeaders.ifHeader)) {
            this.debug("syncTokenMatch does not match");
            intf.rollback();
            throw new WebdavException(412, "Sync token does not matchDefaultCalDavEventManager");
        }
        String userAgent = StringUtils.defaultString((String)request.getHeader("User-Agent"), (String)"");
        if (userAgent.toLowerCase().contains("davdroid")) {
            ifHeaders.create = false;
        }
        this.debug("PutMethod: putContent");
        intf.putContent(request, null, response, false, ifHeaders);
    }

    private Headers.IfHeaders getSanitizeIfHeader(HttpServletRequest request) throws WebdavException {
        Headers.IfHeaders ifHeaders = Headers.processIfHeaders(request);
        String etag = ifHeaders.ifEtag;
        if (StringUtils.isNoneEmpty((CharSequence[])new CharSequence[]{etag})) {
            etag = etag.trim();
            ifHeaders.ifEtag = etag = etag.replace("\"", "");
        }
        return ifHeaders;
    }
}

