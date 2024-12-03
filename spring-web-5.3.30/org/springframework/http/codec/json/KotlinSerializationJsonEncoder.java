/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlinx.serialization.KSerializer
 *  kotlinx.serialization.SerializersKt
 *  kotlinx.serialization.descriptors.PolymorphicKind$OPEN
 *  kotlinx.serialization.descriptors.SerialDescriptor
 *  kotlinx.serialization.json.Json
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.AbstractEncoder
 *  org.springframework.core.codec.CharSequenceEncoder
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.MimeType
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.json;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.descriptors.PolymorphicKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.json.Json;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class KotlinSerializationJsonEncoder
extends AbstractEncoder<Object> {
    private static final Map<Type, KSerializer<Object>> serializerCache = new ConcurrentReferenceHashMap();
    private final Json json;
    private final CharSequenceEncoder charSequenceEncoder = CharSequenceEncoder.allMimeTypes();

    public KotlinSerializationJsonEncoder() {
        this((Json)Json.Default);
    }

    public KotlinSerializationJsonEncoder(Json json) {
        super(new MimeType[]{MediaType.APPLICATION_JSON, new MediaType("application", "*+json")});
        this.json = json;
    }

    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        try {
            this.serializer(elementType.getType());
            return super.canEncode(elementType, mimeType) && !String.class.isAssignableFrom(elementType.toClass()) && !ServerSentEvent.class.isAssignableFrom(elementType.toClass());
        }
        catch (Exception ex) {
            return false;
        }
    }

    public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (inputStream instanceof Mono) {
            return Mono.from(inputStream).map(value -> this.encodeValue(value, bufferFactory, elementType, mimeType, hints)).flux();
        }
        ResolvableType listType = ResolvableType.forClassWithGenerics(List.class, (ResolvableType[])new ResolvableType[]{elementType});
        return Flux.from(inputStream).collectList().map(list -> this.encodeValue(list, bufferFactory, listType, mimeType, hints)).flux();
    }

    public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        String json = this.json.encodeToString(this.serializer(valueType.getType()), value);
        return this.charSequenceEncoder.encodeValue((CharSequence)json, bufferFactory, valueType, mimeType, null);
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
        for (int i = 0; i < descriptor.getElementsCount(); ++i) {
            SerialDescriptor elementDescriptor = descriptor.getElementDescriptor(i);
            if (alreadyProcessed.contains(elementDescriptor.getSerialName()) || !this.hasPolymorphism(elementDescriptor, alreadyProcessed)) continue;
            return true;
        }
        return false;
    }
}

