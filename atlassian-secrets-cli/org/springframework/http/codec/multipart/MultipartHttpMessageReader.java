/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MultipartHttpMessageReader
implements HttpMessageReader<MultiValueMap<String, Part>> {
    private static final ResolvableType MULTIPART_VALUE_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
    private final HttpMessageReader<Part> partReader;

    public MultipartHttpMessageReader(HttpMessageReader<Part> partReader) {
        Assert.notNull(partReader, "'partReader' is required");
        this.partReader = partReader;
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return MULTIPART_VALUE_TYPE.isAssignableFrom(elementType) && (mediaType == null || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType));
    }

    @Override
    public Flux<MultiValueMap<String, Part>> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.from(this.readMono(elementType, message, hints));
    }

    @Override
    public Mono<MultiValueMap<String, Part>> readMono(ResolvableType elementType, ReactiveHttpInputMessage inputMessage, Map<String, Object> hints) {
        return this.partReader.read(elementType, inputMessage, hints).collectMultimap(Part::name).map(this::toMultiValueMap);
    }

    private LinkedMultiValueMap<String, Part> toMultiValueMap(Map<String, Collection<Part>> map) {
        return new LinkedMultiValueMap<String, Part>(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> this.toList((Collection)e.getValue()))));
    }

    private List<Part> toList(Collection<Part> collection) {
        return collection instanceof List ? (List<Object>)collection : new ArrayList<Part>(collection);
    }
}

