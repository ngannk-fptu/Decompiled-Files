/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.awt.Image;
import java.io.InputStream;
import javax.activation.DataHandler;
import org.apache.axis.components.image.ImageIOFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializer;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ImageDataHandlerDeserializer
extends JAFDataHandlerDeserializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$ImageDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$ImageDataHandlerDeserializer = ImageDataHandlerDeserializer.class$("org.apache.axis.encoding.ser.ImageDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$ImageDataHandlerDeserializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ImageDataHandlerDeserializer;

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        super.startElement(namespace, localName, prefix, attributes, context);
        if (this.getValue() instanceof DataHandler) {
            try {
                DataHandler dh = (DataHandler)this.getValue();
                InputStream is = dh.getInputStream();
                Image image = ImageIOFactory.getImageIO().loadImage(is);
                this.setValue(image);
            }
            catch (Exception e) {
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

