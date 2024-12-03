/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.webdav.servlet.shared;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.bedework.access.Acl;
import org.bedework.util.misc.Logged;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.AccessUtil;
import org.bedework.webdav.servlet.common.Headers;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.common.WebdavServlet;
import org.bedework.webdav.servlet.common.WebdavUtils;
import org.bedework.webdav.servlet.shared.PrincipalPropertySearch;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WdSynchReport;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavProperty;
import org.bedework.webdav.servlet.shared.WebdavStatusCode;
import org.bedework.webdav.servlet.shared.serverInfo.Feature;
import org.bedework.webdav.servlet.shared.serverInfo.ServerInfo;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class WebdavNsIntf
extends Logged
implements Serializable {
    protected static volatile SessCt session = new SessCt();
    protected int sessNum;
    protected WebdavServlet servlet;
    private HttpServletRequest req;
    protected String account;
    protected boolean anonymous;
    protected boolean dumpContent;
    protected XmlEmit xml;
    protected HashMap<String, MethodBase.MethodInfo> methods;
    boolean returnMultistatusOk = true;
    private String urlPrefix;
    private static ServerInfo serverInfo;
    public static final int existanceNot = 0;
    public static final int existanceMust = 1;
    public static final int existanceDoesExist = 2;
    public static final int existanceMay = 3;
    public static final int nodeTypeCollection = 0;
    public static final int nodeTypeEntity = 1;
    public static final int nodeTypePrincipal = 2;
    public static final int nodeTypeUnknown = 3;
    private static final int bufferSize = 4096;
    private static final QName[] knownProperties;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(WebdavServlet servlet, HttpServletRequest req, HashMap<String, MethodBase.MethodInfo> methods, boolean dumpContent) throws WebdavException {
        this.servlet = servlet;
        this.req = req;
        this.xml = new XmlEmit();
        this.methods = methods;
        this.dumpContent = dumpContent;
        SessCt sessCt = session;
        synchronized (sessCt) {
            ++WebdavNsIntf.session.sessNum;
            this.sessNum = WebdavNsIntf.session.sessNum;
        }
        this.account = req.getRemoteUser();
        this.anonymous = this.account == null || this.account.length() == 0;
        this.urlPrefix = WebdavUtils.getUrlPrefix(req);
        this.addNamespace(this.xml);
    }

    public String getAccount() {
        return this.account;
    }

    public XmlEmit getXmlEmit() {
        return this.xml;
    }

    public HttpServletRequest getRequest() {
        return this.req;
    }

    public String getDavHeader(WebdavNsNode node) throws WebdavException {
        if (this.account == null) {
            return "1, 3";
        }
        return "1, 3, access-control, extended-mkcol";
    }

    public ServerInfo getServerInfo() {
        if (serverInfo != null) {
            return serverInfo;
        }
        serverInfo = new ServerInfo();
        serverInfo.setToken(String.valueOf(System.currentTimeMillis()));
        serverInfo.addFeature(new Feature(WebdavTags.accessControl));
        serverInfo.addFeature(new Feature(WebdavTags.addMember));
        serverInfo.addFeature(new Feature(WebdavTags.class1));
        serverInfo.addFeature(new Feature(WebdavTags.class2));
        serverInfo.addFeature(new Feature(WebdavTags.extendedMkcol));
        serverInfo.addFeature(new Feature(WebdavTags.syncCollection));
        return serverInfo;
    }

    public boolean syncTokenMatch(Headers.IfHeader ih) throws WebdavException {
        if (ih.resourceTag == null) {
            throw new WebdavException(412, "Bad If header - no resource tag");
        }
        if (ih.tagsAndTokens.size() != 1) {
            throw new WebdavException(412, "Bad If header - only 1 state-token allowed");
        }
        Headers.IfHeader.TagOrToken tt = ih.tagsAndTokens.get(0);
        if (tt.entityTag) {
            throw new WebdavException(412, "Bad If header - entity-tag not allowed");
        }
        String token = this.getSyncToken(ih.resourceTag);
        if (token == null) {
            throw new WebdavException(412, "Bad If header - no token for resource");
        }
        if (!token.equals(tt.value)) {
            if (this.debug) {
                this.debug("putContent: sync-token mismatch ifheader=" + tt.value + "col-token=" + token);
            }
            return false;
        }
        return true;
    }

    public void emitError(QName errorTag, String extra, XmlEmit xml) throws Throwable {
        if (extra == null) {
            xml.emptyTag(errorTag);
        } else {
            xml.property(errorTag, extra);
        }
    }

    public abstract AccessUtil getAccessUtil() throws WebdavException;

    public abstract boolean canPut(WebdavNsNode var1) throws WebdavException;

    public abstract String getAddMemberSuffix() throws WebdavException;

    public Collection<String> getMethodNames() {
        return this.methods.keySet();
    }

    public MethodBase getMethod(String name) throws WebdavException {
        MethodBase.MethodInfo mi = this.methods.get(name = name.toUpperCase());
        if (mi == null || this.getAnonymous() && mi.getRequiresAuth()) {
            return null;
        }
        try {
            MethodBase mb = (MethodBase)mi.getMethodClass().newInstance();
            mb.init(this, this.dumpContent);
            return mb;
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new WebdavException(t);
        }
    }

    public boolean getAnonymous() {
        return this.anonymous;
    }

    public String getUri(String href) throws WebdavException {
        try {
            if (href == null) {
                throw new WebdavBadRequest("bad URI " + href);
            }
            String context = this.req.getContextPath();
            if (href.startsWith(context)) {
                return href.substring(context.length());
            }
            if (href.startsWith("/")) {
                return href;
            }
            URL url = new URL(href);
            String path = url.getPath();
            if (path == null || path.length() <= 1) {
                return path;
            }
            if (context == null) {
                return path;
            }
            if (path.indexOf(context) != 0) {
                return path;
            }
            int pos = context.length();
            if (path.length() == pos) {
                return "";
            }
            if (path.charAt(pos) != '/') {
                throw new WebdavBadRequest("bad URI " + href);
            }
            return path.substring(pos);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new WebdavBadRequest("bad URI " + href);
        }
    }

    public String makeName(String val) {
        if (val == null || val.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        block4: for (int i = 0; i < val.length(); ++i) {
            char ch = val.charAt(i);
            switch (ch) {
                case '\"': 
                case '/': 
                case '\\': {
                    sb.append('-');
                    continue block4;
                }
                case ' ': {
                    sb.append("-");
                    continue block4;
                }
                default: {
                    if (Character.isISOControl(ch)) {
                        sb.append("-");
                        continue block4;
                    }
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    public WebdavServlet getServlet() {
        return this.servlet;
    }

    public boolean getReturnMultistatusOk() {
        return this.returnMultistatusOk;
    }

    public void addNamespace(XmlEmit xml) throws WebdavException {
        try {
            xml.addNs(new XmlEmit.NameSpace("DAV:", "DAV"), true);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public abstract boolean getDirectoryBrowsingDisallowed() throws WebdavException;

    public abstract void rollback();

    public abstract void close() throws WebdavException;

    public abstract String getSupportedLocks();

    public abstract boolean getAccessControl();

    public abstract WebdavNsNode getNode(String var1, int var2, int var3, boolean var4) throws WebdavException;

    public abstract void putNode(WebdavNsNode var1) throws WebdavException;

    public abstract void delete(WebdavNsNode var1) throws WebdavException;

    public abstract Collection<WebdavNsNode> getChildren(WebdavNsNode var1, Supplier<Object> var2) throws WebdavException;

    public abstract WebdavNsNode getParent(WebdavNsNode var1) throws WebdavException;

    public boolean prefetch(HttpServletRequest req, HttpServletResponse resp, WebdavNsNode node) throws WebdavException {
        String etag = Headers.ifNoneMatch(req);
        if (etag != null && !node.isCollection() && etag.equals(node.getEtagValue(true))) {
            resp.setStatus(304);
            return false;
        }
        return true;
    }

    public abstract Content getContent(HttpServletRequest var1, HttpServletResponse var2, String var3, WebdavNsNode var4) throws WebdavException;

    public abstract Content getBinaryContent(WebdavNsNode var1) throws WebdavException;

    public abstract String getAcceptContentType(HttpServletRequest var1) throws WebdavException;

    public PutContentResult putContent(HttpServletRequest req, String resourceUri, HttpServletResponse resp, boolean fromPost, Headers.IfHeaders ifHeaders) throws WebdavException {
        try {
            PutContentResult pcr;
            WebdavNsNode node;
            int existence;
            boolean addMember = false;
            if (ifHeaders.create) {
                existence = 0;
            } else if (!fromPost) {
                existence = 3;
            } else {
                existence = 0;
                addMember = true;
            }
            String ruri = resourceUri != null ? resourceUri : this.getResourceUri(req);
            if (addMember) {
                ruri = Util.buildPath(false, ruri, "/", UUID.randomUUID().toString(), ".ics");
            }
            if ((node = this.getNode(ruri, existence, 1, addMember)) == null) {
                resp.setStatus(404);
                return null;
            }
            if (!node.getAllowsGet() || !this.canPut(node)) {
                resp.setStatus(403);
                return null;
            }
            String[] contentTypePars = null;
            String contentType = req.getContentType();
            boolean returnRep = Headers.returnRepresentation(req);
            Content c = null;
            if (contentType != null) {
                contentTypePars = contentType.split(";");
            }
            if (node.getContentBinary()) {
                pcr = this.putBinaryContent(req, node, contentTypePars, (InputStream)req.getInputStream(), ifHeaders);
            } else {
                Reader rdr = this.getReader(req);
                if (rdr == null) {
                    resp.setStatus(400);
                    return null;
                }
                pcr = this.putContent(req, resp, node, contentTypePars, rdr, ifHeaders);
            }
            if (pcr.emitEtag) {
                resp.setHeader("ETag", node.getEtagValue(true));
            }
            if (fromPost && pcr.created) {
                resp.setHeader("Location", this.getLocation(pcr.node));
            }
            if (!node.getContentBinary() && returnRep) {
                String ctype = this.getAcceptContentType(req);
                resp.setContentType(ctype);
                if (!pcr.emitEtag) {
                    resp.setHeader("ETag", node.getEtagValue(true));
                }
                c = this.getContent(req, resp, ctype, node);
            }
            if (c == null) {
                if (pcr.created) {
                    resp.setStatus(201);
                } else {
                    resp.setStatus(204);
                }
                resp.setContentLength(0);
            } else {
                if (pcr.created) {
                    resp.setStatus(201);
                } else {
                    resp.setStatus(200);
                }
                if (c.contentType != null) {
                    resp.setContentType(c.contentType);
                }
                if (node.getLastmodDate() != null) {
                    resp.addHeader("Last-Modified", node.getLastmodDate());
                }
                if (!c.written) {
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
                            this.streamContent(c.stream, (OutputStream)resp.getOutputStream());
                        } else {
                            this.writeContent(c.rdr, resp.getWriter());
                        }
                    }
                }
            }
            return pcr;
        }
        catch (WebdavForbidden wdf) {
            resp.setStatus(403);
            throw wdf;
        }
        catch (WebdavException we) {
            if (this.debug) {
                this.error(we);
            }
            throw we;
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new WebdavException(t);
        }
    }

    public void writeContent(Reader in, Writer out) throws WebdavException {
        try {
            int len;
            char[] buff = new char[4096];
            while ((len = in.read(buff)) >= 0) {
                out.write(buff, 0, len);
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        finally {
            try {
                in.close();
            }
            catch (Throwable buff) {}
            try {
                out.close();
            }
            catch (Throwable buff) {}
        }
    }

    public void streamContent(InputStream in, OutputStream out) throws WebdavException {
        try {
            int len;
            byte[] buff = new byte[4096];
            while ((len = in.read(buff)) >= 0) {
                out.write(buff, 0, len);
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        finally {
            try {
                in.close();
            }
            catch (Throwable buff) {}
            try {
                out.close();
            }
            catch (Throwable buff) {}
        }
    }

    public String normalizeUri(String uri) throws WebdavException {
        try {
            uri = new URI(null, null, uri, null).toString();
            uri = new URI(URLEncoder.encode(uri, "UTF-8")).normalize().getPath();
            uri = URLDecoder.decode(uri, "UTF-8");
            if (uri.length() > 1 && uri.endsWith("/")) {
                uri = uri.substring(0, uri.length() - 1);
            }
            if (this.debug) {
                this.debug("Normalized uri=" + uri);
            }
            return uri;
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new WebdavBadRequest("Bad uri: " + uri);
        }
    }

    public abstract PutContentResult putContent(HttpServletRequest var1, HttpServletResponse var2, WebdavNsNode var3, String[] var4, Reader var5, Headers.IfHeaders var6) throws WebdavException;

    public abstract PutContentResult putBinaryContent(HttpServletRequest var1, WebdavNsNode var2, String[] var3, InputStream var4, Headers.IfHeaders var5) throws WebdavException;

    public abstract void create(WebdavNsNode var1) throws WebdavException;

    public abstract void createAlias(WebdavNsNode var1) throws WebdavException;

    public abstract void acceptMkcolContent(HttpServletRequest var1) throws WebdavException;

    public abstract void makeCollection(HttpServletRequest var1, HttpServletResponse var2, WebdavNsNode var3) throws WebdavException;

    public abstract void copyMove(HttpServletRequest var1, HttpServletResponse var2, WebdavNsNode var3, WebdavNsNode var4, boolean var5, boolean var6, int var7) throws WebdavException;

    public abstract boolean specialUri(HttpServletRequest var1, HttpServletResponse var2, String var3) throws WebdavException;

    public abstract WdSynchReport getSynchReport(String var1, String var2, int var3, boolean var4) throws WebdavException;

    public abstract String getSyncToken(String var1) throws WebdavException;

    public abstract Collection<WebdavNsNode> getGroups(String var1, String var2) throws WebdavException;

    public abstract Collection<String> getPrincipalCollectionSet(String var1) throws WebdavException;

    public abstract Collection<? extends WebdavNsNode> getPrincipals(String var1, PrincipalPropertySearch var2) throws WebdavException;

    public abstract String makeUserHref(String var1) throws WebdavException;

    public String makeServerInfoUrl(HttpServletRequest req) throws WebdavException {
        UrlHandler uh = new UrlHandler(req, false);
        return uh.prefix("serverinfo/serverinfo.xml");
    }

    public abstract void updateAccess(AclInfo var1) throws WebdavException;

    public abstract void emitAcl(WebdavNsNode var1) throws WebdavException;

    public abstract Collection<String> getAclPrincipalInfo(WebdavNsNode var1) throws WebdavException;

    public void emitSupportedReportSet(WebdavNsNode node) throws WebdavException {
        try {
            this.xml.openTag(WebdavTags.supportedReportSet);
            Collection<QName> supportedReports = node.getSupportedReports();
            for (QName qn : supportedReports) {
                this.xml.openTag(WebdavTags.supportedReport);
                this.xml.openTag(WebdavTags.report);
                this.xml.emptyTag(qn);
                this.xml.closeTag(WebdavTags.report);
                this.xml.closeTag(WebdavTags.supportedReport);
            }
            this.xml.closeTag(WebdavTags.supportedReportSet);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void openPropstat() throws WebdavException {
        try {
            this.xml.openTag(WebdavTags.propstat);
            this.xml.openTag(WebdavTags.prop);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void closePropstat(int status) throws WebdavException {
        try {
            this.xml.closeTag(WebdavTags.prop);
            if (status != 200 || this.getReturnMultistatusOk()) {
                this.xml.property(WebdavTags.status, "HTTP/1.1 " + status + " " + WebdavStatusCode.getMessage(status));
            }
            this.xml.closeTag(WebdavTags.propstat);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void closePropstat() throws WebdavException {
        this.closePropstat(200);
    }

    public List<WebdavProperty> parseProp(Node nd) throws WebdavException {
        Element[] children;
        ArrayList<WebdavProperty> props = new ArrayList<WebdavProperty>();
        for (Element propnode : children = this.getChildren(nd)) {
            String ns = propnode.getNamespaceURI();
            if (this.xml.getNameSpace(ns) == null) {
                try {
                    this.xml.addNs(new XmlEmit.NameSpace(ns, null), false);
                }
                catch (IOException e) {
                    throw new WebdavException(e);
                }
            }
            WebdavProperty prop = this.makeProp(propnode);
            if (this.debug) {
                this.debug("prop: " + prop.getTag());
            }
            props.add(prop);
        }
        return props;
    }

    public WebdavProperty makeProp(Element propnode) throws WebdavException {
        WebdavProperty wd = new WebdavProperty(new QName(propnode.getNamespaceURI(), propnode.getLocalName()), null);
        NamedNodeMap nnm = propnode.getAttributes();
        for (int i = 0; i < nnm.getLength(); ++i) {
            Node n = nnm.item(i);
            wd.addAttr(n.getLocalName(), n.getNodeValue());
        }
        return wd;
    }

    public boolean knownProperty(WebdavNsNode node, WebdavProperty pr) {
        QName tag = pr.getTag();
        for (QName knownProperty : knownProperties) {
            if (!tag.equals(knownProperty)) continue;
            return true;
        }
        return node.knownProperty(tag);
    }

    public boolean generatePropValue(WebdavNsNode node, WebdavProperty pr, boolean allProp) throws WebdavException {
        QName tag = pr.getTag();
        String ns = tag.getNamespaceURI();
        try {
            if (!ns.equals("DAV:")) {
                return false;
            }
            if (tag.equals(WebdavTags.lockdiscovery)) {
                return false;
            }
            if (tag.equals(WebdavTags.source)) {
                return false;
            }
            if (tag.equals(WebdavTags.supportedlock)) {
                return false;
            }
            if (tag.equals(WebdavTags.aclRestrictions)) {
                return false;
            }
            if (tag.equals(WebdavTags.inheritedAclSet)) {
                return false;
            }
            if (tag.equals(WebdavTags.principalCollectionSet)) {
                this.xml.openTag(WebdavTags.principalCollectionSet);
                for (String s : this.getPrincipalCollectionSet(node.getUri())) {
                    this.xml.property(WebdavTags.href, s);
                }
                this.xml.closeTag(WebdavTags.principalCollectionSet);
                return true;
            }
            return node.generatePropertyValue(tag, this, allProp);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public String getLocation(WebdavNsNode node) throws WebdavException {
        try {
            String url;
            if (this.debug) {
                this.debug("Get url " + this.urlPrefix + node.getEncodedUri());
            }
            if ((url = this.urlPrefix + new URI(node.getEncodedUri()).toASCIIString()).endsWith("/")) {
                if (!node.trailSlash()) {
                    url = url.substring(0, url.length() - 1);
                }
            } else if (node.trailSlash()) {
                url = url + "/";
            }
            return url;
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void addStatus(int status) throws WebdavException {
        try {
            this.xml.property(WebdavTags.status, "HTTP/1.1 " + status + " " + WebdavStatusCode.getMessage(status));
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public String getResourceUri(HttpServletRequest req) throws WebdavException {
        String uri = req.getPathInfo();
        if (uri == null || uri.length() == 0) {
            uri = "/";
        }
        return WebdavNsIntf.fixPath(uri);
    }

    public static String fixPath(String path) throws WebdavException {
        String decoded;
        if (path == null) {
            return null;
        }
        try {
            decoded = URLDecoder.decode(path, "UTF8");
        }
        catch (Throwable t) {
            throw new WebdavBadRequest("bad path: " + path);
        }
        if (decoded == null) {
            return null;
        }
        if (decoded.indexOf(92) >= 0) {
            decoded = decoded.replace('\\', '/');
        }
        if (!decoded.startsWith("/")) {
            decoded = "/" + decoded;
        }
        while (decoded.indexOf("//") >= 0) {
            decoded = decoded.replaceAll("//", "/");
        }
        if (decoded.indexOf("/.") < 0) {
            return decoded;
        }
        StringTokenizer st = new StringTokenizer(decoded, "/");
        ArrayList<String> al = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (s.equals(".")) continue;
            if (s.equals("..")) {
                if (al.size() == 0) {
                    return null;
                }
                al.remove(al.size() - 1);
                continue;
            }
            al.add(s);
        }
        StringBuilder sb = new StringBuilder();
        for (String s : al) {
            sb.append('/');
            sb.append(s);
        }
        return sb.toString();
    }

    public Reader getReader(HttpServletRequest req) throws Throwable {
        Reader rdr = this.debug ? new DebugReader(req.getReader()) : req.getReader();
        PushbackReader pbr = new PushbackReader(rdr);
        int c = pbr.read();
        if (c == -1) {
            return null;
        }
        pbr.unread(c);
        return pbr;
    }

    public Element[] getChildren(Node nd) throws WebdavException {
        try {
            return XmlUtil.getElementsArray(nd);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error(this, t);
            }
            throw new WebdavBadRequest();
        }
    }

    public Element getOnlyChild(Node nd) throws WebdavException {
        try {
            return XmlUtil.getOnlyElement(nd);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error(this, t);
            }
            throw new WebdavBadRequest();
        }
    }

    public String getElementContent(Element el) throws WebdavException {
        try {
            return XmlUtil.getElementContent(el);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error(this, t);
            }
            throw new WebdavBadRequest();
        }
    }

    protected String generateHtml(HttpServletRequest req, WebdavNsNode node) throws WebdavException {
        try {
            Sbuff sb = new Sbuff();
            sb.lines(new String[]{"<html>", "  <head>"});
            sb.append("    <title>");
            sb.append(node.getDisplayname());
            sb.line("</title>");
            sb.lines(new String[]{"</head>", "<body>"});
            sb.append("    <h1>");
            sb.append(node.getDisplayname());
            sb.line("</h1>");
            sb.line("  <hr>");
            sb.line("  <table width=\"100%\" cellspacing=\"0\" cellpadding=\"4\">");
            for (WebdavNsNode child : this.getChildren(node, null)) {
                sb.line("<tr>");
                if (node.isCollection()) {
                    // empty if block
                }
                sb.line("  <td align=\"left\">");
                sb.append("<a href=\"");
                sb.append(req.getContextPath());
                sb.append(child.getUri());
                sb.append("\">");
                sb.append(child.getDisplayname());
                sb.line("</a>");
                sb.line("</td>");
                sb.line("  <td align=\"left\">");
                String lastMod = child.getLastmodDate();
                if (lastMod != null) {
                    sb.line(lastMod);
                } else {
                    sb.line("&nbsp;");
                }
                sb.line("</td>");
                sb.append("</tr>\r\n");
            }
            sb.line("</table>");
            sb.line("</body>");
            sb.line("</html>");
            return sb.toString();
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    protected void debug(String msg) {
        super.debug("[" + this.sessNum + "] " + msg);
    }

    @Override
    protected void warn(String msg) {
        super.warn("[" + this.sessNum + "] " + msg);
    }

    @Override
    protected void error(Throwable t) {
        super.error(t);
    }

    protected void logIt(String msg) {
        super.info("[" + this.sessNum + "] " + msg);
    }

    static {
        knownProperties = new QName[]{WebdavTags.principalCollectionSet};
    }

    private static class Sbuff {
        StringBuilder sb = new StringBuilder();

        private Sbuff() {
        }

        public void lines(String[] ss) {
            for (int i = 0; i < ss.length; ++i) {
                this.line(ss[i]);
            }
        }

        public void line(String s) {
            this.sb.append(s);
            this.sb.append("\r\n");
        }

        public void append(String s) {
            this.sb.append(s);
        }

        public String toString() {
            return this.sb.toString();
        }
    }

    private class DebugReader
    extends FilterReader {
        StringBuilder sb;

        public DebugReader(Reader rdr) {
            super(rdr);
            this.sb = new StringBuilder();
        }

        @Override
        public void close() throws IOException {
            if (this.sb != null) {
                WebdavNsIntf.this.debug(this.sb.toString());
            }
            super.close();
        }

        @Override
        public int read() throws IOException {
            int c = super.read();
            if (c == -1) {
                if (this.sb != null) {
                    WebdavNsIntf.this.debug(this.sb.toString());
                    this.sb = null;
                }
                return c;
            }
            if (this.sb != null) {
                char ch = (char)c;
                if (ch == '\n') {
                    WebdavNsIntf.this.debug(this.sb.toString());
                    this.sb = new StringBuilder();
                } else {
                    this.sb.append(ch);
                }
            }
            return c;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int res = super.read(cbuf, off, len);
            if (res > 0 && this.sb != null) {
                this.sb.append(cbuf, off, res);
            }
            return res;
        }
    }

    public static class AclInfo {
        public String what;
        public QName errorTag;
        public Acl acl;

        public AclInfo(String uri) {
            this.what = uri;
        }
    }

    public static class PutContentResult {
        public WebdavNsNode node;
        public boolean created;
        public boolean emitEtag = true;
    }

    public static class Content {
        public boolean written;
        public Reader rdr;
        public InputStream stream = null;
        public String contentType;
        public long contentLength = -1L;
    }

    protected static class SessCt {
        int sessNum;

        protected SessCt() {
        }
    }
}

