/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.activation.DataHandler;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializer;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PlainTextDataHandlerDeserializer
extends JAFDataHandlerDeserializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$PlainTextDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$PlainTextDataHandlerDeserializer = PlainTextDataHandlerDeserializer.class$("org.apache.axis.encoding.ser.PlainTextDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$PlainTextDataHandlerDeserializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$PlainTextDataHandlerDeserializer;

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        super.startElement(namespace, localName, prefix, attributes, context);
        if (this.getValue() instanceof DataHandler) {
            try {
                DataHandler dh = (DataHandler)this.getValue();
                this.setValue(dh.getContent());
            }
            catch (IOException ioe) {
                // empty catch block
            }
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

