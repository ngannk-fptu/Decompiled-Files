/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cmap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.fontbox.cmap.CMap;
import org.apache.fontbox.cmap.CodespaceRange;
import org.apache.fontbox.util.Charsets;

public class CMapParser {
    private static final String MARK_END_OF_DICTIONARY = ">>";
    private static final String MARK_END_OF_ARRAY = "]";
    private final byte[] tokenParserByteBuffer = new byte[512];
    private boolean strictMode = false;

    public CMapParser() {
    }

    public CMapParser(boolean strictMode) {
        this.strictMode = strictMode;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CMap parse(File file) throws IOException {
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            CMap cMap = this.parse(input);
            return cMap;
        }
        finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CMap parsePredefined(String name) throws IOException {
        InputStream input = null;
        try {
            input = this.getExternalCMap(name);
            this.strictMode = false;
            CMap cMap = this.parse(input);
            return cMap;
        }
        finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public CMap parse(InputStream input) throws IOException {
        Object token;
        PushbackInputStream cmapStream = new PushbackInputStream(input);
        CMap result = new CMap();
        Object previousToken = null;
        while ((token = this.parseNextToken(cmapStream)) != null) {
            if (token instanceof Operator) {
                Operator op = (Operator)token;
                if (op.op.equals("endcmap")) break;
                if (previousToken != null) {
                    if (op.op.equals("usecmap") && previousToken instanceof LiteralName) {
                        this.parseUsecmap((LiteralName)previousToken, result);
                    } else if (previousToken instanceof Number) {
                        if (op.op.equals("begincodespacerange")) {
                            this.parseBegincodespacerange((Number)previousToken, cmapStream, result);
                        } else if (op.op.equals("beginbfchar")) {
                            this.parseBeginbfchar((Number)previousToken, cmapStream, result);
                        } else if (op.op.equals("beginbfrange")) {
                            this.parseBeginbfrange((Number)previousToken, cmapStream, result);
                        } else if (op.op.equals("begincidchar")) {
                            this.parseBegincidchar((Number)previousToken, cmapStream, result);
                        } else if (op.op.equals("begincidrange") && previousToken instanceof Integer) {
                            this.parseBegincidrange((Integer)previousToken, cmapStream, result);
                        }
                    }
                }
            } else if (token instanceof LiteralName) {
                this.parseLiteralName((LiteralName)token, cmapStream, result);
            }
            previousToken = token;
        }
        return result;
    }

    private void parseUsecmap(LiteralName useCmapName, CMap result) throws IOException {
        InputStream useStream = this.getExternalCMap(useCmapName.name);
        CMap useCMap = this.parse(useStream);
        result.useCmap(useCMap);
    }

    private void parseLiteralName(LiteralName literal, PushbackInputStream cmapStream, CMap result) throws IOException {
        Object next;
        if ("WMode".equals(literal.name)) {
            Object next2 = this.parseNextToken(cmapStream);
            if (next2 instanceof Integer) {
                result.setWMode((Integer)next2);
            }
        } else if ("CMapName".equals(literal.name)) {
            Object next3 = this.parseNextToken(cmapStream);
            if (next3 instanceof LiteralName) {
                result.setName(((LiteralName)next3).name);
            }
        } else if ("CMapVersion".equals(literal.name)) {
            Object next4 = this.parseNextToken(cmapStream);
            if (next4 instanceof Number) {
                result.setVersion(next4.toString());
            } else if (next4 instanceof String) {
                result.setVersion((String)next4);
            }
        } else if ("CMapType".equals(literal.name)) {
            Object next5 = this.parseNextToken(cmapStream);
            if (next5 instanceof Integer) {
                result.setType((Integer)next5);
            }
        } else if ("Registry".equals(literal.name)) {
            Object next6 = this.parseNextToken(cmapStream);
            if (next6 instanceof String) {
                result.setRegistry((String)next6);
            }
        } else if ("Ordering".equals(literal.name)) {
            Object next7 = this.parseNextToken(cmapStream);
            if (next7 instanceof String) {
                result.setOrdering((String)next7);
            }
        } else if ("Supplement".equals(literal.name) && (next = this.parseNextToken(cmapStream)) instanceof Integer) {
            result.setSupplement((Integer)next);
        }
    }

    private void checkExpectedOperator(Operator operator, String expectedOperatorName, String rangeName) throws IOException {
        if (!operator.op.equals(expectedOperatorName)) {
            throw new IOException("Error : ~" + rangeName + " contains an unexpected operator : " + operator.op);
        }
    }

    private void parseBegincodespacerange(Number cosCount, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int j = 0; j < cosCount.intValue(); ++j) {
            Object nextToken = this.parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                this.checkExpectedOperator((Operator)nextToken, "endcodespacerange", "codespacerange");
                break;
            }
            if (!(nextToken instanceof byte[])) {
                throw new IOException("start range missing");
            }
            byte[] startRange = (byte[])nextToken;
            byte[] endRange = (byte[])this.parseNextToken(cmapStream);
            try {
                result.addCodespaceRange(new CodespaceRange(startRange, endRange));
                continue;
            }
            catch (IllegalArgumentException ex) {
                throw new IOException(ex);
            }
        }
    }

