/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.parser;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import net.minidev.json.parser.ParseException;
import net.minidev.json.writer.JsonReader;
import net.minidev.json.writer.JsonReaderI;

abstract class JSONParserBase {
    protected char c;
    public static final int MAX_DEPTH = 400;
    protected int depth = 0;
    JsonReader base;
    public static final byte EOI = 26;
    protected static final char MAX_STOP = '~';
    private String lastKey;
    protected static boolean[] stopAll = new boolean[126];
    protected static boolean[] stopArray = new boolean[126];
    protected static boolean[] stopKey = new boolean[126];
    protected static boolean[] stopValue = new boolean[126];
    protected static boolean[] stopX = new boolean[126];
    protected final MSB sb = new MSB(15);
    protected Object xo;
    protected String xs;
    protected int pos;
    protected final boolean acceptLeadinZero;
    protected final boolean acceptNaN;
    protected final boolean acceptNonQuote;
    protected final boolean acceptSimpleQuote;
    protected final boolean acceptUselessComma;
    protected final boolean checkTaillingData;
    protected final boolean checkTaillingSpace;
    protected final boolean ignoreControlChar;
    protected final boolean useHiPrecisionFloat;
    protected final boolean useIntegerStorage;
    protected final boolean reject127;
    protected final boolean unrestictBigDigit;

    public JSONParserBase(int permissiveMode) {
        this.acceptNaN = (permissiveMode & 4) > 0;
        this.acceptNonQuote = (permissiveMode & 2) > 0;
        this.acceptSimpleQuote = (permissiveMode & 1) > 0;
        this.ignoreControlChar = (permissiveMode & 8) > 0;
        this.useIntegerStorage = (permissiveMode & 0x10) > 0;
        this.acceptLeadinZero = (permissiveMode & 0x20) > 0;
        this.acceptUselessComma = (permissiveMode & 0x40) > 0;
        this.useHiPrecisionFloat = (permissiveMode & 0x80) > 0;
        this.checkTaillingData = (permissiveMode & 0x300) != 768;
        this.checkTaillingSpace = (permissiveMode & 0x200) == 0;
        this.reject127 = (permissiveMode & 0x400) > 0;
        this.unrestictBigDigit = (permissiveMode & 0x800) > 0;
    }

    public void checkControleChar() throws ParseException {
        if (this.ignoreControlChar) {
            return;
        }
        int l = this.xs.length();
        for (int i = 0; i < l; ++i) {
            char c = this.xs.charAt(i);
            if (c < '\u0000') continue;
            if (c <= '\u001f') {
                throw new ParseException(this.pos + i, 0, Character.valueOf(c));
            }
            if (c != '\u007f' || !this.reject127) continue;
            throw new ParseException(this.pos + i, 0, Character.valueOf(c));
        }
    }

    public void checkLeadinZero() throws ParseException {
        int len = this.xs.length();
        if (len == 1) {
            return;
        }
        if (len == 2) {
            if (this.xs.equals("00")) {
                throw new ParseException(this.pos, 6, this.xs);
            }
            return;
        }
        char c1 = this.xs.charAt(0);
        char c2 = this.xs.charAt(1);
        if (c1 == '-') {
            char c3 = this.xs.charAt(2);
            if (c2 == '0' && c3 >= '0' && c3 <= '9') {
                throw new ParseException(this.pos, 6, this.xs);
            }
            return;
        }
        if (c1 == '0' && c2 >= '0' && c2 <= '9') {
            throw new ParseException(this.pos, 6, this.xs);
        }
    }

    protected Number extractFloat() throws ParseException {
        if (!this.acceptLeadinZero) {
            this.checkLeadinZero();
        }
        try {
            if (!this.useHiPrecisionFloat) {
                return Float.valueOf(Float.parseFloat(this.xs));
            }
            if (this.xs.length() > 18) {
                double asDouble;
                String doubleStr;
                if (!this.unrestictBigDigit && this.compareDoublePrecision(doubleStr = String.valueOf(asDouble = Double.parseDouble(this.xs)), this.xs)) {
                    return asDouble;
                }
                return new BigDecimal(this.xs);
            }
            return Double.parseDouble(this.xs);
        }
        catch (NumberFormatException e) {
            throw new ParseException(this.pos, 1, this.xs);
        }
    }

    private boolean compareDoublePrecision(String convert, String origin) {
        char[] originArray;
        char[] charArray = convert.toCharArray();
        if (charArray.length > (originArray = origin.toCharArray()).length) {
            return false;
        }
        int j = 0;
        for (int i = 0; i < charArray.length; ++i) {
            if (charArray[i] < '0' || charArray[i] > '9') {
                if (originArray[j] >= '0' && originArray[j] <= '9') {
                    return false;
                }
                if (originArray[++j] != '+') continue;
                ++j;
                continue;
            }
            if (charArray[i] != originArray[j]) {
                return false;
            }
            ++j;
        }
        return j == originArray.length;
    }

