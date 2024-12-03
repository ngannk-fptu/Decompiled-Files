/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.sockjs.transport.TransportHandler;
import org.springframework.web.socket.sockjs.transport.TransportHandlingSockJsService;
import org.springframework.web.socket.sockjs.transport.handler.EventSourceTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.HtmlFileTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.XhrPollingTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.XhrReceivingTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.XhrStreamingTransportHandler;

public class DefaultSockJsService
extends TransportHandlingSockJsService
implements ServletContextAware {
    public DefaultSockJsService(TaskScheduler scheduler) {
        this(scheduler, DefaultSockJsService.getDefaultTransportHandlers(null));
    }

    public DefaultSockJsService(TaskScheduler scheduler, TransportHandler ... handlerOverrides) {
        this(scheduler, Arrays.asList(handlerOverrides));
    }

    public DefaultSockJsService(TaskScheduler scheduler, Collection<TransportHandler> handlerOverrides) {
        super(scheduler, DefaultSockJsService.getDefaultTransportHandlers(handlerOverrides));
    }

    private static Set<TransportHandler> getDefaultTransportHandlers(@Nullable Collection<TransportHandler> overrides) {
        LinkedHashSet<TransportHandler> result;
        block3: {
            result = new LinkedHashSet<TransportHandler>(8);
            result.add(new XhrPollingTransportHandler());
            result.add(new XhrReceivingTransportHandler());
            result.add(new XhrStreamingTransportHandler());
            result.add(new EventSourceTransportHandler());
            result.add(new HtmlFileTransportHandler());
            try {
                result.add(new WebSocketTransportHandler(new DefaultHandshakeHandler()));
            }
            catch (Exception ex) {
                Log logger = LogFactory.getLog(DefaultSockJsService.class);
                if (!logger.isWarnEnabled()) break block3;
                logger.warn((Object)"Failed to create a default WebSocketTransportHandler", (Throwable)ex);
            }
        }
        if (overrides != null) {
            result.addAll(overrides);
        }
        return result;
    }

    public void setServletContext(ServletContext servletContext) {
        for (TransportHandler handler : this.getTransportHandlers().values()) {
            if (!(handler instanceof ServletContextAware)) continue;
            ((ServletContextAware)handler).setServletContext(servletContext);
        }
    }
}

