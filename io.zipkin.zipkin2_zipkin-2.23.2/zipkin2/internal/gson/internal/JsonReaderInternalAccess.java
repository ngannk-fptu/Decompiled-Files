/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal.gson.internal;

import java.io.IOException;
import zipkin2.internal.gson.stream.JsonReader;

public abstract class JsonReaderInternalAccess {
    public static JsonReaderInternalAccess INSTANCE;

    public abstract void promoteNameToValue(JsonReader var1) throws IOException;
}

