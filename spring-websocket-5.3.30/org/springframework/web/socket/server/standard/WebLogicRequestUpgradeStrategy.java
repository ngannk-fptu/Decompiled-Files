/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestWrapper
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.glassfish.tyrus.core.TyrusUpgradeResponse
 *  org.glassfish.tyrus.core.Utils
 *  org.glassfish.tyrus.spi.Connection
 *  org.glassfish.tyrus.spi.Connection$CloseListener
 *  org.glassfish.tyrus.spi.WebSocketEngine$UpgradeInfo
 *  org.glassfish.tyrus.spi.Writer
 *  org.springframework.beans.BeanWrapperImpl
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.web.socket.server.standard;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.glassfish.tyrus.core.TyrusUpgradeResponse;
import org.glassfish.tyrus.core.Utils;
import org.glassfish.tyrus.spi.Connection;
import org.glassfish.tyrus.spi.WebSocketEngine;
import org.glassfish.tyrus.spi.Writer;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.standard.AbstractTyrusRequestUpgradeStrategy;

public class WebLogicRequestUpgradeStrategy
extends AbstractTyrusRequestUpgradeStrategy {
    private static final TyrusMuxableWebSocketHelper webSocketHelper = new TyrusMuxableWebSocketHelper();
    private static final WebLogicServletWriterHelper servletWriterHelper = new WebLogicServletWriterHelper();
    private static final Connection.CloseListener noOpCloseListener = reason -> {};

    @Override
    protected void handleSuccess(HttpServletRequest request, HttpServletResponse response, WebSocketEngine.UpgradeInfo upgradeInfo, TyrusUpgradeResponse upgradeResponse) throws IOException, ServletException {
        response.setStatus(upgradeResponse.getStatus());
        upgradeResponse.getHeaders().forEach((key, value) -> response.addHeader(key, Utils.getHeaderFromList((List)value)));
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(-1L);
        Object nativeRequest = WebLogicRequestUpgradeStrategy.getNativeRequest((ServletRequest)request);
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(nativeRequest);
        Object httpSocket = beanWrapper.getPropertyValue("connection.connectionHandler.rawConnection");
        Object webSocket = WebLogicRequestUpgradeStrategy.webSocketHelper.newInstance(request, httpSocket);
        WebLogicRequestUpgradeStrategy.webSocketHelper.upgrade(webSocket, httpSocket, request.getServletContext());
        response.flushBuffer();
        boolean isProtected = request.getUserPrincipal() != null;
        Writer servletWriter = WebLogicRequestUpgradeStrategy.servletWriterHelper.newInstance(webSocket, isProtected);
        Connection connection = upgradeInfo.createConnection(servletWriter, noOpCloseListener);
        new BeanWrapperImpl(webSocket).setPropertyValue("connection", (Object)connection);
        new BeanWrapperImpl((Object)servletWriter).setPropertyValue("connection", (Object)connection);
        WebLogicRequestUpgradeStrategy.webSocketHelper.registerForReadEvent(webSocket);
    }

    private static Class<?> type(String className) throws ClassNotFoundException {
        return WebLogicRequestUpgradeStrategy.class.getClassLoader().loadClass(className);
    }

    private static Method method(String className, String method, Class<?> ... paramTypes) throws ClassNotFoundException, NoSuchMethodException {
        return WebLogicRequestUpgradeStrategy.type(className).getDeclaredMethod(method, paramTypes);
    }

    private static Object getNativeRequest(ServletRequest request) {
        while (request instanceof ServletRequestWrapper) {
            request = ((ServletRequestWrapper)request).getRequest();
        }
        return request;
    }

    private static class WebLogicServletWriterHelper {
        private static final Constructor<?> constructor;

        private WebLogicServletWriterHelper() {
        }

        private Writer newInstance(Object webSocket, boolean isProtected) {
            try {
                return (Writer)constructor.newInstance(webSocket, null, isProtected);
            }
            catch (Exception ex) {
                throw new HandshakeFailureException("Failed to create TyrusServletWriter", ex);
            }
        }

        static {
            try {
                Class writerType = WebLogicRequestUpgradeStrategy.type("weblogic.websocket.tyrus.TyrusServletWriter");
                Class listenerType = WebLogicRequestUpgradeStrategy.type("weblogic.websocket.tyrus.TyrusServletWriter$CloseListener");
                Class webSocketType = TyrusMuxableWebSocketHelper.type;
                constructor = writerType.getDeclaredConstructor(webSocketType, listenerType, Boolean.TYPE);
                ReflectionUtils.makeAccessible(constructor);
            }
            catch (Exception ex) {
                throw new IllegalStateException("No compatible WebSocket version found", ex);
            }
        }
    }

    private static class SubjectHelper {
        private final Method securityContextMethod;
        private final Method currentUserMethod;
        private final Method providerMethod;
        private final Method anonymousSubjectMethod;

        public SubjectHelper() {
            try {
                String className = "weblogic.servlet.internal.WebAppServletContext";
                this.securityContextMethod = WebLogicRequestUpgradeStrategy.method(className, "getSecurityContext", new Class[0]);
                className = "weblogic.servlet.security.internal.SecurityModule";
                this.currentUserMethod = WebLogicRequestUpgradeStrategy.method(className, "getCurrentUser", new Class[]{WebLogicRequestUpgradeStrategy.type("weblogic.servlet.security.internal.ServletSecurityContext"), HttpServletRequest.class});
                className = "weblogic.servlet.security.internal.WebAppSecurity";
                this.providerMethod = WebLogicRequestUpgradeStrategy.method(className, "getProvider", new Class[0]);
                this.anonymousSubjectMethod = this.providerMethod.getReturnType().getDeclaredMethod("getAnonymousSubject", new Class[0]);
            }
            catch (Exception ex) {
                throw new IllegalStateException("No compatible WebSocket version found", ex);
            }
        }

        public Object getSubject(HttpServletRequest request) {
            try {
                ServletContext servletContext = request.getServletContext();
                Object securityContext = this.securityContextMethod.invoke((Object)servletContext, new Object[0]);
                Object subject = this.currentUserMethod.invoke(null, securityContext, request);
                if (subject == null) {
                    Object securityProvider = this.providerMethod.invoke(null, new Object[0]);
                    subject = this.anonymousSubjectMethod.invoke(securityProvider, new Object[0]);
                }
                return subject;
            }
            catch (Exception ex) {
                throw new HandshakeFailureException("Failed to obtain SubjectHandle", ex);
            }
        }
    }

    private static class TyrusMuxableWebSocketHelper {
        private static final Class<?> type;
        private static final Constructor<?> constructor;
        private static final SubjectHelper subjectHelper;
        private static final Method upgradeMethod;
        private static final Method readEventMethod;

        private TyrusMuxableWebSocketHelper() {
        }

        private Object newInstance(HttpServletRequest request, @Nullable Object httpSocket) {
            try {
                Object[] args = new Object[]{httpSocket, null, subjectHelper.getSubject(request)};
                return constructor.newInstance(args);
            }
            catch (Exception ex) {
                throw new HandshakeFailureException("Failed to create TyrusMuxableWebSocket", ex);
            }
        }

        private void upgrade(Object webSocket, @Nullable Object httpSocket, ServletContext servletContext) {
            try {
                upgradeMethod.invoke(webSocket, httpSocket, servletContext);
            }
            catch (Exception ex) {
                throw new HandshakeFailureException("Failed to upgrade TyrusMuxableWebSocket", ex);
            }
        }

        private void registerForReadEvent(Object webSocket) {
            try {
                readEventMethod.invoke(webSocket, new Object[0]);
            }
            catch (Exception ex) {
                throw new HandshakeFailureException("Failed to register WebSocket for read event", ex);
            }
        }

        static {
            try {
                type = WebLogicRequestUpgradeStrategy.type("weblogic.websocket.tyrus.TyrusMuxableWebSocket");
                constructor = type.getDeclaredConstructor(WebLogicRequestUpgradeStrategy.type("weblogic.servlet.internal.MuxableSocketHTTP"), WebLogicRequestUpgradeStrategy.type("weblogic.websocket.tyrus.CoherenceServletFilterService"), WebLogicRequestUpgradeStrategy.type("weblogic.servlet.spi.SubjectHandle"));
                subjectHelper = new SubjectHelper();
                upgradeMethod = type.getMethod("upgrade", WebLogicRequestUpgradeStrategy.type("weblogic.socket.MuxableSocket"), ServletContext.class);
                readEventMethod = type.getMethod("registerForReadEvent", new Class[0]);
            }
            catch (Exception ex) {
                throw new IllegalStateException("No compatible WebSocket version found", ex);
            }
        }
    }
}

