/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.json;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.tomcat.util.json.JSONParserConstants;
import org.apache.tomcat.util.json.JSONParserTokenManager;
import org.apache.tomcat.util.json.JavaCharStream;
import org.apache.tomcat.util.json.ParseException;
import org.apache.tomcat.util.json.Token;

public class JSONParser
implements JSONParserConstants {
    private boolean nativeNumbers = false;
    public JSONParserTokenManager token_source;
    JavaCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_gen;
    private final int[] jj_la1 = new int[13];
    private static int[] jj_la1_0;
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int trace_indent = 0;
    private boolean trace_enabled;

    public JSONParser(String input) {
        this(new StringReader(input));
    }

    public LinkedHashMap<String, Object> parseObject() throws ParseException {
        LinkedHashMap<String, Object> toReturn = this.object();
        if (!this.ensureEOF()) {
            throw new IllegalStateException("Expected EOF, but still had content to parse");
        }
        return toReturn;
    }

    public ArrayList<Object> parseArray() throws ParseException {
        ArrayList<Object> toReturn = this.list();
        if (!this.ensureEOF()) {
            throw new IllegalStateException("Expected EOF, but still had content to parse");
        }
        return toReturn;
    }

    public Object parse() throws ParseException {
        Object toReturn = this.anything();
        if (!this.ensureEOF()) {
            throw new IllegalStateException("Expected EOF, but still had content to parse");
        }
        return toReturn;
    }

    private static String substringBefore(String str, char delim) {
        int pos = str.indexOf(delim);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public void setNativeNumbers(boolean value) {
        this.nativeNumbers = value;
    }

    public boolean getNativeNumbers() {
        return this.nativeNumbers;
    }

    public final boolean ensureEOF() throws ParseException {
        this.jj_consume_token(0);
        return true;
    }

    public final Object anything() throws ParseException {
        Object x;
        switch (this.jj_nt.kind) {
            case 7: {
                x = this.object();
                break;
            }
            case 10: {
                x = this.list();
                break;
            }
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 22: 
            case 23: 
            case 26: 
            case 27: {
                x = this.value();
                break;
            }
            default: {
                this.jj_la1[0] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return x;
    }

    public final String objectKey() throws ParseException {
        String key;
        switch (this.jj_nt.kind) {
            case 22: 
            case 23: 
            case 26: 
            case 27: {
                key = this.string();
                break;
            }
            case 28: {
                key = this.symbol();
                break;
            }
            case 19: {
                this.nullValue();
                key = null;
                break;
            }
            case 15: 
            case 16: 
            case 17: 
            case 18: {
                Serializable o;
                switch (this.jj_nt.kind) {
                    case 17: 
                    case 18: {
                        o = this.booleanValue();
                        break;
                    }
                    case 15: 
                    case 16: {
                        o = this.number();
                        break;
                    }
                    default: {
                        this.jj_la1[1] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                key = o.toString();
                break;
            }
            default: {
                this.jj_la1[2] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return key;
    }

    public final LinkedHashMap<String, Object> object() throws ParseException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        this.jj_consume_token(7);
        block0 : switch (this.jj_nt.kind) {
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 22: 
            case 23: 
            case 26: 
            case 27: 
            case 28: {
                String key = this.objectKey();
                this.jj_consume_token(9);
                Object value = this.anything();
                map.put(key, value);
                key = null;
                value = null;
                while (true) {
                    switch (this.jj_nt.kind) {
                        case 6: {
                            break;
                        }
                        default: {
                            this.jj_la1[3] = this.jj_gen;
                            break block0;
                        }
                    }
                    this.jj_consume_token(6);
                    key = this.objectKey();
                    this.jj_consume_token(9);
                    value = this.anything();
                    map.put(key, value);
                    key = null;
                    value = null;
                }
            }
            default: {
                this.jj_la1[4] = this.jj_gen;
            }
        }
        this.jj_consume_token(8);
        return map;
    }

    public final ArrayList<Object> list() throws ParseException {
        ArrayList<Object> list = new ArrayList<Object>();
        this.jj_consume_token(10);
        block0 : switch (this.jj_nt.kind) {
            case 7: 
            case 10: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 22: 
            case 23: 
            case 26: 
            case 27: {
                Object value = this.anything();
                list.add(value);
                value = null;
                while (true) {
                    switch (this.jj_nt.kind) {
                        case 6: {
                            break;
                        }
                        default: {
                            this.jj_la1[5] = this.jj_gen;
                            break block0;
                        }
                    }
                    this.jj_consume_token(6);
                    value = this.anything();
                    list.add(value);
                    value = null;
                }
            }
            default: {
                this.jj_la1[6] = this.jj_gen;
            }
        }
        this.jj_consume_token(11);
        list.trimToSize();
        return list;
    }

    public final Object value() throws ParseException {
        Object x;
        switch (this.jj_nt.kind) {
            case 22: 
            case 23: 
            case 26: 
            case 27: {
                x = this.string();
                break;
            }
            case 15: 
            case 16: {
                x = this.number();
                break;
            }
            case 17: 
            case 18: {
                x = this.booleanValue();
                break;
            }
            case 19: {
                x = this.nullValue();
                break;
            }
            default: {
                this.jj_la1[7] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return x;
    }

    public final Object nullValue() throws ParseException {
        this.jj_consume_token(19);
        return null;
    }

    public final Boolean booleanValue() throws ParseException {
        Boolean b;
        switch (this.jj_nt.kind) {
            case 17: {
                this.jj_consume_token(17);
                b = Boolean.TRUE;
                break;
            }
            case 18: {
                this.jj_consume_token(18);
                b = Boolean.FALSE;
                break;
            }
            default: {
                this.jj_la1[8] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return b;
    }

    public final Number number() throws ParseException {
        switch (this.jj_nt.kind) {
            case 16: {
                Token t = this.jj_consume_token(16);
                if (this.nativeNumbers) {
                    return Long.valueOf(t.image);
                }
                return new BigDecimal(t.image);
            }
            case 15: {
                Token t = this.jj_consume_token(15);
                if (this.nativeNumbers) {
                    return Double.valueOf(t.image);
                }
                return new BigInteger(JSONParser.substringBefore(t.image, '.'));
            }
        }
        this.jj_la1[9] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final String string() throws ParseException {
        String s;
        switch (this.jj_nt.kind) {
            case 23: 
            case 27: {
                s = this.doubleQuoteString();
                break;
            }
            case 22: 
            case 26: {
                s = this.singleQuoteString();
                break;
            }
            default: {
                this.jj_la1[10] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return s;
    }

    public final String doubleQuoteString() throws ParseException {
        switch (this.jj_nt.kind) {
            case 23: {
                this.jj_consume_token(23);
                return "";
            }
            case 27: {
                this.jj_consume_token(27);
                String image = this.token.image;
                return image.substring(1, image.length() - 1);
            }
        }
        this.jj_la1[11] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final String singleQuoteString() throws ParseException {
        switch (this.jj_nt.kind) {
            case 22: {
                this.jj_consume_token(22);
                return "";
            }
            case 26: {
                this.jj_consume_token(26);
                String image = this.token.image;
                return image.substring(1, image.length() - 1);
            }
        }
        this.jj_la1[12] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final String symbol() throws ParseException {
        this.jj_consume_token(28);
        return this.token.image;
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{214926464, 491520, 483360768, 64, 483360768, 64, 214926464, 214925312, 393216, 98304, 0xCC00000, 0x8800000, 0x4400000};
    }

    public JSONParser(InputStream stream) {
        this(stream, null);
    }

    public JSONParser(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new JavaCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new JSONParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.token.next = this.jj_nt = this.token_source.getNextToken();
        this.jj_gen = 0;
        for (int i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.token.next = this.jj_nt = this.token_source.getNextToken();
        this.jj_gen = 0;
        for (int i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public JSONParser(Reader stream) {
        this.jj_input_stream = new JavaCharStream(stream, 1, 1);
        this.token_source = new JSONParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.token.next = this.jj_nt = this.token_source.getNextToken();
        this.jj_gen = 0;
        for (int i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        if (this.jj_input_stream == null) {
            this.jj_input_stream = new JavaCharStream(stream, 1, 1);
        } else {
            this.jj_input_stream.ReInit(stream, 1, 1);
        }
        if (this.token_source == null) {
            this.token_source = new JSONParserTokenManager(this.jj_input_stream);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.token.next = this.jj_nt = this.token_source.getNextToken();
        this.jj_gen = 0;
        for (int i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public JSONParser(JSONParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.token.next = this.jj_nt = this.token_source.getNextToken();
        this.jj_gen = 0;
        for (int i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(JSONParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.token.next = this.jj_nt = this.token_source.getNextToken();
        this.jj_gen = 0;
        for (int i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = this.jj_nt;
        this.jj_nt = this.token.next != null ? this.jj_nt.next : (this.jj_nt.next = this.token_source.getNextToken());
        if (this.token.kind == kind) {
            ++this.jj_gen;
            return this.token;
        }
        this.jj_nt = this.token;
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    public final Token getNextToken() {
        this.token = this.jj_nt;
        this.jj_nt = this.token.next != null ? this.jj_nt.next : (this.jj_nt.next = this.token_source.getNextToken());
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[29];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 13; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 29; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final boolean trace_enabled() {
        return this.trace_enabled;
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    static {
        JSONParser.jj_la1_init_0();
    }
}

