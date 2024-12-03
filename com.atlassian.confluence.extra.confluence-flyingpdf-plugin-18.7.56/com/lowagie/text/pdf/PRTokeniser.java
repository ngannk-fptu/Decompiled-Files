/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.exceptions.InvalidPdfException;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;

public class PRTokeniser {
    public static final int TK_NUMBER = 1;
    public static final int TK_STRING = 2;
    public static final int TK_NAME = 3;
    public static final int TK_COMMENT = 4;
    public static final int TK_START_ARRAY = 5;
    public static final int TK_END_ARRAY = 6;
    public static final int TK_START_DIC = 7;
    public static final int TK_END_DIC = 8;
    public static final int TK_REF = 9;
    public static final int TK_OTHER = 10;
    public static final int TK_ENDOFFILE = 11;
    public static final boolean[] delims = new boolean[]{true, true, false, false, false, false, false, false, false, false, true, true, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true, false, false, true, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    static final String EMPTY = "";
    protected RandomAccessFileOrArray file;
    protected int type;
    protected String stringValue;
    protected int reference;
    protected int generation;
    protected boolean hexString;

    public PRTokeniser(String filename) throws IOException {
        this.file = new RandomAccessFileOrArray(filename);
    }

    public PRTokeniser(byte[] pdfIn) {
        this.file = new RandomAccessFileOrArray(pdfIn);
    }

    public PRTokeniser(RandomAccessFileOrArray file) {
        this.file = file;
    }

    public void seek(int pos) throws IOException {
        this.file.seek(pos);
    }

    public int getFilePointer() throws IOException {
        return this.file.getFilePointer();
    }

    public void close() throws IOException {
        this.file.close();
    }

    public int length() throws IOException {
        return this.file.length();
    }

    public int read() throws IOException {
        return this.file.read();
    }

    public RandomAccessFileOrArray getSafeFile() {
        return new RandomAccessFileOrArray(this.file);
    }

    public RandomAccessFileOrArray getFile() {
        return this.file;
    }

    public String readString(int size) throws IOException {
        int ch;
        StringBuilder buf = new StringBuilder();
        while (size-- > 0 && (ch = this.file.read()) != -1) {
            buf.append((char)ch);
        }
        return buf.toString();
    }

    public static final boolean isWhitespace(int ch) {
        return ch == 0 || ch == 9 || ch == 10 || ch == 12 || ch == 13 || ch == 32;
    }

    public static final boolean isDelimiter(int ch) {
        return ch == 40 || ch == 41 || ch == 60 || ch == 62 || ch == 91 || ch == 93 || ch == 47 || ch == 37;
    }

    public static final boolean isDelimiterWhitespace(int ch) {
        return delims[ch + 1];
    }

    public int getTokenType() {
        return this.type;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public int getReference() {
        return this.reference;
    }

    public int getGeneration() {
        return this.generation;
    }

    public void backOnePosition(int ch) {
        if (ch != -1) {
            this.file.pushBack((byte)ch);
        }
    }

    public void throwError(String error) throws IOException {
        throw new InvalidPdfException(MessageLocalization.getComposedMessage("1.at.file.pointer.2", error, String.valueOf(this.file.getFilePointer())));
    }

    public char checkPdfHeader() throws IOException {
        this.file.setStartOffset(0);
        String str = this.readString(1024);
        int idx = str.indexOf("%PDF-");
        if (idx < 0) {
            throw new InvalidPdfException(MessageLocalization.getComposedMessage("pdf.header.not.found"));
        }
        this.file.setStartOffset(idx);
        return str.charAt(idx + 7);
    }

    public void checkFdfHeader() throws IOException {
        this.file.setStartOffset(0);
        String str = this.readString(1024);
        int idx = str.indexOf("%FDF-1.2");
        if (idx < 0) {
            throw new InvalidPdfException(MessageLocalization.getComposedMessage("fdf.header.not.found"));
        }
        this.file.setStartOffset(idx);
    }

    public int getStartxref() throws IOException {
        int size = Math.min(1024, this.file.length());
        int pos = this.file.length() - size;
        this.file.seek(pos);
        String str = this.readString(1024);
        int idx = str.lastIndexOf("startxref");
        if (idx < 0) {
            throw new InvalidPdfException(MessageLocalization.getComposedMessage("pdf.startxref.not.found"));
        }
        return pos + idx;
    }

    public static int getHex(int v) {
        if (v >= 48 && v <= 57) {
            return v - 48;
        }
        if (v >= 65 && v <= 70) {
            return v - 65 + 10;
        }
        if (v >= 97 && v <= 102) {
            return v - 97 + 10;
        }
        return -1;
    }

    public void nextValidToken() throws IOException {
        int level = 0;
        String n1 = null;
        String n2 = null;
        int ptr = 0;
        block4: while (this.nextToken() || level == 2) {
            if (this.type == 4) continue;
            switch (level) {
                case 0: {
                    if (this.type != 1) {
                        return;
                    }
                    ptr = this.file.getFilePointer();
                    n1 = this.stringValue;
                    ++level;
                    continue block4;
                }
                case 1: {
                    if (this.type != 1) {
                        this.file.seek(ptr);
                        this.type = 1;
                        this.stringValue = n1;
                        return;
                    }
                    n2 = this.stringValue;
                    ++level;
                    continue block4;
                }
            }
            if (this.type != 10 || !this.stringValue.equals("R")) {
                this.file.seek(ptr);
                this.type = 1;
                this.stringValue = n1;
                return;
            }
            this.type = 9;
            this.reference = Integer.parseInt(n1);
            this.generation = Integer.parseInt(n2);
            return;
        }
        if (level > 0) {
            this.type = 1;
            this.file.seek(ptr);
            this.stringValue = n1;
            return;
        }
        this.throwError("Unexpected end of file");
    }

    public boolean nextToken() throws IOException {
        int ch = 0;
        while ((ch = this.file.read()) != -1 && PRTokeniser.isWhitespace(ch)) {
        }
        if (ch == -1) {
            this.type = 11;
            return false;
        }
        StringBuffer outBuf = null;
        this.stringValue = EMPTY;
        switch (ch) {
            case 91: {
                this.type = 5;
                break;
            }
            case 93: {
                this.type = 6;
                break;
            }
            case 47: {
                outBuf = new StringBuffer();
                this.type = 3;
                while (!delims[(ch = this.file.read()) + 1]) {
                    if (ch == 35) {
                        ch = (PRTokeniser.getHex(this.file.read()) << 4) + PRTokeniser.getHex(this.file.read());
                    }
                    outBuf.append((char)ch);
                }
                this.backOnePosition(ch);
                break;
            }
            case 62: {
                ch = this.file.read();
                if (ch != 62) {
                    this.throwError(MessageLocalization.getComposedMessage("greaterthan.not.expected"));
                }
                this.type = 8;
                break;
            }
            case 60: {
                int v1 = this.file.read();
                while (PRTokeniser.isWhitespace(v1)) {
                    v1 = this.file.read();
                }
                if (v1 == 60) {
                    this.type = 7;
                    break;
                }
                outBuf = new StringBuffer();
                this.type = 2;
                this.hexString = true;
                int v2 = 0;
                while (true) {
                    if (PRTokeniser.isWhitespace(v1)) {
                        v1 = this.file.read();
                        continue;
                    }
                    if (v1 == 62 || (v1 = PRTokeniser.getHex(v1)) < 0) break;
                    v2 = this.file.read();
                    while (PRTokeniser.isWhitespace(v2)) {
                        v2 = this.file.read();
                    }
                    if (v2 == 62) {
                        ch = v1 << 4;
                        outBuf.append((char)ch);
                        break;
                    }
                    if ((v2 = PRTokeniser.getHex(v2)) < 0) break;
                    ch = (v1 << 4) + v2;
                    outBuf.append((char)ch);
                    v1 = this.file.read();
                }
                if (v1 >= 0 && v2 >= 0) break;
                this.throwError(MessageLocalization.getComposedMessage("error.reading.string"));
                break;
            }
            case 37: {
                this.type = 4;
                while ((ch = this.file.read()) != -1 && ch != 13 && ch != 10) {
                }
                break;
            }
            case 40: {
                outBuf = new StringBuffer();
                this.type = 2;
                this.hexString = false;
                int nesting = 0;
                while ((ch = this.file.read()) != -1) {
                    if (ch == 40) {
                        ++nesting;
                    } else if (ch == 41) {
                        --nesting;
                    } else if (ch == 92) {
                        boolean lineBreak = false;
                        ch = this.file.read();
                        switch (ch) {
                            case 110: {
                                ch = 10;
                                break;
                            }
                            case 114: {
                                ch = 13;
                                break;
                            }
                            case 116: {
                                ch = 9;
                                break;
                            }
                            case 98: {
                                ch = 8;
                                break;
                            }
                            case 102: {
                                ch = 12;
                                break;
                            }
                            case 40: 
                            case 41: 
                            case 92: {
                                break;
                            }
                            case 13: {
                                lineBreak = true;
                                ch = this.file.read();
                                if (ch == 10) break;
                                this.backOnePosition(ch);
                                break;
                            }
                            case 10: {
                                lineBreak = true;
                                break;
                            }
                            default: {
                                if (ch < 48 || ch > 55) break;
                                int octal = ch - 48;
                                ch = this.file.read();
                                if (ch < 48 || ch > 55) {
                                    this.backOnePosition(ch);
                                    ch = octal;
                                    break;
                                }
                                octal = (octal << 3) + ch - 48;
                                ch = this.file.read();
                                if (ch < 48 || ch > 55) {
                                    this.backOnePosition(ch);
                                    ch = octal;
                                    break;
                                }
                                octal = (octal << 3) + ch - 48;
                                ch = octal & 0xFF;
                                break;
                            }
                        }
                        if (lineBreak) continue;
                        if (ch < 0) {
                            break;
                        }
                    } else if (ch == 13) {
                        ch = this.file.read();
                        if (ch < 0) break;
                        if (ch != 10) {
                            this.backOnePosition(ch);
                            ch = 10;
                        }
                    }
                    if (nesting == -1) break;
                    outBuf.append((char)ch);
                }
                if (ch != -1) break;
                this.throwError(MessageLocalization.getComposedMessage("error.reading.string"));
                break;
            }
            default: {
                outBuf = new StringBuffer();
                if (ch == 45 || ch == 43 || ch == 46 || ch >= 48 && ch <= 57) {
                    this.type = 1;
                    do {
                        outBuf.append((char)ch);
                    } while ((ch = this.file.read()) != -1 && (ch >= 48 && ch <= 57 || ch == 46));
                } else {
                    this.type = 10;
                    do {
                        outBuf.append((char)ch);
                    } while (!delims[(ch = this.file.read()) + 1]);
                }
                this.backOnePosition(ch);
            }
        }
        if (outBuf != null) {
            this.stringValue = outBuf.toString();
        }
        return true;
    }

    public int intValue() {
        return Integer.parseInt(this.stringValue);
    }

    public boolean readLineSegment(byte[] input) throws IOException {
        int cur;
        int c = -1;
        boolean eol = false;
        int ptr = 0;
        int len = input.length;
        if (ptr < len) {
            while (PRTokeniser.isWhitespace(c = this.read())) {
            }
        }
        while (!eol && ptr < len) {
            switch (c) {
                case -1: 
                case 10: {
                    eol = true;
                    break;
                }
                case 13: {
                    eol = true;
                    cur = this.getFilePointer();
                    if (this.read() == 10) break;
                    this.seek(cur);
                    break;
                }
                default: {
                    input[ptr++] = (byte)c;
                }
            }
            if (eol || len <= ptr) break;
            c = this.read();
        }
        if (ptr >= len) {
            eol = false;
            while (!eol) {
                c = this.read();
                switch (c) {
                    case -1: 
                    case 10: {
                        eol = true;
                        break;
                    }
                    case 13: {
                        eol = true;
                        cur = this.getFilePointer();
                        if (this.read() == 10) break;
                        this.seek(cur);
                    }
                }
            }
        }
        if (c == -1 && ptr == 0) {
            return false;
        }
        if (ptr + 2 <= len) {
            input[ptr++] = 32;
            input[ptr] = 88;
        }
        return true;
    }

    public static int[] checkObjectStart(byte[] line) {
        try {
            PRTokeniser tk = new PRTokeniser(line);
            int num = 0;
            int gen = 0;
            if (!tk.nextToken() || tk.getTokenType() != 1) {
                return null;
            }
            num = tk.intValue();
            if (!tk.nextToken() || tk.getTokenType() != 1) {
                return null;
            }
            gen = tk.intValue();
            if (!tk.nextToken()) {
                return null;
            }
            if (!tk.getStringValue().equals("obj")) {
                return null;
            }
            return new int[]{num, gen};
        }
        catch (Exception exception) {
            return null;
        }
    }

    public boolean isHexString() {
        return this.hexString;
    }
}

