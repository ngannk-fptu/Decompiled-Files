/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.dom.IWikiDocument;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceImageConstants;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceImageTokenManager;
import com.atlassian.plugins.conversion.confluence.parser.ParseException;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ConfluenceImage
implements ConfluenceImageConstants {
    private String _spaceKey;
    private String _pageName;
    private String _imgName;
    private Hashtable _args;
    public ConfluenceImageTokenManager token_source;
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
    private final int[] jj_la1 = new int[13];
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns = new JJCalls[2];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public static void parse(String buf, IWikiDocument doc) throws Exception {
        StringReader reader = new StringReader(buf);
        ConfluenceImage parser = new ConfluenceImage(reader);
        parser.Image();
        doc.addImage(parser._spaceKey, parser._pageName, parser._imgName, parser._args, buf);
    }

    public static String changeImgWidthHeight(String wikiTxt, String width, String height) throws Exception {
        ConfluenceImage parser = new ConfluenceImage(new StringReader(wikiTxt));
        parser.Image();
        return parser.changeWidthHeight(width, height);
    }

    public String changeWidthHeight(String width, String height) {
        String val = "!" + (this._spaceKey != null && this._spaceKey.trim().length() > 0 ? this._spaceKey + ":" : "") + (this._pageName != null && this._spaceKey.trim().length() > 0 ? this._pageName + "^" : "") + this._imgName + "|";
        Enumeration e = this._args.keys();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            if (key.equalsIgnoreCase("height") || key.equalsIgnoreCase("width")) continue;
            String value = (String)this._args.get(key);
            val = val + key + '=' + value + ',';
        }
        return val + "height=" + height + ",width=" + width + '!';
    }

    public String getSpaceKey() {
        return this._spaceKey;
    }

    public String getPageName() {
        return this._pageName;
    }

    public String getImgName() {
        return this._imgName;
    }

    public final void Image() throws ParseException {
        this.jj_consume_token(1);
        this.ImageName();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 2: {
                this.jj_consume_token(2);
                this.KeyValuePairs();
                break;
            }
            default: {
                this.jj_la1[0] = this.jj_gen;
            }
        }
        this.jj_consume_token(1);
        this.jj_consume_token(0);
    }

    public final void ImageName() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4: {
                this.jj_consume_token(4);
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
            }
        }
        if (this.jj_2_1(Integer.MAX_VALUE)) {
            this.SpaceKey();
        }
        if (this.jj_2_2(Integer.MAX_VALUE)) {
            this.PageTitle();
        }
        this.AttachmentReference();
    }

    public final void SpaceKey() throws ParseException {
        StringBuffer buf = new StringBuffer();
        block3: while (true) {
            this.jj_consume_token(8);
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    continue block3;
                }
            }
            break;
        }
        this.jj_la1[2] = this.jj_gen;
        this.jj_consume_token(6);
        this._spaceKey = buf.toString();
    }

    public final void AttachmentReference() throws ParseException {
        StringBuffer buf = new StringBuffer();
        block9: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    this.jj_consume_token(8);
                    break;
                }
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 6: 
                case 8: 
                case 9: {
                    continue block9;
                }
            }
            break;
        }
        this.jj_la1[4] = this.jj_gen;
        this._imgName = buf.toString();
    }

    public final void PageTitle() throws ParseException {
        StringBuffer buf = new StringBuffer();
        block9: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    this.jj_consume_token(8);
                    break;
                }
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 6: 
                case 8: 
                case 9: {
                    continue block9;
                }
            }
            break;
        }
        this.jj_la1[6] = this.jj_gen;
        this.jj_consume_token(7);
    }

    public final void KeyValuePairs() throws ParseException {
        this._args = new Hashtable();
        this.KeyValuePair();
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: {
                    break;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(3);
            this.KeyValuePair();
        }
    }

    public final void KeyValuePair() throws ParseException {
        StringBuffer key = new StringBuffer();
        StringBuffer value = new StringBuffer();
        this.Key(key);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 5: {
                this.jj_consume_token(5);
                this.Value(value);
                break;
            }
            default: {
                this.jj_la1[8] = this.jj_gen;
            }
        }
        this._args.put(key.toString(), value.toString());
    }

    public final void Key(StringBuffer key) throws ParseException {
        block9: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 8: {
                    this.jj_consume_token(8);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                default: {
                    this.jj_la1[9] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            key.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 6: 
                case 8: 
                case 9: {
                    continue block9;
                }
            }
            break;
        }
        this.jj_la1[10] = this.jj_gen;
    }

    public final void Value(StringBuffer value) throws ParseException {
        StringBuffer buf = new StringBuffer();
        block9: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 8: {
                    this.jj_consume_token(8);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                default: {
                    this.jj_la1[11] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            value.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 6: 
                case 8: 
                case 9: {
                    continue block9;
                }
            }
            break;
        }
        this.jj_la1[12] = this.jj_gen;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean jj_2_2(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_2();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(1, xla);
        }
    }

    private final boolean jj_3R_10() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(8)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(9)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(6)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(4)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private final boolean jj_3R_8() {
        Token xsp;
        if (this.jj_3R_10()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_10());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(7);
    }

    private final boolean jj_3_2() {
        return this.jj_3R_8();
    }

    private final boolean jj_3_1() {
        return this.jj_3R_7();
    }

    private final boolean jj_3R_9() {
        return this.jj_scan_token(8);
    }

    private final boolean jj_3R_7() {
        Token xsp;
        if (this.jj_3R_9()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_9());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(6);
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{4, 16, 256, 848, 848, 848, 848, 8, 32, 848, 848, 848, 848};
    }

    public ConfluenceImage(InputStream stream) {
        this(stream, null);
    }

    public ConfluenceImage(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new ConfluenceImageTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 13; ++i) {
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
        for (i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ConfluenceImage(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ConfluenceImageTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 13; ++i) {
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
        for (i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ConfluenceImage(ConfluenceImageTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 13; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(ConfluenceImageTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 13; ++i) {
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
        boolean[] la1tokens = new boolean[10];
        for (i = 0; i < 10; ++i) {
            la1tokens[i] = false;
        }
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
        for (i = 0; i < 10; ++i) {
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
        for (int i = 0; i < 2; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen <= this.jj_gen) continue;
                    this.jj_la = p.arg;
                    this.jj_lastpos = this.jj_scanpos = p.first;
                    switch (i) {
                        case 0: {
                            this.jj_3_1();
                            break;
                        }
                        case 1: {
                            this.jj_3_2();
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
        ConfluenceImage.jj_la1_0();
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

