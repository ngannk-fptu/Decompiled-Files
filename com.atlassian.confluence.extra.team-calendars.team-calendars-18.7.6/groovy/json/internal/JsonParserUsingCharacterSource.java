/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.JsonException;
import groovy.json.internal.BaseJsonParser;
import groovy.json.internal.CharBuf;
import groovy.json.internal.CharScanner;
import groovy.json.internal.CharacterSource;
import groovy.json.internal.Chr;
import groovy.json.internal.LazyMap;
import groovy.json.internal.ReaderCharacterSource;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JsonParserUsingCharacterSource
extends BaseJsonParser {
    private CharacterSource characterSource;
    protected static final char[] NULL = Chr.chars("null");
    protected static final char[] TRUE = Chr.chars("true");
    protected static char[] FALSE = Chr.chars("false");
    private CharBuf builder = CharBuf.create(20);

    protected String exceptionDetails(String message) {
        return this.characterSource.errorDetails(message);
    }

    protected final Object decodeJsonObject() {
        LazyMap map = new LazyMap();
        try {
            CharacterSource characterSource = this.characterSource;
            if (characterSource.currentChar() == 123) {
                characterSource.nextChar();
            }
            while (characterSource.hasChar()) {
                int ch;
                characterSource.skipWhiteSpace();
                if (characterSource.currentChar() == 34) {
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
                    characterSource.skipWhiteSpace();
                    if (characterSource.currentChar() != 58) {
                        this.complain("expecting current character to be : but was " + this.charDescription(characterSource.currentChar()) + "\n");
                    }
                    characterSource.nextChar();
                    characterSource.skipWhiteSpace();
                    Object value = this.decodeValue();
                    characterSource.skipWhiteSpace();
                    map.put(key, value);
                }
                if ((ch = characterSource.currentChar()) == 125) {
                    characterSource.nextChar();
                    break;
                }
                if (ch == 44) {
                    characterSource.nextChar();
                    continue;
                }
                this.complain("expecting '}' or ',' but got current char " + this.charDescription(ch));
            }
        }
        catch (Exception ex) {
            throw new JsonException(this.exceptionDetails("Unable to parse JSON object"), ex);
        }
        return map;
    }

    protected final void complain(String complaint) {
        throw new JsonException(this.exceptionDetails(complaint));
    }

    private Object decodeValue() {
        CharacterSource characterSource = this.characterSource;
        Object value = null;
        characterSource.skipWhiteSpace();
        switch (characterSource.currentChar()) {
            case 34: {
                value = this.decodeString();
                break;
            }
            case 116: {
                value = this.decodeTrue();
                break;
            }
            case 102: {
                value = this.decodeFalse();
                break;
            }
            case 110: {
                value = this.decodeNull();
                break;
            }
            case 91: {
                value = this.decodeJsonArray();
                break;
            }
            case 123: {
                value = this.decodeJsonObject();
                break;
            }
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                value = this.decodeNumber(false);
                break;
            }
            case 45: {
                value = this.decodeNumber(true);
                break;
            }
            default: {
                throw new JsonException(this.exceptionDetails("Unable to determine the current character, it is not a string, number, array, or object"));
            }
        }
        return value;
    }

    private Object decodeNumber(boolean negative) {
        char[] chars = this.characterSource.readNumber();
        Number value = null;
        if (CharScanner.hasDecimalChar(chars, negative)) {
            value = CharScanner.parseBigDecimal(chars);
        } else if (CharScanner.isInteger(chars)) {
            value = CharScanner.parseInt(chars);
        } else if (CharScanner.isLong(chars)) {
            value = CharScanner.parseLong(chars);
        }
        return value;
    }

    protected final Object decodeNull() {
        if (!this.characterSource.consumeIfMatch(NULL)) {
            throw new JsonException(this.exceptionDetails("null not parse properly"));
        }
        return null;
    }

    protected final boolean decodeTrue() {
        if (this.characterSource.consumeIfMatch(TRUE)) {
            return true;
        }
        throw new JsonException(this.exceptionDetails("true not parsed properly"));
    }

    protected final boolean decodeFalse() {
        if (this.characterSource.consumeIfMatch(FALSE)) {
            return false;
        }
        throw new JsonException(this.exceptionDetails("false not parsed properly"));
    }

    private String decodeString() {
        CharacterSource characterSource = this.characterSource;
        characterSource.nextChar();
        char[] chars = characterSource.findNextChar(34, 92);
        String value = null;
        if (characterSource.hadEscape()) {
            value = this.builder.decodeJsonString(chars).toString();
            this.builder.recycle();
        } else {
            value = new String(chars);
        }
        return value;
    }

    protected final List decodeJsonArray() {
        ArrayList<Object> list = null;
        boolean foundEnd = false;
        try {
            CharacterSource characterSource = this.characterSource;
            if (this.characterSource.currentChar() == 91) {
                characterSource.nextChar();
            }
            characterSource.skipWhiteSpace();
            if (this.characterSource.currentChar() == 93) {
                characterSource.nextChar();
                return new ArrayList();
            }
            list = new ArrayList<Object>();
            do {
                characterSource.skipWhiteSpace();
                Object arrayItem = this.decodeValue();
                list.add(arrayItem);
                characterSource.skipWhiteSpace();
                int c = characterSource.currentChar();
                if (c == 44) {
                    characterSource.nextChar();
                    continue;
                }
                if (c == 93) {
                    foundEnd = true;
                    characterSource.nextChar();
                    break;
                }
                String charString = this.charDescription(c);
                this.complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array index of %s \n", charString, list.size()));
            } while (characterSource.hasChar());
        }
        catch (Exception ex) {
            throw new JsonException(this.exceptionDetails("Unexpected issue"), ex);
        }
        if (!foundEnd) {
            throw new JsonException(this.exceptionDetails("Could not find end of JSON array"));
        }
        return list;
    }

    @Override
    public Object parse(Reader reader) {
        this.characterSource = new ReaderCharacterSource(reader);
        return this.decodeValue();
    }

    @Override
    public Object parse(char[] chars) {
        return this.parse(new StringReader(new String(chars)));
    }
}

