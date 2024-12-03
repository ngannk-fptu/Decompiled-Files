/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.type1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.encoding.BuiltInEncoding;
import org.apache.fontbox.encoding.StandardEncoding;
import org.apache.fontbox.type1.Token;
import org.apache.fontbox.type1.Type1Font;
import org.apache.fontbox.type1.Type1Lexer;

final class Type1Parser {
    private static final int EEXEC_KEY = 55665;
    private static final int CHARSTRING_KEY = 4330;
    private Type1Lexer lexer;
    private Type1Font font;

    Type1Parser() {
    }

    public Type1Font parse(byte[] segment1, byte[] segment2) throws IOException {
        this.font = new Type1Font(segment1, segment2);
        try {
            this.parseASCII(segment1);
        }
        catch (NumberFormatException ex) {
            throw new IOException(ex);
        }
        if (segment2.length > 0) {
            this.parseBinary(segment2);
        }
        return this.font;
    }

    private void parseASCII(byte[] bytes) throws IOException {
        Token token;
        if (bytes.length == 0) {
            throw new IOException("ASCII segment of type 1 font is empty");
        }
        if (bytes.length < 2 || bytes[0] != 37 && bytes[1] != 33) {
            throw new IOException("Invalid start of ASCII segment of type 1 font");
        }
        this.lexer = new Type1Lexer(bytes);
        if ("FontDirectory".equals(this.lexer.peekToken().getText())) {
            this.read(Token.NAME, "FontDirectory");
            this.read(Token.LITERAL);
            this.read(Token.NAME, "known");
            this.read(Token.START_PROC);
            this.readProcVoid();
            this.read(Token.START_PROC);
            this.readProcVoid();
            this.read(Token.NAME, "ifelse");
        }
        int length = this.read(Token.INTEGER).intValue();
        this.read(Token.NAME, "dict");
        this.readMaybe(Token.NAME, "dup");
        this.read(Token.NAME, "begin");
        for (int i = 0; i < length && (token = this.lexer.peekToken()) != null && (token.getKind() != Token.NAME || !token.getText().equals("currentdict") && !token.getText().equals("end")); ++i) {
            String key = this.read(Token.LITERAL).getText();
            if (key.equals("FontInfo") || key.equals("Fontinfo")) {
                this.readFontInfo(this.readSimpleDict());
                continue;
            }
            if (key.equals("Metrics")) {
                this.readSimpleDict();
                continue;
            }
            if (key.equals("Encoding")) {
                this.readEncoding();
                continue;
            }
            this.readSimpleValue(key);
        }
        this.readMaybe(Token.NAME, "currentdict");
        this.read(Token.NAME, "end");
        this.read(Token.NAME, "currentfile");
        this.read(Token.NAME, "eexec");
    }

    private void readSimpleValue(String key) throws IOException {
        List<Token> value = this.readDictValue();
        if (key.equals("FontName")) {
            this.font.fontName = value.get(0).getText();
        } else if (key.equals("PaintType")) {
            this.font.paintType = value.get(0).intValue();
        } else if (key.equals("FontType")) {
            this.font.fontType = value.get(0).intValue();
        } else if (key.equals("FontMatrix")) {
            this.font.fontMatrix = this.arrayToNumbers(value);
        } else if (key.equals("FontBBox")) {
            this.font.fontBBox = this.arrayToNumbers(value);
        } else if (key.equals("UniqueID")) {
            this.font.uniqueID = value.get(0).intValue();
        } else if (key.equals("StrokeWidth")) {
            this.font.strokeWidth = value.get(0).floatValue();
        } else if (key.equals("FID")) {
            this.font.fontID = value.get(0).getText();
        }
    }

