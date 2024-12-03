/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.http.entity.ContentType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.common.lang.StringTruncator;
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import com.atlassian.applinks.internal.status.error.ResponseApplinkError;
import com.atlassian.sal.api.net.Response;
import com.google.common.base.Strings;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResponseApplinkError
implements ResponseApplinkError {
    private static final Logger log = LoggerFactory.getLogger(AbstractResponseApplinkError.class);
    public static final ContentType FALLBACK_CONTENT_TYPE = ContentType.create((String)"text/plain", (Charset)StandardCharsets.UTF_8);
    public static final int MAX_RESPONSE_SIZE = 500;
    private final int statusCode;
    private final String body;
    private final String contentType;

    public AbstractResponseApplinkError(@Nonnull Response response) {
        this.statusCode = response.getStatusCode();
        this.body = Strings.emptyToNull((String)this.getTruncatedBody(response));
        this.contentType = this.body != null ? AbstractResponseApplinkError.getContentType(response).getMimeType() : null;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    @Nullable
    public String getBody() {
        return this.body;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    @Nullable
    public <T> T accept(@Nonnull ApplinkErrorVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private String getTruncatedBody(@Nonnull Response response) {
        try (InputStreamReader contents = new InputStreamReader(response.getResponseBodyAsStream(), AbstractResponseApplinkError.getContentEncoding(response));){
            String string = StringTruncator.forInput(contents).maxLines(500).maxCharsInLine(500).truncate();
            return string;
        }
        catch (Exception e) {
            log.debug("Could not retrieve response body", (Throwable)e);
            return "<Could not retrieve response body>";
        }
    }

    private static ContentType getContentType(Response response) {
        try {
            String contentTypeValue = response.getHeader("Content-Type");
            if (contentTypeValue != null) {
                return ContentType.parse((String)contentTypeValue);
            }
            return FALLBACK_CONTENT_TYPE;
        }
        catch (Exception ignored) {
            return FALLBACK_CONTENT_TYPE;
        }
    }

    private static Charset getContentEncoding(Response response) {
        Charset c = AbstractResponseApplinkError.getContentType(response).getCharset();
        return c == null ? FALLBACK_CONTENT_TYPE.getCharset() : c;
    }
}

