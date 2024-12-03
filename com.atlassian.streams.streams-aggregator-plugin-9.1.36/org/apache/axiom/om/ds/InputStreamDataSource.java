/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.ds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.ds.OMDataSourceExtBase;
import org.apache.axiom.om.util.StAXUtils;

public class InputStreamDataSource
extends OMDataSourceExtBase {
    Data data = new Data();
    private static final int BUFFER_LEN = 4096;

    public InputStreamDataSource(InputStream is, String encoding) {
        this.data.is = is;
        this.data.encoding = encoding;
    }

    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        String encoding = format.getCharSetEncoding();
        try {
            if (!this.data.encoding.equalsIgnoreCase(encoding)) {
                byte[] bytes = this.getXMLBytes(encoding);
                output.write(bytes);
            } else {
                InputStreamDataSource.inputStream2OutputStream(this.data.is, output);
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        super.serialize(xmlWriter);
    }

    public XMLStreamReader getReader() throws XMLStreamException {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return StAXUtils.createXMLStreamReader(this.data.is, this.data.encoding);
    }

    public InputStream getXMLInputStream(String encoding) throws UnsupportedEncodingException {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return this.data.is;
    }

    public Object getObject() {
        return this.data;
    }

    public boolean isDestructiveRead() {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return true;
    }

    public boolean isDestructiveWrite() {
        if (this.data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return true;
    }

    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding(encoding);
        try {
            this.serialize(baos, format);
        }
        catch (XMLStreamException e) {
            throw new OMException(e);
        }
        return baos.toByteArray();
    }

    public void close() {
        if (this.data.is != null) {
            try {
                this.data.is.close();
            }
            catch (IOException e) {
                throw new OMException(e);
            }
            this.data.is = null;
        }
    }

    public OMDataSourceExt copy() {
        byte[] bytes;
        try {
            bytes = this.getXMLBytes(this.data.encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new OMException(e);
        }
        ByteArrayInputStream is1 = new ByteArrayInputStream(bytes);
        ByteArrayInputStream is2 = new ByteArrayInputStream(bytes);
        this.data.is = is1;
        return new InputStreamDataSource(is2, this.data.encoding);
    }

    private static void inputStream2OutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead = is.read(buffer);
        while (bytesRead > 0) {
            os.write(buffer, 0, bytesRead);
            bytesRead = is.read(buffer);
        }
    }

    public class Data {
        public String encoding;
        public InputStream is;
    }
}

