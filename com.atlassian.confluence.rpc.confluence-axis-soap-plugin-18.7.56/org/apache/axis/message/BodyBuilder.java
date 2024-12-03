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
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCHandler;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.SOAPFaultBuilder;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BodyBuilder
extends SOAPHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$BodyBuilder == null ? (class$org$apache$axis$message$BodyBuilder = BodyBuilder.class$("org.apache.axis.message.BodyBuilder")) : class$org$apache$axis$message$BodyBuilder).getName());
    boolean gotRPCElement = false;
    private SOAPEnvelope envelope;
    static /* synthetic */ Class class$org$apache$axis$message$BodyBuilder;

    BodyBuilder(SOAPEnvelope envelope) {
        this.envelope = envelope;
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        SOAPConstants soapConstants = context.getSOAPConstants();
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS && attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null) {
            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Body"), null, null, null);
            throw new SAXException(fault);
        }
        if (!context.isDoneParsing()) {
            if (!context.isProcessingRef()) {
                if (this.myElement == null) {
                    try {
                        this.myElement = new SOAPBody(namespace, localName, prefix, attributes, context, this.envelope.getSOAPConstants());
                    }
                    catch (AxisFault axisFault) {
                        throw new SAXException(axisFault);
                    }
                }
                context.pushNewElement(this.myElement);
            }
            this.envelope.setBody((SOAPBody)this.myElement);
        }
    }

    public MessageElement makeNewElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws AxisFault {
        return new SOAPBody(namespace, localName, prefix, attributes, context, context.getSOAPConstants());
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        SOAPBodyElement element = null;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: BodyBuilder::onStartChild()");
        }
        QName qname = new QName(namespace, localName);
        SOAPHandler handler = null;
        boolean isRoot = true;
        String root = attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, "root");
        if (root != null && root.equals("0")) {
            isRoot = false;
        }
        MessageContext msgContext = context.getMessageContext();
        OperationDesc[] operations = null;
        try {
            if (msgContext != null) {
                operations = msgContext.getPossibleOperationsByQName(qname);
            }
            if (operations != null && operations.length == 1) {
                msgContext.setOperation(operations[0]);
            }
        }
        catch (AxisFault e) {
            throw new SAXException(e);
        }
        Style style = operations == null ? Style.RPC : operations[0].getStyle();
        SOAPConstants soapConstants = context.getSOAPConstants();
        if (localName.equals("Fault") && namespace.equals(soapConstants.getEnvelopeURI())) {
            try {
                element = new SOAPFault(namespace, localName, prefix, attributes, context);
            }
            catch (AxisFault axisFault) {
                throw new SAXException(axisFault);
            }
            element.setEnvelope(context.getEnvelope());
            handler = new SOAPFaultBuilder((SOAPFault)element, context);
        } else if (!this.gotRPCElement && isRoot && style != Style.MESSAGE) {
            this.gotRPCElement = true;
            try {
                element = new RPCElement(namespace, localName, prefix, attributes, context, operations);
            }
            catch (AxisFault e) {
                throw new SAXException(e);
            }
            if (!(msgContext == null || msgContext.isHighFidelity() || operations != null && operations.length != 1)) {
                ((RPCElement)element).setNeedDeser(false);
                boolean isResponse = false;
                if (msgContext.getCurrentMessage() != null && "response".equals(msgContext.getCurrentMessage().getMessageType())) {
                    isResponse = true;
                }
                handler = new RPCHandler((RPCElement)element, isResponse);
                if (operations != null) {
                    ((RPCHandler)handler).setOperation(operations[0]);
                    msgContext.setOperation(operations[0]);
                }
            }
        }
        if (element == null) {
            if (style == Style.RPC && soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                throw new SAXException(Messages.getMessage("onlyOneBodyFor12"));
            }
            try {
                element = new SOAPBodyElement(namespace, localName, prefix, attributes, context);
            }
            catch (AxisFault axisFault) {
                throw new SAXException(axisFault);
            }
            if (element.getFixupDeserializer() != null) {
                handler = (SOAPHandler)((Object)element.getFixupDeserializer());
            }
        }
        if (handler == null) {
            handler = new SOAPHandler();
        }
        handler.myElement = element;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: BodyBuilder::onStartChild()");
        }
        return handler;
    }

    public void onEndChild(String namespace, String localName, DeserializationContext context) {
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

