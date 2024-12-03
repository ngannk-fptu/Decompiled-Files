/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.api.EntityBuilder;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;

public class MultiPartEntityBuilder
implements EntityBuilder {
    private final HttpEntity apacheMultipartEntity;

    @Deprecated
    public MultiPartEntityBuilder(MultipartEntity multipartEntity) {
        this.apacheMultipartEntity = multipartEntity;
    }

    public MultiPartEntityBuilder(HttpEntity multipartEntity) {
        this.apacheMultipartEntity = multipartEntity;
    }

    @Override
    public EntityBuilder.Entity build() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            this.apacheMultipartEntity.writeTo(outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Header header = this.apacheMultipartEntity.getContentType();
            HashMap headers = Maps.newHashMap();
            headers.put(header.getName(), header.getValue());
            return new MultiPartEntity(headers, inputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class MultiPartEntity
    implements EntityBuilder.Entity {
        private final Map<String, String> headers;
        private final InputStream inputStream;

        public MultiPartEntity(Map<String, String> headers, InputStream inputStream) {
            this.headers = headers;
            this.inputStream = inputStream;
        }

        @Override
        public Map<String, String> getHeaders() {
            return this.headers;
        }

        @Override
        public InputStream getInputStream() {
            return this.inputStream;
        }
    }
}

