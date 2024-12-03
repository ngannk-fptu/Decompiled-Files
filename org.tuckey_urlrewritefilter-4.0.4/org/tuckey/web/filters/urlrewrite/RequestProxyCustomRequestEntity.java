/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.httpclient.methods.RequestEntity
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.httpclient.methods.RequestEntity;

class RequestProxyCustomRequestEntity
implements RequestEntity {
    private InputStream is = null;
    private long contentLength = 0L;
    private String contentType;

    public RequestProxyCustomRequestEntity(InputStream is, long contentLength, String contentType) {
        this.is = is;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    public boolean isRepeatable() {
        return true;
    }

    public String getContentType() {
        return this.contentType;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeRequest(OutputStream out) throws IOException {
        try {
            int l;
            byte[] buffer = new byte[10240];
            while ((l = this.is.read(buffer)) != -1) {
                out.write(buffer, 0, l);
            }
        }
        finally {
            this.is.close();
        }
    }

    public long getContentLength() {
        return this.contentLength;
    }
}

