/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleChain;
import org.apache.axis.TargetedChain;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;

public class SimpleTargetedChain
extends SimpleChain
implements TargetedChain {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$SimpleTargetedChain == null ? (class$org$apache$axis$SimpleTargetedChain = SimpleTargetedChain.class$("org.apache.axis.SimpleTargetedChain")) : class$org$apache$axis$SimpleTargetedChain).getName());
    protected Handler requestHandler;
    protected Handler pivotHandler;
    protected Handler responseHandler;
    static /* synthetic */ Class class$org$apache$axis$SimpleTargetedChain;

    public SimpleTargetedChain() {
    }

    public SimpleTargetedChain(Handler handler) {
        this.pivotHandler = handler;
        if (this.pivotHandler != null) {
            this.addHandler(this.pivotHandler);
            this.addHandler(new PivotIndicator());
        }
    }

    public SimpleTargetedChain(Handler reqHandler, Handler pivHandler, Handler respHandler) {
        this.init(reqHandler, null, pivHandler, null, respHandler);
    }

    protected void init(Handler reqHandler, Handler specialReqHandler, Handler pivHandler, Handler specialRespHandler, Handler respHandler) {
        this.requestHandler = reqHandler;
        if (this.requestHandler != null) {
            this.addHandler(this.requestHandler);
        }
        if (specialReqHandler != null) {
            this.addHandler(specialReqHandler);
        }
        this.pivotHandler = pivHandler;
        if (this.pivotHandler != null) {
            this.addHandler(this.pivotHandler);
            this.addHandler(new PivotIndicator());
        }
        if (specialRespHandler != null) {
            this.addHandler(specialRespHandler);
        }
        this.responseHandler = respHandler;
        if (this.responseHandler != null) {
            this.addHandler(this.responseHandler);
        }
    }

    public Handler getRequestHandler() {
        return this.requestHandler;
    }

    public Handler getPivotHandler() {
        return this.pivotHandler;
    }

    public Handler getResponseHandler() {
        return this.responseHandler;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private class PivotIndicator
    extends BasicHandler {
        public void invoke(MessageContext msgContext) throws AxisFault {
            msgContext.setPastPivot(true);
        }
    }
}

