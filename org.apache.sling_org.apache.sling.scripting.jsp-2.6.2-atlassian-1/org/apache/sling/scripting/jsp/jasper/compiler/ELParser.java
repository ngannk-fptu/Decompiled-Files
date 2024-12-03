/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import org.apache.sling.scripting.jsp.jasper.compiler.ELNode;

public class ELParser {
    private Token curToken;
    private ELNode.Nodes expr;
    private ELNode.Nodes ELexpr;
    private int index = 0;
    private String expression;
    private char type;
    private boolean escapeBS;
    private static final String[] reservedWords = new String[]{"and", "div", "empty", "eq", "false", "ge", "gt", "instanceof", "le", "lt", "mod", "ne", "not", "null", "or", "true"};

    public ELParser(String expression) {
        this.expression = expression;
        this.expr = new ELNode.Nodes();
    }

    public static ELNode.Nodes parse(String expression) {
        ELParser parser = new ELParser(expression);
        while (parser.hasNextChar()) {
            ELNode.Nodes elexpr;
            String text = parser.skipUntilEL();
            if (text.length() > 0) {
                parser.expr.add(new ELNode.Text(text));
            }
            if ((elexpr = parser.parseEL()).isEmpty()) continue;
            parser.expr.add(new ELNode.Root(elexpr, parser.type));
        }
        return parser.expr;
    }

    private ELNode.Nodes parseEL() {
        StringBuffer buf = new StringBuffer();
        this.ELexpr = new ELNode.Nodes();
        while (this.hasNext()) {
            this.curToken = this.nextToken();
            if (this.curToken instanceof Char) {
                if (this.curToken.toChar() == '}') break;
                buf.append(this.curToken.toChar());
                continue;
            }
            if (buf.length() > 0) {
                this.ELexpr.add(new ELNode.ELText(buf.toString()));
            }
            if (this.parseFunction()) continue;
            this.ELexpr.add(new ELNode.ELText(this.curToken.toString()));
        }
        if (buf.length() > 0) {
            this.ELexpr.add(new ELNode.ELText(buf.toString()));
        }
        return this.ELexpr;
    }

    private boolean parseFunction() {
        if (!(this.curToken instanceof Id) || this.isELReserved(this.curToken.toString())) {
            return false;
        }
        String s1 = null;
        String s2 = this.curToken.toString();
        int mark = this.getIndex();
        if (this.hasNext()) {
            Token t2;
            Token t = this.nextToken();
            if (t.toChar() == ':' && this.hasNext() && (t2 = this.nextToken()) instanceof Id) {
                s1 = s2;
                s2 = t2.toString();
                if (this.hasNext()) {
                    t = this.nextToken();
                }
            }
            if (t.toChar() == '(') {
                this.ELexpr.add(new ELNode.Function(s1, s2));
                return true;
            }
        }
        this.setIndex(mark);
        return false;
    }

    private boolean isELReserved(String id) {
        int i = 0;
        int j = reservedWords.length;
        while (i < j) {
            int k = (i + j) / 2;
            int result = reservedWords[k].compareTo(id);
            if (result == 0) {
                return true;
            }
            if (result < 0) {
                i = k + 1;
                continue;
            }
            j = k;
        }
        return false;
    }

    private String skipUntilEL() {
        int prev = 0;
        StringBuffer buf = new StringBuffer();
        while (this.hasNextChar()) {
            char c = this.nextChar();
            if (prev == 92) {
                prev = 0;
                if (c == '\\') {
                    buf.append('\\');
                    if (!this.escapeBS) {
                        prev = 92;
                    }
                } else if (c == '$' || c == '#') {
                    buf.append(c);
                }
            } else if (prev == 36 || prev == 35) {
                if (c == '{') {
                    this.type = prev;
                    prev = 0;
                    break;
                }
                buf.append((char)prev);
                prev = 0;
            }
            if (c == '\\' || c == '$' || c == '#') {
                prev = c;
                continue;
            }
            buf.append(c);
        }
        if (prev != 0) {
            buf.append((char)prev);
        }
        return buf.toString();
    }

    private boolean hasNext() {
        this.skipSpaces();
        return this.hasNextChar();
    }

    private Token nextToken() {
        this.skipSpaces();
        if (this.hasNextChar()) {
            char ch = this.nextChar();
            if (Character.isJavaIdentifierStart(ch)) {
                StringBuffer buf = new StringBuffer();
                buf.append(ch);
                while ((ch = this.peekChar()) != '\uffffffff' && Character.isJavaIdentifierPart(ch)) {
                    buf.append(ch);
                    this.nextChar();
                }
                return new Id(buf.toString());
            }
            if (ch == '\'' || ch == '\"') {
                return this.parseQuotedChars(ch);
            }
            return new Char(ch);
        }
        return null;
    }

    private Token parseQuotedChars(char quote) {
        StringBuffer buf = new StringBuffer();
        buf.append(quote);
        while (this.hasNextChar()) {
            char ch = this.nextChar();
            if (ch == '\\') {
                ch = this.nextChar();
                if (ch != '\\' && ch != quote) continue;
                buf.append(ch);
                continue;
            }
            if (ch == quote) {
                buf.append(ch);
                break;
            }
            buf.append(ch);
        }
        return new QuotedString(buf.toString());
    }

    private void skipSpaces() {
        while (this.hasNextChar() && this.expression.charAt(this.index) <= ' ') {
            ++this.index;
        }
    }

    private boolean hasNextChar() {
        return this.index < this.expression.length();
    }

    private char nextChar() {
        if (this.index >= this.expression.length()) {
            return '\uffff';
        }
        return this.expression.charAt(this.index++);
    }

    private char peekChar() {
        if (this.index >= this.expression.length()) {
            return '\uffff';
        }
        return this.expression.charAt(this.index);
    }

    private int getIndex() {
        return this.index;
    }

    private void setIndex(int i) {
        this.index = i;
    }

    public char getType() {
        return this.type;
    }

    private static class QuotedString
    extends Token {
        private String value;

        QuotedString(String v) {
            this.value = v;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    private static class Char
    extends Token {
        private char ch;

        Char(char ch) {
            this.ch = ch;
        }

        @Override
        char toChar() {
            return this.ch;
        }

        @Override
        public String toString() {
            return new Character(this.ch).toString();
        }
    }

    private static class Id
    extends Token {
        String id;

        Id(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }

    private static class Token {
        private Token() {
        }

        char toChar() {
            return '\u0000';
        }

        public String toString() {
            return "";
        }
    }
}