    private void readEncoding() throws IOException {
        if (this.lexer.peekKind(Token.NAME)) {
            String name = this.lexer.nextToken().getText();
            if (!name.equals("StandardEncoding")) {
                throw new IOException("Unknown encoding: " + name);
            }
            this.font.encoding = StandardEncoding.INSTANCE;
            this.readMaybe(Token.NAME, "readonly");
            this.read(Token.NAME, "def");
        } else {
            this.read(Token.INTEGER).intValue();
            this.readMaybe(Token.NAME, "array");
            while (!this.lexer.peekKind(Token.NAME) || !this.lexer.peekToken().getText().equals("dup") && !this.lexer.peekToken().getText().equals("readonly") && !this.lexer.peekToken().getText().equals("def")) {
                if (this.lexer.nextToken() != null) continue;
                throw new IOException("Incomplete data while reading encoding of type 1 font");
            }
            HashMap<Integer, String> codeToName = new HashMap<Integer, String>();
            while (this.lexer.peekKind(Token.NAME) && this.lexer.peekToken().getText().equals("dup")) {
                this.read(Token.NAME, "dup");
                int code = this.read(Token.INTEGER).intValue();
                String name = this.read(Token.LITERAL).getText();
                this.read(Token.NAME, "put");
                codeToName.put(code, name);
            }
            this.font.encoding = new BuiltInEncoding(codeToName);
            this.readMaybe(Token.NAME, "readonly");
            this.read(Token.NAME, "def");
        }
    }

    private List<Number> arrayToNumbers(List<Token> value) throws IOException {
        ArrayList<Number> numbers = new ArrayList<Number>();
        int size = value.size() - 1;
        for (int i = 1; i < size; ++i) {
            Token token = value.get(i);
            if (token.getKind() == Token.REAL) {
                numbers.add(Float.valueOf(token.floatValue()));
                continue;
            }
            if (token.getKind() == Token.INTEGER) {
                numbers.add(token.intValue());
                continue;
            }
            throw new IOException("Expected INTEGER or REAL but got " + token + " at array position " + i);
        }
        return numbers;
    }

    private void readFontInfo(Map<String, List<Token>> fontInfo) {
        for (Map.Entry<String, List<Token>> entry : fontInfo.entrySet()) {
            String key = entry.getKey();
            List<Token> value = entry.getValue();
            if (key.equals("version")) {
                this.font.version = value.get(0).getText();
                continue;
            }
            if (key.equals("Notice")) {
                this.font.notice = value.get(0).getText();
                continue;
            }
            if (key.equals("FullName")) {
                this.font.fullName = value.get(0).getText();
                continue;
            }
            if (key.equals("FamilyName")) {
                this.font.familyName = value.get(0).getText();
                continue;
            }
            if (key.equals("Weight")) {
                this.font.weight = value.get(0).getText();
                continue;
            }
            if (key.equals("ItalicAngle")) {
                this.font.italicAngle = value.get(0).floatValue();
                continue;
            }
            if (key.equals("isFixedPitch")) {
                this.font.isFixedPitch = value.get(0).booleanValue();
                continue;
            }
            if (key.equals("UnderlinePosition")) {
                this.font.underlinePosition = value.get(0).floatValue();
                continue;
            }
            if (!key.equals("UnderlineThickness")) continue;
            this.font.underlineThickness = value.get(0).floatValue();
        }
    }

    private Map<String, List<Token>> readSimpleDict() throws IOException {
        HashMap<String, List<Token>> dict = new HashMap<String, List<Token>>();
        int length = this.read(Token.INTEGER).intValue();
        this.read(Token.NAME, "dict");
        this.readMaybe(Token.NAME, "dup");
        this.read(Token.NAME, "begin");
        for (int i = 0; i < length && this.lexer.peekToken() != null; ++i) {
            if (this.lexer.peekKind(Token.NAME) && !this.lexer.peekToken().getText().equals("end")) {
                this.read(Token.NAME);
            }
            if (this.lexer.peekToken() == null || this.lexer.peekKind(Token.NAME) && this.lexer.peekToken().getText().equals("end")) break;
            String key = this.read(Token.LITERAL).getText();
            List<Token> value = this.readDictValue();
            dict.put(key, value);
        }
        this.read(Token.NAME, "end");
        this.readMaybe(Token.NAME, "readonly");
        this.read(Token.NAME, "def");
        return dict;
    }

    private List<Token> readDictValue() throws IOException {
        List<Token> value = this.readValue();
        this.readDef();
        return value;
    }

