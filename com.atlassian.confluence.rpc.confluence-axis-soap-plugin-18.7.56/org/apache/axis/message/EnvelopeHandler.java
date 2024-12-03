/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EnvelopeHandler
extends SOAPHandler {
    SOAPHandler realHandler;

    public EnvelopeHandler(SOAPHandler realHandler) {
        this.realHandler = realHandler;
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        return this.realHandler;
    }
}

