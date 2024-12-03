/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.bind.support;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class WebExchangeDataBinder
extends WebDataBinder {
    public WebExchangeDataBinder(@Nullable Object target) {
        super(target);
    }

    public WebExchangeDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public Mono<Void> bind(ServerWebExchange exchange2) {
        return this.getValuesToBind(exchange2).doOnNext(values -> this.doBind(new MutablePropertyValues(values))).then();
    }

    public Mono<Map<String, Object>> getValuesToBind(ServerWebExchange exchange2) {
        return WebExchangeDataBinder.extractValuesToBind(exchange2);
    }

    public static Mono<Map<String, Object>> extractValuesToBind(ServerWebExchange exchange2) {
        MultiValueMap<String, String> queryParams = exchange2.getRequest().getQueryParams();
        Mono<MultiValueMap<String, String>> formData = exchange2.getFormData();
        Mono<MultiValueMap<String, Part>> multipartData = exchange2.getMultipartData();
        return Mono.zip((Mono)Mono.just(queryParams), formData, multipartData).map(tuple -> {
            TreeMap result = new TreeMap();
            ((MultiValueMap)tuple.getT1()).forEach((key, values) -> WebExchangeDataBinder.addBindValue(result, key, values));
            ((MultiValueMap)tuple.getT2()).forEach((key, values) -> WebExchangeDataBinder.addBindValue(result, key, values));
            ((MultiValueMap)tuple.getT3()).forEach((key, values) -> WebExchangeDataBinder.addBindValue(result, key, values));
            return result;
        });
    }

    protected static void addBindValue(Map<String, Object> params, String key, List<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            params.put(key, (values = values.stream().map(value -> value instanceof FormFieldPart ? ((FormFieldPart)value).value() : value).collect(Collectors.toList())).size() == 1 ? values.get(0) : values);
        }
    }
}

