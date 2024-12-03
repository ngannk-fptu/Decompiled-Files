/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerInfo;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDJAXRPCHandlerInfo;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDJAXRPCHandlerInfoChain
extends WSDDHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$deployment$wsdd$WSDDJAXRPCHandlerInfoChain == null ? (class$org$apache$axis$deployment$wsdd$WSDDJAXRPCHandlerInfoChain = WSDDJAXRPCHandlerInfoChain.class$("org.apache.axis.deployment.wsdd.WSDDJAXRPCHandlerInfoChain")) : class$org$apache$axis$deployment$wsdd$WSDDJAXRPCHandlerInfoChain).getName());
    private ArrayList _hiList;
    private HandlerInfoChainFactory _hiChainFactory;
    private String[] _roles;
    static /* synthetic */ Class class$org$apache$axis$deployment$wsdd$WSDDJAXRPCHandlerInfoChain;

    public WSDDJAXRPCHandlerInfoChain() {
    }

    public WSDDJAXRPCHandlerInfoChain(Element e) throws WSDDException {
        super(e);
        ArrayList<HandlerInfo> infoList = new ArrayList<HandlerInfo>();
        this._hiList = new ArrayList();
        Element[] elements = this.getChildElements(e, "handlerInfo");
        if (elements.length != 0) {
            for (int i = 0; i < elements.length; ++i) {
                WSDDJAXRPCHandlerInfo handlerInfo = new WSDDJAXRPCHandlerInfo(elements[i]);
                this._hiList.add(handlerInfo);
                String handlerClassName = handlerInfo.getHandlerClassName();
                Class handlerClass = null;
                try {
                    handlerClass = ClassUtils.forName(handlerClassName);
                }
                catch (ClassNotFoundException cnf) {
                    log.error((Object)Messages.getMessage("handlerInfoChainNoClass00", handlerClassName), (Throwable)cnf);
                }
                Map handlerMap = handlerInfo.getHandlerMap();
                QName[] headers = handlerInfo.getHeaders();
                if (handlerClass == null) continue;
                HandlerInfo hi = new HandlerInfo(handlerClass, handlerMap, headers);
                infoList.add(hi);
            }
        }
        this._hiChainFactory = new HandlerInfoChainFactory(infoList);
        elements = this.getChildElements(e, "role");
        if (elements.length != 0) {
            ArrayList<String> roleList = new ArrayList<String>();
            for (int i = 0; i < elements.length; ++i) {
                String role = elements[i].getAttribute("soapActorName");
                roleList.add(role);
            }
            this._roles = new String[roleList.size()];
            this._roles = roleList.toArray(this._roles);
            this._hiChainFactory.setRoles(this._roles);
        }
    }

    public HandlerInfoChainFactory getHandlerChainFactory() {
        return this._hiChainFactory;
    }

    public void setHandlerChainFactory(HandlerInfoChainFactory handlerInfoChainFactory) {
        this._hiChainFactory = handlerInfoChainFactory;
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_JAXRPC_HANDLERINFOCHAIN;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        context.startElement(QNAME_JAXRPC_HANDLERINFOCHAIN, null);
        ArrayList his = this._hiList;
        Iterator iter = his.iterator();
        while (iter.hasNext()) {
            WSDDJAXRPCHandlerInfo hi = (WSDDJAXRPCHandlerInfo)iter.next();
            hi.writeToContext(context);
        }
        if (this._roles != null) {
            for (int i = 0; i < this._roles.length; ++i) {
                AttributesImpl attrs1 = new AttributesImpl();
                attrs1.addAttribute("", "soapActorName", "soapActorName", "CDATA", this._roles[i]);
                context.startElement(QNAME_JAXRPC_ROLE, attrs1);
                context.endElement();
            }
        }
        context.endElement();
    }

    public ArrayList getHandlerInfoList() {
        return this._hiList;
    }

    public void setHandlerInfoList(ArrayList hiList) {
        this._hiList = hiList;
    }

    public String[] getRoles() {
        return this._roles;
    }

    public void setRoles(String[] roles) {
        this._roles = roles;
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

