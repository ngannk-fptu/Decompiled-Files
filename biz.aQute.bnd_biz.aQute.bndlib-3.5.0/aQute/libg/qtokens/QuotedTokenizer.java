/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.qtokens;

import aQute.libg.generics.Create;
import java.util.List;

public class QuotedTokenizer {
    String string;
    int index = 0;
    String separators;
    boolean returnTokens;
    boolean ignoreWhiteSpace = true;
    String peek;
    char separator;

    public QuotedTokenizer(String string, String separators, boolean returnTokens) {
        if (string == null) {
            throw new IllegalArgumentException("string argument must be not null");
        }
        this.string = string;
        this.separators = separators;
        this.returnTokens = returnTokens;
    }

    public QuotedTokenizer(String string, String separators) {
        this(string, separators, false);
    }

    public String nextToken(String separators) {
        this.separator = '\u0000';
        if (this.peek != null) {
            String tmp = this.peek;
            this.peek = null;
            return tmp;
        }
        if (this.index == this.string.length()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean hadstring = false;
        boolean validspace = false;
        block3: while (this.index < this.string.length()) {
            char c;
            if (Character.isWhitespace(c = this.string.charAt(this.index++))) {
                if (this.index == this.string.length()) break;
                if (!validspace) continue;
                sb.append(c);
                continue;
            }
            if (separators.indexOf(c) >= 0) {
                if (this.returnTokens) {
                    this.peek = Character.toString(c);
                    break;
                }
                this.separator = c;
                break;
            }
            switch (c) {
                case '\"': 
                case '\'': {
                    hadstring = true;
                    this.quotedString(sb, c);
                    validspace = false;
                    continue block3;
                }
            }
            sb.append(c);
            validspace = true;
        }
        String result = sb.toString();
        if (!hadstring) {
            result = result.trim();
        }
        if (!hadstring && result.length() == 0 && this.index == this.string.length()) {
            return null;
        }
        return result;
    }

    public String nextToken() {
        return this.nextToken(this.separators);
    }

    private void quotedString(StringBuilder sb, char c) {
        char quote = c;
        while (this.index < this.string.length() && (c = this.string.charAt(this.index++)) != quote) {
            if (c == '\\' && this.index < this.string.length()) {
                char cc;
                if ((cc = this.string.charAt(this.index++)) != quote) {
                    sb.append("\\");
                }
                c = cc;
            }
            sb.append(c);
        }
    }

    public String[] getTokens() {
        return this.getTokens(0);
    }

    private String[] getTokens(int cnt) {
        String token = this.nextToken();
        if (token == null) {
            return new String[cnt];
        }
        String[] result = this.getTokens(cnt + 1);
        result[cnt] = token;
        return result;
    }

    public char getSeparator() {
        return this.separator;
    }

    public List<String> getTokenSet() {
        List<String> list = Create.list();
        String token = this.nextToken();
        while (token != null) {
            list.add(token);
            token = this.nextToken();
        }
        return list;
    }
}

