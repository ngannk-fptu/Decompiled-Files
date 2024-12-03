/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMWriterOptions;
import org.apache.abdera.util.AbstractWriter;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.WriterOptions;

public class FOMWriter
extends AbstractWriter
implements NamedWriter {
    public FOMWriter() {
    }

    public FOMWriter(Abdera abdera) {
    }

    public void writeTo(Base base, OutputStream out, WriterOptions options) throws IOException {
        out = this.getCompressedOutputStream(out, options);
        String charset = options.getCharset();
        if (charset == null) {
            Document doc;
            if (base instanceof Document) {
                charset = ((Document)base).getCharset();
            } else if (base instanceof Element && (doc = ((Element)base).getDocument()) != null) {
                charset = doc.getCharset();
            }
            if (charset == null) {
                charset = "UTF-8";
            }
        } else {
            Document doc = null;
            if (base instanceof Document) {
                doc = (Document)base;
            } else if (base instanceof Element) {
                doc = ((Element)base).getDocument();
            }
            if (doc != null) {
                doc.setCharset(charset);
            }
        }
        base.writeTo(new OutputStreamWriter(out, charset));
        this.finishCompressedOutputStream(out, options);
        if (options.getAutoClose()) {
            out.close();
        }
    }

    public void writeTo(Base base, Writer out, WriterOptions options) throws IOException {
        base.writeTo(out);
        if (options.getAutoClose()) {
            out.close();
        }
    }

    public Object write(Base base, WriterOptions options) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.writeTo(base, out, options);
        return out.toString();
    }

    public String getName() {
        return "default";
    }

    public String[] getOutputFormats() {
        return new String[]{"application/atom+xml", "application/atomsvc+xml", "application/atomcat+xml", "application/xml"};
    }

    public boolean outputsFormat(String mediatype) {
        return MimeTypeHelper.isMatch(mediatype, "application/atom+xml") || MimeTypeHelper.isMatch(mediatype, "application/atomsvc+xml") || MimeTypeHelper.isMatch(mediatype, "application/atomcat+xml") || MimeTypeHelper.isMatch(mediatype, "application/xml");
    }

    protected WriterOptions initDefaultWriterOptions() {
        return new FOMWriterOptions();
    }
}

