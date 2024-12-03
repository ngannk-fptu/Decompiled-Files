/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.ext;

import javax.ws.rs.core.Response;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ExceptionMapper<E extends Throwable> {
    public Response toResponse(E var1);
}

