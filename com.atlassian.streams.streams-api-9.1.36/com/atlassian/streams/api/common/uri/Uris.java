/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 */
package com.atlassian.streams.api.common.uri;

import com.atlassian.streams.api.common.Pair;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class Uris {
    private Uris() {
        throw new RuntimeException("UriEncoder cannot be instantiated");
    }

    @Deprecated
    public static Function<String, String> encode() {
        return Encode.INSTANCE;
    }

    public static String encode(String uriComponent) {
        try {
            return URLEncoder.encode(uriComponent, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Funny JVM you have here", e);
        }
    }

    public static String encode(String uriComponent, String encoding) {
        try {
            return URLEncoder.encode(uriComponent, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String uriComponent) {
        try {
            return URLDecoder.decode(uriComponent, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Funny JVM you have here", e);
        }
    }

    public static String decode(String uriComponent, String encoding) {
        try {
            return URLDecoder.decode(uriComponent, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static Multimap<String, String> getQueryParameters(URI uri) {
        if (uri.getQuery() == null) {
            return ImmutableMultimap.of();
        }
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        for (Pair param : Iterables.transform(Arrays.asList(uri.getQuery().split("&")), Uris.asQueryParam())) {
            builder.put(param.first(), param.second());
        }
        return builder.build();
    }

    public static Map<String, Collection<String>> getQueryParams(URI uri) {
        if (uri.getQuery() == null) {
            return Collections.emptyMap();
        }
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        for (Pair param : Iterables.transform(Arrays.asList(uri.getQuery().split("&")), Uris.asQueryParam())) {
            builder.put(param.first(), param.second());
        }
        return builder.build().asMap();
    }

    @Deprecated
    private static Function<String, Pair<String, String>> asQueryParam() {
        return AsQueryParam.INSTANCE;
    }

    public static Pair<String, String> asQueryParam(String p) {
        String[] nameValue = p.split("=");
        String name = Uris.decode(nameValue[0]);
        String value = nameValue.length == 2 ? Uris.decode(nameValue[1]) : "";
        return Pair.pair(name, value);
    }

    @Deprecated
    private static enum AsQueryParam implements Function<String, Pair<String, String>>
    {
        INSTANCE;


        public Pair<String, String> apply(String p) {
            String[] nameValue = p.split("=");
            String name = Uris.decode(nameValue[0]);
            String value = nameValue.length == 2 ? Uris.decode(nameValue[1]) : "";
            return Pair.pair(name, value);
        }
    }

    @Deprecated
    private static enum Encode implements Function<String, String>
    {
        INSTANCE;


        public String apply(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Funny JVM you have here", e);
            }
        }
    }
}

