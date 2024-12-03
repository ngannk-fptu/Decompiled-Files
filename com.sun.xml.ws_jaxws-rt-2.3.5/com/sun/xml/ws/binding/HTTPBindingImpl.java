/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.handler.Handler
 *  javax.xml.ws.handler.LogicalHandler
 *  javax.xml.ws.http.HTTPBinding
 */
package com.sun.xml.ws.binding;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.HandlerConfiguration;
import com.sun.xml.ws.resources.ClientMessages;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.http.HTTPBinding;

public class HTTPBindingImpl
extends BindingImpl
implements HTTPBinding {
    HTTPBindingImpl() {
        this(EMPTY_FEATURES);
    }

    HTTPBindingImpl(WebServiceFeature ... features) {
        super(BindingID.XML_HTTP, features);
    }

    public void setHandlerChain(List<Handler> chain) {
        for (Handler handler : chain) {
            if (handler instanceof LogicalHandler) continue;
            throw new WebServiceException(ClientMessages.NON_LOGICAL_HANDLER_SET(handler.getClass()));
        }
        this.setHandlerConfig(new HandlerConfiguration(Collections.emptySet(), chain));
    }
}

