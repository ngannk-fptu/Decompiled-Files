/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.util.ArrayList;
import java.util.List;

public class ListUnmarshaller<T>
implements Unmarshaller<List<T>, JsonUnmarshallerContext> {
    private final Unmarshaller<T, JsonUnmarshallerContext> itemUnmarshaller;

    public ListUnmarshaller(Unmarshaller<T, JsonUnmarshallerContext> itemUnmarshaller) {
        this.itemUnmarshaller = itemUnmarshaller;
    }

    @Override
    public List<T> unmarshall(JsonUnmarshallerContext context) throws Exception {
        if (context.isInsideResponseHeader()) {
            return this.unmarshallResponseHeaderToList(context);
        }
        return this.unmarshallJsonToList(context);
    }

    private List<T> unmarshallResponseHeaderToList(JsonUnmarshallerContext context) throws Exception {
        String[] headerValues;
        String headerValue = context.readText();
        ArrayList<T> list = new ArrayList<T>();
        for (final String headerVal : headerValues = headerValue.split("[,]")) {
            list.add(this.itemUnmarshaller.unmarshall(new JsonUnmarshallerContext(){

                @Override
                public String readText() {
                    return headerVal;
                }
            }));
        }
        return list;
    }

    private List<T> unmarshallJsonToList(JsonUnmarshallerContext context) throws Exception {
        ArrayList<T> list = new ArrayList<T>();
        if (context.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        JsonToken token;
        while ((token = context.nextToken()) != null) {
            if (token == JsonToken.END_ARRAY) {
                return list;
            }
            list.add(this.itemUnmarshaller.unmarshall(context));
        }
        return list;
    }
}

