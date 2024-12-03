/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.httpclient.api.Response
 *  com.atlassian.webhooks.request.WebhookResponseBody
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.input.BoundedInputStream
 */
package com.atlassian.webhooks.internal.client.request;

import com.atlassian.httpclient.api.Response;
import com.atlassian.webhooks.request.WebhookResponseBody;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.commons.io.input.BoundedInputStream;

public class AtlassianHttpResponseBody
implements WebhookResponseBody {
    private static final InputStream EMPTY = new ByteArrayInputStream(new byte[0]);
    private final String contentType;
    private final long maxBytes;
    private final Response result;

    public AtlassianHttpResponseBody(@Nonnull Response response, long maxBytes) {
        this.result = Objects.requireNonNull(response, "response");
        this.contentType = response.getContentType();
        this.maxBytes = maxBytes;
    }

    @Nonnull
    public InputStream getContent() throws IOException {
        InputStream content = this.result.getEntityStream();
        return content == null ? EMPTY : new BoundedInputStream(content, this.maxBytes);
    }

    @Nonnull
    public Optional<String> getContentType() {
        return Optional.ofNullable(this.contentType);
    }
}

