/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.SOAPFaultCodeBuilder;
import org.apache.axis.message.SOAPFaultDetailsBuilder;
import org.apache.axis.message.SOAPFaultReasonBuilder;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAP11Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SOAPFaultBuilder
extends SOAPHandler
implements Callback {
    boolean waiting = false;
    boolean passedEnd = false;
    protected SOAPFault element;
    protected DeserializationContext context;
    static HashMap fields_soap11 = new HashMap();
    static HashMap fields_soap12 = new HashMap();
    protected QName faultCode = null;
    protected QName[] faultSubCode = null;
    protected String faultString = null;
    protected String faultActor = null;
    protected Element[] faultDetails;
    protected String faultNode = null;
    protected SOAPFaultCodeBuilder code;
    protected Class faultClass = null;
    protected Object faultData = null;
    private static HashMap TYPES;
    static /* synthetic */ Class class$org$apache$axis$AxisFault;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Long;

    public SOAPFaultBuilder(SOAPFault element, DeserializationContext context) {
        this.element = element;
        this.context = context;
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        SOAPConstants soapConstants = context.getSOAPConstants();
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS && attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null) {
            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Fault"), null, null, null);
            throw new SAXException(fault);
        }
        super.startElement(namespace, localName, prefix, attributes, context);
    }

    void setFaultData(Object data) {
        this.faultData = data;
        if (this.waiting && this.passedEnd) {
            this.createFault();
        }
        this.waiting = false;
    }

    public void setFaultClass(Class faultClass) {
        this.faultClass = faultClass;
    }

    public void endElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        super.endElement(namespace, localName, context);
        if (!this.waiting) {
            this.createFault();
        } else {
            this.passedEnd = true;
        }
    }

    void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    private void createFault() {
        SOAP11Constants soapConstants;
        AxisFault f = null;
        SOAPConstants sOAPConstants = soapConstants = this.context.getMessageContext() == null ? SOAPConstants.SOAP11_CONSTANTS : this.context.getMessageContext().getSOAPConstants();
        if (this.faultClass != null) {
            try {
                if (this.faultData != null) {
                    if (this.faultData instanceof AxisFault) {
                        f = (AxisFault)this.faultData;
                    } else {
                        Class argClass = this.ConvertWrapper(this.faultData.getClass());
                        try {
                            Constructor con = this.faultClass.getConstructor(argClass);
                            f = (AxisFault)con.newInstance(this.faultData);
                        }
                        catch (Exception e) {
                            // empty catch block
                        }
                        if (f == null && this.faultData instanceof Exception) {
                            f = AxisFault.makeFault((Exception)this.faultData);
                        }
                    }
                }
                if ((class$org$apache$axis$AxisFault == null ? (class$org$apache$axis$AxisFault = SOAPFaultBuilder.class$("org.apache.axis.AxisFault")) : class$org$apache$axis$AxisFault).isAssignableFrom(this.faultClass)) {
                    if (f == null) {
                        f = (AxisFault)this.faultClass.newInstance();
                    }
                    if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                        f.setFaultCode(this.code.getFaultCode());
                        SOAPFaultCodeBuilder c = this.code;
                        while ((c = c.getNext()) != null) {
                            f.addFaultSubCode(c.getFaultCode());
                        }
                    } else {
                        f.setFaultCode(this.faultCode);
                    }
                    f.setFaultString(this.faultString);
                    f.setFaultActor(this.faultActor);
                    f.setFaultNode(this.faultNode);
                    f.setFaultDetail(this.faultDetails);
                }
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (f == null) {
            if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                this.faultCode = this.code.getFaultCode();
                if (this.code.getNext() != null) {
                    Vector<QName> v = new Vector<QName>();
                    SOAPFaultCodeBuilder c = this.code;
                    while ((c = c.getNext()) != null) {
                        v.add(c.getFaultCode());
                    }
                    this.faultSubCode = v.toArray(new QName[v.size()]);
                }
            }
            f = new AxisFault(this.faultCode, this.faultSubCode, this.faultString, this.faultActor, this.faultNode, this.faultDetails);
            try {
                Vector headers = this.element.getEnvelope().getHeaders();
                for (int i = 0; i < headers.size(); ++i) {
                    SOAPHeaderElement header = (SOAPHeaderElement)headers.elementAt(i);
                    f.addHeader(header);
                }
            }
            catch (AxisFault axisFault) {
                // empty catch block
            }
        }
        this.element.setFault(f);
    }

    public SOAPHandler onStartChild(String namespace, String name, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        SOAPHandler retHandler = null;
        SOAP11Constants soapConstants = context.getMessageContext() == null ? SOAPConstants.SOAP11_CONSTANTS : context.getMessageContext().getSOAPConstants();
        QName qName = null;
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            qName = (QName)fields_soap12.get(name);
            if (qName == null) {
                QName thisQName = new QName(namespace, name);
                if (thisQName.equals(Constants.QNAME_FAULTCODE_SOAP12)) {
                    this.code = new SOAPFaultCodeBuilder();
                    return this.code;
                }
                if (thisQName.equals(Constants.QNAME_FAULTREASON_SOAP12)) {
                    return new SOAPFaultReasonBuilder(this);
                }
                if (thisQName.equals(Constants.QNAME_FAULTDETAIL_SOAP12)) {
                    return new SOAPFaultDetailsBuilder(this);
                }
            }
        } else {
            qName = (QName)fields_soap11.get(name);
            if (qName == null && name.equals("detail")) {
                return new SOAPFaultDetailsBuilder(this);
            }
        }
        if (qName != null) {
            Deserializer currentDeser = context.getDeserializerForType(qName);
            if (currentDeser != null) {
                currentDeser.registerValueTarget(new CallbackTarget(this, new QName(namespace, name)));
            }
            retHandler = (SOAPHandler)((Object)currentDeser);
        }
        return retHandler;
    }

    public void onEndChild(String namespace, String localName, DeserializationContext context) throws SAXException {
        MessageElement el;
        List children;
        if ("detail".equals(localName) && (children = (el = context.getCurElement()).getChildren()) != null) {
            Element[] elements = new Element[children.size()];
            for (int i = 0; i < elements.length; ++i) {
                try {
                    elements[i] = ((MessageElement)children.get(i)).getAsDOM();
                    continue;
                }
                catch (Exception e) {
                    throw new SAXException(e);
                }
            }
            this.faultDetails = elements;
        }
    }

    public void setValue(Object value, Object hint) {
        String local = ((QName)hint).getLocalPart();
        if (((QName)hint).getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            if (local.equals("Role")) {
                this.faultActor = (String)value;
            } else if (local.equals("Text")) {
                this.faultString = (String)value;
            } else if (local.equals("Node")) {
                this.faultNode = (String)value;
            }
        } else if (local.equals("faultcode")) {
            this.faultCode = (QName)value;
        } else if (local.equals("faultstring")) {
            this.faultString = (String)value;
        } else if (local.equals("faultactor")) {
            this.faultActor = (String)value;
        }
    }

    private Class ConvertWrapper(Class cls) {
        Class ret = (Class)TYPES.get(cls);
        if (ret != null) {
            return ret;
        }
        return cls;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        fields_soap11.put("faultcode", Constants.XSD_QNAME);
        fields_soap11.put("faultstring", Constants.XSD_STRING);
        fields_soap11.put("faultactor", Constants.XSD_STRING);
        fields_soap11.put("detail", null);
        fields_soap12.put("Reason", null);
        fields_soap12.put("Role", Constants.XSD_STRING);
        fields_soap12.put("Node", Constants.XSD_STRING);
        fields_soap12.put("Detail", null);
        TYPES = new HashMap(7);
        TYPES.put(class$java$lang$Integer == null ? (class$java$lang$Integer = SOAPFaultBuilder.class$("java.lang.Integer")) : class$java$lang$Integer, Integer.TYPE);
        TYPES.put(class$java$lang$Float == null ? (class$java$lang$Float = SOAPFaultBuilder.class$("java.lang.Float")) : class$java$lang$Float, Float.TYPE);
        TYPES.put(class$java$lang$Boolean == null ? (class$java$lang$Boolean = SOAPFaultBuilder.class$("java.lang.Boolean")) : class$java$lang$Boolean, Boolean.TYPE);
        TYPES.put(class$java$lang$Double == null ? (class$java$lang$Double = SOAPFaultBuilder.class$("java.lang.Double")) : class$java$lang$Double, Double.TYPE);
        TYPES.put(class$java$lang$Byte == null ? (class$java$lang$Byte = SOAPFaultBuilder.class$("java.lang.Byte")) : class$java$lang$Byte, Byte.TYPE);
        TYPES.put(class$java$lang$Short == null ? (class$java$lang$Short = SOAPFaultBuilder.class$("java.lang.Short")) : class$java$lang$Short, Short.TYPE);
        TYPES.put(class$java$lang$Long == null ? (class$java$lang$Long = SOAPFaultBuilder.class$("java.lang.Long")) : class$java$lang$Long, Long.TYPE);
    }
}

