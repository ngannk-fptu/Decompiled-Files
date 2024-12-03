/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.client;

import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerRegistry;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Service;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.handlers.soap.MustUnderstandChecker;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class AxisClient
extends AxisEngine {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$client$AxisClient == null ? (class$org$apache$axis$client$AxisClient = AxisClient.class$("org.apache.axis.client.AxisClient")) : class$org$apache$axis$client$AxisClient).getName());
    MustUnderstandChecker checker = new MustUnderstandChecker(null);
    static /* synthetic */ Class class$org$apache$axis$client$AxisClient;

    public AxisClient(EngineConfiguration config) {
        super(config);
    }

    public AxisClient() {
        this(EngineConfigurationFactoryFinder.newFactory().getClientEngineConfig());
    }

    public AxisEngine getClientEngine() {
        return this;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: AxisClient::invoke");
        }
        String hName = null;
        Handler h = null;
        HandlerChain handlerImpl = null;
        MessageContext previousContext = AxisClient.getCurrentMessageContext();
        try {
            block28: {
                try {
                    AxisClient.setCurrentMessageContext(msgContext);
                    hName = msgContext.getStrProp("engine.handler");
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("EngineHandler: " + hName));
                    }
                    if (hName != null) {
                        h = this.getHandler(hName);
                        if (h != null) {
                            h.invoke(msgContext);
                            break block28;
                        }
                        throw new AxisFault("Client.error", Messages.getMessage("noHandler00", hName), null, null);
                    }
                    SOAPService service = null;
                    msgContext.setPastPivot(false);
                    service = msgContext.getService();
                    if (service != null && (h = service.getRequestHandler()) != null) {
                        h.invoke(msgContext);
                    }
                    if ((h = this.getGlobalRequest()) != null) {
                        h.invoke(msgContext);
                    }
                    if ((handlerImpl = this.getJAXRPChandlerChain(msgContext)) != null) {
                        try {
                            if (!handlerImpl.handleRequest(msgContext)) {
                                msgContext.setPastPivot(true);
                            }
                        }
                        catch (RuntimeException re) {
                            handlerImpl.destroy();
                            throw re;
                        }
                    }
                    if (!msgContext.getPastPivot()) {
                        hName = msgContext.getTransportName();
                        if (hName != null && (h = this.getTransport(hName)) != null) {
                            h.invoke(msgContext);
                        } else {
                            throw new AxisFault(Messages.getMessage("noTransport00", hName));
                        }
                    }
                    msgContext.setPastPivot(true);
                    if (msgContext.isPropertyTrue("axis.one.way")) break block28;
                    if (handlerImpl != null && !msgContext.isPropertyTrue("axis.one.way")) {
                        try {
                            handlerImpl.handleResponse(msgContext);
                        }
                        catch (RuntimeException ex) {
                            handlerImpl.destroy();
                            throw ex;
                        }
                    }
                    if ((h = this.getGlobalResponse()) != null) {
                        h.invoke(msgContext);
                    }
                    if (service != null && (h = service.getResponseHandler()) != null) {
                        h.invoke(msgContext);
                    }
                    if (!msgContext.isPropertyTrue("call.CheckMustUnderstand", true)) break block28;
                    this.checker.invoke(msgContext);
                }
                catch (Exception e) {
                    if (e instanceof AxisFault) {
                        throw (AxisFault)e;
                    }
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    throw AxisFault.makeFault(e);
                }
            }
            Object var9_11 = null;
            if (handlerImpl != null) {
                handlerImpl.destroy();
            }
        }
        catch (Throwable throwable) {
            Object var9_12 = null;
            if (handlerImpl != null) {
                handlerImpl.destroy();
            }
            AxisClient.setCurrentMessageContext(previousContext);
            throw throwable;
        }
        AxisClient.setCurrentMessageContext(previousContext);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: AxisClient::invoke");
        }
    }

    protected HandlerChain getJAXRPChandlerChain(MessageContext context) {
        SOAPService soapService;
        List chain = null;
        HandlerInfoChainFactory hiChainFactory = null;
        boolean clientSpecified = false;
        Service service = (Service)context.getProperty("wsdl.service");
        if (service == null) {
            return null;
        }
        QName portName = (QName)context.getProperty("wsdl.portName");
        if (portName == null) {
            return null;
        }
        HandlerRegistry registry = service.getHandlerRegistry();
        if (registry != null && (chain = registry.getHandlerChain(portName)) != null && !chain.isEmpty()) {
            hiChainFactory = new HandlerInfoChainFactory(chain);
            clientSpecified = true;
        }
        if (!clientSpecified && (soapService = context.getService()) != null) {
            hiChainFactory = (HandlerInfoChainFactory)soapService.getOption("handlerInfoChain");
        }
        if (hiChainFactory == null) {
            return null;
        }
        return hiChainFactory.createHandlerChain();
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

