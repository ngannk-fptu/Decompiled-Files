/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.JsonWriter;

public interface JsonWriterFactory {
    public JsonWriter createWriter(Writer var1);

    public JsonWriter createWriter(OutputStream var1);

    public JsonWriter createWriter(OutputStream var1, Charset var2);

    public Map<String, ?> getConfigInUse();
}

