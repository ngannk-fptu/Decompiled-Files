/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.util;

public class StringIndenter {
    private String json;
    private int startTagIndex;
    private int endTagIndex;
    private int currentNumberOfIndents;
    private StringBuilder result;

    public String result() {
        try {
            this.indent();
        }
        catch (RuntimeException re) {
            throw new RuntimeException("Problem here: " + this, re);
        }
        if (this.json == null) {
            return null;
        }
        String resultString = this.result.toString();
        return resultString.trim();
    }

    private void indent() {
        if (this.json == null) {
            return;
        }
        this.result = new StringBuilder();
        this.startTagIndex = -1;
        this.endTagIndex = -1;
        this.currentNumberOfIndents = 0;
        while (true) {
            char nextChar;
            this.startTagIndex = this.findStartTagIndex();
            if (this.startTagIndex == -1) {
                if (this.endTagIndex == this.json.length() - 1) break;
                this.result.append(this.json, this.endTagIndex + 1, this.json.length());
                break;
            }
            if (StringIndenter.instantIndent(this.json, this.startTagIndex)) {
                ++this.currentNumberOfIndents;
                this.printNewlineIndent(this.startTagIndex, this.startTagIndex + 1);
                this.endTagIndex = this.startTagIndex;
                continue;
            }
            if (StringIndenter.instantUnindentTwoChars(this.json, this.startTagIndex)) {
                --this.currentNumberOfIndents;
                this.newlineIndent();
                this.printNewlineIndent(this.startTagIndex, this.startTagIndex + 2);
                this.endTagIndex = this.startTagIndex + 1;
                continue;
            }
            if (StringIndenter.instantUnindent(this.json, this.startTagIndex)) {
                --this.currentNumberOfIndents;
                if (this.onNewline()) {
                    this.unindent();
                } else {
                    this.newlineIndent();
                }
                this.printNewlineIndent(this.startTagIndex, this.startTagIndex + 1);
                this.endTagIndex = this.startTagIndex;
                continue;
            }
            if (StringIndenter.instantNewline(this.json, this.startTagIndex)) {
                this.printNewlineIndent(this.startTagIndex, this.startTagIndex + 1);
                this.endTagIndex = this.startTagIndex;
                continue;
            }
            this.endTagIndex = this.findEndTagIndex();
            this.result.append(this.json, this.startTagIndex, this.endTagIndex + 1);
            if (this.endTagIndex >= this.json.length() - 1 || (nextChar = this.json.charAt(this.endTagIndex + 1)) != ':') continue;
            this.result.append(':');
            ++this.endTagIndex;
        }
    }

    private boolean onNewline() {
        for (int i = this.result.length() - 1; i >= 0; --i) {
            char curChar = this.result.charAt(i);
            if (curChar == '\n') {
                return true;
            }
            if (Character.isWhitespace(curChar)) continue;
            return false;
        }
        return true;
    }

    private static boolean instantIndent(String json, int index) {
        char curChar = json.charAt(index);
        return curChar == '{' || curChar == '[';
    }

    private static boolean instantNewline(String json, int index) {
        char curChar = json.charAt(index);
        return curChar == ',';
    }

    private static boolean instantUnindent(String json, int index) {
        char curChar = json.charAt(index);
        return curChar == '}' || curChar == ']';
    }

    private static boolean instantUnindentTwoChars(String json, int index) {
        char curChar = json.charAt(index);
        if (index == json.length() - 1) {
            return false;
        }
        char nextchar = json.charAt(index + 1);
        return curChar == '}' && nextchar == ',';
    }

    private void printNewlineIndent(int start, int end) {
        this.result.append(this.json, start, end);
        this.newlineIndent();
    }

    private void newlineIndent() {
        this.result.append("\n").append(StringIndenter.repeat("  ", this.currentNumberOfIndents));
    }

    private static String repeat(String theString, int times) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; ++i) {
            result.append(theString);
        }
        return result.toString();
    }

    private void unindent() {
        for (int i = 0; i < 2; ++i) {
            if (this.result.charAt(this.result.length() - 1) != ' ') continue;
            this.result.deleteCharAt(this.result.length() - 1);
        }
    }

    private int findStartTagIndex() {
        return StringIndenter.findNextStartTagIndex(this.json, this.endTagIndex + 1);
    }

    private int findEndTagIndex() {
        return StringIndenter.findNextEndTagIndex(this.json, this.startTagIndex + 1);
    }

    private static int findNextStartTagIndex(String json, int startFrom) {
        int length = json.length();
        for (int i = startFrom; i < length; ++i) {
            char curChar = json.charAt(i);
            if (Character.isWhitespace(curChar)) continue;
            return i;
        }
        return -1;
    }

    private static int findNextEndTagIndex(String json, int startFrom) {
        int length = json.length();
        boolean quotedString = json.charAt(startFrom - 1) == '\"';
        int ignoreSlashInIndex = -1;
        boolean afterSlash = false;
        for (int i = startFrom; i < length; ++i) {
            afterSlash = i != ignoreSlashInIndex && i != startFrom && json.charAt(i - 1) == '\\';
            char curChar = json.charAt(i);
            if (!afterSlash && curChar == '\\') {
                ignoreSlashInIndex = i + 2;
            }
            if (!quotedString) {
                if (curChar != ':' && !Character.isWhitespace(curChar) && curChar != ']' && curChar != '}' && curChar != ',') continue;
                return i - 1;
            }
            if (afterSlash || curChar != '\"') continue;
            return i;
        }
        return json.length() - 1;
    }

    public StringIndenter(String theJson) {
        this.json = theJson == null ? "" : theJson.trim();
    }
}

