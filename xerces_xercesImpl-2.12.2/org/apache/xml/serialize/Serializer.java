/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;

public interface Serializer {
    public void setOutputByteStream(OutputStream var1);

    public void setOutputCharStream(Writer var1);

    public void setOutputFormat(OutputFormat var1);

    public DocumentHandler asDocumentHandler() throws IOException;

    public ContentHandler asContentHandler() throws IOException;

    public DOMSerializer asDOMSerializer() throws IOException;
}

