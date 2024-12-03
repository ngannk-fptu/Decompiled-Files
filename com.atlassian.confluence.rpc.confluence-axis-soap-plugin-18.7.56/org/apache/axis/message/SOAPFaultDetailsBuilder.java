/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.SOAPFaultBuilder;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SOAPFaultDetailsBuilder
extends SOAPHandler
implements Callback {
    protected SOAPFaultBuilder builder;

    public SOAPFaultDetailsBuilder(SOAPFaultBuilder builder) {
        this.builder = builder;
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        SOAPConstants soapConstants = context.getSOAPConstants();
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS && attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null) {
            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Detail"), null, null, null);
            throw new SAXException(fault);
        }
        super.startElement(namespace, localName, prefix, attributes, context);
    }

    public SOAPHandler onStartChild(String namespace, String name, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        QName qn = new QName(namespace, name);
        if (name.equals("exceptionName")) {
            Deserializer dser = context.getDeserializerForType(Constants.XSD_STRING);
            dser.registerValueTarget(new CallbackTarget(this, "exceptionName"));
            return (SOAPHandler)((Object)dser);
        }
        MessageContext msgContext = context.getMessageContext();
        SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
        OperationDesc op = null;
        if (msgContext != null) {
            soapConstants = msgContext.getSOAPConstants();
            op = msgContext.getOperation();
        }
        Class faultClass = null;
        QName faultXmlType = null;
        if (op != null) {
            FaultDesc faultDesc = null;
            faultXmlType = context.getTypeFromAttributes(namespace, name, attributes);
            if (faultXmlType != null) {
                faultDesc = op.getFaultByXmlType(faultXmlType);
            }
            if (faultDesc == null) {
                faultDesc = op.getFaultByQName(qn);
                if (faultXmlType == null && faultDesc != null) {
                    faultXmlType = faultDesc.getXmlType();
                }
            }
            if (faultDesc == null && op.getFaults() != null) {
                Iterator i = op.getFaults().iterator();
                while (i.hasNext()) {
                    FaultDesc fdesc = (FaultDesc)i.next();
                    if (!fdesc.getClassName().equals(name)) continue;
                    faultDesc = fdesc;
                    faultXmlType = fdesc.getXmlType();
                    break;
                }
            }
            if (faultDesc != null) {
                try {
                    faultClass = ClassUtils.forName(faultDesc.getClassName());
                }
                catch (ClassNotFoundException e) {}
            }
        } else {
            faultXmlType = context.getTypeFromAttributes(namespace, name, attributes);
        }
        if (faultClass == null) {
            faultClass = context.getTypeMapping().getClassForQName(faultXmlType);
        }
        if (faultClass != null && faultXmlType != null) {
            this.builder.setFaultClass(faultClass);
            this.builder.setWaiting(true);
            Deserializer dser = null;
            if (attributes.getValue(soapConstants.getAttrHref()) == null) {
                dser = context.getDeserializerForType(faultXmlType);
            } else {
                dser = new DeserializerImpl();
                dser.setDefaultType(faultXmlType);
            }
            if (dser != null) {
                dser.registerValueTarget(new CallbackTarget(this, "faultData"));
            }
            return (SOAPHandler)((Object)dser);
        }
        return null;
    }

    public void setValue(Object value, Object hint) {
        if ("faultData".equals(hint)) {
            this.builder.setFaultData(value);
        } else if ("exceptionName".equals(hint)) {
            String faultClassName = (String)value;
            try {
                Class faultClass = ClassUtils.forName(faultClassName);
                this.builder.setFaultClass(faultClass);
            }
            catch (ClassNotFoundException e) {
                // empty catch block
            }
        }
    }
}

