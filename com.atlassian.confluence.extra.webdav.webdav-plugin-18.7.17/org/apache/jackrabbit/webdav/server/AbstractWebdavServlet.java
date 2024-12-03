/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jackrabbit.webdav.ContentCodingAwareRequest;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavRequestImpl;
import org.apache.jackrabbit.webdav.WebdavResponse;
import org.apache.jackrabbit.webdav.WebdavResponseImpl;
import org.apache.jackrabbit.webdav.bind.BindInfo;
import org.apache.jackrabbit.webdav.bind.BindableResource;
import org.apache.jackrabbit.webdav.bind.RebindInfo;
import org.apache.jackrabbit.webdav.bind.UnbindInfo;
import org.apache.jackrabbit.webdav.header.CodedUrlHeader;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.InputContextImpl;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.io.OutputContextImpl;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.ObservationResource;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;
import org.apache.jackrabbit.webdav.ordering.OrderPatch;
import org.apache.jackrabbit.webdav.ordering.OrderingResource;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.search.SearchInfo;
import org.apache.jackrabbit.webdav.search.SearchResource;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.AclResource;
import org.apache.jackrabbit.webdav.server.WebdavRequestContextHolder;
import org.apache.jackrabbit.webdav.server.WebdavRequestContextImpl;
import org.apache.jackrabbit.webdav.transaction.TransactionInfo;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.util.CSRFUtil;
import org.apache.jackrabbit.webdav.util.HttpDateTimeFormatter;
import org.apache.jackrabbit.webdav.version.ActivityResource;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.OptionsInfo;
import org.apache.jackrabbit.webdav.version.OptionsResponse;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.VersionableResource;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractWebdavServlet
extends HttpServlet
implements DavConstants {
    private static Logger log = LoggerFactory.getLogger(AbstractWebdavServlet.class);
    public static final String INIT_PARAM_MISSING_AUTH_MAPPING = "missing-auth-mapping";
    public static final String INIT_PARAM_AUTHENTICATE_HEADER = "authenticate-header";
    public static final String DEFAULT_AUTHENTICATE_HEADER = "Basic realm=\"Jackrabbit Webdav Server\"";
    public static final String INIT_PARAM_CSRF_PROTECTION = "csrf-protection";
    public static final String INIT_PARAM_CREATE_ABSOLUTE_URI = "createAbsoluteURI";
    private String authenticate_header;
    private CSRFUtil csrfUtil;
    private boolean createAbsoluteURI = true;

    public void init() throws ServletException {
        super.init();
        this.authenticate_header = this.getInitParameter(INIT_PARAM_AUTHENTICATE_HEADER);
        if (this.authenticate_header == null) {
            this.authenticate_header = DEFAULT_AUTHENTICATE_HEADER;
        }
        log.info("authenticate-header = " + this.authenticate_header);
        String csrfParam = this.getInitParameter(INIT_PARAM_CSRF_PROTECTION);
        this.csrfUtil = new CSRFUtil(csrfParam);
        log.info("csrf-protection = " + csrfParam);
        String param = this.getInitParameter(INIT_PARAM_CREATE_ABSOLUTE_URI);
        if (param != null) {
            this.createAbsoluteURI = Boolean.parseBoolean(param);
        }
        log.info("createAbsoluteURI = " + this.createAbsoluteURI);
    }

    protected abstract boolean isPreconditionValid(WebdavRequest var1, DavResource var2);

    public abstract DavSessionProvider getDavSessionProvider();

    public abstract void setDavSessionProvider(DavSessionProvider var1);

    public abstract DavLocatorFactory getLocatorFactory();

    public abstract void setLocatorFactory(DavLocatorFactory var1);

    public abstract DavResourceFactory getResourceFactory();

    public abstract void setResourceFactory(DavResourceFactory var1);

    public String getAuthenticateHeaderValue() {
        return this.authenticate_header;
    }

    protected boolean isCreateAbsoluteURI() {
        return this.createAbsoluteURI;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        block16: {
            WebdavRequestImpl webdavRequest = new WebdavRequestImpl(request, this.getLocatorFactory(), this.isCreateAbsoluteURI());
            int methodCode = DavMethods.getMethodCode(request.getMethod());
            boolean noCache = DavMethods.isDeltaVMethod(webdavRequest) && 20 != methodCode && 19 != methodCode;
            WebdavResponseImpl webdavResponse = new WebdavResponseImpl(response, noCache);
            try {
                DavResource resource;
                List<String> ces;
                WebdavRequestContextHolder.setContext(new WebdavRequestContextImpl(webdavRequest));
                if (!this.getDavSessionProvider().attachSession(webdavRequest)) {
                    return;
                }
                if (!this.csrfUtil.isValidRequest(webdavRequest)) {
                    webdavResponse.sendError(403);
                    return;
                }
                if (!(webdavRequest instanceof ContentCodingAwareRequest) && !(ces = AbstractWebdavServlet.getContentCodings(request)).isEmpty()) {
                    webdavResponse.setStatus(415);
                    webdavResponse.setHeader("Accept-Encoding", "identity");
                    webdavResponse.setContentType("text/plain; charset=UTF-8");
                    webdavResponse.getWriter().println("Content-Encodings not supported, but received: " + ces);
                    webdavResponse.getWriter().flush();
                }
                if (!this.isPreconditionValid(webdavRequest, resource = this.getResourceFactory().createResource(webdavRequest.getRequestLocator(), webdavRequest, webdavResponse))) {
                    webdavResponse.sendError(412);
                    return;
                }
                if (!this.execute(webdavRequest, webdavResponse, methodCode, resource)) {
                    super.service(request, response);
                }
            }
            catch (DavException e) {
                this.handleDavException(webdavRequest, webdavResponse, e);
            }
            catch (IOException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof DavException) {
                    this.handleDavException(webdavRequest, webdavResponse, (DavException)cause);
                    break block16;
                }
                throw ex;
            }
            finally {
                WebdavRequestContextHolder.clearContext();
                this.getDavSessionProvider().releaseSession(webdavRequest);
            }
        }
    }

    private void handleDavException(WebdavRequest webdavRequest, WebdavResponse webdavResponse, DavException ex) throws IOException {
        if (ex.getErrorCode() == 401) {
            this.sendUnauthorized(webdavRequest, webdavResponse, ex);
        } else {
            Element condition = ex.getErrorCondition();
            if (DomUtil.matches(condition, ContentCodingAwareRequest.PRECONDITION_SUPPORTED) && webdavRequest instanceof ContentCodingAwareRequest) {
                webdavResponse.setHeader("Accept-Encoding", ((ContentCodingAwareRequest)((Object)webdavRequest)).getAcceptableCodings());
            }
            webdavResponse.sendError(ex);
        }
    }

    private void addHintAboutPotentialRequestEncodings(WebdavRequest webdavRequest, WebdavResponse webdavResponse) {
        ContentCodingAwareRequest ccr;
        List<String> ces;
        if (webdavRequest instanceof ContentCodingAwareRequest && (ces = (ccr = (ContentCodingAwareRequest)((Object)webdavRequest)).getRequestContentCodings()).isEmpty()) {
            webdavResponse.setHeader("Accept-Encoding", ccr.getAcceptableCodings());
        }
    }

    protected void sendUnauthorized(WebdavRequest request, WebdavResponse response, DavException error) throws IOException {
        response.setHeader("WWW-Authenticate", this.getAuthenticateHeaderValue());
        if (error == null || error.getErrorCode() != 401) {
            response.sendError(401);
        } else {
            response.sendError(error.getErrorCode(), error.getStatusPhrase());
        }
    }

    protected boolean execute(WebdavRequest request, WebdavResponse response, int method, DavResource resource) throws ServletException, IOException, DavException {
        switch (method) {
            case 2: {
                this.doGet(request, response, resource);
                break;
            }
            case 3: {
                this.doHead(request, response, resource);
                break;
            }
            case 7: {
                this.doPropFind(request, response, resource);
                break;
            }
            case 8: {
                this.doPropPatch(request, response, resource);
                break;
            }
            case 4: {
                this.doPost(request, response, resource);
                break;
            }
            case 6: {
                this.doPut(request, response, resource);
                break;
            }
            case 5: {
                this.doDelete(request, response, resource);
                break;
            }
            case 10: {
                this.doCopy(request, response, resource);
                break;
            }
            case 11: {
                this.doMove(request, response, resource);
                break;
            }
            case 9: {
                this.doMkCol(request, response, resource);
                break;
            }
            case 1: {
                this.doOptions(request, response, resource);
                break;
            }
            case 12: {
                this.doLock(request, response, resource);
                break;
            }
            case 13: {
                this.doUnlock(request, response, resource);
                break;
            }
            case 14: {
                this.doOrderPatch(request, response, resource);
                break;
            }
            case 15: {
                this.doSubscribe(request, response, resource);
                break;
            }
            case 16: {
                this.doUnsubscribe(request, response, resource);
                break;
            }
            case 17: {
                this.doPoll(request, response, resource);
                break;
            }
            case 18: {
                this.doSearch(request, response, resource);
                break;
            }
            case 20: {
                this.doVersionControl(request, response, resource);
                break;
            }
            case 24: {
                this.doLabel(request, response, resource);
                break;
            }
            case 19: {
                this.doReport(request, response, resource);
                break;
            }
            case 21: {
                this.doCheckin(request, response, resource);
                break;
            }
            case 22: {
                this.doCheckout(request, response, resource);
                break;
            }
            case 23: {
                this.doUncheckout(request, response, resource);
                break;
            }
            case 25: {
                this.doMerge(request, response, resource);
                break;
            }
            case 26: {
                this.doUpdate(request, response, resource);
                break;
            }
            case 27: {
                this.doMkWorkspace(request, response, resource);
                break;
            }
            case 29: {
                this.doMkActivity(request, response, resource);
                break;
            }
            case 28: {
                this.doBaselineControl(request, response, resource);
                break;
            }
            case 30: {
                this.doAcl(request, response, resource);
                break;
            }
            case 31: {
                this.doRebind(request, response, resource);
                break;
            }
            case 32: {
                this.doUnbind(request, response, resource);
                break;
            }
            case 33: {
                this.doBind(request, response, resource);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    protected void doOptions(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        response.addHeader("DAV", resource.getComplianceClass());
        response.addHeader("Allow", resource.getSupportedMethods());
        response.addHeader("MS-Author-Via", "DAV");
        if (resource instanceof SearchResource) {
            String[] langs;
            for (String lang : langs = ((SearchResource)((Object)resource)).getQueryGrammerSet().getQueryLanguages()) {
                response.addHeader("DASL", "<" + lang + ">");
            }
        }
        OptionsResponse oR = null;
        OptionsInfo oInfo = request.getOptionsInfo();
        if (oInfo != null && resource instanceof DeltaVResource) {
            oR = ((DeltaVResource)resource).getOptionResponse(oInfo);
        }
        if (oR == null) {
            response.setStatus(200);
        } else {
            response.sendXmlResponse(oR, 200);
        }
    }

    protected void doHead(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException {
        this.spoolResource(request, response, resource, false);
    }

    protected void doGet(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        this.spoolResource(request, response, resource, true);
    }

    private void spoolResource(WebdavRequest request, WebdavResponse response, DavResource resource, boolean sendContent) throws IOException {
        long modTime;
        if (!resource.exists()) {
            response.sendError(404);
            return;
        }
        long modSince = -1L;
        try {
            String value = AbstractWebdavServlet.getSingletonField(request, "If-Modified-Since");
            if (value != null) {
                modSince = HttpDateTimeFormatter.parse(value);
            }
        }
        catch (IllegalArgumentException | DateTimeParseException ex) {
            log.debug("illegal value for if-modified-since ignored: " + ex.getMessage());
        }
        if (modSince > -1L && (modTime = resource.getModificationTime()) != -1L && modTime / 1000L * 1000L <= modSince) {
            DavProperty<?> etagProp = resource.getProperty(DavPropertyName.GETETAG);
            if (etagProp != null) {
                response.setHeader("etag", etagProp.getValue().toString());
            }
            response.setStatus(304);
            return;
        }
        ServletOutputStream out = sendContent ? response.getOutputStream() : null;
        resource.spool(this.getOutputContext(response, (OutputStream)out));
        response.flushBuffer();
    }

    protected void doPropFind(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        if (!resource.exists()) {
            response.sendError(404);
            return;
        }
        int depth = request.getDepth(Integer.MAX_VALUE);
        DavPropertyNameSet requestProperties = request.getPropFindProperties();
        int propfindType = request.getPropFindType();
        MultiStatus mstatus = new MultiStatus();
        mstatus.addResourceProperties(resource, requestProperties, propfindType, depth);
        this.addHintAboutPotentialRequestEncodings(request, response);
        response.sendMultiStatus(mstatus, AbstractWebdavServlet.acceptsGzipEncoding(request) ? Collections.singletonList("gzip") : Collections.emptyList());
    }

    protected void doPropPatch(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        List<? extends PropEntry> changeList = request.getPropPatchChangeList();
        if (changeList.isEmpty()) {
            response.sendError(400);
            return;
        }
        MultiStatus ms = new MultiStatus();
        MultiStatusResponse msr = resource.alterProperties(changeList);
        ms.addResponse(msr);
        this.addHintAboutPotentialRequestEncodings(request, response);
        response.sendMultiStatus(ms);
    }

    protected void doPost(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        response.sendError(405);
    }

    protected void doPut(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        if (request.getHeader("Content-Range") != null) {
            response.sendError(400, "Content-Range in PUT request not supported");
            return;
        }
        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists()) {
            response.sendError(409);
            return;
        }
        int status = resource.exists() ? 204 : 201;
        parentResource.addMember(resource, this.getInputContext(request, (InputStream)request.getInputStream()));
        response.setStatus(status);
    }

    protected void doMkCol(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists() || !parentResource.isCollection()) {
            response.sendError(409);
            return;
        }
        if (resource.exists()) {
            response.sendError(405);
            return;
        }
        if (request.getContentLength() > 0 || request.getHeader("Transfer-Encoding") != null) {
            parentResource.addMember(resource, this.getInputContext(request, (InputStream)request.getInputStream()));
        } else {
            parentResource.addMember(resource, this.getInputContext(request, null));
        }
        response.setStatus(201);
    }

    protected void doDelete(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        DavResource parent = resource.getCollection();
        if (parent != null) {
            parent.removeMember(resource);
            response.setStatus(204);
        } else {
            response.sendError(403, "Cannot remove the root resource.");
        }
    }

    protected void doCopy(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        int depth = request.getDepth(Integer.MAX_VALUE);
        if (depth != 0 && depth != Integer.MAX_VALUE) {
            response.sendError(400);
            return;
        }
        DavResource destResource = this.getResourceFactory().createResource(request.getDestinationLocator(), request, response);
        int status = this.validateDestination(destResource, request, true);
        if (status > 204) {
            response.sendError(status);
            return;
        }
        resource.copy(destResource, depth == 0);
        response.setStatus(status);
    }

    protected void doMove(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        DavResource destResource = this.getResourceFactory().createResource(request.getDestinationLocator(), request, response);
        int status = this.validateDestination(destResource, request, true);
        if (status > 204) {
            response.sendError(status);
            return;
        }
        resource.move(destResource);
        response.setStatus(status);
    }

    protected void doBind(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        if (!resource.exists()) {
            response.sendError(404);
        }
        BindInfo bindInfo = request.getBindInfo();
        DavResource oldBinding = this.getResourceFactory().createResource(request.getHrefLocator(bindInfo.getHref()), request, response);
        if (!(oldBinding instanceof BindableResource)) {
            response.sendError(405);
            return;
        }
        DavResource newBinding = this.getResourceFactory().createResource(request.getMemberLocator(bindInfo.getSegment()), request, response);
        int status = this.validateDestination(newBinding, request, false);
        if (status > 204) {
            response.sendError(status);
            return;
        }
        ((BindableResource)((Object)oldBinding)).bind(resource, newBinding);
        response.setStatus(status);
    }

    protected void doRebind(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        if (!resource.exists()) {
            response.sendError(404);
        }
        RebindInfo rebindInfo = request.getRebindInfo();
        DavResource oldBinding = this.getResourceFactory().createResource(request.getHrefLocator(rebindInfo.getHref()), request, response);
        if (!(oldBinding instanceof BindableResource)) {
            response.sendError(405);
            return;
        }
        DavResource newBinding = this.getResourceFactory().createResource(request.getMemberLocator(rebindInfo.getSegment()), request, response);
        int status = this.validateDestination(newBinding, request, false);
        if (status > 204) {
            response.sendError(status);
            return;
        }
        ((BindableResource)((Object)oldBinding)).rebind(resource, newBinding);
        response.setStatus(status);
    }

    protected void doUnbind(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        UnbindInfo unbindInfo = request.getUnbindInfo();
        DavResource srcResource = this.getResourceFactory().createResource(request.getMemberLocator(unbindInfo.getSegment()), request, response);
        resource.removeMember(srcResource);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected int validateDestination(DavResource destResource, WebdavRequest request, boolean checkHeader) throws DavException {
        DavResource col;
        if (checkHeader) {
            String destHeader = request.getHeader("Destination");
            if (destHeader == null) return 400;
            if ("".equals(destHeader)) {
                return 400;
            }
        }
        if (destResource.getLocator().equals(request.getRequestLocator())) {
            return 403;
        }
        if (!destResource.exists()) return 201;
        if (!request.isOverwrite()) return 412;
        if (!request.matchesIfHeader(destResource)) {
            return 412;
        }
        try {
            col = destResource.getCollection();
        }
        catch (IllegalArgumentException ex) {
            return 502;
        }
        col.removeMember(destResource);
        return 204;
    }

    protected void doLock(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        LockInfo lockInfo = request.getLockInfo();
        if (lockInfo.isRefreshLock()) {
            ActiveLock[] activeLocks = resource.getLocks();
            ArrayList<ActiveLock> lList = new ArrayList<ActiveLock>();
            for (ActiveLock activeLock : activeLocks) {
                String etag;
                lockInfo.setType(activeLock.getType());
                lockInfo.setScope(activeLock.getScope());
                DavProperty<?> etagProp = resource.getProperty(DavPropertyName.GETETAG);
                String string = etag = etagProp != null ? String.valueOf(etagProp.getValue()) : "";
                if (!request.matchesIfHeader(resource.getHref(), activeLock.getToken(), etag)) continue;
                lList.add(resource.refreshLock(lockInfo, activeLock.getToken()));
            }
            if (lList.isEmpty()) {
                throw new DavException(412);
            }
            ActiveLock[] refreshedLocks = lList.toArray(new ActiveLock[lList.size()]);
            response.sendRefreshLockResponse(refreshedLocks);
        } else {
            int status = 200;
            if (!resource.exists()) {
                status = 201;
            }
            ActiveLock lock = resource.lock(lockInfo);
            CodedUrlHeader header = new CodedUrlHeader("Lock-Token", lock.getToken());
            response.setHeader(header.getHeaderName(), header.getHeaderValue());
            DavPropertySet propSet = new DavPropertySet();
            propSet.add(new LockDiscovery(lock));
            response.sendXmlResponse(propSet, status);
        }
    }

    protected void doUnlock(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException {
        String lockToken = request.getLockToken();
        TransactionInfo tInfo = request.getTransactionInfo();
        if (tInfo != null) {
            ((TransactionResource)resource).unlock(lockToken, tInfo);
        } else {
            resource.unlock(lockToken);
        }
        response.setStatus(204);
    }

    protected void doOrderPatch(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        if (!(resource instanceof OrderingResource)) {
            response.sendError(405);
            return;
        }
        OrderPatch op = request.getOrderPatch();
        if (op == null) {
            response.sendError(400);
            return;
        }
        ((OrderingResource)resource).orderMembers(op);
        response.setStatus(200);
    }

    protected void doSubscribe(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        if (!(resource instanceof ObservationResource)) {
            response.sendError(405);
            return;
        }
        SubscriptionInfo info = request.getSubscriptionInfo();
        if (info == null) {
            response.sendError(415);
            return;
        }
        Subscription subs = ((ObservationResource)resource).subscribe(info, request.getSubscriptionId());
        response.sendSubscriptionResponse(subs);
    }

    protected void doUnsubscribe(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        if (!(resource instanceof ObservationResource)) {
            response.sendError(405);
            return;
        }
        ((ObservationResource)resource).unsubscribe(request.getSubscriptionId());
        response.setStatus(204);
    }

    protected void doPoll(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        if (!(resource instanceof ObservationResource)) {
            response.sendError(405);
            return;
        }
        EventDiscovery ed = ((ObservationResource)resource).poll(request.getSubscriptionId(), request.getPollTimeout());
        response.sendPollResponse(ed);
    }

    protected void doVersionControl(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!(resource instanceof VersionableResource)) {
            response.sendError(405);
            return;
        }
        ((VersionableResource)resource).addVersionControl();
    }

    protected void doLabel(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        LabelInfo labelInfo = request.getLabelInfo();
        if (resource instanceof VersionResource) {
            ((VersionResource)resource).label(labelInfo);
        } else if (resource instanceof VersionControlledResource) {
            ((VersionControlledResource)resource).label(labelInfo);
        } else {
            response.sendError(405);
        }
    }

    protected void doReport(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        Report report;
        ReportInfo info = request.getReportInfo();
        if (resource instanceof DeltaVResource) {
            report = ((DeltaVResource)resource).getReport(info);
        } else if (resource instanceof AclResource) {
            report = ((AclResource)resource).getReport(info);
        } else {
            response.sendError(405);
            return;
        }
        int statusCode = report.isMultiStatusReport() ? 207 : 200;
        this.addHintAboutPotentialRequestEncodings(request, response);
        response.sendXmlResponse(report, statusCode, AbstractWebdavServlet.acceptsGzipEncoding(request) ? Collections.singletonList("gzip") : Collections.emptyList());
    }

    protected void doCheckin(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(405);
            return;
        }
        String versionHref = ((VersionControlledResource)resource).checkin();
        response.setHeader("Location", versionHref);
        response.setStatus(201);
    }

    protected void doCheckout(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(405);
            return;
        }
        ((VersionControlledResource)resource).checkout();
    }

    protected void doUncheckout(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(405);
            return;
        }
        ((VersionControlledResource)resource).uncheckout();
    }

    protected void doMerge(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(405);
            return;
        }
        MergeInfo info = request.getMergeInfo();
        MultiStatus ms = ((VersionControlledResource)resource).merge(info);
        response.sendMultiStatus(ms);
    }

    protected void doUpdate(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!(resource instanceof VersionControlledResource)) {
            response.sendError(405);
            return;
        }
        UpdateInfo info = request.getUpdateInfo();
        MultiStatus ms = ((VersionControlledResource)resource).update(info);
        response.sendMultiStatus(ms);
    }

    protected void doMkWorkspace(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (resource.exists()) {
            log.warn("Cannot create a new workspace. Resource already exists.");
            response.sendError(403);
            return;
        }
        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists() || !parentResource.isCollection()) {
            response.sendError(409);
            return;
        }
        if (!(parentResource instanceof DeltaVResource)) {
            response.sendError(405);
            return;
        }
        ((DeltaVResource)parentResource).addWorkspace(resource);
        response.setStatus(201);
    }

    protected void doMkActivity(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (resource.exists()) {
            log.warn("Unable to create activity: A resource already exists at the request-URL " + request.getRequestURL());
            response.sendError(403);
            return;
        }
        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists() || !parentResource.isCollection()) {
            response.sendError(409);
            return;
        }
        if (!parentResource.getComplianceClass().contains("activity")) {
            response.sendError(405);
            return;
        }
        if (!(resource instanceof ActivityResource)) {
            log.error("Unable to create activity: ActivityResource expected");
            response.sendError(500);
            return;
        }
        parentResource.addMember(resource, this.getInputContext(request, (InputStream)request.getInputStream()));
        response.setStatus(201);
    }

    protected void doBaselineControl(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!resource.exists()) {
            log.warn("Unable to add baseline control. Resource does not exist " + resource.getHref());
            response.sendError(404);
            return;
        }
        if (!(resource instanceof VersionControlledResource) || !resource.isCollection()) {
            log.warn("BaselineControl is not supported by resource " + resource.getHref());
            response.sendError(405);
            return;
        }
        throw new DavException(501);
    }

    protected void doSearch(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!(resource instanceof SearchResource)) {
            response.sendError(405);
            return;
        }
        Document doc = request.getRequestDocument();
        if (doc != null) {
            SearchInfo sR = SearchInfo.createFromXml(doc.getDocumentElement());
            response.sendMultiStatus(((SearchResource)((Object)resource)).search(sR));
        } else {
            response.sendMultiStatus(((SearchResource)((Object)resource)).search(null));
        }
    }

    protected void doAcl(WebdavRequest request, WebdavResponse response, DavResource resource) throws DavException, IOException {
        if (!(resource instanceof AclResource)) {
            response.sendError(405);
            return;
        }
        Document doc = request.getRequestDocument();
        if (doc == null) {
            throw new DavException(400, "ACL request requires a DAV:acl body.");
        }
        AclProperty acl = AclProperty.createFromXml(doc.getDocumentElement());
        ((AclResource)resource).alterAcl(acl);
    }

    protected InputContext getInputContext(DavServletRequest request, InputStream in) {
        return new InputContextImpl(request, in);
    }

    protected OutputContext getOutputContext(DavServletResponse response, OutputStream out) {
        return new OutputContextImpl(response, out);
    }

    public static List<String> getContentCodings(HttpServletRequest request) {
        return AbstractWebdavServlet.getListElementsFromHeaderField(request, "Content-Encoding");
    }

    private static boolean acceptsGzipEncoding(HttpServletRequest request) {
        List<String> result = AbstractWebdavServlet.getListElementsFromHeaderField(request, "Accept-Encoding");
        for (String s : result) {
            s = s.replace(" ", "");
            int semi = s.indexOf(59);
            if ("gzip".equals(s)) {
                return true;
            }
            if (semi <= 0) continue;
            String enc = s.substring(0, semi);
            String parm = s.substring(semi + 1);
            if (!"gzip".equals(enc) || !parm.startsWith("q=")) continue;
            float q = Float.valueOf(parm.substring(2)).floatValue();
            return q > 0.0f;
        }
        return false;
    }

    private static List<String> getListElementsFromHeaderField(HttpServletRequest request, String fieldName) {
        List<String> result = Collections.emptyList();
        Enumeration ceh = request.getHeaders(fieldName);
        while (ceh.hasMoreElements()) {
            for (String h : ((String)ceh.nextElement()).split(",")) {
                if (h.trim().isEmpty()) continue;
                if (result.isEmpty()) {
                    result = new ArrayList<String>();
                }
                result.add(h.trim().toLowerCase(Locale.ENGLISH));
            }
        }
        return result;
    }

    protected static String getSingletonField(HttpServletRequest request, String fieldName) {
        Enumeration lines = request.getHeaders(fieldName);
        if (!lines.hasMoreElements()) {
            return null;
        }
        String value = (String)lines.nextElement();
        if (!lines.hasMoreElements()) {
            return value;
        }
        ArrayList<String> v = new ArrayList<String>();
        v.add(value);
        while (lines.hasMoreElements()) {
            v.add((String)lines.nextElement());
        }
        throw new IllegalArgumentException("Multiple field lines for '" + fieldName + "' header field: " + v);
    }
}

