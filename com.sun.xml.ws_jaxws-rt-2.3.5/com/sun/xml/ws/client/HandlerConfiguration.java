/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.handler.Handler
 *  javax.xml.ws.handler.LogicalHandler
 *  javax.xml.ws.handler.soap.SOAPHandler
 */
package com.sun.xml.ws.client;

import com.sun.xml.ws.api.handler.MessageHandler;
import com.sun.xml.ws.handler.HandlerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.soap.SOAPHandler;

public class HandlerConfiguration {
    private final Set<String> roles;
    private final List<Handler> handlerChain;
    private final List<LogicalHandler> logicalHandlers;
    private final List<SOAPHandler> soapHandlers;
    private final List<MessageHandler> messageHandlers;
    private final Set<QName> handlerKnownHeaders;

    public HandlerConfiguration(Set<String> roles, List<Handler> handlerChain) {
        this.roles = roles;
        this.handlerChain = handlerChain;
        this.logicalHandlers = new ArrayList<LogicalHandler>();
        this.soapHandlers = new ArrayList<SOAPHandler>();
        this.messageHandlers = new ArrayList<MessageHandler>();
        HashSet<QName> modHandlerKnownHeaders = new HashSet<QName>();
        for (Handler handler : handlerChain) {
            Set<QName> headers;
            if (handler instanceof LogicalHandler) {
                this.logicalHandlers.add((LogicalHandler)handler);
                continue;
            }
            if (handler instanceof SOAPHandler) {
                this.soapHandlers.add((SOAPHandler)handler);
                headers = ((SOAPHandler)handler).getHeaders();
                if (headers == null) continue;
                modHandlerKnownHeaders.addAll(headers);
                continue;
            }
            if (handler instanceof MessageHandler) {
                this.messageHandlers.add((MessageHandler)handler);
                headers = ((MessageHandler)handler).getHeaders();
                if (headers == null) continue;
                modHandlerKnownHeaders.addAll(headers);
                continue;
            }
            throw new HandlerException("handler.not.valid.type", handler.getClass());
        }
        this.handlerKnownHeaders = Collections.unmodifiableSet(modHandlerKnownHeaders);
    }

    public HandlerConfiguration(Set<String> roles, HandlerConfiguration oldConfig) {
        this.roles = roles;
        this.handlerChain = oldConfig.handlerChain;
        this.logicalHandlers = oldConfig.logicalHandlers;
        this.soapHandlers = oldConfig.soapHandlers;
        this.messageHandlers = oldConfig.messageHandlers;
        this.handlerKnownHeaders = oldConfig.handlerKnownHeaders;
    }

    public Set<String> getRoles() {
        return this.roles;
    }

    public List<Handler> getHandlerChain() {
        if (this.handlerChain == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Handler>(this.handlerChain);
    }

    public List<LogicalHandler> getLogicalHandlers() {
        return this.logicalHandlers;
    }

    public List<SOAPHandler> getSoapHandlers() {
        return this.soapHandlers;
    }

    public List<MessageHandler> getMessageHandlers() {
        return this.messageHandlers;
    }

    public Set<QName> getHandlerKnownHeaders() {
        return this.handlerKnownHeaders;
    }
}

