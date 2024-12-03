/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

public class WebSocketTransportRegistration {
    @Nullable
    private Integer messageSizeLimit;
    @Nullable
    private Integer sendTimeLimit;
    @Nullable
    private Integer sendBufferSizeLimit;
    @Nullable
    private Integer timeToFirstMessage;
    private final List<WebSocketHandlerDecoratorFactory> decoratorFactories = new ArrayList<WebSocketHandlerDecoratorFactory>(2);

    public WebSocketTransportRegistration setMessageSizeLimit(int messageSizeLimit) {
        this.messageSizeLimit = messageSizeLimit;
        return this;
    }

    @Nullable
    protected Integer getMessageSizeLimit() {
        return this.messageSizeLimit;
    }

    public WebSocketTransportRegistration setSendTimeLimit(int timeLimit) {
        this.sendTimeLimit = timeLimit;
        return this;
    }

    @Nullable
    protected Integer getSendTimeLimit() {
        return this.sendTimeLimit;
    }

    public WebSocketTransportRegistration setSendBufferSizeLimit(int sendBufferSizeLimit) {
        this.sendBufferSizeLimit = sendBufferSizeLimit;
        return this;
    }

    @Nullable
    protected Integer getSendBufferSizeLimit() {
        return this.sendBufferSizeLimit;
    }

    public WebSocketTransportRegistration setTimeToFirstMessage(int timeToFirstMessage) {
        this.timeToFirstMessage = timeToFirstMessage;
        return this;
    }

    @Nullable
    protected Integer getTimeToFirstMessage() {
        return this.timeToFirstMessage;
    }

    public WebSocketTransportRegistration setDecoratorFactories(WebSocketHandlerDecoratorFactory ... factories) {
        this.decoratorFactories.addAll(Arrays.asList(factories));
        return this;
    }

    public WebSocketTransportRegistration addDecoratorFactory(WebSocketHandlerDecoratorFactory factory) {
        this.decoratorFactories.add(factory);
        return this;
    }

    protected List<WebSocketHandlerDecoratorFactory> getDecoratorFactories() {
        return this.decoratorFactories;
    }
}

