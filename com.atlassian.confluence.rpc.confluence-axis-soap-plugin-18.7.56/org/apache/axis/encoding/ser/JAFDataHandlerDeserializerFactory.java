/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;

public class JAFDataHandlerDeserializerFactory
extends BaseDeserializerFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializerFactory == null ? (class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializerFactory = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory")) : class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializerFactory).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer;
    static /* synthetic */ Class class$java$awt$Image;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ImageDataHandlerDeserializer;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$PlainTextDataHandlerDeserializer;
    static /* synthetic */ Class class$javax$xml$transform$Source;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SourceDataHandlerDeserializer;
    static /* synthetic */ Class class$javax$mail$internet$MimeMultipart;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerDeserializer;
    static /* synthetic */ Class class$org$apache$axis$attachments$OctetStream;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$OctetStreamDataHandlerDeserializer;

    public JAFDataHandlerDeserializerFactory(Class javaType, QName xmlType) {
        super(JAFDataHandlerDeserializerFactory.getDeserializerClass(javaType, xmlType), xmlType, javaType);
        log.debug((Object)("Enter/Exit: JAFDataHandlerDeserializerFactory(" + javaType + ", " + xmlType + ")"));
    }

    public JAFDataHandlerDeserializerFactory() {
        super(class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.encoding.ser.JAFDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer);
        log.debug((Object)"Enter/Exit: JAFDataHandlerDeserializerFactory()");
    }

    private static Class getDeserializerClass(Class javaType, QName xmlType) {
        Class deser = (class$java$awt$Image == null ? (class$java$awt$Image = JAFDataHandlerDeserializerFactory.class$("java.awt.Image")) : class$java$awt$Image).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$ImageDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$ImageDataHandlerDeserializer = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.encoding.ser.ImageDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$ImageDataHandlerDeserializer) : ((class$java$lang$String == null ? (class$java$lang$String = JAFDataHandlerDeserializerFactory.class$("java.lang.String")) : class$java$lang$String).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$PlainTextDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$PlainTextDataHandlerDeserializer = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.encoding.ser.PlainTextDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$PlainTextDataHandlerDeserializer) : ((class$javax$xml$transform$Source == null ? (class$javax$xml$transform$Source = JAFDataHandlerDeserializerFactory.class$("javax.xml.transform.Source")) : class$javax$xml$transform$Source).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$SourceDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$SourceDataHandlerDeserializer = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.encoding.ser.SourceDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$SourceDataHandlerDeserializer) : ((class$javax$mail$internet$MimeMultipart == null ? (class$javax$mail$internet$MimeMultipart = JAFDataHandlerDeserializerFactory.class$("javax.mail.internet.MimeMultipart")) : class$javax$mail$internet$MimeMultipart).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerDeserializer = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.encoding.ser.MimeMultipartDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerDeserializer) : ((class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$OctetStreamDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$OctetStreamDataHandlerDeserializer = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.encoding.ser.OctetStreamDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$OctetStreamDataHandlerDeserializer) : (class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer == null ? (class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer = JAFDataHandlerDeserializerFactory.class$("org.apache.axis.encoding.ser.JAFDataHandlerDeserializer")) : class$org$apache$axis$encoding$ser$JAFDataHandlerDeserializer)))));
        return deser;
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

