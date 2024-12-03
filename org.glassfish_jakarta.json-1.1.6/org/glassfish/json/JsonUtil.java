/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonValue;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static JsonValue toJson(String jsonString) {
        StringBuilder builder = new StringBuilder();
        boolean single_context = false;
        for (int i = 0; i < jsonString.length(); ++i) {
            char ch = jsonString.charAt(i);
            if (ch == '\\') {
                if (++i < jsonString.length()) {
                    ch = jsonString.charAt(i);
                    if (!single_context || ch != '\'') {
                        builder.append('\\');
                    }
                }
            } else if (ch == '\'') {
                ch = '\"';
                single_context = !single_context;
            }
            builder.append(ch);
        }
        JsonReader reader = Json.createReader(new StringReader(builder.toString()));
        JsonValue value = reader.readValue();
        reader.close();
        return value;
    }
}

