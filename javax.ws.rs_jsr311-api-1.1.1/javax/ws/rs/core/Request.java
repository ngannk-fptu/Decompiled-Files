/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.util.Date;
import java.util.List;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Request {
    public String getMethod();

    public Variant selectVariant(List<Variant> var1) throws IllegalArgumentException;

    public Response.ResponseBuilder evaluatePreconditions(EntityTag var1);

    public Response.ResponseBuilder evaluatePreconditions(Date var1);

    public Response.ResponseBuilder evaluatePreconditions(Date var1, EntityTag var2);

    public Response.ResponseBuilder evaluatePreconditions();
}

