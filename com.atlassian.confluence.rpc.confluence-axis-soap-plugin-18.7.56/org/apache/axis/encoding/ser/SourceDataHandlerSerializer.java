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
import javax.xml.transform.stream.StreamSource;
import org.apache.axis.attachments.SourceDataSource;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializer;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

public class SourceDataHandlerSerializer
extends JAFDataHandlerSerializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$SourceDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$SourceDataHandlerSerializer = SourceDataHandlerSerializer.class$("org.apache.axis.encoding.ser.SourceDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$SourceDataHandlerSerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SourceDataHandlerSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        if (value != null) {
            if (!(value instanceof StreamSource)) {
                throw new IOException(Messages.getMessage("badSource", value.getClass().getName()));
            }
            DataHandler dh = new DataHandler((DataSource)new SourceDataSource("source", "text/xml", (StreamSource)value));
            super.serialize(name, attributes, dh, context);
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

