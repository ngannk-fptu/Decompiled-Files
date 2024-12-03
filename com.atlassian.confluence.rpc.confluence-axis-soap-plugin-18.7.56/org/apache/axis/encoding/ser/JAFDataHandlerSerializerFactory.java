/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class JAFDataHandlerSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer;
    static /* synthetic */ Class class$java$awt$Image;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ImageDataHandlerSerializer;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$PlainTextDataHandlerSerializer;
    static /* synthetic */ Class class$javax$xml$transform$Source;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SourceDataHandlerSerializer;
    static /* synthetic */ Class class$javax$mail$internet$MimeMultipart;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerSerializer;
    static /* synthetic */ Class class$org$apache$axis$attachments$OctetStream;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$OctetStreamDataHandlerSerializer;

    public JAFDataHandlerSerializerFactory(Class javaType, QName xmlType) {
        super(JAFDataHandlerSerializerFactory.getSerializerClass(javaType, xmlType), xmlType, javaType);
    }

    public JAFDataHandlerSerializerFactory() {
        super(class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer = JAFDataHandlerSerializerFactory.class$("org.apache.axis.encoding.ser.JAFDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer);
    }

    private static Class getSerializerClass(Class javaType, QName xmlType) {
        Class ser = (class$java$awt$Image == null ? (class$java$awt$Image = JAFDataHandlerSerializerFactory.class$("java.awt.Image")) : class$java$awt$Image).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$ImageDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$ImageDataHandlerSerializer = JAFDataHandlerSerializerFactory.class$("org.apache.axis.encoding.ser.ImageDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$ImageDataHandlerSerializer) : ((class$java$lang$String == null ? (class$java$lang$String = JAFDataHandlerSerializerFactory.class$("java.lang.String")) : class$java$lang$String).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$PlainTextDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$PlainTextDataHandlerSerializer = JAFDataHandlerSerializerFactory.class$("org.apache.axis.encoding.ser.PlainTextDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$PlainTextDataHandlerSerializer) : ((class$javax$xml$transform$Source == null ? (class$javax$xml$transform$Source = JAFDataHandlerSerializerFactory.class$("javax.xml.transform.Source")) : class$javax$xml$transform$Source).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$SourceDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$SourceDataHandlerSerializer = JAFDataHandlerSerializerFactory.class$("org.apache.axis.encoding.ser.SourceDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$SourceDataHandlerSerializer) : ((class$javax$mail$internet$MimeMultipart == null ? (class$javax$mail$internet$MimeMultipart = JAFDataHandlerSerializerFactory.class$("javax.mail.internet.MimeMultipart")) : class$javax$mail$internet$MimeMultipart).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerSerializer = JAFDataHandlerSerializerFactory.class$("org.apache.axis.encoding.ser.MimeMultipartDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$MimeMultipartDataHandlerSerializer) : ((class$org$apache$axis$attachments$OctetStream == null ? (class$org$apache$axis$attachments$OctetStream = JAFDataHandlerSerializerFactory.class$("org.apache.axis.attachments.OctetStream")) : class$org$apache$axis$attachments$OctetStream).isAssignableFrom(javaType) ? (class$org$apache$axis$encoding$ser$OctetStreamDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$OctetStreamDataHandlerSerializer = JAFDataHandlerSerializerFactory.class$("org.apache.axis.encoding.ser.OctetStreamDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$OctetStreamDataHandlerSerializer) : (class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer == null ? (class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer = JAFDataHandlerSerializerFactory.class$("org.apache.axis.encoding.ser.JAFDataHandlerSerializer")) : class$org$apache$axis$encoding$ser$JAFDataHandlerSerializer)))));
        return ser;
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

