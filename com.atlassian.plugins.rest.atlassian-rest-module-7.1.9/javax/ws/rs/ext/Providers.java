/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.ext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Providers {
    public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> var1, Type var2, Annotation[] var3, MediaType var4);

    public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> var1, Type var2, Annotation[] var3, MediaType var4);

    public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> var1);

    public <T> ContextResolver<T> getContextResolver(Class<T> var1, MediaType var2);
}

