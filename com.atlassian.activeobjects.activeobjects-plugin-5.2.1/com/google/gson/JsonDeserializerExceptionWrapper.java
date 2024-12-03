/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.$Gson$Preconditions;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class JsonDeserializerExceptionWrapper<T>
implements JsonDeserializer<T> {
    private final JsonDeserializer<T> delegate;

    JsonDeserializerExceptionWrapper(JsonDeserializer<T> delegate) {
        this.delegate = $Gson$Preconditions.checkNotNull(delegate);
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return this.delegate.deserialize(json, typeOfT, context);
        }
        catch (JsonParseException e) {
            throw e;
        }
        catch (Exception e) {
            StringBuilder errorMsg = new StringBuilder().append("The JsonDeserializer ").append(this.delegate).append(" failed to deserialize json object ").append(json).append(" given the type ").append(typeOfT);
            throw new JsonParseException(errorMsg.toString(), e);
        }
    }

    public String toString() {
        return this.delegate.toString();
    }
}

