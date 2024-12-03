/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.Filter
 *  javax.servlet.FilterRegistration$Dynamic
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.websocket.CloseReason
 *  javax.websocket.CloseReason$CloseCode
 *  javax.websocket.CloseReason$CloseCodes
 *  javax.websocket.DeploymentException
 *  javax.websocket.Encoder
 *  javax.websocket.server.ServerContainer
 *  javax.websocket.server.ServerEndpoint
 *  javax.websocket.server.ServerEndpointConfig
 *  javax.websocket.server.ServerEndpointConfig$Builder
 *  javax.websocket.server.ServerEndpointConfig$Configurator
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.naming.NamingException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.apache.tomcat.websocket.pojo.PojoMethodMapping;
import org.apache.tomcat.websocket.server.UpgradeUtil;
import org.apache.tomcat.websocket.server.UriTemplate;
import org.apache.tomcat.websocket.server.WsFilter;
import org.apache.tomcat.websocket.server.WsMappingResult;
import org.apache.tomcat.websocket.server.WsWriteTimeout;

public class WsServerContainer
extends WsWebSocketContainer
implements ServerContainer {
    private static final StringManager sm = StringManager.getManager(WsServerContainer.class);
    private static final CloseReason AUTHENTICATED_HTTP_SESSION_CLOSED = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.VIOLATED_POLICY, "This connection was established under an authenticated HTTP session that has ended.");
    private final WsWriteTimeout wsWriteTimeout = new WsWriteTimeout();
    private final ServletContext servletContext;
    private final Map<String, ExactPathMatch> configExactMatchMap = new ConcurrentHashMap<String, ExactPathMatch>();
    private final Map<Integer, ConcurrentSkipListMap<String, TemplatePathMatch>> configTemplateMatchMap = new ConcurrentHashMap<Integer, ConcurrentSkipListMap<String, TemplatePathMatch>>();
    private volatile boolean enforceNoAddAfterHandshake = Constants.STRICT_SPEC_COMPLIANCE;
    private volatile boolean addAllowed = true;
    private final Map<String, Set<WsSession>> authenticatedSessions = new ConcurrentHashMap<String, Set<WsSession>>();
    private volatile boolean endpointsRegistered = false;
    private volatile boolean deploymentFailed = false;

    WsServerContainer(ServletContext servletContext) {
        FilterRegistration.Dynamic fr;
        this.servletContext = servletContext;
        this.setInstanceManager((InstanceManager)servletContext.getAttribute(InstanceManager.class.getName()));
        String value = servletContext.getInitParameter("org.apache.tomcat.websocket.binaryBufferSize");
        if (value != null) {
            this.setDefaultMaxBinaryMessageBufferSize(Integer.parseInt(value));
        }
        if ((value = servletContext.getInitParameter("org.apache.tomcat.websocket.textBufferSize")) != null) {
            this.setDefaultMaxTextMessageBufferSize(Integer.parseInt(value));
        }
        if ((value = servletContext.getInitParameter("org.apache.tomcat.websocket.noAddAfterHandshake")) != null) {
            this.setEnforceNoAddAfterHandshake(Boolean.parseBoolean(value));
        }
        if ((fr = servletContext.addFilter("Tomcat WebSocket (JSR356) Filter", (Filter)new WsFilter())) != null) {
            fr.setAsyncSupported(true);
            EnumSet<DispatcherType> types = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
            fr.addMappingForUrlPatterns(types, true, new String[]{"/*"});
        }
    }

    public void addEndpoint(ServerEndpointConfig sec) throws DeploymentException {
        this.addEndpoint(sec, false);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void addEndpoint(ServerEndpointConfig sec, boolean fromAnnotatedPojo) throws DeploymentException {
        if (this.enforceNoAddAfterHandshake && !this.addAllowed) {
            throw new DeploymentException(sm.getString("serverContainer.addNotAllowed"));
        }
        if (this.servletContext == null) {
            throw new DeploymentException(sm.getString("serverContainer.servletContextMissing"));
        }
        if (this.deploymentFailed) {
            throw new DeploymentException(sm.getString("serverContainer.failedDeployment", new Object[]{this.servletContext.getContextPath(), this.servletContext.getVirtualServerName()}));
        }
        try {
            UriTemplate uriTemplate;
            String path = sec.getPath();
            PojoMethodMapping methodMapping = new PojoMethodMapping(sec.getEndpointClass(), sec.getDecoders(), path, this.getInstanceManager(Thread.currentThread().getContextClassLoader()));
            if (methodMapping.getOnClose() != null || methodMapping.getOnOpen() != null || methodMapping.getOnError() != null || methodMapping.hasMessageHandlers()) {
                sec.getUserProperties().put("org.apache.tomcat.websocket.pojo.PojoEndpoint.methodMapping", methodMapping);
            }
            if ((uriTemplate = new UriTemplate(path)).hasParameters()) {
                Integer key = uriTemplate.getSegmentCount();
                ConcurrentSkipListMap<String, TemplatePathMatch> templateMatches = this.configTemplateMatchMap.get(key);
                if (templateMatches == null) {
                    templateMatches = new ConcurrentSkipListMap();
                    this.configTemplateMatchMap.putIfAbsent(key, templateMatches);
                    templateMatches = this.configTemplateMatchMap.get(key);
                }
                TemplatePathMatch newMatch = new TemplatePathMatch(sec, uriTemplate, fromAnnotatedPojo);
                TemplatePathMatch oldMatch = templateMatches.putIfAbsent(uriTemplate.getNormalizedPath(), newMatch);
                if (oldMatch != null) {
                    if (!oldMatch.isFromAnnotatedPojo() || newMatch.isFromAnnotatedPojo() || oldMatch.getConfig().getEndpointClass() != newMatch.getConfig().getEndpointClass()) throw new DeploymentException(sm.getString("serverContainer.duplicatePaths", new Object[]{path, sec.getEndpointClass(), sec.getEndpointClass()}));
                    templateMatches.put(path, oldMatch);
                }
            } else {
                ExactPathMatch newMatch = new ExactPathMatch(sec, fromAnnotatedPojo);
                ExactPathMatch oldMatch = this.configExactMatchMap.put(path, newMatch);
                if (oldMatch != null) {
                    if (!oldMatch.isFromAnnotatedPojo() || newMatch.isFromAnnotatedPojo() || oldMatch.getConfig().getEndpointClass() != newMatch.getConfig().getEndpointClass()) throw new DeploymentException(sm.getString("serverContainer.duplicatePaths", new Object[]{path, oldMatch.getConfig().getEndpointClass(), sec.getEndpointClass()}));
                    this.configExactMatchMap.put(path, oldMatch);
                }
            }
            this.endpointsRegistered = true;
            return;
        }
        catch (DeploymentException de) {
            this.failDeployment();
            throw de;
        }
    }

    public void addEndpoint(Class<?> pojo) throws DeploymentException {
        this.addEndpoint(pojo, false);
    }

    void addEndpoint(Class<?> pojo, boolean fromAnnotatedPojo) throws DeploymentException {
        ServerEndpointConfig sec;
        if (this.deploymentFailed) {
            throw new DeploymentException(sm.getString("serverContainer.failedDeployment", new Object[]{this.servletContext.getContextPath(), this.servletContext.getVirtualServerName()}));
        }
        try {
            ServerEndpoint annotation = pojo.getAnnotation(ServerEndpoint.class);
            if (annotation == null) {
                throw new DeploymentException(sm.getString("serverContainer.missingAnnotation", new Object[]{pojo.getName()}));
            }
            String path = annotation.value();
            WsServerContainer.validateEncoders(annotation.encoders(), this.getInstanceManager(Thread.currentThread().getContextClassLoader()));
            Class configuratorClazz = annotation.configurator();
            ServerEndpointConfig.Configurator configurator = null;
            if (!configuratorClazz.equals(ServerEndpointConfig.Configurator.class)) {
                try {
                    configurator = (ServerEndpointConfig.Configurator)annotation.configurator().getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (ReflectiveOperationException e) {
                    throw new DeploymentException(sm.getString("serverContainer.configuratorFail", new Object[]{annotation.configurator().getName(), pojo.getClass().getName()}), (Throwable)e);
                }
            }
            sec = ServerEndpointConfig.Builder.create(pojo, (String)path).decoders(Arrays.asList(annotation.decoders())).encoders(Arrays.asList(annotation.encoders())).subprotocols(Arrays.asList(annotation.subprotocols())).configurator(configurator).build();
        }
        catch (DeploymentException de) {
            this.failDeployment();
            throw de;
        }
        this.addEndpoint(sec, fromAnnotatedPojo);
    }

    void failDeployment() {
        this.deploymentFailed = true;
        this.endpointsRegistered = false;
        this.configExactMatchMap.clear();
        this.configTemplateMatchMap.clear();
    }

    boolean areEndpointsRegistered() {
        return this.endpointsRegistered;
    }

    @Deprecated
    public void doUpgrade(HttpServletRequest request, HttpServletResponse response, ServerEndpointConfig sec, Map<String, String> pathParams) throws ServletException, IOException {
        UpgradeUtil.doUpgrade(this, request, response, sec, pathParams);
    }

    public void upgradeHttpToWebSocket(Object httpServletRequest, Object httpServletResponse, ServerEndpointConfig sec, Map<String, String> pathParameters) throws IOException, DeploymentException {
        try {
            UpgradeUtil.doUpgrade(this, (HttpServletRequest)httpServletRequest, (HttpServletResponse)httpServletResponse, sec, pathParameters);
        }
        catch (ServletException e) {
            throw new DeploymentException(e.getMessage(), (Throwable)e);
        }
    }

    public WsMappingResult findMapping(String path) {
        ExactPathMatch match;
        if (this.addAllowed) {
            this.addAllowed = false;
        }
        if ((match = this.configExactMatchMap.get(path)) != null) {
            return new WsMappingResult(match.getConfig(), Collections.emptyMap());
        }
        UriTemplate pathUriTemplate = null;
        try {
            pathUriTemplate = new UriTemplate(path);
        }
        catch (DeploymentException e) {
            return null;
        }
        Integer key = pathUriTemplate.getSegmentCount();
        ConcurrentSkipListMap<String, TemplatePathMatch> templateMatches = this.configTemplateMatchMap.get(key);
        if (templateMatches == null) {
            return null;
        }
        ServerEndpointConfig sec = null;
        Map<String, String> pathParams = null;
        for (TemplatePathMatch templateMatch : templateMatches.values()) {
            pathParams = templateMatch.getUriTemplate().match(pathUriTemplate);
            if (pathParams == null) continue;
            sec = templateMatch.getConfig();
            break;
        }
        if (sec == null) {
            return null;
        }
        return new WsMappingResult(sec, pathParams);
    }

    @Deprecated
    public boolean isEnforceNoAddAfterHandshake() {
        return this.enforceNoAddAfterHandshake;
    }

    @Deprecated
    public void setEnforceNoAddAfterHandshake(boolean enforceNoAddAfterHandshake) {
        this.enforceNoAddAfterHandshake = enforceNoAddAfterHandshake;
    }

    protected WsWriteTimeout getTimeout() {
        return this.wsWriteTimeout;
    }

    @Override
    protected InstanceManager getInstanceManager(ClassLoader classLoader) {
        return super.getInstanceManager(classLoader);
    }

    @Override
    protected void registerSession(Object key, WsSession wsSession) {
        super.registerSession(key, wsSession);
        if (wsSession.isOpen() && wsSession.getUserPrincipal() != null && wsSession.getHttpSessionId() != null) {
            this.registerAuthenticatedSession(wsSession, wsSession.getHttpSessionId());
        }
    }

    @Override
    protected void unregisterSession(Object key, WsSession wsSession) {
        if (wsSession.getUserPrincipal() != null && wsSession.getHttpSessionId() != null) {
            this.unregisterAuthenticatedSession(wsSession, wsSession.getHttpSessionId());
        }
        super.unregisterSession(key, wsSession);
    }

    private void registerAuthenticatedSession(WsSession wsSession, String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.get(httpSessionId);
        if (wsSessions == null) {
            wsSessions = ConcurrentHashMap.newKeySet();
            this.authenticatedSessions.putIfAbsent(httpSessionId, wsSessions);
            wsSessions = this.authenticatedSessions.get(httpSessionId);
        }
        wsSessions.add(wsSession);
    }

    private void unregisterAuthenticatedSession(WsSession wsSession, String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.get(httpSessionId);
        if (wsSessions != null) {
            wsSessions.remove(wsSession);
        }
    }

    public void closeAuthenticatedSession(String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.remove(httpSessionId);
        if (wsSessions != null && !wsSessions.isEmpty()) {
            for (WsSession wsSession : wsSessions) {
                try {
                    wsSession.close(AUTHENTICATED_HTTP_SESSION_CLOSED);
                }
                catch (IOException iOException) {}
            }
        }
    }

    private static void validateEncoders(Class<? extends Encoder>[] encoders, InstanceManager instanceManager) throws DeploymentException {
        for (Class<? extends Encoder> encoder : encoders) {
            try {
                Encoder instance;
                if (instanceManager == null) {
                    instance = encoder.getConstructor(new Class[0]).newInstance(new Object[0]);
                    continue;
                }
                instance = (Encoder)instanceManager.newInstance(encoder);
                instanceManager.destroyInstance((Object)instance);
            }
            catch (ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(sm.getString("serverContainer.encoderFail", new Object[]{encoder.getName()}), (Throwable)e);
            }
        }
    }

    private static class TemplatePathMatch {
        private final ServerEndpointConfig config;
        private final UriTemplate uriTemplate;
        private final boolean fromAnnotatedPojo;

        TemplatePathMatch(ServerEndpointConfig config, UriTemplate uriTemplate, boolean fromAnnotatedPojo) {
            this.config = config;
            this.uriTemplate = uriTemplate;
            this.fromAnnotatedPojo = fromAnnotatedPojo;
        }

        public ServerEndpointConfig getConfig() {
            return this.config;
        }

        public UriTemplate getUriTemplate() {
            return this.uriTemplate;
        }

        public boolean isFromAnnotatedPojo() {
            return this.fromAnnotatedPojo;
        }
    }

    private static class ExactPathMatch {
        private final ServerEndpointConfig config;
        private final boolean fromAnnotatedPojo;

        ExactPathMatch(ServerEndpointConfig config, boolean fromAnnotatedPojo) {
            this.config = config;
            this.fromAnnotatedPojo = fromAnnotatedPojo;
        }

        public ServerEndpointConfig getConfig() {
            return this.config;
        }

        public boolean isFromAnnotatedPojo() {
            return this.fromAnnotatedPojo;
        }
    }
}

