/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonBoolean;
import com.atlassian.confluence.json.json.JsonNull;
import com.atlassian.confluence.json.json.JsonNumber;
import com.atlassian.confluence.json.json.JsonString;
import com.atlassian.confluence.json.jsonator.Jsonator;

class PrimitiveJsonator
implements Jsonator<Object> {
    PrimitiveJsonator() {
    }

    @Override
    public Json convert(Object object) {
        if (object == null) {
            return new JsonNull();
        }
        if (object instanceof String) {
            return new JsonString((String)object);
        }
        if (object instanceof Number) {
            return new JsonNumber((Number)object);
        }
        if (object instanceof Boolean) {
            return new JsonBoolean((Boolean)object);
        }
        throw new IllegalArgumentException("Passed object is not a primitive: " + object);
    }
}

