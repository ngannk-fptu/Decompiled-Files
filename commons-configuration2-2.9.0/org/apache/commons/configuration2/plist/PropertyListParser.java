/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Hex
 */
package org.apache.commons.configuration2.plist;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.plist.ParseException;
import org.apache.commons.configuration2.plist.PropertyListConfiguration;
import org.apache.commons.configuration2.plist.PropertyListParserConstants;
import org.apache.commons.configuration2.plist.PropertyListParserTokenManager;
import org.apache.commons.configuration2.plist.SimpleCharStream;
import org.apache.commons.configuration2.plist.Token;
import org.apache.commons.configuration2.tree.ImmutableNode;

class PropertyListParser
implements PropertyListParserConstants {
    public PropertyListParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[6];
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns = new JJCalls[1];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    protected String removeQuotes(String s) {
        if (s == null) {
            return null;
        }
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    protected String unescapeQuotes(String s) {
        return s.replaceAll("\\\\\"", "\"");
    }

    protected byte[] filterData(String s) throws ParseException {
        if (s == null) {
            return null;
        }
        if (s.startsWith("<") && s.endsWith(">") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        if ((s = s.replaceAll("\\s", "")).length() % 2 != 0) {
            s = "0" + s;
        }
        try {
            return Hex.decodeHex((char[])s.toCharArray());
        }
        catch (Exception e) {
            throw new ParseException("Unable to parse the byte[] : " + e.getMessage());
        }
    }

    protected Date parseDate(String s) throws ParseException {
        return PropertyListConfiguration.parseDate(s);
    }

    public final PropertyListConfiguration parse() throws ParseException {
        PropertyListConfiguration configuration = null;
        configuration = this.Dictionary();
        this.jj_consume_token(0);
        return configuration;
    }

    public final PropertyListConfiguration Dictionary() throws ParseException {
        ImmutableNode.Builder builder = new ImmutableNode.Builder();
        ImmutableNode child = null;
        this.jj_consume_token(14);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 27: 
                case 28: {
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    break block3;
                }
            }
            child = this.Property();
            if (child.getValue() instanceof HierarchicalConfiguration) {
                HierarchicalConfiguration conf = (HierarchicalConfiguration)child.getValue();
                ImmutableNode root = (ImmutableNode)conf.getNodeModel().getNodeHandler().getRootNode();
                ImmutableNode.Builder childBuilder = new ImmutableNode.Builder();
                childBuilder.name(child.getNodeName()).value(root.getValue()).addChildren(root.getChildren());
                builder.addChild(childBuilder.create());
                continue;
            }
            builder.addChild(child);
        }
        this.jj_consume_token(15);
        return new PropertyListConfiguration(builder.create());
    }

    public final ImmutableNode Property() throws ParseException {
        String key = null;
        Object value = null;
        ImmutableNode.Builder node = new ImmutableNode.Builder();
        key = this.String();
        node.name(key);
        this.jj_consume_token(17);
        value = this.Element();
        node.value(value);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 16: {
                this.jj_consume_token(16);
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
            }
        }
        return node.create();
    }

    public final Object Element() throws ParseException {
        Object value = null;
        if (this.jj_2_1(2)) {
            value = this.Array();
            return value;
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14: {
                value = this.Dictionary();
                return value;
            }
            case 27: 
            case 28: {
                value = this.String();
                return value;
            }
            case 25: {
                value = this.Data();
                return value;
            }
            case 26: {
                value = this.Date();
                return value;
            }
        }
        this.jj_la1[2] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final List Array() throws ParseException {
        ArrayList<Object> list = new ArrayList<Object>();
        Object element = null;
        this.jj_consume_token(11);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 11: 
            case 14: 
            case 25: 
            case 26: 
            case 27: 
            case 28: {
                element = this.Element();
                list.add(element);
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 13: {
                            break;
                        }
                        default: {
                            this.jj_la1[3] = this.jj_gen;
                            break block0;
                        }
                    }
                    this.jj_consume_token(13);
                    element = this.Element();
                    list.add(element);
                }
            }
            default: {
                this.jj_la1[4] = this.jj_gen;
            }
        }
        this.jj_consume_token(12);
        return list;
    }

    public final String String() throws ParseException {
        Token token = null;
        Object value = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 28: {
                token = this.jj_consume_token(28);
                return this.unescapeQuotes(this.removeQuotes(token.image));
            }
            case 27: {
                token = this.jj_consume_token(27);
                return token.image;
            }
        }
        this.jj_la1[5] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final byte[] Data() throws ParseException {
        Token token = this.jj_consume_token(25);
        return this.filterData(token.image);
    }

    public final Date Date() throws ParseException {
        Token token = this.jj_consume_token(26);
        return this.parseDate(token.image);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_1(int xla) {
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

    private boolean jj_3R_15() {
        return this.jj_scan_token(27);
    }

    private boolean jj_3R_3() {
        if (this.jj_scan_token(11)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_4()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(12);
    }

    private boolean jj_3_1() {
        return this.jj_3R_3();
    }

    private boolean jj_3R_5() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3_1()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_6()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_7()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_8()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_9()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_14() {
        return this.jj_scan_token(28);
    }

    private boolean jj_3R_11() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_14()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_15()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_10() {
        return this.jj_scan_token(14);
    }

    private boolean jj_3R_13() {
        return this.jj_scan_token(26);
    }

    private boolean jj_3R_9() {
        return this.jj_3R_13();
    }

    private boolean jj_3R_8() {
        return this.jj_3R_12();
    }

    private boolean jj_3R_12() {
        return this.jj_scan_token(25);
    }

    private boolean jj_3R_7() {
        return this.jj_3R_11();
    }

    private boolean jj_3R_4() {
        return this.jj_3R_5();
    }

    private boolean jj_3R_6() {
        return this.jj_3R_10();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{0x18000000, 65536, 503332864, 8192, 503334912, 0x18000000};
    }

    public PropertyListParser(InputStream stream) {
        this(stream, null);
    }

    public PropertyListParser(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new PropertyListParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 6; ++i) {
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
        for (i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public PropertyListParser(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new PropertyListParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 6; ++i) {
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
        for (i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public PropertyListParser(PropertyListParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(PropertyListParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
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

    private boolean jj_scan_token(int kind) {
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
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private int jj_ntk() {
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
            block1: for (int[] oldentry : this.jj_expentries) {
                if (oldentry.length != this.jj_expentry.length) continue;
                for (int i = 0; i < this.jj_expentry.length; ++i) {
                    if (oldentry[i] != this.jj_expentry[i]) continue block1;
                }
                this.jj_expentries.add(this.jj_expentry);
                break;
            }
            if (pos != 0) {
                this.jj_endpos = pos;
                this.jj_lasttokens[this.jj_endpos - 1] = kind;
            }
        }
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[30];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 6; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 30; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
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

    private void jj_save(int index, int xla) {
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
        PropertyListParser.jj_la1_init_0();
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

