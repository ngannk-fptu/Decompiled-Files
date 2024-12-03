/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.extra.calendar3.caldav.servlet;

import com.atlassian.confluence.extra.calendar3.caldav.servlet.SecureXmlMethod;
import java.io.Reader;
import javax.servlet.http.HttpServletRequest;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.RequestPars;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Document;

public class RequestParsExt
extends RequestPars
implements SecureXmlMethod {
    private Document xmlDocument;

    public RequestParsExt(HttpServletRequest request, CaldavBWIntf intf, String resourceUri) throws WebdavException {
        super(request, intf, resourceUri);
    }

    @Override
    public Document getXmlDoc() {
        return this.xmlDocument;
    }

    @Override
    public final boolean processXml() throws WebdavException {
        if (!this.isAppXml()) {
            return false;
        }
        Reader reader = this.getReader();
        this.xmlDocument = this.parseContentSafe(this.getReq().getContentLength(), reader);
        this.getTheReader = false;
        return true;
    }
}

