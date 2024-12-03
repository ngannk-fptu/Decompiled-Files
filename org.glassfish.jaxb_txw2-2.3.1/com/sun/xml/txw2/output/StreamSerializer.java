/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

import com.sun.xml.txw2.TxwException;
import com.sun.xml.txw2.output.DataWriter;
import com.sun.xml.txw2.output.SaxSerializer;
import com.sun.xml.txw2.output.XMLWriter;
import com.sun.xml.txw2.output.XmlSerializer;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;

public class StreamSerializer
implements XmlSerializer {
    private final SaxSerializer serializer;
    private final XMLWriter writer;

    public StreamSerializer(OutputStream out) {
        this(StreamSerializer.createWriter(out));
    }

    public StreamSerializer(OutputStream out, String encoding) throws UnsupportedEncodingException {
        this(StreamSerializer.createWriter(out, encoding));
    }

    public StreamSerializer(Writer out) {
        this(new StreamResult(out));
    }

    public StreamSerializer(StreamResult streamResult) {
        final OutputStream[] autoClose = new OutputStream[1];
        if (streamResult.getWriter() != null) {
            this.writer = StreamSerializer.createWriter(streamResult.getWriter());
        } else if (streamResult.getOutputStream() != null) {
            this.writer = StreamSerializer.createWriter(streamResult.getOutputStream());
        } else if (streamResult.getSystemId() != null) {
            String fileURL = streamResult.getSystemId();
            fileURL = this.convertURL(fileURL);
            try {
                FileOutputStream fos = new FileOutputStream(fileURL);
                autoClose[0] = fos;
                this.writer = StreamSerializer.createWriter(fos);
            }
            catch (IOException e) {
                throw new TxwException(e);
            }
        } else {
            throw new IllegalArgumentException();
        }
        this.serializer = new SaxSerializer(this.writer, this.writer, false){

            @Override
            public void endDocument() {
                super.endDocument();
                if (autoClose[0] != null) {
                    try {
                        autoClose[0].close();
                    }
                    catch (IOException e) {
                        throw new TxwException(e);
                    }
                    autoClose[0] = null;
                }
            }
        };
    }

    private StreamSerializer(XMLWriter writer) {
        this.writer = writer;
        this.serializer = new SaxSerializer(writer, writer, false);
    }

    private String convertURL(String url) {
        url = url.replace('\\', '/');
        url = url.replaceAll("//", "/");
        if ((url = url.replaceAll("//", "/")).startsWith("file:/")) {
            url = url.substring(6).indexOf(":") > 0 ? url.substring(6) : url.substring(5);
        }
        return url;
    }

    @Override
    public void startDocument() {
        this.serializer.startDocument();
    }

    @Override
    public void beginStartTag(String uri, String localName, String prefix) {
        this.serializer.beginStartTag(uri, localName, prefix);
    }

    @Override
    public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
        this.serializer.writeAttribute(uri, localName, prefix, value);
    }

    @Override
    public void writeXmlns(String prefix, String uri) {
        this.serializer.writeXmlns(prefix, uri);
    }

    @Override
    public void endStartTag(String uri, String localName, String prefix) {
        this.serializer.endStartTag(uri, localName, prefix);
    }

    @Override
    public void endTag() {
        this.serializer.endTag();
    }

    @Override
    public void text(StringBuilder text) {
        this.serializer.text(text);
    }

    @Override
    public void cdata(StringBuilder text) {
        this.serializer.cdata(text);
    }

    @Override
    public void comment(StringBuilder comment) {
        this.serializer.comment(comment);
    }

    @Override
    public void endDocument() {
        this.serializer.endDocument();
    }

    @Override
    public void flush() {
        this.serializer.flush();
        try {
            this.writer.flush();
        }
        catch (IOException e) {
            throw new TxwException(e);
        }
    }

    private static XMLWriter createWriter(Writer w) {
        DataWriter dw = new DataWriter(new BufferedWriter(w));
        dw.setIndentStep("  ");
        return dw;
    }

    private static XMLWriter createWriter(OutputStream os, String encoding) throws UnsupportedEncodingException {
        XMLWriter writer = StreamSerializer.createWriter(new OutputStreamWriter(os, encoding));
        writer.setEncoding(encoding);
        return writer;
    }

    private static XMLWriter createWriter(OutputStream os) {
        try {
            return StreamSerializer.createWriter(os, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
}

