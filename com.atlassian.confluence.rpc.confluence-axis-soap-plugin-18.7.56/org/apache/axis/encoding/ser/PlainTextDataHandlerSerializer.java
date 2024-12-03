/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import org.apache.axis.attachments.PlainTextDataSource;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializer;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

public class PlainTextDataHandlerSerializer
extends JAFDataHandlerSerializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$PlainTextDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$PlainTextDataHandlerSerializer = PlainTextDataHandlerSerializer.class$("org.apache.axis.encoding.ser.PlainTextDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$PlainTextDataHandlerSerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$PlainTextDataHandlerSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        DataHandler dh = new DataHandler((DataSource)new PlainTextDataSource("source", (String)value));
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

