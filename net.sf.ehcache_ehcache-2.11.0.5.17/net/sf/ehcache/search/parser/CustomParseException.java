/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import net.sf.ehcache.search.parser.ParseException;
import net.sf.ehcache.search.parser.Token;

public class CustomParseException
extends ParseException {
    private static final long serialVersionUID = 5082041880401542754L;

    public CustomParseException(Token tok, String message) {
        super(CustomParseException.makeMessage(tok, message));
        this.currentToken = tok;
    }

    public CustomParseException(Token tok, Throwable t) {
        this(tok, t.getMessage());
    }

    public CustomParseException(ParseException pe) {
        super(pe.getMessage());
        this.currentToken = pe.currentToken;
        this.expectedTokenSequences = pe.expectedTokenSequences;
        this.tokenImage = pe.tokenImage;
    }

    public static String makeMessage(Token tok, String message) {
        if (tok != null) {
            int lineNum = tok.beginLine;
            int charPos = tok.beginColumn;
            return "Parse error at line " + lineNum + ", column " + charPos + ": " + message;
        }
        return "Parse error: " + message;
    }

    public static CustomParseException factory(Token tok, Throwable t) {
        if (t instanceof ParseException) {
            return new CustomParseException((ParseException)t);
        }
        return new CustomParseException(tok, t);
    }

    public static CustomParseException factory(Token tok, Message msg) {
        return new CustomParseException(tok, msg.getMessage() + (tok == null ? "" : tok.image));
    }

    public static enum Message {
        SINGLE_QUOTE("Error parsing quoted string: "),
        BOOLEAN_CAST("Error parsing boolean literal:"),
        BYTE_CAST("Error parsing byte literal:"),
        SHORT_LITERAL("Error parsing short literal:"),
        INT_LITERAL("Error parsing integer literal:"),
        FLOAT_LITERAL("Error parsing float literal:"),
        LONG_LITERAL("Error parsing long literal:"),
        DOUBLE_LITERAL("Error parsing double literal:"),
        DATE_LITERAL("Error parsing date literal:"),
        SQLDATE_LITERAL("Error parsing sqldate literal:"),
        STRING_LITERAL("Error parsing string literal:"),
        CLASS_LITERAL("Error parsing class literal:"),
        ENUM_LITERAL("Error parsing enum literal:"),
        MEMBER_LITERAL("Error parsing member literal:"),
        CHAR_LITERAL("Error parsing char literal");

        private String msg;

        private Message(String msg) {
            this.msg = msg;
        }

        public String getMessage() {
            return this.msg;
        }
    }
}