    private List<Token> readValue() throws IOException {
        ArrayList<Token> value = new ArrayList<Token>();
        Token token = this.lexer.nextToken();
        if (this.lexer.peekToken() == null) {
            return value;
        }
        value.add(token);
        if (token.getKind() == Token.START_ARRAY) {
            int openArray = 1;
            do {
                if (this.lexer.peekToken() == null) {
                    return value;
                }
                if (this.lexer.peekKind(Token.START_ARRAY)) {
                    ++openArray;
                }
                token = this.lexer.nextToken();
                value.add(token);
            } while (token.getKind() != Token.END_ARRAY || --openArray != 0);
        } else if (token.getKind() == Token.START_PROC) {
            value.addAll(this.readProc());
        } else if (token.getKind() == Token.START_DICT) {
            this.read(Token.END_DICT);
            return value;
        }
        this.readPostScriptWrapper(value);
        return value;
    }

    private void readPostScriptWrapper(List<Token> value) throws IOException {
        if (this.lexer.peekToken() == null) {
            throw new IOException("Missing start token for the system dictionary");
        }
        if ("systemdict".equals(this.lexer.peekToken().getText())) {
            this.read(Token.NAME, "systemdict");
            this.read(Token.LITERAL, "internaldict");
            this.read(Token.NAME, "known");
            this.read(Token.START_PROC);
            this.readProcVoid();
            this.read(Token.START_PROC);
            this.readProcVoid();
            this.read(Token.NAME, "ifelse");
            this.read(Token.START_PROC);
            this.read(Token.NAME, "pop");
            value.clear();
            value.addAll(this.readValue());
            this.read(Token.END_PROC);
            this.read(Token.NAME, "if");
        }
    }

    private List<Token> readProc() throws IOException {
        Token token;
        ArrayList<Token> value = new ArrayList<Token>();
        int openProc = 1;
        do {
            if (this.lexer.peekToken() == null) {
                throw new IOException("Malformed procedure: missing token");
            }
            if (this.lexer.peekKind(Token.START_PROC)) {
                ++openProc;
            }
            token = this.lexer.nextToken();
            value.add(token);
        } while (token.getKind() != Token.END_PROC || --openProc != 0);
        Token executeonly = this.readMaybe(Token.NAME, "executeonly");
        if (executeonly != null) {
            value.add(executeonly);
        }
        return value;
    }

    private void readProcVoid() throws IOException {
        Token token;
        int openProc = 1;
        do {
            if (this.lexer.peekToken() == null) {
                throw new IOException("Malformed procedure: missing token");
            }
            if (!this.lexer.peekKind(Token.START_PROC)) continue;
            ++openProc;
        } while ((token = this.lexer.nextToken()).getKind() != Token.END_PROC || --openProc != 0);
        this.readMaybe(Token.NAME, "executeonly");
    }

    private void parseBinary(byte[] bytes) throws IOException {
        byte[] decrypted = this.isBinary(bytes) ? this.decrypt(bytes, 55665, 4) : this.decrypt(this.hexToBinary(bytes), 55665, 4);
        this.lexer = new Type1Lexer(decrypted);
        Token peekToken = this.lexer.peekToken();
        while (peekToken != null && !"Private".equals(peekToken.getText())) {
            this.lexer.nextToken();
            peekToken = this.lexer.peekToken();
        }
        if (peekToken == null) {
            throw new IOException("/Private token not found");
        }
        this.read(Token.LITERAL, "Private");
        int length = this.read(Token.INTEGER).intValue();
        this.read(Token.NAME, "dict");
        this.readMaybe(Token.NAME, "dup");
        this.read(Token.NAME, "begin");
        int lenIV = 4;
        for (int i = 0; i < length && this.lexer.peekKind(Token.LITERAL); ++i) {
            String key = this.read(Token.LITERAL).getText();
            if ("Subrs".equals(key)) {
                this.readSubrs(lenIV);
                continue;
            }
            if ("OtherSubrs".equals(key)) {
                this.readOtherSubrs();
                continue;
            }
            if ("lenIV".equals(key)) {
                lenIV = this.readDictValue().get(0).intValue();
                continue;
            }
            if ("ND".equals(key)) {
                this.read(Token.START_PROC);
                this.readMaybe(Token.NAME, "noaccess");
                this.read(Token.NAME, "def");
                this.read(Token.END_PROC);
                this.readMaybe(Token.NAME, "executeonly");
                this.readMaybe(Token.NAME, "readonly");
                this.read(Token.NAME, "def");
                continue;
            }
            if ("NP".equals(key)) {
                this.read(Token.START_PROC);
                this.readMaybe(Token.NAME, "noaccess");
                this.read(Token.NAME);
                this.read(Token.END_PROC);
                this.readMaybe(Token.NAME, "executeonly");
                this.readMaybe(Token.NAME, "readonly");
                this.read(Token.NAME, "def");
                continue;
            }
            if ("RD".equals(key)) {
                this.read(Token.START_PROC);
                this.readProcVoid();
                this.readMaybe(Token.NAME, "bind");
                this.readMaybe(Token.NAME, "executeonly");
                this.readMaybe(Token.NAME, "readonly");
                this.read(Token.NAME, "def");
                continue;
            }
            this.readPrivate(key, this.readDictValue());
        }
        while (!this.lexer.peekKind(Token.LITERAL) || !this.lexer.peekToken().getText().equals("CharStrings")) {
            if (this.lexer.nextToken() != null) continue;
            throw new IOException("Missing 'CharStrings' dictionary in type 1 font");
        }
        this.read(Token.LITERAL, "CharStrings");
        this.readCharStrings(lenIV);
    }

