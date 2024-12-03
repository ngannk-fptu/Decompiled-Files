/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.impl;

import java.io.ByteArrayInputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.format.InputAccessor;
import org.codehaus.jackson.format.MatchStrength;
import org.codehaus.jackson.impl.ReaderBasedParser;
import org.codehaus.jackson.impl.Utf8StreamParser;
import org.codehaus.jackson.io.IOContext;
import org.codehaus.jackson.io.MergedStream;
import org.codehaus.jackson.io.UTF32Reader;
import org.codehaus.jackson.sym.BytesToNameCanonicalizer;
import org.codehaus.jackson.sym.CharsToNameCanonicalizer;

public final class ByteSourceBootstrapper {
    static final byte UTF8_BOM_1 = -17;
    static final byte UTF8_BOM_2 = -69;
    static final byte UTF8_BOM_3 = -65;
    protected final IOContext _context;
    protected final InputStream _in;
    protected final byte[] _inputBuffer;
    private int _inputPtr;
    private int _inputEnd;
    private final boolean _bufferRecyclable;
    protected int _inputProcessed;
    protected boolean _bigEndian = true;
    protected int _bytesPerChar = 0;

    public ByteSourceBootstrapper(IOContext ctxt, InputStream in) {
        this._context = ctxt;
        this._in = in;
        this._inputBuffer = ctxt.allocReadIOBuffer();
        this._inputPtr = 0;
        this._inputEnd = 0;
        this._inputProcessed = 0;
        this._bufferRecyclable = true;
    }

