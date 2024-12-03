/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Null;
import java.util.Arrays;
import java.util.LinkedList;

class StringUtil {
    StringUtil() {
    }

    static String[] split(String source) {
        Null.not("source", source);
        JSONList result = new JSONList(source.trim());
        return result.toArray(new String[result.size()]);
    }

    static String toString(String[] source) {
        Null.not("source", source);
        return new JSONList(source).toString();
    }

    private static class JSONList
    extends LinkedList<String> {
        private static final long serialVersionUID = -8317241062936626298L;

        JSONList(String source) {
            Tokenizer tokenizer = new Tokenizer(source);
            if (tokenizer.nextClean() != '[') {
                tokenizer.syntaxError("String must start with square bracket");
            }
            switch (tokenizer.nextClean()) {
                case '\u0000': 
                case ']': {
                    return;
                }
            }
            tokenizer.back();
            block10: while (true) {
                if (tokenizer.nextClean() == ',') {
                    tokenizer.back();
                    this.add(null);
                } else {
                    tokenizer.back();
                    this.add(tokenizer.nextValue());
                }
                char nextClean = tokenizer.nextClean();
                switch (nextClean) {
                    case ',': 
                    case ';': {
                        switch (tokenizer.nextClean()) {
                            case '\u0000': 
                            case ']': {
                                return;
                            }
                        }
                        tokenizer.back();
                        continue block10;
                    }
                    case '\u0000': 
                    case ']': {
                        return;
                    }
                }
                tokenizer.syntaxError("Expected a ',' or ']' rather than: " + nextClean);
            }
        }

        JSONList(String[] source) {
            super(Arrays.asList(source));
        }

        String join(String separator) {
            int len = this.size();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; ++i) {
                if (i > 0) {
                    sb.append(separator);
                }
                sb.append(this.quote((String)this.get(i)));
            }
            return sb.toString();
        }

        String quote(String string) {
            if (string == null || string.length() == 0) {
                return "\"\"";
            }
            char c = '\u0000';
            int len = string.length();
            StringBuffer sb = new StringBuffer(len + 4);
            sb.append('\"');
            block9: for (int i = 0; i < len; ++i) {
                char b = c;
                c = string.charAt(i);
                switch (c) {
                    case '\"': 
                    case '\\': {
                        sb.append('\\');
                        sb.append(c);
                        continue block9;
                    }
                    case '/': {
                        if (b == '<') {
                            sb.append('\\');
                        }
                        sb.append(c);
                        continue block9;
                    }
                    case '\b': {
                        sb.append("\\b");
                        continue block9;
                    }
                    case '\t': {
                        sb.append("\\t");
                        continue block9;
                    }
                    case '\n': {
                        sb.append("\\n");
                        continue block9;
                    }
                    case '\f': {
                        sb.append("\\f");
                        continue block9;
                    }
                    case '\r': {
                        sb.append("\\r");
                        continue block9;
                    }
                    default: {
                        if (c < ' ' || c >= '\u0080' && c < '\u00a0' || c >= '\u2000' && c < '\u2100') {
                            String t = "000" + Integer.toHexString(c);
                            sb.append("\\u").append(t.substring(t.length() - 4));
                            continue block9;
                        }
                        sb.append(c);
                    }
                }
            }
            sb.append('\"');
            return sb.toString();
        }

        @Override
        public String toString() {
            try {
                return '[' + this.join(",") + ']';
            }
            catch (Exception e) {
                return "";
            }
        }

        private static class Tokenizer {
            private final String source;
            private int index = 0;

            Tokenizer(String s) {
                this.source = s;
            }

            private void back() {
                if (this.index > 0) {
                    --this.index;
                }
            }

            private boolean more() {
                return this.index < this.source.length();
            }

            private char next() {
                if (this.more()) {
                    char c = this.source.charAt(this.index);
                    ++this.index;
                    return c;
                }
                return '\u0000';
            }

            private String next(int n) {
                int i = this.index;
                int j = i + n;
                if (j >= this.source.length()) {
                    throw new IllegalStateException("Substring bounds error");
                }
                this.index += n;
                return this.source.substring(i, j);
            }

            private char nextClean() {
                char c;
                block4: while (true) {
                    if ((c = this.next()) == '/') {
                        switch (this.next()) {
                            case '/': {
                                while ((c = this.next()) != '\n' && c != '\r' && c != '\u0000') {
                                }
                                continue block4;
                            }
                            case '*': {
                                while (true) {
                                    if ((c = this.next()) == '\u0000') {
                                        throw new IllegalStateException("Unclosed comment");
                                    }
                                    if (c != '*') continue;
                                    if (this.next() == '/') continue block4;
                                    this.back();
                                }
                            }
                            default: {
                                this.back();
                                return '/';
                            }
                        }
                    }
                    if (c == '#') {
                        while ((c = this.next()) != '\n' && c != '\r' && c != '\u0000') {
                        }
                        continue;
                    }
                    if (c == '\u0000' || c > ' ') break;
                }
                return c;
            }

            private String nextString(char quote) {
                StringBuffer sb = new StringBuffer();
                block13: while (true) {
                    char c = this.next();
                    switch (c) {
                        case '\u0000': 
                        case '\n': 
                        case '\r': {
                            throw new IllegalStateException("Unterminated string");
                        }
                        case '\\': {
                            c = this.next();
                            switch (c) {
                                case 'b': {
                                    sb.append('\b');
                                    continue block13;
                                }
                                case 't': {
                                    sb.append('\t');
                                    continue block13;
                                }
                                case 'n': {
                                    sb.append('\n');
                                    continue block13;
                                }
                                case 'f': {
                                    sb.append('\f');
                                    continue block13;
                                }
                                case 'r': {
                                    sb.append('\r');
                                    continue block13;
                                }
                                case 'u': {
                                    sb.append((char)Integer.parseInt(this.next(4), 16));
                                    continue block13;
                                }
                                case 'x': {
                                    sb.append((char)Integer.parseInt(this.next(2), 16));
                                    continue block13;
                                }
                            }
                            sb.append(c);
                            continue block13;
                        }
                    }
                    if (c == quote) {
                        return sb.toString();
                    }
                    sb.append(c);
                }
            }

            private String nextValue() {
                char c = this.nextClean();
                switch (c) {
                    case '\"': 
                    case '\'': {
                        return this.nextString(c);
                    }
                }
                StringBuffer sb = new StringBuffer();
                while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) <= 0) {
                    sb.append(c);
                    c = this.next();
                }
                this.back();
                String s = sb.toString().trim();
                if (s.equals("")) {
                    throw new IllegalStateException("Missing value" + this.toString());
                }
                if (s.equalsIgnoreCase("null")) {
                    return null;
                }
                return s;
            }

            void syntaxError(String message) {
                throw new IllegalStateException(message + this.toString());
            }

            public String toString() {
                return " at character " + this.index + " of " + this.source;
            }
        }
    }
}

