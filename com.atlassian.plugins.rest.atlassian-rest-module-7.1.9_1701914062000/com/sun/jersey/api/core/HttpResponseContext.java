/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public interface HttpResponseContext {
    public Response getResponse();

    public void setResponse(Response var1);

    public boolean isResponseSet();

    public Throwable getMappedThrowable();

    public Response.StatusType getStatusType();

    public void setStatusType(Response.StatusType var1);

    public int getStatus();

    public void setStatus(int var1);

    public Object getEntity();

    public Type getEntityType();

    public Object getOriginalEntity();

    public void setEntity(Object var1);

    public Annotation[] getAnnotations();

    public void setAnnotations(Annotation[] var1);

    public MultivaluedMap<String, Object> getHttpHeaders();

    public MediaType getMediaType();

    public OutputStream getOutputStream() throws IOException;

    public boolean isCommitted();
}

