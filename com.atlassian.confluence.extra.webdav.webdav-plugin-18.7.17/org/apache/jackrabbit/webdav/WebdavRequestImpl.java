/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.DispatcherType
 *  javax.servlet.ReadListener
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpUpgradeHandler
 *  javax.servlet.http.Part
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.webdav.AbstractLocatorFactory;
import org.apache.jackrabbit.webdav.ContentCodingAwareRequest;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.bind.BindInfo;
import org.apache.jackrabbit.webdav.bind.RebindInfo;
import org.apache.jackrabbit.webdav.bind.UnbindInfo;
import org.apache.jackrabbit.webdav.header.CodedUrlHeader;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.header.IfHeader;
import org.apache.jackrabbit.webdav.header.LabelHeader;
import org.apache.jackrabbit.webdav.header.OverwriteHeader;
import org.apache.jackrabbit.webdav.header.PollTimeoutHeader;
import org.apache.jackrabbit.webdav.header.TimeoutHeader;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;
import org.apache.jackrabbit.webdav.ordering.OrderPatch;
import org.apache.jackrabbit.webdav.ordering.Position;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.apache.jackrabbit.webdav.transaction.TransactionInfo;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.OptionsInfo;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class WebdavRequestImpl
implements WebdavRequest,
DavConstants,
ContentCodingAwareRequest {
    private static Logger log = LoggerFactory.getLogger(WebdavRequestImpl.class);
    private final HttpServletRequest httpRequest;
    private final DavLocatorFactory factory;
    private final IfHeader ifHeader;
    private final String hrefPrefix;
    private DavSession session;
    private int propfindType = 1;
    private DavPropertyNameSet propfindProps;
    private DavPropertySet proppatchSet;
    private List<PropEntry> proppatchList;
    private List<String> requestContentCodings = null;

    public WebdavRequestImpl(HttpServletRequest httpRequest, DavLocatorFactory factory) {
        this(httpRequest, factory, true);
    }

    public WebdavRequestImpl(HttpServletRequest httpRequest, DavLocatorFactory factory, boolean createAbsoluteURI) {
        this.httpRequest = httpRequest;
        this.factory = factory;
        this.ifHeader = new IfHeader(httpRequest);
        String host = this.getHeader("Host");
        String scheme = this.getScheme();
        String uriPrefix = scheme + "://" + host + this.getContextPath();
        this.hrefPrefix = createAbsoluteURI ? uriPrefix : this.getContextPath();
    }

    @Override
    public void setDavSession(DavSession session) {
        this.session = session;
        if (session != null) {
            String lt = this.getLockToken();
            if (lt != null) {
                session.addLockToken(lt);
            }
            Iterator<String> it = this.ifHeader.getAllTokens();
            while (it.hasNext()) {
                String ifHeaderToken = it.next();
                session.addLockToken(ifHeaderToken);
            }
        }
    }

    @Override
    public DavSession getDavSession() {
        return this.session;
    }

    @Override
    public DavResourceLocator getRequestLocator() {
        String ctx;
        String path = this.getRequestURI();
        if (path.startsWith(ctx = this.getContextPath())) {
            path = path.substring(ctx.length());
        }
        return this.factory.createResourceLocator(this.hrefPrefix, path);
    }

    @Override
    public DavResourceLocator getDestinationLocator() throws DavException {
        return this.getHrefLocator(this.httpRequest.getHeader("Destination"), true);
    }

    private DavResourceLocator getHrefLocator(String href, boolean forDestination) throws DavException {
        String ref = href;
        if (ref != null) {
            try {
                URI uri = new URI(ref).normalize();
                String auth = uri.getAuthority();
                ref = uri.getRawPath();
                if (auth == null) {
                    if (ref.startsWith("//") || !ref.startsWith("/")) {
                        log.warn("expected absolute path but found " + ref);
                        throw new DavException(400);
                    }
                } else if (!auth.equals(this.httpRequest.getHeader("Host"))) {
                    throw new DavException(403);
                }
            }
            catch (URISyntaxException e) {
                log.warn("malformed uri: " + href, (Throwable)e);
                throw new DavException(400);
            }
            String contextPath = this.httpRequest.getContextPath();
            if (ref.startsWith(contextPath)) {
                ref = ref.substring(contextPath.length());
            } else {
                throw new DavException(403);
            }
        }
        if (this.factory instanceof AbstractLocatorFactory) {
            return ((AbstractLocatorFactory)this.factory).createResourceLocator(this.hrefPrefix, ref, forDestination);
        }
        return this.factory.createResourceLocator(this.hrefPrefix, ref);
    }

    @Override
    public DavResourceLocator getHrefLocator(String href) throws DavException {
        return this.getHrefLocator(href, false);
    }

    @Override
    public DavResourceLocator getMemberLocator(String segment) {
        String path = (this.getRequestLocator().getHref(true) + segment).substring(this.hrefPrefix.length());
        return this.factory.createResourceLocator(this.hrefPrefix, path);
    }

    @Override
    public boolean isOverwrite() {
        return new OverwriteHeader(this.httpRequest).isOverwrite();
    }

    @Override
    public int getDepth(int defaultValue) {
        return DepthHeader.parse(this.httpRequest, defaultValue).getDepth();
    }

    @Override
    public int getDepth() {
        return this.getDepth(Integer.MAX_VALUE);
    }

    @Override
    public long getTimeout() {
        return TimeoutHeader.parse(this.httpRequest, Integer.MIN_VALUE).getTimeout();
    }

    @Override
    public String getLockToken() {
        return CodedUrlHeader.parse(this.httpRequest, "Lock-Token").getCodedUrl();
    }

    @Override
    public Document getRequestDocument() throws DavException {
        Document requestDocument = null;
        if (this.httpRequest.getContentLength() == 0) {
            return requestDocument;
        }
        try {
            InputStream in = this.getDecodedInputStream();
            if (in != null) {
                BufferedInputStream bin = new BufferedInputStream(in);
                ((InputStream)bin).mark(1);
                boolean isEmpty = -1 == ((InputStream)bin).read();
                ((InputStream)bin).reset();
                if (!isEmpty) {
                    requestDocument = DomUtil.parseDocument(bin);
                }
            }
        }
        catch (IOException e) {
            Throwable cause;
            if (log.isDebugEnabled()) {
                log.debug("Unable to build an XML Document from the request body: " + e.getMessage());
            }
            throw (cause = e.getCause()) instanceof DavException ? (DavException)cause : new DavException(400);
        }
        catch (ParserConfigurationException e) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to build an XML Document from the request body: " + e.getMessage());
            }
            throw new DavException(500);
        }
        catch (SAXException e) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to build an XML Document from the request body: " + e.getMessage());
            }
            throw new DavException(400);
        }
        return requestDocument;
    }

    @Override
    public int getPropFindType() throws DavException {
        if (this.propfindProps == null) {
            this.parsePropFindRequest();
        }
        return this.propfindType;
    }

    @Override
    public DavPropertyNameSet getPropFindProperties() throws DavException {
        if (this.propfindProps == null) {
            this.parsePropFindRequest();
        }
        return this.propfindProps;
    }

    private InputStream getDecodedInputStream() throws IOException {
        List<String> contentCodings = this.getRequestContentCodings();
        int len = contentCodings.size();
        log.trace("content codings: " + contentCodings);
        Object result = this.httpRequest.getInputStream();
        for (int i = 1; i <= len; ++i) {
            String s = contentCodings.get(len - i);
            log.trace("decoding: " + s);
            if ("gzip".equals(s)) {
                result = new GZIPInputStream((InputStream)result);
                continue;
            }
            if ("deflate".equals(s)) {
                result = new InflaterInputStream((InputStream)result);
                continue;
            }
            String message = "Unsupported content coding: " + s;
            try {
                Element condition = DomUtil.createElement(DomUtil.createDocument(), PRECONDITION_SUPPORTED);
                throw new IOException(new DavException(415, message, null, condition));
            }
            catch (ParserConfigurationException ex) {
                throw new IOException(message);
            }
        }
        return result;
    }

    @Override
    public List<String> getRequestContentCodings() {
        if (this.requestContentCodings == null) {
            this.requestContentCodings = AbstractWebdavServlet.getContentCodings(this.httpRequest);
        }
        return this.requestContentCodings;
    }

    @Override
    public String getAcceptableCodings() {
        return "deflate, gzip";
    }

    private void parsePropFindRequest() throws DavException {
        this.propfindProps = new DavPropertyNameSet();
        Document requestDocument = this.getRequestDocument();
        if (requestDocument == null) {
            return;
        }
        Element root = requestDocument.getDocumentElement();
        if (!"propfind".equals(root.getLocalName())) {
            log.info("PropFind-Request has no <propfind> tag.");
            throw new DavException(400, "PropFind-Request has no <propfind> tag.");
        }
        DavPropertyNameSet include = null;
        ElementIterator it = DomUtil.getChildren(root);
        int propfindTypeFound = 0;
        while (it.hasNext()) {
            Element child = it.nextElement();
            String nodeName = child.getLocalName();
            if (!NAMESPACE.getURI().equals(child.getNamespaceURI())) continue;
            if ("prop".equals(nodeName)) {
                this.propfindType = 0;
                this.propfindProps = new DavPropertyNameSet(child);
                ++propfindTypeFound;
                continue;
            }
            if ("propname".equals(nodeName)) {
                this.propfindType = 2;
                ++propfindTypeFound;
                continue;
            }
            if ("allprop".equals(nodeName)) {
                this.propfindType = 1;
                ++propfindTypeFound;
                continue;
            }
            if (!"include".equals(nodeName)) continue;
            include = new DavPropertyNameSet();
            ElementIterator pit = DomUtil.getChildren(child);
            while (pit.hasNext()) {
                include.add(DavPropertyName.createFromXml(pit.nextElement()));
            }
        }
        if (propfindTypeFound > 1) {
            log.info("Multiple top-level propfind instructions");
            throw new DavException(400, "Multiple top-level propfind instructions");
        }
        if (include != null) {
            if (this.propfindType == 1) {
                this.propfindType = 3;
                this.propfindProps = include;
            } else {
                throw new DavException(400, "<include> goes only with <allprop>");
            }
        }
    }

    @Override
    public List<? extends PropEntry> getPropPatchChangeList() throws DavException {
        if (this.proppatchList == null) {
            this.parsePropPatchRequest();
        }
        return this.proppatchList;
    }

    private void parsePropPatchRequest() throws DavException {
        this.proppatchSet = new DavPropertySet();
        this.proppatchList = new ArrayList<PropEntry>();
        Document requestDocument = this.getRequestDocument();
        if (requestDocument == null) {
            throw new DavException(400, "Invalid request body.");
        }
        Element root = requestDocument.getDocumentElement();
        if (!DomUtil.matches(root, "propertyupdate", NAMESPACE)) {
            log.warn("PropPatch-Request has no <DAV:propertyupdate> tag.");
            throw new DavException(400, "PropPatch-Request has no <propertyupdate> tag.");
        }
        ElementIterator it = DomUtil.getChildren(root);
        while (it.hasNext()) {
            DefaultDavProperty<?> davProp;
            ElementIterator properties;
            Element propEl;
            Element el = it.nextElement();
            if (DomUtil.matches(el, "set", NAMESPACE)) {
                propEl = DomUtil.getChildElement(el, "prop", NAMESPACE);
                if (propEl == null) continue;
                properties = DomUtil.getChildren(propEl);
                while (properties.hasNext()) {
                    davProp = DefaultDavProperty.createFromXml(properties.nextElement());
                    this.proppatchSet.add(davProp);
                    this.proppatchList.add(davProp);
                }
                continue;
            }
            if (DomUtil.matches(el, "remove", NAMESPACE)) {
                propEl = DomUtil.getChildElement(el, "prop", NAMESPACE);
                if (propEl == null) continue;
                properties = DomUtil.getChildren(propEl);
                while (properties.hasNext()) {
                    davProp = DefaultDavProperty.createFromXml(properties.nextElement());
                    this.proppatchSet.add(davProp);
                    this.proppatchList.add(davProp.getName());
                }
                continue;
            }
            log.debug("Unknown element in DAV:propertyupdate: " + el.getNodeName());
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public LockInfo getLockInfo() throws DavException {
        boolean isDeep = this.getDepth(Integer.MAX_VALUE) == Integer.MAX_VALUE;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument == null) return new LockInfo(null, this.getTimeout(), isDeep);
        Element root = requestDocument.getDocumentElement();
        if (root.getLocalName().equals("lockinfo")) {
            return new LockInfo(root, this.getTimeout(), isDeep);
        }
        log.debug("Lock request body must start with a DAV:lockinfo element.");
        throw new DavException(400);
    }

    @Override
    public boolean matchesIfHeader(DavResource resource) {
        if (!this.ifHeader.hasValue() || resource == null || !resource.hasLock(Type.WRITE, Scope.EXCLUSIVE)) {
            return true;
        }
        boolean isMatching = false;
        String lockToken = resource.getLock(Type.WRITE, Scope.EXCLUSIVE).getToken();
        if (lockToken != null) {
            isMatching = this.matchesIfHeader(resource.getHref(), lockToken, this.getStrongETag(resource));
        }
        return isMatching;
    }

    @Override
    public boolean matchesIfHeader(String href, String token, String eTag) {
        return this.ifHeader.matches(href, token, WebdavRequestImpl.isStrongETag(eTag) ? eTag : "");
    }

    private String getStrongETag(DavResource resource) {
        String etag;
        DavProperty<?> prop = resource.getProperty(DavPropertyName.GETETAG);
        if (prop != null && prop.getValue() != null && WebdavRequestImpl.isStrongETag(etag = prop.getValue().toString())) {
            return etag;
        }
        return "";
    }

    private static boolean isStrongETag(String eTag) {
        return eTag != null && eTag.length() > 0 && !eTag.startsWith("W\\");
    }

    @Override
    public String getTransactionId() {
        return CodedUrlHeader.parse(this.httpRequest, "TransactionId").getCodedUrl();
    }

    @Override
    public TransactionInfo getTransactionInfo() throws DavException {
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            return new TransactionInfo(requestDocument.getDocumentElement());
        }
        return null;
    }

    @Override
    public String getSubscriptionId() {
        return CodedUrlHeader.parse(this.httpRequest, "SubscriptionId").getCodedUrl();
    }

    @Override
    public long getPollTimeout() {
        return PollTimeoutHeader.parseHeader(this.httpRequest, 0L).getTimeout();
    }

    @Override
    public SubscriptionInfo getSubscriptionInfo() throws DavException {
        Element root;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null && "subscriptioninfo".equals((root = requestDocument.getDocumentElement()).getLocalName())) {
            int depth = this.getDepth(0);
            return new SubscriptionInfo(root, this.getTimeout(), depth == Integer.MAX_VALUE);
        }
        return null;
    }

    @Override
    public String getOrderingType() {
        return this.getHeader("Ordering-Type");
    }

    @Override
    public Position getPosition() {
        String[] typeNSegment;
        String h = this.getHeader("Position");
        Position pos = null;
        if (h != null && (typeNSegment = h.split("\\s")).length == 2) {
            try {
                pos = new Position(typeNSegment[0], typeNSegment[1]);
            }
            catch (IllegalArgumentException e) {
                log.error("Cannot parse Position header: " + e.getMessage());
            }
        }
        return pos;
    }

    @Override
    public OrderPatch getOrderPatch() throws DavException {
        OrderPatch op = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            Element root = requestDocument.getDocumentElement();
            op = OrderPatch.createFromXml(root);
        } else {
            log.error("Error while building xml document from ORDERPATH request body.");
        }
        return op;
    }

    @Override
    public String getLabel() {
        LabelHeader label = LabelHeader.parse(this);
        if (label != null) {
            return label.getLabel();
        }
        return null;
    }

    @Override
    public LabelInfo getLabelInfo() throws DavException {
        LabelInfo lInfo = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            Element root = requestDocument.getDocumentElement();
            int depth = this.getDepth(0);
            lInfo = new LabelInfo(root, depth);
        }
        return lInfo;
    }

    @Override
    public MergeInfo getMergeInfo() throws DavException {
        MergeInfo mInfo = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            mInfo = new MergeInfo(requestDocument.getDocumentElement());
        }
        return mInfo;
    }

    @Override
    public UpdateInfo getUpdateInfo() throws DavException {
        UpdateInfo uInfo = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            uInfo = new UpdateInfo(requestDocument.getDocumentElement());
        }
        return uInfo;
    }

    @Override
    public ReportInfo getReportInfo() throws DavException {
        ReportInfo rInfo = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            rInfo = new ReportInfo(requestDocument.getDocumentElement(), this.getDepth(0));
        }
        return rInfo;
    }

    @Override
    public OptionsInfo getOptionsInfo() throws DavException {
        OptionsInfo info = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            info = OptionsInfo.createFromXml(requestDocument.getDocumentElement());
        }
        return info;
    }

    @Override
    public RebindInfo getRebindInfo() throws DavException {
        RebindInfo info = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            info = RebindInfo.createFromXml(requestDocument.getDocumentElement());
        }
        return info;
    }

    @Override
    public UnbindInfo getUnbindInfo() throws DavException {
        UnbindInfo info = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            info = UnbindInfo.createFromXml(requestDocument.getDocumentElement());
        }
        return info;
    }

    @Override
    public BindInfo getBindInfo() throws DavException {
        BindInfo info = null;
        Document requestDocument = this.getRequestDocument();
        if (requestDocument != null) {
            info = BindInfo.createFromXml(requestDocument.getDocumentElement());
        }
        return info;
    }

    public String getAuthType() {
        return this.httpRequest.getAuthType();
    }

    public Cookie[] getCookies() {
        return this.httpRequest.getCookies();
    }

    public long getDateHeader(String s) {
        return this.httpRequest.getDateHeader(s);
    }

    public String getHeader(String s) {
        return this.httpRequest.getHeader(s);
    }

    public Enumeration<String> getHeaders(String s) {
        return this.httpRequest.getHeaders(s);
    }

    public Enumeration<String> getHeaderNames() {
        return this.httpRequest.getHeaderNames();
    }

    public int getIntHeader(String s) {
        return this.httpRequest.getIntHeader(s);
    }

    public String getMethod() {
        return this.httpRequest.getMethod();
    }

    public String getPathInfo() {
        return this.httpRequest.getPathInfo();
    }

    public String getPathTranslated() {
        return this.httpRequest.getPathTranslated();
    }

    public String getContextPath() {
        return this.httpRequest.getContextPath();
    }

    public String getQueryString() {
        return this.httpRequest.getQueryString();
    }

    public String getRemoteUser() {
        return this.httpRequest.getRemoteUser();
    }

    public boolean isUserInRole(String s) {
        return this.httpRequest.isUserInRole(s);
    }

    public Principal getUserPrincipal() {
        return this.httpRequest.getUserPrincipal();
    }

    public String getRequestedSessionId() {
        return this.httpRequest.getRequestedSessionId();
    }

    public String getRequestURI() {
        return this.httpRequest.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        return this.httpRequest.getRequestURL();
    }

    public String getServletPath() {
        return this.httpRequest.getServletPath();
    }

    public HttpSession getSession(boolean b) {
        return this.httpRequest.getSession(b);
    }

    public HttpSession getSession() {
        return this.httpRequest.getSession();
    }

    public boolean isRequestedSessionIdValid() {
        return this.httpRequest.isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return this.httpRequest.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        return this.httpRequest.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl() {
        return this.httpRequest.isRequestedSessionIdFromUrl();
    }

    public Object getAttribute(String s) {
        return this.httpRequest.getAttribute(s);
    }

    public Enumeration<String> getAttributeNames() {
        return this.httpRequest.getAttributeNames();
    }

    public String getCharacterEncoding() {
        return this.httpRequest.getCharacterEncoding();
    }

    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        this.httpRequest.setCharacterEncoding(s);
    }

    public int getContentLength() {
        return this.httpRequest.getContentLength();
    }

    public String getContentType() {
        return this.httpRequest.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException {
        return new MyServletInputStream(this.getDecodedInputStream());
    }

    public String getParameter(String s) {
        return this.httpRequest.getParameter(s);
    }

    public Enumeration<String> getParameterNames() {
        return this.httpRequest.getParameterNames();
    }

    public String[] getParameterValues(String s) {
        return this.httpRequest.getParameterValues(s);
    }

    public Map<String, String[]> getParameterMap() {
        return this.httpRequest.getParameterMap();
    }

    public String getProtocol() {
        return this.httpRequest.getProtocol();
    }

    public String getScheme() {
        return this.httpRequest.getScheme();
    }

    public String getServerName() {
        return this.httpRequest.getServerName();
    }

    public int getServerPort() {
        return this.httpRequest.getServerPort();
    }

    public BufferedReader getReader() throws IOException {
        return this.httpRequest.getReader();
    }

    public String getRemoteAddr() {
        return this.httpRequest.getRemoteAddr();
    }

    public String getRemoteHost() {
        return this.httpRequest.getRemoteHost();
    }

    public void setAttribute(String s, Object o) {
        this.httpRequest.setAttribute(s, o);
    }

    public void removeAttribute(String s) {
        this.httpRequest.removeAttribute(s);
    }

    public Locale getLocale() {
        return this.httpRequest.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        return this.httpRequest.getLocales();
    }

    public boolean isSecure() {
        return this.httpRequest.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return this.httpRequest.getRequestDispatcher(s);
    }

    public String getRealPath(String s) {
        return this.httpRequest.getRealPath(s);
    }

    public int getRemotePort() {
        return this.httpRequest.getRemotePort();
    }

    public String getLocalName() {
        return this.httpRequest.getLocalName();
    }

    public String getLocalAddr() {
        return this.httpRequest.getLocalAddr();
    }

    public int getLocalPort() {
        return this.httpRequest.getLocalPort();
    }

    public String changeSessionId() {
        return this.httpRequest.changeSessionId();
    }

    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return this.httpRequest.authenticate(response);
    }

    public void login(String username, String password) throws ServletException {
        this.httpRequest.login(username, password);
    }

    public void logout() throws ServletException {
        this.httpRequest.logout();
    }

    public Collection<Part> getParts() throws IOException, ServletException {
        return this.httpRequest.getParts();
    }

    public Part getPart(String name) throws IOException, ServletException {
        return this.httpRequest.getPart(name);
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return (T)this.httpRequest.upgrade(handlerClass);
    }

    public long getContentLengthLong() {
        return this.httpRequest.getContentLengthLong();
    }

    public ServletContext getServletContext() {
        return this.httpRequest.getServletContext();
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return this.httpRequest.startAsync();
    }

    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return this.httpRequest.startAsync(servletRequest, servletResponse);
    }

    public boolean isAsyncStarted() {
        return this.httpRequest.isAsyncStarted();
    }

    public boolean isAsyncSupported() {
        return this.httpRequest.isAsyncSupported();
    }

    public AsyncContext getAsyncContext() {
        return this.httpRequest.getAsyncContext();
    }

    public DispatcherType getDispatcherType() {
        return this.httpRequest.getDispatcherType();
    }

    private static class MyServletInputStream
    extends ServletInputStream {
        private final InputStream delegate;

        public MyServletInputStream(InputStream delegate) {
            this.delegate = delegate;
        }

        public int available() throws IOException {
            return this.delegate.available();
        }

        public void close() throws IOException {
            this.delegate.close();
        }

        public boolean equals(Object other) {
            return this.delegate.equals(other);
        }

        public int hashCode() {
            return this.delegate.hashCode();
        }

        public void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        public boolean markSupported() {
            return this.delegate.markSupported();
        }

        public int read() throws IOException {
            return this.delegate.read();
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        public int readLine(byte[] b, int off, int len) throws IOException {
            throw new UnsupportedOperationException();
        }

        public void reset() throws IOException {
            this.delegate.reset();
        }

        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        public String toString() {
            return this.delegate.toString();
        }

        public boolean isFinished() {
            throw new UnsupportedOperationException();
        }

        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }
}

