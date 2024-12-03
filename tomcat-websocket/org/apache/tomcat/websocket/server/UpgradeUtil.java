/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  javax.websocket.Extension
 *  javax.websocket.Extension$Parameter
 *  javax.websocket.HandshakeResponse
 *  javax.websocket.server.HandshakeRequest
 *  javax.websocket.server.ServerEndpointConfig
 *  org.apache.tomcat.util.codec.binary.Base64
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.ConcurrentMessageDigest
 */
package org.apache.tomcat.websocket.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.TransformationFactory;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.WsHandshakeResponse;
import org.apache.tomcat.websocket.pojo.PojoMethodMapping;
import org.apache.tomcat.websocket.server.WsHandshakeRequest;
import org.apache.tomcat.websocket.server.WsHttpUpgradeHandler;
import org.apache.tomcat.websocket.server.WsPerSessionServerEndpointConfig;
import org.apache.tomcat.websocket.server.WsServerContainer;

public class UpgradeUtil {
    private static final StringManager sm = StringManager.getManager((String)UpgradeUtil.class.getPackage().getName());
    private static final byte[] WS_ACCEPT = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes(StandardCharsets.ISO_8859_1);

    private UpgradeUtil() {
    }

    public static boolean isWebSocketUpgradeRequest(ServletRequest request, ServletResponse response) {
        return request instanceof HttpServletRequest && response instanceof HttpServletResponse && UpgradeUtil.headerContainsToken((HttpServletRequest)request, "Upgrade", "websocket") && "GET".equals(((HttpServletRequest)request).getMethod());
    }

    public static void doUpgrade(WsServerContainer sc, HttpServletRequest req, HttpServletResponse resp, ServerEndpointConfig sec, Map<String, String> pathParams) throws ServletException, IOException {
        List<Extension> negotiatedExtensionsPhase2;
        String subProtocol = null;
        if (!UpgradeUtil.headerContainsToken(req, "Connection", "upgrade")) {
            resp.sendError(400);
            return;
        }
        if (!UpgradeUtil.headerContainsToken(req, "Sec-WebSocket-Version", "13")) {
            resp.setStatus(426);
            resp.setHeader("Sec-WebSocket-Version", "13");
            return;
        }
        String key = req.getHeader("Sec-WebSocket-Key");
        if (!UpgradeUtil.validateKey(key)) {
            resp.sendError(400);
            return;
        }
        String origin = req.getHeader("Origin");
        if (!sec.getConfigurator().checkOrigin(origin)) {
            resp.sendError(403);
            return;
        }
        List<String> subProtocols = UpgradeUtil.getTokensFromHeader(req, "Sec-WebSocket-Protocol");
        subProtocol = sec.getConfigurator().getNegotiatedSubprotocol(sec.getSubprotocols(), subProtocols);
        ArrayList<Extension> extensionsRequested = new ArrayList<Extension>();
        Enumeration extHeaders = req.getHeaders("Sec-WebSocket-Extensions");
        while (extHeaders.hasMoreElements()) {
            Util.parseExtensionHeader(extensionsRequested, (String)extHeaders.nextElement());
        }
        List<Extension> installedExtensions = null;
        if (sec.getExtensions().size() == 0) {
            installedExtensions = Constants.INSTALLED_EXTENSIONS;
        } else {
            installedExtensions = new ArrayList<Extension>();
            installedExtensions.addAll(sec.getExtensions());
            installedExtensions.addAll(Constants.INSTALLED_EXTENSIONS);
        }
        List negotiatedExtensionsPhase1 = sec.getConfigurator().getNegotiatedExtensions(installedExtensions, extensionsRequested);
        List<Transformation> transformations = UpgradeUtil.createTransformations(negotiatedExtensionsPhase1);
        if (transformations.isEmpty()) {
            negotiatedExtensionsPhase2 = Collections.emptyList();
        } else {
            negotiatedExtensionsPhase2 = new ArrayList(transformations.size());
            for (Transformation t : transformations) {
                negotiatedExtensionsPhase2.add(t.getExtensionResponse());
            }
        }
        Transformation transformation = null;
        StringBuilder responseHeaderExtensions = new StringBuilder();
        boolean first = true;
        for (Transformation t : transformations) {
            if (first) {
                first = false;
            } else {
                responseHeaderExtensions.append(',');
            }
            UpgradeUtil.append(responseHeaderExtensions, t.getExtensionResponse());
            if (transformation == null) {
                transformation = t;
                continue;
            }
            transformation.setNext(t);
        }
        if (transformation != null && !transformation.validateRsvBits(0)) {
            throw new ServletException(sm.getString("upgradeUtil.incompatibleRsv"));
        }
        resp.setHeader("Upgrade", "websocket");
        resp.setHeader("Connection", "upgrade");
        resp.setHeader("Sec-WebSocket-Accept", UpgradeUtil.getWebSocketAccept(key));
        if (subProtocol != null && subProtocol.length() > 0) {
            resp.setHeader("Sec-WebSocket-Protocol", subProtocol);
        }
        if (!transformations.isEmpty()) {
            resp.setHeader("Sec-WebSocket-Extensions", responseHeaderExtensions.toString());
        }
        if (!Endpoint.class.isAssignableFrom(sec.getEndpointClass()) && sec.getUserProperties().get("org.apache.tomcat.websocket.pojo.PojoEndpoint.methodMapping") == null) {
            try {
                PojoMethodMapping methodMapping = new PojoMethodMapping(sec.getEndpointClass(), sec.getDecoders(), sec.getPath(), sc.getInstanceManager(Thread.currentThread().getContextClassLoader()));
                if (methodMapping.getOnClose() != null || methodMapping.getOnOpen() != null || methodMapping.getOnError() != null || methodMapping.hasMessageHandlers()) {
                    sec.getUserProperties().put("org.apache.tomcat.websocket.pojo.PojoEndpoint.methodMapping", methodMapping);
                }
            }
            catch (DeploymentException e) {
                throw new ServletException(sm.getString("upgradeUtil.pojoMapFail", new Object[]{sec.getEndpointClass().getName()}), (Throwable)e);
            }
        }
        WsPerSessionServerEndpointConfig perSessionServerEndpointConfig = new WsPerSessionServerEndpointConfig(sec);
        WsHandshakeRequest wsRequest = new WsHandshakeRequest(req, pathParams);
        WsHandshakeResponse wsResponse = new WsHandshakeResponse();
        sec.getConfigurator().modifyHandshake((ServerEndpointConfig)perSessionServerEndpointConfig, (HandshakeRequest)wsRequest, (HandshakeResponse)wsResponse);
        wsRequest.finished();
        for (Map.Entry<String, List<String>> entry : wsResponse.getHeaders().entrySet()) {
            for (String headerValue : entry.getValue()) {
                resp.addHeader(entry.getKey(), headerValue);
            }
        }
        WsHttpUpgradeHandler wsHandler = (WsHttpUpgradeHandler)req.upgrade(WsHttpUpgradeHandler.class);
        wsHandler.preInit(perSessionServerEndpointConfig, sc, wsRequest, negotiatedExtensionsPhase2, subProtocol, transformation, pathParams, req.isSecure());
    }

