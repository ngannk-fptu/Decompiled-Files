/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpException
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpResponseInterceptor
 *  org.apache.http.entity.HttpEntityWrapper
 *  org.apache.http.protocol.HttpContext
 */
package com.amazonaws.http.apache.client.impl;

import com.amazonaws.util.CRC32ChecksumCalculatingInputStream;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HttpContext;

public class CRC32ChecksumResponseInterceptor
implements HttpResponseInterceptor {
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpEntity entity = response.getEntity();
        Header[] headers = response.getHeaders("x-amz-crc32");
        if (entity == null || headers == null || headers.length == 0) {
            return;
        }
        HttpEntityWrapper crc32ResponseEntity = new HttpEntityWrapper(entity){
            private final InputStream content;
            {
                this.content = new CRC32ChecksumCalculatingInputStream(this.wrappedEntity.getContent());
            }

            public InputStream getContent() throws IOException {
                return this.content;
            }

            public void writeTo(OutputStream outstream) throws IOException {
                try {
                    IOUtils.copy(this.getContent(), outstream);
                }
                finally {
                    this.getContent().close();
                }
            }
        };
        response.setEntity((HttpEntity)crc32ResponseEntity);
        context.setAttribute(CRC32ChecksumCalculatingInputStream.class.getName(), (Object)crc32ResponseEntity.getContent());
    }
}

