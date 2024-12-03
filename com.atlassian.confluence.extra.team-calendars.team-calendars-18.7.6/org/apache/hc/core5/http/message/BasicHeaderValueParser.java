/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.message;

import java.util.ArrayList;
import java.util.BitSet;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicHeaderElement;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.message.HeaderValueParser;
import org.apache.hc.core5.http.message.ParserCursor;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Tokenizer;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class BasicHeaderValueParser
implements HeaderValueParser {
    public static final BasicHeaderValueParser INSTANCE = new BasicHeaderValueParser();
    private static final char PARAM_DELIMITER = ';';
    private static final char ELEM_DELIMITER = ',';
    private static final BitSet TOKEN_DELIMITER = Tokenizer.INIT_BITSET(61, 59, 44);
    private static final BitSet VALUE_DELIMITER = Tokenizer.INIT_BITSET(59, 44);
    private final Tokenizer tokenizer = Tokenizer.INSTANCE;
    private static final HeaderElement[] EMPTY_HEADER_ELEMENT_ARRAY = new HeaderElement[0];
    private static final NameValuePair[] EMPTY_NAME_VALUE_ARRAY = new NameValuePair[0];

    @Override
    public HeaderElement[] parseElements(CharSequence buffer, ParserCursor cursor) {
        Args.notNull(buffer, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        ArrayList<HeaderElement> elements = new ArrayList<HeaderElement>();
        while (!cursor.atEnd()) {
            HeaderElement element = this.parseHeaderElement(buffer, cursor);
            if (element.getName().isEmpty() && element.getValue() == null) continue;
            elements.add(element);
        }
        return elements.toArray(EMPTY_HEADER_ELEMENT_ARRAY);
    }

    @Override
    public HeaderElement parseHeaderElement(CharSequence buffer, ParserCursor cursor) {
        char ch;
        Args.notNull(buffer, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        NameValuePair nvp = this.parseNameValuePair(buffer, cursor);
        NameValuePair[] params = null;
        if (!cursor.atEnd() && (ch = buffer.charAt(cursor.getPos() - 1)) != ',') {
            params = this.parseParameters(buffer, cursor);
        }
        return new BasicHeaderElement(nvp.getName(), nvp.getValue(), params);
    }

    @Override
    public NameValuePair[] parseParameters(CharSequence buffer, ParserCursor cursor) {
        Args.notNull(buffer, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        this.tokenizer.skipWhiteSpace(buffer, cursor);
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            NameValuePair param = this.parseNameValuePair(buffer, cursor);
            params.add(param);
            char ch = buffer.charAt(cursor.getPos() - 1);
            if (ch != ',') continue;
            break;
        }
        return params.toArray(EMPTY_NAME_VALUE_ARRAY);
    }

    @Override
    public NameValuePair parseNameValuePair(CharSequence buffer, ParserCursor cursor) {
        Args.notNull(buffer, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        String name = this.tokenizer.parseToken(buffer, cursor, TOKEN_DELIMITER);
        if (cursor.atEnd()) {
            return new BasicNameValuePair(name, null);
        }
        char delim = buffer.charAt(cursor.getPos());
        cursor.updatePos(cursor.getPos() + 1);
        if (delim != '=') {
            return new BasicNameValuePair(name, null);
        }
        String value = this.tokenizer.parseValue(buffer, cursor, VALUE_DELIMITER);
        if (!cursor.atEnd()) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        return new BasicNameValuePair(name, value);
    }
}

