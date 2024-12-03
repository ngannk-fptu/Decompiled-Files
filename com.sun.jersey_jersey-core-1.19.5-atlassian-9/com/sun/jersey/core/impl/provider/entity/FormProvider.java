/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.impl.provider.entity.BaseFormProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Produces(value={"application/x-www-form-urlencoded", "*/*"})
@Consumes(value={"application/x-www-form-urlencoded", "*/*"})
public final class FormProvider
extends BaseFormProvider<Form> {
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Form.class;
    }

    public Form readFrom(Class<Form> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        return this.readFrom(new Form(), mediaType, entityStream);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Form.class;
    }

    public void writeTo(Form t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        this.writeTo(t, mediaType, entityStream);
    }
}

