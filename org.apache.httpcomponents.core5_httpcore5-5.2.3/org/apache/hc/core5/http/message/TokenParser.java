/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.message;

import java.util.BitSet;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.message.ParserCursor;
import org.apache.hc.core5.util.Tokenizer;

@Deprecated
@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class TokenParser
extends Tokenizer {
    public static final TokenParser INSTANCE = new TokenParser();
    public static final char DQUOTE = '\"';
    public static final char ESCAPE = '\\';

    public String parseToken(CharSequence buf, ParserCursor cursor, BitSet delimiters) {
        return super.parseToken(buf, cursor, delimiters);
    }

    public String parseValue(CharSequence buf, ParserCursor cursor, BitSet delimiters) {
        return super.parseValue(buf, cursor, delimiters);
    }

    public void skipWhiteSpace(CharSequence buf, ParserCursor cursor) {
        super.skipWhiteSpace(buf, cursor);
    }

    public void copyContent(CharSequence buf, ParserCursor cursor, BitSet delimiters, StringBuilder dst) {
        super.copyContent(buf, cursor, delimiters, dst);
    }

    @Override
    public void copyContent(CharSequence buf, Tokenizer.Cursor cursor, BitSet delimiters, StringBuilder dst) {
        ParserCursor parserCursor = new ParserCursor(cursor.getLowerBound(), cursor.getUpperBound());
        parserCursor.updatePos(cursor.getPos());
        this.copyContent(buf, parserCursor, delimiters, dst);
        cursor.updatePos(parserCursor.getPos());
    }

    public void copyUnquotedContent(CharSequence buf, ParserCursor cursor, BitSet delimiters, StringBuilder dst) {
        super.copyUnquotedContent(buf, cursor, delimiters, dst);
    }

    @Override
    public void copyUnquotedContent(CharSequence buf, Tokenizer.Cursor cursor, BitSet delimiters, StringBuilder dst) {
        ParserCursor parserCursor = new ParserCursor(cursor.getLowerBound(), cursor.getUpperBound());
        parserCursor.updatePos(cursor.getPos());
        this.copyUnquotedContent(buf, parserCursor, delimiters, dst);
        cursor.updatePos(parserCursor.getPos());
    }

    public void copyQuotedContent(CharSequence buf, ParserCursor cursor, StringBuilder dst) {
        super.copyQuotedContent(buf, cursor, dst);
    }

    @Override
    public void copyQuotedContent(CharSequence buf, Tokenizer.Cursor cursor, StringBuilder dst) {
        ParserCursor parserCursor = new ParserCursor(cursor.getLowerBound(), cursor.getUpperBound());
        parserCursor.updatePos(cursor.getPos());
        this.copyQuotedContent(buf, parserCursor, dst);
        cursor.updatePos(parserCursor.getPos());
    }
}

