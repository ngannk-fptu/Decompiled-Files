/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsEntry$Link
 *  com.google.common.base.Predicate
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.api.StreamsEntry;
import com.google.common.base.Predicate;
import java.net.URI;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public final class Predicates {
    public static <K, V> Predicate<Map.Entry<K, V>> whereMapEntryKey(Predicate<K> p) {
        return new WhereKey(p);
    }

    public static Predicate<String> blank() {
        return Blank.INSTANCE;
    }

    public static Predicate<URI> isAbsolute() {
        return IsAbsolute.INSTANCE;
    }

    public static boolean isAbsolute(URI uri) {
        return Predicates.isAbsolute().apply((Object)uri);
    }

    public static Predicate<StreamsEntry.Link> linkHref(Predicate<URI> p) {
        return new LinkHref(p);
    }

    public static Predicate<StreamsEntry.Link> linkRel(Predicate<String> p) {
        return new LinkRel(p);
    }

    private static final class LinkRel
    implements Predicate<StreamsEntry.Link> {
        private final Predicate<String> p;

        public LinkRel(Predicate<String> p) {
            this.p = p;
        }

        public boolean apply(StreamsEntry.Link link) {
            return this.p.apply((Object)link.getRel());
        }
    }

    private static final class LinkHref
    implements Predicate<StreamsEntry.Link> {
        private final Predicate<URI> p;

        public LinkHref(Predicate<URI> p) {
            this.p = p;
        }

        public boolean apply(StreamsEntry.Link link) {
            return this.p.apply((Object)link.getHref());
        }
    }

    private static enum IsAbsolute implements Predicate<URI>
    {
        INSTANCE;


        public boolean apply(URI uri) {
            return uri != null && uri.isAbsolute();
        }
    }

    private static enum Blank implements Predicate<String>
    {
        INSTANCE;


        public boolean apply(String s) {
            return StringUtils.isBlank((CharSequence)s);
        }
    }

    private static final class WhereKey<K, V>
    implements Predicate<Map.Entry<K, V>> {
        private final Predicate<K> p;

        public WhereKey(Predicate<K> p) {
            this.p = p;
        }

        public boolean apply(Map.Entry<K, V> e) {
            return this.p.apply(e.getKey());
        }
    }
}

