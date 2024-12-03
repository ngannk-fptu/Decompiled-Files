/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.handler.HandlerChain;
import org.apache.axis.handlers.HandlerChainImpl;

public class HandlerInfoChainFactory
implements Serializable {
    protected List handlerInfos = new ArrayList();
    protected String[] _roles = null;

    public HandlerInfoChainFactory() {
    }

    public HandlerInfoChainFactory(List handlerInfos) {
        this.handlerInfos = handlerInfos;
    }

    public List getHandlerInfos() {
        return this.handlerInfos;
    }

    public HandlerChain createHandlerChain() {
        HandlerChainImpl hc = new HandlerChainImpl(this.handlerInfos);
        hc.setRoles(this.getRoles());
        return hc;
    }

    public String[] getRoles() {
        return this._roles;
    }

    public void setRoles(String[] roles) {
        this._roles = roles;
    }

    public void init(Map map) {
    }
}

