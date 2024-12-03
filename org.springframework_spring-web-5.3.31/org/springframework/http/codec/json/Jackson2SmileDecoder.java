/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.dataformat.smile.SmileFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.MimeType
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;

public class Jackson2SmileDecoder
extends AbstractJackson2Decoder {
    private static final MimeType[] DEFAULT_SMILE_MIME_TYPES = new MimeType[]{new MimeType("application", "x-jackson-smile"), new MimeType("application", "*+x-jackson-smile")};

    public Jackson2SmileDecoder() {
        this((ObjectMapper)Jackson2ObjectMapperBuilder.smile().build(), DEFAULT_SMILE_MIME_TYPES);
    }

    public Jackson2SmileDecoder(ObjectMapper mapper, MimeType ... mimeTypes) {
        super(mapper, mimeTypes);
        Assert.isAssignable(SmileFactory.class, mapper.getFactory().getClass());
    }
}

