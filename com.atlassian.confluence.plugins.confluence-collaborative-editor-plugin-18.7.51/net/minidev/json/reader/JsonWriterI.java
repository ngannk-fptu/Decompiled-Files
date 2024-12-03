/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.reader;

import java.io.IOException;
import net.minidev.json.JSONStyle;

public interface JsonWriterI<T> {
    public <E extends T> void writeJSONString(E var1, Appendable var2, JSONStyle var3) throws IOException;
}

