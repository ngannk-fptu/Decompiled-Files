/*
 * Decompiled with CFR 0.152.
 */
package javax.json.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue;

public interface JsonGenerator
extends Flushable,
Closeable {
    public static final String PRETTY_PRINTING = "javax.json.stream.JsonGenerator.prettyPrinting";

    public JsonGenerator writeStartObject();

    public JsonGenerator writeStartObject(String var1);

    public JsonGenerator writeKey(String var1);

    public JsonGenerator writeStartArray();

    public JsonGenerator writeStartArray(String var1);

    public JsonGenerator write(String var1, JsonValue var2);

    public JsonGenerator write(String var1, String var2);

    public JsonGenerator write(String var1, BigInteger var2);

    public JsonGenerator write(String var1, BigDecimal var2);

    public JsonGenerator write(String var1, int var2);

    public JsonGenerator write(String var1, long var2);

    public JsonGenerator write(String var1, double var2);

    public JsonGenerator write(String var1, boolean var2);

    public JsonGenerator writeNull(String var1);

    public JsonGenerator writeEnd();

    public JsonGenerator write(JsonValue var1);

    public JsonGenerator write(String var1);

    public JsonGenerator write(BigDecimal var1);

    public JsonGenerator write(BigInteger var1);

    public JsonGenerator write(int var1);

    public JsonGenerator write(long var1);

    public JsonGenerator write(double var1);

    public JsonGenerator write(boolean var1);

    public JsonGenerator writeNull();

    @Override
    public void close();

    @Override
    public void flush();
}

