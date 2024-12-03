/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.tokenizer;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.Text;
import com.opensymphony.module.sitemesh.html.tokenizer.Lexer;
import com.opensymphony.module.sitemesh.html.tokenizer.TokenHandler;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.util.CharArrayReader;
import java.io.IOException;

public class Parser
extends Lexer {
    private final CharArray attributeBuffer = new CharArray(64);
    private final ReusableToken reusableToken = new ReusableToken();
    private int pushbackToken = -1;
    private String pushbackText;
    public static final short SLASH = 257;
    public static final short WHITESPACE = 258;
    public static final short EQUALS = 259;
    public static final short QUOTE = 260;
    public static final short WORD = 261;
    public static final short TEXT = 262;
    public static final short QUOTED = 263;
    public static final short LT = 264;
    public static final short GT = 265;
    public static final short LT_OPEN_MAGIC_COMMENT = 266;
    public static final short LT_CLOSE_MAGIC_COMMENT = 267;
    private final char[] input;
    private TokenHandler handler;
    private int position;
    private int length;
    private String name;
    private int type;

    public Parser(char[] input, int length, TokenHandler handler) {
        super(new CharArrayReader(input, 0, length));
        this.input = input;
        this.handler = handler;
    }

    private String text() {
        if (this.pushbackToken == -1) {
            return this.yytext();
        }
        return this.pushbackText;
    }

    private void skipWhiteSpace() throws IOException {
        int next;
        do {
            if (this.pushbackToken == -1) {
                next = this.yylex();
                continue;
            }
            next = this.pushbackToken;
            this.pushbackToken = -1;
        } while (next == 258);
        this.pushBack(next);
    }

    private void pushBack(int next) {
        if (this.pushbackToken != -1) {
            this.reportError("Cannot pushback more than once", this.line(), this.column());
        }
        this.pushbackToken = next;
        this.pushbackText = next == 261 || next == 263 || next == 257 || next == 259 ? this.yytext() : null;
    }

    public void start() {
        try {
            while (true) {
                int token;
                if (this.pushbackToken == -1) {
                    token = this.yylex();
                } else {
                    token = this.pushbackToken;
                    this.pushbackToken = -1;
                }
                if (token == 0) {
                    return;
                }
                if (token == 262) {
                    this.parsedText(this.position(), this.length());
                    continue;
                }
                if (token == 264) {
                    this.parseTag(1);
                    continue;
                }
                if (token == 266) {
                    this.parseTag(4);
                    continue;
                }
                if (token == 267) {
                    this.parseTag(5);
                    continue;
                }
                this.reportError("Unexpected token from lexer, was expecting TEXT or LT", this.line(), this.column());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseTag(int type) throws IOException {
        int token;
        int start = this.position();
        this.skipWhiteSpace();
        if (this.pushbackToken == -1) {
            token = this.yylex();
        } else {
            token = this.pushbackToken;
            this.pushbackToken = -1;
        }
        if (token == 257) {
            type = 2;
            if (this.pushbackToken == -1) {
                token = this.yylex();
            } else {
                token = this.pushbackToken;
                this.pushbackToken = -1;
            }
        }
        if (token == 261) {
            String name = this.text();
            if (this.handler.shouldProcessTag(name)) {
                this.parseFullTag(type, name, start);
            } else {
                this.resetLexerState();
                this.pushBack(this.yylex());
                this.parsedText(start, this.position() - start);
            }
        } else if (token != 265) {
            if (token == 0) {
                this.parsedText(start, this.position() - start);
            } else {
                this.reportError("Could not recognise tag", this.line(), this.column());
            }
        }
    }

    private void parseFullTag(int type, String name, int start) throws IOException {
        int token;
        block13: {
            while (true) {
                this.skipWhiteSpace();
                if (this.pushbackToken == -1) {
                    token = this.yylex();
                } else {
                    token = this.pushbackToken;
                    this.pushbackToken = -1;
                }
                this.pushBack(token);
                if (token == 257 || token == 265) break block13;
                if (token != 261) break;
                this.parseAttribute();
            }
            if (token == 0) {
                this.parsedText(start, this.position() - start);
                return;
            }
            this.reportError("Illegal tag", this.line(), this.column());
        }
        if (this.pushbackToken == -1) {
            token = this.yylex();
        } else {
            token = this.pushbackToken;
            this.pushbackToken = -1;
        }
        if (token == 257) {
            type = 3;
            if (this.pushbackToken == -1) {
                token = this.yylex();
            } else {
                token = this.pushbackToken;
                this.pushbackToken = -1;
            }
        }
        if (token == 265) {
            this.parsedTag(type, name, start, this.position() - start + 1);
        } else if (token == 0) {
            this.parsedText(start, this.position() - start);
        } else {
            this.reportError("Expected end of tag", this.line(), this.column());
            this.parsedTag(type, name, start, this.position() - start + 1);
        }
    }

    private void parseAttribute() throws IOException {
        int token;
        if (this.pushbackToken == -1) {
            token = this.yylex();
        } else {
            token = this.pushbackToken;
            this.pushbackToken = -1;
        }
        String attributeName = this.text();
        this.skipWhiteSpace();
        if (this.pushbackToken == -1) {
            token = this.yylex();
        } else {
            token = this.pushbackToken;
            this.pushbackToken = -1;
        }
        if (token == 259) {
            this.skipWhiteSpace();
            if (this.pushbackToken == -1) {
                token = this.yylex();
            } else {
                token = this.pushbackToken;
                this.pushbackToken = -1;
            }
            if (token == 263) {
                this.parsedAttribute(attributeName, this.text(), true);
            } else if (token == 261 || token == 257) {
                int next;
                this.attributeBuffer.clear();
                this.attributeBuffer.append(this.text());
                while (true) {
                    if (this.pushbackToken == -1) {
                        next = this.yylex();
                    } else {
                        next = this.pushbackToken;
                        this.pushbackToken = -1;
                    }
                    if (next != 261 && next != 259 && next != 257) break;
                    this.attributeBuffer.append(this.text());
                }
                this.pushBack(next);
                this.parsedAttribute(attributeName, this.attributeBuffer.toString(), false);
            } else if (token == 257 || token == 265) {
                this.pushBack(token);
            } else {
                if (token == 0) {
                    return;
                }
                this.reportError("Illegal attribute value", this.line(), this.column());
            }
        } else if (token == 257 || token == 265 || token == 261) {
            this.parsedAttribute(attributeName, null, false);
            this.pushBack(token);
        } else {
            if (token == 0) {
                return;
            }
            this.reportError("Illegal attribute name", this.line(), this.column());
        }
    }

    protected void parsedText(int position, int length) {
        this.position = position;
        this.length = length;
        this.handler.text(this.reusableToken);
    }

    protected void parsedTag(int type, String name, int start, int length) {
        this.type = type;
        this.name = name;
        this.position = start;
        this.length = length;
        this.handler.tag(this.reusableToken);
        this.reusableToken.attributeCount = 0;
    }

    protected void parsedAttribute(String name, String value, boolean quoted) {
        if (this.reusableToken.attributeCount + 2 >= this.reusableToken.attributes.length) {
            String[] newAttributes = new String[this.reusableToken.attributeCount * 2];
            System.arraycopy(this.reusableToken.attributes, 0, newAttributes, 0, this.reusableToken.attributeCount);
            this.reusableToken.attributes = newAttributes;
        }
        this.reusableToken.attributes[this.reusableToken.attributeCount++] = name;
        this.reusableToken.attributes[this.reusableToken.attributeCount++] = quoted ? value.substring(1, value.length() - 1) : value;
    }

    protected void reportError(String message, int line, int column) {
        this.handler.warning(message, line, column);
    }

    public class ReusableToken
    implements Tag,
    Text {
        public int attributeCount = 0;
        public String[] attributes = new String[10];

        public String getName() {
            return Parser.this.name;
        }

        public int getType() {
            return Parser.this.type;
        }

        public String getContents() {
            return new String(Parser.this.input, Parser.this.position, Parser.this.length);
        }

        public void writeTo(SitemeshBufferFragment.Builder buffer, int position) {
            buffer.insert(position, SitemeshBufferFragment.builder().setBuffer(new DefaultSitemeshBuffer(Parser.this.input)).setStart(position).setLength(Parser.this.length).build());
        }

        public int getAttributeCount() {
            return this.attributeCount / 2;
        }

        public int getAttributeIndex(String name, boolean caseSensitive) {
            if (this.attributeCount == 0) {
                return -1;
            }
            int len = this.attributeCount;
            for (int i = 0; i < len; i += 2) {
                String current = this.attributes[i];
                if (!(caseSensitive ? name.equals(current) : name.equalsIgnoreCase(current))) continue;
                return i / 2;
            }
            return -1;
        }

        public String getAttributeName(int index) {
            return this.attributes[index * 2];
        }

        public String getAttributeValue(int index) {
            return this.attributes[index * 2 + 1];
        }

        public String getAttributeValue(String name, boolean caseSensitive) {
            if (this.attributeCount == 0) {
                return null;
            }
            int len = this.attributeCount;
            for (int i = 0; i < len; i += 2) {
                String current = this.attributes[i];
                if (!(caseSensitive ? name.equals(current) : name.equalsIgnoreCase(current))) continue;
                return this.attributes[i + 1];
            }
            return null;
        }

        public boolean hasAttribute(String name, boolean caseSensitive) {
            return this.getAttributeIndex(name, caseSensitive) > -1;
        }

        public int getPosition() {
            return Parser.this.position;
        }

        public int getLength() {
            return Parser.this.length;
        }
    }
}

