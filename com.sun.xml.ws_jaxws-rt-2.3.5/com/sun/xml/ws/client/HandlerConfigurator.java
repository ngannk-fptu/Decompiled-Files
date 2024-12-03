/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.handler.Handler
 *  javax.xml.ws.handler.HandlerResolver
 *  javax.xml.ws.handler.PortInfo
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.handler.HandlerChainsModel;
import com.sun.xml.ws.util.HandlerAnnotationInfo;
import com.sun.xml.ws.util.HandlerAnnotationProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.soap.SOAPBinding;

abstract class HandlerConfigurator {
    HandlerConfigurator() {
    }

    abstract void configureHandlers(@NotNull WSPortInfo var1, @NotNull BindingImpl var2);

    abstract HandlerResolver getResolver();

    static final class AnnotationConfigurator
    extends HandlerConfigurator {
        private final HandlerChainsModel handlerModel;
        private final Map<WSPortInfo, HandlerAnnotationInfo> chainMap = new HashMap<WSPortInfo, HandlerAnnotationInfo>();
        private static final Logger logger = Logger.getLogger("com.sun.xml.ws.handler");

        AnnotationConfigurator(WSServiceDelegate delegate) {
            this.handlerModel = HandlerAnnotationProcessor.buildHandlerChainsModel(delegate.getServiceClass());
            assert (this.handlerModel != null);
        }

        @Override
        void configureHandlers(WSPortInfo port, BindingImpl binding) {
            HandlerAnnotationInfo chain = this.chainMap.get(port);
            if (chain == null) {
                this.logGetChain(port);
                chain = this.handlerModel.getHandlersForPortInfo(port);
                this.chainMap.put(port, chain);
            }
            if (binding instanceof SOAPBinding) {
                ((SOAPBinding)binding).setRoles(chain.getRoles());
            }
            this.logSetChain(port, chain);
            binding.setHandlerChain(chain.getHandlers());
        }

        @Override
        HandlerResolver getResolver() {
            return new HandlerResolver(){

                public List<Handler> getHandlerChain(PortInfo portInfo) {
                    return new ArrayList<Handler>(handlerModel.getHandlersForPortInfo(portInfo).getHandlers());
                }
            };
        }

        private void logSetChain(WSPortInfo info, HandlerAnnotationInfo chain) {
            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Setting chain of length {0} for port info", chain.getHandlers().size());
                this.logPortInfo(info, Level.FINER);
            }
        }

        private void logGetChain(WSPortInfo info) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("No handler chain found for port info:");
                this.logPortInfo(info, Level.FINE);
                logger.fine("Existing handler chains:");
                if (this.chainMap.isEmpty()) {
                    logger.fine("none");
                } else {
                    for (Map.Entry<WSPortInfo, HandlerAnnotationInfo> entry : this.chainMap.entrySet()) {
                        logger.log(Level.FINE, "{0} handlers for port info ", entry.getValue().getHandlers().size());
                        this.logPortInfo(entry.getKey(), Level.FINE);
                    }
                }
            }
        }

        private void logPortInfo(WSPortInfo info, Level level) {
            logger.log(level, "binding: {0}\nservice: {1}\nport: {2}", new Object[]{info.getBindingID(), info.getServiceName(), info.getPortName()});
        }
    }

    static final class HandlerResolverImpl
    extends HandlerConfigurator {
        @Nullable
        private final HandlerResolver resolver;

        public HandlerResolverImpl(HandlerResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        void configureHandlers(@NotNull WSPortInfo port, @NotNull BindingImpl binding) {
            if (this.resolver != null) {
                binding.setHandlerChain(this.resolver.getHandlerChain((PortInfo)port));
            }
        }

        @Override
        HandlerResolver getResolver() {
            return this.resolver;
        }
    }
}

