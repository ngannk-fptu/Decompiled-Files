/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.ClientEndpointConfig
 *  javax.websocket.CloseReason
 *  javax.websocket.CloseReason$CloseCode
 *  javax.websocket.CloseReason$CloseCodes
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Extension
 *  javax.websocket.MessageHandler
 *  javax.websocket.MessageHandler$Partial
 *  javax.websocket.MessageHandler$Whole
 *  javax.websocket.PongMessage
 *  javax.websocket.RemoteEndpoint$Async
 *  javax.websocket.RemoteEndpoint$Basic
 *  javax.websocket.SendResult
 *  javax.websocket.Session
 *  javax.websocket.WebSocketContainer
 *  javax.websocket.server.ServerEndpointConfig
 *  javax.websocket.server.ServerEndpointConfig$Builder
 *  javax.websocket.server.ServerEndpointConfig$Configurator
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.naming.NamingException;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.SendResult;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.ClientEndpointHolder;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.FutureToSendHandler;
import org.apache.tomcat.websocket.MessageHandlerResult;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.WrappedMessageHandler;
import org.apache.tomcat.websocket.WsFrameBase;
import org.apache.tomcat.websocket.WsRemoteEndpointAsync;
import org.apache.tomcat.websocket.WsRemoteEndpointBasic;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.apache.tomcat.websocket.pojo.PojoEndpointServer;
import org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator;

