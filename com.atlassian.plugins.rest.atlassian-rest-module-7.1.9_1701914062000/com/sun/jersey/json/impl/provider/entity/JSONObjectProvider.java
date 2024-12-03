/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.provider.entity;

import com.sun.jersey.json.impl.ImplMessages;
import com.sun.jersey.json.impl.provider.entity.JSONLowLevelProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JSONObjectProvider
extends JSONLowLevelProvider<JSONObject> {
    JSONObjectProvider() {
        super(JSONObject.class);
    }

    @Override
    public JSONObject readFrom(Class<JSONObject> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try {
            return new JSONObject(JSONObjectProvider.readFromAsString(entityStream, mediaType));
        }
        catch (JSONException je) {
            throw new WebApplicationException((Throwable)new Exception(ImplMessages.ERROR_PARSING_JSON_OBJECT(), je), 400);
        }
    }

    @Override
    public void writeTo(JSONObject t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(entityStream, JSONObjectProvider.getCharset(mediaType));
            t.write(writer);
            writer.flush();
        }
        catch (JSONException je) {
            throw new WebApplicationException((Throwable)new Exception(ImplMessages.ERROR_WRITING_JSON_OBJECT(), je), 500);
        }
    }

    @Produces(value={"*/*"})
    @Consumes(value={"*/*"})
    public static final class General
    extends JSONObjectProvider {
        @Override
        protected boolean isSupported(MediaType m) {
            return m.getSubtype().endsWith("+json");
        }
    }

    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public static final class App
    extends JSONObjectProvider {
    }
}

