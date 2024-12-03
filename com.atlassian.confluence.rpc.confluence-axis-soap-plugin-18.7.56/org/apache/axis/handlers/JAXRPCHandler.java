/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.util.Map;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.HandlerChainImpl;
import org.apache.commons.logging.Log;

public class JAXRPCHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$JAXRPCHandler == null ? (class$org$apache$axis$handlers$JAXRPCHandler = JAXRPCHandler.class$("org.apache.axis.handlers.JAXRPCHandler")) : class$org$apache$axis$handlers$JAXRPCHandler).getName());
    protected HandlerChainImpl impl = new HandlerChainImpl();
    static /* synthetic */ Class class$org$apache$axis$handlers$JAXRPCHandler;

    public void init() {
        super.init();
        String className = (String)this.getOption("className");
        if (className != null) {
            this.addNewHandler(className, this.getOptions());
        }
    }

    public void addNewHandler(String className, Map options) {
        this.impl.addNewHandler(className, options);
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: JAXRPCHandler::enter invoke");
        if (!msgContext.getPastPivot()) {
            this.impl.handleRequest(msgContext);
        } else {
            this.impl.handleResponse(msgContext);
        }
        log.debug((Object)"Enter: JAXRPCHandler::exit invoke");
    }

    public void onFault(MessageContext msgContext) {
        this.impl.handleFault(msgContext);
    }

    public void cleanup() {
        this.impl.destroy();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

