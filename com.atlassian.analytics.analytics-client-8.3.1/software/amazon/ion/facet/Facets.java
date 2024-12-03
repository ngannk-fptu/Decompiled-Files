/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.facet;

import software.amazon.ion.facet.Faceted;
import software.amazon.ion.facet.UnsupportedFacetException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Facets {
    public static <T> T asFacet(Class<T> facetType, Faceted subject) {
        return subject == null ? null : (T)subject.asFacet(facetType);
    }

    public static <T> T asFacet(Class<T> facetType, Object subject) {
        T facet = null;
        if (subject instanceof Faceted) {
            facet = ((Faceted)subject).asFacet(facetType);
        } else if (facetType.isInstance(subject)) {
            facet = facetType.cast(subject);
        }
        return facet;
    }

    public static <T> T assumeFacet(Class<T> facetType, Faceted subject) {
        T facet;
        if (subject != null && (facet = subject.asFacet(facetType)) != null) {
            return facet;
        }
        throw new UnsupportedFacetException(facetType, subject);
    }

    public static <T> T assumeFacet(Class<T> facetType, Object subject) {
        if (subject instanceof Faceted) {
            T facet = ((Faceted)subject).asFacet(facetType);
            if (facet != null) {
                return facet;
            }
        } else if (facetType.isInstance(subject)) {
            return facetType.cast(subject);
        }
        throw new UnsupportedFacetException(facetType, subject);
    }
}

