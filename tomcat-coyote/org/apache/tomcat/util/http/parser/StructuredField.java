/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.codec.binary.Base64
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.res.StringManager;

public class StructuredField {
    private static final StringManager sm;
    private static final int ARRAY_SIZE = 128;
    private static final boolean[] IS_KEY_FIRST;
    private static final boolean[] IS_KEY;
    private static final boolean[] IS_OWS;
    private static final boolean[] IS_BASE64;
    private static final boolean[] IS_TOKEN;

    static SfList parseSfList(Reader input) throws IOException {
        StructuredField.skipSP(input);
        SfList result = new SfList();
        if (StructuredField.peek(input) != -1) {
            while (true) {
                SfListMember listMember = StructuredField.parseSfListMember(input);
                result.addListMember(listMember);
                StructuredField.skipOWS(input);
                if (StructuredField.peek(input) == -1) break;
                StructuredField.requireChar(input, 44);
                StructuredField.skipOWS(input);
                StructuredField.requireNotChar(input, -1);
            }
        }
        StructuredField.skipSP(input);
        StructuredField.requireChar(input, -1);
        return result;
    }

    static SfListMember parseSfListMember(Reader input) throws IOException {
        SfListMember listMember = StructuredField.peek(input) == 40 ? StructuredField.parseSfInnerList(input) : StructuredField.parseSfBareItem(input);
        StructuredField.parseSfParameters(input, listMember);
        return listMember;
    }

    static SfInnerList parseSfInnerList(Reader input) throws IOException {
        StructuredField.requireChar(input, 40);
        SfInnerList innerList = new SfInnerList();
        while (true) {
            StructuredField.skipSP(input);
            if (StructuredField.peek(input) == 41) break;
            SfItem<?> item = StructuredField.parseSfBareItem(input);
            StructuredField.parseSfParameters(input, item);
            innerList.addListItem(item);
            input.mark(1);
            StructuredField.requireChar(input, 32, 41);
            input.reset();
        }
        StructuredField.requireChar(input, 41);
        return innerList;
    }

    static SfDictionary parseSfDictionary(Reader input) throws IOException {
        StructuredField.skipSP(input);
        SfDictionary result = new SfDictionary();
        if (StructuredField.peek(input) != -1) {
            while (true) {
                SfListMember listMember;
                String key = StructuredField.parseSfKey(input);
                input.mark(1);
                int c = input.read();
                if (c == 61) {
                    listMember = StructuredField.parseSfListMember(input);
                } else {
                    listMember = new SfBoolean(true);
                    input.reset();
                }
                StructuredField.parseSfParameters(input, listMember);
                result.addDictionaryMember(key, listMember);
                StructuredField.skipOWS(input);
                if (StructuredField.peek(input) == -1) break;
                StructuredField.requireChar(input, 44);
                StructuredField.skipOWS(input);
                StructuredField.requireNotChar(input, -1);
            }
        }
        StructuredField.skipSP(input);
        StructuredField.requireChar(input, -1);
        return result;
    }

    static SfItem<?> parseSfItem(Reader input) throws IOException {
        StructuredField.skipSP(input);
        SfItem<?> item = StructuredField.parseSfBareItem(input);
        StructuredField.parseSfParameters(input, item);
        StructuredField.skipSP(input);
        StructuredField.requireChar(input, -1);
        return item;
    }

    static SfItem<?> parseSfBareItem(Reader input) throws IOException {
        SfItem item;
        int c = input.read();
        if (c == 45 || HttpParser.isNumeric(c)) {
            item = StructuredField.parseSfNumeric(input, c);
        } else if (c == 34) {
            item = StructuredField.parseSfString(input);
        } else if (c == 42 || HttpParser.isAlpha(c)) {
            item = StructuredField.parseSfToken(input, c);
        } else if (c == 58) {
            item = StructuredField.parseSfByteSequence(input);
        } else if (c == 63) {
            item = StructuredField.parseSfBoolean(input);
        } else {
            throw new IllegalArgumentException(sm.getString("sf.bareitem.invalidCharacter", new Object[]{String.format("\\u%40X", c)}));
        }
        return item;
    }

    static void parseSfParameters(Reader input, SfListMember listMember) throws IOException {
        while (StructuredField.peek(input) == 59) {
            SfBoolean item;
            StructuredField.requireChar(input, 59);
            StructuredField.skipSP(input);
            String key = StructuredField.parseSfKey(input);
            input.mark(1);
            int c = input.read();
            if (c == 61) {
                item = StructuredField.parseSfBareItem(input);
            } else {
                item = new SfBoolean(true);
                input.reset();
            }
            listMember.addParameter(key, item);
        }
    }

