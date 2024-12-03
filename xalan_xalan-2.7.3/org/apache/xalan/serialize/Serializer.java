/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.apache.xalan.serialize.DOMSerializer;
import org.xml.sax.ContentHandler;

public interface Serializer {
    public void setOutputStream(OutputStream var1);

    public OutputStream getOutputStream();

    public void setWriter(Writer var1);

    public Writer getWriter();

    public void setOutputFormat(Properties var1);

    public Properties getOutputFormat();

    public ContentHandler asContentHandler() throws IOException;

    public DOMSerializer asDOMSerializer() throws IOException;

    public boolean reset();
}

