/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdfparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfparser.SequentialSource;
import org.apache.pdfbox.util.Charsets;

public abstract class BaseParser {
    private static final long OBJECT_NUMBER_THRESHOLD = 10000000000L;
    private static final long GENERATION_NUMBER_THRESHOLD = 65535L;
    static final int MAX_LENGTH_LONG = Long.toString(Long.MAX_VALUE).length();
    private final CharsetDecoder utf8Decoder = Charsets.UTF_8.newDecoder();
    private static final Log LOG = LogFactory.getLog(BaseParser.class);
    protected static final int E = 101;
    protected static final int N = 110;
    protected static final int D = 100;
    protected static final int S = 115;
    protected static final int T = 116;
    protected static final int R = 114;
    protected static final int A = 97;
    protected static final int M = 109;
    protected static final int O = 111;
    protected static final int B = 98;
    protected static final int J = 106;
    public static final String DEF = "def";
    protected static final String ENDOBJ_STRING = "endobj";
    protected static final String ENDSTREAM_STRING = "endstream";
    protected static final String STREAM_STRING = "stream";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String NULL = "null";
    protected static final byte ASCII_LF = 10;
    protected static final byte ASCII_CR = 13;
    private static final byte ASCII_ZERO = 48;
    private static final byte ASCII_NINE = 57;
    private static final byte ASCII_SPACE = 32;
    final SequentialSource seqSource;
    protected COSDocument document;

    BaseParser(SequentialSource pdfSource) {
        this.seqSource = pdfSource;
    }

    private static boolean isHexDigit(char ch) {
        return BaseParser.isDigit(ch) || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F';
    }

    private COSBase parseCOSDictionaryValue() throws IOException {
        long numOffset = this.seqSource.getPosition();
        COSBase value = this.parseDirObject();
        this.skipSpaces();
        if (!(value instanceof COSNumber) || !this.isDigit()) {
            return value;
        }
        long genOffset = this.seqSource.getPosition();
        COSBase generationNumber = this.parseDirObject();
        this.skipSpaces();
        this.readExpectedChar('R');
        if (!(value instanceof COSInteger)) {
            LOG.error((Object)("expected number, actual=" + value + " at offset " + numOffset));
            return COSNull.NULL;
        }
        if (!(generationNumber instanceof COSInteger)) {
            LOG.error((Object)("expected number, actual=" + generationNumber + " at offset " + genOffset));
            return COSNull.NULL;
        }
        long objNumber = ((COSInteger)value).longValue();
        if (objNumber <= 0L) {
            LOG.warn((Object)("invalid object number value =" + objNumber + " at offset " + numOffset));
            return COSNull.NULL;
        }
        int genNumber = ((COSInteger)generationNumber).intValue();
        if (genNumber < 0) {
            LOG.error((Object)("invalid generation number value =" + genNumber + " at offset " + numOffset));
            return COSNull.NULL;
        }
        return this.getObjectFromPool(new COSObjectKey(objNumber, genNumber));
    }

    private COSBase getObjectFromPool(COSObjectKey key) throws IOException {
        if (this.document == null) {
            throw new IOException("object reference " + key + " at offset " + this.seqSource.getPosition() + " in content stream");
        }
        return this.document.getObjectFromPool(key);
    }

    protected COSDictionary parseCOSDictionary() throws IOException {
        this.readExpectedChar('<');
        this.readExpectedChar('<');
        this.skipSpaces();
        COSDictionary obj = new COSDictionary();
        boolean done = false;
        while (!done) {
            this.skipSpaces();
            char c = (char)this.seqSource.peek();
            if (c == '>') {
                done = true;
                continue;
            }
            if (c == '/') {
                if (this.parseCOSDictionaryNameValuePair(obj)) continue;
                return obj;
            }
            LOG.warn((Object)("Invalid dictionary, found: '" + c + "' but expected: '/' at offset " + this.seqSource.getPosition()));
            if (!this.readUntilEndOfCOSDictionary()) continue;
            return obj;
        }
        try {
            this.readExpectedChar('>');
            this.readExpectedChar('>');
        }
        catch (IOException exception) {
            LOG.warn((Object)("Invalid dictionary, can't find end of dictionary at offset " + this.seqSource.getPosition()));
        }
        return obj;
    }