    static String parseSfKey(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        input.mark(1);
        int c = input.read();
        if (!StructuredField.isKeyFirst(c)) {
            throw new IllegalArgumentException(sm.getString("sf.key.invalidFirstCharacter", new Object[]{String.format("\\u%40X", c)}));
        }
        while (c != -1 && StructuredField.isKey(c)) {
            result.append((char)c);
            input.mark(1);
            c = input.read();
        }
        input.reset();
        return result.toString();
    }

    static SfItem<?> parseSfNumeric(Reader input, int first) throws IOException {
        int c;
        int sign = 1;
        boolean integer = true;
        int decimalPos = 0;
        StringBuilder result = new StringBuilder();
        if (first == 45) {
            sign = -1;
            c = input.read();
        } else {
            c = first;
        }
        if (!HttpParser.isNumeric(c)) {
            throw new IllegalArgumentException(sm.getString("sf.numeric.invalidCharacter", new Object[]{String.format("\\u%40X", c)}));
        }
        result.append((char)c);
        input.mark(1);
        c = input.read();
        while (c != -1) {
            if (HttpParser.isNumeric(c)) {
                result.append((char)c);
            } else if (integer && c == 46) {
                if (result.length() > 12) {
                    throw new IllegalArgumentException(sm.getString("sf.numeric.integralPartTooLong"));
                }
                integer = false;
                result.append((char)c);
                decimalPos = result.length();
            } else {
                input.reset();
                break;
            }
            if (integer && result.length() > 15) {
                throw new IllegalArgumentException(sm.getString("sf.numeric.integerTooLong"));
            }
            if (!integer && result.length() > 16) {
                throw new IllegalArgumentException(sm.getString("sf.numeric.decimalTooLong"));
            }
            input.mark(1);
            c = input.read();
        }
        if (integer) {
            return new SfInteger(Long.parseLong(result.toString()) * (long)sign);
        }
        if (result.charAt(result.length() - 1) == '.') {
            throw new IllegalArgumentException(sm.getString("sf.numeric.decimalInvalidFinal"));
        }
        if (result.length() - decimalPos > 3) {
            throw new IllegalArgumentException(sm.getString("sf.numeric.decimalPartTooLong"));
        }
        return new SfDecimal(Double.parseDouble(result.toString()) * (double)sign);
    }

    static SfString parseSfString(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        while (true) {
            int c;
            if ((c = input.read()) == 92) {
                StructuredField.requireNotChar(input, -1);
                c = input.read();
                if (c != 92 && c != 34) {
                    throw new IllegalArgumentException(sm.getString("sf.string.invalidEscape", new Object[]{String.format("\\u%40X", c)}));
                }
            } else {
                if (c == 34) break;
                if (c < 32 || c > 126) {
                    throw new IllegalArgumentException(sm.getString("sf.string.invalidCharacter", new Object[]{String.format("\\u%40X", c)}));
                }
            }
            result.append((char)c);
        }
        return new SfString(result.toString());
    }

