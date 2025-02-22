/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.dataformat.cbor.CBORConstants;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import java.io.IOException;
import java.io.InputStream;

public class CBORParserBootstrapper {
    protected final IOContext _context;
    protected final InputStream _in;
    protected final byte[] _inputBuffer;
    protected int _inputPtr;
    protected int _inputEnd;
    protected final boolean _bufferRecyclable;
    protected int _inputProcessed;

    public CBORParserBootstrapper(IOContext ctxt, InputStream in) {
        this._context = ctxt;
        this._in = in;
        this._inputBuffer = ctxt.allocReadIOBuffer();
        this._inputPtr = 0;
        this._inputEnd = 0;
        this._inputProcessed = 0;
        this._bufferRecyclable = true;
    }

    public CBORParserBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen) {
        this._context = ctxt;
        this._in = null;
        this._inputBuffer = inputBuffer;
        this._inputPtr = inputStart;
        this._inputEnd = inputStart + inputLen;
        this._inputProcessed = -inputStart;
        this._bufferRecyclable = false;
    }

    public CBORParser constructParser(int factoryFeatures, int generalParserFeatures, int formatFeatures, ObjectCodec codec, ByteQuadsCanonicalizer rootByteSymbols) throws IOException, JsonParseException {
        ByteQuadsCanonicalizer can = rootByteSymbols.makeChildOrPlaceholder(factoryFeatures);
        this.ensureLoaded(1);
        CBORParser p = new CBORParser(this._context, generalParserFeatures, formatFeatures, codec, can, this._in, this._inputBuffer, this._inputPtr, this._inputEnd, this._bufferRecyclable);
        if (this._inputPtr < this._inputEnd) {
            // empty if block
        }
        return p;
    }

    public static MatchStrength hasCBORFormat(InputAccessor acc) throws IOException {
        if (!acc.hasMoreBytes()) {
            return MatchStrength.INCONCLUSIVE;
        }
        byte b = acc.nextByte();
        if (b == -65) {
            if (acc.hasMoreBytes()) {
                b = acc.nextByte();
                if (b == -1) {
                    return MatchStrength.SOLID_MATCH;
                }
                if (CBORConstants.hasMajorType(3, b)) {
                    return MatchStrength.SOLID_MATCH;
                }
                return MatchStrength.INCONCLUSIVE;
            }
        } else if (b == -97) {
            if (acc.hasMoreBytes()) {
                b = acc.nextByte();
                if (b == -1) {
                    return MatchStrength.SOLID_MATCH;
                }
                return MatchStrength.WEAK_MATCH;
            }
        } else {
            if (CBORConstants.hasMajorType(6, b)) {
                if (b == -39 && acc.hasMoreBytes() && (b = acc.nextByte()) == -39 && acc.hasMoreBytes() && (b = acc.nextByte()) == -9) {
                    return MatchStrength.FULL_MATCH;
                }
                return MatchStrength.WEAK_MATCH;
            }
            if (CBORConstants.hasMajorType(7, b)) {
                if (b == -12 || b == -11 || b == -10) {
                    return MatchStrength.SOLID_MATCH;
                }
                return MatchStrength.NO_MATCH;
            }
        }
        return MatchStrength.INCONCLUSIVE;
    }

    protected boolean ensureLoaded(int minimum) throws IOException {
        int count;
        if (this._in == null) {
            return false;
        }
        for (int gotten = this._inputEnd - this._inputPtr; gotten < minimum; gotten += count) {
            count = this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
            if (count < 1) {
                return false;
            }
            this._inputEnd += count;
        }
        return true;
    }
}

