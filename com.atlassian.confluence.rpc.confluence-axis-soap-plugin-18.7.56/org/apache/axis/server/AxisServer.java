/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.server;

import java.util.Map;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServerFactory;
import org.apache.axis.server.DefaultAxisServerFactory;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class AxisServer
extends AxisEngine {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$server$AxisServer == null ? (class$org$apache$axis$server$AxisServer = AxisServer.class$("org.apache.axis.server.AxisServer")) : class$org$apache$axis$server$AxisServer).getName());
    private static Log tlog = LogFactory.getLog("org.apache.axis.TIME");
    private static AxisServerFactory factory = null;
    private AxisEngine clientEngine;
    private boolean running = true;
    static /* synthetic */ Class class$org$apache$axis$server$AxisServer;
    static /* synthetic */ Class class$org$apache$axis$server$AxisServerFactory;

    public static AxisServer getServer(Map environment) throws AxisFault {
        if (factory == null) {
            String factoryClassName = AxisProperties.getProperty("axis.ServerFactory");
            if (factoryClassName != null) {
                try {
                    Class factoryClass = ClassUtils.forName(factoryClassName);
                    if ((class$org$apache$axis$server$AxisServerFactory == null ? (class$org$apache$axis$server$AxisServerFactory = AxisServer.class$("org.apache.axis.server.AxisServerFactory")) : class$org$apache$axis$server$AxisServerFactory).isAssignableFrom(factoryClass)) {
                        factory = (AxisServerFactory)factoryClass.newInstance();
                    }
                }
                catch (Exception e) {
                    log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                }
            }
            if (factory == null) {
                factory = new DefaultAxisServerFactory();
            }
        }
        return factory.getServer(environment);
    }

    public AxisServer() {
        this(EngineConfigurationFactoryFinder.newFactory().getServerEngineConfig());
    }

    public AxisServer(EngineConfiguration config) {
        super(config);
        this.setShouldSaveConfig(true);
    }

    public boolean isRunning() {
        return this.running;
    }

    public void start() {
        this.init();
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public synchronized AxisEngine getClientEngine() {
        if (this.clientEngine == null) {
            this.clientEngine = new AxisClient();
        }
        return this.clientEngine;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        block33: {
            long t0 = 0L;
            long t1 = 0L;
            long t2 = 0L;
            long t3 = 0L;
            long t4 = 0L;
            long t5 = 0L;
            if (tlog.isDebugEnabled()) {
                t0 = System.currentTimeMillis();
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)"Enter: AxisServer::invoke");
            }
            if (!this.isRunning()) {
                throw new AxisFault("Server.disabled", Messages.getMessage("serverDisabled00"), null, null);
            }
            String hName = null;
            Handler h = null;
            MessageContext previousContext = AxisServer.getCurrentMessageContext();
            try {
                AxisServer.setCurrentMessageContext(msgContext);
                hName = msgContext.getStrProp("engine.handler");
                if (hName != null) {
                    h = this.getHandler(hName);
                    if (h == null) {
                        ClassLoader cl = msgContext.getClassLoader();
                        try {
                            log.debug((Object)Messages.getMessage("tryingLoad00", hName));
                            Class cls = ClassUtils.forName(hName, true, cl);
                            h = (Handler)cls.newInstance();
                        }
                        catch (Exception e) {
                            h = null;
                        }
                    }
                    if (tlog.isDebugEnabled()) {
                        t1 = System.currentTimeMillis();
                    }
                    if (h == null) {
                        throw new AxisFault("Server.error", Messages.getMessage("noHandler00", hName), null, null);
                    }
                    h.invoke(msgContext);
                    if (tlog.isDebugEnabled()) {
                        t2 = System.currentTimeMillis();
                        tlog.debug((Object)("AxisServer.invoke " + hName + " invoke=" + (t2 - t1) + " pre=" + (t1 - t0)));
                    }
                    break block33;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("defaultLogic00"));
                }
                hName = msgContext.getTransportName();
                SimpleTargetedChain transportChain = null;
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("transport01", "AxisServer.invoke", hName));
                }
                if (tlog.isDebugEnabled()) {
                    t1 = System.currentTimeMillis();
                }
                if (hName != null && (h = this.getTransport(hName)) != null && h instanceof SimpleTargetedChain && (h = (transportChain = (SimpleTargetedChain)h).getRequestHandler()) != null) {
                    h.invoke(msgContext);
                }
                if (tlog.isDebugEnabled()) {
                    t2 = System.currentTimeMillis();
                }
                if ((h = this.getGlobalRequest()) != null) {
                    h.invoke(msgContext);
                }
                if ((h = msgContext.getService()) == null) {
                    Message rm = msgContext.getRequestMessage();
                    rm.getSOAPEnvelope().getFirstBody();
                    h = msgContext.getService();
                    if (h == null) {
                        throw new AxisFault("Server.NoService", Messages.getMessage("noService05", "" + msgContext.getTargetService()), null, null);
                    }
                }
                if (tlog.isDebugEnabled()) {
                    t3 = System.currentTimeMillis();
                }
                this.initSOAPConstants(msgContext);
                try {
                    h.invoke(msgContext);
                }
                catch (AxisFault ae) {
                    h = this.getGlobalRequest();
                    if (h != null) {
                        h.onFault(msgContext);
                    }
                    throw ae;
                }
                if (tlog.isDebugEnabled()) {
                    t4 = System.currentTimeMillis();
                }
                if ((h = this.getGlobalResponse()) != null) {
                    h.invoke(msgContext);
                }
                if (transportChain != null && (h = transportChain.getResponseHandler()) != null) {
                    h.invoke(msgContext);
                }
                if (tlog.isDebugEnabled()) {
                    t5 = System.currentTimeMillis();
                    tlog.debug((Object)("AxisServer.invoke2  preTr=" + (t1 - t0) + " tr=" + (t2 - t1) + " preInvoke=" + (t3 - t2) + " invoke=" + (t4 - t3) + " postInvoke=" + (t5 - t4) + " " + msgContext.getTargetService() + "." + (msgContext.getOperation() == null ? "" : msgContext.getOperation().getName())));
                }
            }
            catch (AxisFault e) {
                throw e;
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            finally {
                AxisServer.setCurrentMessageContext(previousContext);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: AxisServer::invoke");
        }
    }

    private void initSOAPConstants(MessageContext msgContext) throws AxisFault {
        Message msg = msgContext.getRequestMessage();
        if (msg == null) {
            return;
        }
        SOAPEnvelope env = msg.getSOAPEnvelope();
        if (env == null) {
            return;
        }
        SOAPConstants constants = env.getSOAPConstants();
        if (constants == null) {
            return;
        }
        msgContext.setSOAPConstants(constants);
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        block21: {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Enter: AxisServer::generateWSDL");
            }
            if (!this.isRunning()) {
                throw new AxisFault("Server.disabled", Messages.getMessage("serverDisabled00"), null, null);
            }
            String hName = null;
            Handler h = null;
            MessageContext previousContext = AxisServer.getCurrentMessageContext();
            try {
                AxisServer.setCurrentMessageContext(msgContext);
                hName = msgContext.getStrProp("engine.handler");
                if (hName != null) {
                    h = this.getHandler(hName);
                    if (h == null) {
                        ClassLoader cl = msgContext.getClassLoader();
                        try {
                            log.debug((Object)Messages.getMessage("tryingLoad00", hName));
                            Class cls = ClassUtils.forName(hName, true, cl);
                            h = (Handler)cls.newInstance();
                        }
                        catch (Exception e) {
                            throw new AxisFault("Server.error", Messages.getMessage("noHandler00", hName), null, null);
                        }
                    }
                    h.generateWSDL(msgContext);
                    break block21;
                }
                log.debug((Object)Messages.getMessage("defaultLogic00"));
                hName = msgContext.getTransportName();
                SimpleTargetedChain transportChain = null;
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("transport01", "AxisServer.generateWSDL", hName));
                }
                if (hName != null && (h = this.getTransport(hName)) != null && h instanceof SimpleTargetedChain && (h = (transportChain = (SimpleTargetedChain)h).getRequestHandler()) != null) {
                    h.generateWSDL(msgContext);
                }
                if ((h = this.getGlobalRequest()) != null) {
                    h.generateWSDL(msgContext);
                }
                if ((h = msgContext.getService()) == null) {
                    Message rm = msgContext.getRequestMessage();
                    if (rm != null) {
                        rm.getSOAPEnvelope().getFirstBody();
                        h = msgContext.getService();
                    }
                    if (h == null) {
                        throw new AxisFault(Constants.QNAME_NO_SERVICE_FAULT_CODE, Messages.getMessage("noService05", "" + msgContext.getTargetService()), null, null);
                    }
                }
                h.generateWSDL(msgContext);
                h = this.getGlobalResponse();
                if (h != null) {
                    h.generateWSDL(msgContext);
                }
                if (transportChain != null && (h = transportChain.getResponseHandler()) != null) {
                    h.generateWSDL(msgContext);
                }
            }
            catch (AxisFault e) {
                throw e;
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            finally {
                AxisServer.setCurrentMessageContext(previousContext);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: AxisServer::generateWSDL");
        }
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

