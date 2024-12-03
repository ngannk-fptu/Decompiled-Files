/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.io.StringReader;
import javax.activation.DataHandler;
import javax.xml.transform.stream.StreamSource;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializer;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SourceDataHandlerDeserializer
extends JAFDataHandlerDeserializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$SourceDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$SourceDataHandlerDeserializer = SourceDataHandlerDeserializer.class$("org.apache.axis.encoding.ser.SourceDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$SourceDataHandlerDeserializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SourceDataHandlerDeserializer;

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        super.startElement(namespace, localName, prefix, attributes, context);
        if (this.getValue() instanceof DataHandler) {
            try {
                DataHandler dh = (DataHandler)this.getValue();
                StreamSource ss = new StreamSource(new StringReader((String)dh.getContent()));
                this.setValue(ss);
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

