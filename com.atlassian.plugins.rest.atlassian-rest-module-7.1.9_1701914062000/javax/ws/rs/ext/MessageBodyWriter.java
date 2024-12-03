/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.ext;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MessageBodyWriter<T> {
    public boolean isWriteable(Class<?> var1, Type var2, Annotation[] var3, MediaType var4);

    public long getSize(T var1, Class<?> var2, Type var3, Annotation[] var4, MediaType var5);

    public void writeTo(T var1, Class<?> var2, Type var3, Annotation[] var4, MediaType var5, MultivaluedMap<String, Object> var6, OutputStream var7) throws IOException, WebApplicationException;
}

