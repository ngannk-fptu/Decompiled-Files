/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serialize.OutputFormat
 */
package com.atlassian.xhtml.serialize;

import com.atlassian.xhtml.serialize.BugFixedHTMLSerializer;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.xml.serialize.OutputFormat;

public class BugFixedXHTMLSerializer
extends BugFixedHTMLSerializer {
    public BugFixedXHTMLSerializer() {
        super(true, new OutputFormat("xhtml", null, false));
    }

    public BugFixedXHTMLSerializer(OutputFormat format) {
        super(true, format != null ? format : new OutputFormat("xhtml", null, false));
    }

    public BugFixedXHTMLSerializer(Writer writer, OutputFormat format) {
        super(true, format != null ? format : new OutputFormat("xhtml", null, false));
        this.setOutputCharStream(writer);
    }

    public BugFixedXHTMLSerializer(OutputStream output, OutputFormat format) {
        super(true, format != null ? format : new OutputFormat("xhtml", null, false));
        this.setOutputByteStream(output);
    }

    public void setOutputFormat(OutputFormat format) {
        super.setOutputFormat(format != null ? format : new OutputFormat("xhtml", null, false));
    }
}

