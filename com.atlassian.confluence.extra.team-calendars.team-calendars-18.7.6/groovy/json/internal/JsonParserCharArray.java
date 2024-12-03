/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.JsonException;
import groovy.json.internal.BaseJsonParser;
import groovy.json.internal.CharBuf;
import groovy.json.internal.CharScanner;
import groovy.json.internal.Chr;
import groovy.json.internal.LazyMap;
import java.util.ArrayList;
import java.util.List;

public class JsonParserCharArray
extends BaseJsonParser {
    protected char[] charArray;
    protected int __index;
    protected char __currentChar;
    private int lastIndex;
    int[] endIndex = new int[1];
    protected static final char[] NULL = Chr.chars("null");
    protected static final char[] TRUE = Chr.chars("true");
    protected static char[] FALSE = Chr.chars("false");
    private CharBuf builder = CharBuf.create(20);

    protected Object decodeFromChars(char[] cs) {
        this.__index = 0;
        this.charArray = cs;
        this.lastIndex = cs.length - 1;
        return this.decodeValue();
    }

    protected final boolean hasMore() {
        return this.__index < this.lastIndex;
    }

    protected final boolean hasCurrent() {
        return this.__index <= this.lastIndex;
    }

    protected final void skipWhiteSpace() {
        int ix = this.__index;
        if (this.hasCurrent()) {
            this.__currentChar = this.charArray[ix];
        }
        if (this.__currentChar <= ' ') {
            ix = JsonParserCharArray.skipWhiteSpaceFast(this.charArray, ix);
            this.__currentChar = this.charArray[ix];
            this.__index = ix;
        }
    }

    protected final char nextChar() {
        try {
            if (this.hasMore()) {
                ++this.__index;
                this.__currentChar = this.charArray[this.__index];
                return this.__currentChar;
            }
            return '\u0000';
        }
        catch (Exception ex) {
            throw new JsonException(this.exceptionDetails("unable to advance character"), ex);
        }
    }

    protected String exceptionDetails(String message) {
        return CharScanner.errorDetails(message, this.charArray, this.__index, this.__currentChar);
    }

    private static int skipWhiteSpaceFast(char[] array, int index) {
        while (index < array.length) {
            char c = array[index];
            if (c > ' ') {
                return index;
            }
            ++index;
        }
        return index - 1;
    }

    protected final Object decodeJsonObject() {
        if (this.__currentChar == '{') {
            ++this.__index;
        }
        LazyMap map = new LazyMap();
        while (this.__index < this.charArray.length) {
            this.skipWhiteSpace();
            if (this.__currentChar == '\"') {
                String key = this.decodeString();
                if (internKeys) {
                    String keyPrime = (String)internedKeysCache.get(key);
                    if (keyPrime == null) {
                        key = key.intern();
                        internedKeysCache.put(key, key);
                    } else {
                        key = keyPrime;
                    }
                }
                this.skipWhiteSpace();
                if (this.__currentChar != ':') {
                    this.complain("expecting current character to be " + this.charDescription(this.__currentChar) + "\n");
                }
                ++this.__index;
                this.skipWhiteSpace();
                Object value = this.decodeValueInternal();
                this.skipWhiteSpace();
                map.put(key, value);
            }
            if (this.__currentChar == '}') {
                ++this.__index;
                break;
            }
            if (this.__currentChar != ',') {
                this.complain("expecting '}' or ',' but got current char " + this.charDescription(this.__currentChar));
            }
            ++this.__index;
        }
        return map;
    }

    protected final void complain(String complaint) {
        throw new JsonException(this.exceptionDetails(complaint));
    }

    protected Object decodeValue() {
        return this.decodeValueInternal();
    }

    private Object decodeValueInternal() {
        Object value = null;
        this.skipWhiteSpace();
        switch (this.__currentChar) {
            case '\"': {
                value = this.decodeString();
                break;
            }
            case 't': {
                value = this.decodeTrue();
                break;
            }
            case 'f': {
                value = this.decodeFalse();
                break;
            }
            case 'n': {
                value = this.decodeNull();
                break;
            }
            case '[': {
                value = this.decodeJsonArray();
                break;
            }
            case '{': {
                value = this.decodeJsonObject();
                break;
            }
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
                value = this.decodeNumber();
                break;
            }
            case '-': {
                value = this.decodeNumber();
                break;
            }
            default: {
                throw new JsonException(this.exceptionDetails("Unable to determine the current character, it is not a string, number, array, or object"));
            }
        }
        return value;
    }

    private Object decodeNumber() {
        Number num = CharScanner.parseJsonNumber(this.charArray, this.__index, this.charArray.length, this.endIndex);
        this.__index = this.endIndex[0];
        return num;
    }

    protected final Object decodeNull() {
        if (this.__index + NULL.length <= this.charArray.length && this.charArray[this.__index] == 'n' && this.charArray[++this.__index] == 'u' && this.charArray[++this.__index] == 'l' && this.charArray[++this.__index] == 'l') {
            ++this.__index;
            return null;
        }
        throw new JsonException(this.exceptionDetails("null not parse properly"));
    }

    protected final boolean decodeTrue() {
        if (this.__index + TRUE.length <= this.charArray.length && this.charArray[this.__index] == 't' && this.charArray[++this.__index] == 'r' && this.charArray[++this.__index] == 'u' && this.charArray[++this.__index] == 'e') {
            ++this.__index;
            return true;
        }
        throw new JsonException(this.exceptionDetails("true not parsed properly"));
    }

    protected final boolean decodeFalse() {
        if (this.__index + FALSE.length <= this.charArray.length && this.charArray[this.__index] == 'f' && this.charArray[++this.__index] == 'a' && this.charArray[++this.__index] == 'l' && this.charArray[++this.__index] == 's' && this.charArray[++this.__index] == 'e') {
            ++this.__index;
            return false;
        }
        throw new JsonException(this.exceptionDetails("false not parsed properly"));
    }

    private String decodeString() {
        char[] array = this.charArray;
        int index = this.__index;
        char currentChar = array[index];
        if (index < array.length && currentChar == '\"') {
            ++index;
        }
        int startIndex = index;
        boolean encoded = JsonParserCharArray.hasEscapeChar(array, index, this.indexHolder);
        index = this.indexHolder[0];
        String value = null;
        if (encoded) {
            index = JsonParserCharArray.findEndQuote(array, index);
            value = this.builder.decodeJsonString(array, startIndex, index).toString();
            this.builder.recycle();
        } else {
            value = new String(array, startIndex, index - startIndex);
        }
        if (index < this.charArray.length) {
            ++index;
        }
        this.__index = index;
        return value;
    }

    protected final List decodeJsonArray() {
        ArrayList<Object> list = null;
        boolean foundEnd = false;
        char[] charArray = this.charArray;
        try {
            if (this.__currentChar == '[') {
                ++this.__index;
            }
            this.skipWhiteSpace();
            if (this.__currentChar == ']') {
                ++this.__index;
                return new ArrayList();
            }
            list = new ArrayList<Object>();
            while (this.hasMore()) {
                Object arrayItem = this.decodeValueInternal();
                list.add(arrayItem);
                char c = charArray[this.__index];
                if (c == ',') {
                    ++this.__index;
                    continue;
                }
                if (c == ']') {
                    ++this.__index;
                    foundEnd = true;
                    break;
                }
                int lastIndex = this.__index;
                this.skipWhiteSpace();
                c = charArray[this.__index];
                if (c == ',') {
                    ++this.__index;
                    continue;
                }
                if (c == ']' && lastIndex != this.__index) {
                    ++this.__index;
                    foundEnd = true;
                    break;
                }
                String charString = this.charDescription(c);
                this.complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array index of %s \n", charString, list.size()));
            }
        }
        catch (Exception ex) {
            if (ex instanceof JsonException) {
                throw (JsonException)ex;
            }
            throw new JsonException(this.exceptionDetails("issue parsing JSON array"), ex);
        }
        if (!foundEnd) {
            this.complain("Did not find end of Json Array");
        }
        return list;
    }

    protected final char currentChar() {
        if (this.__index > this.lastIndex) {
            return '\u0000';
        }
        return this.charArray[this.__index];
    }

    @Override
    public Object parse(char[] chars) {
        return this.decodeFromChars(chars);
    }
}

