/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.awt.Image;
import java.io.IOException;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import org.apache.axis.attachments.ImageDataSource;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializer;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

public class ImageDataHandlerSerializer
extends JAFDataHandlerSerializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$ImageDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$ImageDataHandlerSerializer = ImageDataHandlerSerializer.class$("org.apache.axis.encoding.ser.ImageDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$ImageDataHandlerSerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ImageDataHandlerSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        DataHandler dh = new DataHandler((DataSource)new ImageDataSource("source", (Image)value));
        super.serialize(name, attributes, dh, context);
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

