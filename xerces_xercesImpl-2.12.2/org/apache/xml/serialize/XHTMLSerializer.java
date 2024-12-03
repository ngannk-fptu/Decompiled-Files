/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.OutputStream;
import java.io.Writer;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;

public class XHTMLSerializer
extends HTMLSerializer {
    public XHTMLSerializer() {
        super(true, new OutputFormat("xhtml", null, false));
    }

    public XHTMLSerializer(OutputFormat outputFormat) {
        super(true, outputFormat != null ? outputFormat : new OutputFormat("xhtml", null, false));
    }

    public XHTMLSerializer(Writer writer, OutputFormat outputFormat) {
        super(true, outputFormat != null ? outputFormat : new OutputFormat("xhtml", null, false));
        this.setOutputCharStream(writer);
    }

    public XHTMLSerializer(OutputStream outputStream, OutputFormat outputFormat) {
        super(true, outputFormat != null ? outputFormat : new OutputFormat("xhtml", null, false));
        this.setOutputByteStream(outputStream);
    }

    @Override
    public void setOutputFormat(OutputFormat outputFormat) {
        super.setOutputFormat(outputFormat != null ? outputFormat : new OutputFormat("xhtml", null, false));
    }
}

