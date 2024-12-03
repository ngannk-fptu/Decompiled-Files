/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.Jsonable$JsonMappingException
 *  com.atlassian.json.marshal.JsonableMarshaller
 */
package com.atlassian.json.jsonorg.marshal;

import com.atlassian.annotations.PublicApi;
import com.atlassian.json.jsonorg.JSONElement;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.JsonableMarshaller;
import java.io.IOException;
import java.io.Writer;

@PublicApi
public class JSONJsonableMarshaller
implements JsonableMarshaller {
    public Jsonable marshal(Object toJsonObj) {
        return this.marshal((JSONElement)toJsonObj);
    }

    public Jsonable marshal(final JSONElement jsonElement) {
        return new Jsonable(){

            public void write(Writer writer) throws IOException {
                if (jsonElement == null || JSONObject.NULL.equals(jsonElement)) {
                    writer.write("null");
                } else {
                    try {
                        jsonElement.write(writer);
                    }
                    catch (JSONException e) {
                        if (e.getCause() instanceof IOException) {
                            throw (IOException)e.getCause();
                        }
                        throw new Jsonable.JsonMappingException((Throwable)e);
                    }
                }
            }
        };
    }
}

