/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.apache.xml.serialize.TextSerializer;
import org.apache.xml.serialize.XHTMLSerializer;
import org.apache.xml.serialize.XMLSerializer;

final class SerializerFactoryImpl
extends SerializerFactory {
    private String _method;

    SerializerFactoryImpl(String string) {
        this._method = string;
        if (!(this._method.equals("xml") || this._method.equals("html") || this._method.equals("xhtml") || this._method.equals("text"))) {
            String string2 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[]{string});
            throw new IllegalArgumentException(string2);
        }
    }

    @Override
    public Serializer makeSerializer(OutputFormat outputFormat) {
        Serializer serializer = this.getSerializer(outputFormat);
        serializer.setOutputFormat(outputFormat);
        return serializer;
    }

    @Override
    public Serializer makeSerializer(Writer writer, OutputFormat outputFormat) {
        Serializer serializer = this.getSerializer(outputFormat);
        serializer.setOutputCharStream(writer);
        return serializer;
    }

    @Override
    public Serializer makeSerializer(OutputStream outputStream, OutputFormat outputFormat) throws UnsupportedEncodingException {
        Serializer serializer = this.getSerializer(outputFormat);
        serializer.setOutputByteStream(outputStream);
        return serializer;
    }

    private Serializer getSerializer(OutputFormat outputFormat) {
        if (this._method.equals("xml")) {
            return new XMLSerializer(outputFormat);
        }
        if (this._method.equals("html")) {
            return new HTMLSerializer(outputFormat);
        }
        if (this._method.equals("xhtml")) {
            return new XHTMLSerializer(outputFormat);
        }
        if (this._method.equals("text")) {
            return new TextSerializer();
        }
        String string = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[]{this._method});
        throw new IllegalStateException(string);
    }

    @Override
    protected String getSupportedMethod() {
        return this._method;
    }
}