    public ByteSourceBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen) {
        this._context = ctxt;
        this._in = null;
        this._inputBuffer = inputBuffer;
        this._inputPtr = inputStart;
        this._inputEnd = inputStart + inputLen;
        this._inputProcessed = -inputStart;
        this._bufferRecyclable = false;
    }

    public JsonEncoding detectEncoding() throws IOException, JsonParseException {
        JsonEncoding enc;
        int i16;
        boolean foundEncoding = false;
        if (this.ensureLoaded(4)) {
            int quad = this._inputBuffer[this._inputPtr] << 24 | (this._inputBuffer[this._inputPtr + 1] & 0xFF) << 16 | (this._inputBuffer[this._inputPtr + 2] & 0xFF) << 8 | this._inputBuffer[this._inputPtr + 3] & 0xFF;
            if (this.handleBOM(quad)) {
                foundEncoding = true;
            } else if (this.checkUTF32(quad)) {
                foundEncoding = true;
            } else if (this.checkUTF16(quad >>> 16)) {
                foundEncoding = true;
            }
        } else if (this.ensureLoaded(2) && this.checkUTF16(i16 = (this._inputBuffer[this._inputPtr] & 0xFF) << 8 | this._inputBuffer[this._inputPtr + 1] & 0xFF)) {
            foundEncoding = true;
        }
        if (!foundEncoding) {
            enc = JsonEncoding.UTF8;
        } else {
            switch (this._bytesPerChar) {
                case 1: {
                    enc = JsonEncoding.UTF8;
                    break;
                }
                case 2: {
                    enc = this._bigEndian ? JsonEncoding.UTF16_BE : JsonEncoding.UTF16_LE;
                    break;
                }
                case 4: {
                    enc = this._bigEndian ? JsonEncoding.UTF32_BE : JsonEncoding.UTF32_LE;
                    break;
                }
                default: {
                    throw new RuntimeException("Internal error");
                }
            }
        }
        this._context.setEncoding(enc);
        return enc;
    }

    public Reader constructReader() throws IOException {
        JsonEncoding enc = this._context.getEncoding();
        switch (enc) {
            case UTF32_BE: 
            case UTF32_LE: {
                return new UTF32Reader(this._context, this._in, this._inputBuffer, this._inputPtr, this._inputEnd, this._context.getEncoding().isBigEndian());
            }
            case UTF16_BE: 
            case UTF16_LE: 
            case UTF8: {
                InputStream in = this._in;
                if (in == null) {
                    in = new ByteArrayInputStream(this._inputBuffer, this._inputPtr, this._inputEnd);
                } else if (this._inputPtr < this._inputEnd) {
                    in = new MergedStream(this._context, in, this._inputBuffer, this._inputPtr, this._inputEnd);
                }
                return new InputStreamReader(in, enc.getJavaName());
            }
        }
        throw new RuntimeException("Internal error");
    }

    public JsonParser constructParser(int features, ObjectCodec codec, BytesToNameCanonicalizer rootByteSymbols, CharsToNameCanonicalizer rootCharSymbols) throws IOException, JsonParseException {
        JsonEncoding enc = this.detectEncoding();
        boolean canonicalize = JsonParser.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(features);
        boolean intern = JsonParser.Feature.INTERN_FIELD_NAMES.enabledIn(features);
        if (enc == JsonEncoding.UTF8 && canonicalize) {
            BytesToNameCanonicalizer can = rootByteSymbols.makeChild(canonicalize, intern);
            return new Utf8StreamParser(this._context, features, this._in, codec, can, this._inputBuffer, this._inputPtr, this._inputEnd, this._bufferRecyclable);
        }
        return new ReaderBasedParser(this._context, features, this.constructReader(), codec, rootCharSymbols.makeChild(canonicalize, intern));
    }

    public static MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
        int ch;
        if (!acc.hasMoreBytes()) {
            return MatchStrength.INCONCLUSIVE;
        }
        byte b = acc.nextByte();
        if (b == -17) {
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != -69) {
                return MatchStrength.NO_MATCH;
            }
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != -65) {
                return MatchStrength.NO_MATCH;
            }
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            b = acc.nextByte();
        }
        if ((ch = ByteSourceBootstrapper.skipSpace(acc, b)) < 0) {
            return MatchStrength.INCONCLUSIVE;
        }
        if (ch == 123) {
            ch = ByteSourceBootstrapper.skipSpace(acc);
            if (ch < 0) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (ch == 34 || ch == 125) {
                return MatchStrength.SOLID_MATCH;
            }
            return MatchStrength.NO_MATCH;
        }
        if (ch == 91) {
            ch = ByteSourceBootstrapper.skipSpace(acc);
            if (ch < 0) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (ch == 93 || ch == 91) {
                return MatchStrength.SOLID_MATCH;
            }
            return MatchStrength.SOLID_MATCH;
        }
        MatchStrength strength = MatchStrength.WEAK_MATCH;
        if (ch == 34) {
            return strength;
        }
        if (ch <= 57 && ch >= 48) {
            return strength;
        }
        if (ch == 45) {
            ch = ByteSourceBootstrapper.skipSpace(acc);
            if (ch < 0) {
                return MatchStrength.INCONCLUSIVE;
            }
            return ch <= 57 && ch >= 48 ? strength : MatchStrength.NO_MATCH;
        }
        if (ch == 110) {
            return ByteSourceBootstrapper.tryMatch(acc, "ull", strength);
        }
        if (ch == 116) {
            return ByteSourceBootstrapper.tryMatch(acc, "rue", strength);
        }
        if (ch == 102) {
            return ByteSourceBootstrapper.tryMatch(acc, "alse", strength);
        }
        return MatchStrength.NO_MATCH;
    }

    private static final MatchStrength tryMatch(InputAccessor acc, String matchStr, MatchStrength fullMatchStrength) throws IOException {
        int len = matchStr.length();
        for (int i = 0; i < len; ++i) {
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() == matchStr.charAt(i)) continue;
            return MatchStrength.NO_MATCH;
        }
        return fullMatchStrength;
    }

    private static final int skipSpace(InputAccessor acc) throws IOException {
        if (!acc.hasMoreBytes()) {
            return -1;
        }
        return ByteSourceBootstrapper.skipSpace(acc, acc.nextByte());
    }

    private static final int skipSpace(InputAccessor acc, byte b) throws IOException {
        int ch;
        while ((ch = b & 0xFF) == 32 || ch == 13 || ch == 10 || ch == 9) {
            if (!acc.hasMoreBytes()) {
                return -1;
            }
            b = acc.nextByte();
            int n = b & 0xFF;
        }
        return ch;
    }

    private boolean handleBOM(int quad) throws IOException {
        switch (quad) {
            case 65279: {
                this._bigEndian = true;
                this._inputPtr += 4;
                this._bytesPerChar = 4;
                return true;
            }
            case -131072: {
                this._inputPtr += 4;
                this._bytesPerChar = 4;
                this._bigEndian = false;
                return true;
            }
            case 65534: {
                this.reportWeirdUCS4("2143");
            }
            case -16842752: {
                this.reportWeirdUCS4("3412");
            }
        }
        int msw = quad >>> 16;
        if (msw == 65279) {
            this._inputPtr += 2;
            this._bytesPerChar = 2;
            this._bigEndian = true;
            return true;
        }
        if (msw == 65534) {
            this._inputPtr += 2;
            this._bytesPerChar = 2;
            this._bigEndian = false;
            return true;
        }
        if (quad >>> 8 == 0xEFBBBF) {
            this._inputPtr += 3;
            this._bytesPerChar = 1;
            this._bigEndian = true;
            return true;
        }
        return false;
    }

    private boolean checkUTF32(int quad) throws IOException {
        if (quad >> 8 == 0) {
            this._bigEndian = true;
        } else if ((quad & 0xFFFFFF) == 0) {
            this._bigEndian = false;
        } else if ((quad & 0xFF00FFFF) == 0) {
            this.reportWeirdUCS4("3412");
        } else if ((quad & 0xFFFF00FF) == 0) {
            this.reportWeirdUCS4("2143");
        } else {
            return false;
        }
        this._bytesPerChar = 4;
        return true;
    }

    private boolean checkUTF16(int i16) {
        if ((i16 & 0xFF00) == 0) {
            this._bigEndian = true;
        } else if ((i16 & 0xFF) == 0) {
            this._bigEndian = false;
        } else {
            return false;
        }
        this._bytesPerChar = 2;
        return true;
    }

    private void reportWeirdUCS4(String type) throws IOException {
        throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
    }

    protected boolean ensureLoaded(int minimum) throws IOException {
        int count;
        for (int gotten = this._inputEnd - this._inputPtr; gotten < minimum; gotten += count) {
            count = this._in == null ? -1 : this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
            if (count < 1) {
                return false;
            }
            this._inputEnd += count;
        }
        return true;
    }
}

