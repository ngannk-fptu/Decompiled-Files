/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletException
 *  javax.servlet.SessionTrackingMode
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.Adapter
 *  org.apache.coyote.Request
 *  org.apache.coyote.Response
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.CharChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.http.ServerCookie
 *  org.apache.tomcat.util.http.ServerCookies
 *  org.apache.tomcat.util.net.SocketEvent
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.CoyotePrincipal;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.SessionConfig;
import org.apache.catalina.util.URLEncoder;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.res.StringManager;

public class CoyoteAdapter
implements Adapter {
    private static final Log log = LogFactory.getLog(CoyoteAdapter.class);
    private static final String POWERED_BY = "Servlet/4.0 JSP/2.3 (" + ServerInfo.getServerInfo() + " Java/" + System.getProperty("java.vm.vendor") + "/" + System.getProperty("java.runtime.version") + ")";
    private static final EnumSet<SessionTrackingMode> SSL_ONLY = EnumSet.of(SessionTrackingMode.SSL);
    public static final int ADAPTER_NOTES = 1;
    protected static final boolean ALLOW_BACKSLASH = Boolean.parseBoolean(System.getProperty("org.apache.catalina.connector.CoyoteAdapter.ALLOW_BACKSLASH", "false"));
    private final Connector connector;
    protected static final StringManager sm = StringManager.getManager(CoyoteAdapter.class);

    public CoyoteAdapter(Connector connector) {
        this.connector = connector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public boolean asyncDispatch(org.apache.coyote.Request req, org.apache.coyote.Response res, SocketEvent status) throws Exception {
        request = (Request)req.getNote(1);
        response = (Response)res.getNote(1);
        if (request == null) {
            throw new IllegalStateException(CoyoteAdapter.sm.getString("coyoteAdapter.nullRequest"));
        }
        success = true;
        asyncConImpl = request.getAsyncContextInternal();
        req.setRequestThread();
        try {
            if (!request.isAsync()) {
                response.setSuspended(false);
            }
            if (status == SocketEvent.TIMEOUT) {
                if (!asyncConImpl.timeout()) {
                    asyncConImpl.setErrorState(null, false);
                }
            } else if (status == SocketEvent.ERROR) {
                success = false;
                t = (Throwable)req.getAttribute("javax.servlet.error.exception");
                context = request.getContext();
                oldCL = null;
                try {
                    oldCL = context.bind(false, null);
                    if (req.getReadListener() != null) {
                        req.getReadListener().onError(t);
                    }
                    if (res.getWriteListener() != null) {
                        res.getWriteListener().onError(t);
                    }
                    res.action(ActionCode.CLOSE_NOW, (Object)t);
                    asyncConImpl.setErrorState(t, true);
                }
                finally {
                    context.unbind(false, oldCL);
                }
            }
            if (!request.isAsyncDispatching() && request.isAsync()) {
                writeListener = res.getWriteListener();
                readListener = req.getReadListener();
                if (writeListener != null && status == SocketEvent.OPEN_WRITE) {
                    context = request.getContext();
                    oldCL = null;
                    try {
                        oldCL = context.bind(false, null);
                        res.onWritePossible();
                        if (request.isFinished() && req.sendAllDataReadEvent() && readListener != null) {
                            readListener.onAllDataRead();
                        }
                        if (!response.getCoyoteResponse().isExceptionPresent()) ** GOTO lbl76
                        throw response.getCoyoteResponse().getErrorException();
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                        writeListener.onError(t);
                        res.action(ActionCode.CLOSE_NOW, (Object)t);
                        asyncConImpl.setErrorState(t, true);
                    }
                    finally {
                        context.unbind(false, oldCL);
                    }
                } else if (readListener != null && status == SocketEvent.OPEN_READ) {
                    context = request.getContext();
                    oldCL = null;
                    try {
                        oldCL = context.bind(false, null);
                        if (!request.isFinished()) {
                            req.onDataAvailable();
                        }
                        if (request.isFinished() && req.sendAllDataReadEvent()) {
                            readListener.onAllDataRead();
                        }
                        if (request.getCoyoteRequest().isExceptionPresent()) {
                            throw request.getCoyoteRequest().getErrorException();
                        }
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                        readListener.onError(t);
                        res.action(ActionCode.CLOSE_NOW, (Object)t);
                        asyncConImpl.setErrorState(t, true);
                    }
                    finally {
                        context.unbind(false, oldCL);
                    }
                }
            }
            if (!request.isAsyncDispatching() && request.isAsync() && response.isErrorReportRequired()) {
                this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
            }
            if (request.isAsyncDispatching()) {
                this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
                if (response.isError()) {
                    t = (Throwable)request.getAttribute("javax.servlet.error.exception");
                    asyncConImpl.setErrorState(t, true);
                }
            }
            if (!request.isAsync()) {
                request.finishRequest();
                response.finishResponse();
            }
            error = new AtomicBoolean(false);
            res.action(ActionCode.IS_ERROR, (Object)error);
            if (error.get()) {
                if (request.isAsyncCompleting() || request.isAsyncDispatching()) {
                    res.action(ActionCode.ASYNC_POST_PROCESS, null);
                }
                success = false;
            }
        }
        catch (IOException e) {
            success = false;
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            success = false;
            CoyoteAdapter.log.error((Object)CoyoteAdapter.sm.getString("coyoteAdapter.asyncDispatch"), t);
        }
        finally {
            if (!success) {
                res.setStatus(500);
            }
            if (!success || !request.isAsync()) {
                time = 0L;
                if (req.getStartTime() != -1L) {
                    time = System.currentTimeMillis() - req.getStartTime();
                }
                if ((context = request.getContext()) != null) {
                    context.logAccess(request, response, time, false);
                } else {
                    this.log(req, res, time);
                }
            }
            req.getRequestProcessor().setWorkerThreadName(null);
            req.clearRequestThread();
            if (!success || !request.isAsync()) {
                this.updateWrapperErrorCount(request, response);
                request.recycle();
                response.recycle();
            }
        }
        return success;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void service(org.apache.coyote.Request req, org.apache.coyote.Response res) throws Exception {
        block24: {
            Request request = (Request)req.getNote(1);
            Response response = (Response)res.getNote(1);
            if (request == null) {
                request = this.connector.createRequest();
                request.setCoyoteRequest(req);
                response = this.connector.createResponse();
                response.setCoyoteResponse(res);
                request.setResponse(response);
                response.setRequest(request);
                req.setNote(1, (Object)request);
                res.setNote(1, (Object)response);
                req.getParameters().setQueryStringCharset(this.connector.getURICharset());
            }
            if (this.connector.getXpoweredBy()) {
                response.addHeader("X-Powered-By", POWERED_BY);
            }
            boolean async = false;
            boolean postParseSuccess = false;
            req.setRequestThread();
            try {
                postParseSuccess = this.postParseRequest(req, request, res, response);
                if (postParseSuccess) {
                    request.setAsyncSupported(this.connector.getService().getContainer().getPipeline().isAsyncSupported());
                    this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
                }
                if (request.isAsync()) {
                    async = true;
                    ReadListener readListener = req.getReadListener();
                    if (readListener != null && request.isFinished()) {
                        ClassLoader oldCL = null;
                        try {
                            oldCL = request.getContext().bind(false, null);
                            if (req.sendAllDataReadEvent()) {
                                req.getReadListener().onAllDataRead();
                            }
                        }
                        finally {
                            request.getContext().unbind(false, oldCL);
                        }
                    }
                    Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
                    if (!request.isAsyncCompleting() && throwable != null) {
                        request.getAsyncContextInternal().setErrorState(throwable, true);
                    }
                    break block24;
                }
                request.finishRequest();
                response.finishResponse();
            }
            catch (IOException error) {
            }
            finally {
                AtomicBoolean error = new AtomicBoolean(false);
                res.action(ActionCode.IS_ERROR, (Object)error);
                if (request.isAsyncCompleting() && error.get()) {
                    res.action(ActionCode.ASYNC_POST_PROCESS, null);
                    async = false;
                }
                if (!async && postParseSuccess) {
                    Context context = request.getContext();
                    Host host = request.getHost();
                    long time = System.currentTimeMillis() - req.getStartTime();
                    if (context != null) {
                        context.logAccess(request, response, time, false);
                    } else if (response.isError()) {
                        if (host != null) {
                            host.logAccess(request, response, time, false);
                        } else {
                            this.connector.getService().getContainer().logAccess(request, response, time, false);
                        }
                    }
                }
                req.getRequestProcessor().setWorkerThreadName(null);
                req.clearRequestThread();
                if (!async) {
                    this.updateWrapperErrorCount(request, response);
                    request.recycle();
                    response.recycle();
                }
            }
        }
    }

    private void updateWrapperErrorCount(Request request, Response response) {
        Wrapper wrapper;
        if (response.isError() && (wrapper = request.getWrapper()) != null) {
            wrapper.incrementErrorCount();
        }
    }

    public boolean prepare(org.apache.coyote.Request req, org.apache.coyote.Response res) throws IOException, ServletException {
        Request request = (Request)req.getNote(1);
        Response response = (Response)res.getNote(1);
        return this.postParseRequest(req, request, res, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void log(org.apache.coyote.Request req, org.apache.coyote.Response res, long time) {
        Request request = (Request)req.getNote(1);
        Response response = (Response)res.getNote(1);
        if (request == null) {
            request = this.connector.createRequest();
            request.setCoyoteRequest(req);
            response = this.connector.createResponse();
            response.setCoyoteResponse(res);
            request.setResponse(response);
            response.setRequest(request);
            req.setNote(1, (Object)request);
            res.setNote(1, (Object)response);
            req.getParameters().setQueryStringCharset(this.connector.getURICharset());
        }
        try {
            boolean logged = false;
            Context context = request.mappingData.context;
            Host host = request.mappingData.host;
            if (context != null) {
                logged = true;
                context.logAccess(request, response, time, true);
            } else if (host != null) {
                logged = true;
                host.logAccess(request, response, time, true);
            }
            if (!logged) {
                this.connector.getService().getContainer().logAccess(request, response, time, true);
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.warn((Object)sm.getString("coyoteAdapter.accesslogFail"), t);
        }
        finally {
            this.updateWrapperErrorCount(request, response);
            request.recycle();
            response.recycle();
        }
    }

    public void checkRecycled(org.apache.coyote.Request req, org.apache.coyote.Response res) {
        Request request = (Request)req.getNote(1);
        Response response = (Response)res.getNote(1);
        String messageKey = null;
        if (request != null && request.getHost() != null) {
            messageKey = "coyoteAdapter.checkRecycled.request";
        } else if (response != null && response.getContentWritten() != 0L) {
            messageKey = "coyoteAdapter.checkRecycled.response";
        }
        if (messageKey != null) {
            this.log(req, res, 0L);
            if (this.connector.getState().isAvailable()) {
                if (log.isInfoEnabled()) {
                    log.info((Object)sm.getString(messageKey), (Throwable)new RecycleRequiredException());
                }
            } else if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString(messageKey), (Throwable)new RecycleRequiredException());
            }
        }
    }

    public String getDomain() {
        return this.connector.getDomain();
    }

    protected boolean postParseRequest(org.apache.coyote.Request req, Request request, org.apache.coyote.Response res, Response response) throws IOException, ServletException {
        MessageBytes serverName;
        MessageBytes undecodedURI;
        if (req.scheme().isNull()) {
            req.scheme().setString(this.connector.getScheme());
            request.setSecure(this.connector.getSecure());
        } else {
            request.setSecure(req.scheme().equals("https"));
        }
        String proxyName = this.connector.getProxyName();
        int proxyPort = this.connector.getProxyPort();
        if (proxyPort != 0) {
            req.setServerPort(proxyPort);
        } else if (req.getServerPort() == -1) {
            if (req.scheme().equals("https")) {
                req.setServerPort(443);
            } else {
                req.setServerPort(80);
            }
        }
        if (proxyName != null) {
            req.serverName().setString(proxyName);
        }
        if ((undecodedURI = req.requestURI()).equals("*")) {
            if (req.method().equals("OPTIONS")) {
                StringBuilder allow = new StringBuilder();
                allow.append("GET, HEAD, POST, PUT, DELETE, OPTIONS");
                if (this.connector.getAllowTrace()) {
                    allow.append(", TRACE");
                }
                res.setHeader("Allow", allow.toString());
                this.connector.getService().getContainer().logAccess(request, response, 0L, true);
                return false;
            }
            response.sendError(400, sm.getString("coyoteAdapter.invalidURI"));
        }
        MessageBytes decodedURI = req.decodedURI();
        if (req.method().equals("CONNECT")) {
            response.sendError(501, sm.getString("coyoteAdapter.connect"));
        } else if (undecodedURI.getType() == 2) {
            decodedURI.duplicate(undecodedURI);
            this.parsePathParameters(req, request);
            try {
                req.getURLDecoder().convert(decodedURI.getByteChunk(), this.connector.getEncodedSolidusHandlingInternal());
            }
            catch (IOException ioe) {
                response.sendError(400, sm.getString("coyoteAdapter.invalidURIWithMessage", new Object[]{ioe.getMessage()}));
            }
            if (CoyoteAdapter.normalize(req.decodedURI())) {
                this.convertURI(decodedURI, request);
                if (!CoyoteAdapter.checkNormalize(req.decodedURI())) {
                    response.sendError(400, "Invalid URI");
                }
            } else {
                response.sendError(400, sm.getString("coyoteAdapter.invalidURI"));
            }
        } else {
            decodedURI.toChars();
            CharChunk uriCC = decodedURI.getCharChunk();
            int semicolon = uriCC.indexOf(';');
            if (semicolon > 0) {
                decodedURI.setChars(uriCC.getBuffer(), uriCC.getStart(), semicolon);
            }
        }
        if (this.connector.getUseIPVHosts()) {
            serverName = req.localName();
            if (serverName.isNull()) {
                res.action(ActionCode.REQ_LOCAL_NAME_ATTRIBUTE, null);
            }
        } else {
            serverName = req.serverName();
        }
        String version = null;
        Context versionContext = null;
        boolean mapRequired = true;
        if (response.isError()) {
            decodedURI.recycle();
        }
        while (mapRequired) {
            String sessionID;
            this.connector.getService().getMapper().map(serverName, decodedURI, version, request.getMappingData());
            if (request.getContext() == null) {
                return true;
            }
            if (request.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.URL) && (sessionID = request.getPathParameter(SessionConfig.getSessionUriParamName(request.getContext()))) != null) {
                request.setRequestedSessionId(sessionID);
                request.setRequestedSessionURL(true);
            }
            try {
                this.parseSessionCookiesId(request);
            }
            catch (IllegalArgumentException e) {
                if (!response.isError()) {
                    response.setError();
                    response.sendError(400, e.getMessage());
                }
                return true;
            }
            this.parseSessionSslId(request);
            sessionID = request.getRequestedSessionId();
            mapRequired = false;
            if (version == null || request.getContext() != versionContext) {
                version = null;
                versionContext = null;
                Context[] contexts = request.getMappingData().contexts;
                if (contexts != null && sessionID != null) {
                    for (int i = contexts.length; i > 0; --i) {
                        Context ctxt = contexts[i - 1];
                        if (ctxt.getManager().findSession(sessionID) == null) continue;
                        if (ctxt.equals(request.getMappingData().context)) break;
                        version = ctxt.getWebappVersion();
                        versionContext = ctxt;
                        request.getMappingData().recycle();
                        mapRequired = true;
                        request.recycleSessionInfo();
                        request.recycleCookieInfo(true);
                        break;
                    }
                }
            }
            if (mapRequired || !request.getContext().getPaused()) continue;
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException contexts) {
                // empty catch block
            }
            request.getMappingData().recycle();
            mapRequired = true;
        }
        MessageBytes redirectPathMB = request.getMappingData().redirectPath;
        if (!redirectPathMB.isNull()) {
            String redirectPath = URLEncoder.DEFAULT.encode(redirectPathMB.toString(), StandardCharsets.UTF_8);
            String query = request.getQueryString();
            if (request.isRequestedSessionIdFromURL()) {
                redirectPath = redirectPath + ";" + SessionConfig.getSessionUriParamName(request.getContext()) + "=" + request.getRequestedSessionId();
            }
            if (query != null) {
                redirectPath = redirectPath + "?" + query;
            }
            response.sendRedirect(redirectPath);
            request.getContext().logAccess(request, response, 0L, true);
            return false;
        }
        if (!this.connector.getAllowTrace() && req.method().equals("TRACE")) {
            String[] methods;
            Wrapper wrapper = request.getWrapper();
            String header = null;
            if (wrapper != null && (methods = wrapper.getServletMethods()) != null) {
                for (String method : methods) {
                    if ("TRACE".equals(method)) continue;
                    header = header == null ? method : header + ", " + method;
                }
            }
            if (header != null) {
                res.addHeader("Allow", header);
            }
            response.sendError(405, sm.getString("coyoteAdapter.trace"));
            return true;
        }
        this.doConnectorAuthenticationAuthorization(req, request);
        return true;
    }

    private void doConnectorAuthenticationAuthorization(org.apache.coyote.Request req, Request request) {
        String authType;
        String username = req.getRemoteUser().toString();
        if (username != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("coyoteAdapter.authenticate", new Object[]{username}));
            }
            if (req.getRemoteUserNeedsAuthorization()) {
                Authenticator authenticator = request.getContext().getAuthenticator();
                if (!(authenticator instanceof AuthenticatorBase)) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("coyoteAdapter.authorize", new Object[]{username}));
                    }
                    request.setUserPrincipal(request.getContext().getRealm().authenticate(username));
                }
            } else {
                request.setUserPrincipal(new CoyotePrincipal(username));
            }
        }
        if ((authType = req.getAuthType().toString()) != null) {
            request.setAuthType(authType);
        }
    }

    protected void parsePathParameters(org.apache.coyote.Request req, Request request) {
        req.decodedURI().toBytes();
        ByteChunk uriBC = req.decodedURI().getByteChunk();
        int semicolon = uriBC.indexOf(';', 1);
        if (semicolon == -1) {
            return;
        }
        Charset charset = this.connector.getURICharset();
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"uriBC", uriBC.toString()}));
            log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"semicolon", String.valueOf(semicolon)}));
            log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"enc", charset.name()}));
        }
        while (semicolon > -1) {
            int equals;
            int start = uriBC.getStart();
            int end = uriBC.getEnd();
            int pathParamStart = semicolon + 1;
            int pathParamEnd = ByteChunk.findBytes((byte[])uriBC.getBuffer(), (int)(start + pathParamStart), (int)end, (byte[])new byte[]{59, 47});
            String pv = null;
            if (pathParamEnd >= 0) {
                if (charset != null) {
                    pv = new String(uriBC.getBuffer(), start + pathParamStart, pathParamEnd - pathParamStart, charset);
                }
                byte[] buf = uriBC.getBuffer();
                for (int i = 0; i < end - start - pathParamEnd; ++i) {
                    buf[start + semicolon + i] = buf[start + i + pathParamEnd];
                }
                uriBC.setBytes(buf, start, end - start - pathParamEnd + semicolon);
            } else {
                if (charset != null) {
                    pv = new String(uriBC.getBuffer(), start + pathParamStart, end - start - pathParamStart, charset);
                }
                uriBC.setEnd(start + semicolon);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"pathParamStart", String.valueOf(pathParamStart)}));
                log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"pathParamEnd", String.valueOf(pathParamEnd)}));
                log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"pv", pv}));
            }
            if (pv != null && (equals = pv.indexOf(61)) > -1) {
                String name = pv.substring(0, equals);
                String value = pv.substring(equals + 1);
                request.addPathParameter(name, value);
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"equals", String.valueOf(equals)}));
                    log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"name", name}));
                    log.debug((Object)sm.getString("coyoteAdapter.debug", new Object[]{"value", value}));
                }
            }
            semicolon = uriBC.indexOf(';', semicolon);
        }
    }

    protected void parseSessionSslId(Request request) {
        String sessionId;
        if (request.getRequestedSessionId() == null && SSL_ONLY.equals(request.getServletContext().getEffectiveSessionTrackingModes()) && request.connector.secure && (sessionId = (String)request.getAttribute("javax.servlet.request.ssl_session_id")) != null) {
            request.setRequestedSessionId(sessionId);
            request.setRequestedSessionSSL(true);
        }
    }

    protected void parseSessionCookiesId(Request request) {
        Context context = request.getMappingData().context;
        if (context != null && !context.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE)) {
            return;
        }
        ServerCookies serverCookies = request.getServerCookies();
        int count = serverCookies.getCookieCount();
        if (count <= 0) {
            return;
        }
        String sessionCookieName = SessionConfig.getSessionCookieName(context);
        for (int i = 0; i < count; ++i) {
            ServerCookie scookie = serverCookies.getCookie(i);
            if (!scookie.getName().equals(sessionCookieName)) continue;
            if (!request.isRequestedSessionIdFromCookie()) {
                this.convertMB(scookie.getValue());
                request.setRequestedSessionId(scookie.getValue().toString());
                request.setRequestedSessionCookie(true);
                request.setRequestedSessionURL(false);
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)(" Requested cookie session id is " + request.getRequestedSessionId()));
                continue;
            }
            if (request.isRequestedSessionIdValid()) continue;
            this.convertMB(scookie.getValue());
            request.setRequestedSessionId(scookie.getValue().toString());
        }
    }

    protected void convertURI(MessageBytes uri, Request request) throws IOException {
        ByteChunk bc = uri.getByteChunk();
        int length = bc.getLength();
        CharChunk cc = uri.getCharChunk();
        cc.allocate(length, -1);
        Charset charset = this.connector.getURICharset();
        B2CConverter conv = request.getURIConverter();
        if (conv == null) {
            conv = new B2CConverter(charset, true);
            request.setURIConverter(conv);
        } else {
            conv.recycle();
        }
        try {
            conv.convert(bc, cc, true);
            uri.setChars(cc.getBuffer(), cc.getStart(), cc.getLength());
        }
        catch (IOException ioe) {
            request.getResponse().sendError(400);
        }
    }

    protected void convertMB(MessageBytes mb) {
        if (mb.getType() != 2) {
            return;
        }
        ByteChunk bc = mb.getByteChunk();
        CharChunk cc = mb.getCharChunk();
        int length = bc.getLength();
        cc.allocate(length, -1);
        byte[] bbuf = bc.getBuffer();
        char[] cbuf = cc.getBuffer();
        int start = bc.getStart();
        for (int i = 0; i < length; ++i) {
            cbuf[i] = (char)(bbuf[i + start] & 0xFF);
        }
        mb.setChars(cbuf, 0, length);
    }

    public static boolean normalize(MessageBytes uriMB) {
        int end;
        ByteChunk uriBC = uriMB.getByteChunk();
        byte[] b = uriBC.getBytes();
        int start = uriBC.getStart();
        if (start == (end = uriBC.getEnd())) {
            return false;
        }
        int pos = 0;
        int index = 0;
        if (b[start] != 47 && b[start] != 92) {
            return false;
        }
        for (pos = start; pos < end; ++pos) {
            if (b[pos] == 92) {
                if (ALLOW_BACKSLASH) {
                    b[pos] = 47;
                    continue;
                }
                return false;
            }
            if (b[pos] != 0) continue;
            return false;
        }
        for (pos = start; pos < end - 1; ++pos) {
            if (b[pos] != 47) continue;
            while (pos + 1 < end && b[pos + 1] == 47) {
                CoyoteAdapter.copyBytes(b, pos, pos + 1, end - pos - 1);
                --end;
            }
        }
        if (end - start >= 2 && b[end - 1] == 46 && (b[end - 2] == 47 || b[end - 2] == 46 && b[end - 3] == 47)) {
            b[end] = 47;
            ++end;
        }
        uriBC.setEnd(end);
        index = 0;
        while ((index = uriBC.indexOf("/./", 0, 3, index)) >= 0) {
            CoyoteAdapter.copyBytes(b, start + index, start + index + 2, end - start - index - 2);
            uriBC.setEnd(end -= 2);
        }
        index = 0;
        while ((index = uriBC.indexOf("/../", 0, 4, index)) >= 0) {
            if (index == 0) {
                return false;
            }
            int index2 = -1;
            for (pos = start + index - 1; pos >= 0 && index2 < 0; --pos) {
                if (b[pos] != 47) continue;
                index2 = pos;
            }
            CoyoteAdapter.copyBytes(b, start + index2, start + index + 3, end - start - index - 3);
            end = end + index2 - index - 3;
            uriBC.setEnd(end);
            index = index2;
        }
        return true;
    }

    @Deprecated
    public static boolean checkNormalize(MessageBytes uriMB) {
        CharChunk uriCC = uriMB.getCharChunk();
        char[] c = uriCC.getChars();
        int start = uriCC.getStart();
        int end = uriCC.getEnd();
        int pos = 0;
        for (pos = start; pos < end; ++pos) {
            if (c[pos] == '\\') {
                return false;
            }
            if (c[pos] != '\u0000') continue;
            return false;
        }
        for (pos = start; pos < end - 1; ++pos) {
            if (c[pos] != '/' || c[pos + 1] != '/') continue;
            return false;
        }
        if (end - start >= 2 && c[end - 1] == '.' && (c[end - 2] == '/' || c[end - 2] == '.' && c[end - 3] == '/')) {
            return false;
        }
        if (uriCC.indexOf("/./", 0, 3, 0) >= 0) {
            return false;
        }
        return uriCC.indexOf("/../", 0, 4, 0) < 0;
    }

    protected static void copyBytes(byte[] b, int dest, int src, int len) {
        System.arraycopy(b, src, b, dest, len);
    }

    private static class RecycleRequiredException
    extends Exception {
        private static final long serialVersionUID = 1L;

        private RecycleRequiredException() {
        }
    }
}

