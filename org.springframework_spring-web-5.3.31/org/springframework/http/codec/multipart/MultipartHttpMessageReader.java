/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Hints
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MultipartHttpMessageReader
extends LoggingCodecSupport
implements HttpMessageReader<MultiValueMap<String, Part>> {
    private static final ResolvableType MULTIPART_VALUE_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, (Class[])new Class[]{String.class, Part.class});
    static final List<MediaType> MIME_TYPES = Collections.unmodifiableList(Arrays.asList(MediaType.MULTIPART_FORM_DATA, MediaType.MULTIPART_MIXED, MediaType.MULTIPART_RELATED));
    private final HttpMessageReader<Part> partReader;

    public MultipartHttpMessageReader(HttpMessageReader<Part> partReader) {
        Assert.notNull(partReader, (String)"'partReader' is required");
        this.partReader = partReader;
    }

    public HttpMessageReader<Part> getPartReader() {
        return this.partReader;
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return MIME_TYPES;
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        if (MULTIPART_VALUE_TYPE.isAssignableFrom(elementType)) {
            if (mediaType == null) {
                return true;
            }
            for (MediaType supportedMediaType : MIME_TYPES) {
                if (!supportedMediaType.isCompatibleWith(mediaType)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public Flux<MultiValueMap<String, Part>> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.from(this.readMono(elementType, message, hints));
    }

    @Override
    public Mono<MultiValueMap<String, Part>> readMono(ResolvableType elementType, ReactiveHttpInputMessage inputMessage, Map<String, Object> hints) {
        Map allHints = Hints.merge(hints, (String)Hints.SUPPRESS_LOGGING_HINT, (Object)true);
        return this.partReader.read(elementType, inputMessage, allHints).collectMultimap(Part::name).doOnNext(map -> LogFormatUtils.traceDebug((Log)this.logger, traceOn -> Hints.getLogPrefix((Map)hints) + "Parsed " + (this.isEnableLoggingRequestDetails() ? LogFormatUtils.formatValue((Object)map, (traceOn == false ? 1 : 0) != 0) : "parts " + map.keySet() + " (content masked)"))).map(this::toMultiValueMap);
    }

    private LinkedMultiValueMap<String, Part> toMultiValueMap(Map<String, Collection<Part>> map) {
        return new LinkedMultiValueMap(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> this.toList((Collection)e.getValue()))));
    }

    private List<Part> toList(Collection<Part> collection) {
        return collection instanceof List ? (List<Object>)collection : new ArrayList<Part>(collection);
    }
}