    private boolean readUntilEndOfCOSDictionary() throws IOException {
        int c = this.seqSource.read();
        while (c != -1 && c != 47 && c != 62) {
            if (c == 101 && (c = this.seqSource.read()) == 110 && (c = this.seqSource.read()) == 100) {
                boolean isObj;
                c = this.seqSource.read();
                boolean isStream = c == 115 && this.seqSource.read() == 116 && this.seqSource.read() == 114 && this.seqSource.read() == 101 && this.seqSource.read() == 97 && this.seqSource.read() == 109;
                boolean bl = isObj = !isStream && c == 111 && this.seqSource.read() == 98 && this.seqSource.read() == 106;
                if (isStream || isObj) {
                    return true;
                }
            }
            c = this.seqSource.read();
        }
        if (c == -1) {
            return true;
        }
        this.seqSource.unread(c);
        return false;
    }

    private boolean parseCOSDictionaryNameValuePair(COSDictionary obj) throws IOException {
        COSName key = this.parseCOSName();
        if (key == null || key.getName().isEmpty()) {
            LOG.warn((Object)("Empty COSName at offset " + this.seqSource.getPosition()));
        }
        COSBase value = this.parseCOSDictionaryValue();
        this.skipSpaces();
        if (value == null) {
            LOG.warn((Object)("Bad dictionary declaration at offset " + this.seqSource.getPosition()));
            return false;
        }
        if (value instanceof COSInteger && !((COSInteger)value).isValid()) {
            LOG.warn((Object)("Skipped out of range number value at offset " + this.seqSource.getPosition()));
        } else {
            value.setDirect(true);
            obj.setItem(key, value);
        }
        return true;
    }

    protected void skipWhiteSpaces() throws IOException {
        int whitespace = this.seqSource.read();
        while (32 == whitespace) {
            whitespace = this.seqSource.read();
        }
        if (13 == whitespace) {
            whitespace = this.seqSource.read();
            if (10 != whitespace) {
                this.seqSource.unread(whitespace);
            }
        } else if (10 != whitespace) {
            this.seqSource.unread(whitespace);
        }
    }

    private int checkForEndOfString(int bracesParameter) throws IOException {
        if (bracesParameter == 0) {
            return 0;
        }
        byte[] nextThreeBytes = new byte[3];
        int amountRead = this.seqSource.read(nextThreeBytes);
        if (amountRead > 0) {
            this.seqSource.unread(nextThreeBytes, 0, amountRead);
        }
        if (amountRead < 3) {
            return bracesParameter;
        }
        if ((nextThreeBytes[0] == 13 || nextThreeBytes[0] == 10) && (nextThreeBytes[1] == 47 || nextThreeBytes[1] == 62) || nextThreeBytes[0] == 13 && nextThreeBytes[1] == 10 && (nextThreeBytes[2] == 47 || nextThreeBytes[2] == 62)) {
            return 0;
        }
        return bracesParameter;
    }

