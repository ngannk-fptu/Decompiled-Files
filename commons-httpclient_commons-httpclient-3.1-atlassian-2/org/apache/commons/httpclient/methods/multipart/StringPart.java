/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods.multipart;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringPart
extends PartBase {
    private static final Log LOG = LogFactory.getLog(StringPart.class);
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";
    public static final String DEFAULT_CHARSET = "US-ASCII";
    public static final String DEFAULT_TRANSFER_ENCODING = "8bit";
    private byte[] content;
    private String value;

    public StringPart(String name, String value, String charset) {
        super(name, DEFAULT_CONTENT_TYPE, charset == null ? DEFAULT_CHARSET : charset, DEFAULT_TRANSFER_ENCODING);
        if (value == null) {
            throw new IllegalArgumentException("Value may not be null");
        }
        if (value.indexOf(0) != -1) {
            throw new IllegalArgumentException("NULs may not be present in string parts");
        }
        this.value = value;
    }

    public StringPart(String name, String value) {
        this(name, value, null);
    }

    private byte[] getContent() {
        if (this.content == null) {
            this.content = EncodingUtil.getBytes(this.value, this.getCharSet());
        }
        return this.content;
    }

    @Override
    protected void sendData(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendData(OutputStream)");
        out.write(this.getContent());
    }

    @Override
    protected long lengthOfData() throws IOException {
        LOG.trace((Object)"enter lengthOfData()");
        return this.getContent().length;
    }

    @Override
    public void setCharSet(String charSet) {
        super.setCharSet(charSet);
        this.content = null;
    }
}

