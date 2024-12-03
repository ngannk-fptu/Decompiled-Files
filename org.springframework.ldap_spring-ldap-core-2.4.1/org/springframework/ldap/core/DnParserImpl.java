/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.DnParser;
import org.springframework.ldap.core.DnParserImplConstants;
import org.springframework.ldap.core.DnParserImplTokenManager;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.ldap.core.LdapRdnComponent;
import org.springframework.ldap.core.ParseException;
import org.springframework.ldap.core.SimpleCharStream;
import org.springframework.ldap.core.Token;

public class DnParserImpl
implements DnParser,
DnParserImplConstants {
    public DnParserImplTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1 = new int[6];
    private static int[] jj_la1_0;
    private List jj_expentries = new ArrayList();
    private int[] jj_expentry;
    private int jj_kind = -1;

    public final void input() throws ParseException {
        this.dn();
    }

    @Override
    public final DistinguishedName dn() throws ParseException {
        DistinguishedName dn = new DistinguishedName();
        LdapRdn rdn = this.rdn();
        dn.add(0, rdn);
        block7: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 19: 
                case 20: {
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    break block7;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                default: {
                    this.jj_la1[1] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            rdn = this.rdn();
            dn.add(0, rdn);
        }
        return dn;
    }

    @Override
    public final LdapRdn rdn() throws ParseException {
        LdapRdn rdn = new LdapRdn();
        LdapRdnComponent rdnComponent = this.attributeTypeAndValue();
        rdn.addComponent(rdnComponent);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 21: {
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(21);
            rdnComponent = this.attributeTypeAndValue();
            rdn.addComponent(rdnComponent);
        }
        return rdn;
    }

    public final LdapRdnComponent attributeTypeAndValue() throws ParseException {
        block6: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 16: {
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    break block6;
                }
            }
            this.jj_consume_token(16);
        }
        String attributeType = this.AttributeType();
        this.SpacedEquals();
        String value = this.AttributeValue();
        block7: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 16: {
                    break;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    break block7;
                }
            }
            this.jj_consume_token(16);
        }
        return new LdapRdnComponent(attributeType, value, true);
    }

    public final void SpacedEquals() throws ParseException {
        this.token_source.SwitchTo(2);
        this.jj_consume_token(18);
    }

    public final String AttributeType() throws ParseException {
        Token t;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 15: {
                t = this.jj_consume_token(15);
                break;
            }
            case 14: {
                t = this.jj_consume_token(14);
                break;
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return t.image.toString();
    }

    public final String AttributeValue() throws ParseException {
        this.token_source.SwitchTo(1);
        Token t = this.jj_consume_token(17);
        this.token_source.SwitchTo(0);
        return t.image.toString();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{0x180000, 0x180000, 0x200000, 65536, 65536, 49152};
    }

    public DnParserImpl(InputStream stream) {
        this(stream, null);
    }

    public DnParserImpl(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new DnParserImplTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
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
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public DnParserImpl(Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new DnParserImplTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public DnParserImpl(DnParserImplTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(DnParserImplTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
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

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[22];
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
        for (i = 0; i < 22; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = (int[])this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    static {
        DnParserImpl.jj_la1_init_0();
    }
}