    protected COSString parseCOSString() throws IOException {
        char nextChar = (char)this.seqSource.read();
        if (nextChar == '<') {
            return this.parseCOSHexString();
        }
        if (nextChar != '(') {
            throw new IOException("parseCOSString string should start with '(' or '<' and not '" + nextChar + "' at offset " + this.seqSource.getPosition());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int braces = 1;
        int c = this.seqSource.read();
        while (braces > 0 && c != -1) {
            char ch = (char)c;
            int nextc = -2;
            if (ch == ')') {
                --braces;
                if ((braces = this.checkForEndOfString(braces)) != 0) {
                    out.write(ch);
                }
            } else if (ch == '(') {
                ++braces;
                out.write(ch);
            } else if (ch == '\\') {
                char next = (char)this.seqSource.read();
                switch (next) {
                    case 'n': {
                        out.write(10);
                        break;
                    }
                    case 'r': {
                        out.write(13);
                        break;
                    }
                    case 't': {
                        out.write(9);
                        break;
                    }
                    case 'b': {
                        out.write(8);
                        break;
                    }
                    case 'f': {
                        out.write(12);
                        break;
                    }
                    case ')': {
                        braces = this.checkForEndOfString(braces);
                        if (braces != 0) {
                            out.write(next);
                            break;
                        }
                        out.write(92);
                        break;
                    }
                    case '(': 
                    case '\\': {
                        out.write(next);
                        break;
                    }
                    case '\n': 
                    case '\r': {
                        c = this.seqSource.read();
                        while (this.isEOL(c) && c != -1) {
                            c = this.seqSource.read();
                        }
                        nextc = c;
                        break;
                    }
                    case '0': 
                    case '1': 
                    case '2': 
                    case '3': 
                    case '4': 
                    case '5': 
                    case '6': 
                    case '7': {
                        StringBuilder octal = new StringBuilder();
                        octal.append(next);
                        c = this.seqSource.read();
                        char digit = (char)c;
                        if (digit >= '0' && digit <= '7') {
                            octal.append(digit);
                            c = this.seqSource.read();
                            digit = (char)c;
                            if (digit >= '0' && digit <= '7') {
                                octal.append(digit);
                            } else {
                                nextc = c;
                            }
                        } else {
                            nextc = c;
                        }
                        int character = 0;
                        try {
                            character = Integer.parseInt(octal.toString(), 8);
                        }
                        catch (NumberFormatException e) {
                            throw new IOException("Error: Expected octal character, actual='" + octal + "'", e);
                        }
                        out.write(character);
                        break;
                    }
                    default: {
                        out.write(next);
                        break;
                    }
                }
            } else {
                out.write(ch);
            }
            if (nextc != -2) {
                c = nextc;
                continue;
            }
            c = this.seqSource.read();
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
        return new COSString(out.toByteArray());
    }

    private COSString parseCOSHexString() throws IOException {
        StringBuilder sBuf;
        block6: {
            int c;
            sBuf = new StringBuilder();
            while (true) {
                if (BaseParser.isHexDigit((char)(c = this.seqSource.read()))) {
                    sBuf.append((char)c);
                    continue;
                }
                if (c == 62) break block6;
                if (c < 0) {
                    throw new IOException("Missing closing bracket for hex string. Reached EOS.");
                }
                if (c != 32 && c != 10 && c != 9 && c != 13 && c != 8 && c != 12) break;
            }
            if (sBuf.length() % 2 != 0) {
                sBuf.deleteCharAt(sBuf.length() - 1);
            }
            while ((c = this.seqSource.read()) != 62 && c >= 0) {
            }
            if (c < 0) {
                throw new IOException("Missing closing bracket for hex string. Reached EOS.");
            }
        }
        return COSString.parseHex(sBuf.toString());
    }

    protected COSArray parseCOSArray() throws IOException {
        int i;
        long startPosition = this.seqSource.getPosition();
        this.readExpectedChar('[');
        COSArray po = new COSArray();
        this.skipSpaces();
        while ((i = this.seqSource.peek()) > 0 && (char)i != ']') {
            COSBase pbo = this.parseDirObject();
            if (pbo instanceof COSObject) {
                if (po.size() > 0 && po.get(po.size() - 1) instanceof COSInteger) {
                    COSInteger genNumber = (COSInteger)po.remove(po.size() - 1);
                    if (po.size() > 0 && po.get(po.size() - 1) instanceof COSInteger) {
                        COSInteger number = (COSInteger)po.remove(po.size() - 1);
                        COSObjectKey key = new COSObjectKey(number.longValue(), genNumber.intValue());
                        pbo = this.getObjectFromPool(key);
                    } else {
                        pbo = null;
                    }
                } else {
                    pbo = null;
                }
            }
            if (pbo != null) {
                po.add(pbo);
            } else {
                LOG.warn((Object)("Corrupt array element at offset " + this.seqSource.getPosition() + ", start offset: " + startPosition));
                String isThisTheEnd = this.readString();
                if (isThisTheEnd.isEmpty() && this.seqSource.peek() == 91) {
                    return po;
                }
                this.seqSource.unread(isThisTheEnd.getBytes(Charsets.ISO_8859_1));
                if (ENDOBJ_STRING.equals(isThisTheEnd) || ENDSTREAM_STRING.equals(isThisTheEnd)) {
                    return po;
                }
            }
            this.skipSpaces();
        }
        this.seqSource.read();
        this.skipSpaces();
        return po;
    }

    protected boolean isEndOfName(int ch) {
        return ch == 32 || ch == 13 || ch == 10 || ch == 9 || ch == 62 || ch == 60 || ch == 91 || ch == 47 || ch == 93 || ch == 41 || ch == 40 || ch == 0 || ch == 12 || ch == 37;
    }

    protected COSName parseCOSName() throws IOException {
        byte[] bytes;
        this.readExpectedChar('/');
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int c = this.seqSource.read();
        while (c != -1) {
            int ch = c;
            if (ch == 35) {
                int ch1 = this.seqSource.read();
                int ch2 = this.seqSource.read();
                if (BaseParser.isHexDigit((char)ch1) && BaseParser.isHexDigit((char)ch2)) {
                    String hex = Character.toString((char)ch1) + (char)ch2;
                    try {
                        buffer.write(Integer.parseInt(hex, 16));
                    }
                    catch (NumberFormatException e) {
                        throw new IOException("Error: expected hex digit, actual='" + hex + "'", e);
                    }
                    c = this.seqSource.read();
                    continue;
                }
                if (ch2 == -1 || ch1 == -1) {
                    LOG.error((Object)"Premature EOF in BaseParser#parseCOSName");
                    c = -1;
                    break;
                }
                this.seqSource.unread(ch2);
                c = ch1;
                buffer.write(ch);
                continue;
            }
            if (this.isEndOfName(ch)) break;
            buffer.write(ch);
            c = this.seqSource.read();
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
        String string = this.isValidUTF8(bytes = buffer.toByteArray()) ? new String(bytes, Charsets.UTF_8) : new String(bytes, Charsets.WINDOWS_1252);
        return COSName.getPDFName(string);
    }

    private boolean isValidUTF8(byte[] input) {
        try {
            this.utf8Decoder.decode(ByteBuffer.wrap(input));
            return true;
        }
        catch (CharacterCodingException e) {
            return false;
        }
    }

    protected COSBoolean parseBoolean() throws IOException {
        COSBoolean retval;
        char c = (char)this.seqSource.peek();
        if (c == 't') {
            String trueString = new String(this.seqSource.readFully(4), Charsets.ISO_8859_1);
            if (!trueString.equals(TRUE)) {
                throw new IOException("Error parsing boolean: expected='true' actual='" + trueString + "' at offset " + this.seqSource.getPosition());
            }
            retval = COSBoolean.TRUE;
        } else if (c == 'f') {
            String falseString = new String(this.seqSource.readFully(5), Charsets.ISO_8859_1);
            if (!falseString.equals(FALSE)) {
                throw new IOException("Error parsing boolean: expected='true' actual='" + falseString + "' at offset " + this.seqSource.getPosition());
            }
            retval = COSBoolean.FALSE;
        } else {
            throw new IOException("Error parsing boolean expected='t or f' actual='" + c + "' at offset " + this.seqSource.getPosition());
        }
        return retval;
    }

    protected COSBase parseDirObject() throws IOException {
        this.skipSpaces();
        char c = (char)this.seqSource.peek();
        switch (c) {
            case '<': {
                int leftBracket = this.seqSource.read();
                c = (char)this.seqSource.peek();
                this.seqSource.unread(leftBracket);
                return c == '<' ? this.parseCOSDictionary() : this.parseCOSString();
            }
            case '[': {
                return this.parseCOSArray();
            }
            case '(': {
                return this.parseCOSString();
            }
            case '/': {
                return this.parseCOSName();
            }
            case 'n': {
                this.readExpectedString(NULL);
                return COSNull.NULL;
            }
            case 't': {
                String trueString = new String(this.seqSource.readFully(4), Charsets.ISO_8859_1);
                if (trueString.equals(TRUE)) {
                    return COSBoolean.TRUE;
                }
                throw new IOException("expected true actual='" + trueString + "' " + this.seqSource + "' at offset " + this.seqSource.getPosition());
            }
            case 'f': {
                String falseString = new String(this.seqSource.readFully(5), Charsets.ISO_8859_1);
                if (falseString.equals(FALSE)) {
                    return COSBoolean.FALSE;
                }
                throw new IOException("expected false actual='" + falseString + "' " + this.seqSource + "' at offset " + this.seqSource.getPosition());
            }
            case 'R': {
                this.seqSource.read();
                return new COSObject(null);
            }
            case '\uffff': {
                return null;
            }
        }
        if (Character.isDigit(c) || c == '-' || c == '+' || c == '.') {
            return this.parseCOSNumber();
        }
        long startOffset = this.seqSource.getPosition();
        String badString = this.readString();
        if (badString.isEmpty()) {
            int peek = this.seqSource.peek();
            throw new IOException("Unknown dir object c='" + c + "' cInt=" + c + " peek='" + (char)peek + "' peekInt=" + peek + " at offset " + this.seqSource.getPosition() + " (start offset: " + startOffset + ")");
        }
        if (!ENDOBJ_STRING.equals(badString) && !ENDSTREAM_STRING.equals(badString)) {
            LOG.warn((Object)("Skipped unexpected dir object = '" + badString + "' at offset " + this.seqSource.getPosition() + " (start offset: " + startOffset + ")"));
            return this instanceof PDFStreamParser ? null : COSNull.NULL;
        }
        this.seqSource.unread(badString.getBytes(Charsets.ISO_8859_1));
        return null;
    }

    private COSNumber parseCOSNumber() throws IOException {
        StringBuilder buf = new StringBuilder();
        int ic = this.seqSource.read();
        char c = (char)ic;
        while (Character.isDigit(c) || c == '-' || c == '+' || c == '.' || c == 'E' || c == 'e') {
            buf.append(c);
            ic = this.seqSource.read();
            c = (char)ic;
        }
        if (ic != -1) {
            this.seqSource.unread(ic);
        }
        return COSNumber.get(buf.toString());
    }

    protected String readString() throws IOException {
        this.skipSpaces();
        StringBuilder buffer = new StringBuilder();
        int c = this.seqSource.read();
        while (!this.isEndOfName((char)c) && c != -1) {
            buffer.append((char)c);
            c = this.seqSource.read();
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
        return buffer.toString();
    }

    protected void readExpectedString(String expectedString) throws IOException {
        this.readExpectedString(expectedString.toCharArray(), false);
    }

    protected final void readExpectedString(char[] expectedString, boolean skipSpaces) throws IOException {
        this.skipSpaces();
        for (char c : expectedString) {
            if (this.seqSource.read() == c) continue;
            throw new IOException("Expected string '" + new String(expectedString) + "' but missed at character '" + c + "' at offset " + this.seqSource.getPosition());
        }
        this.skipSpaces();
    }

    protected void readExpectedChar(char ec) throws IOException {
        char c = (char)this.seqSource.read();
        if (c != ec) {
            throw new IOException("expected='" + ec + "' actual='" + c + "' at offset " + this.seqSource.getPosition());
        }
    }

    protected String readString(int length) throws IOException {
        this.skipSpaces();
        int c = this.seqSource.read();
        StringBuilder buffer = new StringBuilder(length);
        while (!this.isWhitespace(c) && !this.isClosing(c) && c != -1 && buffer.length() < length && c != 91 && c != 60 && c != 40 && c != 47) {
            buffer.append((char)c);
            c = this.seqSource.read();
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
        return buffer.toString();
    }

    protected boolean isClosing() throws IOException {
        return this.isClosing(this.seqSource.peek());
    }

    protected boolean isClosing(int c) {
        return c == 93;
    }

    protected String readLine() throws IOException {
        int c;
        if (this.seqSource.isEOF()) {
            throw new IOException("Error: End-of-File, expected line at offset " + this.seqSource.getPosition());
        }
        StringBuilder buffer = new StringBuilder(11);
        while ((c = this.seqSource.read()) != -1 && !this.isEOL(c)) {
            buffer.append((char)c);
        }
        if (this.isCR(c) && this.isLF(this.seqSource.peek())) {
            this.seqSource.read();
        }
        return buffer.toString();
    }

    protected boolean isEOL() throws IOException {
        return this.isEOL(this.seqSource.peek());
    }

    protected boolean isEOL(int c) {
        return this.isLF(c) || this.isCR(c);
    }

    private boolean isLF(int c) {
        return 10 == c;
    }

    private boolean isCR(int c) {
        return 13 == c;
    }

    protected boolean isWhitespace() throws IOException {
        return this.isWhitespace(this.seqSource.peek());
    }

    protected boolean isWhitespace(int c) {
        return c == 0 || c == 9 || c == 12 || c == 10 || c == 13 || c == 32;
    }

    protected boolean isSpace() throws IOException {
        return this.isSpace(this.seqSource.peek());
    }

    protected boolean isSpace(int c) {
        return 32 == c;
    }

    protected boolean isDigit() throws IOException {
        return BaseParser.isDigit(this.seqSource.peek());
    }

    protected static boolean isDigit(int c) {
        return c >= 48 && c <= 57;
    }

    protected void skipSpaces() throws IOException {
        int c = this.seqSource.read();
        while (this.isWhitespace(c) || c == 37) {
            if (c == 37) {
                c = this.seqSource.read();
                while (!this.isEOL(c) && c != -1) {
                    c = this.seqSource.read();
                }
                continue;
            }
            c = this.seqSource.read();
        }
        if (c != -1) {
            this.seqSource.unread(c);
        }
    }

    protected long readObjectNumber() throws IOException {
        long retval = this.readLong();
        if (retval < 0L || retval >= 10000000000L) {
            throw new IOException("Object Number '" + retval + "' has more than 10 digits or is negative");
        }
        return retval;
    }

    protected int readGenerationNumber() throws IOException {
        int retval = this.readInt();
        if (retval < 0 || (long)retval > 65535L) {
            throw new IOException("Generation Number '" + retval + "' has more than 5 digits");
        }
        return retval;
    }

    protected int readInt() throws IOException {
        this.skipSpaces();
        int retval = 0;
        StringBuilder intBuffer = this.readStringNumber();
        try {
            retval = Integer.parseInt(intBuffer.toString());
        }
        catch (NumberFormatException e) {
            this.seqSource.unread(intBuffer.toString().getBytes(Charsets.ISO_8859_1));
            throw new IOException("Error: Expected an integer type at offset " + this.seqSource.getPosition() + ", instead got '" + intBuffer + "'", e);
        }
        return retval;
    }

    protected long readLong() throws IOException {
        this.skipSpaces();
        long retval = 0L;
        StringBuilder longBuffer = this.readStringNumber();
        try {
            retval = Long.parseLong(longBuffer.toString());
        }
        catch (NumberFormatException e) {
            this.seqSource.unread(longBuffer.toString().getBytes(Charsets.ISO_8859_1));
            throw new IOException("Error: Expected a long type at offset " + this.seqSource.getPosition() + ", instead got '" + longBuffer + "'", e);
        }
        return retval;
    }

    protected final StringBuilder readStringNumber() throws IOException {
        int lastByte;
        StringBuilder buffer = new StringBuilder();
        while ((lastByte = this.seqSource.read()) >= 48 && lastByte <= 57) {
            buffer.append((char)lastByte);
            if (buffer.length() <= MAX_LENGTH_LONG) continue;
            throw new IOException("Number '" + buffer + "' is getting too long, stop reading at offset " + this.seqSource.getPosition());
        }
        if (lastByte != -1) {
            this.seqSource.unread(lastByte);
        }
        return buffer;
    }
}

