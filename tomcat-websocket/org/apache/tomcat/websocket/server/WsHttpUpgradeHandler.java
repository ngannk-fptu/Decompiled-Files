/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.WebConnection
 *  javax.websocket.CloseReason
 *  javax.websocket.CloseReason$CloseCode
 *  javax.websocket.CloseReason$CloseCodes
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Extension
 *  javax.websocket.Session
 *  javax.websocket.server.ServerEndpointConfig
 *  org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
 *  org.apache.coyote.http11.upgrade.UpgradeInfo
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState
 *  org.apache.tomcat.util.net.SSLSupport
 *  org.apache.tomcat.util.net.SocketEvent
 *  org.apache.tomcat.util.net.SocketWrapperBase
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.server;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.http.WebConnection;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsIOException;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.server.WsFrameServer;
import org.apache.tomcat.websocket.server.WsHandshakeRequest;
import org.apache.tomcat.websocket.server.WsRemoteEndpointImplServer;
import org.apache.tomcat.websocket.server.WsServerContainer;

public class WsHttpUpgradeHandler
implements InternalHttpUpgradeHandler {
    private final Log log = LogFactory.getLog(WsHttpUpgradeHandler.class);
    private static final StringManager sm = StringManager.getManager(WsHttpUpgradeHandler.class);
    private final ClassLoader applicationClassLoader;
    private SocketWrapperBase<?> socketWrapper;
    private UpgradeInfo upgradeInfo = new UpgradeInfo();
    private Endpoint ep;
    private ServerEndpointConfig serverEndpointConfig;
    private WsServerContainer webSocketContainer;
    private WsHandshakeRequest handshakeRequest;
    private List<Extension> negotiatedExtensions;
    private String subProtocol;
    private Transformation transformation;
    private Map<String, String> pathParameters;
    private boolean secure;
    private WebConnection connection;
    private WsRemoteEndpointImplServer wsRemoteEndpointServer;
    private WsFrameServer wsFrame;
    private WsSession wsSession;

    public WsHttpUpgradeHandler() {
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public void setSocketWrapper(SocketWrapperBase<?> socketWrapper) {
        this.socketWrapper = socketWrapper;
    }

    public void preInit(ServerEndpointConfig serverEndpointConfig, WsServerContainer wsc, WsHandshakeRequest handshakeRequest, List<Extension> negotiatedExtensionsPhase2, String subProtocol, Transformation transformation, Map<String, String> pathParameters, boolean secure) {
        this.serverEndpointConfig = serverEndpointConfig;
        this.webSocketContainer = wsc;
        this.handshakeRequest = handshakeRequest;
        this.negotiatedExtensions = negotiatedExtensionsPhase2;
        this.subProtocol = subProtocol;
        this.transformation = transformation;
        this.pathParameters = pathParameters;
        this.secure = secure;
    }

    public void init(WebConnection connection) {
        this.connection = connection;
        if (this.serverEndpointConfig == null) {
            throw new IllegalStateException(sm.getString("wsHttpUpgradeHandler.noPreInit"));
        }
        String httpSessionId = null;
        Object session = this.handshakeRequest.getHttpSession();
        if (session != null) {
            httpSessionId = ((HttpSession)session).getId();
        }
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.wsRemoteEndpointServer = new WsRemoteEndpointImplServer(this.socketWrapper, this.upgradeInfo, this.webSocketContainer, connection);
            this.wsSession = new WsSession(this.wsRemoteEndpointServer, this.webSocketContainer, this.handshakeRequest.getRequestURI(), this.handshakeRequest.getParameterMap(), this.handshakeRequest.getQueryString(), this.handshakeRequest.getUserPrincipal(), httpSessionId, this.negotiatedExtensions, this.subProtocol, this.pathParameters, this.secure, this.serverEndpointConfig);
            this.ep = this.wsSession.getLocal();
            this.wsFrame = new WsFrameServer(this.socketWrapper, this.upgradeInfo, this.wsSession, this.transformation, this.applicationClassLoader);
            this.wsRemoteEndpointServer.setTransformation(this.wsFrame.getTransformation());
            this.ep.onOpen((Session)this.wsSession, (EndpointConfig)this.serverEndpointConfig);
            this.webSocketContainer.registerSession(this.serverEndpointConfig.getPath(), this.wsSession);
        }
        catch (DeploymentException e) {
            throw new IllegalArgumentException(e);
        }
        finally {
            t.setContextClassLoader(cl);
        }
    }

    public UpgradeInfo getUpgradeInfo() {
        return this.upgradeInfo;
    }

    public AbstractEndpoint.Handler.SocketState upgradeDispatch(SocketEvent status) {
        switch (status) {
            case OPEN_READ: {
                try {
                    return this.wsFrame.notifyDataAvailable();
                }
                catch (WsIOException ws) {
                    this.close(ws.getCloseReason());
                }
                catch (IOException ioe) {
                    this.onError(ioe);
                    CloseReason cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, ioe.getMessage());
                    this.close(cr);
                }
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
            case OPEN_WRITE: {
                this.wsRemoteEndpointServer.onWritePossible(false);
                break;
            }
            case STOP: {
                CloseReason cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, sm.getString("wsHttpUpgradeHandler.serverStop"));
                try {
                    this.wsSession.close(cr);
                    break;
                }
                catch (IOException ioe) {
                    this.onError(ioe);
                    cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, ioe.getMessage());
                    this.close(cr);
                    return AbstractEndpoint.Handler.SocketState.CLOSED;
                }
            }
            case ERROR: {
                this.wsRemoteEndpointServer.clearHandler(this.socketWrapper.getError(), false);
                String msg = sm.getString("wsHttpUpgradeHandler.closeOnError");
                this.wsSession.doClose(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg));
            }
            case DISCONNECT: 
            case TIMEOUT: 
            case CONNECT_FAIL: {
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
        }
        if (this.wsSession.isClosed()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        return AbstractEndpoint.Handler.SocketState.UPGRADED;
    }

    public void timeoutAsync(long now) {
    }

    public void pause() {
    }

    public void destroy() {
        WebConnection connection = this.connection;
        if (connection != null) {
            this.connection = null;
            try {
                connection.close();
            }
            catch (Exception e) {
                this.log.error((Object)sm.getString("wsHttpUpgradeHandler.destroyFailed"), (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onError(Throwable throwable) {
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.ep.onError((Session)this.wsSession, throwable);
        }
        finally {
            t.setContextClassLoader(cl);
        }
    }

    private void close(CloseReason cr) {
        this.wsRemoteEndpointServer.clearHandler(new EOFException(), true);
        this.wsSession.onClose(cr);
    }

    public void setSslSupport(SSLSupport sslSupport) {
    }
}