    private void readPrivate(String key, List<Token> value) throws IOException {
        if (key.equals("BlueValues")) {
            this.font.blueValues = this.arrayToNumbers(value);
        } else if (key.equals("OtherBlues")) {
            this.font.otherBlues = this.arrayToNumbers(value);
        } else if (key.equals("FamilyBlues")) {
            this.font.familyBlues = this.arrayToNumbers(value);
        } else if (key.equals("FamilyOtherBlues")) {
            this.font.familyOtherBlues = this.arrayToNumbers(value);
        } else if (key.equals("BlueScale")) {
            this.font.blueScale = value.get(0).floatValue();
        } else if (key.equals("BlueShift")) {
            this.font.blueShift = value.get(0).intValue();
        } else if (key.equals("BlueFuzz")) {
            this.font.blueFuzz = value.get(0).intValue();
        } else if (key.equals("StdHW")) {
            this.font.stdHW = this.arrayToNumbers(value);
        } else if (key.equals("StdVW")) {
            this.font.stdVW = this.arrayToNumbers(value);
        } else if (key.equals("StemSnapH")) {
            this.font.stemSnapH = this.arrayToNumbers(value);
        } else if (key.equals("StemSnapV")) {
            this.font.stemSnapV = this.arrayToNumbers(value);
        } else if (key.equals("ForceBold")) {
            this.font.forceBold = value.get(0).booleanValue();
        } else if (key.equals("LanguageGroup")) {
            this.font.languageGroup = value.get(0).intValue();
        }
    }

    private void readSubrs(int lenIV) throws IOException {
        int i;
        int length = this.read(Token.INTEGER).intValue();
        for (i = 0; i < length; ++i) {
            this.font.subrs.add(null);
        }
        this.read(Token.NAME, "array");
        for (i = 0; i < length && this.lexer.peekToken() != null && this.lexer.peekKind(Token.NAME) && this.lexer.peekToken().getText().equals("dup"); ++i) {
            this.read(Token.NAME, "dup");
            Token index = this.read(Token.INTEGER);
            this.read(Token.INTEGER);
            Token charstring = this.read(Token.CHARSTRING);
            int j = index.intValue();
            if (j < this.font.subrs.size()) {
                this.font.subrs.set(j, this.decrypt(charstring.getData(), 4330, lenIV));
            }
            this.readPut();
        }
        this.readDef();
    }

    private void readOtherSubrs() throws IOException {
        if (this.lexer.peekToken() == null) {
            throw new IOException("Missing start token of OtherSubrs procedure");
        }
        if (this.lexer.peekKind(Token.START_ARRAY)) {
            this.readValue();
            this.readDef();
        } else {
            int length = this.read(Token.INTEGER).intValue();
            this.read(Token.NAME, "array");
            for (int i = 0; i < length; ++i) {
                this.read(Token.NAME, "dup");
                this.read(Token.INTEGER);
                this.readValue();
                this.readPut();
            }
            this.readDef();
        }
    }

