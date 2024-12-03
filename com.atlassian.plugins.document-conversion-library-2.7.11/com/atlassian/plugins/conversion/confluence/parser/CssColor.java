/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.parser.CssColorConstants;
import com.atlassian.plugins.conversion.confluence.parser.CssColorTokenManager;
import com.atlassian.plugins.conversion.confluence.parser.ParseException;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import java.awt.Color;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

public class CssColor
implements CssColorConstants {
    Color _color;
    private static HashMap _colorMap = new HashMap();
    public CssColorTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    public boolean lookingAhead = false;
    private boolean jj_semLA;
    private int jj_gen;
    private final int[] jj_la1 = new int[20];
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns = new JJCalls[1];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public static Color parse(String buf) throws Exception {
        StringReader reader = new StringReader(buf);
        CssColor parser = new CssColor(reader);
        parser.Color();
        return parser._color;
    }

    public final void Color() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                this.HexLiteral();
                break;
            }
            case 2: {
                this.RgbColor();
                break;
            }
            case 4: 
            case 7: {
                this.Literal();
                break;
            }
            default: {
                this.jj_la1[0] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.jj_consume_token(0);
    }

    public final void Literal() throws ParseException {
        StringBuffer buf = new StringBuffer();
        boolean rgb = false;
        block7: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: {
                    this.jj_consume_token(7);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                default: {
                    this.jj_la1[1] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 7: {
                    continue block7;
                }
            }
            break;
        }
        this.jj_la1[2] = this.jj_gen;
        String key = buf.toString().trim().toLowerCase();
        Integer val = (Integer)_colorMap.get(key);
        if (val != null) {
            this._color = new Color(val);
        }
    }

    public final void HexLiteral() throws ParseException {
        StringBuffer hexBuf = new StringBuffer();
        this.jj_consume_token(1);
        this.jj_consume_token(4);
        hexBuf.append(this.token.image);
        this.jj_consume_token(4);
        hexBuf.append(this.token.image);
        this.jj_consume_token(4);
        hexBuf.append(this.token.image);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4: {
                this.jj_consume_token(4);
                hexBuf.append(this.token.image);
                this.jj_consume_token(4);
                hexBuf.append(this.token.image);
                this.jj_consume_token(4);
                hexBuf.append(this.token.image);
                break;
            }
            default: {
                this.jj_la1[3] = this.jj_gen;
            }
        }
        if (hexBuf.length() == 3) {
            hexBuf.insert(2, hexBuf.charAt(2));
            hexBuf.insert(1, hexBuf.charAt(1));
            hexBuf.insert(0, hexBuf.charAt(0));
        }
        int rgb = Integer.parseInt(hexBuf.toString(), 16);
        this._color = new Color(rgb);
    }

    public final void RgbColor() throws ParseException {
        StringBuffer r = new StringBuffer();
        StringBuffer g = new StringBuffer();
        StringBuffer b = new StringBuffer();
        this.jj_consume_token(2);
        this.jj_consume_token(9);
        if (this.jj_2_1(4)) {
            this.jj_consume_token(4);
            r.append(this.token.image);
            block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    r.append(this.token.image);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 4: {
                            this.jj_consume_token(4);
                            r.append(this.token.image);
                            break block0;
                        }
                    }
                    this.jj_la1[4] = this.jj_gen;
                    break;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                }
            }
            this.jj_consume_token(3);
            this.jj_consume_token(4);
            g.append(this.token.image);
            block6 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    g.append(this.token.image);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 4: {
                            this.jj_consume_token(4);
                            g.append(this.token.image);
                            break block6;
                        }
                    }
                    this.jj_la1[6] = this.jj_gen;
                    break;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                }
            }
            this.jj_consume_token(3);
            this.jj_consume_token(4);
            b.append(this.token.image);
            block12 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    b.append(this.token.image);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 4: {
                            this.jj_consume_token(4);
                            b.append(this.token.image);
                            break block12;
                        }
                    }
                    this.jj_la1[8] = this.jj_gen;
                    break;
                }
                default: {
                    this.jj_la1[9] = this.jj_gen;
                }
            }
            Integer red = new Integer(r.toString());
            Integer green = new Integer(g.toString());
            Integer blue = new Integer(b.toString());
            this._color = new Color(red, green, blue);
        } else {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    r.append(this.token.image);
                    block21 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 4: {
                            this.jj_consume_token(4);
                            r.append(this.token.image);
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 4: {
                                    this.jj_consume_token(4);
                                    r.append(this.token.image);
                                    break block21;
                                }
                            }
                            this.jj_la1[10] = this.jj_gen;
                            break;
                        }
                        default: {
                            this.jj_la1[11] = this.jj_gen;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 6: {
                            this.jj_consume_token(6);
                            this.jj_consume_token(4);
                            r.append("." + this.token.image);
                            break;
                        }
                        default: {
                            this.jj_la1[12] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(5);
                    this.jj_consume_token(3);
                    this.jj_consume_token(4);
                    g.append(this.token.image);
                    block30 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 4: {
                            this.jj_consume_token(4);
                            g.append(this.token.image);
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 4: {
                                    this.jj_consume_token(4);
                                    g.append(this.token.image);
                                    break block30;
                                }
                            }
                            this.jj_la1[13] = this.jj_gen;
                            break;
                        }
                        default: {
                            this.jj_la1[14] = this.jj_gen;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 6: {
                            this.jj_consume_token(6);
                            this.jj_consume_token(4);
                            g.append("." + this.token.image);
                            break;
                        }
                        default: {
                            this.jj_la1[15] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(5);
                    this.jj_consume_token(3);
                    this.jj_consume_token(4);
                    b.append(this.token.image);
                    block39 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 4: {
                            this.jj_consume_token(4);
                            b.append(this.token.image);
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 4: {
                                    this.jj_consume_token(4);
                                    b.append(this.token.image);
                                    break block39;
                                }
                            }
                            this.jj_la1[16] = this.jj_gen;
                            break;
                        }
                        default: {
                            this.jj_la1[17] = this.jj_gen;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 6: {
                            this.jj_consume_token(6);
                            this.jj_consume_token(4);
                            b.append("." + this.token.image);
                            break;
                        }
                        default: {
                            this.jj_la1[18] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(5);
                    Float red = new Float(r.toString());
                    Float green = new Float(g.toString());
                    Float blue = new Float(b.toString());
                    this._color = new Color(red.floatValue() / 100.0f, green.floatValue() / 100.0f, blue.floatValue() / 100.0f);
                    break;
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        this.jj_consume_token(10);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean jj_2_1(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_1();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(0, xla);
        }
    }

    private final boolean jj_3R_4() {
        return this.jj_scan_token(4);
    }

    private final boolean jj_3R_3() {
        return this.jj_scan_token(4);
    }

    private final boolean jj_3R_2() {
        if (this.jj_scan_token(4)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_4()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private final boolean jj_3_1() {
        if (this.jj_scan_token(4)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_2()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(3)) {
            return true;
        }
        if (this.jj_scan_token(4)) {
            return true;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_3()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(3);
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{150, 144, 144, 16, 16, 16, 16, 16, 16, 16, 16, 16, 64, 16, 16, 64, 16, 16, 64, 16};
    }

    public CssColor(InputStream stream) {
        this(stream, null);
    }

    public CssColor(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new CssColorTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 20; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        int i;
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
        for (i = 0; i < 20; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public CssColor(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new CssColorTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 20; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(Reader stream) {
        int i;
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 20; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public CssColor(CssColorTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 20; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(CssColorTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 20; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    private final Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    JJCalls c = this.jj_2_rtns[i];
                    while (c != null) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                        c = c.next;
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    private final boolean jj_scan_token(int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
                this.jj_lastpos = this.jj_scanpos.next;
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok = this.token;
            while (tok != null && tok != this.jj_scanpos) {
                ++i;
                tok = tok.next;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.lookingAhead ? this.jj_scanpos : this.token;
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

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
            boolean exists = false;
            Enumeration e = this.jj_expentries.elements();
            while (e.hasMoreElements()) {
                int[] oldentry = (int[])e.nextElement();
                if (oldentry.length != this.jj_expentry.length) continue;
                exists = true;
                for (int i = 0; i < this.jj_expentry.length; ++i) {
                    if (oldentry[i] == this.jj_expentry[i]) continue;
                    exists = false;
                    break;
                }
                if (!exists) continue;
                break;
            }
            if (!exists) {
                this.jj_expentries.addElement(this.jj_expentry);
            }
            if (pos != 0) {
                this.jj_endpos = pos;
                this.jj_lasttokens[this.jj_endpos - 1] = kind;
            }
        }
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.removeAllElements();
        boolean[] la1tokens = new boolean[11];
        for (i = 0; i < 11; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 20; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 11; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.addElement(this.jj_expentry);
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
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

    private final void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 1; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen <= this.jj_gen) continue;
                    this.jj_la = p.arg;
                    this.jj_lastpos = this.jj_scanpos = p.first;
                    switch (i) {
                        case 0: {
                            this.jj_3_1();
                        }
                    }
                } while ((p = p.next) != null);
                continue;
            }
            catch (LookaheadSuccess lookaheadSuccess) {
                // empty catch block
            }
        }
        this.jj_rescan = false;
    }

    private final void jj_save(int index, int xla) {
        JJCalls p = this.jj_2_rtns[index];
        while (p.gen > this.jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();
                break;
            }
            p = p.next;
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    static {
        _colorMap.put("black", new Integer(0));
        _colorMap.put("gray", new Integer(0x808080));
        _colorMap.put("grey", new Integer(0x808080));
        _colorMap.put("maroon", new Integer(0x800000));
        _colorMap.put("red", new Integer(0xFF0000));
        _colorMap.put("green", new Integer(32768));
        _colorMap.put("lime", new Integer(65280));
        _colorMap.put("olive", new Integer(0x808000));
        _colorMap.put("yellow", new Integer(0xFFFF00));
        _colorMap.put("navy", new Integer(128));
        _colorMap.put("blue", new Integer(255));
        _colorMap.put("purple", new Integer(0x800080));
        _colorMap.put("fuchsia", new Integer(0xFF00FF));
        _colorMap.put("teal", new Integer(32896));
        _colorMap.put("aqua", new Integer(65535));
        _colorMap.put("silver", new Integer(0xC0C0C0));
        _colorMap.put("white", new Integer(0xFFFFFF));
        _colorMap.put("aliceblue", new Integer(0xF0F8FF));
        _colorMap.put("antiquewhite", new Integer(16444375));
        _colorMap.put("aquamarine", new Integer(8388564));
        _colorMap.put("azure", new Integer(0xF0FFFF));
        _colorMap.put("beige", new Integer(16119260));
        _colorMap.put("blueviolet", new Integer(9055202));
        _colorMap.put("brown", new Integer(0xA52A2A));
        _colorMap.put("burlywood", new Integer(14596231));
        _colorMap.put("cadetblue", new Integer(6266528));
        _colorMap.put("chartreuse", new Integer(0x7FFF00));
        _colorMap.put("chocolate", new Integer(13789470));
        _colorMap.put("coral", new Integer(16744272));
        _colorMap.put("cornflowerblue", new Integer(6591981));
        _colorMap.put("cornsilk", new Integer(16775388));
        _colorMap.put("crimson", new Integer(14423100));
        _colorMap.put("darkblue", new Integer(139));
        _colorMap.put("darkcyan", new Integer(35723));
        _colorMap.put("darkgoldenrod", new Integer(12092939));
        _colorMap.put("darkgray", new Integer(0xA9A9A9));
        _colorMap.put("darkgreen", new Integer(25600));
        _colorMap.put("darkkhaki", new Integer(12433259));
        _colorMap.put("darkmagenta", new Integer(0x8B008B));
        _colorMap.put("darkolivegreen", new Integer(5597999));
        _colorMap.put("darkorange", new Integer(16747520));
        _colorMap.put("darkorchid", new Integer(10040012));
        _colorMap.put("darkred", new Integer(0x8B0000));
        _colorMap.put("darksalmon", new Integer(15308410));
        _colorMap.put("darkseagreen", new Integer(9419919));
        _colorMap.put("darkslateblue", new Integer(4734347));
        _colorMap.put("darkslategray", new Integer(0x2F4F4F));
        _colorMap.put("darkturquoise", new Integer(52945));
        _colorMap.put("darkviolet", new Integer(9699539));
        _colorMap.put("deeppink", new Integer(16716947));
        _colorMap.put("deepskyblue", new Integer(49151));
        _colorMap.put("dimgray", new Integer(0x696969));
        _colorMap.put("dodgerblue", new Integer(2003199));
        _colorMap.put("firebrick", new Integer(0xB22222));
        _colorMap.put("floralwhite", new Integer(0xFFFAF0));
        _colorMap.put("forestgreen", new Integer(0x228B22));
        _colorMap.put("gainsboro", new Integer(0xDCDCDC));
        _colorMap.put("ghostwhite", new Integer(0xF8F8FF));
        _colorMap.put("gold", new Integer(16766720));
        _colorMap.put("goldenrod", new Integer(14329120));
        _colorMap.put("greenyellow", new Integer(11403055));
        _colorMap.put("honeydew", new Integer(0xF0FFF0));
        _colorMap.put("hotpink", new Integer(16738740));
        _colorMap.put("indianred", new Integer(0xCD5C5C));
        _colorMap.put("indigo", new Integer(4915330));
        _colorMap.put("ivory", new Integer(0xFFFFF0));
        _colorMap.put("khaki", new Integer(15787660));
        _colorMap.put("lavender", new Integer(15132410));
        _colorMap.put("lavenderblush", new Integer(0xFFF0F5));
        _colorMap.put("lawngreen", new Integer(8190976));
        _colorMap.put("lemonchiffon", new Integer(16775885));
        _colorMap.put("lightblue", new Integer(11393254));
        _colorMap.put("lightcoral", new Integer(0xF08080));
        _colorMap.put("lightcyan", new Integer(0xE0FFFF));
        _colorMap.put("lightgoldenrodyellow", new Integer(16448210));
        _colorMap.put("lightgreen", new Integer(0x90EE90));
        _colorMap.put("lightgrey", new Integer(0xD3D3D3));
        _colorMap.put("lightpink", new Integer(16758465));
        _colorMap.put("lightsalmon", new Integer(16752762));
        _colorMap.put("lightseagreen", new Integer(2142890));
        _colorMap.put("lightskyblue", new Integer(8900346));
        _colorMap.put("lightslategray", new Integer(0x778899));
        _colorMap.put("lightsteelblue", new Integer(11584734));
        _colorMap.put("lightyellow", new Integer(0xFFFFE0));
        _colorMap.put("limegreen", new Integer(3329330));
        _colorMap.put("linen", new Integer(16445670));
        _colorMap.put("mediumaquamarine", new Integer(6737322));
        _colorMap.put("mediumblue", new Integer(205));
        _colorMap.put("mediumorchid", new Integer(12211667));
        _colorMap.put("mediumpurple", new Integer(9662683));
        _colorMap.put("mediumseagreen", new Integer(3978097));
        _colorMap.put("mediumslateblue", new Integer(8087790));
        _colorMap.put("mediumspringgreen", new Integer(64154));
        _colorMap.put("mediumturquoise", new Integer(4772300));
        _colorMap.put("mediumvioletred", new Integer(13047173));
        _colorMap.put("midnightblue", new Integer(1644912));
        _colorMap.put("mintcream", new Integer(0xF5FFFA));
        _colorMap.put("mistyrose", new Integer(16770273));
        _colorMap.put("moccasin", new Integer(16770229));
        _colorMap.put("navajowhite", new Integer(16768685));
        _colorMap.put("oldlace", new Integer(16643558));
        _colorMap.put("olivedrab", new Integer(7048739));
        _colorMap.put("orange", new Integer(16753920));
        _colorMap.put("orangered", new Integer(16729344));
        _colorMap.put("orchid", new Integer(14315734));
        _colorMap.put("palegoldenrod", new Integer(0xEEE8AA));
        _colorMap.put("palegreen", new Integer(10025880));
        _colorMap.put("paleturquoise", new Integer(0xAFEEEE));
        _colorMap.put("palevioletred", new Integer(14381203));
        _colorMap.put("papayawhip", new Integer(16773077));
        _colorMap.put("peachpuff", new Integer(16767673));
        _colorMap.put("peru", new Integer(13468991));
        _colorMap.put("pink", new Integer(16761035));
        _colorMap.put("plum", new Integer(0xDDA0DD));
        _colorMap.put("powderblue", new Integer(11591910));
        _colorMap.put("rosybrown", new Integer(12357519));
        _colorMap.put("royalblue", new Integer(4286945));
        _colorMap.put("saddlebrown", new Integer(9127187));
        _colorMap.put("salmon", new Integer(16416882));
        _colorMap.put("sandybrown", new Integer(16032864));
        _colorMap.put("seagreen", new Integer(3050327));
        _colorMap.put("seashell", new Integer(0xFFF5EE));
        _colorMap.put("sienna", new Integer(10506797));
        _colorMap.put("skyblue", new Integer(8900331));
        _colorMap.put("slateblue", new Integer(6970061));
        _colorMap.put("slategray", new Integer(7372944));
        _colorMap.put("snow", new Integer(0xFFFAFA));
        _colorMap.put("springgreen", new Integer(65407));
        _colorMap.put("steelblue", new Integer(4620980));
        _colorMap.put("tan", new Integer(13808780));
        _colorMap.put("thistle", new Integer(14204888));
        _colorMap.put("tomato", new Integer(16737095));
        _colorMap.put("turquoise", new Integer(4251856));
        _colorMap.put("violet", new Integer(976942));
        _colorMap.put("wheat", new Integer(16113331));
        _colorMap.put("whitesmoke", new Integer(0xF5F5F5));
        _colorMap.put("yellowgreen", new Integer(10145074));
        CssColor.jj_la1_0();
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }

    private static final class LookaheadSuccess
    extends Error {
        private LookaheadSuccess() {
        }
    }
}

