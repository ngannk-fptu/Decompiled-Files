/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.io.ParsingDecoder;
import org.apache.avro.io.parsing.JsonGrammarGenerator;
import org.apache.avro.io.parsing.Parser;
import org.apache.avro.io.parsing.Symbol;
import org.apache.avro.util.Utf8;

public class JsonDecoder
extends ParsingDecoder
implements Parser.ActionHandler {
    private JsonParser in;
    private static JsonFactory jsonFactory = new JsonFactory();
    Stack<ReorderBuffer> reorderBuffers = new Stack();
    ReorderBuffer currentReorderBuffer;

    private JsonDecoder(Symbol root, InputStream in) throws IOException {
        super(root);
        this.configure(in);
    }

    private JsonDecoder(Symbol root, String in) throws IOException {
        super(root);
        this.configure(in);
    }

    JsonDecoder(Schema schema, InputStream in) throws IOException {
        this(JsonDecoder.getSymbol(schema), in);
    }

    JsonDecoder(Schema schema, String in) throws IOException {
        this(JsonDecoder.getSymbol(schema), in);
    }

    private static Symbol getSymbol(Schema schema) {
        Objects.requireNonNull(schema, "Schema cannot be null");
        return new JsonGrammarGenerator().generate(schema);
    }

    public JsonDecoder configure(InputStream in) throws IOException {
        Objects.requireNonNull(in, "InputStream cannot be null");
        this.parser.reset();
        this.reorderBuffers.clear();
        this.currentReorderBuffer = null;
        this.in = jsonFactory.createParser(in);
        this.in.nextToken();
        return this;
    }

    public JsonDecoder configure(String in) throws IOException {
        Objects.requireNonNull(in, "String to read from cannot be null");
        this.parser.reset();
        this.reorderBuffers.clear();
        this.currentReorderBuffer = null;
        this.in = new JsonFactory().createParser(in);
        this.in.nextToken();
        return this;
    }

    private void advance(Symbol symbol) throws IOException {
        this.parser.processTrailingImplicitActions();
        if (this.in.getCurrentToken() == null && this.parser.depth() == 1) {
            throw new EOFException();
        }
        this.parser.advance(symbol);
    }

    @Override
    public void readNull() throws IOException {
        this.advance(Symbol.NULL);
        if (this.in.getCurrentToken() != JsonToken.VALUE_NULL) {
            throw this.error("null");
        }
        this.in.nextToken();
    }

    @Override
    public boolean readBoolean() throws IOException {
        this.advance(Symbol.BOOLEAN);
        JsonToken t = this.in.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE || t == JsonToken.VALUE_FALSE) {
            this.in.nextToken();
            return t == JsonToken.VALUE_TRUE;
        }
        throw this.error("boolean");
    }

    @Override
    public int readInt() throws IOException {
        this.advance(Symbol.INT);
        if (this.in.getCurrentToken().isNumeric()) {
            int result = this.in.getIntValue();
            this.in.nextToken();
            return result;
        }
        throw this.error("int");
    }

    @Override
    public long readLong() throws IOException {
        this.advance(Symbol.LONG);
        if (this.in.getCurrentToken().isNumeric()) {
            long result = this.in.getLongValue();
            this.in.nextToken();
            return result;
        }
        throw this.error("long");
    }

    @Override
    public float readFloat() throws IOException {
        this.advance(Symbol.FLOAT);
        if (this.in.getCurrentToken().isNumeric()) {
            float result = this.in.getFloatValue();
            this.in.nextToken();
            return result;
        }
        throw this.error("float");
    }

    @Override
    public double readDouble() throws IOException {
        this.advance(Symbol.DOUBLE);
        if (this.in.getCurrentToken().isNumeric()) {
            double result = this.in.getDoubleValue();
            this.in.nextToken();
            return result;
        }
        throw this.error("double");
    }

    @Override
    public Utf8 readString(Utf8 old) throws IOException {
        return new Utf8(this.readString());
    }

    @Override
    public String readString() throws IOException {
        this.advance(Symbol.STRING);
        if (this.parser.topSymbol() == Symbol.MAP_KEY_MARKER) {
            this.parser.advance(Symbol.MAP_KEY_MARKER);
            if (this.in.getCurrentToken() != JsonToken.FIELD_NAME) {
                throw this.error("map-key");
            }
        } else if (this.in.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw this.error("string");
        }
        String result = this.in.getText();
        this.in.nextToken();
        return result;
    }

    @Override
    public void skipString() throws IOException {
        this.advance(Symbol.STRING);
        if (this.parser.topSymbol() == Symbol.MAP_KEY_MARKER) {
            this.parser.advance(Symbol.MAP_KEY_MARKER);
            if (this.in.getCurrentToken() != JsonToken.FIELD_NAME) {
                throw this.error("map-key");
            }
        } else if (this.in.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw this.error("string");
        }
        this.in.nextToken();
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer old) throws IOException {
        this.advance(Symbol.BYTES);
        if (this.in.getCurrentToken() == JsonToken.VALUE_STRING) {
            byte[] result = this.readByteArray();
            this.in.nextToken();
            return ByteBuffer.wrap(result);
        }
        throw this.error("bytes");
    }

    private byte[] readByteArray() throws IOException {
        byte[] result = this.in.getText().getBytes(StandardCharsets.ISO_8859_1);
        return result;
    }

    @Override
    public void skipBytes() throws IOException {
        this.advance(Symbol.BYTES);
        if (this.in.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw this.error("bytes");
        }
        this.in.nextToken();
    }

    private void checkFixed(int size) throws IOException {
        this.advance(Symbol.FIXED);
        Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        if (size != top.size) {
            throw new AvroTypeException("Incorrect length for fixed binary: expected " + top.size + " but received " + size + " bytes.");
        }
    }

    @Override
    public void readFixed(byte[] bytes, int start, int len) throws IOException {
        byte[] result;
        this.checkFixed(len);
        if (this.in.getCurrentToken() == JsonToken.VALUE_STRING) {
            result = this.readByteArray();
            this.in.nextToken();
            if (result.length != len) {
                throw new AvroTypeException("Expected fixed length " + len + ", but got" + result.length);
            }
        } else {
            throw this.error("fixed");
        }
        System.arraycopy(result, 0, bytes, start, len);
    }

    @Override
    public void skipFixed(int length) throws IOException {
        this.checkFixed(length);
        this.doSkipFixed(length);
    }

    private void doSkipFixed(int length) throws IOException {
        if (this.in.getCurrentToken() == JsonToken.VALUE_STRING) {
            byte[] result = this.readByteArray();
            this.in.nextToken();
            if (result.length != length) {
                throw new AvroTypeException("Expected fixed length " + length + ", but got" + result.length);
            }
        } else {
            throw this.error("fixed");
        }
    }

    @Override
    protected void skipFixed() throws IOException {
        this.advance(Symbol.FIXED);
        Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        this.doSkipFixed(top.size);
    }

    @Override
    public int readEnum() throws IOException {
        this.advance(Symbol.ENUM);
        Symbol.EnumLabelsAction top = (Symbol.EnumLabelsAction)this.parser.popSymbol();
        if (this.in.getCurrentToken() == JsonToken.VALUE_STRING) {
            this.in.getText();
            int n = top.findLabel(this.in.getText());
            if (n >= 0) {
                this.in.nextToken();
                return n;
            }
            throw new AvroTypeException("Unknown symbol in enum " + this.in.getText());
        }
        throw this.error("fixed");
    }

    @Override
    public long readArrayStart() throws IOException {
        this.advance(Symbol.ARRAY_START);
        if (this.in.getCurrentToken() == JsonToken.START_ARRAY) {
            this.in.nextToken();
            return this.doArrayNext();
        }
        throw this.error("array-start");
    }

    @Override
    public long arrayNext() throws IOException {
        this.advance(Symbol.ITEM_END);
        return this.doArrayNext();
    }

    private long doArrayNext() throws IOException {
        if (this.in.getCurrentToken() == JsonToken.END_ARRAY) {
            this.parser.advance(Symbol.ARRAY_END);
            this.in.nextToken();
            return 0L;
        }
        return 1L;
    }

    @Override
    public long skipArray() throws IOException {
        this.advance(Symbol.ARRAY_START);
        if (this.in.getCurrentToken() != JsonToken.START_ARRAY) {
            throw this.error("array-start");
        }
        this.in.skipChildren();
        this.in.nextToken();
        this.advance(Symbol.ARRAY_END);
        return 0L;
    }

    @Override
    public long readMapStart() throws IOException {
        this.advance(Symbol.MAP_START);
        if (this.in.getCurrentToken() == JsonToken.START_OBJECT) {
            this.in.nextToken();
            return this.doMapNext();
        }
        throw this.error("map-start");
    }

    @Override
    public long mapNext() throws IOException {
        this.advance(Symbol.ITEM_END);
        return this.doMapNext();
    }

    private long doMapNext() throws IOException {
        if (this.in.getCurrentToken() == JsonToken.END_OBJECT) {
            this.in.nextToken();
            this.advance(Symbol.MAP_END);
            return 0L;
        }
        return 1L;
    }

    @Override
    public long skipMap() throws IOException {
        this.advance(Symbol.MAP_START);
        if (this.in.getCurrentToken() != JsonToken.START_OBJECT) {
            throw this.error("map-start");
        }
        this.in.skipChildren();
        this.in.nextToken();
        this.advance(Symbol.MAP_END);
        return 0L;
    }

    @Override
    public int readIndex() throws IOException {
        String label;
        this.advance(Symbol.UNION);
        Symbol.Alternative a = (Symbol.Alternative)this.parser.popSymbol();
        if (this.in.getCurrentToken() == JsonToken.VALUE_NULL) {
            label = "null";
        } else if (this.in.getCurrentToken() == JsonToken.START_OBJECT && this.in.nextToken() == JsonToken.FIELD_NAME) {
            label = this.in.getText();
            this.in.nextToken();
            this.parser.pushSymbol(Symbol.UNION_END);
        } else {
            throw this.error("start-union");
        }
        int n = a.findLabel(label);
        if (n < 0) {
            throw new AvroTypeException("Unknown union branch " + label);
        }
        this.parser.pushSymbol(a.getSymbol(n));
        return n;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Symbol doAction(Symbol input, Symbol top) throws IOException {
        if (top instanceof Symbol.FieldAdjustAction) {
            Symbol.FieldAdjustAction fa = (Symbol.FieldAdjustAction)top;
            String name = fa.fname;
            if (this.currentReorderBuffer != null) {
                try (TokenBuffer tokenBuffer = this.currentReorderBuffer.savedFields.get(name);){
                    if (tokenBuffer != null) {
                        this.currentReorderBuffer.savedFields.remove(name);
                        this.currentReorderBuffer.origParser = this.in;
                        this.in = tokenBuffer.asParser();
                        this.in.nextToken();
                        Symbol symbol = null;
                        return symbol;
                    }
                }
            }
            if (this.in.getCurrentToken() != JsonToken.FIELD_NAME) return null;
            do {
                String fn = this.in.getText();
                this.in.nextToken();
                if (name.equals(fn) || fa.aliases.contains(fn)) {
                    return null;
                }
                if (this.currentReorderBuffer == null) {
                    this.currentReorderBuffer = new ReorderBuffer();
                }
                try (TokenBuffer tokenBuffer = new TokenBuffer(this.in);){
                    tokenBuffer.copyCurrentStructure(this.in);
                    this.currentReorderBuffer.savedFields.put(fn, tokenBuffer);
                }
                this.in.nextToken();
            } while (this.in.getCurrentToken() == JsonToken.FIELD_NAME);
            throw new AvroTypeException("Expected field name not found: " + fa.fname);
        }
        if (top == Symbol.FIELD_END) {
            if (this.currentReorderBuffer == null || this.currentReorderBuffer.origParser == null) return null;
            this.in = this.currentReorderBuffer.origParser;
            this.currentReorderBuffer.origParser = null;
            return null;
        } else if (top == Symbol.RECORD_START) {
            if (this.in.getCurrentToken() != JsonToken.START_OBJECT) throw this.error("record-start");
            this.in.nextToken();
            this.reorderBuffers.push(this.currentReorderBuffer);
            this.currentReorderBuffer = null;
            return null;
        } else {
            if (top != Symbol.RECORD_END && top != Symbol.UNION_END) throw new AvroTypeException("Unknown action symbol " + top);
            while (this.in.getCurrentToken() != JsonToken.END_OBJECT) {
                this.in.nextToken();
            }
            if (top == Symbol.RECORD_END) {
                if (this.currentReorderBuffer != null && !this.currentReorderBuffer.savedFields.isEmpty()) {
                    throw this.error("Unknown fields: " + this.currentReorderBuffer.savedFields.keySet());
                }
                this.currentReorderBuffer = this.reorderBuffers.pop();
            }
            this.in.nextToken();
        }
        return null;
    }

    private AvroTypeException error(String type) {
        return new AvroTypeException("Expected " + type + ". Got " + (Object)((Object)this.in.getCurrentToken()));
    }

    private static class ReorderBuffer {
        public Map<String, TokenBuffer> savedFields = new HashMap<String, TokenBuffer>();
        public JsonParser origParser = null;

        private ReorderBuffer() {
        }
    }
}

