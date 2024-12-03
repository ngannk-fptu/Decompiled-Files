/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.ELNode;
import org.apache.jasper.compiler.Localizer;

public class ELParser {
    private Token curToken;
    private Token prevToken;
    private String whiteSpace = "";
    private final ELNode.Nodes expr;
    private ELNode.Nodes ELexpr;
    private int index = 0;
    private final String expression;
    private char type;
    private final boolean isDeferredSyntaxAllowedAsLiteral;
    private static final String[] reservedWords = new String[]{"and", "div", "empty", "eq", "false", "ge", "gt", "instanceof", "le", "lt", "mod", "ne", "not", "null", "or", "true"};

    public ELParser(String expression, boolean isDeferredSyntaxAllowedAsLiteral) {
        this.expression = expression;
        this.isDeferredSyntaxAllowedAsLiteral = isDeferredSyntaxAllowedAsLiteral;
        this.expr = new ELNode.Nodes();
    }

    public static ELNode.Nodes parse(String expression, boolean isDeferredSyntaxAllowedAsLiteral) {
        ELParser parser = new ELParser(expression, isDeferredSyntaxAllowedAsLiteral);
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
        StringBuilder buf = new StringBuilder();
        this.ELexpr = new ELNode.Nodes();
        this.curToken = null;
        this.prevToken = null;
        int openBraces = 0;
        while (this.hasNext()) {
            this.curToken = this.nextToken();
            if (this.curToken instanceof Char) {
                if (this.curToken.toChar() == '}') {
                    if (--openBraces < 0) {
                        break;
                    }
                } else if (this.curToken.toChar() == '{') {
                    ++openBraces;
                }
                buf.append(this.curToken.toString());
                continue;
            }
            if (buf.length() > 0) {
                this.ELexpr.add(new ELNode.ELText(buf.toString()));
                buf.setLength(0);
            }
            if (this.parseFunction()) continue;
            this.ELexpr.add(new ELNode.ELText(this.curToken.toString()));
        }
        if (this.curToken != null) {
            buf.append(this.curToken.getWhiteSpace());
        }
        if (buf.length() > 0) {
            this.ELexpr.add(new ELNode.ELText(buf.toString()));
        }
        return this.ELexpr;
    }

    private boolean parseFunction() {
        if (!(this.curToken instanceof Id) || this.isELReserved(this.curToken.toTrimmedString()) || this.prevToken instanceof Char && this.prevToken.toChar() == '.') {
            return false;
        }
        String s1 = null;
        String s2 = this.curToken.toTrimmedString();
        int start = this.index - this.curToken.toString().length();
        Token original = this.curToken;
        if (this.hasNext()) {
            Token t2;
            int mark = this.getIndex() - this.whiteSpace.length();
            this.curToken = this.nextToken();
            if (this.curToken.toChar() == ':' && this.hasNext() && (t2 = this.nextToken()) instanceof Id) {
                s1 = s2;
                s2 = t2.toTrimmedString();
                if (this.hasNext()) {
                    this.curToken = this.nextToken();
                }
            }
            if (this.curToken.toChar() == '(') {
                this.ELexpr.add(new ELNode.Function(s1, s2, this.expression.substring(start, this.index - 1)));
                return true;
            }
            this.curToken = original;
            this.setIndex(mark);
        }
        return false;
    }

