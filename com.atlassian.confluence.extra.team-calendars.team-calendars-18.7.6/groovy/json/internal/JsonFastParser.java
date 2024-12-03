/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.CharSequenceValue;
import groovy.json.internal.JsonParserCharArray;
import groovy.json.internal.LazyValueMap;
import groovy.json.internal.MapItemValue;
import groovy.json.internal.NumberValue;
import groovy.json.internal.Type;
import groovy.json.internal.Value;
import groovy.json.internal.ValueContainer;
import groovy.json.internal.ValueList;
import groovy.json.internal.ValueMap;
import groovy.json.internal.ValueMapImpl;
import java.util.AbstractList;
import java.util.ArrayList;

public class JsonFastParser
extends JsonParserCharArray {
    private final boolean useValues;
    private final boolean chop;
    private final boolean lazyChop;
    private final boolean checkDates;

    public JsonFastParser() {
        this(true);
    }

    public JsonFastParser(boolean useValues) {
        this(useValues, false);
    }

    public JsonFastParser(boolean useValues, boolean chop) {
        this(useValues, chop, !chop);
    }

    public JsonFastParser(boolean useValues, boolean chop, boolean lazyChop) {
        this(useValues, chop, lazyChop, true);
    }

    public JsonFastParser(boolean useValues, boolean chop, boolean lazyChop, boolean checkDates) {
        this.useValues = useValues;
        this.chop = chop;
        this.lazyChop = lazyChop;
        this.checkDates = checkDates;
    }

    protected final Value decodeJsonObjectLazyFinalParse() {
        char[] array = this.charArray;
        if (this.__currentChar == '{') {
            ++this.__index;
        }
        ValueMap map = (ValueMap)((Object)(this.useValues ? new ValueMapImpl() : new LazyValueMap(this.lazyChop)));
        ValueContainer value = new ValueContainer(map);
        block7: while (this.__index < array.length) {
            this.skipWhiteSpace();
            switch (this.__currentChar) {
                case '\"': {
                    Value key = this.decodeStringOverlay();
                    this.skipWhiteSpace();
                    if (this.__currentChar != ':') {
                        this.complain("expecting current character to be " + this.charDescription(this.__currentChar) + "\n");
                    }
                    ++this.__index;
                    Value item = this.decodeValueOverlay();
                    this.skipWhiteSpace();
                    MapItemValue miv = new MapItemValue(key, item);
                    map.add(miv);
                }
            }
            switch (this.__currentChar) {
                case '}': {
                    ++this.__index;
                    break block7;
                }
                case ',': {
                    break;
                }
                default: {
                    this.complain("expecting '}' or ',' but got current char " + this.charDescription(this.__currentChar));
                }
            }
            ++this.__index;
        }
        return value;
    }

    @Override
    protected Value decodeValue() {
        return this.decodeValueOverlay();
    }

    private Value decodeValueOverlay() {
        this.skipWhiteSpace();
        switch (this.__currentChar) {
            case '\"': {
                return this.decodeStringOverlay();
            }
            case '{': {
                return this.decodeJsonObjectLazyFinalParse();
            }
            case 't': {
                return this.decodeTrue() ? ValueContainer.TRUE : ValueContainer.FALSE;
            }
            case 'f': {
                return !this.decodeFalse() ? ValueContainer.FALSE : ValueContainer.TRUE;
            }
            case 'n': {
                return this.decodeNull() == null ? ValueContainer.NULL : ValueContainer.NULL;
            }
            case '[': {
                return this.decodeJsonArrayOverlay();
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
                return this.decodeNumberOverlay(false);
            }
            case '-': {
                return this.decodeNumberOverlay(true);
            }
        }
        this.complain("Unable to determine the current character, it is not a string, number, array, or object");
        return null;
    }

    private Value decodeNumberOverlay(boolean minus) {
        char prevChar;
        char currentChar;
        char[] array = this.charArray;
        int startIndex = this.__index;
        int index = this.__index;
        boolean doubleFloat = false;
        boolean foundDot = false;
        boolean foundSign = false;
        boolean foundExp = false;
        if (minus && index + 1 < array.length) {
            ++index;
        }
        do {
            if (JsonFastParser.isNumberDigit(currentChar = array[index])) continue;
            if (currentChar <= ' ' || JsonFastParser.isDelimiter(currentChar)) break;
            if (JsonFastParser.isDecimalChar(currentChar)) {
                switch (currentChar) {
                    case '.': {
                        if (foundDot || foundExp) {
                            this.complain("unexpected character " + currentChar);
                        }
                        foundDot = true;
                        break;
                    }
                    case 'E': 
                    case 'e': {
                        if (foundExp) {
                            this.complain("unexpected character " + currentChar);
                        }
                        foundExp = true;
                        break;
                    }
                    case '+': 
                    case '-': {
                        if (foundSign || !foundExp) {
                            this.complain("unexpected character " + currentChar);
                        }
                        if (foundExp && array[index - 1] != 'e' && array[index - 1] != 'E') {
                            this.complain("unexpected character " + currentChar);
                        }
                        foundSign = true;
                    }
                }
                doubleFloat = true;
                continue;
            }
            this.complain("unexpected character " + currentChar);
        } while (++index < array.length);
        if (foundExp && ((prevChar = array[index - 1]) == 'e' || prevChar == 'E' || prevChar == '-' || prevChar == '+')) {
            this.complain("unexpected character " + currentChar);
        }
        this.__index = index;
        this.__currentChar = currentChar;
        Type type = doubleFloat ? Type.DOUBLE : Type.INTEGER;
        return new NumberValue(this.chop, type, startIndex, this.__index, this.charArray);
    }

    private Value decodeStringOverlay() {
        char[] array = this.charArray;
        int index = this.__index;
        char currentChar = this.charArray[index];
        if (index < array.length && currentChar == '\"') {
            ++index;
        }
        int startIndex = index;
        boolean encoded = JsonFastParser.hasEscapeChar(array, index, this.indexHolder);
        index = this.indexHolder[0];
        if (encoded) {
            index = JsonFastParser.findEndQuote(array, index);
        }
        CharSequenceValue value = new CharSequenceValue(this.chop, Type.STRING, startIndex, index, array, encoded, this.checkDates);
        if (index < array.length) {
            ++index;
        }
        this.__index = index;
        return value;
    }

    private Value decodeJsonArrayOverlay() {
        char[] array = this.charArray;
        if (this.__currentChar == '[') {
            ++this.__index;
        }
        this.skipWhiteSpace();
        if (this.__currentChar == ']') {
            ++this.__index;
            return new ValueContainer(new ArrayList<Object>());
        }
        AbstractList list = this.useValues ? new ArrayList() : new ValueList(this.lazyChop);
        ValueContainer value = new ValueContainer(list);
        boolean foundEnd = false;
        block8: while (this.__index < array.length) {
            Value item = this.decodeValueOverlay();
            list.add((Object)item);
            char c = this.currentChar();
            block0 : switch (c) {
                case ',': {
                    break;
                }
                case ']': {
                    ++this.__index;
                    foundEnd = true;
                    break block8;
                }
                default: {
                    int lastIndex = this.__index;
                    this.skipWhiteSpace();
                    c = this.currentChar();
                    switch (c) {
                        case ',': {
                            break block0;
                        }
                        case ']': {
                            if (this.__index == lastIndex) {
                                this.complain("missing ]");
                            }
                            foundEnd = true;
                            ++this.__index;
                            break block8;
                        }
                        default: {
                            this.complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array size of %s \n", this.charDescription(this.__currentChar), list.size()));
                        }
                    }
                }
            }
            ++this.__index;
        }
        if (!foundEnd) {
            this.complain("Did not find end of Json Array");
        }
        return value;
    }

    @Override
    protected final Object decodeFromChars(char[] cs) {
        Value value = (Value)super.decodeFromChars(cs);
        if (value.isContainer()) {
            return value.toValue();
        }
        return value;
    }
}