    protected <T> T parse(JsonReaderI<T> mapper) throws ParseException {
        T result;
        this.pos = -1;
        try {
            this.read();
            result = this.readFirst(mapper);
            if (this.checkTaillingData) {
                if (!this.checkTaillingSpace) {
                    this.skipSpace();
                }
                if (this.c != '\u001a') {
                    throw new ParseException(this.pos - 1, 1, Character.valueOf(this.c));
                }
            }
        }
        catch (IOException e) {
            throw new ParseException(this.pos, (Throwable)e);
        }
        this.xs = null;
        this.xo = null;
        return result;
    }

    protected Number parseNumber(String s) throws ParseException {
        boolean mustCheck;
        boolean neg;
        int p = 0;
        int l = s.length();
        int max = 19;
        if (s.charAt(0) == '-') {
            ++p;
            ++max;
            neg = true;
            if (!this.acceptLeadinZero && l >= 3 && s.charAt(1) == '0') {
                throw new ParseException(this.pos, 6, s);
            }
        } else {
            neg = false;
            if (!this.acceptLeadinZero && l >= 2 && s.charAt(0) == '0') {
                throw new ParseException(this.pos, 6, s);
            }
        }
        if (l < max) {
            max = l;
            mustCheck = false;
        } else {
            if (l > max) {
                return new BigInteger(s, 10);
            }
            max = l - 1;
            mustCheck = true;
        }
        long r = 0L;
        while (p < max) {
            r = r * 10L + (long)(48 - s.charAt(p++));
        }
        if (mustCheck) {
            boolean isBig;
            if (r > -922337203685477580L) {
                isBig = false;
            } else if (r < -922337203685477580L) {
                isBig = true;
            } else if (neg) {
                isBig = s.charAt(p) > '8';
            } else {
                boolean bl = isBig = s.charAt(p) > '7';
            }
            if (isBig) {
                return new BigInteger(s, 10);
            }
            r = r * 10L + (long)(48 - s.charAt(p));
        }
        if (neg) {
            if (this.useIntegerStorage && r >= Integer.MIN_VALUE) {
                return (int)r;
            }
            return r;
        }
        r = -r;
        if (this.useIntegerStorage && r <= Integer.MAX_VALUE) {
            return (int)r;
        }
        return r;
    }

    protected abstract void read() throws IOException;

