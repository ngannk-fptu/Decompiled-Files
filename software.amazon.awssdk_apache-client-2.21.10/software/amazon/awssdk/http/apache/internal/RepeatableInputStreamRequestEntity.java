/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.entity.BasicHttpEntity
 *  org.apache.http.entity.InputStreamEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.HttpExecuteRequest
 */
package software.amazon.awssdk.http.apache.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.HttpExecuteRequest;

@SdkInternalApi
public class RepeatableInputStreamRequestEntity
extends BasicHttpEntity {
    private static final Logger log = LoggerFactory.getLogger(RepeatableInputStreamRequestEntity.class);
    private boolean firstAttempt = true;
    private boolean isChunked;
    private InputStreamEntity inputStreamRequestEntity;
    private InputStream content;
    private IOException originalException;

    public RepeatableInputStreamRequestEntity(HttpExecuteRequest request) {
        this.isChunked = request.httpRequest().matchingHeaders("Transfer-Encoding").contains("chunked");
        this.setChunked(this.isChunked);
        long contentLength = request.httpRequest().firstMatchingHeader("Content-Length").map(this::parseContentLength).orElse(-1L);
        this.content = this.getContent(request.contentStreamProvider());
        this.inputStreamRequestEntity = new InputStreamEntity(this.content, contentLength);
        this.setContent(this.content);
        this.setContentLength(contentLength);
        request.httpRequest().firstMatchingHeader("Content-Type").ifPresent(contentType -> {
            this.inputStreamRequestEntity.setContentType(contentType);
            this.setContentType((String)contentType);
        });
    }

    private long parseContentLength(String contentLength) {
        try {
            return Long.parseLong(contentLength);
        }
        catch (NumberFormatException nfe) {
            log.warn("Unable to parse content length from request. Buffering contents in memory.");
            return -1L;
        }
    }

    private InputStream getContent(Optional<ContentStreamProvider> contentStreamProvider) {
        return contentStreamProvider.map(ContentStreamProvider::newStream).orElseGet(() -> new ByteArrayInputStream(new byte[0]));
    }

    public boolean isChunked() {
        return this.isChunked;
    }

    public boolean isRepeatable() {
        return this.content.markSupported() || this.inputStreamRequestEntity.isRepeatable();
    }

    public void writeTo(OutputStream output) throws IOException {
        try {
            if (!this.firstAttempt && this.isRepeatable()) {
                this.content.reset();
            }
            this.firstAttempt = false;
            this.inputStreamRequestEntity.writeTo(output);
        }
        catch (IOException ioe) {
            if (this.originalException == null) {
                this.originalException = ioe;
            }
            throw this.originalException;
        }
    }
}

