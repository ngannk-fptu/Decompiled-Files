/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  org.codehaus.jettison.json.JSONArray
 *  org.codehaus.jettison.json.JSONException
 */
package com.sun.jersey.json.impl.provider.entity;

import com.sun.jersey.json.impl.ImplMessages;
import com.sun.jersey.json.impl.provider.entity.JSONLowLevelProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

public class JSONArrayProvider
extends JSONLowLevelProvider<JSONArray> {
    JSONArrayProvider() {
        super(JSONArray.class);
    }

    public JSONArray readFrom(Class<JSONArray> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try {
            return new JSONArray(JSONArrayProvider.readFromAsString((InputStream)entityStream, (MediaType)mediaType));
        }
        catch (JSONException je) {
            throw new WebApplicationException((Throwable)new Exception(ImplMessages.ERROR_PARSING_JSON_ARRAY(), je), 400);
        }
    }

    public void writeTo(JSONArray t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(entityStream, JSONArrayProvider.getCharset((MediaType)mediaType));
            t.write((Writer)writer);
            writer.write("\n");
            writer.flush();
        }
        catch (JSONException je) {
            throw new WebApplicationException((Throwable)new Exception(ImplMessages.ERROR_WRITING_JSON_ARRAY(), je), 500);
        }
    }

    @Produces(value={"*/*"})
    @Consumes(value={"*/*"})
    public static final class General
    extends JSONArrayProvider {
        @Override
        protected boolean isSupported(MediaType m) {
            return m.getSubtype().endsWith("+json");
        }
    }

    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public static final class App
    extends JSONArrayProvider {
    }
}

