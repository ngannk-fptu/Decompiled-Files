/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.ParsingDecoder;
import org.apache.avro.io.parsing.Parser;
import org.apache.avro.io.parsing.Symbol;
import org.apache.avro.io.parsing.ValidatingGrammarGenerator;
import org.apache.avro.util.Utf8;

public class ValidatingDecoder
extends ParsingDecoder
implements Parser.ActionHandler {
    protected Decoder in;

    ValidatingDecoder(Symbol root, Decoder in) throws IOException {
        super(root);
        this.configure(in);
    }

    ValidatingDecoder(Schema schema, Decoder in) throws IOException {
        this(ValidatingDecoder.getSymbol(schema), in);
    }

    private static Symbol getSymbol(Schema schema) {
        Objects.requireNonNull(schema, "Schema cannot be null");
        return new ValidatingGrammarGenerator().generate(schema);
    }

    public ValidatingDecoder configure(Decoder in) throws IOException {
        this.parser.reset();
        this.in = in;
        return this;
    }

    @Override
    public void readNull() throws IOException {
        this.parser.advance(Symbol.NULL);
        this.in.readNull();
    }

    @Override
    public boolean readBoolean() throws IOException {
        this.parser.advance(Symbol.BOOLEAN);
        return this.in.readBoolean();
    }

    @Override
    public int readInt() throws IOException {
        this.parser.advance(Symbol.INT);
        return this.in.readInt();
    }

    @Override
    public long readLong() throws IOException {
        this.parser.advance(Symbol.LONG);
        return this.in.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        this.parser.advance(Symbol.FLOAT);
        return this.in.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        this.parser.advance(Symbol.DOUBLE);
        return this.in.readDouble();
    }

    @Override
    public Utf8 readString(Utf8 old) throws IOException {
        this.parser.advance(Symbol.STRING);
        return this.in.readString(old);
    }

    @Override
    public String readString() throws IOException {
        this.parser.advance(Symbol.STRING);
        return this.in.readString();
    }

    @Override
    public void skipString() throws IOException {
        this.parser.advance(Symbol.STRING);
        this.in.skipString();
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer old) throws IOException {
        this.parser.advance(Symbol.BYTES);
        return this.in.readBytes(old);
    }

    @Override
    public void skipBytes() throws IOException {
        this.parser.advance(Symbol.BYTES);
        this.in.skipBytes();
    }

    private void checkFixed(int size) throws IOException {
        this.parser.advance(Symbol.FIXED);
        Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        if (size != top.size) {
            throw new AvroTypeException("Incorrect length for fixed binary: expected " + top.size + " but received " + size + " bytes.");
        }
    }

    @Override
    public void readFixed(byte[] bytes, int start, int len) throws IOException {
        this.checkFixed(len);
        this.in.readFixed(bytes, start, len);
    }

    @Override
    public void skipFixed(int length) throws IOException {
        this.checkFixed(length);
        this.in.skipFixed(length);
    }

    @Override
    protected void skipFixed() throws IOException {
        this.parser.advance(Symbol.FIXED);
        Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        this.in.skipFixed(top.size);
    }

    @Override
    public int readEnum() throws IOException {
        this.parser.advance(Symbol.ENUM);
        Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        int result = this.in.readEnum();
        if (result < 0 || result >= top.size) {
            throw new AvroTypeException("Enumeration out of range: max is " + top.size + " but received " + result);
        }
        return result;
    }

    @Override
    public long readArrayStart() throws IOException {
        this.parser.advance(Symbol.ARRAY_START);
        long result = this.in.readArrayStart();
        if (result == 0L) {
            this.parser.advance(Symbol.ARRAY_END);
        }
        return result;
    }

    @Override
    public long arrayNext() throws IOException {
        this.parser.processTrailingImplicitActions();
        long result = this.in.arrayNext();
        if (result == 0L) {
            this.parser.advance(Symbol.ARRAY_END);
        }
        return result;
    }

    @Override
    public long skipArray() throws IOException {
        this.parser.advance(Symbol.ARRAY_START);
        long c = this.in.skipArray();
        while (c != 0L) {
            while (c-- > 0L) {
                this.parser.skipRepeater();
            }
            c = this.in.skipArray();
        }
        this.parser.advance(Symbol.ARRAY_END);
        return 0L;
    }

    @Override
    public long readMapStart() throws IOException {
        this.parser.advance(Symbol.MAP_START);
        long result = this.in.readMapStart();
        if (result == 0L) {
            this.parser.advance(Symbol.MAP_END);
        }
        return result;
    }

    @Override
    public long mapNext() throws IOException {
        this.parser.processTrailingImplicitActions();
        long result = this.in.mapNext();
        if (result == 0L) {
            this.parser.advance(Symbol.MAP_END);
        }
        return result;
    }

    @Override
    public long skipMap() throws IOException {
        this.parser.advance(Symbol.MAP_START);
        long c = this.in.skipMap();
        while (c != 0L) {
            while (c-- > 0L) {
                this.parser.skipRepeater();
            }
            c = this.in.skipMap();
        }
        this.parser.advance(Symbol.MAP_END);
        return 0L;
    }

    @Override
    public int readIndex() throws IOException {
        this.parser.advance(Symbol.UNION);
        Symbol.Alternative top = (Symbol.Alternative)this.parser.popSymbol();
        int result = this.in.readIndex();
        this.parser.pushSymbol(top.getSymbol(result));
        return result;
    }

    @Override
    public Symbol doAction(Symbol input, Symbol top) throws IOException {
        return null;
    }
}

