/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.dom.IWikiDocument;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceMacroConstants;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceMacroTokenManager;
import com.atlassian.plugins.conversion.confluence.parser.ParseException;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

public class ConfluenceMacro
implements ConfluenceMacroConstants {
    private String _name;
    private String _defaultArg;
    private Hashtable _args;
    public ConfluenceMacroTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1 = new int[12];
    private static int[] jj_la1_0;
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;

    public String getDefaultArg() {
        return this._defaultArg;
    }

    public String getName() {
        return this._name;
    }

    public Hashtable getArgs() {
        return this._args;
    }

    public boolean isEmpty() {
        return this._defaultArg == null && (this._args == null || this._args.size() == 0);
    }

    public static ConfluenceMacro parse(String buf, IWikiDocument doc) throws ParseException {
        StringReader reader = new StringReader(buf);
        ConfluenceMacro parser = new ConfluenceMacro(reader);
        parser.Macro(doc);
        return parser;
    }

    public final void Macro(IWikiDocument doc) throws ParseException {
        this.jj_consume_token(1);
        this.MacroName();
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 5: {
                this.jj_consume_token(5);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 5: 
                    case 7: 
                    case 9: {
                        this.KeyValuePairs();
                        break block0;
                    }
                }
                this.jj_la1[0] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
            }
        }
        this.jj_consume_token(2);
        this.jj_consume_token(0);
    }

    public final void MacroName() throws ParseException {
        StringBuffer buf = new StringBuffer();
        block7: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 7: {
                    this.jj_consume_token(7);
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: 
                case 9: {
                    continue block7;
                }
            }
            break;
        }
        this.jj_la1[3] = this.jj_gen;
        this._name = buf.toString();
    }

    public final void KeyValuePairs() throws ParseException {
        this._args = new Hashtable();
        this.KeyValuePair();
        block6: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 6: {
                    break;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    break block6;
                }
            }
            this.jj_consume_token(6);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: 
                case 7: 
                case 9: {
                    this.KeyValuePair();
                    continue block6;
                }
            }
            this.jj_la1[5] = this.jj_gen;
        }
    }

    public final void KeyValuePair() throws ParseException {
        StringBuffer key = new StringBuffer();
        StringBuffer value = new StringBuffer();
        boolean isDefault = true;
        this.Key(key);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: {
                this.jj_consume_token(8);
                isDefault = false;
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 5: 
                    case 7: 
                    case 8: 
                    case 9: {
                        this.Value(value);
                        break block0;
                    }
                }
                this.jj_la1[6] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[7] = this.jj_gen;
            }
        }
        if (isDefault) {
            this._defaultArg = key.toString();
        } else {
            this._args.put(key.toString(), value.toString());
        }
    }

    public final void Key(StringBuffer key) throws ParseException {
        block8: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 7: {
                    this.jj_consume_token(7);
                    break;
                }
                default: {
                    this.jj_la1[8] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            key.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: 
                case 7: 
                case 9: {
                    continue block8;
                }
            }
            break;
        }
        this.jj_la1[9] = this.jj_gen;
    }

    public final void Value(StringBuffer value) throws ParseException {
        block9: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 7: {
                    this.jj_consume_token(7);
                    break;
                }
                case 8: {
                    this.jj_consume_token(8);
                    break;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            value.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: 
                case 7: 
                case 8: 
                case 9: {
                    continue block9;
                }
            }
            break;
        }
        this.jj_la1[11] = this.jj_gen;
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{672, 32, 640, 640, 64, 672, 928, 256, 672, 672, 928, 928};
    }

    public ConfluenceMacro(InputStream stream) {
        this(stream, null);
    }

    public ConfluenceMacro(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new ConfluenceMacroTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 12; ++i) {
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
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 12; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public ConfluenceMacro(Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ConfluenceMacroTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 12; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 12; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public ConfluenceMacro(ConfluenceMacroTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 12; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(ConfluenceMacroTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 12; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    private final Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
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

    private final int jj_ntk() {
        this.jj_nt = this.token.next;
        if (this.jj_nt == null) {
            this.token.next = this.token_source.getNextToken();
            this.jj_ntk = this.token.next.kind;
            return this.jj_ntk;
        }
        this.jj_ntk = this.jj_nt.kind;
        return this.jj_ntk;
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.removeAllElements();
        boolean[] la1tokens = new boolean[10];
        for (i = 0; i < 10; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 12; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 10; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.addElement(this.jj_expentry);
        }
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = (int[])this.jj_expentries.elementAt(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    static {
        ConfluenceMacro.jj_la1_0();
    }
}

