/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PGtokenizer {
    private static final Map<Character, Character> CLOSING_TO_OPENING_CHARACTER = new HashMap<Character, Character>();
    protected List<String> tokens = new ArrayList<String>();

    public PGtokenizer(String string, char delim) {
        this.tokenize(string, delim);
    }

    public int tokenize(String string, char delim) {
        this.tokens.clear();
        ArrayDeque<Character> stack = new ArrayDeque<Character>();
        boolean skipChar = false;
        boolean nestedDoubleQuote = false;
        char c = '\u0000';
        int s = 0;
        for (int p = 0; p < string.length(); ++p) {
            c = string.charAt(p);
            if (c == '(' || c == '[' || c == '<' || !nestedDoubleQuote && !skipChar && c == '\"') {
                stack.push(Character.valueOf(c));
                if (c == '\"') {
                    nestedDoubleQuote = true;
                    skipChar = true;
                }
            }
            if (c == ')' || c == ']' || c == '>' || nestedDoubleQuote && !skipChar && c == '\"') {
                if (c == '\"') {
                    while (!stack.isEmpty() && !Character.valueOf('\"').equals(stack.peek())) {
                        stack.pop();
                    }
                    nestedDoubleQuote = false;
                    stack.pop();
                } else {
                    Character ch = CLOSING_TO_OPENING_CHARACTER.get(Character.valueOf(c));
                    if (!stack.isEmpty() && ch != null && ch.equals(stack.peek())) {
                        stack.pop();
                    }
                }
            }
            boolean bl = skipChar = c == '\\';
            if (!stack.isEmpty() || c != delim) continue;
            this.tokens.add(string.substring(s, p));
            s = p + 1;
        }
        if (s < string.length()) {
            this.tokens.add(string.substring(s));
        }
        if (s == string.length() && c == delim) {
            this.tokens.add("");
        }
        return this.tokens.size();
    }

    public int getSize() {
        return this.tokens.size();
    }

    public String getToken(int n) {
        return this.tokens.get(n);
    }

    public PGtokenizer tokenizeToken(int n, char delim) {
        return new PGtokenizer(this.getToken(n), delim);
    }

    public static String remove(String s, String l, String t) {
        if (s.startsWith(l)) {
            s = s.substring(l.length());
        }
        if (s.endsWith(t)) {
            s = s.substring(0, s.length() - t.length());
        }
        return s;
    }

    public void remove(String l, String t) {
        for (int i = 0; i < this.tokens.size(); ++i) {
            this.tokens.set(i, PGtokenizer.remove(this.tokens.get(i), l, t));
        }
    }

    public static String removePara(String s) {
        return PGtokenizer.remove(s, "(", ")");
    }

    public void removePara() {
        this.remove("(", ")");
    }

    public static String removeBox(String s) {
        return PGtokenizer.remove(s, "[", "]");
    }

    public void removeBox() {
        this.remove("[", "]");
    }

    public static String removeAngle(String s) {
        return PGtokenizer.remove(s, "<", ">");
    }

    public void removeAngle() {
        this.remove("<", ">");
    }

    public static String removeCurlyBrace(String s) {
        return PGtokenizer.remove(s, "{", "}");
    }

    public void removeCurlyBrace() {
        this.remove("{", "}");
    }

    static {
        CLOSING_TO_OPENING_CHARACTER.put(Character.valueOf(')'), Character.valueOf('('));
        CLOSING_TO_OPENING_CHARACTER.put(Character.valueOf(']'), Character.valueOf('['));
        CLOSING_TO_OPENING_CHARACTER.put(Character.valueOf('>'), Character.valueOf('<'));
        CLOSING_TO_OPENING_CHARACTER.put(Character.valueOf('\"'), Character.valueOf('\"'));
    }
}

