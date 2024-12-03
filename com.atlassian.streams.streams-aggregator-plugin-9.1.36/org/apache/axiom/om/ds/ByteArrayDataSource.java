/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.ds;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.ds.OMDataSourceExtBase;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ByteArrayDataSource
extends OMDataSourceExtBase {
    private static final Log log = LogFactory.getLog(ByteArrayDataSource.class);
    ByteArray byteArray = new ByteArray();

    public ByteArrayDataSource(byte[] bytes, String encoding) {
        this.byteArray.bytes = bytes;
        this.byteArray.encoding = encoding;
    }

    public XMLStreamReader getReader() throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"getReader");
        }
        return StAXUtils.createXMLStreamReader(new ByteArrayInputStream(this.byteArray.bytes), this.byteArray.encoding);
    }

    public Object getObject() {
        return this.byteArray;
    }

    public boolean isDestructiveRead() {
        return false;
    }

    public boolean isDestructiveWrite() {
        return false;
    }

    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        if (encoding == null) {
            encoding = "utf-8";
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("getXMLBytes encoding=" + encoding));
        }
        if (!this.byteArray.encoding.equalsIgnoreCase(encoding)) {
            String text = new String(this.byteArray.bytes, this.byteArray.encoding);
            this.byteArray.bytes = text.getBytes(encoding);
            this.byteArray.encoding = encoding;
        }
        return this.byteArray.bytes;
    }

    public void close() {
        this.byteArray = null;
    }

    public OMDataSourceExt copy() {
        return new ByteArrayDataSource(this.byteArray.bytes, this.byteArray.encoding);
    }

    public class ByteArray {
        public byte[] bytes;
        public String encoding;
    }
}

