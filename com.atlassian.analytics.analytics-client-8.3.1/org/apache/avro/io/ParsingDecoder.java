/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.parsing.Parser;
import org.apache.avro.io.parsing.SkipParser;
import org.apache.avro.io.parsing.Symbol;

public abstract class ParsingDecoder
extends Decoder
implements Parser.ActionHandler,
SkipParser.SkipHandler {
    protected final SkipParser parser;

    protected ParsingDecoder(Symbol root) throws IOException {
        this.parser = new SkipParser(root, this, this);
    }

    protected abstract void skipFixed() throws IOException;

    @Override
    public void skipAction() throws IOException {
        this.parser.popSymbol();
    }

    @Override
    public void skipTopSymbol() throws IOException {
        Symbol top = this.parser.topSymbol();
        if (top == Symbol.NULL) {
            this.readNull();
        } else if (top == Symbol.BOOLEAN) {
            this.readBoolean();
        } else if (top == Symbol.INT) {
            this.readInt();
        } else if (top == Symbol.LONG) {
            this.readLong();
        } else if (top == Symbol.FLOAT) {
            this.readFloat();
        } else if (top == Symbol.DOUBLE) {
            this.readDouble();
        } else if (top == Symbol.STRING) {
            this.skipString();
        } else if (top == Symbol.BYTES) {
            this.skipBytes();
        } else if (top == Symbol.ENUM) {
            this.readEnum();
        } else if (top == Symbol.FIXED) {
            this.skipFixed();
        } else if (top == Symbol.UNION) {
            this.readIndex();
        } else if (top == Symbol.ARRAY_START) {
            this.skipArray();
        } else if (top == Symbol.MAP_START) {
            this.skipMap();
        }
    }
}

