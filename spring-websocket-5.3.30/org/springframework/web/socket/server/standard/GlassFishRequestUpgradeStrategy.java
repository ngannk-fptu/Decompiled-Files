/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.glassfish.tyrus.core.TyrusUpgradeResponse
 *  org.glassfish.tyrus.core.Utils
 *  org.glassfish.tyrus.servlet.TyrusHttpUpgradeHandler
 *  org.glassfish.tyrus.spi.WebSocketEngine$UpgradeInfo
 *  org.glassfish.tyrus.spi.Writer
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.web.socket.server.standard;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.glassfish.tyrus.core.TyrusUpgradeResponse;
import org.glassfish.tyrus.core.Utils;
import org.glassfish.tyrus.servlet.TyrusHttpUpgradeHandler;
import org.glassfish.tyrus.spi.WebSocketEngine;
import org.glassfish.tyrus.spi.Writer;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.standard.AbstractTyrusRequestUpgradeStrategy;

public class GlassFishRequestUpgradeStrategy
extends AbstractTyrusRequestUpgradeStrategy {
    private static final Constructor<?> constructor;

    @Override
    protected void handleSuccess(HttpServletRequest request, HttpServletResponse response, WebSocketEngine.UpgradeInfo upgradeInfo, TyrusUpgradeResponse upgradeResponse) throws IOException, ServletException {
        TyrusHttpUpgradeHandler handler = (TyrusHttpUpgradeHandler)request.upgrade(TyrusHttpUpgradeHandler.class);
        Writer servletWriter = this.newServletWriter(handler);
        handler.preInit(upgradeInfo, servletWriter, request.getUserPrincipal() != null);
        response.setStatus(upgradeResponse.getStatus());
        upgradeResponse.getHeaders().forEach((key, value) -> response.addHeader(key, Utils.getHeaderFromList((List)value)));
        response.flushBuffer();
    }

    private Writer newServletWriter(TyrusHttpUpgradeHandler handler) {
        try {
            return (Writer)constructor.newInstance(handler);
        }
        catch (Exception ex) {
            throw new HandshakeFailureException("Failed to instantiate TyrusServletWriter", ex);
        }
    }

    static {
        try {
            ClassLoader classLoader = GlassFishRequestUpgradeStrategy.class.getClassLoader();
            Class<?> type = classLoader.loadClass("org.glassfish.tyrus.servlet.TyrusServletWriter");
            constructor = type.getDeclaredConstructor(TyrusHttpUpgradeHandler.class);
            ReflectionUtils.makeAccessible(constructor);
        }
        catch (Exception ex) {
            throw new IllegalStateException("No compatible Tyrus version found", ex);
        }
    }
}

