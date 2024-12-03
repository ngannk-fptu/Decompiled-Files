/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.ClientEndpoint
 *  javax.websocket.ClientEndpointConfig
 *  javax.websocket.ClientEndpointConfig$Builder
 *  javax.websocket.ClientEndpointConfig$Configurator
 *  javax.websocket.CloseReason
 *  javax.websocket.CloseReason$CloseCode
 *  javax.websocket.CloseReason$CloseCodes
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Extension
 *  javax.websocket.Extension$Parameter
 *  javax.websocket.HandshakeResponse
 *  javax.websocket.Session
 *  javax.websocket.WebSocketContainer
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.InstanceManagerBindings
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.codec.binary.Base64
 *  org.apache.tomcat.util.collections.CaseInsensitiveKeyMap
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.KeyStoreUtil
 */
package org.apache.tomcat.websocket;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.InstanceManagerBindings;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.KeyStoreUtil;
import org.apache.tomcat.websocket.AsyncChannelGroupUtil;
import org.apache.tomcat.websocket.AsyncChannelWrapper;
import org.apache.tomcat.websocket.AsyncChannelWrapperNonSecure;
import org.apache.tomcat.websocket.AsyncChannelWrapperSecure;
import org.apache.tomcat.websocket.AuthenticationException;
import org.apache.tomcat.websocket.AuthenticationType;
import org.apache.tomcat.websocket.Authenticator;
import org.apache.tomcat.websocket.AuthenticatorFactory;
import org.apache.tomcat.websocket.BackgroundProcess;
import org.apache.tomcat.websocket.BackgroundProcessManager;
import org.apache.tomcat.websocket.ClientEndpointHolder;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.EndpointClassHolder;
import org.apache.tomcat.websocket.EndpointHolder;
import org.apache.tomcat.websocket.PojoClassHolder;
import org.apache.tomcat.websocket.PojoHolder;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.TransformationFactory;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.WsFrameClient;
import org.apache.tomcat.websocket.WsHandshakeResponse;
import org.apache.tomcat.websocket.WsRemoteEndpointImplClient;
import org.apache.tomcat.websocket.WsSession;

