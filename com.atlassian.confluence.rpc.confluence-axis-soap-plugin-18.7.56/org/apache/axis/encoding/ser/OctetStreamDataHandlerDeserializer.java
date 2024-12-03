/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataHandler;
import org.apache.axis.attachments.OctetStream;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializer;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class OctetStreamDataHandlerDeserializer
extends JAFDataHandlerDeserializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$OctetStreamDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$OctetStreamDataHandlerDeserializer = OctetStreamDataHandlerDeserializer.class$("org.apache.axis.encoding.ser.OctetStreamDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$OctetStreamDataHandlerDeserializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$OctetStreamDataHandlerDeserializer;

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        super.startElement(namespace, localName, prefix, attributes, context);
        if (this.getValue() instanceof DataHandler) {
            try {
                DataHandler dh = (DataHandler)this.getValue();
                InputStream in = dh.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int byte1 = -1;
                while ((byte1 = in.read()) != -1) {
                    baos.write(byte1);
                }
                OctetStream os = new OctetStream(baos.toByteArray());
                this.setValue(os);
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