    private void parseBeginbfchar(Number cosCount, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int j = 0; j < cosCount.intValue(); ++j) {
            Object nextToken = this.parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                this.checkExpectedOperator((Operator)nextToken, "endbfchar", "bfchar");
                break;
            }
            if (!(nextToken instanceof byte[])) {
                throw new IOException("input code missing");
            }
            byte[] inputCode = (byte[])nextToken;
            nextToken = this.parseNextToken(cmapStream);
            if (nextToken instanceof byte[]) {
                byte[] bytes = (byte[])nextToken;
                String value = this.createStringFromBytes(bytes);
                result.addCharMapping(inputCode, value);
                continue;
            }
            if (nextToken instanceof LiteralName) {
                result.addCharMapping(inputCode, ((LiteralName)nextToken).name);
                continue;
            }
            throw new IOException("Error parsing CMap beginbfchar, expected{COSString or COSName} and not " + nextToken);
        }
    }

    private void parseBegincidrange(int numberOfLines, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int n = 0; n < numberOfLines; ++n) {
            Object nextToken = this.parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                this.checkExpectedOperator((Operator)nextToken, "endcidrange", "cidrange");
                break;
            }
            if (!(nextToken instanceof byte[])) {
                throw new IOException("start range missing");
            }
            byte[] startCode = (byte[])nextToken;
            int start = this.createIntFromBytes(startCode);
            byte[] endCode = (byte[])this.parseNextToken(cmapStream);
            int end = this.createIntFromBytes(endCode);
            int mappedCode = (Integer)this.parseNextToken(cmapStream);
            if (startCode.length <= 2 && endCode.length <= 2) {
                if (end == start) {
                    result.addCIDMapping(mappedCode, start);
                    continue;
                }
                result.addCIDRange((char)start, (char)end, mappedCode);
                continue;
            }
            int endOfMappings = mappedCode + end - start;
            while (mappedCode <= endOfMappings) {
                int mappedCID = this.createIntFromBytes(startCode);
                result.addCIDMapping(mappedCode++, mappedCID);
                this.increment(startCode, startCode.length - 1, false);
            }
        }
    }

    private void parseBegincidchar(Number cosCount, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int j = 0; j < cosCount.intValue(); ++j) {
            Object nextToken = this.parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                this.checkExpectedOperator((Operator)nextToken, "endcidchar", "cidchar");
                break;
            }
            if (!(nextToken instanceof byte[])) {
                throw new IOException("start code missing");
            }
            byte[] inputCode = (byte[])nextToken;
            int mappedCode = (Integer)this.parseNextToken(cmapStream);
            int mappedCID = this.createIntFromBytes(inputCode);
            result.addCIDMapping(mappedCode, mappedCID);
        }
    }

    private void parseBeginbfrange(Number cosCount, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int j = 0; j < cosCount.intValue(); ++j) {
            byte[] tokenBytes;
            Object nextToken = this.parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                this.checkExpectedOperator((Operator)nextToken, "endbfrange", "bfrange");
                break;
            }
            if (!(nextToken instanceof byte[])) {
                throw new IOException("start code missing");
            }
            byte[] startCode = (byte[])nextToken;
            nextToken = this.parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                this.checkExpectedOperator((Operator)nextToken, "endbfrange", "bfrange");
                break;
            }
            if (!(nextToken instanceof byte[])) {
                throw new IOException("end code missing");
            }
            byte[] endCode = (byte[])nextToken;
            int start = CMap.toInt(startCode, startCode.length);
            int end = CMap.toInt(endCode, endCode.length);
            if (end < start) break;
            nextToken = this.parseNextToken(cmapStream);
            if (nextToken instanceof List) {
                List array = (List)nextToken;
                if (array.isEmpty() || array.size() < end - start) continue;
                this.addMappingFrombfrange(result, startCode, array);
                continue;
            }
            if (!(nextToken instanceof byte[]) || (tokenBytes = (byte[])nextToken).length <= 0) continue;
            if (tokenBytes.length == 2 && start == 0 && end == 65535 && tokenBytes[0] == 0 && tokenBytes[1] == 0) {
                for (int i = 0; i < 256; ++i) {
                    startCode[0] = (byte)i;
                    startCode[1] = 0;
                    tokenBytes[0] = (byte)i;
                    tokenBytes[1] = 0;
                    this.addMappingFrombfrange(result, startCode, 256, tokenBytes);
                }
                continue;
            }
            this.addMappingFrombfrange(result, startCode, end - start + 1, tokenBytes);
        }
    }

    private void addMappingFrombfrange(CMap cmap, byte[] startCode, List<byte[]> tokenBytesList) {
        for (byte[] tokenBytes : tokenBytesList) {
            String value = this.createStringFromBytes(tokenBytes);
            cmap.addCharMapping(startCode, value);
            this.increment(startCode, startCode.length - 1, false);
        }
    }

    private void addMappingFrombfrange(CMap cmap, byte[] startCode, int values, byte[] tokenBytes) {
        for (int i = 0; i < values; ++i) {
            String value = this.createStringFromBytes(tokenBytes);
            cmap.addCharMapping(startCode, value);
            if (!this.increment(tokenBytes, tokenBytes.length - 1, this.strictMode)) break;
            this.increment(startCode, startCode.length - 1, false);
        }
    }

    protected InputStream getExternalCMap(String name) throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(name);
        if (resourceAsStream == null) {
            throw new IOException("Error: Could not find referenced cmap stream " + name);
        }
        return new BufferedInputStream(resourceAsStream);
    }

    private Object parseNextToken(PushbackInputStream is) throws IOException {
        Object retval = null;
        int nextByte = is.read();
        while (nextByte == 9 || nextByte == 32 || nextByte == 13 || nextByte == 10) {
            nextByte = is.read();
        }
        switch (nextByte) {
            case 37: {
                StringBuilder buffer = new StringBuilder();
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
                throw new IOException("Error: expected the end of a dictionary.");
            }
            case 93: {
                retval = MARK_END_OF_ARRAY;
                break;
            }
            case 91: {
                ArrayList<Object> list = new ArrayList<Object>();
                Object nextToken = this.parseNextToken(is);
                while (nextToken != null && !MARK_END_OF_ARRAY.equals(nextToken)) {
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
                    while (key instanceof LiteralName && !MARK_END_OF_DICTIONARY.equals(((LiteralName)key).name)) {
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
                    if (theNextByte >= 48 && theNextByte <= 57) {
                        intValue = theNextByte - 48;
                    } else if (theNextByte >= 65 && theNextByte <= 70) {
                        intValue = 10 + theNextByte - 65;
                    } else if (theNextByte >= 97 && theNextByte <= 102) {
                        intValue = 10 + theNextByte - 97;
                    } else {
                        if (this.isWhitespaceOrEOF(theNextByte)) {
                            theNextByte = is.read();
                            continue;
                        }
                        throw new IOException("Error: expected hex character and not " + (char)theNextByte + ":" + theNextByte);
                    }
                    intValue *= multiplyer;
                    if (multiplyer == 16) {
                        if (++bufferIndex >= this.tokenParserByteBuffer.length) {
                            throw new IOException("cmap token ist larger than buffer size " + this.tokenParserByteBuffer.length);
                        }
                        this.tokenParserByteBuffer[bufferIndex] = 0;
                        multiplyer = 1;
                    } else {
                        multiplyer = 16;
                    }
                    int n = bufferIndex;
                    this.tokenParserByteBuffer[n] = (byte)(this.tokenParserByteBuffer[n] + intValue);
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
                while (!this.isWhitespaceOrEOF(stringByte) && !this.isDelimiter(stringByte)) {
                    buffer.append((char)stringByte);
                    stringByte = is.read();
                }
                if (this.isDelimiter(stringByte)) {
                    is.unread(stringByte);
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
                try {
                    if (value.indexOf(46) >= 0) {
                        retval = Double.valueOf(value);
                        break;
                    }
                    retval = Integer.valueOf(value);
                    break;
                }
                catch (NumberFormatException ex) {
                    throw new IOException("Invalid number '" + value + "'", ex);
                }
            }
            default: {
                StringBuilder buffer = new StringBuilder();
                buffer.append((char)nextByte);
                nextByte = is.read();
                while (!(this.isWhitespaceOrEOF(nextByte) || this.isDelimiter(nextByte) || Character.isDigit(nextByte))) {
                    buffer.append((char)nextByte);
                    nextByte = is.read();
                }
                if (this.isDelimiter(nextByte) || Character.isDigit(nextByte)) {
                    is.unread(nextByte);
                }
                retval = new Operator(buffer.toString());
                break;
            }
        }
        return retval;
    }

    private void readUntilEndOfLine(InputStream is, StringBuilder buf) throws IOException {
        int nextByte = is.read();
        while (nextByte != -1 && nextByte != 13 && nextByte != 10) {
            buf.append((char)nextByte);
            nextByte = is.read();
        }
    }

    private boolean isWhitespaceOrEOF(int aByte) {
        return aByte == -1 || aByte == 32 || aByte == 13 || aByte == 10;
    }

    private boolean isDelimiter(int aByte) {
        switch (aByte) {
            case 37: 
            case 40: 
            case 41: 
            case 47: 
            case 60: 
            case 62: 
            case 91: 
            case 93: 
            case 123: 
            case 125: {
                return true;
            }
        }
        return false;
    }

    private boolean increment(byte[] data, int position, boolean useStrictMode) {
        if (position > 0 && (data[position] & 0xFF) == 255) {
            if (useStrictMode) {
                return false;
            }
            data[position] = 0;
            this.increment(data, position - 1, useStrictMode);
        } else {
            data[position] = (byte)(data[position] + 1);
        }
        return true;
    }

    private int createIntFromBytes(byte[] bytes) {
        int intValue = bytes[0] & 0xFF;
        if (bytes.length == 2) {
            intValue <<= 8;
            intValue += bytes[1] & 0xFF;
        }
        return intValue;
    }

    private String createStringFromBytes(byte[] bytes) {
        return new String(bytes, bytes.length == 1 ? Charsets.ISO_8859_1 : Charsets.UTF_16BE);
    }

    private static final class Operator {
        private String op;

        private Operator(String theOp) {
            this.op = theOp;
        }
    }

    private static final class LiteralName {
        private String name;

        private LiteralName(String theName) {
            this.name = theName;
        }
    }
}

