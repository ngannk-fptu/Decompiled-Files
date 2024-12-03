/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.facet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnsupportedFacetException
extends UnsupportedOperationException {
    private static final long serialVersionUID = 1L;
    private Class<?> myFacetType;
    private Object mySubject;

    public UnsupportedFacetException(Class<?> facetType, Object subject) {
        this.myFacetType = facetType;
        this.mySubject = subject;
    }

    @Override
    public String getMessage() {
        return "Facet " + this.myFacetType.getName() + " is not supported by " + this.mySubject;
    }

    public Class<?> getFacetType() {
        return this.myFacetType;
    }

    public Object getSubject() {
        return this.mySubject;
    }
}

