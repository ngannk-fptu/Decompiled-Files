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
 *  org.springframework.core.codec.AbstractDecoder
 *  org.springframework.core.codec.StringDecoder
 *  org.springframework.core.io.buffer.DataBuffer
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
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class KotlinSerializationJsonDecoder
extends AbstractDecoder<Object> {
    private static final Map<Type, KSerializer<Object>> serializerCache = new ConcurrentReferenceHashMap();
    private final Json json;
    private final StringDecoder stringDecoder = StringDecoder.allMimeTypes((List)StringDecoder.DEFAULT_DELIMITERS, (boolean)false);

    public KotlinSerializationJsonDecoder() {
        this((Json)Json.Default);
    }

    public KotlinSerializationJsonDecoder(Json json) {
        super(new MimeType[]{MediaType.APPLICATION_JSON, new MediaType("application", "*+json")});
        this.json = json;
    }

    public void setMaxInMemorySize(int byteCount) {
        this.stringDecoder.setMaxInMemorySize(byteCount);
    }

    public int getMaxInMemorySize() {
        return this.stringDecoder.getMaxInMemorySize();
    }

    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        try {
            this.serializer(elementType.getType());
            return super.canDecode(elementType, mimeType) && !CharSequence.class.isAssignableFrom(elementType.toClass());
        }
        catch (Exception ex) {
            return false;
        }
    }

    public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.error((Throwable)new UnsupportedOperationException());
    }

    public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return this.stringDecoder.decodeToMono(inputStream, elementType, mimeType, hints).map(jsonText -> this.json.decodeFromString(this.serializer(elementType.getType()), jsonText));
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

