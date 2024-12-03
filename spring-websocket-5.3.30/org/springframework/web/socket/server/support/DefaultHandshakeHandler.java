/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.socket.server.support;

import javax.servlet.ServletContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;

public class DefaultHandshakeHandler
extends AbstractHandshakeHandler
implements ServletContextAware {
    public DefaultHandshakeHandler() {
    }

    public DefaultHandshakeHandler(RequestUpgradeStrategy requestUpgradeStrategy) {
        super(requestUpgradeStrategy);
    }

    public void setServletContext(ServletContext servletContext) {
        RequestUpgradeStrategy strategy = this.getRequestUpgradeStrategy();
        if (strategy instanceof ServletContextAware) {
            ((ServletContextAware)strategy).setServletContext(servletContext);
        }
    }
}