    private static boolean validateKey(String key) {
        if (key == null) {
            return false;
        }
        if (key.length() != 24) {
            return false;
        }
        char[] keyChars = key.toCharArray();
        if (keyChars[22] != '=' || keyChars[23] != '=') {
            return false;
        }
        for (int i = 0; i < 22; ++i) {
            if (Base64.isInAlphabet((char)keyChars[i])) continue;
            return false;
        }
        return true;
    }

    private static List<Transformation> createTransformations(List<Extension> negotiatedExtensions) {
        TransformationFactory factory = TransformationFactory.getInstance();
        LinkedHashMap<String, List> extensionPreferences = new LinkedHashMap<String, List>();
        ArrayList<Transformation> result = new ArrayList<Transformation>(negotiatedExtensions.size());
        for (Extension extension : negotiatedExtensions) {
            extensionPreferences.computeIfAbsent(extension.getName(), k -> new ArrayList()).add(extension.getParameters());
        }
        for (Map.Entry entry : extensionPreferences.entrySet()) {
            Transformation transformation = factory.create((String)entry.getKey(), (List)entry.getValue(), true);
            if (transformation == null) continue;
            result.add(transformation);
        }
        return result;
    }

    private static void append(StringBuilder sb, Extension extension) {
        if (extension == null || extension.getName() == null || extension.getName().length() == 0) {
            return;
        }
        sb.append(extension.getName());
        for (Extension.Parameter p : extension.getParameters()) {
            sb.append(';');
            sb.append(p.getName());
            if (p.getValue() == null) continue;
            sb.append('=');
            sb.append(p.getValue());
        }
    }

    private static boolean headerContainsToken(HttpServletRequest req, String headerName, String target) {
        Enumeration headers = req.getHeaders(headerName);
        while (headers.hasMoreElements()) {
            String[] tokens;
            String header = (String)headers.nextElement();
            for (String token : tokens = header.split(",")) {
                if (!target.equalsIgnoreCase(token.trim())) continue;
                return true;
            }
        }
        return false;
    }

    private static List<String> getTokensFromHeader(HttpServletRequest req, String headerName) {
        ArrayList<String> result = new ArrayList<String>();
        Enumeration headers = req.getHeaders(headerName);
        while (headers.hasMoreElements()) {
            String[] tokens;
            String header = (String)headers.nextElement();
            for (String token : tokens = header.split(",")) {
                result.add(token.trim());
            }
        }
        return result;
    }

    private static String getWebSocketAccept(String key) {
        byte[] digest = ConcurrentMessageDigest.digestSHA1((byte[][])new byte[][]{key.getBytes(StandardCharsets.ISO_8859_1), WS_ACCEPT});
        return Base64.encodeBase64String((byte[])digest);
    }
}

