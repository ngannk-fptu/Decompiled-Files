/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.RPCParamTarget;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RPCHandler
extends SOAPHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$RPCHandler == null ? (class$org$apache$axis$message$RPCHandler = RPCHandler.class$("org.apache.axis.message.RPCHandler")) : class$org$apache$axis$message$RPCHandler).getName());
    private RPCElement rpcElem;
    private RPCParam currentParam = null;
    private boolean isResponse;
    private OperationDesc operation;
    private boolean isHeaderElement;
    static /* synthetic */ Class class$org$apache$axis$message$RPCHandler;
    static /* synthetic */ Class class$org$w3c$dom$Element;

    public RPCHandler(RPCElement rpcElem, boolean isResponse) throws SAXException {
        this.rpcElem = rpcElem;
        this.isResponse = isResponse;
    }

    public void setOperation(OperationDesc myOperation) {
        this.operation = myOperation;
    }

    public void setHeaderElement(boolean value) {
        this.isHeaderElement = true;
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        super.startElement(namespace, localName, prefix, attributes, context);
        this.currentParam = null;
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: RPCHandler.onStartChild()");
        }
        if (!context.isDoneParsing()) {
            try {
                context.pushNewElement(new MessageElement(namespace, localName, prefix, attributes, context));
            }
            catch (AxisFault axisFault) {
                throw new SAXException(axisFault);
            }
        }
        MessageElement curEl = context.getCurElement();
        QName type = null;
        QName qname = new QName(namespace, localName);
        ParameterDesc paramDesc = null;
        SOAPConstants soapConstants = context.getSOAPConstants();
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS && Constants.QNAME_RPC_RESULT.equals(qname)) {
            return new DeserializerImpl();
        }
        if (this.currentParam == null || !this.currentParam.getQName().getNamespaceURI().equals(namespace) || !this.currentParam.getQName().getLocalPart().equals(localName)) {
            this.currentParam = new RPCParam(namespace, localName, (Object)null);
            this.rpcElem.addParam(this.currentParam);
        }
        if ((type = curEl.getType()) == null) {
            type = context.getTypeFromAttributes(namespace, localName, attributes);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("typeFromAttr00", "" + type));
        }
        Class destClass = null;
        if (this.operation != null) {
            paramDesc = this.isResponse ? this.operation.getOutputParamByQName(qname) : this.operation.getInputParamByQName(qname);
            if (paramDesc == null) {
                paramDesc = this.isResponse ? this.operation.getReturnParamDesc() : this.operation.getParameter(this.rpcElem.getParams().size() - 1);
            }
            if (paramDesc == null) {
                throw new SAXException(Messages.getMessage("noParmDesc"));
            }
            if (!this.isHeaderElement && (this.isResponse && paramDesc.isOutHeader() || !this.isResponse && paramDesc.isInHeader())) {
                throw new SAXException(Messages.getMessage("expectedHeaderParam", paramDesc.getQName().toString()));
            }
            destClass = paramDesc.getJavaType();
            if (destClass != null && destClass.isArray()) {
                context.setDestinationClass(destClass);
            }
            this.currentParam.setParamDesc(paramDesc);
            if (type == null) {
                type = paramDesc.getTypeQName();
            }
        }
        if (type != null && type.equals(XMLType.AXIS_VOID)) {
            DeserializerImpl nilDSer = new DeserializerImpl();
            return nilDSer;
        }
        if (context.isNil(attributes)) {
            DeserializerImpl nilDSer = new DeserializerImpl();
            nilDSer.registerValueTarget(new RPCParamTarget(this.currentParam));
            return nilDSer;
        }
        Deserializer dser = null;
        if (type == null && namespace != null && !namespace.equals("")) {
            dser = context.getDeserializerForType(qname);
        } else {
            dser = context.getDeserializer(destClass, type);
            if (dser == null && destClass != null && destClass.isArray() && this.operation.getStyle() == Style.DOCUMENT) {
                dser = context.getDeserializerForClass(destClass);
            }
        }
        if (dser == null) {
            if (type != null) {
                Class xsiClass;
                dser = context.getDeserializerForType(type);
                if (null != destClass && dser == null && (class$org$w3c$dom$Element == null ? (class$org$w3c$dom$Element = RPCHandler.class$("org.w3c.dom.Element")) : class$org$w3c$dom$Element).isAssignableFrom(destClass)) {
                    dser = context.getDeserializerForType(Constants.SOAP_ELEMENT);
                }
                if (dser == null) {
                    dser = context.getDeserializerForClass(destClass);
                }
                if (dser == null) {
                    throw new SAXException(Messages.getMessage("noDeser01", localName, "" + type));
                }
                if (paramDesc != null && paramDesc.getJavaType() != null && null != (xsiClass = context.getTypeMapping().getClassForQName(type)) && !JavaUtils.isConvertable(xsiClass, destClass)) {
                    throw new SAXException("Bad types (" + xsiClass + " -> " + destClass + ")");
                }
            } else {
                dser = context.getDeserializerForClass(destClass);
                if (dser == null) {
                    dser = new DeserializerImpl();
                }
            }
        }
        dser.setDefaultType(type);
        dser.registerValueTarget(new RPCParamTarget(this.currentParam));
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: RPCHandler.onStartChild()");
        }
        return (SOAPHandler)((Object)dser);
    }

    public void endElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("setProp00", "MessageContext", "RPCHandler.endElement()."));
        }
        context.getMessageContext().setProperty("RPC", this.rpcElem);
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

