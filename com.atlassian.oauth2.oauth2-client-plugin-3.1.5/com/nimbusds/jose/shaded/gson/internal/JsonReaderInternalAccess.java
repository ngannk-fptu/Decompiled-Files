/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.shaded.gson.internal;

import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import java.io.IOException;

public abstract class JsonReaderInternalAccess {
    public static JsonReaderInternalAccess INSTANCE;

    public abstract void promoteNameToValue(JsonReader var1) throws IOException;
}

