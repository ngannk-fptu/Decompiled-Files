/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlinx.serialization.KSerializer
 *  kotlinx.serialization.SerializationException
 *  kotlinx.serialization.SerializersKt
 *  kotlinx.serialization.descriptors.PolymorphicKind$OPEN
 *  kotlinx.serialization.descriptors.SerialDescriptor
 *  kotlinx.serialization.json.Json
 */
package org.springframework.http.converter.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerializationException;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.descriptors.PolymorphicKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.json.Json;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StreamUtils;

public class KotlinSerializationJsonHttpMessageConverter
extends AbstractGenericHttpMessageConverter<Object> {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Map<Type, KSerializer<Object>> serializerCache = new ConcurrentReferenceHashMap<Type, KSerializer<Object>>();
    private final Json json;

    public KotlinSerializationJsonHttpMessageConverter() {
        this((Json)Json.Default);
    }

    public KotlinSerializationJsonHttpMessageConverter(Json json) {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        this.json = json;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        try {
            this.serializer(clazz);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        try {
            this.serializer(GenericTypeResolver.resolveType(type, contextClass));
            return this.canRead(mediaType);
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType) {
        try {
            this.serializer(type != null ? GenericTypeResolver.resolveType(type, clazz) : clazz);
            return this.canWrite(mediaType);
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public final Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return this.decode(this.serializer(GenericTypeResolver.resolveType(type, contextClass)), inputMessage);
    }

    @Override
    protected final Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return this.decode(this.serializer(clazz), inputMessage);
    }

    private Object decode(KSerializer<Object> serializer, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        String jsonText = StreamUtils.copyToString(inputMessage.getBody(), this.getCharsetToUse(contentType));
        try {
            return this.json.decodeFromString(serializer, jsonText);
        }
        catch (SerializationException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex, inputMessage);
        }
    }

    @Override
    protected final void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.encode(object, this.serializer(type != null ? type : object.getClass()), outputMessage);
    }

    private void encode(Object object, KSerializer<Object> serializer, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            String json = this.json.encodeToString(serializer, object);
            MediaType contentType = outputMessage.getHeaders().getContentType();
            outputMessage.getBody().write(json.getBytes(this.getCharsetToUse(contentType)));
            outputMessage.getBody().flush();
        }
        catch (IOException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    private Charset getCharsetToUse(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        return DEFAULT_CHARSET;
    }

    private KSerializer<Object> serializer(Type type) {
        KSerializer serializer = serializerCache.get(type);
        if (serializer == null) {
            serializer = SerializersKt.serializer((Type)type);
            if (this.hasPolymorphism(serializer.getDescriptor(), new HashSet<String>())) {
                throw new UnsupportedOperationException("Open polymorphic serialization is not supported yet");
            }
            serializerCache.put(type, (KSerializer<Object>)serializer);
        }
        return serializer;
    }

    private boolean hasPolymorphism(SerialDescriptor descriptor, Set<String> alreadyProcessed) {
        alreadyProcessed.add(descriptor.getSerialName());
        if (descriptor.getKind().equals(PolymorphicKind.OPEN.INSTANCE)) {
            return true;
        }
        for (int i2 = 0; i2 < descriptor.getElementsCount(); ++i2) {
            SerialDescriptor elementDescriptor = descriptor.getElementDescriptor(i2);
            if (alreadyProcessed.contains(elementDescriptor.getSerialName()) || !this.hasPolymorphism(elementDescriptor, alreadyProcessed)) continue;
            return true;
        }
        return false;
    }
}

