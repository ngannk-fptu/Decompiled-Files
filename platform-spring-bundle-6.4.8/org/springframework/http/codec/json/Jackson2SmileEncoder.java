/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.dataformat.smile.SmileFactory
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import java.util.Collections;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Encoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;

public class Jackson2SmileEncoder
extends AbstractJackson2Encoder {
    private static final MimeType[] DEFAULT_SMILE_MIME_TYPES = new MimeType[]{new MimeType("application", "x-jackson-smile"), new MimeType("application", "*+x-jackson-smile")};
    private static final byte[] STREAM_SEPARATOR = new byte[0];

    public Jackson2SmileEncoder() {
        this((ObjectMapper)Jackson2ObjectMapperBuilder.smile().build(), DEFAULT_SMILE_MIME_TYPES);
    }

    public Jackson2SmileEncoder(ObjectMapper mapper, MimeType ... mimeTypes) {
        super(mapper, mimeTypes);
        Assert.isAssignable(SmileFactory.class, mapper.getFactory().getClass());
        this.setStreamingMediaTypes(Collections.singletonList(new MediaType("application", "stream+x-jackson-smile")));
    }

    @Override
    @Nullable
    protected byte[] getStreamingMediaTypeSeparator(@Nullable MimeType mimeType) {
        for (MediaType streamingMediaType : this.getStreamingMediaTypes()) {
            if (!streamingMediaType.isCompatibleWith(mimeType)) continue;
            return STREAM_SEPARATOR;
        }
        return null;
    }
}

