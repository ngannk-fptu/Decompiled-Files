/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.mail.internet.MimeMultipart
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.internet.MimeMultipart;
import javax.xml.namespace.QName;
import org.apache.axis.attachments.MimeMultipartDataSource;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializer;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

public class MimeMultipartDataHandlerSerializer
extends JAFDataHandlerSerializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerSerializer = MimeMultipartDataHandlerSerializer.class$("org.apache.axis.encoding.ser.MimeMultipartDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerSerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        if (value != null) {
            DataHandler dh = new DataHandler((DataSource)new MimeMultipartDataSource("Multipart", (MimeMultipart)value));
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

