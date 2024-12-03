/*
 * Decompiled with CFR 0.152.
 */
package com.sun.activation.registries;

public class MailcapTokenizer {
    public static final int UNKNOWN_TOKEN = 0;
    public static final int START_TOKEN = 1;
    public static final int STRING_TOKEN = 2;
    public static final int EOI_TOKEN = 5;
    public static final int SLASH_TOKEN = 47;
    public static final int SEMICOLON_TOKEN = 59;
    public static final int EQUALS_TOKEN = 61;
    private String data;
    private int dataIndex;
    private int dataLength;
    private int currentToken;
    private String currentTokenValue;
    private boolean isAutoquoting;
    private char autoquoteChar;

    public MailcapTokenizer(String inputString) {
        this.data = inputString;
        this.dataIndex = 0;
        this.dataLength = inputString.length();
        this.currentToken = 1;
        this.currentTokenValue = "";
        this.isAutoquoting = false;
        this.autoquoteChar = (char)59;
    }

    public void setIsAutoquoting(boolean value) {
        this.isAutoquoting = value;
    }

    public int getCurrentToken() {
        return this.currentToken;
    }

    public static String nameForToken(int token) {
        String name = "really unknown";
        switch (token) {
            case 0: {
                name = "unknown";
                break;
            }
            case 1: {
                name = "start";
                break;
            }
            case 2: {
                name = "string";
                break;
            }
            case 5: {
                name = "EOI";
                break;
            }
            case 47: {
                name = "'/'";
                break;
            }
            case 59: {
                name = "';'";
                break;
            }
            case 61: {
                name = "'='";
            }
        }
        return name;
    }

    public String getCurrentTokenValue() {
        return this.currentTokenValue;
    }

    public int nextToken() {
        if (this.dataIndex < this.dataLength) {
            while (this.dataIndex < this.dataLength && MailcapTokenizer.isWhiteSpaceChar(this.data.charAt(this.dataIndex))) {
                ++this.dataIndex;
            }
            if (this.dataIndex < this.dataLength) {
                char c = this.data.charAt(this.dataIndex);
                if (this.isAutoquoting) {
                    if (c == ';' || c == '=') {
                        this.currentToken = c;
                        this.currentTokenValue = new Character(c).toString();
                        ++this.dataIndex;
                    } else {
                        this.processAutoquoteToken();
                    }
                } else if (MailcapTokenizer.isStringTokenChar(c)) {
                    this.processStringToken();
                } else if (c == '/' || c == ';' || c == '=') {
                    this.currentToken = c;
                    this.currentTokenValue = new Character(c).toString();
                    ++this.dataIndex;
                } else {
                    this.currentToken = 0;
                    this.currentTokenValue = new Character(c).toString();
                    ++this.dataIndex;
                }
            } else {
                this.currentToken = 5;
                this.currentTokenValue = null;
            }
        } else {
            this.currentToken = 5;
            this.currentTokenValue = null;
        }
        return this.currentToken;
    }

    private void processStringToken() {
        int initialIndex = this.dataIndex;
        while (this.dataIndex < this.dataLength && MailcapTokenizer.isStringTokenChar(this.data.charAt(this.dataIndex))) {
            ++this.dataIndex;
        }
        this.currentToken = 2;
        this.currentTokenValue = this.data.substring(initialIndex, this.dataIndex);
    }

    private void processAutoquoteToken() {
        int initialIndex = this.dataIndex;
        boolean foundTerminator = false;
        while (this.dataIndex < this.dataLength && !foundTerminator) {
            char c = this.data.charAt(this.dataIndex);
            if (c != this.autoquoteChar) {
                ++this.dataIndex;
                continue;
            }
            foundTerminator = true;
        }
        this.currentToken = 2;
        this.currentTokenValue = MailcapTokenizer.fixEscapeSequences(this.data.substring(initialIndex, this.dataIndex));
    }

    private static boolean isSpecialChar(char c) {
        boolean lAnswer = false;
        switch (c) {
            case '\"': 
            case '(': 
            case ')': 
            case ',': 
            case '/': 
            case ':': 
            case ';': 
            case '<': 
            case '=': 
            case '>': 
            case '?': 
            case '@': 
            case '[': 
            case '\\': 
            case ']': {
                lAnswer = true;
            }
        }
        return lAnswer;
    }

    private static boolean isControlChar(char c) {
        return Character.isISOControl(c);
    }

    private static boolean isWhiteSpaceChar(char c) {
        return Character.isWhitespace(c);
    }

    private static boolean isStringTokenChar(char c) {
        return !MailcapTokenizer.isSpecialChar(c) && !MailcapTokenizer.isControlChar(c) && !MailcapTokenizer.isWhiteSpaceChar(c);
    }

    private static String fixEscapeSequences(String inputString) {
        int inputLength = inputString.length();
        StringBuffer buffer = new StringBuffer();
        buffer.ensureCapacity(inputLength);
        for (int i = 0; i < inputLength; ++i) {
            char currentChar = inputString.charAt(i);
            if (currentChar != '\\') {
                buffer.append(currentChar);
                continue;
            }
            if (i < inputLength - 1) {
                char nextChar = inputString.charAt(i + 1);
                buffer.append(nextChar);
                ++i;
                continue;
            }
            buffer.append(currentChar);
        }
        return buffer.toString();
    }
}

