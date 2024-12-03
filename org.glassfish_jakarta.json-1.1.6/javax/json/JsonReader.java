/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.io.Closeable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

public interface JsonReader
extends Closeable {
    public JsonStructure read();

    public JsonObject readObject();

    public JsonArray readArray();

    default public JsonValue readValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close();
}

