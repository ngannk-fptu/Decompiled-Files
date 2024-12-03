/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.message.SOAPFaultBuilder;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SOAPFaultReasonBuilder
extends SOAPHandler
implements Callback {
    private ArrayList text = new ArrayList();
    private SOAPFaultBuilder faultBuilder;

    public SOAPFaultReasonBuilder(SOAPFaultBuilder faultBuilder) {
        this.faultBuilder = faultBuilder;
    }

    public SOAPHandler onStartChild(String namespace, String name, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        QName thisQName = new QName(namespace, name);
        if (thisQName.equals(Constants.QNAME_TEXT_SOAP12)) {
            Deserializer currentDeser = null;
            currentDeser = context.getDeserializerForType(Constants.XSD_STRING);
            if (currentDeser != null) {
                currentDeser.registerValueTarget(new CallbackTarget(this.faultBuilder, thisQName));
            }
            return (SOAPHandler)((Object)currentDeser);
        }
        return null;
    }

    public void setValue(Object value, Object hint) {
        this.text.add(value);
    }

    public ArrayList getText() {
        return this.text;
    }
}

