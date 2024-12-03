/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import javax.json.stream.JsonGenerator;
import org.glassfish.json.JsonGeneratorImpl;
import org.glassfish.json.api.BufferPool;

public class JsonPrettyGeneratorImpl
extends JsonGeneratorImpl {
    private int indentLevel;
    private static final String INDENT = "    ";

    public JsonPrettyGeneratorImpl(Writer writer, BufferPool bufferPool) {
        super(writer, bufferPool);
    }

    public JsonPrettyGeneratorImpl(OutputStream out, BufferPool bufferPool) {
        super(out, bufferPool);
    }

    public JsonPrettyGeneratorImpl(OutputStream out, Charset encoding, BufferPool bufferPool) {
        super(out, encoding, bufferPool);
    }

    @Override
    public JsonGenerator writeStartObject() {
        super.writeStartObject();
        ++this.indentLevel;
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        super.writeStartObject(name);
        ++this.indentLevel;
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        super.writeStartArray();
        ++this.indentLevel;
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        super.writeStartArray(name);
        ++this.indentLevel;
        return this;
    }

    @Override
    public JsonGenerator writeEnd() {
        this.writeNewLine();
        --this.indentLevel;
        this.writeIndent();
        super.writeEnd();
        return this;
    }

    private void writeIndent() {
        for (int i = 0; i < this.indentLevel; ++i) {
            this.writeString(INDENT);
        }
    }

    @Override
    protected void writeComma() {
        super.writeComma();
        if (this.isCommaAllowed()) {
            this.writeChar('\n');
            this.writeIndent();
        }
    }

    @Override
    protected void writeColon() {
        super.writeColon();
        this.writeChar(' ');
    }

    private void writeNewLine() {
        this.writeChar('\n');
    }
}

