/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.Serializer
 *  org.apache.xml.serializer.SerializerFactory
 */
package org.apache.xalan.transformer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.ContentHandler;

public class SerializerSwitcher {
    public static void switchSerializerIfHTML(TransformerImpl transformer, String ns, String localName) throws TransformerException {
        if (null == transformer) {
            return;
        }
        if ((null == ns || ns.length() == 0) && localName.equalsIgnoreCase("html")) {
            if (null != transformer.getOutputPropertyNoDefault("method")) {
                return;
            }
            Properties prevProperties = transformer.getOutputFormat().getProperties();
            OutputProperties htmlOutputProperties = new OutputProperties("html");
            htmlOutputProperties.copyFrom(prevProperties, true);
            Properties htmlProperties = htmlOutputProperties.getProperties();
            try {
                Object oldSerializer = null;
                if (null != oldSerializer) {
                    Serializer serializer = SerializerFactory.getSerializer((Properties)htmlProperties);
                    Writer writer = oldSerializer.getWriter();
                    if (null != writer) {
                        serializer.setWriter(writer);
                    } else {
                        OutputStream os = oldSerializer.getOutputStream();
                        if (null != os) {
                            serializer.setOutputStream(os);
                        }
                    }
                    ContentHandler ch = serializer.asContentHandler();
                    transformer.setContentHandler(ch);
                }
            }
            catch (IOException e) {
                throw new TransformerException(e);
            }
        }
    }

    private static String getOutputPropertyNoDefault(String qnameString, Properties props) throws IllegalArgumentException {
        String value = (String)props.get(qnameString);
        return value;
    }

    public static Serializer switchSerializerIfHTML(String ns, String localName, Properties props, Serializer oldSerializer) throws TransformerException {
        Serializer newSerializer = oldSerializer;
        if ((null == ns || ns.length() == 0) && localName.equalsIgnoreCase("html")) {
            if (null != SerializerSwitcher.getOutputPropertyNoDefault("method", props)) {
                return newSerializer;
            }
            Properties prevProperties = props;
            OutputProperties htmlOutputProperties = new OutputProperties("html");
            htmlOutputProperties.copyFrom(prevProperties, true);
            Properties htmlProperties = htmlOutputProperties.getProperties();
            if (null != oldSerializer) {
                Serializer serializer = SerializerFactory.getSerializer((Properties)htmlProperties);
                Writer writer = oldSerializer.getWriter();
                if (null != writer) {
                    serializer.setWriter(writer);
                } else {
                    OutputStream os = serializer.getOutputStream();
                    if (null != os) {
                        serializer.setOutputStream(os);
                    }
                }
                newSerializer = serializer;
            }
        }
        return newSerializer;
    }
}

