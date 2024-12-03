/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.fonts.cmaps;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.fonts.cmaps.CMap;
import com.lowagie.text.pdf.fonts.cmaps.CodespaceRange;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CMapParser {
    private static final String BEGIN_CODESPACE_RANGE = "begincodespacerange";
    private static final String BEGIN_BASE_FONT_CHAR = "beginbfchar";
    private static final String BEGIN_BASE_FONT_RANGE = "beginbfrange";
    private static final String MARK_END_OF_DICTIONARY = ">>";
    private static final String MARK_END_OF_ARRAY = "]";
    private byte[] tokenParserByteBuffer = new byte[512];

    public CMap parse(InputStream input) throws IOException {
        PushbackInputStream cmapStream = new PushbackInputStream(input);
        CMap result = new CMap();
        Object previousToken = null;
        Object token = null;
        while ((token = this.parseNextToken(cmapStream)) != null) {
            if (token instanceof Operator) {
                Operator op = (Operator)token;
                switch (op.op) {
                    case "begincodespacerange": {
                        int j;
                        Number cosCount = (Number)previousToken;
                        for (j = 0; j < cosCount.intValue(); ++j) {
                            byte[] startRange = (byte[])this.parseNextToken(cmapStream);
                            byte[] endRange = (byte[])this.parseNextToken(cmapStream);
                            CodespaceRange range = new CodespaceRange();
                            range.setStart(startRange);
                            range.setEnd(endRange);
                            result.addCodespaceRange(range);
                        }
                        break;
                    }
                    case "beginbfchar": {
                        int j;
                        Number cosCount = (Number)previousToken;
                        for (j = 0; j < cosCount.intValue(); ++j) {
                            byte[] inputCode = (byte[])this.parseNextToken(cmapStream);
                            Object nextToken = this.parseNextToken(cmapStream);
                            if (nextToken instanceof byte[]) {
                                byte[] bytes = (byte[])nextToken;
                                String value = this.createStringFromBytes(bytes);
                                result.addMapping(inputCode, value);
                                continue;
                            }
                            if (nextToken instanceof LiteralName) {
                                result.addMapping(inputCode, ((LiteralName)nextToken).name);
                                continue;
                            }
                            throw new IOException(MessageLocalization.getComposedMessage("error.parsing.cmap.beginbfchar.expected.cosstring.or.cosname.and.not.1", nextToken));
                        }
                        break;
                    }
                    case "beginbfrange": {
                        int j;
                        Number cosCount = (Number)previousToken;
                        for (j = 0; j < cosCount.intValue(); ++j) {
                            byte[] startCode = (byte[])this.parseNextToken(cmapStream);
                            byte[] endCode = (byte[])this.parseNextToken(cmapStream);
                            Object nextToken = this.parseNextToken(cmapStream);
                            List array = null;
                            byte[] tokenBytes = null;
                            if (nextToken instanceof List) {
                                array = (List)nextToken;
                                tokenBytes = (byte[])array.get(0);
                            } else {
                                tokenBytes = (byte[])nextToken;
                            }
                            String value = null;
                            int arrayIndex = 0;
                            boolean done = false;
                            while (!done) {
                                if (this.compare(startCode, endCode) >= 0) {
                                    done = true;
                                }
                                value = this.createStringFromBytes(tokenBytes);
                                result.addMapping(startCode, value);
                                this.increment(startCode);
                                if (array == null) {
                                    this.increment(tokenBytes);
                                    continue;
                                }
                                if (++arrayIndex >= array.size()) continue;
                                tokenBytes = (byte[])array.get(arrayIndex);
                            }
                        }
                        break;
                    }
                }
            }
            previousToken = token;
        }
        return result;
    }

    private Object parseNextToken(PushbackInputStream is) throws IOException {
        Object retval = null;
        int nextByte = is.read();
        while (nextByte == 9 || nextByte == 32 || nextByte == 13 || nextByte == 10) {
            nextByte = is.read();
        }
        switch (nextByte) {
            case 37: {
                StringBuffer buffer = new StringBuffer();
                buffer.append((char)nextByte);
                this.readUntilEndOfLine(is, buffer);
                retval = buffer.toString();
                break;
            }
            case 40: {
                StringBuilder buffer = new StringBuilder();
                int stringByte = is.read();
                while (stringByte != -1 && stringByte != 41) {
                    buffer.append((char)stringByte);
                    stringByte = is.read();
                }
                retval = buffer.toString();
                break;
            }
            case 62: {
                int secondCloseBrace = is.read();
                if (secondCloseBrace == 62) {
                    retval = MARK_END_OF_DICTIONARY;
                    break;
                }
                throw new IOException(MessageLocalization.getComposedMessage("error.expected.the.end.of.a.dictionary"));
            }
            case 93: {
                retval = MARK_END_OF_ARRAY;
                break;
            }
            case 91: {
                ArrayList<Object> list = new ArrayList<Object>();
                Object nextToken = this.parseNextToken(is);
                while (nextToken != MARK_END_OF_ARRAY) {
                    list.add(nextToken);
                    nextToken = this.parseNextToken(is);
                }
                retval = list;
                break;
            }
            case 60: {
                int theNextByte = is.read();
                if (theNextByte == 60) {
                    HashMap<String, Object> result = new HashMap<String, Object>();
                    Object key = this.parseNextToken(is);
                    while (key instanceof LiteralName && key != MARK_END_OF_DICTIONARY) {
                        Object value = this.parseNextToken(is);
                        result.put(((LiteralName)key).name, value);
                        key = this.parseNextToken(is);
                    }
                    retval = result;
                    break;
                }
                int multiplyer = 16;
                int bufferIndex = -1;
                while (theNextByte != -1 && theNextByte != 62) {
                    int intValue = 0;
                    if (theNextByte == 32 || theNextByte == 9 || theNextByte == 10 || theNextByte == 13 || theNextByte == 12) {
                        theNextByte = is.read();
                        continue;
                    }
                    if (theNextByte >= 48 && theNextByte <= 57) {
                        intValue = theNextByte - 48;
                    } else if (theNextByte >= 65 && theNextByte <= 70) {
                        intValue = 10 + theNextByte - 65;
                    } else if (theNextByte >= 97 && theNextByte <= 102) {
                        intValue = 10 + theNextByte - 97;
                    } else {
                        throw new IOException(MessageLocalization.getComposedMessage("error.expected.hex.character.and.not.char.thenextbyte.1", theNextByte));
                    }
                    intValue *= multiplyer;
                    if (multiplyer == 16) {
                        this.tokenParserByteBuffer[++bufferIndex] = 0;
                        multiplyer = 1;
                    } else {
                        multiplyer = 16;
                    }
                    int n = bufferIndex;
                    this.tokenParserByteBuffer[n] = (byte)(this.tokenParserByteBuffer[n] + (byte)intValue);
                    theNextByte = is.read();
                }
                byte[] finalResult = new byte[bufferIndex + 1];
                System.arraycopy(this.tokenParserByteBuffer, 0, finalResult, 0, bufferIndex + 1);
                retval = finalResult;
                break;
            }
            case 47: {
                StringBuilder buffer = new StringBuilder();
                int stringByte = is.read();
                while (!this.isWhitespaceOrEOF(stringByte)) {
                    buffer.append((char)stringByte);
                    stringByte = is.read();
                }
                retval = new LiteralName(buffer.toString());
                break;
            }
            case -1: {
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
                StringBuilder buffer = new StringBuilder();
                buffer.append((char)nextByte);
                nextByte = is.read();
                while (!this.isWhitespaceOrEOF(nextByte) && (Character.isDigit((char)nextByte) || nextByte == 46)) {
                    buffer.append((char)nextByte);
                    nextByte = is.read();
                }
                is.unread(nextByte);
                String value = buffer.toString();
                if (value.indexOf(46) >= 0) {
                    retval = Double.valueOf(value);
                    break;
                }
                retval = Integer.valueOf(buffer.toString());
                break;
            }
            default: {
                StringBuilder buffer = new StringBuilder();
                buffer.append((char)nextByte);
                nextByte = is.read();
                while (!this.isWhitespaceOrEOF(nextByte)) {
                    buffer.append((char)nextByte);
                    nextByte = is.read();
                }
                retval = new Operator(buffer.toString());
                break;
            }
        }
        return retval;
    }

    private void readUntilEndOfLine(InputStream is, StringBuffer buf) throws IOException {
        int nextByte = is.read();
        while (nextByte != -1 && nextByte != 13 && nextByte != 10) {
            buf.append((char)nextByte);
            nextByte = is.read();
        }
    }

    private boolean isWhitespaceOrEOF(int aByte) {
        return aByte == -1 || aByte == 32 || aByte == 13 || aByte == 10;
    }

    private void increment(byte[] data) {
        this.increment(data, data.length - 1);
    }

    private void increment(byte[] data, int position) {
        if (position > 0 && (data[position] + 256) % 256 == 255) {
            data[position] = 0;
            this.increment(data, position - 1);
        } else {
            data[position] = (byte)(data[position] + 1);
        }
    }

    private String createStringFromBytes(byte[] bytes) throws IOException {
        String retval = null;
        retval = bytes.length == 1 ? new String(bytes) : new String(bytes, StandardCharsets.UTF_16BE);
        return retval;
    }

    private int compare(byte[] first, byte[] second) {
        int retval = 1;
        boolean done = false;
        for (int i = 0; i < first.length && !done; ++i) {
            if (first[i] == second[i]) continue;
            if ((first[i] + 256) % 256 < (second[i] + 256) % 256) {
                done = true;
                retval = -1;
                continue;
            }
            done = true;
            retval = 1;
        }
        return retval;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: java org.pdfbox.cmapparser.CMapParser <CMAP File>");
            System.exit(-1);
        }
        CMapParser parser = new CMapParser();
        CMap result = parser.parse(new FileInputStream(args[0]));
        System.out.println("Result:" + result);
    }

    private class Operator {
        private String op;

        private Operator(String theOp) {
            this.op = theOp;
        }
    }

    private class LiteralName {
        private String name;

        private LiteralName(String theName) {
            this.name = theName;
        }
    }
}