    static SfToken parseSfToken(Reader input, int first) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append((char)first);
        while (true) {
            input.mark(1);
            int c = input.read();
            if (!StructuredField.isToken(c)) break;
            result.append((char)c);
        }
        input.reset();
        return new SfToken(result.toString());
    }

    static SfByteSequence parseSfByteSequence(Reader input) throws IOException {
        int c;
        StringBuilder base64 = new StringBuilder();
        while ((c = input.read()) != 58) {
            if (StructuredField.isBase64(c)) {
                base64.append((char)c);
                continue;
            }
            throw new IllegalArgumentException(sm.getString("sf.base64.invalidCharacter", new Object[]{String.format("\\u%40X", c)}));
        }
        return new SfByteSequence(Base64.decodeBase64((String)base64.toString()));
    }

    static SfBoolean parseSfBoolean(Reader input) throws IOException {
        int c = input.read();
        if (c == 49) {
            return new SfBoolean(true);
        }
        if (c == 48) {
            return new SfBoolean(false);
        }
        throw new IllegalArgumentException(sm.getString("sf.boolean.invalidCharacter", new Object[]{String.format("\\u%40X", c)}));
    }

    static void skipSP(Reader input) throws IOException {
        input.mark(1);
        int c = input.read();
        while (c == 32) {
            input.mark(1);
            c = input.read();
        }
        input.reset();
    }

    static void skipOWS(Reader input) throws IOException {
        input.mark(1);
        int c = input.read();
        while (StructuredField.isOws(c)) {
            input.mark(1);
            c = input.read();
        }
        input.reset();
    }

    static void requireChar(Reader input, int ... required) throws IOException {
        int c = input.read();
        for (int r : required) {
            if (c != r) continue;
            return;
        }
        throw new IllegalArgumentException(sm.getString("sf.invalidCharacter", new Object[]{String.format("\\u%40X", c)}));
    }

    static void requireNotChar(Reader input, int required) throws IOException {
        input.mark(1);
        int c = input.read();
        if (c == required) {
            throw new IllegalArgumentException(sm.getString("sf.invalidCharacter", new Object[]{String.format("\\u%40X", c)}));
        }
        input.reset();
    }

    static int peek(Reader input) throws IOException {
        input.mark(1);
        int c = input.read();
        input.reset();
        return c;
    }

    static boolean isKeyFirst(int c) {
        try {
            return IS_KEY_FIRST[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    static boolean isKey(int c) {
        try {
            return IS_KEY[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    static boolean isOws(int c) {
        try {
            return IS_OWS[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    static boolean isBase64(int c) {
        try {
            return IS_BASE64[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    static boolean isToken(int c) {
        try {
            return IS_TOKEN[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    private StructuredField() {
    }

    static {
        int i;
        sm = StringManager.getManager(StructuredField.class);
        IS_KEY_FIRST = new boolean[128];
        IS_KEY = new boolean[128];
        IS_OWS = new boolean[128];
        IS_BASE64 = new boolean[128];
        IS_TOKEN = new boolean[128];
        for (i = 0; i < 128; ++i) {
            if (i == 42 || i >= 97 && i <= 122) {
                StructuredField.IS_KEY_FIRST[i] = true;
                StructuredField.IS_KEY[i] = true;
                continue;
            }
            if ((i < 48 || i > 57) && i != 95 && i != 45 && i != 46) continue;
            StructuredField.IS_KEY[i] = true;
        }
        for (i = 0; i < 128; ++i) {
            if (i != 9 && i != 32) continue;
            StructuredField.IS_OWS[i] = true;
        }
        for (i = 0; i < 128; ++i) {
            if (!(i == 43 || i == 47 || i >= 48 && i <= 57 || i == 61 || i >= 65 && i <= 90) && (i < 97 || i > 122)) continue;
            StructuredField.IS_BASE64[i] = true;
        }
        for (i = 0; i < 128; ++i) {
            if (!HttpParser.isToken(i) && i != 58 && i != 47) continue;
            StructuredField.IS_TOKEN[i] = true;
        }
    }

    static class SfList {
        private List<SfListMember> listMembers = new ArrayList<SfListMember>();

        SfList() {
        }

        void addListMember(SfListMember listMember) {
            this.listMembers.add(listMember);
        }
    }

    static class SfListMember {
        private Map<String, SfItem<?>> parameters = null;

        SfListMember() {
        }

        void addParameter(String key, SfItem<?> value) {
            if (this.parameters == null) {
                this.parameters = new LinkedHashMap();
            }
            this.parameters.put(key, value);
        }
    }

    static class SfInnerList
    extends SfListMember {
        List<SfItem<?>> listItems = new ArrayList();

        SfInnerList() {
        }

        void addListItem(SfItem<?> item) {
            this.listItems.add(item);
        }

        List<SfItem<?>> getListItem() {
            return this.listItems;
        }
    }

    static abstract class SfItem<T>
    extends SfListMember {
        private final T value;

        SfItem(T value) {
            this.value = value;
        }

        T getVaue() {
            return this.value;
        }
    }

    static class SfDictionary {
        private Map<String, SfListMember> dictionary = new LinkedHashMap<String, SfListMember>();

        SfDictionary() {
        }

        void addDictionaryMember(String key, SfListMember value) {
            this.dictionary.put(key, value);
        }

        SfListMember getDictionaryMember(String key) {
            return this.dictionary.get(key);
        }
    }

    static class SfBoolean
    extends SfItem<Boolean> {
        SfBoolean(boolean value) {
            super(value);
        }
    }

    static class SfString
    extends SfItem<String> {
        SfString(String value) {
            super(value);
        }
    }

    static class SfToken
    extends SfItem<String> {
        SfToken(String value) {
            super(value);
        }
    }

    static class SfByteSequence
    extends SfItem<byte[]> {
        SfByteSequence(byte[] value) {
            super(value);
        }
    }

    static class SfInteger
    extends SfItem<Long> {
        SfInteger(long value) {
            super(value);
        }
    }

    static class SfDecimal
    extends SfItem<Double> {
        SfDecimal(double value) {
            super(value);
        }
    }
}

