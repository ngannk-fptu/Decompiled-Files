/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.util.HashMap;
import java.util.Map;

public class MapUnmarshaller<K, V>
implements Unmarshaller<Map<K, V>, JsonUnmarshallerContext> {
    private final Unmarshaller<K, JsonUnmarshallerContext> keyUnmarshaller;
    private final Unmarshaller<V, JsonUnmarshallerContext> valueUnmarshaller;

    public MapUnmarshaller(Unmarshaller<K, JsonUnmarshallerContext> keyUnmarshaller, Unmarshaller<V, JsonUnmarshallerContext> valueUnmarshaller) {
        this.keyUnmarshaller = keyUnmarshaller;
        this.valueUnmarshaller = valueUnmarshaller;
    }

    @Override
    public Map<K, V> unmarshall(JsonUnmarshallerContext context) throws Exception {
        HashMap<K, V> map = new HashMap<K, V>();
        int originalDepth = context.getCurrentDepth();
        if (context.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        while (true) {
            JsonToken token;
            if ((token = context.nextToken()) == null) {
                return map;
            }
            if (token == JsonToken.FIELD_NAME) {
                K k = this.keyUnmarshaller.unmarshall(context);
                token = context.nextToken();
                V v = this.valueUnmarshaller.unmarshall(context);
                map.put(k, v);
                continue;
            }
            if ((token == JsonToken.END_ARRAY || token == JsonToken.END_OBJECT) && context.getCurrentDepth() <= originalDepth) break;
        }
        return map;
    }
}