    private void readCharStrings(int lenIV) throws IOException {
        int length = this.read(Token.INTEGER).intValue();
        this.read(Token.NAME, "dict");
        this.read(Token.NAME, "dup");
        this.read(Token.NAME, "begin");
        for (int i = 0; !(i >= length || this.lexer.peekToken() == null || this.lexer.peekKind(Token.NAME) && this.lexer.peekToken().getText().equals("end")); ++i) {
            String name = this.read(Token.LITERAL).getText();
            this.read(Token.INTEGER);
            Token charstring = this.read(Token.CHARSTRING);
            this.font.charstrings.put(name, this.decrypt(charstring.getData(), 4330, lenIV));
            this.readDef();
        }
        this.read(Token.NAME, "end");
    }

    private void readDef() throws IOException {
        this.readMaybe(Token.NAME, "readonly");
        this.readMaybe(Token.NAME, "noaccess");
        Token token = this.read(Token.NAME);
        if (token.getText().equals("ND") || token.getText().equals("|-")) {
            return;
        }
        if (token.getText().equals("noaccess")) {
            token = this.read(Token.NAME);
        }
        if (token.getText().equals("def")) {
            return;
        }
        throw new IOException("Found " + token + " but expected ND");
    }

    private void readPut() throws IOException {
        this.readMaybe(Token.NAME, "readonly");
        Token token = this.read(Token.NAME);
        if (token.getText().equals("NP") || token.getText().equals("|")) {
            return;
        }
        if (token.getText().equals("noaccess")) {
            token = this.read(Token.NAME);
        }
        if (token.getText().equals("put")) {
            return;
        }
        throw new IOException("Found " + token + " but expected NP");
    }

    private Token read(Token.Kind kind) throws IOException {
        Token token = this.lexer.nextToken();
        if (token == null || token.getKind() != kind) {
            throw new IOException("Found " + token + " but expected " + (Object)((Object)kind));
        }
        return token;
    }

    private void read(Token.Kind kind, String name) throws IOException {
        Token token = this.read(kind);
        if (token.getText() == null || !token.getText().equals(name)) {
            throw new IOException("Found " + token + " but expected " + name);
        }
    }

    private Token readMaybe(Token.Kind kind, String name) throws IOException {
        if (this.lexer.peekKind(kind) && this.lexer.peekToken().getText().equals(name)) {
            return this.lexer.nextToken();
        }
        return null;
    }

    private byte[] decrypt(byte[] cipherBytes, int r, int n) {
        if (n == -1) {
            return cipherBytes;
        }
        if (cipherBytes.length == 0 || n > cipherBytes.length) {
            return new byte[0];
        }
        int c1 = 52845;
        int c2 = 22719;
        byte[] plainBytes = new byte[cipherBytes.length - n];
        for (int i = 0; i < cipherBytes.length; ++i) {
            int cipher = cipherBytes[i] & 0xFF;
            int plain = cipher ^ r >> 8;
            if (i >= n) {
                plainBytes[i - n] = (byte)plain;
            }
            r = (cipher + r) * c1 + c2 & 0xFFFF;
        }
        return plainBytes;
    }

    private boolean isBinary(byte[] bytes) {
        if (bytes.length < 4) {
            return true;
        }
        for (int i = 0; i < 4; ++i) {
            byte by = bytes[i];
            if (by == 10 || by == 13 || by == 32 || by == 9 || Character.digit((char)by, 16) != -1) continue;
            return true;
        }
        return false;
    }

    private byte[] hexToBinary(byte[] bytes) {
        int len = 0;
        for (byte by : bytes) {
            if (Character.digit((char)by, 16) == -1) continue;
            ++len;
        }
        byte[] res = new byte[len / 2];
        int r = 0;
        int prev = -1;
        for (byte by : bytes) {
            int digit = Character.digit((char)by, 16);
            if (digit == -1) continue;
            if (prev == -1) {
                prev = digit;
                continue;
            }
            res[r++] = (byte)(prev * 16 + digit);
            prev = -1;
        }
        return res;
    }
}

