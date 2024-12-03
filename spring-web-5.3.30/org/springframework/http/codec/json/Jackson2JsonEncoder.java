/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.PrettyPrinter
 *  com.fasterxml.jackson.core.util.DefaultIndenter
 *  com.fasterxml.jackson.core.util.DefaultPrettyPrinter
 *  com.fasterxml.jackson.core.util.DefaultPrettyPrinter$Indenter
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ObjectWriter
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.MimeType
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Arrays;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Encoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

public class Jackson2JsonEncoder
extends AbstractJackson2Encoder {
    @Nullable
    private final PrettyPrinter ssePrettyPrinter;

    public Jackson2JsonEncoder() {
        this((ObjectMapper)Jackson2ObjectMapperBuilder.json().build(), new MimeType[0]);
    }

    public Jackson2JsonEncoder(ObjectMapper mapper, MimeType ... mimeTypes) {
        super(mapper, mimeTypes);
        this.setStreamingMediaTypes(Arrays.asList(MediaType.APPLICATION_NDJSON, MediaType.APPLICATION_STREAM_JSON));
        this.ssePrettyPrinter = Jackson2JsonEncoder.initSsePrettyPrinter();
    }

    private static PrettyPrinter initSsePrettyPrinter() {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith((DefaultPrettyPrinter.Indenter)new DefaultIndenter("  ", "\ndata:"));
        return printer;
    }

    @Override
    protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType, ResolvableType elementType, @Nullable Map<String, Object> hints) {
        return this.ssePrettyPrinter != null && MediaType.TEXT_EVENT_STREAM.isCompatibleWith(mimeType) && writer.getConfig().isEnabled(SerializationFeature.INDENT_OUTPUT) ? writer.with(this.ssePrettyPrinter) : writer;
    }
}

