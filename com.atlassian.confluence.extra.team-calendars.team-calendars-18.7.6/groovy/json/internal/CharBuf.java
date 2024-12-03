/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.JsonException;
import groovy.json.internal.ArrayUtils;
import groovy.json.internal.ByteScanner;
import groovy.json.internal.Cache;
import groovy.json.internal.CacheType;
import groovy.json.internal.CharScanner;
import groovy.json.internal.Chr;
import groovy.json.internal.Exceptions;
import groovy.json.internal.FastStringUtils;
import groovy.json.internal.SimpleCache;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

public class CharBuf
extends Writer
implements CharSequence {
    protected int capacity = 16;
    protected int location = 0;
    protected char[] buffer;
    private Cache<Integer, char[]> icache;
    final char[] trueChars = "true".toCharArray();
    final char[] falseChars = "false".toCharArray();
    private Cache<Double, char[]> dcache;
    private Cache<Float, char[]> fcache;
    final byte[] encoded = new byte[2];
    final byte[] charTo = new byte[2];
    private static final char[] EMPTY_STRING_CHARS = Chr.array('\"', '\"');
    static final char[] nullChars = "null".toCharArray();
    private Cache<BigDecimal, char[]> bigDCache;
    private Cache<BigInteger, char[]> bigICache;
    private Cache<Long, char[]> lcache;

    public CharBuf(char[] buffer) {
        this.__init__(buffer);
    }

    private void __init__(char[] buffer) {
        this.buffer = buffer;
        this.capacity = buffer.length;
    }

    public CharBuf(byte[] bytes) {
        this.buffer = null;
        try {
            String str = new String(bytes, "UTF-8");
            this.__init__(FastStringUtils.toCharArray(str));
        }
        catch (UnsupportedEncodingException e) {
            Exceptions.handle(e);
        }
    }

    public static CharBuf createExact(int capacity) {
        return new CharBuf(capacity){

            @Override
            public CharBuf add(char[] chars) {
                Chr._idx(this.buffer, this.location, chars);
                this.location += chars.length;
                return this;
            }
        };
    }

    public static CharBuf create(int capacity) {
        return new CharBuf(capacity);
    }

    public static CharBuf create(char[] buffer) {
        return new CharBuf(buffer);
    }

    protected CharBuf(int capacity) {
        this.capacity = capacity;
        this.init();
    }

    protected CharBuf() {
        this.init();
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        if (off == 0 && cbuf.length == len) {
            this.add(cbuf);
        } else {
            char[] buffer = ArrayUtils.copyRange(cbuf, off, off + len);
            this.add(buffer);
        }
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }

    public void init() {
        this.buffer = new char[this.capacity];
    }

    public final CharBuf add(String str) {
        this.add(FastStringUtils.toCharArray(str));
        return this;
    }

    public final CharBuf addString(String str) {
        this.add(FastStringUtils.toCharArray(str));
        return this;
    }

    public final CharBuf add(int i) {
        this.add(Integer.toString(i));
        return this;
    }

    public final CharBuf addInt(int i) {
        switch (i) {
            case 0: {
                this.addChar('0');
                return this;
            }
            case 1: {
                this.addChar('1');
                return this;
            }
            case -1: {
                this.addChar('-');
                this.addChar('1');
                return this;
            }
        }
        this.addInt((Integer)i);
        return this;
    }

    public final CharBuf addInt(Integer key) {
        char[] chars;
        if (this.icache == null) {
            this.icache = new SimpleCache<Integer, char[]>(20, CacheType.LRU);
        }
        if ((chars = this.icache.get(key)) == null) {
            String str = Integer.toString(key);
            chars = FastStringUtils.toCharArray(str);
            this.icache.put(key, chars);
        }
        this.addChars(chars);
        return this;
    }

    public final CharBuf add(boolean b) {
        this.addChars(b ? this.trueChars : this.falseChars);
        return this;
    }

    public final CharBuf addBoolean(boolean b) {
        this.add(Boolean.toString(b));
        return this;
    }

    public final CharBuf add(byte i) {
        this.add(Byte.toString(i));
        return this;
    }

    public final CharBuf addByte(byte i) {
        this.addInt(i);
        return this;
    }

    public final CharBuf add(short i) {
        this.add(Short.toString(i));
        return this;
    }

    public final CharBuf addShort(short i) {
        this.addInt(i);
        return this;
    }

    public final CharBuf add(long l) {
        this.add(Long.toString(l));
        return this;
    }

    public final CharBuf add(double d) {
        this.add(Double.toString(d));
        return this;
    }

    public final CharBuf addDouble(double d) {
        this.addDouble((Double)d);
        return this;
    }

    public final CharBuf addDouble(Double key) {
        char[] chars;
        if (this.dcache == null) {
            this.dcache = new SimpleCache<Double, char[]>(20, CacheType.LRU);
        }
        if ((chars = this.dcache.get(key)) == null) {
            String str = Double.toString(key);
            chars = FastStringUtils.toCharArray(str);
            this.dcache.put(key, chars);
        }
        this.add(chars);
        return this;
    }

    public final CharBuf add(float d) {
        this.add(Float.toString(d));
        return this;
    }

    public final CharBuf addFloat(float d) {
        this.addFloat(Float.valueOf(d));
        return this;
    }

    public final CharBuf addFloat(Float key) {
        char[] chars;
        if (this.fcache == null) {
            this.fcache = new SimpleCache<Float, char[]>(20, CacheType.LRU);
        }
        if ((chars = this.fcache.get(key)) == null) {
            String str = Float.toString(key.floatValue());
            chars = FastStringUtils.toCharArray(str);
            this.fcache.put(key, chars);
        }
        this.add(chars);
        return this;
    }

    public final CharBuf addChar(byte i) {
        this.add((char)i);
        return this;
    }

    public final CharBuf addChar(int i) {
        this.add((char)i);
        return this;
    }

    public final CharBuf addChar(short i) {
        this.add((char)i);
        return this;
    }

    public final CharBuf addChar(char ch) {
        int _location = this.location;
        char[] _buffer = this.buffer;
        int _capacity = this.capacity;
        if (1 + _location > _capacity) {
            _buffer = Chr.grow(_buffer);
            _capacity = _buffer.length;
        }
        _buffer[_location] = ch;
        this.location = ++_location;
        this.buffer = _buffer;
        this.capacity = _capacity;
        return this;
    }

    public CharBuf addLine(String str) {
        this.add(str.toCharArray());
        this.add('\n');
        return this;
    }

    public CharBuf addLine(CharSequence str) {
        this.add(str.toString());
        this.add('\n');
        return this;
    }

    public CharBuf add(char[] chars) {
        if (chars.length + this.location > this.capacity) {
            this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + chars.length);
            this.capacity = this.buffer.length;
        }
        Chr._idx(this.buffer, this.location, chars);
        this.location += chars.length;
        return this;
    }

    public final CharBuf addChars(char[] chars) {
        if (chars.length + this.location > this.capacity) {
            this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + chars.length);
            this.capacity = this.buffer.length;
        }
        System.arraycopy(chars, 0, this.buffer, this.location, chars.length);
        this.location += chars.length;
        return this;
    }

    public final CharBuf addQuoted(char[] chars) {
        int _location = this.location;
        char[] _buffer = this.buffer;
        int sizeNeeded = chars.length + 2 + _location;
        int _capacity = this.capacity;
        if (sizeNeeded > _capacity) {
            _buffer = Chr.grow(_buffer, sizeNeeded * 2);
            _capacity = _buffer.length;
        }
        _buffer[_location] = 34;
        System.arraycopy(chars, 0, _buffer, ++_location, chars.length);
        _buffer[_location += chars.length] = 34;
        this.location = ++_location;
        this.buffer = _buffer;
        this.capacity = _capacity;
        return this;
    }

    public final CharBuf addJsonEscapedString(String jsonString) {
        char[] charArray = FastStringUtils.toCharArray(jsonString);
        return this.addJsonEscapedString(charArray);
    }

    private static boolean hasAnyJSONControlOrUnicodeChars(int c) {
        if (c < 30) {
            return true;
        }
        if (c == 34) {
            return true;
        }
        if (c == 92) {
            return true;
        }
        return c < 32 || c > 126;
    }

    private static boolean hasAnyJSONControlChars(char[] charArray) {
        int index = 0;
        do {
            char c;
            if (!CharBuf.hasAnyJSONControlOrUnicodeChars(c = charArray[index])) continue;
            return true;
        } while (++index < charArray.length);
        return false;
    }

    public final CharBuf addJsonEscapedString(char[] charArray) {
        if (charArray.length == 0) {
            return this;
        }
        if (CharBuf.hasAnyJSONControlChars(charArray)) {
            return this.doAddJsonEscapedString(charArray);
        }
        return this.addQuoted(charArray);
    }

    private CharBuf doAddJsonEscapedString(char[] charArray) {
        char[] _buffer = this.buffer;
        int _location = this.location;
        byte[] _encoded = this.encoded;
        byte[] _charTo = this.charTo;
        int ensureThisMuch = charArray.length * 6 + 2;
        int sizeNeeded = ensureThisMuch + _location;
        if (sizeNeeded > this.capacity) {
            int growBy = _buffer.length * 2 < sizeNeeded ? sizeNeeded : _buffer.length * 2;
            _buffer = Chr.grow(this.buffer, growBy);
            this.capacity = _buffer.length;
        }
        _buffer[_location] = 34;
        ++_location;
        int index = 0;
        do {
            char c;
            if (CharBuf.hasAnyJSONControlOrUnicodeChars(c = charArray[index])) {
                if (_location + 5 > _buffer.length) {
                    _buffer = Chr.grow(_buffer, 20);
                }
                switch (c) {
                    case '\"': {
                        _buffer[_location] = 92;
                        _buffer[++_location] = 34;
                        ++_location;
                        break;
                    }
                    case '\\': {
                        _buffer[_location] = 92;
                        _buffer[++_location] = 92;
                        ++_location;
                        break;
                    }
                    case '\b': {
                        _buffer[_location] = 92;
                        _buffer[++_location] = 98;
                        ++_location;
                        break;
                    }
                    case '\f': {
                        _buffer[_location] = 92;
                        _buffer[++_location] = 102;
                        ++_location;
                        break;
                    }
                    case '\n': {
                        _buffer[_location] = 92;
                        _buffer[++_location] = 110;
                        ++_location;
                        break;
                    }
                    case '\r': {
                        _buffer[_location] = 92;
                        _buffer[++_location] = 114;
                        ++_location;
                        break;
                    }
                    case '\t': {
                        _buffer[_location] = 92;
                        _buffer[++_location] = 116;
                        ++_location;
                        break;
                    }
                    default: {
                        _buffer[_location] = 92;
                        _buffer[++_location] = 117;
                        ++_location;
                        if (c <= '\u00ff') {
                            _buffer[_location] = 48;
                            _buffer[++_location] = 48;
                            ++_location;
                            ByteScanner.encodeByteIntoTwoAsciiCharBytes(c, _encoded);
                            for (byte b : _encoded) {
                                _buffer[_location] = (char)b;
                                ++_location;
                            }
                        } else {
                            _charTo[1] = (byte)c;
                            _charTo[0] = (byte)(c >>> 8);
                            for (byte charByte : _charTo) {
                                ByteScanner.encodeByteIntoTwoAsciiCharBytes(charByte, _encoded);
                                for (byte b : _encoded) {
                                    _buffer[_location] = (char)b;
                                    ++_location;
                                }
                            }
                        }
                    }
                }
                continue;
            }
            _buffer[_location] = c;
            ++_location;
        } while (++index < charArray.length);
        _buffer[_location] = 34;
        this.buffer = _buffer;
        this.location = ++_location;
        return this;
    }

    public final CharBuf addJsonFieldName(String str) {
        return this.addJsonFieldName(FastStringUtils.toCharArray(str));
    }

    public final CharBuf addJsonFieldName(char[] chars) {
        if (chars.length > 0) {
            this.addJsonEscapedString(chars);
        } else {
            this.addChars(EMPTY_STRING_CHARS);
        }
        this.addChar(':');
        return this;
    }

    public final CharBuf addQuoted(String str) {
        char[] chars = FastStringUtils.toCharArray(str);
        this.addQuoted(chars);
        return this;
    }

    public CharBuf add(char[] chars, int length) {
        if (length + this.location < this.capacity) {
            Chr._idx(this.buffer, this.location, chars, length);
        } else {
            this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + length);
            Chr._idx(this.buffer, this.location, chars);
            this.capacity = this.buffer.length;
        }
        this.location += length;
        return this;
    }

    public CharBuf add(byte[] chars) {
        if (chars.length + this.location < this.capacity) {
            Chr._idx(this.buffer, this.location, chars);
        } else {
            this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + chars.length);
            Chr._idx(this.buffer, this.location, chars);
            this.capacity = this.buffer.length;
        }
        this.location += chars.length;
        return this;
    }

    public CharBuf add(byte[] bytes, int start, int end) {
        int charsLength = end - start;
        if (charsLength + this.location > this.capacity) {
            this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + charsLength);
        }
        Chr._idx(this.buffer, this.location, bytes, start, end);
        this.capacity = this.buffer.length;
        this.location += charsLength;
        return this;
    }

    public final CharBuf add(char ch) {
        if (1 + this.location < this.capacity) {
            this.buffer[this.location] = ch;
        } else {
            this.buffer = Chr.grow(this.buffer);
            this.buffer[this.location] = ch;
            this.capacity = this.buffer.length;
        }
        ++this.location;
        return this;
    }

    @Override
    public int length() {
        return this.len();
    }

    @Override
    public char charAt(int index) {
        return this.buffer[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new String(this.buffer, start, end - start);
    }

    @Override
    public String toString() {
        return new String(this.buffer, 0, this.location);
    }

    public String toDebugString() {
        return "CharBuf{capacity=" + this.capacity + ", location=" + this.location + '}';
    }

    public String toStringAndRecycle() {
        String str = new String(this.buffer, 0, this.location);
        this.location = 0;
        return str;
    }

    public int len() {
        return this.location;
    }

    public char[] toCharArray() {
        return this.buffer;
    }

    public void _len(int location) {
        this.location = location;
    }

    public char[] readForRecycle() {
        this.location = 0;
        return this.buffer;
    }

    public void recycle() {
        this.location = 0;
    }

    public double doubleValue() {
        return CharScanner.parseDouble(this.buffer, 0, this.location);
    }

    public float floatValue() {
        return CharScanner.parseFloat(this.buffer, 0, this.location);
    }

    public int intValue() {
        return CharScanner.parseIntFromTo(this.buffer, 0, this.location);
    }

    public long longValue() {
        return CharScanner.parseLongFromTo(this.buffer, 0, this.location);
    }

    public byte byteValue() {
        return (byte)this.intValue();
    }

    public short shortValue() {
        return (short)this.intValue();
    }

    public Number toIntegerWrapper() {
        if (CharScanner.isInteger(this.buffer, 0, this.location)) {
            return this.intValue();
        }
        return this.longValue();
    }

    public final void addNull() {
        this.add(nullChars);
    }

    public void removeLastChar() {
        --this.location;
    }

    public CharBuf addBigDecimal(BigDecimal key) {
        char[] chars;
        if (this.bigDCache == null) {
            this.bigDCache = new SimpleCache<BigDecimal, char[]>(20, CacheType.LRU);
        }
        if ((chars = this.bigDCache.get(key)) == null) {
            String str = key.toString();
            chars = FastStringUtils.toCharArray(str);
            this.bigDCache.put(key, chars);
        }
        this.add(chars);
        return this;
    }

    public CharBuf addBigInteger(BigInteger key) {
        char[] chars;
        if (this.bigICache == null) {
            this.bigICache = new SimpleCache<BigInteger, char[]>(20, CacheType.LRU);
        }
        if ((chars = this.bigICache.get(key)) == null) {
            String str = key.toString();
            chars = FastStringUtils.toCharArray(str);
            this.bigICache.put(key, chars);
        }
        this.add(chars);
        return this;
    }

    public final CharBuf addLong(long l) {
        this.addLong((Long)l);
        return this;
    }

    public final CharBuf addLong(Long key) {
        char[] chars;
        if (this.lcache == null) {
            this.lcache = new SimpleCache<Long, char[]>(20, CacheType.LRU);
        }
        if ((chars = this.lcache.get(key)) == null) {
            String str = Long.toString(key);
            chars = FastStringUtils.toCharArray(str);
            this.lcache.put(key, chars);
        }
        this.add(chars);
        return this;
    }

    public final CharBuf decodeJsonString(char[] chars) {
        return this.decodeJsonString(chars, 0, chars.length);
    }

    public final CharBuf decodeJsonString(char[] chars, int start, int to) {
        int len = to - start;
        char[] buffer = this.buffer;
        int location = this.location;
        if (len > this.capacity) {
            buffer = Chr.grow(buffer, buffer.length * 2 + len);
            this.capacity = buffer.length;
        }
        block11: for (int index = start; index < to; ++index) {
            char c = chars[index];
            if (c == '\\') {
                if (index >= to) continue;
                c = chars[++index];
                switch (c) {
                    case 'n': {
                        buffer[location++] = 10;
                        continue block11;
                    }
                    case '/': {
                        buffer[location++] = 47;
                        continue block11;
                    }
                    case '\"': {
                        buffer[location++] = 34;
                        continue block11;
                    }
                    case 'f': {
                        buffer[location++] = 12;
                        continue block11;
                    }
                    case 't': {
                        buffer[location++] = 9;
                        continue block11;
                    }
                    case '\\': {
                        buffer[location++] = 92;
                        continue block11;
                    }
                    case 'b': {
                        buffer[location++] = 8;
                        continue block11;
                    }
                    case 'r': {
                        buffer[location++] = 13;
                        continue block11;
                    }
                    case 'u': {
                        if (index + 4 >= to) continue block11;
                        String hex = new String(chars, index + 1, 4);
                        char unicode = (char)Integer.parseInt(hex, 16);
                        buffer[location++] = unicode;
                        index += 4;
                        continue block11;
                    }
                    default: {
                        throw new JsonException("Unable to decode string");
                    }
                }
            }
            buffer[location++] = c;
        }
        this.buffer = buffer;
        this.location = location;
        return this;
    }
}

