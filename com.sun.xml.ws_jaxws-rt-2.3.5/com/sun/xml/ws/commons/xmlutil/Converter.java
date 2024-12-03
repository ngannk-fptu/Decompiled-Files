/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.logging.Logger
 */
package com.sun.xml.ws.commons.xmlutil;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.commons.xmlutil.ContextClassloaderLocal;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public final class Converter {
    public static final String UTF_8 = "UTF-8";
    private static final Logger LOGGER = Logger.getLogger(Converter.class);
    private static final ContextClassloaderLocal<XMLOutputFactory> xmlOutputFactory = new ContextClassloaderLocal<XMLOutputFactory>(){

        @Override
        protected XMLOutputFactory initialValue() throws Exception {
            return XMLOutputFactory.newInstance();
        }
    };
    private static final AtomicBoolean logMissingStaxUtilsWarning = new AtomicBoolean(false);

    private Converter() {
    }

    public static String toString(Throwable throwable) {
        if (throwable == null) {
            return "[ No exception ]";
        }
        StringWriter stringOut = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringOut));
        return stringOut.toString();
    }

    public static String toString(Packet packet) {
        if (packet == null) {
            return "[ Null packet ]";
        }
        if (packet.getMessage() == null) {
            return "[ Empty packet ]";
        }
        return Converter.toString(packet.getMessage());
    }

    public static String toStringNoIndent(Packet packet) {
        if (packet == null) {
            return "[ Null packet ]";
        }
        if (packet.getMessage() == null) {
            return "[ Empty packet ]";
        }
        return Converter.toStringNoIndent(packet.getMessage());
    }

    public static String toString(Message message) {
        return Converter.toString(message, true);
    }

    public static String toStringNoIndent(Message message) {
        return Converter.toString(message, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String toString(Message message, boolean createIndenter) {
        if (message == null) {
            return "[ Null message ]";
        }
        StringWriter stringOut = null;
        try {
            stringOut = new StringWriter();
            XMLStreamWriter writer = null;
            try {
                writer = xmlOutputFactory.get().createXMLStreamWriter(stringOut);
                if (createIndenter) {
                    writer = Converter.createIndenter(writer);
                }
                message.copy().writeTo(writer);
            }
            catch (Exception e) {
                LOGGER.log(Level.WARNING, "Unexpected exception occured while dumping message", (Throwable)e);
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (XMLStreamException ignored) {
                        LOGGER.fine("Unexpected exception occured while closing XMLStreamWriter", (Throwable)ignored);
                    }
                }
            }
            String string = stringOut.toString();
            return string;
        }
        finally {
            if (stringOut != null) {
                try {
                    stringOut.close();
                }
                catch (IOException ex) {
                    LOGGER.finest("An exception occured when trying to close StringWriter", (Throwable)ex);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] toBytes(Message message, String encoding) throws XMLStreamException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            if (message != null) {
                XMLStreamWriter xsw = xmlOutputFactory.get().createXMLStreamWriter(baos, encoding);
                try {
                    message.writeTo(xsw);
                }
                finally {
                    try {
                        xsw.close();
                    }
                    catch (XMLStreamException ex) {
                        LOGGER.warning("Unexpected exception occured while closing XMLStreamWriter", (Throwable)ex);
                    }
                }
            }
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
        finally {
            try {
                baos.close();
            }
            catch (IOException ex) {
                LOGGER.warning("Unexpected exception occured while closing ByteArrayOutputStream", (Throwable)ex);
            }
        }
    }

    public static Message toMessage(@NotNull InputStream dataStream, String encoding) throws XMLStreamException {
        XMLStreamReader xsr = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(dataStream, encoding);
        return Messages.create(xsr);
    }

    public static String messageDataToString(byte[] data, String encoding) {
        try {
            return Converter.toString(Converter.toMessage(new ByteArrayInputStream(data), encoding));
        }
        catch (XMLStreamException ex) {
            LOGGER.warning("Unexpected exception occured while converting message data to string", (Throwable)ex);
            return "[ Message Data Conversion Failed ]";
        }
    }

    private static XMLStreamWriter createIndenter(XMLStreamWriter writer) {
        block2: {
            try {
                Class<?> clazz = Converter.class.getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
                Constructor<?> c = clazz.getConstructor(XMLStreamWriter.class);
                writer = (XMLStreamWriter)XMLStreamWriter.class.cast(c.newInstance(writer));
            }
            catch (Exception ex) {
                if (!logMissingStaxUtilsWarning.compareAndSet(false, true)) break block2;
                LOGGER.log(Level.WARNING, "Put stax-utils.jar to the classpath to indent the dump output", (Throwable)ex);
            }
        }
        return writer;
    }
}

