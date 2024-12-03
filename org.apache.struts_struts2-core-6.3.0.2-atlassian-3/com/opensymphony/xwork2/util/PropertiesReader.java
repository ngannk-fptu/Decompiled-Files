/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PropertiesReader
extends LineNumberReader {
    private List<String> commentLines = new ArrayList<String>();
    private String propertyName;
    private String propertyValue;
    private char delimiter;
    static final String COMMENT_CHARS = "#!";
    private static final int HEX_RADIX = 16;
    private static final int UNICODE_LEN = 4;
    private static final char[] SEPARATORS = new char[]{'=', ':'};
    private static final char[] WHITE_SPACE = new char[]{' ', '\t', '\f'};

    public PropertiesReader(Reader reader) {
        this(reader, ',');
    }

    public PropertiesReader(Reader reader, char listDelimiter) {
        super(reader);
        this.delimiter = listDelimiter;
    }

    boolean isCommentLine(String line) {
        String s = line.trim();
        return s.length() < 1 || COMMENT_CHARS.indexOf(s.charAt(0)) >= 0;
    }

    public String readProperty() throws IOException {
        String line;
        this.commentLines.clear();
        StringBuilder buffer = new StringBuilder();
        while (true) {
            if ((line = this.readLine()) == null) {
                return null;
            }
            if (this.isCommentLine(line)) {
                this.commentLines.add(line);
                continue;
            }
            if (!this.checkCombineLines(line = line.trim())) break;
            line = line.substring(0, line.length() - 1);
            buffer.append(line);
        }
        buffer.append(line);
        return buffer.toString();
    }

    public boolean nextProperty() throws IOException {
        String line = this.readProperty();
        if (line == null) {
            return false;
        }
        String[] property = this.parseProperty(line);
        this.propertyName = PropertiesReader.unescapeJava(property[0]);
        this.propertyValue = PropertiesReader.unescapeJava(property[1], this.delimiter);
        return true;
    }

    public List<String> getCommentLines() {
        return this.commentLines;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }

    private boolean checkCombineLines(String line) {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; --idx) {
            ++bsCount;
        }
        return bsCount % 2 == 1;
    }

    private String[] parseProperty(String line) {
        String[] result = new String[2];
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int state = 0;
        block6: for (int pos = 0; pos < line.length(); ++pos) {
            char c = line.charAt(pos);
            switch (state) {
                case 0: {
                    if (c == '\\') {
                        state = 1;
                        continue block6;
                    }
                    if (this.contains(WHITE_SPACE, c)) {
                        state = 2;
                        continue block6;
                    }
                    if (this.contains(SEPARATORS, c)) {
                        state = 3;
                        continue block6;
                    }
                    key.append(c);
                    continue block6;
                }
                case 1: {
                    if (this.contains(SEPARATORS, c) || this.contains(WHITE_SPACE, c)) {
                        key.append(c);
                    } else {
                        key.append('\\');
                        key.append(c);
                    }
                    state = 0;
                    continue block6;
                }
                case 2: {
                    if (this.contains(WHITE_SPACE, c)) {
                        state = 2;
                        continue block6;
                    }
                    if (this.contains(SEPARATORS, c)) {
                        state = 3;
                        continue block6;
                    }
                    value.append(c);
                    state = 3;
                    continue block6;
                }
                case 3: {
                    value.append(c);
                }
            }
        }
        result[0] = key.toString().trim();
        result[1] = value.toString().trim();
        return result;
    }

    protected static String unescapeJava(String str, char delimiter) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder out = new StringBuilder(sz);
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() != 4) continue;
                try {
                    int value = Integer.parseInt(unicode.toString(), 16);
                    out.append((char)value);
                    unicode.setLength(0);
                    inUnicode = false;
                    hadSlash = false;
                    continue;
                }
                catch (NumberFormatException nfe) {
                    throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                }
            }
            if (hadSlash) {
                hadSlash = false;
                if (ch == '\\') {
                    out.append('\\');
                    continue;
                }
                if (ch == '\'') {
                    out.append('\'');
                    continue;
                }
                if (ch == '\"') {
                    out.append('\"');
                    continue;
                }
                if (ch == 'r') {
                    out.append('\r');
                    continue;
                }
                if (ch == 'f') {
                    out.append('\f');
                    continue;
                }
                if (ch == 't') {
                    out.append('\t');
                    continue;
                }
                if (ch == 'n') {
                    out.append('\n');
                    continue;
                }
                if (ch == 'b') {
                    out.append('\b');
                    continue;
                }
                if (ch == delimiter) {
                    out.append('\\');
                    out.append(delimiter);
                    continue;
                }
                if (ch == 'u') {
                    inUnicode = true;
                    continue;
                }
                out.append(ch);
                continue;
            }
            if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.append(ch);
        }
        if (hadSlash) {
            out.append('\\');
        }
        return out.toString();
    }

    public boolean contains(char[] array, char objectToFind) {
        if (array == null) {
            return false;
        }
        for (char anArray : array) {
            if (objectToFind != anArray) continue;
            return true;
        }
        return false;
    }

    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            PropertiesReader.unescapeJava(writer, str);
            return writer.toString();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() != 4) continue;
                try {
                    int value = Integer.parseInt(unicode.toString(), 16);
                    out.write((char)value);
                    unicode.setLength(0);
                    inUnicode = false;
                    hadSlash = false;
                    continue;
                }
                catch (NumberFormatException nfe) {
                    throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                }
            }
            if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case '\\': {
                        out.write(92);
                        break;
                    }
                    case '\'': {
                        out.write(39);
                        break;
                    }
                    case '\"': {
                        out.write(34);
                        break;
                    }
                    case 'r': {
                        out.write(13);
                        break;
                    }
                    case 'f': {
                        out.write(12);
                        break;
                    }
                    case 't': {
                        out.write(9);
                        break;
                    }
                    case 'n': {
                        out.write(10);
                        break;
                    }
                    case 'b': {
                        out.write(8);
                        break;
                    }
                    case 'u': {
                        inUnicode = true;
                        break;
                    }
                    default: {
                        out.write(ch);
                        break;
                    }
                }
                continue;
            }
            if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            out.write(92);
        }
    }
}