public class WsWebSocketContainer
implements WebSocketContainer,
BackgroundProcess {
    private static final StringManager sm = StringManager.getManager(WsWebSocketContainer.class);
    private static final Random RANDOM = new Random();
    private static final byte[] CRLF = new byte[]{13, 10};
    private static final byte[] GET_BYTES = "GET ".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] ROOT_URI_BYTES = "/".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] HTTP_VERSION_BYTES = " HTTP/1.1\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private volatile AsynchronousChannelGroup asynchronousChannelGroup = null;
    private final Object asynchronousChannelGroupLock = new Object();
    private final Log log = LogFactory.getLog(WsWebSocketContainer.class);
    private final Map<Object, Set<WsSession>> endpointSessionMap = new HashMap<Object, Set<WsSession>>();
    private final Map<WsSession, WsSession> sessions = new ConcurrentHashMap<WsSession, WsSession>();
    private final Object endPointSessionMapLock = new Object();
    private long defaultAsyncTimeout = -1L;
    private int maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private int maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private volatile long defaultMaxSessionIdleTimeout = 0L;
    private int backgroundProcessCount = 0;
    private int processPeriod = Constants.DEFAULT_PROCESS_PERIOD;
    private InstanceManager instanceManager;

    protected InstanceManager getInstanceManager(ClassLoader classLoader) {
        if (this.instanceManager != null) {
            return this.instanceManager;
        }
        return InstanceManagerBindings.get((ClassLoader)classLoader);
    }

    protected void setInstanceManager(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    public Session connectToServer(Object pojo, URI path) throws DeploymentException {
        ClientEndpointConfig config = this.createClientEndpointConfig(pojo.getClass());
        PojoHolder holder = new PojoHolder(pojo, config);
        return this.connectToServerRecursive(holder, config, path, new HashSet<URI>());
    }

    public Session connectToServer(Class<?> annotatedEndpointClass, URI path) throws DeploymentException {
        ClientEndpointConfig config = this.createClientEndpointConfig(annotatedEndpointClass);
        PojoClassHolder holder = new PojoClassHolder(annotatedEndpointClass, config);
        return this.connectToServerRecursive(holder, config, path, new HashSet<URI>());
    }

    private ClientEndpointConfig createClientEndpointConfig(Class<?> annotatedEndpointClass) throws DeploymentException {
        ClientEndpoint annotation = annotatedEndpointClass.getAnnotation(ClientEndpoint.class);
        if (annotation == null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.missingAnnotation", new Object[]{annotatedEndpointClass.getName()}));
        }
        Class configuratorClazz = annotation.configurator();
        ClientEndpointConfig.Configurator configurator = null;
        if (!ClientEndpointConfig.Configurator.class.equals((Object)configuratorClazz)) {
            try {
                configurator = (ClientEndpointConfig.Configurator)configuratorClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ReflectiveOperationException e) {
                throw new DeploymentException(sm.getString("wsWebSocketContainer.defaultConfiguratorFail"), (Throwable)e);
            }
        }
        ClientEndpointConfig.Builder builder = ClientEndpointConfig.Builder.create();
        if (configurator != null) {
            builder.configurator(configurator);
        }
        ClientEndpointConfig config = builder.decoders(Arrays.asList(annotation.decoders())).encoders(Arrays.asList(annotation.encoders())).preferredSubprotocols(Arrays.asList(annotation.subprotocols())).build();
        return config;
    }

    public Session connectToServer(Class<? extends Endpoint> clazz, ClientEndpointConfig clientEndpointConfiguration, URI path) throws DeploymentException {
        EndpointClassHolder holder = new EndpointClassHolder(clazz);
        return this.connectToServerRecursive(holder, clientEndpointConfiguration, path, new HashSet<URI>());
    }

    public Session connectToServer(Endpoint endpoint, ClientEndpointConfig clientEndpointConfiguration, URI path) throws DeploymentException {
        EndpointHolder holder = new EndpointHolder(endpoint);
        return this.connectToServerRecursive(holder, clientEndpointConfiguration, path, new HashSet<URI>());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Session connectToServerRecursive(ClientEndpointHolder clientEndpointHolder, ClientEndpointConfig clientEndpointConfiguration, URI path, Set<URI> redirectSet) throws DeploymentException {
        String subProtocol;
        AsynchronousSocketChannel socketChannel;
        URI proxyPath;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("wsWebSocketContainer.connect.entry", new Object[]{clientEndpointHolder.getClassName(), path}));
        }
        boolean secure = false;
        ByteBuffer proxyConnect = null;
        String scheme = path.getScheme();
        if ("ws".equalsIgnoreCase(scheme)) {
            proxyPath = URI.create("http" + path.toString().substring(2));
        } else {
            if (!"wss".equalsIgnoreCase(scheme)) {
                throw new DeploymentException(sm.getString("wsWebSocketContainer.pathWrongScheme", new Object[]{scheme}));
            }
            proxyPath = URI.create("https" + path.toString().substring(3));
            secure = true;
        }
        String host = path.getHost();
        if (host == null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.pathNoHost"));
        }
        int port = path.getPort();
        SocketAddress sa = null;
        List<Proxy> proxies = ProxySelector.getDefault().select(proxyPath);
        Proxy selectedProxy = null;
        for (Proxy proxy : proxies) {
            InetSocketAddress inet;
            if (!proxy.type().equals((Object)Proxy.Type.HTTP)) continue;
            sa = proxy.address();
            if (sa instanceof InetSocketAddress && (inet = (InetSocketAddress)sa).isUnresolved()) {
                sa = new InetSocketAddress(inet.getHostName(), inet.getPort());
            }
            selectedProxy = proxy;
            break;
        }
        if (port == -1) {
            port = "ws".equalsIgnoreCase(scheme) ? 80 : 443;
        }
        Map userProperties = clientEndpointConfiguration.getUserProperties();
        if (sa == null) {
            sa = new InetSocketAddress(host, port);
        } else {
            proxyConnect = WsWebSocketContainer.createProxyRequest(host, port, (String)userProperties.get("Proxy-Authorization"));
        }
        Map<String, List<String>> reqHeaders = WsWebSocketContainer.createRequestHeaders(host, port, secure, clientEndpointConfiguration);
        clientEndpointConfiguration.getConfigurator().beforeRequest(reqHeaders);
        if (Constants.DEFAULT_ORIGIN_HEADER_VALUE != null && !reqHeaders.containsKey("Origin")) {
            ArrayList<String> originValues = new ArrayList<String>(1);
            originValues.add(Constants.DEFAULT_ORIGIN_HEADER_VALUE);
            reqHeaders.put("Origin", originValues);
        }
        ByteBuffer request = WsWebSocketContainer.createRequest(path, reqHeaders);
        long timeout = 5000L;
        String timeoutValue = (String)userProperties.get("org.apache.tomcat.websocket.IO_TIMEOUT_MS");
        if (timeoutValue != null) {
            timeout = Long.valueOf(timeoutValue).intValue();
        }
        try {
            socketChannel = AsynchronousSocketChannel.open(this.getAsynchronousChannelGroup());
        }
        catch (IOException ioe) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.asynchronousSocketChannelFail"), (Throwable)ioe);
        }
        ByteBuffer response = ByteBuffer.allocate(this.getDefaultMaxBinaryMessageBufferSize());
        boolean success = false;
        ArrayList<Extension> extensionsAgreed = new ArrayList<Extension>();
        Transformation transformation = null;
        AsyncChannelWrapper channel = null;
        try {
            Future<Void> fConnect = socketChannel.connect(sa);
            if (proxyConnect != null) {
                fConnect.get(timeout, TimeUnit.MILLISECONDS);
                channel = new AsyncChannelWrapperNonSecure(socketChannel);
                WsWebSocketContainer.writeRequest(channel, proxyConnect, timeout);
                HttpResponse httpResponse = this.processResponse(response, channel, timeout);
                if (httpResponse.status == 407) {
                    Session session = this.processAuthenticationChallenge(clientEndpointHolder, clientEndpointConfiguration, path, redirectSet, userProperties, request, httpResponse, AuthenticationType.PROXY);
                    return session;
                }
                if (httpResponse.getStatus() != 200) {
                    throw new DeploymentException(sm.getString("wsWebSocketContainer.proxyConnectFail", new Object[]{selectedProxy, Integer.toString(httpResponse.getStatus())}));
                }
            }
            if (secure) {
                SSLEngine sslEngine = this.createSSLEngine(clientEndpointConfiguration, host, port);
                channel = new AsyncChannelWrapperSecure(socketChannel, sslEngine);
            } else if (channel == null) {
                channel = new AsyncChannelWrapperNonSecure(socketChannel);
            }
            fConnect.get(timeout, TimeUnit.MILLISECONDS);
            Future<Void> fHandshake = channel.handshake();
            fHandshake.get(timeout, TimeUnit.MILLISECONDS);
            if (this.log.isDebugEnabled()) {
                SocketAddress localAddress = null;
                try {
                    localAddress = channel.getLocalAddress();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.log.debug((Object)sm.getString("wsWebSocketContainer.connect.write", new Object[]{request.position(), request.limit(), localAddress}));
            }
            WsWebSocketContainer.writeRequest(channel, request, timeout);
            HttpResponse httpResponse = this.processResponse(response, channel, timeout);
            int maxRedirects = 20;
            String maxRedirectsValue = (String)userProperties.get("org.apache.tomcat.websocket.MAX_REDIRECTIONS");
            if (maxRedirectsValue != null) {
                maxRedirects = Integer.parseInt(maxRedirectsValue);
            }
            if (httpResponse.status != 101) {
                if (WsWebSocketContainer.isRedirectStatus(httpResponse.status)) {
                    String redirectScheme;
                    List locationHeader = (List)httpResponse.getHandshakeResponse().getHeaders().get("Location");
                    if (locationHeader == null || locationHeader.isEmpty() || locationHeader.get(0) == null || ((String)locationHeader.get(0)).isEmpty()) {
                        throw new DeploymentException(sm.getString("wsWebSocketContainer.missingLocationHeader", new Object[]{Integer.toString(httpResponse.status)}));
                    }
                    URI redirectLocation = URI.create((String)locationHeader.get(0)).normalize();
                    if (!redirectLocation.isAbsolute()) {
                        redirectLocation = path.resolve(redirectLocation);
                    }
                    if ((redirectScheme = redirectLocation.getScheme().toLowerCase()).startsWith("http")) {
                        redirectLocation = new URI(redirectScheme.replace("http", "ws"), redirectLocation.getUserInfo(), redirectLocation.getHost(), redirectLocation.getPort(), redirectLocation.getPath(), redirectLocation.getQuery(), redirectLocation.getFragment());
                    }
                    if (redirectSet.add(redirectLocation) && redirectSet.size() <= maxRedirects) {
                        Session session = this.connectToServerRecursive(clientEndpointHolder, clientEndpointConfiguration, redirectLocation, redirectSet);
                        return session;
                    }
                    throw new DeploymentException(sm.getString("wsWebSocketContainer.redirectThreshold", new Object[]{redirectLocation, Integer.toString(redirectSet.size()), Integer.toString(maxRedirects)}));
                }
                if (httpResponse.status == 401) {
                    Session locationHeader = this.processAuthenticationChallenge(clientEndpointHolder, clientEndpointConfiguration, path, redirectSet, userProperties, request, httpResponse, AuthenticationType.WWW);
                    return locationHeader;
                }
                throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", new Object[]{Integer.toString(httpResponse.status)}));
            }
            HandshakeResponse handshakeResponse = httpResponse.getHandshakeResponse();
            clientEndpointConfiguration.getConfigurator().afterResponse(handshakeResponse);
            List protocolHeaders = (List)handshakeResponse.getHeaders().get("Sec-WebSocket-Protocol");
            if (protocolHeaders == null || protocolHeaders.size() == 0) {
                subProtocol = null;
            } else {
                if (protocolHeaders.size() != 1) throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidSubProtocol"));
                subProtocol = (String)protocolHeaders.get(0);
            }
            List extHeaders = (List)handshakeResponse.getHeaders().get("Sec-WebSocket-Extensions");
            if (extHeaders != null) {
                for (String extHeader : extHeaders) {
                    Util.parseExtensionHeader(extensionsAgreed, extHeader);
                }
            }
            TransformationFactory factory = TransformationFactory.getInstance();
            for (Extension extension : extensionsAgreed) {
                ArrayList<List<Extension.Parameter>> wrapper = new ArrayList<List<Extension.Parameter>>(1);
                wrapper.add(extension.getParameters());
                Transformation t = factory.create(extension.getName(), wrapper, false);
                if (t == null) {
                    throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidExtensionParameters"));
                }
                if (transformation == null) {
                    transformation = t;
                    continue;
                }
                transformation.setNext(t);
            }
            success = true;
        }
        catch (EOFException | InterruptedException | URISyntaxException | ExecutionException | TimeoutException | SSLException | AuthenticationException e) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.httpRequestFailed", new Object[]{path}), (Throwable)e);
        }
        finally {
            if (!success) {
                if (channel != null) {
                    channel.close();
                } else {
                    try {
                        socketChannel.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        WsRemoteEndpointImplClient wsRemoteEndpointClient = new WsRemoteEndpointImplClient(channel);
        WsSession wsSession = new WsSession(clientEndpointHolder, wsRemoteEndpointClient, this, extensionsAgreed, subProtocol, Collections.emptyMap(), secure, clientEndpointConfiguration);
        WsFrameClient wsFrameClient = new WsFrameClient(response, channel, wsSession, transformation);
        wsRemoteEndpointClient.setTransformation(wsFrameClient.getTransformation());
        wsSession.getLocal().onOpen((Session)wsSession, (EndpointConfig)clientEndpointConfiguration);
        this.registerSession(wsSession.getLocal(), wsSession);
        wsFrameClient.startInputProcessing();
        return wsSession;
    }

    private Session processAuthenticationChallenge(ClientEndpointHolder clientEndpointHolder, ClientEndpointConfig clientEndpointConfiguration, URI path, Set<URI> redirectSet, Map<String, Object> userProperties, ByteBuffer request, HttpResponse httpResponse, AuthenticationType authenticationType) throws DeploymentException, AuthenticationException {
        if (userProperties.get(authenticationType.getAuthorizationHeaderName()) != null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.failedAuthentication", new Object[]{httpResponse.status, authenticationType.getAuthorizationHeaderName()}));
        }
        List authenticateHeaders = (List)httpResponse.getHandshakeResponse().getHeaders().get(authenticationType.getAuthenticateHeaderName());
        if (authenticateHeaders == null || authenticateHeaders.isEmpty() || authenticateHeaders.get(0) == null || ((String)authenticateHeaders.get(0)).isEmpty()) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.missingAuthenticateHeader", new Object[]{Integer.toString(httpResponse.status), authenticationType.getAuthenticateHeaderName()}));
        }
        String authScheme = ((String)authenticateHeaders.get(0)).split("\\s+", 2)[0];
        Authenticator auth = AuthenticatorFactory.getAuthenticator(authScheme);
        if (auth == null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.unsupportedAuthScheme", new Object[]{httpResponse.status, authScheme}));
        }
        String requestUri = new String(request.array(), StandardCharsets.ISO_8859_1).split("\\s", 3)[1];
        userProperties.put(authenticationType.getAuthorizationHeaderName(), auth.getAuthorization(requestUri, (String)authenticateHeaders.get(0), (String)userProperties.get(authenticationType.getUserNameProperty()), (String)userProperties.get(authenticationType.getUserPasswordProperty()), (String)userProperties.get(authenticationType.getUserRealmProperty())));
        return this.connectToServerRecursive(clientEndpointHolder, clientEndpointConfiguration, path, redirectSet);
    }

    private static void writeRequest(AsyncChannelWrapper channel, ByteBuffer request, long timeout) throws TimeoutException, InterruptedException, ExecutionException {
        int toWrite = request.limit();
        Future<Integer> fWrite = channel.write(request);
        Integer thisWrite = fWrite.get(timeout, TimeUnit.MILLISECONDS);
        toWrite -= thisWrite.intValue();
        while (toWrite > 0) {
            fWrite = channel.write(request);
            thisWrite = fWrite.get(timeout, TimeUnit.MILLISECONDS);
            toWrite -= thisWrite.intValue();
        }
    }

    private static boolean isRedirectStatus(int httpResponseCode) {
        boolean isRedirect = false;
        switch (httpResponseCode) {
            case 300: 
            case 301: 
            case 302: 
            case 303: 
            case 305: 
            case 307: {
                isRedirect = true;
                break;
            }
        }
        return isRedirect;
    }

    private static ByteBuffer createProxyRequest(String host, int port, String authorizationHeader) {
        StringBuilder request = new StringBuilder();
        request.append("CONNECT ");
        request.append(host);
        request.append(':');
        request.append(port);
        request.append(" HTTP/1.1\r\nProxy-Connection: keep-alive\r\nConnection: keepalive\r\nHost: ");
        request.append(host);
        request.append(':');
        request.append(port);
        if (authorizationHeader != null) {
            request.append("\r\n");
            request.append("Proxy-Authorization");
            request.append(':');
            request.append(authorizationHeader);
        }
        request.append("\r\n\r\n");
        byte[] bytes = request.toString().getBytes(StandardCharsets.ISO_8859_1);
        return ByteBuffer.wrap(bytes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void registerSession(Object key, WsSession wsSession) {
        if (!wsSession.isOpen()) {
            return;
        }
        Object object = this.endPointSessionMapLock;
        synchronized (object) {
            if (this.endpointSessionMap.size() == 0) {
                BackgroundProcessManager.getInstance().register(this);
            }
            this.endpointSessionMap.computeIfAbsent(key, k -> new HashSet()).add(wsSession);
        }
        this.sessions.put(wsSession, wsSession);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void unregisterSession(Object key, WsSession wsSession) {
        Object object = this.endPointSessionMapLock;
        synchronized (object) {
            Set<WsSession> wsSessions = this.endpointSessionMap.get(key);
            if (wsSessions != null) {
                wsSessions.remove(wsSession);
                if (wsSessions.size() == 0) {
                    this.endpointSessionMap.remove(key);
                }
            }
            if (this.endpointSessionMap.size() == 0) {
                BackgroundProcessManager.getInstance().unregister(this);
            }
        }
        this.sessions.remove(wsSession);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Set<Session> getOpenSessions(Object key) {
        HashSet<Session> result = new HashSet<Session>();
        Object object = this.endPointSessionMapLock;
        synchronized (object) {
            Set<WsSession> sessions = this.endpointSessionMap.get(key);
            if (sessions != null) {
                result.addAll(sessions);
            }
        }
        return result;
    }

    private static Map<String, List<String>> createRequestHeaders(String host, int port, boolean secure, ClientEndpointConfig clientEndpointConfiguration) {
        HashMap<String, List<String>> headers = new HashMap<String, List<String>>();
        List extensions = clientEndpointConfiguration.getExtensions();
        List subProtocols = clientEndpointConfiguration.getPreferredSubprotocols();
        Map userProperties = clientEndpointConfiguration.getUserProperties();
        if (userProperties.get("Authorization") != null) {
            ArrayList<String> authValues = new ArrayList<String>(1);
            authValues.add((String)userProperties.get("Authorization"));
            headers.put("Authorization", authValues);
        }
        ArrayList<String> hostValues = new ArrayList<String>(1);
        if (port == 80 && !secure || port == 443 && secure) {
            hostValues.add(host);
        } else {
            hostValues.add(host + ':' + port);
        }
        headers.put("Host", hostValues);
        ArrayList<String> upgradeValues = new ArrayList<String>(1);
        upgradeValues.add("websocket");
        headers.put("Upgrade", upgradeValues);
        ArrayList<String> connectionValues = new ArrayList<String>(1);
        connectionValues.add("upgrade");
        headers.put("Connection", connectionValues);
        ArrayList<String> wsVersionValues = new ArrayList<String>(1);
        wsVersionValues.add("13");
        headers.put("Sec-WebSocket-Version", wsVersionValues);
        ArrayList<String> wsKeyValues = new ArrayList<String>(1);
        wsKeyValues.add(WsWebSocketContainer.generateWsKeyValue());
        headers.put("Sec-WebSocket-Key", wsKeyValues);
        if (subProtocols != null && subProtocols.size() > 0) {
            headers.put("Sec-WebSocket-Protocol", subProtocols);
        }
        if (extensions != null && extensions.size() > 0) {
            headers.put("Sec-WebSocket-Extensions", WsWebSocketContainer.generateExtensionHeaders(extensions));
        }
        return headers;
    }

    private static List<String> generateExtensionHeaders(List<Extension> extensions) {
        ArrayList<String> result = new ArrayList<String>(extensions.size());
        for (Extension extension : extensions) {
            StringBuilder header = new StringBuilder();
            header.append(extension.getName());
            for (Extension.Parameter param : extension.getParameters()) {
                header.append(';');
                header.append(param.getName());
                String value = param.getValue();
                if (value == null || value.length() <= 0) continue;
                header.append('=');
                header.append(value);
            }
            result.add(header.toString());
        }
        return result;
    }

    private static String generateWsKeyValue() {
        byte[] keyBytes = new byte[16];
        RANDOM.nextBytes(keyBytes);
        return Base64.encodeBase64String((byte[])keyBytes);
    }

    private static ByteBuffer createRequest(URI uri, Map<String, List<String>> reqHeaders) {
        ByteBuffer result = ByteBuffer.allocate(4096);
        result.put(GET_BYTES);
        String path = uri.getPath();
        if (null == path || path.isEmpty()) {
            result.put(ROOT_URI_BYTES);
        } else {
            result.put(uri.getRawPath().getBytes(StandardCharsets.ISO_8859_1));
        }
        String query = uri.getRawQuery();
        if (query != null) {
            result.put((byte)63);
            result.put(query.getBytes(StandardCharsets.ISO_8859_1));
        }
        result.put(HTTP_VERSION_BYTES);
        for (Map.Entry<String, List<String>> entry : reqHeaders.entrySet()) {
            result = WsWebSocketContainer.addHeader(result, entry.getKey(), entry.getValue());
        }
        result.put(CRLF);
        result.flip();
        return result;
    }

    private static ByteBuffer addHeader(ByteBuffer result, String key, List<String> values) {
        if (values.isEmpty()) {
            return result;
        }
        result = WsWebSocketContainer.putWithExpand(result, key.getBytes(StandardCharsets.ISO_8859_1));
        result = WsWebSocketContainer.putWithExpand(result, ": ".getBytes(StandardCharsets.ISO_8859_1));
        result = WsWebSocketContainer.putWithExpand(result, StringUtils.join(values).getBytes(StandardCharsets.ISO_8859_1));
        result = WsWebSocketContainer.putWithExpand(result, CRLF);
        return result;
    }

    private static ByteBuffer putWithExpand(ByteBuffer input, byte[] bytes) {
        if (bytes.length > input.remaining()) {
            int newSize = bytes.length > input.capacity() ? 2 * bytes.length : input.capacity() * 2;
            ByteBuffer expanded = ByteBuffer.allocate(newSize);
            input.flip();
            expanded.put(input);
            input = expanded;
        }
        return input.put(bytes);
    }

    private HttpResponse processResponse(ByteBuffer response, AsyncChannelWrapper channel, long timeout) throws InterruptedException, ExecutionException, DeploymentException, EOFException, TimeoutException {
        CaseInsensitiveKeyMap headers = new CaseInsensitiveKeyMap();
        int status = 0;
        boolean readStatus = false;
        boolean readHeaders = false;
        String line = null;
        while (!readHeaders) {
            Integer bytesRead;
            response.clear();
            Future<Integer> read = channel.read(response);
            try {
                bytesRead = read.get(timeout, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e) {
                TimeoutException te = new TimeoutException(sm.getString("wsWebSocketContainer.responseFail", new Object[]{Integer.toString(status), headers}));
                te.initCause(e);
                throw te;
            }
            if (bytesRead == -1) {
                throw new EOFException(sm.getString("wsWebSocketContainer.responseFail", new Object[]{Integer.toString(status), headers}));
            }
            response.flip();
            while (response.hasRemaining() && !readHeaders) {
                if ("\r\n".equals(line = line == null ? this.readLine(response) : line + this.readLine(response))) {
                    readHeaders = true;
                    continue;
                }
                if (!line.endsWith("\r\n")) continue;
                if (readStatus) {
                    this.parseHeaders(line, (Map<String, List<String>>)headers);
                } else {
                    status = this.parseStatus(line);
                    readStatus = true;
                }
                line = null;
            }
        }
        return new HttpResponse(status, new WsHandshakeResponse((Map<String, List<String>>)headers));
    }

    private int parseStatus(String line) throws DeploymentException {
        String[] parts = line.trim().split(" ");
        if (parts.length < 2 || !"HTTP/1.0".equals(parts[0]) && !"HTTP/1.1".equals(parts[0])) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", new Object[]{line}));
        }
        try {
            return Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException nfe) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", new Object[]{line}));
        }
    }

    private void parseHeaders(String line, Map<String, List<String>> headers) {
        int index = line.indexOf(58);
        if (index == -1) {
            this.log.warn((Object)sm.getString("wsWebSocketContainer.invalidHeader", new Object[]{line}));
            return;
        }
        String headerName = line.substring(0, index).trim().toLowerCase(Locale.ENGLISH);
        String headerValue = line.substring(index + 1).trim();
        List values = headers.computeIfAbsent(headerName, k -> new ArrayList(1));
        values.add(headerValue);
    }

    private String readLine(ByteBuffer response) {
        StringBuilder sb = new StringBuilder();
        char c = '\u0000';
        while (response.hasRemaining()) {
            c = (char)response.get();
            sb.append(c);
            if (c != '\n') continue;
            break;
        }
        return sb.toString();
    }

    private SSLEngine createSSLEngine(ClientEndpointConfig clientEndpointConfig, String host, int port) throws DeploymentException {
        Map userProperties = clientEndpointConfig.getUserProperties();
        try {
            SSLContext sslContext = (SSLContext)userProperties.get("org.apache.tomcat.websocket.SSL_CONTEXT");
            if (sslContext == null) {
                sslContext = SSLContext.getInstance("TLS");
                String sslTrustStoreValue = (String)userProperties.get("org.apache.tomcat.websocket.SSL_TRUSTSTORE");
                if (sslTrustStoreValue != null) {
                    String sslTrustStorePwdValue = (String)userProperties.get("org.apache.tomcat.websocket.SSL_TRUSTSTORE_PWD");
                    if (sslTrustStorePwdValue == null) {
                        sslTrustStorePwdValue = "changeit";
                    }
                    File keyStoreFile = new File(sslTrustStoreValue);
                    KeyStore ks = KeyStore.getInstance("JKS");
                    try (FileInputStream is = new FileInputStream(keyStoreFile);){
                        KeyStoreUtil.load((KeyStore)ks, (InputStream)is, (char[])sslTrustStorePwdValue.toCharArray());
                    }
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(ks);
                    sslContext.init(null, tmf.getTrustManagers(), null);
                } else {
                    sslContext.init(null, null, null);
                }
            }
            SSLEngine engine = sslContext.createSSLEngine(host, port);
            String sslProtocolsValue = (String)userProperties.get("org.apache.tomcat.websocket.SSL_PROTOCOLS");
            if (sslProtocolsValue != null) {
                engine.setEnabledProtocols(sslProtocolsValue.split(","));
            }
            engine.setUseClientMode(true);
            SSLParameters sslParams = engine.getSSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            engine.setSSLParameters(sslParams);
            return engine;
        }
        catch (Exception e) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.sslEngineFail"), (Throwable)e);
        }
    }

    public long getDefaultMaxSessionIdleTimeout() {
        return this.defaultMaxSessionIdleTimeout;
    }

    public void setDefaultMaxSessionIdleTimeout(long timeout) {
        this.defaultMaxSessionIdleTimeout = timeout;
    }

    public int getDefaultMaxBinaryMessageBufferSize() {
        return this.maxBinaryMessageBufferSize;
    }

    public void setDefaultMaxBinaryMessageBufferSize(int max) {
        this.maxBinaryMessageBufferSize = max;
    }

    public int getDefaultMaxTextMessageBufferSize() {
        return this.maxTextMessageBufferSize;
    }

    public void setDefaultMaxTextMessageBufferSize(int max) {
        this.maxTextMessageBufferSize = max;
    }

    public Set<Extension> getInstalledExtensions() {
        return Collections.emptySet();
    }

    public long getDefaultAsyncSendTimeout() {
        return this.defaultAsyncTimeout;
    }

    public void setAsyncSendTimeout(long timeout) {
        this.defaultAsyncTimeout = timeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        CloseReason cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, sm.getString("wsWebSocketContainer.shutdown"));
        for (WsSession session : this.sessions.keySet()) {
            try {
                session.close(cr);
            }
            catch (IOException ioe) {
                this.log.debug((Object)sm.getString("wsWebSocketContainer.sessionCloseFail", new Object[]{session.getId()}), (Throwable)ioe);
            }
        }
        if (this.asynchronousChannelGroup != null) {
            Object object = this.asynchronousChannelGroupLock;
            synchronized (object) {
                if (this.asynchronousChannelGroup != null) {
                    AsyncChannelGroupUtil.unregister();
                    this.asynchronousChannelGroup = null;
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private AsynchronousChannelGroup getAsynchronousChannelGroup() {
        AsynchronousChannelGroup result = this.asynchronousChannelGroup;
        if (result == null) {
            Object object = this.asynchronousChannelGroupLock;
            synchronized (object) {
                if (this.asynchronousChannelGroup == null) {
                    this.asynchronousChannelGroup = AsyncChannelGroupUtil.register();
                }
                result = this.asynchronousChannelGroup;
            }
        }
        return result;
    }

    @Override
    public void backgroundProcess() {
        ++this.backgroundProcessCount;
        if (this.backgroundProcessCount >= this.processPeriod) {
            this.backgroundProcessCount = 0;
            for (WsSession wsSession : this.sessions.keySet()) {
                wsSession.checkExpiration();
            }
        }
    }

    @Override
    public void setProcessPeriod(int period) {
        this.processPeriod = period;
    }

    @Override
    public int getProcessPeriod() {
        return this.processPeriod;
    }

    private static class HttpResponse {
        private final int status;
        private final HandshakeResponse handshakeResponse;

        HttpResponse(int status, HandshakeResponse handshakeResponse) {
            this.status = status;
            this.handshakeResponse = handshakeResponse;
        }

        public int getStatus() {
            return this.status;
        }

        public HandshakeResponse getHandshakeResponse() {
            return this.handshakeResponse;
        }
    }
}

