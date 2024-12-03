/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.io.Closeable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

public interface JsonWriter
extends Closeable {
    public void writeArray(JsonArray var1);

    public void writeObject(JsonObject var1);

    public void write(JsonStructure var1);

    default public void write(JsonValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close();
}

