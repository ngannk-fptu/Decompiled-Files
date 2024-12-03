/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.net.MediaType
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.webhooks.internal.history;

import com.google.common.net.MediaType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class BodyUtils {
    public static boolean isTextContent(String contentType) {
        try {
            MediaType mediaType = StringUtils.isBlank((CharSequence)contentType) ? null : MediaType.parse((String)contentType);
            return mediaType == null || mediaType.is(MediaType.ANY_TEXT_TYPE) || mediaType.is(MediaType.JSON_UTF_8.withoutParameters());
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Nonnull
    public static Charset getCharset(String contentType) {
        try {
            return Optional.ofNullable(contentType).map(MediaType::parse).flatMap(mediaType -> Optional.ofNullable(mediaType.charset().orNull())).orElse(StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException e) {
            return StandardCharsets.UTF_8;
        }
    }
}

