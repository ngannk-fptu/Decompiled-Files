/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.ext.RuntimeDelegate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class UriBuilder {
    protected UriBuilder() {
    }

    protected static UriBuilder newInstance() {
        UriBuilder b = RuntimeDelegate.getInstance().createUriBuilder();
        return b;
    }

    public static UriBuilder fromUri(URI uri) throws IllegalArgumentException {
        UriBuilder b = UriBuilder.newInstance();
        b.uri(uri);
        return b;
    }

    public static UriBuilder fromUri(String uri) throws IllegalArgumentException {
        URI u;
        try {
            u = URI.create(uri);
        }
        catch (NullPointerException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        return UriBuilder.fromUri(u);
    }

    public static UriBuilder fromPath(String path) throws IllegalArgumentException {
        UriBuilder b = UriBuilder.newInstance();
        b.path(path);
        return b;
    }

    public static UriBuilder fromResource(Class<?> resource) throws IllegalArgumentException {
        UriBuilder b = UriBuilder.newInstance();
        b.path(resource);
        return b;
    }

    public abstract UriBuilder clone();

    public abstract UriBuilder uri(URI var1) throws IllegalArgumentException;

    public abstract UriBuilder scheme(String var1) throws IllegalArgumentException;

    public abstract UriBuilder schemeSpecificPart(String var1) throws IllegalArgumentException;

    public abstract UriBuilder userInfo(String var1);

    public abstract UriBuilder host(String var1) throws IllegalArgumentException;

    public abstract UriBuilder port(int var1) throws IllegalArgumentException;

    public abstract UriBuilder replacePath(String var1);

    public abstract UriBuilder path(String var1) throws IllegalArgumentException;

    public abstract UriBuilder path(Class var1) throws IllegalArgumentException;

    public abstract UriBuilder path(Class var1, String var2) throws IllegalArgumentException;

    public abstract UriBuilder path(Method var1) throws IllegalArgumentException;

    public abstract UriBuilder segment(String ... var1) throws IllegalArgumentException;

    public abstract UriBuilder replaceMatrix(String var1) throws IllegalArgumentException;

    public abstract UriBuilder matrixParam(String var1, Object ... var2) throws IllegalArgumentException;

    public abstract UriBuilder replaceMatrixParam(String var1, Object ... var2) throws IllegalArgumentException;

    public abstract UriBuilder replaceQuery(String var1) throws IllegalArgumentException;

    public abstract UriBuilder queryParam(String var1, Object ... var2) throws IllegalArgumentException;

    public abstract UriBuilder replaceQueryParam(String var1, Object ... var2) throws IllegalArgumentException;

    public abstract UriBuilder fragment(String var1);

    public abstract URI buildFromMap(Map<String, ? extends Object> var1) throws IllegalArgumentException, UriBuilderException;

    public abstract URI buildFromEncodedMap(Map<String, ? extends Object> var1) throws IllegalArgumentException, UriBuilderException;

    public abstract URI build(Object ... var1) throws IllegalArgumentException, UriBuilderException;

    public abstract URI buildFromEncoded(Object ... var1) throws IllegalArgumentException, UriBuilderException;
}

