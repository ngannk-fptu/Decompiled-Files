/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.mail.internet.MimeMultipart
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import javax.activation.DataHandler;
import javax.mail.internet.MimeMultipart;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializer;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MimeMultipartDataHandlerDeserializer
extends JAFDataHandlerDeserializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerDeserializer = MimeMultipartDataHandlerDeserializer.class$("org.apache.axis.encoding.ser.MimeMultipartDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerDeserializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerDeserializer;

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        super.startElement(namespace, localName, prefix, attributes, context);
        if (this.getValue() instanceof DataHandler) {
            try {
                DataHandler dh = (DataHandler)this.getValue();
                MimeMultipart mmp = new MimeMultipart(dh.getDataSource());
                if (mmp.getCount() == 0) {
                    mmp = null;
                }
                this.setValue(mmp);
            }
            catch (Exception e) {
                throw new SAXException(e);
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

