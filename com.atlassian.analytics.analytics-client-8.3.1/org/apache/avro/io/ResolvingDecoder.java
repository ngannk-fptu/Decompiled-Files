/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.ValidatingDecoder;
import org.apache.avro.io.parsing.ResolvingGrammarGenerator;
import org.apache.avro.io.parsing.Symbol;
import org.apache.avro.util.Utf8;

public class ResolvingDecoder
extends ValidatingDecoder {
    private Decoder backup;

    ResolvingDecoder(Schema writer, Schema reader, Decoder in) throws IOException {
        this(ResolvingDecoder.resolve(writer, reader), in);
    }

    private ResolvingDecoder(Object resolver, Decoder in) throws IOException {
        super((Symbol)resolver, in);
    }

    public static Object resolve(Schema writer, Schema reader) throws IOException {
        Objects.requireNonNull(writer, "Writer schema cannot be null");
        Objects.requireNonNull(reader, "Reader schema cannot be null");
        return new ResolvingGrammarGenerator().generate(writer, reader);
    }

    public final Schema.Field[] readFieldOrder() throws IOException {
        return ((Symbol.FieldOrderAction)this.parser.advance((Symbol)Symbol.FIELD_ACTION)).fields;
    }

    public final Schema.Field[] readFieldOrderIfDiff() throws IOException {
        Symbol.FieldOrderAction top = (Symbol.FieldOrderAction)this.parser.advance(Symbol.FIELD_ACTION);
        return top.noReorder ? null : top.fields;
    }

    public final void drain() throws IOException {
        this.parser.processImplicitActions();
    }

    @Override
    public long readLong() throws IOException {
        Symbol actual = this.parser.advance(Symbol.LONG);
        if (actual == Symbol.INT) {
            return this.in.readInt();
        }
        if (actual == Symbol.DOUBLE) {
            return (long)this.in.readDouble();
        }
        assert (actual == Symbol.LONG);
        return this.in.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        Symbol actual = this.parser.advance(Symbol.FLOAT);
        if (actual == Symbol.INT) {
            return this.in.readInt();
        }
        if (actual == Symbol.LONG) {
            return this.in.readLong();
        }
        assert (actual == Symbol.FLOAT);
        return this.in.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        Symbol actual = this.parser.advance(Symbol.DOUBLE);
        if (actual == Symbol.INT) {
            return this.in.readInt();
        }
        if (actual == Symbol.LONG) {
            return this.in.readLong();
        }
        if (actual == Symbol.FLOAT) {
            return this.in.readFloat();
        }
        assert (actual == Symbol.DOUBLE);
        return this.in.readDouble();
    }

    @Override
    public Utf8 readString(Utf8 old) throws IOException {
        Symbol actual = this.parser.advance(Symbol.STRING);
        if (actual == Symbol.BYTES) {
            return new Utf8(this.in.readBytes(null).array());
        }
        assert (actual == Symbol.STRING);
        return this.in.readString(old);
    }

    @Override
    public String readString() throws IOException {
        Symbol actual = this.parser.advance(Symbol.STRING);
        if (actual == Symbol.BYTES) {
            return new String(this.in.readBytes(null).array(), StandardCharsets.UTF_8);
        }
        assert (actual == Symbol.STRING);
        return this.in.readString();
    }

    @Override
    public void skipString() throws IOException {
        Symbol actual = this.parser.advance(Symbol.STRING);
        if (actual == Symbol.BYTES) {
            this.in.skipBytes();
        } else {
            assert (actual == Symbol.STRING);
            this.in.skipString();
        }
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer old) throws IOException {
        Symbol actual = this.parser.advance(Symbol.BYTES);
        if (actual == Symbol.STRING) {
            Utf8 s = this.in.readString(null);
            return ByteBuffer.wrap(s.getBytes(), 0, s.getByteLength());
        }
        assert (actual == Symbol.BYTES);
        return this.in.readBytes(old);
    }

    @Override
    public void skipBytes() throws IOException {
        Symbol actual = this.parser.advance(Symbol.BYTES);
        if (actual == Symbol.STRING) {
            this.in.skipString();
        } else {
            assert (actual == Symbol.BYTES);
            this.in.skipBytes();
        }
    }

    @Override
    public int readEnum() throws IOException {
        this.parser.advance(Symbol.ENUM);
        Symbol.EnumAdjustAction top = (Symbol.EnumAdjustAction)this.parser.popSymbol();
        int n = this.in.readEnum();
        if (top.noAdjustments) {
            return n;
        }
        Object o = top.adjustments[n];
        if (o instanceof Integer) {
            return (Integer)o;
        }
        throw new AvroTypeException((String)o);
    }

    @Override
    public int readIndex() throws IOException {
        int result;
        this.parser.advance(Symbol.UNION);
        Symbol top = this.parser.popSymbol();
        if (top instanceof Symbol.UnionAdjustAction) {
            result = ((Symbol.UnionAdjustAction)top).rindex;
            top = ((Symbol.UnionAdjustAction)top).symToParse;
        } else {
            result = this.in.readIndex();
            top = ((Symbol.Alternative)top).getSymbol(result);
        }
        this.parser.pushSymbol(top);
        return result;
    }

    @Override
    public Symbol doAction(Symbol input, Symbol top) throws IOException {
        if (top instanceof Symbol.FieldOrderAction) {
            return input == Symbol.FIELD_ACTION ? top : null;
        }
        if (top instanceof Symbol.ResolvingAction) {
            Symbol.ResolvingAction t = (Symbol.ResolvingAction)top;
            if (t.reader != input) {
                throw new AvroTypeException("Found " + t.reader + " while looking for " + input);
            }
            return t.writer;
        }
        if (top instanceof Symbol.SkipAction) {
            Symbol symToSkip = ((Symbol.SkipAction)top).symToSkip;
            this.parser.skipSymbol(symToSkip);
        } else if (top instanceof Symbol.WriterUnionAction) {
            Symbol.Alternative branches = (Symbol.Alternative)this.parser.popSymbol();
            this.parser.pushSymbol(branches.getSymbol(this.in.readIndex()));
        } else {
            if (top instanceof Symbol.ErrorAction) {
                throw new AvroTypeException(((Symbol.ErrorAction)top).msg);
            }
            if (top instanceof Symbol.DefaultStartAction) {
                Symbol.DefaultStartAction dsa = (Symbol.DefaultStartAction)top;
                this.backup = this.in;
                this.in = DecoderFactory.get().binaryDecoder(dsa.contents, null);
            } else if (top == Symbol.DEFAULT_END_ACTION) {
                this.in = this.backup;
            } else {
                throw new AvroTypeException("Unknown action: " + top);
            }
        }
        return null;
    }

    @Override
    public void skipAction() throws IOException {
        Symbol top = this.parser.popSymbol();
        if (top instanceof Symbol.ResolvingAction) {
            this.parser.pushSymbol(((Symbol.ResolvingAction)top).writer);
        } else if (top instanceof Symbol.SkipAction) {
            this.parser.pushSymbol(((Symbol.SkipAction)top).symToSkip);
        } else if (top instanceof Symbol.WriterUnionAction) {
            Symbol.Alternative branches = (Symbol.Alternative)this.parser.popSymbol();
            this.parser.pushSymbol(branches.getSymbol(this.in.readIndex()));
        } else {
            if (top instanceof Symbol.ErrorAction) {
                throw new AvroTypeException(((Symbol.ErrorAction)top).msg);
            }
            if (top instanceof Symbol.DefaultStartAction) {
                Symbol.DefaultStartAction dsa = (Symbol.DefaultStartAction)top;
                this.backup = this.in;
                this.in = DecoderFactory.get().binaryDecoder(dsa.contents, null);
            } else if (top == Symbol.DEFAULT_END_ACTION) {
                this.in = this.backup;
            }
        }
    }
}