public class WsSession
implements Session {
    private final Log log;
    private static final StringManager sm = StringManager.getManager(WsSession.class);
    private static final byte[] ELLIPSIS_BYTES = "\u2026".getBytes(StandardCharsets.UTF_8);
    private static final int ELLIPSIS_BYTES_LEN = ELLIPSIS_BYTES.length;
    private static final boolean SEC_CONFIGURATOR_USES_IMPL_DEFAULT;
    private static AtomicLong ids;
    private final Endpoint localEndpoint;
    private final WsRemoteEndpointImplBase wsRemoteEndpoint;
    private final RemoteEndpoint.Async remoteEndpointAsync;
    private final RemoteEndpoint.Basic remoteEndpointBasic;
    private final ClassLoader applicationClassLoader;
    private final WsWebSocketContainer webSocketContainer;
    private final URI requestUri;
    private final Map<String, List<String>> requestParameterMap;
    private final String queryString;
    private final Principal userPrincipal;
    private final EndpointConfig endpointConfig;
    private final List<Extension> negotiatedExtensions;
    private final String subProtocol;
    private final Map<String, String> pathParameters;
    private final boolean secure;
    private final String httpSessionId;
    private final String id;
    private volatile MessageHandler textMessageHandler;
    private volatile MessageHandler binaryMessageHandler;
    private volatile MessageHandler.Whole<PongMessage> pongMessageHandler;
    private AtomicReference<State> state;
    private final Map<String, Object> userProperties;
    private volatile int maxBinaryMessageBufferSize;
    private volatile int maxTextMessageBufferSize;
    private volatile long maxIdleTimeout;
    private volatile long lastActiveRead;
    private volatile long lastActiveWrite;
    private Map<FutureToSendHandler, FutureToSendHandler> futures;
    private WsFrameBase wsFrame;

    public WsSession(ClientEndpointHolder clientEndpointHolder, WsRemoteEndpointImplBase wsRemoteEndpoint, WsWebSocketContainer wsWebSocketContainer, List<Extension> negotiatedExtensions, String subProtocol, Map<String, String> pathParameters, boolean secure, ClientEndpointConfig clientEndpointConfig) throws DeploymentException {
        this.log = LogFactory.getLog(WsSession.class);
        this.textMessageHandler = null;
        this.binaryMessageHandler = null;
        this.pongMessageHandler = null;
        this.state = new AtomicReference<State>(State.OPEN);
        this.userProperties = new ConcurrentHashMap<String, Object>();
        this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxIdleTimeout = 0L;
        this.lastActiveRead = System.currentTimeMillis();
        this.lastActiveWrite = System.currentTimeMillis();
        this.futures = new ConcurrentHashMap<FutureToSendHandler, FutureToSendHandler>();
        this.wsRemoteEndpoint = wsRemoteEndpoint;
        this.wsRemoteEndpoint.setSession(this);
        this.remoteEndpointAsync = new WsRemoteEndpointAsync(wsRemoteEndpoint);
        this.remoteEndpointBasic = new WsRemoteEndpointBasic(wsRemoteEndpoint);
        this.webSocketContainer = wsWebSocketContainer;
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
        wsRemoteEndpoint.setSendTimeout(wsWebSocketContainer.getDefaultAsyncSendTimeout());
        this.maxBinaryMessageBufferSize = this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
        this.maxTextMessageBufferSize = this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
        this.maxIdleTimeout = this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
        this.requestUri = null;
        this.requestParameterMap = Collections.emptyMap();
        this.queryString = null;
        this.userPrincipal = null;
        this.httpSessionId = null;
        this.negotiatedExtensions = negotiatedExtensions;
        this.subProtocol = subProtocol == null ? "" : subProtocol;
        this.pathParameters = pathParameters;
        this.secure = secure;
        this.wsRemoteEndpoint.setEncoders((EndpointConfig)clientEndpointConfig);
        this.endpointConfig = clientEndpointConfig;
        this.userProperties.putAll(this.endpointConfig.getUserProperties());
        this.id = Long.toHexString(ids.getAndIncrement());
        this.localEndpoint = clientEndpointHolder.getInstance(this.getInstanceManager());
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("wsSession.created", new Object[]{this.id}));
        }
    }

    public WsSession(WsRemoteEndpointImplBase wsRemoteEndpoint, WsWebSocketContainer wsWebSocketContainer, URI requestUri, Map<String, List<String>> requestParameterMap, String queryString, Principal userPrincipal, String httpSessionId, List<Extension> negotiatedExtensions, String subProtocol, Map<String, String> pathParameters, boolean secure, ServerEndpointConfig serverEndpointConfig) throws DeploymentException {
        Object endpointInstance;
        block6: {
            this.log = LogFactory.getLog(WsSession.class);
            this.textMessageHandler = null;
            this.binaryMessageHandler = null;
            this.pongMessageHandler = null;
            this.state = new AtomicReference<State>(State.OPEN);
            this.userProperties = new ConcurrentHashMap<String, Object>();
            this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
            this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
            this.maxIdleTimeout = 0L;
            this.lastActiveRead = System.currentTimeMillis();
            this.lastActiveWrite = System.currentTimeMillis();
            this.futures = new ConcurrentHashMap<FutureToSendHandler, FutureToSendHandler>();
            this.wsRemoteEndpoint = wsRemoteEndpoint;
            this.wsRemoteEndpoint.setSession(this);
            this.remoteEndpointAsync = new WsRemoteEndpointAsync(wsRemoteEndpoint);
            this.remoteEndpointBasic = new WsRemoteEndpointBasic(wsRemoteEndpoint);
            this.webSocketContainer = wsWebSocketContainer;
            this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
            wsRemoteEndpoint.setSendTimeout(wsWebSocketContainer.getDefaultAsyncSendTimeout());
            this.maxBinaryMessageBufferSize = this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
            this.maxTextMessageBufferSize = this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
            this.maxIdleTimeout = this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
            this.requestUri = requestUri;
            this.requestParameterMap = requestParameterMap == null ? Collections.emptyMap() : requestParameterMap;
            this.queryString = queryString;
            this.userPrincipal = userPrincipal;
            this.httpSessionId = httpSessionId;
            this.negotiatedExtensions = negotiatedExtensions;
            this.subProtocol = subProtocol == null ? "" : subProtocol;
            this.pathParameters = pathParameters;
            this.secure = secure;
            this.wsRemoteEndpoint.setEncoders((EndpointConfig)serverEndpointConfig);
            this.endpointConfig = serverEndpointConfig;
            this.userProperties.putAll(this.endpointConfig.getUserProperties());
            this.id = Long.toHexString(ids.getAndIncrement());
            InstanceManager instanceManager = this.getInstanceManager();
            ServerEndpointConfig.Configurator configurator = serverEndpointConfig.getConfigurator();
            Class clazz = serverEndpointConfig.getEndpointClass();
            try {
                if (instanceManager == null || !this.isDefaultConfigurator(configurator)) {
                    endpointInstance = configurator.getEndpointInstance(clazz);
                    if (instanceManager == null) break block6;
                    try {
                        instanceManager.newInstance(endpointInstance);
                        break block6;
                    }
                    catch (ReflectiveOperationException | NamingException e) {
                        throw new DeploymentException(sm.getString("wsSession.instanceNew"), (Throwable)e);
                    }
                }
                endpointInstance = instanceManager.newInstance(clazz);
            }
            catch (ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(sm.getString("wsSession.instanceCreateFailed"), (Throwable)e);
            }
        }
        this.localEndpoint = endpointInstance instanceof Endpoint ? (Endpoint)endpointInstance : new PojoEndpointServer(pathParameters, endpointInstance);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("wsSession.created", new Object[]{this.id}));
        }
    }

    private boolean isDefaultConfigurator(ServerEndpointConfig.Configurator configurator) {
        if (configurator.getClass().equals(DefaultServerEndpointConfigurator.class)) {
            return true;
        }
        return SEC_CONFIGURATOR_USES_IMPL_DEFAULT && configurator.getClass().equals(ServerEndpointConfig.Configurator.class);
    }

    @Deprecated
    public WsSession(Endpoint localEndpoint, WsRemoteEndpointImplBase wsRemoteEndpoint, WsWebSocketContainer wsWebSocketContainer, URI requestUri, Map<String, List<String>> requestParameterMap, String queryString, Principal userPrincipal, String httpSessionId, List<Extension> negotiatedExtensions, String subProtocol, Map<String, String> pathParameters, boolean secure, EndpointConfig endpointConfig) throws DeploymentException {
        this.log = LogFactory.getLog(WsSession.class);
        this.textMessageHandler = null;
        this.binaryMessageHandler = null;
        this.pongMessageHandler = null;
        this.state = new AtomicReference<State>(State.OPEN);
        this.userProperties = new ConcurrentHashMap<String, Object>();
        this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxIdleTimeout = 0L;
        this.lastActiveRead = System.currentTimeMillis();
        this.lastActiveWrite = System.currentTimeMillis();
        this.futures = new ConcurrentHashMap<FutureToSendHandler, FutureToSendHandler>();
        this.localEndpoint = localEndpoint;
        this.wsRemoteEndpoint = wsRemoteEndpoint;
        this.wsRemoteEndpoint.setSession(this);
        this.remoteEndpointAsync = new WsRemoteEndpointAsync(wsRemoteEndpoint);
        this.remoteEndpointBasic = new WsRemoteEndpointBasic(wsRemoteEndpoint);
        this.webSocketContainer = wsWebSocketContainer;
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
        wsRemoteEndpoint.setSendTimeout(wsWebSocketContainer.getDefaultAsyncSendTimeout());
        this.maxBinaryMessageBufferSize = this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
        this.maxTextMessageBufferSize = this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
        this.maxIdleTimeout = this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
        this.requestUri = requestUri;
        this.requestParameterMap = requestParameterMap == null ? Collections.emptyMap() : requestParameterMap;
        this.queryString = queryString;
        this.userPrincipal = userPrincipal;
        this.httpSessionId = httpSessionId;
        this.negotiatedExtensions = negotiatedExtensions;
        this.subProtocol = subProtocol == null ? "" : subProtocol;
        this.pathParameters = pathParameters;
        this.secure = secure;
        this.wsRemoteEndpoint.setEncoders(endpointConfig);
        this.endpointConfig = endpointConfig;
        this.userProperties.putAll(endpointConfig.getUserProperties());
        this.id = Long.toHexString(ids.getAndIncrement());
        InstanceManager instanceManager = this.getInstanceManager();
        if (instanceManager != null) {
            try {
                instanceManager.newInstance((Object)localEndpoint);
            }
            catch (Exception e) {
                throw new DeploymentException(sm.getString("wsSession.instanceNew"), (Throwable)e);
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("wsSession.created", new Object[]{this.id}));
        }
    }

    public InstanceManager getInstanceManager() {
        return this.webSocketContainer.getInstanceManager(this.applicationClassLoader);
    }

    public WebSocketContainer getContainer() {
        this.checkState();
        return this.webSocketContainer;
    }

    public void addMessageHandler(MessageHandler listener) {
        Class<?> target = Util.getMessageType(listener);
        this.doAddMessageHandler(target, listener);
    }

    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) throws IllegalStateException {
        this.doAddMessageHandler(clazz, (MessageHandler)handler);
    }

    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) throws IllegalStateException {
        this.doAddMessageHandler(clazz, (MessageHandler)handler);
    }

    private void doAddMessageHandler(Class<?> target, MessageHandler listener) {
        this.checkState();
        Set<MessageHandlerResult> mhResults = Util.getMessageHandlers(target, listener, this.endpointConfig, this);
        block5: for (MessageHandlerResult mhResult : mhResults) {
            switch (mhResult.getType()) {
                case TEXT: {
                    if (this.textMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerText"));
                    }
                    this.textMessageHandler = mhResult.getHandler();
                    continue block5;
                }
                case BINARY: {
                    if (this.binaryMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerBinary"));
                    }
                    this.binaryMessageHandler = mhResult.getHandler();
                    continue block5;
                }
                case PONG: {
                    if (this.pongMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerPong"));
                    }
                    MessageHandler handler = mhResult.getHandler();
                    if (handler instanceof MessageHandler.Whole) {
                        this.pongMessageHandler = (MessageHandler.Whole)handler;
                        continue block5;
                    }
                    throw new IllegalStateException(sm.getString("wsSession.invalidHandlerTypePong"));
                }
            }
            throw new IllegalArgumentException(sm.getString("wsSession.unknownHandlerType", new Object[]{listener, mhResult.getType()}));
        }
    }

    public Set<MessageHandler> getMessageHandlers() {
        this.checkState();
        HashSet<MessageHandler> result = new HashSet<MessageHandler>();
        if (this.binaryMessageHandler != null) {
            result.add(this.binaryMessageHandler);
        }
        if (this.textMessageHandler != null) {
            result.add(this.textMessageHandler);
        }
        if (this.pongMessageHandler != null) {
            result.add((MessageHandler)this.pongMessageHandler);
        }
        return result;
    }

    public void removeMessageHandler(MessageHandler listener) {
        this.checkState();
        if (listener == null) {
            return;
        }
        MessageHandler wrapped = null;
        if (listener instanceof WrappedMessageHandler) {
            wrapped = ((WrappedMessageHandler)listener).getWrappedHandler();
        }
        if (wrapped == null) {
            wrapped = listener;
        }
        boolean removed = false;
        if (wrapped.equals(this.textMessageHandler) || listener.equals(this.textMessageHandler)) {
            this.textMessageHandler = null;
            removed = true;
        }
        if (wrapped.equals(this.binaryMessageHandler) || listener.equals(this.binaryMessageHandler)) {
            this.binaryMessageHandler = null;
            removed = true;
        }
        if (wrapped.equals(this.pongMessageHandler) || listener.equals(this.pongMessageHandler)) {
            this.pongMessageHandler = null;
            removed = true;
        }
        if (!removed) {
            throw new IllegalStateException(sm.getString("wsSession.removeHandlerFailed", new Object[]{listener}));
        }
    }

    public String getProtocolVersion() {
        this.checkState();
        return "13";
    }

    public String getNegotiatedSubprotocol() {
        this.checkState();
        return this.subProtocol;
    }

    public List<Extension> getNegotiatedExtensions() {
        this.checkState();
        return this.negotiatedExtensions;
    }

    public boolean isSecure() {
        this.checkState();
        return this.secure;
    }

    public boolean isOpen() {
        return this.state.get() == State.OPEN || this.state.get() == State.OUTPUT_CLOSING || this.state.get() == State.CLOSING;
    }

    public boolean isClosed() {
        return this.state.get() == State.CLOSED;
    }

    public long getMaxIdleTimeout() {
        this.checkState();
        return this.maxIdleTimeout;
    }

    public void setMaxIdleTimeout(long timeout) {
        this.checkState();
        this.maxIdleTimeout = timeout;
    }

    public void setMaxBinaryMessageBufferSize(int max) {
        this.checkState();
        this.maxBinaryMessageBufferSize = max;
    }

    public int getMaxBinaryMessageBufferSize() {
        this.checkState();
        return this.maxBinaryMessageBufferSize;
    }

    public void setMaxTextMessageBufferSize(int max) {
        this.checkState();
        this.maxTextMessageBufferSize = max;
    }

    public int getMaxTextMessageBufferSize() {
        this.checkState();
        return this.maxTextMessageBufferSize;
    }

    public Set<Session> getOpenSessions() {
        this.checkState();
        return this.webSocketContainer.getOpenSessions(this.getSessionMapKey());
    }

    public RemoteEndpoint.Async getAsyncRemote() {
        this.checkState();
        return this.remoteEndpointAsync;
    }

    public RemoteEndpoint.Basic getBasicRemote() {
        this.checkState();
        return this.remoteEndpointBasic;
    }

    public void close() throws IOException {
        this.close(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.NORMAL_CLOSURE, ""));
    }

    public void close(CloseReason closeReason) throws IOException {
        this.doClose(closeReason, closeReason);
    }

    public void doClose(CloseReason closeReasonMessage, CloseReason closeReasonLocal) {
        this.doClose(closeReasonMessage, closeReasonLocal, false);
    }

    public void doClose(CloseReason closeReasonMessage, CloseReason closeReasonLocal, boolean closeSocket) {
        if (!this.state.compareAndSet(State.OPEN, State.OUTPUT_CLOSING)) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("wsSession.doClose", new Object[]{this.id}));
        }
        try {
            this.wsRemoteEndpoint.setBatchingAllowed(false);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log.warn((Object)sm.getString("wsSession.flushFailOnClose"), t);
            this.fireEndpointOnError(t);
        }
        this.sendCloseMessage(closeReasonMessage);
        this.fireEndpointOnClose(closeReasonLocal);
        if (!this.state.compareAndSet(State.OUTPUT_CLOSING, State.OUTPUT_CLOSED) || closeSocket) {
            this.state.set(State.CLOSED);
            this.wsRemoteEndpoint.close();
        }
        IOException ioe = new IOException(sm.getString("wsSession.messageFailed"));
        SendResult sr = new SendResult((Throwable)ioe);
        for (FutureToSendHandler f2sh : this.futures.keySet()) {
            f2sh.onResult(sr);
        }
    }

    public void onClose(CloseReason closeReason) {
        if (this.state.compareAndSet(State.OPEN, State.CLOSING)) {
            try {
                this.wsRemoteEndpoint.setBatchingAllowed(false);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.log.warn((Object)sm.getString("wsSession.flushFailOnClose"), t);
                this.fireEndpointOnError(t);
            }
            this.sendCloseMessage(closeReason);
            this.fireEndpointOnClose(closeReason);
            this.state.set(State.CLOSED);
            this.wsRemoteEndpoint.close();
        } else if (!this.state.compareAndSet(State.OUTPUT_CLOSING, State.CLOSING) && this.state.compareAndSet(State.OUTPUT_CLOSED, State.CLOSED)) {
            this.wsRemoteEndpoint.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireEndpointOnClose(CloseReason closeReason) {
        Throwable throwable = null;
        InstanceManager instanceManager = this.getInstanceManager();
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.localEndpoint.onClose((Session)this, closeReason);
        }
        catch (Throwable t1) {
            ExceptionUtils.handleThrowable((Throwable)t1);
            throwable = t1;
        }
        finally {
            block14: {
                if (instanceManager != null) {
                    try {
                        instanceManager.destroyInstance((Object)this.localEndpoint);
                    }
                    catch (Throwable t2) {
                        ExceptionUtils.handleThrowable((Throwable)t2);
                        if (throwable != null) break block14;
                        throwable = t2;
                    }
                }
            }
            t.setContextClassLoader(cl);
        }
        if (throwable != null) {
            this.fireEndpointOnError(throwable);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireEndpointOnError(Throwable throwable) {
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.localEndpoint.onError((Session)this, throwable);
        }
        finally {
            t.setContextClassLoader(cl);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendCloseMessage(CloseReason closeReason) {
        ByteBuffer msg = ByteBuffer.allocate(125);
        CloseReason.CloseCode closeCode = closeReason.getCloseCode();
        if (closeCode == CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
            msg.putShort((short)CloseReason.CloseCodes.PROTOCOL_ERROR.getCode());
        } else {
            msg.putShort((short)closeCode.getCode());
        }
        String reason = closeReason.getReasonPhrase();
        if (reason != null && reason.length() > 0) {
            WsSession.appendCloseReasonWithTruncation(msg, reason);
        }
        msg.flip();
        try {
            this.wsRemoteEndpoint.sendMessageBlock((byte)8, msg, true);
        }
        catch (IOException | IllegalStateException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("wsSession.sendCloseFail", new Object[]{this.id}), (Throwable)e);
            }
            this.wsRemoteEndpoint.close();
            if (closeCode != CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
                this.localEndpoint.onError((Session)this, (Throwable)e);
            }
        }
        finally {
            this.webSocketContainer.unregisterSession(this.getSessionMapKey(), this);
        }
    }

    private Object getSessionMapKey() {
        if (this.endpointConfig instanceof ServerEndpointConfig) {
            return ((ServerEndpointConfig)this.endpointConfig).getPath();
        }
        return this.localEndpoint;
    }

    protected static void appendCloseReasonWithTruncation(ByteBuffer msg, String reason) {
        byte[] reasonBytes = reason.getBytes(StandardCharsets.UTF_8);
        if (reasonBytes.length <= 123) {
            msg.put(reasonBytes);
        } else {
            int pos = 0;
            byte[] bytesNext = reason.substring(pos, pos + 1).getBytes(StandardCharsets.UTF_8);
            for (int remaining = 123 - ELLIPSIS_BYTES_LEN; remaining >= bytesNext.length; remaining -= bytesNext.length) {
                msg.put(bytesNext);
                bytesNext = reason.substring(++pos, pos + 1).getBytes(StandardCharsets.UTF_8);
            }
            msg.put(ELLIPSIS_BYTES);
        }
    }

    protected void registerFuture(FutureToSendHandler f2sh) {
        this.futures.put(f2sh, f2sh);
        if (this.isOpen()) {
            return;
        }
        if (f2sh.isDone()) {
            return;
        }
        IOException ioe = new IOException(sm.getString("wsSession.messageFailed"));
        SendResult sr = new SendResult((Throwable)ioe);
        f2sh.onResult(sr);
    }

    protected void unregisterFuture(FutureToSendHandler f2sh) {
        this.futures.remove(f2sh);
    }

    public URI getRequestURI() {
        this.checkState();
        return this.requestUri;
    }

    public Map<String, List<String>> getRequestParameterMap() {
        this.checkState();
        return this.requestParameterMap;
    }

    public String getQueryString() {
        this.checkState();
        return this.queryString;
    }

    public Principal getUserPrincipal() {
        this.checkState();
        return this.userPrincipal;
    }

    public Map<String, String> getPathParameters() {
        this.checkState();
        return this.pathParameters;
    }

    public String getId() {
        return this.id;
    }

    public Map<String, Object> getUserProperties() {
        this.checkState();
        return this.userProperties;
    }

    public Endpoint getLocal() {
        return this.localEndpoint;
    }

    public String getHttpSessionId() {
        return this.httpSessionId;
    }

    protected MessageHandler getTextMessageHandler() {
        return this.textMessageHandler;
    }

    protected MessageHandler getBinaryMessageHandler() {
        return this.binaryMessageHandler;
    }

    protected MessageHandler.Whole<PongMessage> getPongMessageHandler() {
        return this.pongMessageHandler;
    }

    protected void updateLastActiveRead() {
        this.lastActiveRead = System.currentTimeMillis();
    }

    protected void updateLastActiveWrite() {
        this.lastActiveWrite = System.currentTimeMillis();
    }

    protected void checkExpiration() {
        long timeout = this.maxIdleTimeout;
        long timeoutRead = this.getMaxIdleTimeoutRead();
        long timeoutWrite = this.getMaxIdleTimeoutWrite();
        long currentTime = System.currentTimeMillis();
        String key = null;
        if (timeoutRead > 0L && currentTime - this.lastActiveRead > timeoutRead) {
            key = "wsSession.timeoutRead";
        } else if (timeoutWrite > 0L && currentTime - this.lastActiveWrite > timeoutWrite) {
            key = "wsSession.timeoutWrite";
        } else if (timeout > 0L && currentTime - this.lastActiveRead > timeout && currentTime - this.lastActiveWrite > timeout) {
            key = "wsSession.timeout";
        }
        if (key != null) {
            String msg = sm.getString(key, new Object[]{this.getId()});
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)msg);
            }
            this.doClose(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg));
        }
    }

    private long getMaxIdleTimeoutRead() {
        Object timeout = this.userProperties.get("org.apache.tomcat.websocket.READ_IDLE_TIMEOUT_MS");
        if (timeout instanceof Long) {
            return (Long)timeout;
        }
        return 0L;
    }

    private long getMaxIdleTimeoutWrite() {
        Object timeout = this.userProperties.get("org.apache.tomcat.websocket.WRITE_IDLE_TIMEOUT_MS");
        if (timeout instanceof Long) {
            return (Long)timeout;
        }
        return 0L;
    }

    private void checkState() {
        if (this.isClosed()) {
            throw new IllegalStateException(sm.getString("wsSession.closed", new Object[]{this.id}));
        }
    }

    void setWsFrame(WsFrameBase wsFrame) {
        this.wsFrame = wsFrame;
    }

    public void suspend() {
        this.wsFrame.suspend();
    }

    public void resume() {
        this.wsFrame.resume();
    }

    static {
        ids = new AtomicLong(0L);
        ServerEndpointConfig.Builder builder = ServerEndpointConfig.Builder.create(Object.class, (String)"/");
        ServerEndpointConfig sec = builder.build();
        SEC_CONFIGURATOR_USES_IMPL_DEFAULT = sec.getConfigurator().getClass().equals(DefaultServerEndpointConfigurator.class);
    }

    private static enum State {
        OPEN,
        OUTPUT_CLOSING,
        OUTPUT_CLOSED,
        CLOSING,
        CLOSED;

    }
}

