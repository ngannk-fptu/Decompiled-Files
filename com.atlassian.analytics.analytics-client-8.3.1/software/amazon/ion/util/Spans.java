/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.util;

import software.amazon.ion.Span;
import software.amazon.ion.SpanProvider;
import software.amazon.ion.facet.Facets;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Spans {
    public static Span currentSpan(Object spanProvider) {
        SpanProvider sp = Facets.asFacet(SpanProvider.class, spanProvider);
        Span span = sp == null ? null : sp.currentSpan();
        return span;
    }

    public static <T> T currentSpan(Class<T> spanFacetType, Object spanProvider) {
        Span span = Spans.currentSpan(spanProvider);
        T spanFacet = Facets.asFacet(spanFacetType, span);
        return spanFacet;
    }
}

