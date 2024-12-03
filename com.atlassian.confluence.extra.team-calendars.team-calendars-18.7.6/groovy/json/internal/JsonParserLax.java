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

public class JsonParserLax
extends JsonParserCharArray {
    private final boolean useValues;
    private final boolean chop;
    private final boolean lazyChop;
    private final boolean defaultCheckDates;

    public JsonParserLax() {
        this(true);
    }

    public JsonParserLax(boolean useValues) {
        this(useValues, false);
    }

    public JsonParserLax(boolean useValues, boolean chop) {
        this(useValues, chop, !chop);
    }

    public JsonParserLax(boolean useValues, boolean chop, boolean lazyChop) {
        this(useValues, chop, lazyChop, true);
    }

    public JsonParserLax(boolean useValues, boolean chop, boolean lazyChop, boolean defaultCheckDates) {
        this.useValues = useValues;
        this.chop = chop;
        this.lazyChop = lazyChop;
        this.defaultCheckDates = defaultCheckDates;
    }

    /*
     * Enabled aggressive block sorting
     */
    private Value decodeJsonObjectLax() {
        if (this.__currentChar == '{') {
            this.nextChar();
        }
        ValueMap map = (ValueMap)((Object)(this.useValues ? new ValueMapImpl() : new LazyValueMap(this.lazyChop)));
        ValueContainer value = new ValueContainer(map);
        this.skipWhiteSpace();
        int startIndexOfKey = this.__index;
        while (this.__index < this.charArray.length) {
            this.skipWhiteSpace();
            switch (this.__currentChar) {
                case ':': {
                    char startChar = this.charArray[startIndexOfKey];
                    if (startChar == ',') {
                        ++startIndexOfKey;
                    }
                    Value key = this.extractLaxString(startIndexOfKey, this.__index - 1, false, false);
                    Value item = this.decodeValueInternal();
                    this.skipWhiteSpace();
                    MapItemValue miv = new MapItemValue(key, item);
                    map.add(miv);
                    startIndexOfKey = ++this.__index;
                    if (this.__currentChar != '}') break;
                    ++this.__index;
                    return value;
                }
                case '\'': {
                    Value key = this.decodeStringSingle();
                    this.skipWhiteSpace();
                    if (this.__currentChar != ':') {
                        this.complain("expecting current character to be ':' but got " + this.charDescription(this.__currentChar) + "\n");
                    }
                    Value item = this.decodeValueInternal();
                    this.skipWhiteSpace();
                    MapItemValue miv = new MapItemValue(key, item);
                    map.add(miv);
                    startIndexOfKey = ++this.__index;
                    if (this.__currentChar != '}') break;
                    ++this.__index;
                    return value;
                }
                case '\"': {
                    Value key = this.decodeStringDouble();
                    this.skipWhiteSpace();
                    if (this.__currentChar != ':') {
                        this.complain("expecting current character to be ':' but got " + this.charDescription(this.__currentChar) + "\n");
                    }
                    Value item = this.decodeValueInternal();
                    this.skipWhiteSpace();
                    MapItemValue miv = new MapItemValue(key, item);
                    map.add(miv);
                    startIndexOfKey = ++this.__index;
                    if (this.__currentChar != '}') break;
                    ++this.__index;
                    return value;
                }
            }
            switch (this.__currentChar) {
                case '}': {
                    ++this.__index;
                    return value;
                }
                case '/': {
                    this.handleComment();
                    startIndexOfKey = this.__index;
                    break;
                }
                case '#': {
                    this.handleBashComment();
                    startIndexOfKey = this.__index;
                    break;
                }
            }
            ++this.__index;
        }
        return value;
    }

    private Value extractLaxString(int startIndexOfKey, int end, boolean encoded, boolean checkDate) {
        int endIndex;
        block8: while (startIndexOfKey < this.__index && startIndexOfKey < this.charArray.length) {
            char startChar = this.charArray[startIndexOfKey];
            switch (startChar) {
                case '\t': 
                case '\n': 
                case ' ': {
                    break;
                }
                default: {
                    break block8;
                }
            }
            ++startIndexOfKey;
        }
        int n = endIndex = end >= this.charArray.length ? this.charArray.length - 1 : end;
        block9: while (endIndex >= startIndexOfKey + 1 && endIndex >= 0) {
            char endChar = this.charArray[endIndex];
            switch (endChar) {
                case '\t': 
                case '\n': 
                case ' ': 
                case '}': {
                    break;
                }
                case ',': 
                case ';': {
                    break;
                }
                case ']': {
                    break;
                }
                default: {
                    break block9;
                }
            }
            --endIndex;
        }
        return new CharSequenceValue(this.chop, Type.STRING, startIndexOfKey, endIndex + 1, this.charArray, encoded, checkDate);
    }

    @Override
    protected final Object decodeValue() {
        return this.decodeValueInternal();
    }

    private Value decodeValueInternal() {
        Value value = null;
        while (this.__index < this.charArray.length) {
            this.skipWhiteSpace();
            switch (this.__currentChar) {
                case '\n': {
                    break;
                }
                case '\r': {
                    break;
                }
                case ' ': {
                    break;
                }
                case '\t': {
                    break;
                }
                case '\b': {
                    break;
                }
                case '\f': {
                    break;
                }
                case '/': {
                    this.handleComment();
                    break;
                }
                case '#': {
                    this.handleBashComment();
                    break;
                }
                case '\"': {
                    value = this.decodeStringDouble();
                    break;
                }
                case '\'': {
                    value = this.decodeStringSingle();
                    break;
                }
                case 't': {
                    if (this.isTrue()) {
                        return this.decodeTrue() ? ValueContainer.TRUE : ValueContainer.FALSE;
                    }
                    value = this.decodeStringLax();
                    break;
                }
                case 'f': {
                    if (this.isFalse()) {
                        return !this.decodeFalse() ? ValueContainer.FALSE : ValueContainer.TRUE;
                    }
                    value = this.decodeStringLax();
                    break;
                }
                case 'n': {
                    if (this.isNull()) {
                        return this.decodeNull() == null ? ValueContainer.NULL : ValueContainer.NULL;
                    }
                    value = this.decodeStringLax();
                    break;
                }
                case '[': {
                    value = this.decodeJsonArrayLax();
                    break;
                }
                case '{': {
                    value = this.decodeJsonObjectLax();
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
                    return this.decodeNumberLax(false);
                }
                case '-': {
                    return this.decodeNumberLax(true);
                }
                default: {
                    value = this.decodeStringLax();
                }
            }
            if (value != null) {
                return value;
            }
            ++this.__index;
        }
        return null;
    }

    private void handleBashComment() {
        while (this.__index < this.charArray.length) {
            this.__currentChar = this.charArray[this.__index];
            if (this.__currentChar == '\n') {
                ++this.__index;
                return;
            }
            ++this.__index;
        }
    }

    private void handleComment() {
        if (this.hasMore()) {
            ++this.__index;
            this.__currentChar = this.charArray[this.__index];
            switch (this.__currentChar) {
                case '*': {
                    while (this.__index < this.charArray.length) {
                        this.__currentChar = this.charArray[this.__index];
                        if (this.__currentChar == '*') {
                            if (this.hasMore()) {
                                ++this.__index;
                                this.__currentChar = this.charArray[this.__index];
                                if (this.__currentChar == '/' && this.hasMore()) {
                                    ++this.__index;
                                    return;
                                }
                            } else {
                                this.complain("missing close of comment");
                            }
                        }
                        ++this.__index;
                    }
                }
                case '/': {
                    while (this.__index < this.charArray.length) {
                        this.__currentChar = this.charArray[this.__index];
                        if (this.__currentChar == '\n') {
                            if (this.hasMore()) {
                                ++this.__index;
                                return;
                            }
                            return;
                        }
                        ++this.__index;
                    }
                    break;
                }
            }
        }
    }

    protected final Value decodeNumberLax(boolean minus) {
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
            if (JsonParserLax.isNumberDigit(currentChar = array[index])) continue;
            if (currentChar <= ' ' || JsonParserLax.isDelimiter(currentChar)) break;
            if (JsonParserLax.isDecimalChar(currentChar)) {
                switch (currentChar) {
                    case '.': {
                        if (foundDot || foundExp) {
                            return this.decodeStringLax();
                        }
                        foundDot = true;
                        break;
                    }
                    case 'E': 
                    case 'e': {
                        if (foundExp) {
                            return this.decodeStringLax();
                        }
                        foundExp = true;
                        break;
                    }
                    case '+': 
                    case '-': {
                        if (foundSign || !foundExp) {
                            return this.decodeStringLax();
                        }
                        if (foundExp && array[index - 1] != 'e' && array[index - 1] != 'E') {
                            return this.decodeStringLax();
                        }
                        foundSign = true;
                    }
                }
                doubleFloat = true;
                continue;
            }
            return this.decodeStringLax();
        } while (++index < array.length);
        if (foundExp && ((prevChar = array[index - 1]) == 'e' || prevChar == 'E' || prevChar == '-' || prevChar == '+')) {
            return this.decodeStringLax();
        }
        this.__index = index;
        this.__currentChar = currentChar;
        Type type = doubleFloat ? Type.DOUBLE : Type.INTEGER;
        return new NumberValue(this.chop, type, startIndex, this.__index, this.charArray);
    }

    private boolean isNull() {
        return this.__index + NULL.length <= this.charArray.length && this.charArray[this.__index] == 'n' && this.charArray[this.__index + 1] == 'u' && this.charArray[this.__index + 2] == 'l' && this.charArray[this.__index + 3] == 'l';
    }

    private boolean isTrue() {
        return this.__index + TRUE.length <= this.charArray.length && this.charArray[this.__index] == 't' && this.charArray[this.__index + 1] == 'r' && this.charArray[this.__index + 2] == 'u' && this.charArray[this.__index + 3] == 'e';
    }

    private boolean isFalse() {
        return this.__index + FALSE.length <= this.charArray.length && this.charArray[this.__index] == 'f' && this.charArray[this.__index + 1] == 'a' && this.charArray[this.__index + 2] == 'l' && this.charArray[this.__index + 3] == 's' && this.charArray[this.__index + 4] == 'e';
    }

    private Value decodeStringLax() {
        int index;
        char currentChar = this.charArray[this.__index];
        int startIndex = this.__index;
        boolean encoded = false;
        char[] charArray = this.charArray;
        for (index = this.__index; index < charArray.length && !JsonParserLax.isDelimiter(currentChar = charArray[index]) && currentChar != '\\'; ++index) {
        }
        Value value = this.extractLaxString(startIndex, index, encoded, this.defaultCheckDates);
        this.__index = index;
        return value;
    }

    private Value decodeStringDouble() {
        this.__currentChar = this.charArray[this.__index];
        if (this.__index < this.charArray.length && this.__currentChar == '\"') {
            ++this.__index;
        }
        int startIndex = this.__index;
        boolean escape = false;
        boolean encoded = false;
        block4: while (this.__index < this.charArray.length) {
            this.__currentChar = this.charArray[this.__index];
            switch (this.__currentChar) {
                case '\"': {
                    if (!escape) break block4;
                    escape = false;
                    break;
                }
                case '\\': {
                    escape = !escape;
                    encoded = true;
                    break;
                }
                default: {
                    escape = false;
                }
            }
            ++this.__index;
        }
        CharSequenceValue value = new CharSequenceValue(this.chop, Type.STRING, startIndex, this.__index, this.charArray, encoded, this.defaultCheckDates);
        if (this.__index < this.charArray.length) {
            ++this.__index;
        }
        return value;
    }

    /*
     * Enabled aggressive block sorting
     */
    private Value decodeStringSingle() {
        this.__currentChar = this.charArray[this.__index];
        if (this.__index < this.charArray.length && this.__currentChar == '\'') {
            ++this.__index;
        }
        int startIndex = this.__index;
        boolean escape = false;
        boolean encoded = false;
        int minusCount = 0;
        int colonCount = 0;
        block6: while (this.__index < this.charArray.length) {
            block9: {
                this.__currentChar = this.charArray[this.__index];
                switch (this.__currentChar) {
                    case '\'': {
                        if (!escape) break block6;
                        escape = false;
                        break block9;
                    }
                    case '\\': {
                        encoded = true;
                        escape = true;
                        break block9;
                    }
                    case '-': {
                        ++minusCount;
                        break;
                    }
                    case ':': {
                        ++colonCount;
                    }
                }
                escape = false;
            }
            ++this.__index;
        }
        boolean checkDates = this.defaultCheckDates && !encoded && minusCount >= 2 && colonCount >= 2;
        CharSequenceValue value = new CharSequenceValue(this.chop, Type.STRING, startIndex, this.__index, this.charArray, encoded, checkDates);
        if (this.__index < this.charArray.length) {
            ++this.__index;
        }
        return value;
    }

    private Value decodeJsonArrayLax() {
        boolean doStop;
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
        block6: do {
            this.skipWhiteSpace();
            Value arrayItem = this.decodeValueInternal();
            list.add((Object)arrayItem);
            doStop = false;
            do {
                this.skipWhiteSpace();
                switch (this.__currentChar) {
                    case '/': {
                        this.handleComment();
                        break;
                    }
                    case '#': {
                        this.handleBashComment();
                        break;
                    }
                    case ',': {
                        ++this.__index;
                        continue block6;
                    }
                    case ']': {
                        ++this.__index;
                        doStop = true;
                        continue block6;
                    }
                    default: {
                        String charString = this.charDescription(this.__currentChar);
                        this.complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array index of %s \n", charString, list.size()));
                    }
                }
            } while (this.hasMore());
        } while (!doStop && this.hasMore());
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

