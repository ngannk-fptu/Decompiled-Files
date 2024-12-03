/*
 * Decompiled with CFR 0.152.
 */
package javax.json.stream;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.stream.JsonGenerator;

public interface JsonGeneratorFactory {
    public JsonGenerator createGenerator(Writer var1);

    public JsonGenerator createGenerator(OutputStream var1);

    public JsonGenerator createGenerator(OutputStream var1, Charset var2);

    public Map<String, ?> getConfigInUse();
}

