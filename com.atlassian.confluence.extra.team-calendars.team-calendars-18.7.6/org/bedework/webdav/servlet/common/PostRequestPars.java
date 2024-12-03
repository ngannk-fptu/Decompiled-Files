/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.bedework.webdav.servlet.common;

import java.io.Reader;
import javax.servlet.http.HttpServletRequest;
import org.bedework.util.misc.Util;
import org.bedework.webdav.servlet.common.SecureXml;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.w3c.dom.Document;

public class PostRequestPars
implements SecureXml {
    private final HttpServletRequest req;
    private final WebdavNsIntf intf;
    private final String method;
    private final String resourceUri;
    private String noPrefixResourceUri;
    private final String acceptType;
    private String contentType;
    private String[] contentTypePars;
    private Reader reqRdr;
    private Document xmlDoc;
    protected boolean addMember;
    protected boolean getTheReader = true;

    public PostRequestPars(HttpServletRequest req, WebdavNsIntf intf, String resourceUri) throws WebdavException {
        this.req = req;
        this.intf = intf;
        this.resourceUri = resourceUri;
        this.method = req.getMethod();
        this.acceptType = req.getHeader("ACCEPT");
        this.contentType = req.getContentType();
        if (this.contentType != null) {
            this.contentTypePars = this.contentType.split(";");
        }
    }

    public boolean processRequest() throws WebdavException {
        String addMemberSuffix = this.intf.getAddMemberSuffix();
        if (addMemberSuffix == null) {
            return false;
        }
        String reqUri = this.req.getRequestURI();
        if (reqUri == null) {
            return false;
        }
        int pos = reqUri.lastIndexOf("/");
        if (pos > 0 && reqUri.regionMatches(pos + 1, addMemberSuffix, 0, addMemberSuffix.length())) {
            this.addMember = true;
            return true;
        }
        return false;
    }

    public boolean processXml() throws WebdavException {
        if (!this.isAppXml()) {
            return false;
        }
        try {
            this.reqRdr = this.req.getReader();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        this.xmlDoc = this.parseXmlSafely(this.req.getContentLength(), this.reqRdr);
        this.getTheReader = false;
        return true;
    }

    public Reader getReader() throws WebdavException {
        if (!this.getTheReader) {
            return null;
        }
        if (this.reqRdr != null) {
            return this.reqRdr;
        }
        try {
            this.reqRdr = this.req.getReader();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        return this.reqRdr;
    }

    public HttpServletRequest getReq() {
        return this.req;
    }

    public String getMethod() {
        return this.method;
    }

    public String getResourceUri() {
        return this.resourceUri;
    }

    public String getNoPrefixResourceUri() {
        return this.noPrefixResourceUri;
    }

    public String getAcceptType() {
        return this.acceptType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String[] getContentTypePars() {
        return this.contentTypePars;
    }

    public Document getXmlDoc() {
        return this.xmlDoc;
    }

    public boolean isAddMember() {
        return this.addMember;
    }

    public boolean isAppXml() {
        return this.contentTypePars != null && (this.contentTypePars[0].equals("application/xml") || this.contentTypePars[0].equals("text/xml"));
    }

    public void setContentType(String val) {
        this.contentType = val;
    }

    protected boolean checkUri(String specialUri) {
        int pos;
        String prefix;
        if (specialUri == null) {
            return false;
        }
        String toMatch = Util.buildPath(true, specialUri);
        if (!toMatch.equals(Util.buildPath(true, prefix = (pos = this.resourceUri.indexOf("/", 1)) < 0 ? this.noParameters(this.resourceUri) : this.resourceUri.substring(0, pos)))) {
            this.noPrefixResourceUri = this.noParameters(this.resourceUri);
            return false;
        }
        this.noPrefixResourceUri = pos < 0 ? "" : this.noParameters(this.resourceUri.substring(pos));
        return true;
    }

    private String noParameters(String uri) {
        int pos = uri.indexOf("?");
        if (pos > 0) {
            uri = uri.substring(0, pos);
        }
        if (uri.equals("/")) {
            uri = "";
        }
        return uri;
    }
}

