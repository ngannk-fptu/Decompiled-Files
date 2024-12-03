/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
 */
package org.springframework.web.socket.config;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

public class WebSocketMessageBrokerStats {
    private static final Log logger = LogFactory.getLog(WebSocketMessageBrokerStats.class);
    @Nullable
    private SubProtocolWebSocketHandler webSocketHandler;
    @Nullable
    private StompSubProtocolHandler stompSubProtocolHandler;
    @Nullable
    private StompBrokerRelayMessageHandler stompBrokerRelay;
    @Nullable
    private TaskExecutor inboundChannelExecutor;
    @Nullable
    private TaskExecutor outboundChannelExecutor;
    @Nullable
    private TaskScheduler sockJsTaskScheduler;
    @Nullable
    private ScheduledFuture<?> loggingTask;
    private long loggingPeriod = TimeUnit.MINUTES.toMillis(30L);

    public void setSubProtocolWebSocketHandler(SubProtocolWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        this.stompSubProtocolHandler = this.initStompSubProtocolHandler();
    }

    @Nullable
    private StompSubProtocolHandler initStompSubProtocolHandler() {
        if (this.webSocketHandler == null) {
            return null;
        }
        for (SubProtocolHandler handler : this.webSocketHandler.getProtocolHandlers()) {
            if (!(handler instanceof StompSubProtocolHandler)) continue;
            return (StompSubProtocolHandler)handler;
        }
        SubProtocolHandler defaultHandler = this.webSocketHandler.getDefaultProtocolHandler();
        if (defaultHandler instanceof StompSubProtocolHandler) {
            return (StompSubProtocolHandler)defaultHandler;
        }
        return null;
    }

    public void setStompBrokerRelay(StompBrokerRelayMessageHandler stompBrokerRelay) {
        this.stompBrokerRelay = stompBrokerRelay;
    }

    public void setInboundChannelExecutor(TaskExecutor inboundChannelExecutor) {
        this.inboundChannelExecutor = inboundChannelExecutor;
    }

    public void setOutboundChannelExecutor(TaskExecutor outboundChannelExecutor) {
        this.outboundChannelExecutor = outboundChannelExecutor;
    }

    public void setSockJsTaskScheduler(TaskScheduler sockJsTaskScheduler) {
        this.sockJsTaskScheduler = sockJsTaskScheduler;
        this.loggingTask = this.initLoggingTask(TimeUnit.MINUTES.toMillis(1L));
    }

    @Nullable
    private ScheduledFuture<?> initLoggingTask(long initialDelay) {
        if (this.sockJsTaskScheduler != null && this.loggingPeriod > 0L && logger.isInfoEnabled()) {
            return this.sockJsTaskScheduler.scheduleWithFixedDelay(() -> logger.info((Object)this.toString()), Instant.now().plusMillis(initialDelay), Duration.ofMillis(this.loggingPeriod));
        }
        return null;
    }

    public void setLoggingPeriod(long period) {
        if (this.loggingTask != null) {
            this.loggingTask.cancel(true);
        }
        this.loggingPeriod = period;
        this.loggingTask = this.initLoggingTask(0L);
    }

    public long getLoggingPeriod() {
        return this.loggingPeriod;
    }

    public String getWebSocketSessionStatsInfo() {
        return this.webSocketHandler != null ? this.webSocketHandler.getStatsInfo() : "null";
    }

    public String getStompSubProtocolStatsInfo() {
        return this.stompSubProtocolHandler != null ? this.stompSubProtocolHandler.getStatsInfo() : "null";
    }

    public String getStompBrokerRelayStatsInfo() {
        return this.stompBrokerRelay != null ? this.stompBrokerRelay.getStatsInfo() : "null";
    }

    public String getClientInboundExecutorStatsInfo() {
        return this.getExecutorStatsInfo((Executor)this.inboundChannelExecutor);
    }

    public String getClientOutboundExecutorStatsInfo() {
        return this.getExecutorStatsInfo((Executor)this.outboundChannelExecutor);
    }

    public String getSockJsTaskSchedulerStatsInfo() {
        if (this.sockJsTaskScheduler == null) {
            return "null";
        }
        if (this.sockJsTaskScheduler instanceof ThreadPoolTaskScheduler) {
            return this.getExecutorStatsInfo(((ThreadPoolTaskScheduler)this.sockJsTaskScheduler).getScheduledThreadPoolExecutor());
        }
        return "unknown";
    }

    private String getExecutorStatsInfo(@Nullable Executor executor) {
        String str;
        int indexOfPool;
        if (executor == null) {
            return "null";
        }
        if (executor instanceof ThreadPoolTaskExecutor) {
            executor = ((ThreadPoolTaskExecutor)executor).getThreadPoolExecutor();
        }
        if (executor instanceof ThreadPoolExecutor && (indexOfPool = (str = executor.toString()).indexOf("pool")) != -1) {
            return str.substring(indexOfPool, str.length() - 1);
        }
        return "unknown";
    }

    public String toString() {
        return "WebSocketSession[" + this.getWebSocketSessionStatsInfo() + "], stompSubProtocol[" + this.getStompSubProtocolStatsInfo() + "], stompBrokerRelay[" + this.getStompBrokerRelayStatsInfo() + "], inboundChannel[" + this.getClientInboundExecutorStatsInfo() + "], outboundChannel[" + this.getClientOutboundExecutorStatsInfo() + "], sockJsScheduler[" + this.getSockJsTaskSchedulerStatsInfo() + "]";
    }
}

