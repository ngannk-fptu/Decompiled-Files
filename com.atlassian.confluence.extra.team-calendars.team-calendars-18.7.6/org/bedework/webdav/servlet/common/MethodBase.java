/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.ws.Holder
 */
package org.bedework.webdav.servlet.common;

import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import org.bedework.util.misc.Logged;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.Headers;
import org.bedework.webdav.servlet.common.SecureXml;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavProperty;
import org.bedework.webdav.servlet.shared.WebdavStatusCode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class MethodBase
extends Logged
implements SecureXml {
    protected boolean dumpContent;
    protected boolean hasBriefHeader;
    protected WebdavNsIntf nsIntf;
    private String resourceUri;
    protected XmlEmit xml;
    private SimpleDateFormat httpDateFormatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ");

    public abstract void init();

    public abstract void doMethod(HttpServletRequest var1, HttpServletResponse var2) throws WebdavException;

    public void init(WebdavNsIntf nsIntf, boolean dumpContent) throws WebdavException {
        this.nsIntf = nsIntf;
        this.debug = this.getLogger().isDebugEnabled();
        this.dumpContent = dumpContent;
        this.xml = nsIntf.getXmlEmit();
        this.resourceUri = null;
        this.init();
    }

    public WebdavNsIntf getNsIntf() {
        return this.nsIntf;
    }

    public String getResourceUri(HttpServletRequest req) throws WebdavException {
        if (this.resourceUri != null) {
            return this.resourceUri;
        }
        this.resourceUri = this.getNsIntf().getResourceUri(req);
        if (this.debug) {
            this.debug("resourceUri: " + this.resourceUri);
        }
        return this.resourceUri;
    }

    public static List<String> fixPath(String path) throws WebdavException {
        String decoded;
        if (path == null) {
            return null;
        }
        try {
            decoded = URLDecoder.decode(path, "UTF8");
        }
        catch (Throwable t) {
            throw new WebdavException("bad path: " + path);
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
        while (decoded.contains("//")) {
            decoded = decoded.replaceAll("//", "/");
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
        return al;
    }

    protected int defaultDepth(int depth, int def) {
        if (depth < 0) {
            return def;
        }
        return depth;
    }

    protected void checkDepth(int depth, int val) throws WebdavException {
        if (depth != val) {
            throw new WebdavBadRequest();
        }
    }

    protected String getStatus(int status, String message) {
        if (message == null) {
            message = WebdavStatusCode.getMessage(status);
        }
        return "HTTP/1.1 " + status + " " + message;
    }

    protected void addStatus(int status, String message) throws WebdavException {
        try {
            if (message == null) {
                message = WebdavStatusCode.getMessage(status);
            }
            this.property(WebdavTags.status, "HTTP/1.1 " + status + " " + message);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected void addHeaders(HttpServletRequest req, HttpServletResponse resp, WebdavNsNode node) throws WebdavException {
        this.addDavHeader(resp, node);
        resp.addHeader("MS-Author-Via", "DAV");
        StringBuilder methods = new StringBuilder();
        for (String name : this.getNsIntf().getMethodNames()) {
            if (methods.length() > 0) {
                methods.append(", ");
            }
            methods.append(name);
        }
        resp.addHeader("Allow", methods.toString());
    }

    public void checkServerInfo(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        String curToken = this.getNsIntf().getServerInfo().getToken();
        String method = req.getMethod();
        boolean sendServerInfoUrl = false;
        String theirToken = req.getHeader("server-info-token");
        if (theirToken == null) {
            sendServerInfoUrl = method.equalsIgnoreCase("options");
        } else if (!theirToken.equals(curToken)) {
            sendServerInfoUrl = true;
        }
        if (sendServerInfoUrl) {
            resp.addHeader("Link", "<" + this.getNsIntf().makeServerInfoUrl(req) + ">; rel=\"server-info\"; token=\"" + curToken + "\"");
        }
    }

    protected void addDavHeader(HttpServletResponse resp, WebdavNsNode node) throws WebdavException {
        resp.addHeader("DAV", this.getNsIntf().getDavHeader(node));
    }

    protected Document parseContent(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        try {
            this.hasBriefHeader = Headers.brief(req);
            return this.parseContent(req.getContentLength(), this.getNsIntf().getReader(req));
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected Document parseContent(int contentLength, Reader reader) throws WebdavException {
        return this.parseXmlSafely(contentLength, reader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String formatHTTPDate(Timestamp val) {
        if (val == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = this.httpDateFormatter;
        synchronized (simpleDateFormat) {
            return this.httpDateFormatter.format(val) + "GMT";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doPropFind(WebdavNsNode node, Collection<WebdavProperty> props) throws WebdavException {
        WebdavNsIntf intf = this.getNsIntf();
        ArrayList<WebdavProperty> unknowns = new ArrayList<WebdavProperty>();
        Holder openFlag = new Holder((Object)Boolean.FALSE);
        XmlNotifier notifier = new XmlNotifier((Holder<Boolean>)openFlag);
        try {
            this.xml.setNotifier(notifier);
            for (WebdavProperty pr : props) {
                if (!intf.knownProperty(node, pr)) {
                    unknowns.add(pr);
                    continue;
                }
                this.addNs(pr.getTag().getNamespaceURI());
                if (intf.generatePropValue(node, pr, false)) continue;
                unknowns.add(pr);
            }
            if (((Boolean)openFlag.value).booleanValue()) {
                this.closeTag(WebdavTags.prop);
                this.addStatus(node.getStatus(), null);
                this.closeTag(WebdavTags.propstat);
            }
            this.xml.setNotifier(null);
            if (!this.hasBriefHeader && !unknowns.isEmpty()) {
                this.openTag(WebdavTags.propstat);
                this.openTag(WebdavTags.prop);
                for (WebdavProperty prop : unknowns) {
                    try {
                        this.xml.emptyTag(prop.getTag());
                    }
                    catch (Throwable t) {
                        throw new WebdavException(t);
                    }
                }
                this.closeTag(WebdavTags.prop);
                this.addStatus(404, null);
                this.closeTag(WebdavTags.propstat);
            }
        }
        finally {
            this.xml.setNotifier(null);
        }
    }

    protected Collection<Element> getChildren(Node nd) throws WebdavException {
        try {
            return XmlUtil.getElements(nd);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error(this, t);
            }
            throw new WebdavBadRequest(t.getMessage());
        }
    }

    protected Element[] getChildrenArray(Node nd) throws WebdavException {
        try {
            return XmlUtil.getElementsArray(nd);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error(this, t);
            }
            throw new WebdavBadRequest(t.getMessage());
        }
    }

    protected Element getOnlyChild(Node nd) throws WebdavException {
        try {
            return XmlUtil.getOnlyElement(nd);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error(this, t);
            }
            throw new WebdavBadRequest(t.getMessage());
        }
    }

    protected String getElementContent(Element el) throws WebdavException {
        try {
            return XmlUtil.getElementContent(el);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error(this, t);
            }
            throw new WebdavBadRequest(t.getMessage());
        }
    }

    protected boolean isEmpty(Element el) throws WebdavException {
        try {
            return XmlUtil.isEmpty(el);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error(this, t);
            }
            throw new WebdavBadRequest(t.getMessage());
        }
    }

    protected void startEmit(HttpServletResponse resp) throws WebdavException {
        try {
            this.xml.startEmit(resp.getWriter());
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void addNs(String val) throws WebdavException {
        if (this.xml.getNameSpace(val) == null) {
            try {
                this.xml.addNs(new XmlEmit.NameSpace(val, null), false);
            }
            catch (IOException e) {
                throw new WebdavException(e);
            }
        }
    }

    public String getNsAbbrev(String ns) {
        return this.xml.getNsAbbrev(ns);
    }

    protected void openTag(QName tag) throws WebdavException {
        try {
            this.xml.openTag(tag);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected void openTagNoNewline(QName tag) throws WebdavException {
        try {
            this.xml.openTagNoNewline(tag);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected void closeTag(QName tag) throws WebdavException {
        try {
            this.xml.closeTag(tag);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void emptyTag(QName tag) throws WebdavException {
        try {
            this.xml.emptyTag(tag);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void emptyTag(Node nd) throws WebdavException {
        String ns = nd.getNamespaceURI();
        String ln = nd.getLocalName();
        this.emptyTag(new QName(ns, ln));
    }

    public void property(QName tag, String val) throws WebdavException {
        try {
            this.xml.property(tag, val);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void cdataProperty(QName tag, String attrName, String attrVal, String val) throws WebdavException {
        try {
            if (attrName == null) {
                this.xml.cdataProperty(tag, val);
            } else {
                this.xml.openTagSameLine(tag, attrName, attrVal);
                this.xml.cdataValue(val);
                this.xml.closeTagSameLine(tag);
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void property(QName tag, Reader val) throws WebdavException {
        try {
            this.xml.property(tag, val);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void propertyTagVal(QName tag, QName tagVal) throws WebdavException {
        try {
            this.xml.propertyTagVal(tag, tagVal);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected void flush() throws WebdavException {
        try {
            this.xml.flush();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private class XmlNotifier
    extends XmlEmit.Notifier {
        private boolean enabled;
        private Holder<Boolean> openFlag;

        XmlNotifier(Holder<Boolean> openFlag) {
            this.openFlag = openFlag;
            this.enabled = true;
        }

        @Override
        public void doNotification() throws Throwable {
            this.enabled = false;
            if (!((Boolean)this.openFlag.value).booleanValue()) {
                this.openFlag.value = true;
                MethodBase.this.openTag(WebdavTags.propstat);
                MethodBase.this.openTag(WebdavTags.prop);
            }
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }

    public static class MethodInfo {
        private Class methodClass;
        private boolean requiresAuth;

        public MethodInfo(Class methodClass, boolean requiresAuth) {
            this.methodClass = methodClass;
            this.requiresAuth = requiresAuth;
        }

        public Class getMethodClass() {
            return this.methodClass;
        }

        public boolean getRequiresAuth() {
            return this.requiresAuth;
        }
    }
}

