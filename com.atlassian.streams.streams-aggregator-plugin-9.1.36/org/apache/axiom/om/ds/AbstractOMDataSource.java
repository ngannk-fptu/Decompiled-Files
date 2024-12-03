/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.ds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.util.StAXUtils;

public abstract class AbstractOMDataSource
implements OMDataSourceExt {
    private HashMap properties = null;

    public final Object getProperty(String key) {
        return this.properties == null ? null : this.properties.get(key);
    }

    public final boolean hasProperty(String key) {
        return this.properties != null && this.properties.containsKey(key);
    }

    public final Object setProperty(String key, Object value) {
        if (this.properties == null) {
            this.properties = new HashMap();
        }
        return this.properties.put(key, value);
    }

    public final void serialize(OutputStream out, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(out, format);
        this.serialize(writer);
        writer.flush();
    }

    public final void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter xmlWriter = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer));
        xmlWriter.setOutputFormat(format);
        this.serialize(xmlWriter);
        xmlWriter.flush();
    }

    public final byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding(encoding);
        try {
            this.serialize(baos, format);
        }
        catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
        return baos.toByteArray();
    }

    public final InputStream getXMLInputStream(String encoding) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(this.getXMLBytes(encoding));
    }

    public Object getObject() {
        return null;
    }

    public void close() {
    }

    public OMDataSourceExt copy() {
        return null;
    }
}