    protected <T> T readArray(JsonReaderI<T> mapper) throws ParseException, IOException {
        if (this.c != '[') {
            throw new RuntimeException("Internal Error");
        }
        if (++this.depth > 400) {
            throw new ParseException(this.pos, 7, Character.valueOf(this.c));
        }
        Object current = mapper.createArray();
        this.read();
        boolean needData = false;
        if (this.c == ',' && !this.acceptUselessComma) {
            throw new ParseException(this.pos, 0, Character.valueOf(this.c));
        }
        block7: while (true) {
            switch (this.c) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    this.read();
                    continue block7;
                }
                case ']': {
                    if (needData && !this.acceptUselessComma) {
                        throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                    }
                    --this.depth;
                    this.read();
                    return mapper.convert(current);
                }
                case ':': 
                case '}': {
                    throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                }
                case ',': {
                    if (needData && !this.acceptUselessComma) {
                        throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                    }
                    this.read();
                    needData = true;
                    continue block7;
                }
                case '\u001a': {
                    throw new ParseException(this.pos - 1, 3, "EOF");
                }
            }
            mapper.addValue(current, this.readMain(mapper, stopArray));
            needData = false;
        }
    }

    protected <T> T readFirst(JsonReaderI<T> mapper) throws ParseException, IOException {
        block12: while (true) {
            switch (this.c) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    this.read();
                    continue block12;
                }
                case ':': 
                case ']': 
                case '}': {
                    throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                }
                case '{': {
                    return this.readObject(mapper);
                }
                case '[': {
                    return this.readArray(mapper);
                }
                case '\"': 
                case '\'': {
                    this.readString();
                    return mapper.convert(this.xs);
                }
                case 'n': {
                    this.readNQString(stopX);
                    if ("null".equals(this.xs)) {
                        return null;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
                case 'f': {
                    this.readNQString(stopX);
                    if ("false".equals(this.xs)) {
                        return mapper.convert(Boolean.FALSE);
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
                case 't': {
                    this.readNQString(stopX);
                    if ("true".equals(this.xs)) {
                        return mapper.convert(Boolean.TRUE);
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
                case 'N': {
                    this.readNQString(stopX);
                    if (!this.acceptNaN) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    if ("NaN".equals(this.xs)) {
                        return mapper.convert(Float.valueOf(Float.NaN));
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
                case '-': 
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': {
                    this.xo = this.readNumber(stopX);
                    return mapper.convert(this.xo);
                }
            }
            break;
        }
        this.readNQString(stopX);
        if (!this.acceptNonQuote) {
            throw new ParseException(this.pos, 1, this.xs);
        }
        return mapper.convert(this.xs);
    }

    protected Object readMain(JsonReaderI<?> mapper, boolean[] stop) throws ParseException, IOException {
        block12: while (true) {
            switch (this.c) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    this.read();
                    continue block12;
                }
                case ':': 
                case ']': 
                case '}': {
                    throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                }
                case '{': {
                    return this.readObject(mapper.startObject(this.lastKey));
                }
                case '[': {
                    return this.readArray(mapper.startArray(this.lastKey));
                }
                case '\"': 
                case '\'': {
                    this.readString();
                    return this.xs;
                }
                case 'n': {
                    this.readNQString(stop);
                    if ("null".equals(this.xs)) {
                        return null;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                case 'f': {
                    this.readNQString(stop);
                    if ("false".equals(this.xs)) {
                        return Boolean.FALSE;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                case 't': {
                    this.readNQString(stop);
                    if ("true".equals(this.xs)) {
                        return Boolean.TRUE;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                case 'N': {
                    this.readNQString(stop);
                    if (!this.acceptNaN) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    if ("NaN".equals(this.xs)) {
                        return Float.valueOf(Float.NaN);
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                case '-': 
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': {
                    return this.readNumber(stop);
                }
            }
            break;
        }
        this.readNQString(stop);
        if (!this.acceptNonQuote) {
            throw new ParseException(this.pos, 1, this.xs);
        }
        return this.xs;
    }

    protected abstract void readNoEnd() throws ParseException, IOException;

    protected abstract void readNQString(boolean[] var1) throws IOException;

    protected abstract Object readNumber(boolean[] var1) throws ParseException, IOException;

    protected <T> T readObject(JsonReaderI<T> mapper) throws ParseException, IOException {
        if (this.c != '{') {
            throw new RuntimeException("Internal Error");
        }
        if (++this.depth > 400) {
            throw new ParseException(this.pos, 7, Character.valueOf(this.c));
        }
        Object current = mapper.createObject();
        boolean needData = false;
        boolean acceptData = true;
        block6: while (true) {
            this.read();
            switch (this.c) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    continue block6;
                }
                case ':': 
                case '[': 
                case ']': 
                case '{': {
                    throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                }
                case '}': {
                    if (needData && !this.acceptUselessComma) {
                        throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                    }
                    --this.depth;
                    this.read();
                    return mapper.convert(current);
                }
                case ',': {
                    if (needData && !this.acceptUselessComma) {
                        throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                    }
                    needData = true;
                    acceptData = true;
                    continue block6;
                }
            }
            if (this.c == '\"' || this.c == '\'') {
                this.readString();
            } else {
                this.readNQString(stopKey);
                if (!this.acceptNonQuote) {
                    throw new ParseException(this.pos, 1, this.xs);
                }
            }
            String key = this.xs;
            if (!acceptData) {
                throw new ParseException(this.pos, 1, key);
            }
            this.skipSpace();
            if (this.c != ':') {
                if (this.c == '\u001a') {
                    throw new ParseException(this.pos - 1, 3, null);
                }
                throw new ParseException(this.pos - 1, 0, Character.valueOf(this.c));
            }
            this.readNoEnd();
            this.lastKey = key;
            Object value = this.readMain(mapper, stopValue);
            mapper.setValue(current, key, value);
            this.lastKey = null;
            this.skipSpace();
            if (this.c == '}') {
                --this.depth;
                this.read();
                return mapper.convert(current);
            }
            if (this.c == '\u001a') {
                throw new ParseException(this.pos - 1, 3, null);
            }
            if (this.c != ',') break;
            needData = true;
            acceptData = true;
        }
        throw new ParseException(this.pos - 1, 1, Character.valueOf(this.c));
    }

    abstract void readS() throws IOException;

    protected abstract void readString() throws ParseException, IOException;

    protected void readString2() throws ParseException, IOException {
        char sep = this.c;
        block20: while (true) {
            this.read();
            switch (this.c) {
                case '\u001a': {
                    throw new ParseException(this.pos - 1, 3, null);
                }
                case '\"': 
                case '\'': {
                    if (sep == this.c) {
                        this.read();
                        this.xs = this.sb.toString();
                        return;
                    }
                    this.sb.append(this.c);
                    continue block20;
                }
                case '\\': {
                    this.read();
                    switch (this.c) {
                        case 't': {
                            this.sb.append('\t');
                            continue block20;
                        }
                        case 'n': {
                            this.sb.append('\n');
                            continue block20;
                        }
                        case 'r': {
                            this.sb.append('\r');
                            continue block20;
                        }
                        case 'f': {
                            this.sb.append('\f');
                            continue block20;
                        }
                        case 'b': {
                            this.sb.append('\b');
                            continue block20;
                        }
                        case '\\': {
                            this.sb.append('\\');
                            continue block20;
                        }
                        case '/': {
                            this.sb.append('/');
                            continue block20;
                        }
                        case '\'': {
                            this.sb.append('\'');
                            continue block20;
                        }
                        case '\"': {
                            this.sb.append('\"');
                            continue block20;
                        }
                        case 'u': {
                            this.sb.append(this.readUnicode(4));
                            continue block20;
                        }
                        case 'x': {
                            this.sb.append(this.readUnicode(2));
                            continue block20;
                        }
                    }
                    continue block20;
                }
                case '\u0000': 
                case '\u0001': 
                case '\u0002': 
                case '\u0003': 
                case '\u0004': 
                case '\u0005': 
                case '\u0006': 
                case '\u0007': 
                case '\b': 
                case '\t': 
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case '\u000e': 
                case '\u000f': 
                case '\u0010': 
                case '\u0011': 
                case '\u0012': 
                case '\u0013': 
                case '\u0014': 
                case '\u0015': 
                case '\u0016': 
                case '\u0017': 
                case '\u0018': 
                case '\u0019': 
                case '\u001b': 
                case '\u001c': 
                case '\u001d': 
                case '\u001e': 
                case '\u001f': {
                    if (this.ignoreControlChar) continue block20;
                    throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                }
                case '\u007f': {
                    if (this.ignoreControlChar) continue block20;
                    if (!this.reject127) break;
                    throw new ParseException(this.pos, 0, Character.valueOf(this.c));
                }
            }
            this.sb.append(this.c);
        }
    }

    protected char readUnicode(int totalChars) throws ParseException, IOException {
        int value = 0;
        for (int i = 0; i < totalChars; ++i) {
            value *= 16;
            this.read();
            if (this.c <= '9' && this.c >= '0') {
                value += this.c - 48;
                continue;
            }
            if (this.c <= 'F' && this.c >= 'A') {
                value += this.c - 65 + 10;
                continue;
            }
            if (this.c >= 'a' && this.c <= 'f') {
                value += this.c - 97 + 10;
                continue;
            }
            if (this.c == '\u001a') {
                throw new ParseException(this.pos, 3, "EOF");
            }
            throw new ParseException(this.pos, 4, Character.valueOf(this.c));
        }
        return (char)value;
    }

    protected void skipDigits() throws IOException {
        while (this.c >= '0' && this.c <= '9') {
            this.readS();
        }
        return;
    }

    protected void skipNQString(boolean[] stop) throws IOException {
        while (!(this.c == '\u001a' || this.c >= '\u0000' && this.c < '~' && stop[this.c])) {
            this.readS();
        }
        return;
    }

    protected void skipSpace() throws IOException {
        while (this.c <= ' ' && this.c != '\u001a') {
            this.readS();
        }
        return;
    }

    static {
        JSONParserBase.stopKey[26] = true;
        JSONParserBase.stopKey[58] = true;
        JSONParserBase.stopValue[26] = true;
        JSONParserBase.stopValue[125] = true;
        JSONParserBase.stopValue[44] = true;
        JSONParserBase.stopArray[26] = true;
        JSONParserBase.stopArray[93] = true;
        JSONParserBase.stopArray[44] = true;
        JSONParserBase.stopX[26] = true;
        JSONParserBase.stopAll[58] = true;
        JSONParserBase.stopAll[44] = true;
        JSONParserBase.stopAll[26] = true;
        JSONParserBase.stopAll[125] = true;
        JSONParserBase.stopAll[93] = true;
    }

    public static class MSB {
        char[] b;
        int p;

        public MSB(int size) {
            this.b = new char[size];
            this.p = -1;
        }

        public void append(char c) {
            ++this.p;
            if (this.b.length <= this.p) {
                char[] t = new char[this.b.length * 2 + 1];
                System.arraycopy(this.b, 0, t, 0, this.b.length);
                this.b = t;
            }
            this.b[this.p] = c;
        }

        public void append(int c) {
            ++this.p;
            if (this.b.length <= this.p) {
                char[] t = new char[this.b.length * 2 + 1];
                System.arraycopy(this.b, 0, t, 0, this.b.length);
                this.b = t;
            }
            this.b[this.p] = (char)c;
        }

        public String toString() {
            return new String(this.b, 0, this.p + 1);
        }

        public void clear() {
            this.p = -1;
        }
    }
}

