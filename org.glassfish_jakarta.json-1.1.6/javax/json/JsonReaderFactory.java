/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.JsonReader;

public interface JsonReaderFactory {
    public JsonReader createReader(Reader var1);

    public JsonReader createReader(InputStream var1);

    public JsonReader createReader(InputStream var1, Charset var2);

    public Map<String, ?> getConfigInUse();
}

