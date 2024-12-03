/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.dom.Hyperlink;
import com.atlassian.plugins.conversion.confluence.dom.IWikiDocument;
import com.atlassian.plugins.conversion.confluence.dom.TextFormat;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceHyperlinkConstants;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceHyperlinkTokenManager;
import com.atlassian.plugins.conversion.confluence.parser.ParseException;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;

public class ConfluenceHyperlink
implements ConfluenceHyperlinkConstants {
    private Hyperlink _hyperlink;
    public ConfluenceHyperlinkTokenManager token_source;
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
    private final int[] jj_la1 = new int[31];
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns = new JJCalls[4];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public static void parse(String buf, IWikiDocument doc, TextFormat format) throws Exception {
        StringReader reader = new StringReader(buf);
        ConfluenceHyperlink parser = new ConfluenceHyperlink(reader);
        parser._hyperlink = new Hyperlink();
        parser.Hyperlink(doc);
        doc.setHyperlink(parser._hyperlink, format, buf);
    }

    public final void Hyperlink(IWikiDocument doc) throws ParseException, Exception {
        this.jj_consume_token(1);
        if (this.jj_2_1(Integer.MAX_VALUE)) {
            this.LinkAlias(doc);
            this.Reference();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: {
                    this.LinkTip();
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    break;
                }
            }
        } else {
            this.Reference();
        }
        this.jj_consume_token(2);
        this.jj_consume_token(0);
    }

    public final void LinkAlias(IWikiDocument doc) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        block24: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: {
                    break;
                }
                default: {
                    this.jj_la1[1] = this.jj_gen;
                    break block24;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 18: {
                    this.jj_consume_token(18);
                    break;
                }
                case 14: {
                    this.jj_consume_token(14);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
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
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 11: {
                    this.jj_consume_token(11);
                    break;
                }
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 16: {
                    this.jj_consume_token(16);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 12: {
                    this.jj_consume_token(12);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
        }
        this.jj_consume_token(3);
        this._hyperlink.setDisplay(buf.toString());
    }

    public final void SpaceKey(IWikiDocument doc) throws ParseException {
        StringBuffer buf = new StringBuffer();
        block3: while (true) {
            this.jj_consume_token(21);
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 21: {
                    continue block3;
                }
            }
            break;
        }
        this.jj_la1[3] = this.jj_gen;
        this.jj_consume_token(7);
        this._hyperlink.setSpace(buf.toString());
    }

    public final void Reference() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 18: {
                this.jj_consume_token(18);
                break;
            }
            default: {
                this.jj_la1[4] = this.jj_gen;
            }
        }
        if (this.jj_2_2(Integer.MAX_VALUE)) {
            this.SpaceKey(null);
        }
        block3 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 12: 
            case 13: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 9: {
                        this.AttachmentReference();
                        break block3;
                    }
                    case 13: {
                        this.AnchorReference();
                        break block3;
                    }
                    case 15: 
                    case 16: 
                    case 19: 
                    case 20: {
                        this.ExternalLink();
                        break block3;
                    }
                    case 17: {
                        this.UserReference();
                        break block3;
                    }
                }
                this.jj_la1[7] = this.jj_gen;
                if (this.jj_2_3(Integer.MAX_VALUE)) {
                    this.PossibleShortcutLink();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 8: {
                        this.IDReference();
                        break block3;
                    }
                }
                this.jj_la1[8] = this.jj_gen;
                if (this.jj_2_4(12)) {
                    this.BlogPost();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 4: 
                    case 5: 
                    case 6: 
                    case 12: 
                    case 18: 
                    case 21: 
                    case 22: {
                        this.PageTitle();
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 9: 
                            case 13: {
                                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                    case 9: {
                                        this.AttachmentReference();
                                        break block3;
                                    }
                                    case 13: {
                                        this.AnchorReference();
                                        break block3;
                                    }
                                }
                                this.jj_la1[5] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.jj_la1[6] = this.jj_gen;
                        break block3;
                    }
                }
                this.jj_la1[9] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[10] = this.jj_gen;
            }
        }
    }

    public final void BlogPost() throws ParseException {
        StringBuffer buf = new StringBuffer();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 16: {
                this.jj_consume_token(16);
                buf.append(this.token.image);
                break;
            }
            default: {
                this.jj_la1[11] = this.jj_gen;
            }
        }
        this.jj_consume_token(21);
        buf.append(this.token.image);
        this.jj_consume_token(21);
        buf.append(this.token.image);
        this.jj_consume_token(21);
        buf.append(this.token.image);
        this.jj_consume_token(21);
        buf.append(this.token.image);
        this.jj_consume_token(16);
        buf.append(this.token.image);
        this.jj_consume_token(21);
        buf.append(this.token.image);
        this.jj_consume_token(21);
        buf.append(this.token.image);
        this.jj_consume_token(16);
        buf.append(this.token.image);
        this.jj_consume_token(21);
        buf.append(this.token.image);
        this.jj_consume_token(21);
        buf.append(this.token.image);
        this.jj_consume_token(16);
        buf.append(this.token.image);
        this.PageTitle();
        this._hyperlink.setReference(buf.toString() + this._hyperlink.getReference());
        this._hyperlink.setType(5);
    }

    public final void IDReference() throws ParseException {
        this._hyperlink.setType(1);
        StringBuffer buf = new StringBuffer();
        this.jj_consume_token(8);
        buf.append(this.token.image);
        block3: while (true) {
            this.jj_consume_token(21);
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 21: {
                    continue block3;
                }
            }
            break;
        }
        this.jj_la1[12] = this.jj_gen;
        this._hyperlink.setReference(buf.toString());
    }

    public final void UserReference() throws ParseException {
        this._hyperlink.setType(6);
        StringBuffer buf = new StringBuffer();
        this.jj_consume_token(17);
        buf.append(this.token.image);
        block22: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
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
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 16: {
                    this.jj_consume_token(16);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 18: {
                    this.jj_consume_token(18);
                    break;
                }
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                case 11: {
                    this.jj_consume_token(11);
                    break;
                }
                default: {
                    this.jj_la1[13] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 13: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: {
                    continue block22;
                }
            }
            break;
        }
        this.jj_la1[14] = this.jj_gen;
        this._hyperlink.setReference(buf.toString());
    }

    public final void PossibleShortcutLink() throws ParseException {
        this._hyperlink.setType(7);
        StringBuffer buf = new StringBuffer();
        block42: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
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
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 16: {
                    this.jj_consume_token(16);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 18: {
                    this.jj_consume_token(18);
                    break;
                }
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                default: {
                    this.jj_la1[15] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 13: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: {
                    continue block42;
                }
            }
            break;
        }
        this.jj_la1[16] = this.jj_gen;
        this.jj_consume_token(11);
        buf.append(this.token.image);
        block43: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
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
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 16: {
                    this.jj_consume_token(16);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 18: {
                    this.jj_consume_token(18);
                    break;
                }
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                default: {
                    this.jj_la1[17] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 13: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: {
                    continue block43;
                }
            }
            break;
        }
        this.jj_la1[18] = this.jj_gen;
        this._hyperlink.setReference(buf.toString());
    }

    public final void ExternalLink() throws ParseException {
        this._hyperlink.setType(4);
        StringBuffer buf = new StringBuffer();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 19: {
                this.jj_consume_token(19);
                break;
            }
            case 20: {
                this.jj_consume_token(20);
                break;
            }
            case 15: {
                this.jj_consume_token(15);
                break;
            }
            case 16: {
                this.jj_consume_token(16);
                break;
            }
            default: {
                this.jj_la1[19] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        buf.append(this.token.image);
        block29: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: {
                    break;
                }
                default: {
                    this.jj_la1[20] = this.jj_gen;
                    break block29;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 18: {
                    this.jj_consume_token(18);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
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
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 11: {
                    this.jj_consume_token(11);
                    break;
                }
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 16: {
                    this.jj_consume_token(16);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 12: {
                    this.jj_consume_token(12);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                default: {
                    this.jj_la1[21] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
        }
        this._hyperlink.setReference(buf.toString());
    }

    public final void PageTitle() throws ParseException {
        this._hyperlink.setType(0);
        StringBuffer buf = new StringBuffer();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 18: {
                this.jj_consume_token(18);
                break;
            }
            case 4: {
                this.jj_consume_token(4);
                break;
            }
            case 5: {
                this.jj_consume_token(5);
                break;
            }
            case 6: {
                this.jj_consume_token(6);
                break;
            }
            case 21: {
                this.jj_consume_token(21);
                break;
            }
            case 22: {
                this.jj_consume_token(22);
                break;
            }
            case 12: {
                this.jj_consume_token(12);
                break;
            }
            default: {
                this.jj_la1[22] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        buf.append(this.token.image);
        block24: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 5: 
                case 6: 
                case 8: 
                case 10: 
                case 12: 
                case 17: 
                case 18: 
                case 21: 
                case 22: {
                    break;
                }
                default: {
                    this.jj_la1[23] = this.jj_gen;
                    break block24;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 18: {
                    this.jj_consume_token(18);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 8: {
                    this.jj_consume_token(8);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 12: {
                    this.jj_consume_token(12);
                    break;
                }
                default: {
                    this.jj_la1[24] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
        }
        this._hyperlink.setReference(buf.toString());
    }

    public final void AttachmentReference() throws ParseException {
        this._hyperlink.setType(2);
        StringBuffer buf = new StringBuffer();
        this.jj_consume_token(9);
        block19: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 18: {
                    this.jj_consume_token(18);
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
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 11: {
                    this.jj_consume_token(11);
                    break;
                }
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 16: {
                    this.jj_consume_token(16);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                default: {
                    this.jj_la1[25] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 13: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: {
                    continue block19;
                }
            }
            break;
        }
        this.jj_la1[26] = this.jj_gen;
        this._hyperlink.setReference(buf.toString());
    }

    public final void AnchorReference() throws ParseException {
        this._hyperlink.setType(3);
        StringBuffer buf = new StringBuffer();
        this.jj_consume_token(13);
        block23: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 18: {
                    this.jj_consume_token(18);
                    break;
                }
                case 12: {
                    this.jj_consume_token(12);
                    break;
                }
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
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
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 11: {
                    this.jj_consume_token(11);
                    break;
                }
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 16: {
                    this.jj_consume_token(16);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                default: {
                    this.jj_la1[27] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: {
                    continue block23;
                }
            }
            break;
        }
        this.jj_la1[28] = this.jj_gen;
        this._hyperlink.setAnchor(buf.toString());
    }

    public final void LinkTip() throws ParseException {
        StringBuffer buf = new StringBuffer();
        this.jj_consume_token(3);
        block23: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: {
                    break;
                }
                default: {
                    this.jj_la1[29] = this.jj_gen;
                    break block23;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    break;
                }
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
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
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                case 11: {
                    this.jj_consume_token(11);
                    break;
                }
                case 12: {
                    this.jj_consume_token(12);
                    break;
                }
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 16: {
                    this.jj_consume_token(16);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 18: {
                    this.jj_consume_token(18);
                    break;
                }
                case 19: {
                    this.jj_consume_token(19);
                    break;
                }
                case 20: {
                    this.jj_consume_token(20);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 22: {
                    this.jj_consume_token(22);
                    break;
                }
                default: {
                    this.jj_la1[30] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(this.token.image);
        }
        this._hyperlink.setLinkTip(buf.toString());
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean jj_2_3(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_3();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(2, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean jj_2_4(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_4();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(3, xla);
        }
    }

    private final boolean jj_3_2() {
        return this.jj_3R_13();
    }

    private final boolean jj_3R_21() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(18)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(4)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(5)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(6)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(21)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(22)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(12)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private final boolean jj_3R_18() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(5)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(6)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(7)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(8)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(9)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(13)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(10)) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_scan_token(15)) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_scan_token(16)) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_scan_token(17)) {
                                                    this.jj_scanpos = xsp;
                                                    if (this.jj_scan_token(18)) {
                                                        this.jj_scanpos = xsp;
                                                        if (this.jj_scan_token(19)) {
                                                            this.jj_scanpos = xsp;
                                                            if (this.jj_scan_token(20)) {
                                                                this.jj_scanpos = xsp;
                                                                if (this.jj_scan_token(21)) {
                                                                    this.jj_scanpos = xsp;
                                                                    if (this.jj_scan_token(22)) {
                                                                        return true;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private final boolean jj_3R_14() {
        Token xsp;
        if (this.jj_3R_18()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_18());
        this.jj_scanpos = xsp;
        if (this.jj_scan_token(11)) {
            return true;
        }
        if (this.jj_3R_19()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_19());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_20() {
        return this.jj_scan_token(16);
    }

    private final boolean jj_3R_15() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_20()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_scan_token(16)) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_scan_token(16)) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_scan_token(16)) {
            return true;
        }
        return this.jj_3R_21();
    }

    private final boolean jj_3_3() {
        return this.jj_3R_14();
    }

    private final boolean jj_3R_19() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(5)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(6)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(7)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(8)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(9)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(13)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(10)) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_scan_token(15)) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_scan_token(16)) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_scan_token(17)) {
                                                    this.jj_scanpos = xsp;
                                                    if (this.jj_scan_token(18)) {
                                                        this.jj_scanpos = xsp;
                                                        if (this.jj_scan_token(19)) {
                                                            this.jj_scanpos = xsp;
                                                            if (this.jj_scan_token(20)) {
                                                                this.jj_scanpos = xsp;
                                                                if (this.jj_scan_token(21)) {
                                                                    this.jj_scanpos = xsp;
                                                                    if (this.jj_scan_token(22)) {
                                                                        return true;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private final boolean jj_3R_16() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(18)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(14)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(4)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(5)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(6)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(7)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(8)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(9)) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_scan_token(10)) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_scan_token(11)) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_scan_token(13)) {
                                                    this.jj_scanpos = xsp;
                                                    if (this.jj_scan_token(15)) {
                                                        this.jj_scanpos = xsp;
                                                        if (this.jj_scan_token(16)) {
                                                            this.jj_scanpos = xsp;
                                                            if (this.jj_scan_token(17)) {
                                                                this.jj_scanpos = xsp;
                                                                if (this.jj_scan_token(19)) {
                                                                    this.jj_scanpos = xsp;
                                                                    if (this.jj_scan_token(20)) {
                                                                        this.jj_scanpos = xsp;
                                                                        if (this.jj_scan_token(21)) {
                                                                            this.jj_scanpos = xsp;
                                                                            if (this.jj_scan_token(12)) {
                                                                                this.jj_scanpos = xsp;
                                                                                if (this.jj_scan_token(22)) {
                                                                                    return true;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private final boolean jj_3_1() {
        return this.jj_3R_12();
    }

    private final boolean jj_3R_17() {
        return this.jj_scan_token(21);
    }

    private final boolean jj_3R_12() {
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_16());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(3);
    }

    private final boolean jj_3R_13() {
        Token xsp;
        if (this.jj_3R_17()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_17());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(7);
    }

    private final boolean jj_3_4() {
        return this.jj_3R_15();
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{8, 0x7FFFF0, 0x7FFFF0, 0x200000, 262144, 8704, 8704, 1810944, 256, 6557808, 8370160, 65536, 0x200000, 8368112, 8368112, 8366064, 8366064, 8366064, 8366064, 1671168, 8372208, 8372208, 6557808, 6690160, 6690160, 8368000, 8368000, 8372208, 8372208, 8372208, 8372208};
    }

    public ConfluenceHyperlink(InputStream stream) {
        this(stream, null);
    }

    public ConfluenceHyperlink(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new ConfluenceHyperlinkTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 31; ++i) {
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
        for (i = 0; i < 31; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ConfluenceHyperlink(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ConfluenceHyperlinkTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 31; ++i) {
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
        for (i = 0; i < 31; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ConfluenceHyperlink(ConfluenceHyperlinkTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 31; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(ConfluenceHyperlinkTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 31; ++i) {
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
        boolean[] la1tokens = new boolean[23];
        for (i = 0; i < 23; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 31; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 23; ++i) {
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
        for (int i = 0; i < 4; ++i) {
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
                            break;
                        }
                        case 2: {
                            this.jj_3_3();
                            break;
                        }
                        case 3: {
                            this.jj_3_4();
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
        ConfluenceHyperlink.jj_la1_0();
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

