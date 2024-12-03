/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.DOMSerializer
 *  org.apache.xml.serializer.Serializer
 *  org.apache.xml.serializer.SerializerFactory
 */
package org.apache.xalan.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.apache.xalan.serialize.DOMSerializer;
import org.apache.xalan.serialize.Serializer;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public abstract class SerializerFactory {
    private SerializerFactory() {
    }

    public static Serializer getSerializer(Properties format) {
        org.apache.xml.serializer.Serializer ser = org.apache.xml.serializer.SerializerFactory.getSerializer((Properties)format);
        SerializerWrapper si = new SerializerWrapper(ser);
        return si;
    }

    private static class DOMSerializerWrapper
    implements DOMSerializer {
        private final org.apache.xml.serializer.DOMSerializer m_dom;

        DOMSerializerWrapper(org.apache.xml.serializer.DOMSerializer domser) {
            this.m_dom = domser;
        }

        public void serialize(Node node) throws IOException {
            this.m_dom.serialize(node);
        }
    }

    private static class SerializerWrapper
    implements Serializer {
        private final org.apache.xml.serializer.Serializer m_serializer;
        private DOMSerializer m_old_DOMSerializer;

        SerializerWrapper(org.apache.xml.serializer.Serializer ser) {
            this.m_serializer = ser;
        }

        @Override
        public void setOutputStream(OutputStream output) {
            this.m_serializer.setOutputStream(output);
        }

        @Override
        public OutputStream getOutputStream() {
            return this.m_serializer.getOutputStream();
        }

        @Override
        public void setWriter(Writer writer) {
            this.m_serializer.setWriter(writer);
        }

        @Override
        public Writer getWriter() {
            return this.m_serializer.getWriter();
        }

        @Override
        public void setOutputFormat(Properties format) {
            this.m_serializer.setOutputFormat(format);
        }

        @Override
        public Properties getOutputFormat() {
            return this.m_serializer.getOutputFormat();
        }

        @Override
        public ContentHandler asContentHandler() throws IOException {
            return this.m_serializer.asContentHandler();
        }

        @Override
        public DOMSerializer asDOMSerializer() throws IOException {
            if (this.m_old_DOMSerializer == null) {
                this.m_old_DOMSerializer = new DOMSerializerWrapper(this.m_serializer.asDOMSerializer());
            }
            return this.m_old_DOMSerializer;
        }

        @Override
        public boolean reset() {
            return this.m_serializer.reset();
        }
    }
}

