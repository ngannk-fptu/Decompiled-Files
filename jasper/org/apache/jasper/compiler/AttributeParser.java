/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import org.apache.jasper.compiler.Localizer;

public class AttributeParser {
    private final String input;
    private final char quote;
    private final boolean isELIgnored;
    private final boolean isDeferredSyntaxAllowedAsLiteral;
    private final boolean strict;
    private final boolean quoteAttributeEL;
    private final char type;
    private final int size;
    private int i = 0;
    private boolean lastChEscaped = false;
    private final StringBuilder result;

    public static String getUnquoted(String input, char quote, boolean isELIgnored, boolean isDeferredSyntaxAllowedAsLiteral, boolean strict, boolean quoteAttributeEL) {
        return new AttributeParser(input, quote, isELIgnored, isDeferredSyntaxAllowedAsLiteral, strict, quoteAttributeEL).getUnquoted();
    }

    private AttributeParser(String input, char quote, boolean isELIgnored, boolean isDeferredSyntaxAllowedAsLiteral, boolean strict, boolean quoteAttributeEL) {
        this.input = input;
        this.quote = quote;
        this.isELIgnored = isELIgnored;
        this.isDeferredSyntaxAllowedAsLiteral = isDeferredSyntaxAllowedAsLiteral;
        this.strict = strict;
        this.quoteAttributeEL = quoteAttributeEL;
        this.type = this.getType(input);
        this.size = input.length();
        this.result = new StringBuilder(this.size);
    }

    private String getUnquoted() {
        while (this.i < this.size) {
            this.parseLiteral();
            this.parseEL();
        }
        return this.result.toString();
    }

    private void parseLiteral() {
        boolean foundEL = false;
        while (this.i < this.size && !foundEL) {
            char ch = this.nextChar();
            if (!this.isELIgnored && ch == '\\') {
                if (this.type == '\u0000') {
                    this.result.append("\\");
                    continue;
                }
                this.result.append(this.type);
                this.result.append("{'\\\\'}");
                continue;
            }
            if (!this.isELIgnored && ch == '$' && this.lastChEscaped) {
                if (this.type == '\u0000') {
                    this.result.append("\\$");
                    continue;
                }
                this.result.append(this.type);
                this.result.append("{'$'}");
                continue;
            }
            if (!this.isELIgnored && ch == '#' && this.lastChEscaped) {
                if (this.type == '\u0000') {
                    this.result.append("\\#");
                    continue;
                }
                this.result.append(this.type);
                this.result.append("{'#'}");
                continue;
            }
            if (ch == this.type) {
                if (this.i < this.size) {
                    char next = this.input.charAt(this.i);
                    if (next == '{') {
                        foundEL = true;
                        --this.i;
                        continue;
                    }
                    this.result.append(ch);
                    continue;
                }
                this.result.append(ch);
                continue;
            }
            this.result.append(ch);
        }
    }

    private void parseEL() {
        boolean endEL = false;
        boolean insideLiteral = false;
        char literalQuote = '\u0000';
        while (this.i < this.size && !endEL) {
            char ch = this.quoteAttributeEL ? this.nextChar() : this.input.charAt(this.i++);
            if (ch == '\'' || ch == '\"') {
                if (insideLiteral) {
                    if (literalQuote == ch) {
                        insideLiteral = false;
                    }
                } else {
                    insideLiteral = true;
                    literalQuote = ch;
                }
                this.result.append(ch);
                continue;
            }
            if (ch == '\\') {
                this.result.append(ch);
                if (!insideLiteral || this.size >= this.i) continue;
                ch = this.quoteAttributeEL ? this.nextChar() : this.input.charAt(this.i++);
                this.result.append(ch);
                continue;
            }
            if (ch == '}') {
                if (!insideLiteral) {
                    endEL = true;
                }
                this.result.append(ch);
                continue;
            }
            this.result.append(ch);
        }
    }

    private char nextChar() {
        this.lastChEscaped = false;
        char ch = this.input.charAt(this.i);
        if (ch == '&') {
            if (this.i + 5 < this.size && this.input.charAt(this.i + 1) == 'a' && this.input.charAt(this.i + 2) == 'p' && this.input.charAt(this.i + 3) == 'o' && this.input.charAt(this.i + 4) == 's' && this.input.charAt(this.i + 5) == ';') {
                ch = '\'';
                this.i += 6;
            } else if (this.i + 5 < this.size && this.input.charAt(this.i + 1) == 'q' && this.input.charAt(this.i + 2) == 'u' && this.input.charAt(this.i + 3) == 'o' && this.input.charAt(this.i + 4) == 't' && this.input.charAt(this.i + 5) == ';') {
                ch = '\"';
                this.i += 6;
            } else {
                ++this.i;
            }
        } else if (ch == '\\' && this.i + 1 < this.size) {
            ch = this.input.charAt(this.i + 1);
            if (ch == '\\' || ch == '\"' || ch == '\'' || !this.isELIgnored && (ch == '$' || !this.isDeferredSyntaxAllowedAsLiteral && ch == '#')) {
                this.i += 2;
                this.lastChEscaped = true;
            } else {
                ch = '\\';
                ++this.i;
            }
        } else {
            if (ch == '<' && this.i + 2 < this.size && this.input.charAt(this.i + 1) == '\\' && this.input.charAt(this.i + 2) == '%') {
                this.result.append('<');
                this.i += 3;
                return '%';
            }
            if (ch == '%' && this.i + 2 < this.size && this.input.charAt(this.i + 1) == '\\' && this.input.charAt(this.i + 2) == '>') {
                this.result.append('%');
                this.i += 3;
                return '>';
            }
            if (ch == this.quote && this.strict) {
                String msg = Localizer.getMessage("jsp.error.attribute.noescape", this.input, "" + this.quote);
                throw new IllegalArgumentException(msg);
            }
            ++this.i;
        }
        return (char)ch;
    }

    private char getType(String value) {
        if (value == null) {
            return '\u0000';
        }
        if (this.isELIgnored) {
            return '\u0000';
        }
        int len = value.length();
        for (int j = 0; j < len; ++j) {
            char current = value.charAt(j);
            if (current == '\\') {
                ++j;
                continue;
            }
            if (current == '#' && !this.isDeferredSyntaxAllowedAsLiteral) {
                if (j >= len - 1 || value.charAt(j + 1) != '{') continue;
                return '#';
            }
            if (current != '$' || j >= len - 1 || value.charAt(j + 1) != '{') continue;
            return '$';
        }
        return '\u0000';
    }
}

