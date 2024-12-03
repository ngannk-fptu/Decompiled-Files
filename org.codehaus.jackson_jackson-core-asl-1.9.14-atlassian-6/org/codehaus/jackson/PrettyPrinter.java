/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;

public interface PrettyPrinter {
    public void writeRootValueSeparator(JsonGenerator var1) throws IOException, JsonGenerationException;

    public void writeStartObject(JsonGenerator var1) throws IOException, JsonGenerationException;

    public void writeEndObject(JsonGenerator var1, int var2) throws IOException, JsonGenerationException;

    public void writeObjectEntrySeparator(JsonGenerator var1) throws IOException, JsonGenerationException;

    public void writeObjectFieldValueSeparator(JsonGenerator var1) throws IOException, JsonGenerationException;

    public void writeStartArray(JsonGenerator var1) throws IOException, JsonGenerationException;

    public void writeEndArray(JsonGenerator var1, int var2) throws IOException, JsonGenerationException;

    public void writeArrayValueSeparator(JsonGenerator var1) throws IOException, JsonGenerationException;

    public void beforeArrayValues(JsonGenerator var1) throws IOException, JsonGenerationException;

    public void beforeObjectEntries(JsonGenerator var1) throws IOException, JsonGenerationException;
}

