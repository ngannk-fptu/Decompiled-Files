/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SOAPFaultCodeBuilder
extends SOAPHandler
implements Callback {
    protected QName faultCode = null;
    protected SOAPFaultCodeBuilder next = null;

    public QName getFaultCode() {
        return this.faultCode;
    }

    public SOAPFaultCodeBuilder getNext() {
        return this.next;
    }

    public SOAPHandler onStartChild(String namespace, String name, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        QName thisQName = new QName(namespace, name);
        if (thisQName.equals(Constants.QNAME_FAULTVALUE_SOAP12)) {
            Deserializer currentDeser = null;
            currentDeser = context.getDeserializerForType(Constants.XSD_QNAME);
            if (currentDeser != null) {
                currentDeser.registerValueTarget(new CallbackTarget(this, thisQName));
            }
            return (SOAPHandler)((Object)currentDeser);
        }
        if (thisQName.equals(Constants.QNAME_FAULTSUBCODE_SOAP12)) {
            this.next = new SOAPFaultCodeBuilder();
            return this.next;
        }
        return null;
    }

    public void setValue(Object value, Object hint) {
        QName thisQName = (QName)hint;
        if (thisQName.equals(Constants.QNAME_FAULTVALUE_SOAP12)) {
            this.faultCode = (QName)value;
        }
    }
}

