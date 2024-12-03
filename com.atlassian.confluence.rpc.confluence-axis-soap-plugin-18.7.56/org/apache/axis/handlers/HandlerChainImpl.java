/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class HandlerChainImpl
extends ArrayList
implements HandlerChain {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$HandlerChainImpl == null ? (class$org$apache$axis$handlers$HandlerChainImpl = HandlerChainImpl.class$("org.apache.axis.handlers.HandlerChainImpl")) : class$org$apache$axis$handlers$HandlerChainImpl).getName());
    public static final String JAXRPC_METHOD_INFO = "jaxrpc.method.info";
    private String[] _roles;
    private int falseIndex = -1;
    protected List handlerInfos = new ArrayList();
    static /* synthetic */ Class class$org$apache$axis$handlers$HandlerChainImpl;

    public String[] getRoles() {
        return this._roles;
    }

    public void setRoles(String[] roles) {
        if (roles != null) {
            this._roles = (String[])roles.clone();
        }
    }

    public void init(Map map) {
    }

    public HandlerChainImpl() {
    }

    public HandlerChainImpl(List handlerInfos) {
        this.handlerInfos = handlerInfos;
        for (int i = 0; i < handlerInfos.size(); ++i) {
            this.add(this.newHandler(this.getHandlerInfo(i)));
        }
    }

    public void addNewHandler(String className, Map config) {
        try {
            HandlerInfo handlerInfo = new HandlerInfo(ClassUtils.forName(className), config, null);
            this.handlerInfos.add(handlerInfo);
            this.add(this.newHandler(handlerInfo));
        }
        catch (Exception ex) {
            String messageText = Messages.getMessage("NoJAXRPCHandler00", className);
            throw new JAXRPCException(messageText, ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean handleFault(javax.xml.rpc.handler.MessageContext _context) {
        SOAPMessageContext context = (SOAPMessageContext)_context;
        this.preInvoke(context);
        try {
            int endIdx = this.size() - 1;
            if (this.falseIndex != -1) {
                endIdx = this.falseIndex;
            }
            for (int i = endIdx; i >= 0; --i) {
                if (this.getHandlerInstance(i).handleFault(context)) continue;
                boolean bl = false;
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.postInvoke(context);
        }
    }

    public ArrayList getMessageInfo(SOAPMessage message) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            if (message == null || message.getSOAPPart() == null) {
                return list;
            }
            SOAPEnvelope env = message.getSOAPPart().getEnvelope();
            SOAPBody body = env.getBody();
            Iterator it = body.getChildElements();
            SOAPElement operation = (SOAPElement)it.next();
            list.add(operation.getElementName().toString());
            Iterator i = operation.getChildElements();
            while (i.hasNext()) {
                SOAPElement elt = (SOAPElement)i.next();
                list.add(elt.getElementName().toString());
            }
        }
        catch (Exception e) {
            log.debug((Object)"Exception in getMessageInfo : ", (Throwable)e);
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean handleRequest(javax.xml.rpc.handler.MessageContext _context) {
        MessageContext actx = (MessageContext)_context;
        actx.setRoles(this.getRoles());
        SOAPMessageContext context = (SOAPMessageContext)_context;
        this.preInvoke(context);
        try {
            for (int i = 0; i < this.size(); ++i) {
                Handler currentHandler = this.getHandlerInstance(i);
                try {
                    if (currentHandler.handleRequest(context)) continue;
                    this.falseIndex = i;
                    boolean bl = false;
                    return bl;
                }
                catch (SOAPFaultException sfe) {
                    this.falseIndex = i;
                    throw sfe;
                }
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.postInvoke(context);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean handleResponse(javax.xml.rpc.handler.MessageContext context) {
        SOAPMessageContext scontext = (SOAPMessageContext)context;
        this.preInvoke(scontext);
        try {
            int endIdx = this.size() - 1;
            if (this.falseIndex != -1) {
                endIdx = this.falseIndex;
            }
            for (int i = endIdx; i >= 0; --i) {
                if (this.getHandlerInstance(i).handleResponse(context)) continue;
                boolean bl = false;
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.postInvoke(scontext);
        }
    }

    private void preInvoke(SOAPMessageContext msgContext) {
        try {
            SOAPMessage message = msgContext.getMessage();
            if (message != null && message.getSOAPPart() != null) {
                message.getSOAPPart().getEnvelope();
            }
            msgContext.setProperty("axis.form.optimization", Boolean.FALSE);
            msgContext.setProperty(JAXRPC_METHOD_INFO, this.getMessageInfo(message));
        }
        catch (Exception e) {
            log.debug((Object)"Exception in preInvoke : ", (Throwable)e);
            throw new RuntimeException("Exception in preInvoke : " + e.toString());
        }
    }

    private void postInvoke(SOAPMessageContext msgContext) {
        Boolean propFormOptimization = (Boolean)msgContext.getProperty("axis.form.optimization");
        if (propFormOptimization != null && !propFormOptimization.booleanValue()) {
            msgContext.setProperty("axis.form.optimization", Boolean.TRUE);
            SOAPMessage message = msgContext.getMessage();
            ArrayList oldList = (ArrayList)msgContext.getProperty(JAXRPC_METHOD_INFO);
            if (oldList != null && !Arrays.equals(oldList.toArray(), this.getMessageInfo(message).toArray())) {
                throw new RuntimeException(Messages.getMessage("invocationArgumentsModified00"));
            }
            try {
                if (message != null) {
                    message.saveChanges();
                }
            }
            catch (SOAPException e) {
                log.debug((Object)"Exception in postInvoke : ", (Throwable)e);
                throw new RuntimeException("Exception in postInvoke : " + e.toString());
            }
        }
    }

    public void destroy() {
        int endIdx = this.size() - 1;
        if (this.falseIndex != -1) {
            endIdx = this.falseIndex;
        }
        for (int i = endIdx; i >= 0; --i) {
            this.getHandlerInstance(i).destroy();
        }
        this.falseIndex = -1;
        this.clear();
    }

    private Handler getHandlerInstance(int index) {
        return (Handler)this.get(index);
    }

    private HandlerInfo getHandlerInfo(int index) {
        return (HandlerInfo)this.handlerInfos.get(index);
    }

    private Handler newHandler(HandlerInfo handlerInfo) {
        try {
            Handler handler = (Handler)handlerInfo.getHandlerClass().newInstance();
            handler.init(handlerInfo);
            return handler;
        }
        catch (Exception ex) {
            String messageText = Messages.getMessage("NoJAXRPCHandler00", handlerInfo.getHandlerClass().toString());
            throw new JAXRPCException(messageText, ex);
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