    private boolean isELReserved(String id) {
        int i = 0;
        int j = reservedWords.length;
        while (i < j) {
            int k = i + j >>> 1;
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
        StringBuilder buf = new StringBuilder();
        while (this.hasNextChar()) {
            char ch = this.nextChar();
            if (ch == '\\') {
                char p0 = this.peek(0);
                if (p0 == '$' || p0 == '#' && !this.isDeferredSyntaxAllowedAsLiteral) {
                    buf.append(this.nextChar());
                    continue;
                }
                buf.append(ch);
                continue;
            }
            if ((ch == '$' || ch == '#' && !this.isDeferredSyntaxAllowedAsLiteral) && this.peek(0) == '{') {
                this.type = ch;
                this.nextChar();
                break;
            }
            buf.append(ch);
        }
        return buf.toString();
    }

    static String escapeLiteralExpression(String input, boolean isDeferredSyntaxAllowedAsLiteral) {
        int len = input.length();
        int lastAppend = 0;
        StringBuilder output = null;
        for (int i = 0; i < len; ++i) {
            char ch = input.charAt(i);
            if (ch != '$' && (isDeferredSyntaxAllowedAsLiteral || ch != '#') || i + 1 >= len || input.charAt(i + 1) != '{') continue;
            if (output == null) {
                output = new StringBuilder(len + 20);
            }
            output.append(input.substring(lastAppend, i));
            lastAppend = i + 1;
            output.append('\\');
            output.append(ch);
        }
        if (output == null) {
            return input;
        }
        output.append(input.substring(lastAppend, len));
        return output.toString();
    }

    private static String escapeELText(String input) {
        int len = input.length();
        char quote = '\u0000';
        int lastAppend = 0;
        int start = 0;
        int end = len;
        String trimmed = input.trim();
        int trimmedLen = trimmed.length();
        if (trimmedLen > 1) {
            quote = trimmed.charAt(0);
            if (quote == '\'' || quote == '\"') {
                if (trimmed.charAt(trimmedLen - 1) != quote) {
                    throw new IllegalArgumentException(Localizer.getMessage("org.apache.jasper.compiler.ELParser.invalidQuotesForStringLiteral", input));
                }
                start = input.indexOf(quote) + 1;
                end = start + trimmedLen - 2;
            } else {
                quote = '\u0000';
            }
        }
        StringBuilder output = null;
        for (int i = start; i < end; ++i) {
            char ch = input.charAt(i);
            if (ch != '\\' && ch != quote) continue;
            if (output == null) {
                output = new StringBuilder(len + 20);
            }
            output.append(input.substring(lastAppend, i));
            lastAppend = i + 1;
            output.append('\\');
            output.append(ch);
        }
        if (output == null) {
            return input;
        }
        output.append(input.substring(lastAppend, len));
        return output.toString();
    }

    private boolean hasNext() {
        this.skipSpaces();
        return this.hasNextChar();
    }

    private String getAndResetWhiteSpace() {
        String result = this.whiteSpace;
        this.whiteSpace = "";
        return result;
    }

    private Token nextToken() {
        this.prevToken = this.curToken;
        if (this.hasNextChar()) {
            char ch = this.nextChar();
            if (Character.isJavaIdentifierStart(ch)) {
                int start = this.index - 1;
                while (this.index < this.expression.length() && Character.isJavaIdentifierPart(ch = this.expression.charAt(this.index))) {
                    this.nextChar();
                }
                return new Id(this.getAndResetWhiteSpace(), this.expression.substring(start, this.index));
            }
            if (ch == '\'' || ch == '\"') {
                return this.parseQuotedChars(ch);
            }
            return new Char(this.getAndResetWhiteSpace(), ch);
        }
        return null;
    }

    private Token parseQuotedChars(char quote) {
        StringBuilder buf = new StringBuilder();
        buf.append(quote);
        while (this.hasNextChar()) {
            char ch = this.nextChar();
            if (ch == '\\') {
                ch = this.nextChar();
                if (ch == '\\' || ch == '\'' || ch == '\"') {
                    buf.append(ch);
                    continue;
                }
                throw new IllegalArgumentException(Localizer.getMessage("org.apache.jasper.compiler.ELParser.invalidQuoting", this.expression));
            }
            if (ch == quote) {
                buf.append(ch);
                break;
            }
            buf.append(ch);
        }
        return new QuotedString(this.getAndResetWhiteSpace(), buf.toString());
    }

    private void skipSpaces() {
        char c;
        int start = this.index;
        while (this.hasNextChar() && (c = this.expression.charAt(this.index)) <= ' ') {
            ++this.index;
        }
        this.whiteSpace = this.expression.substring(start, this.index);
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

    private char peek(int advance) {
        int target = this.index + advance;
        if (target >= this.expression.length()) {
            return '\uffff';
        }
        return this.expression.charAt(target);
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

    private static class Token {
        protected final String whiteSpace;

        Token(String whiteSpace) {
            this.whiteSpace = whiteSpace;
        }

        char toChar() {
            return '\u0000';
        }

        public String toString() {
            return this.whiteSpace;
        }

        String toTrimmedString() {
            return "";
        }

        String getWhiteSpace() {
            return this.whiteSpace;
        }
    }

    private static class Char
    extends Token {
        private char ch;

        Char(String whiteSpace, char ch) {
            super(whiteSpace);
            this.ch = ch;
        }

        @Override
        char toChar() {
            return this.ch;
        }

        @Override
        public String toString() {
            return this.whiteSpace + this.ch;
        }

        @Override
        String toTrimmedString() {
            return "" + this.ch;
        }
    }

    private static class Id
    extends Token {
        String id;

        Id(String whiteSpace, String id) {
            super(whiteSpace);
            this.id = id;
        }

        @Override
        public String toString() {
            return this.whiteSpace + this.id;
        }

        @Override
        String toTrimmedString() {
            return this.id;
        }
    }

    private static class QuotedString
    extends Token {
        private String value;

        QuotedString(String whiteSpace, String v) {
            super(whiteSpace);
            this.value = v;
        }

        @Override
        public String toString() {
            return this.whiteSpace + this.value;
        }

        @Override
        String toTrimmedString() {
            return this.value;
        }
    }

    static class TextBuilder
    extends ELNode.Visitor {
        protected final boolean isDeferredSyntaxAllowedAsLiteral;
        protected final StringBuilder output = new StringBuilder();

        protected TextBuilder(boolean isDeferredSyntaxAllowedAsLiteral) {
            this.isDeferredSyntaxAllowedAsLiteral = isDeferredSyntaxAllowedAsLiteral;
        }

        public String getText() {
            return this.output.toString();
        }

        @Override
        public void visit(ELNode.Root n) throws JasperException {
            this.output.append(n.getType());
            this.output.append('{');
            n.getExpression().visit(this);
            this.output.append('}');
        }

        @Override
        public void visit(ELNode.Function n) throws JasperException {
            this.output.append(ELParser.escapeLiteralExpression(n.getOriginalText(), this.isDeferredSyntaxAllowedAsLiteral));
            this.output.append('(');
        }

        @Override
        public void visit(ELNode.Text n) throws JasperException {
            this.output.append(ELParser.escapeLiteralExpression(n.getText(), this.isDeferredSyntaxAllowedAsLiteral));
        }

        @Override
        public void visit(ELNode.ELText n) throws JasperException {
            this.output.append(ELParser.escapeELText(n.getText()));
        }
    }
}

