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
import java.util.Random;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultipartRequestEntity
implements RequestEntity {
    private static final Log log = LogFactory.getLog(MultipartRequestEntity.class);
    private static final String MULTIPART_FORM_CONTENT_TYPE = "multipart/form-data";
    private static byte[] MULTIPART_CHARS = EncodingUtil.getAsciiBytes("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    protected Part[] parts;
    private byte[] multipartBoundary;
    private HttpMethodParams params;

    private static byte[] generateMultipartBoundary() {
        Random rand = new Random();
        byte[] bytes = new byte[rand.nextInt(11) + 30];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)];
        }
        return bytes;
    }

    public MultipartRequestEntity(Part[] parts, HttpMethodParams params) {
        if (parts == null) {
            throw new IllegalArgumentException("parts cannot be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("params cannot be null");
        }
        this.parts = parts;
        this.params = params;
    }

    protected byte[] getMultipartBoundary() {
        if (this.multipartBoundary == null) {
            String temp = (String)this.params.getParameter("http.method.multipart.boundary");
            this.multipartBoundary = temp != null ? EncodingUtil.getAsciiBytes(temp) : MultipartRequestEntity.generateMultipartBoundary();
        }
        return this.multipartBoundary;
    }

    @Override
    public boolean isRepeatable() {
        for (int i = 0; i < this.parts.length; ++i) {
            if (this.parts[i].isRepeatable()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void writeRequest(OutputStream out) throws IOException {
        Part.sendParts(out, this.parts, this.getMultipartBoundary());
    }

    @Override
    public long getContentLength() {
        try {
            return Part.getLengthOfParts(this.parts, this.getMultipartBoundary());
        }
        catch (Exception e) {
            log.error((Object)"An exception occurred while getting the length of the parts", (Throwable)e);
            return 0L;
        }
    }

    @Override
    public String getContentType() {
        StringBuffer buffer = new StringBuffer(MULTIPART_FORM_CONTENT_TYPE);
        buffer.append("; boundary=");
        buffer.append(EncodingUtil.getAsciiString(this.getMultipartBoundary()));
        return buffer.toString();
    }
}

