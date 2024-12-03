/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.dom.IWikiDocument;
import com.atlassian.plugins.conversion.confluence.dom.MacroInfo;
import com.atlassian.plugins.conversion.confluence.dom.TextFormat;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceHyperlink;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceImage;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceMacro;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceTextChunkConstants;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceTextChunkTokenManager;
import com.atlassian.plugins.conversion.confluence.parser.ParseException;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class ConfluenceTextChunk
implements ConfluenceTextChunkConstants {
    ConfluenceMacro _openedMacro;
    MacroInfo _info;
    StringBuffer _macroBody;
    public ConfluenceTextChunkTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1 = new int[55];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;

    public static void parse(String buf, IWikiDocument doc, TextFormat format) throws Exception {
        StringReader reader = new StringReader(buf);
        ConfluenceTextChunk parser = new ConfluenceTextChunk(reader);
        parser.TextChunk(doc, format);
    }

    public static String processLeadToken(String leadToken, StringBuffer buf) {
        int tokLen = leadToken.length();
        if (tokLen > 1 && leadToken.charAt(tokLen - 1) != '}') {
            buf.append(leadToken.charAt(tokLen - 1));
            leadToken = leadToken.substring(0, tokLen - 1);
        }
        return leadToken;
    }

    public void processNestedText(StringBuffer buf, String leadToken, boolean closed, IWikiDocument doc, TextFormat format) throws Exception {
        if (!closed) {
            doc.addText(leadToken, format);
        } else if (leadToken.charAt(0) == ' ') {
            doc.addText(" ", format);
        }
        ConfluenceTextChunk.parse(buf.toString(), doc, format);
        this.token_source.SwitchTo(0);
    }

    public final void TextChunk(IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        block82: {
            boolean leadingSpace = false;
            block74: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 1: {
                        break;
                    }
                    default: {
                        this.jj_la1[0] = this.jj_gen;
                        break block74;
                    }
                }
                this.jj_consume_token(1);
                doc.addText(this.token.image, format);
            }
            block6 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 13: 
                case 15: 
                case 17: 
                case 21: 
                case 23: 
                case 25: 
                case 27: 
                case 29: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 13: {
                            this.jj_consume_token(13);
                            this.token_source.SwitchTo(1);
                            this.BoldChunk(this.token.image, doc, format);
                            break block6;
                        }
                        case 15: {
                            this.jj_consume_token(15);
                            this.token_source.SwitchTo(2);
                            this.EmphasisChunk(this.token.image, doc, format);
                            break block6;
                        }
                        case 17: {
                            this.jj_consume_token(17);
                            this.token_source.SwitchTo(3);
                            this.CitationChunk(this.token.image, doc, format);
                            break block6;
                        }
                        case 21: {
                            this.jj_consume_token(21);
                            this.token_source.SwitchTo(4);
                            this.StrikeChunk(this.token.image, doc, format);
                            break block6;
                        }
                        case 23: {
                            this.jj_consume_token(23);
                            this.token_source.SwitchTo(5);
                            this.UnderlineChunk(this.token.image, doc, format);
                            break block6;
                        }
                        case 25: {
                            this.jj_consume_token(25);
                            this.token_source.SwitchTo(6);
                            this.SuperscriptChunk(this.token.image, doc, format);
                            break block6;
                        }
                        case 27: {
                            this.jj_consume_token(27);
                            this.token_source.SwitchTo(7);
                            this.SubscriptChunk(this.token.image, doc, format);
                            break block6;
                        }
                        case 29: {
                            this.jj_consume_token(29);
                            this.token_source.SwitchTo(8);
                            this.MonoChunk(this.token.image, doc, format);
                            break block6;
                        }
                    }
                    this.jj_la1[1] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                }
            }
            block75: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 9: 
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
                    case 22: 
                    case 23: 
                    case 24: 
                    case 25: 
                    case 26: 
                    case 27: 
                    case 28: 
                    case 29: 
                    case 30: 
                    case 31: 
                    case 32: 
                    case 34: 
                    case 90: 
                    case 91: {
                        break;
                    }
                    default: {
                        this.jj_la1[3] = this.jj_gen;
                        break block82;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 2: 
                    case 14: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 14: {
                                this.jj_consume_token(14);
                                doc.addText(this.token.image.substring(0, 1), format);
                                this.token.image = this.token.image.substring(1);
                                break;
                            }
                            case 2: {
                                this.jj_consume_token(2);
                                break;
                            }
                            default: {
                                this.jj_la1[4] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.token_source.SwitchTo(1);
                        this.BoldChunk(this.token.image, doc, format);
                        continue block75;
                    }
                    case 3: 
                    case 16: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 16: {
                                this.jj_consume_token(16);
                                doc.addText(this.token.image.substring(0, 1), format);
                                this.token.image = this.token.image.substring(1);
                                break;
                            }
                            case 3: {
                                this.jj_consume_token(3);
                                break;
                            }
                            default: {
                                this.jj_la1[5] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.token_source.SwitchTo(2);
                        this.EmphasisChunk(this.token.image, doc, format);
                        continue block75;
                    }
                    case 4: 
                    case 18: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 18: {
                                this.jj_consume_token(18);
                                doc.addText(this.token.image.substring(0, 1), format);
                                this.token.image = this.token.image.substring(1);
                                break;
                            }
                            case 4: {
                                this.jj_consume_token(4);
                                break;
                            }
                            default: {
                                this.jj_la1[6] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.token_source.SwitchTo(3);
                        this.CitationChunk(this.token.image, doc, format);
                        continue block75;
                    }
                    case 5: 
                    case 22: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 22: {
                                this.jj_consume_token(22);
                                doc.addText(this.token.image.substring(0, 1), format);
                                this.token.image = this.token.image.substring(1);
                                break;
                            }
                            case 5: {
                                this.jj_consume_token(5);
                                break;
                            }
                            default: {
                                this.jj_la1[7] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.token_source.SwitchTo(4);
                        this.StrikeChunk(this.token.image, doc, format);
                        continue block75;
                    }
                    case 6: 
                    case 24: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 24: {
                                this.jj_consume_token(24);
                                doc.addText(this.token.image.substring(0, 1), format);
                                this.token.image = this.token.image.substring(1);
                                break;
                            }
                            case 6: {
                                this.jj_consume_token(6);
                                break;
                            }
                            default: {
                                this.jj_la1[8] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.token_source.SwitchTo(5);
                        this.UnderlineChunk(this.token.image, doc, format);
                        continue block75;
                    }
                    case 7: 
                    case 26: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 26: {
                                this.jj_consume_token(26);
                                doc.addText(this.token.image.substring(0, 1), format);
                                this.token.image = this.token.image.substring(1);
                                break;
                            }
                            case 7: {
                                this.jj_consume_token(7);
                                break;
                            }
                            default: {
                                this.jj_la1[9] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.token_source.SwitchTo(6);
                        this.SuperscriptChunk(this.token.image, doc, format);
                        continue block75;
                    }
                    case 8: 
                    case 28: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 28: {
                                this.jj_consume_token(28);
                                doc.addText(this.token.image.substring(0, 1), format);
                                this.token.image = this.token.image.substring(1);
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
                        this.token_source.SwitchTo(7);
                        this.SubscriptChunk(this.token.image, doc, format);
                        continue block75;
                    }
                    case 9: 
                    case 30: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 30: {
                                this.jj_consume_token(30);
                                doc.addText(this.token.image.substring(0, 1), format);
                                this.token.image = this.token.image.substring(1);
                                break;
                            }
                            case 9: {
                                this.jj_consume_token(9);
                                break;
                            }
                            default: {
                                this.jj_la1[11] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.token_source.SwitchTo(8);
                        this.MonoChunk(this.token.image, doc, format);
                        continue block75;
                    }
                    case 90: {
                        this.jj_consume_token(90);
                        try {
                            ConfluenceMacro macro = ConfluenceMacro.parse(this.token.image, doc);
                            if (macro.getName().equalsIgnoreCase(this._openedMacro.getName())) {
                                doc.macroEnd(this._info, macro.getName(), this._macroBody.toString(), this._openedMacro.getDefaultArg(), this._openedMacro.getArgs(), format);
                                this.token_source.SwitchTo(0);
                                continue block75;
                            }
                            this._macroBody.append(this.token.image);
                        }
                        catch (ParseException e) {
                            this._macroBody.append(this.token.image);
                        }
                        continue block75;
                    }
                    case 91: {
                        this.jj_consume_token(91);
                        this._macroBody.append(this.token.image);
                        continue block75;
                    }
                    case 31: {
                        this.jj_consume_token(31);
                        this._openedMacro = ConfluenceMacro.parse(this.token.image, doc);
                        this._info = doc.getMacroInfo(this._openedMacro.getName());
                        if (this._info.hasBody() && this._info.isInline()) {
                            this._macroBody = new StringBuffer();
                            this.token_source.SwitchTo(9);
                            boolean foundEnd = false;
                            int nextIndex = 0;
                            Token t = this.getToken(nextIndex++);
                            while (t.kind != 0) {
                                ConfluenceMacro macro;
                                if (t.kind == 90 && (macro = ConfluenceMacro.parse(t.image, doc)).getName().equalsIgnoreCase(this._openedMacro.getName()) && macro.isEmpty()) {
                                    foundEnd = true;
                                    break;
                                }
                                t = this.getToken(nextIndex++);
                            }
                            if (!foundEnd) {
                                this.token_source.SwitchTo(0);
                                Token tempToken = this.token.next;
                                while (tempToken != null) {
                                    this.jj_input_stream.backup(tempToken.image.length());
                                    tempToken = tempToken.next;
                                }
                                this.token.next = null;
                            }
                        }
                        doc.macroStart(this.token.image, this._openedMacro.getName(), this._openedMacro.getDefaultArg(), this._openedMacro.getArgs(), format, this.token_source.curLexState == 0 && this._info.hasBody());
                        continue block75;
                    }
                    case 12: {
                        this.jj_consume_token(12);
                        try {
                            ConfluenceHyperlink.parse(this.token.image, doc, format);
                        }
                        catch (ParseException e) {
                            doc.addText(this.token.image, format);
                        }
                        continue block75;
                    }
                    case 11: {
                        this.jj_consume_token(11);
                        try {
                            ConfluenceImage.parse(this.token.image, doc);
                        }
                        catch (ParseException e) {
                            doc.addText(this.token.image, format);
                        }
                        continue block75;
                    }
                    case 32: {
                        this.jj_consume_token(32);
                        doc.endSoftParagraph();
                        doc.startParagraph();
                        continue block75;
                    }
                    case 1: 
                    case 13: 
                    case 15: 
                    case 17: 
                    case 19: 
                    case 20: 
                    case 21: 
                    case 23: 
                    case 25: 
                    case 27: 
                    case 29: 
                    case 34: {
                        this.PlainChunk(doc, format);
                        continue block75;
                    }
                }
                break;
            }
            this.jj_la1[12] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        this.jj_consume_token(0);
    }

    public final void PlainChunk(IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        block17: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 34: {
                    this.jj_consume_token(34);
                    break;
                }
                case 1: {
                    this.jj_consume_token(1);
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
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 15: {
                    this.jj_consume_token(15);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    break;
                }
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                case 23: {
                    this.jj_consume_token(23);
                    break;
                }
                case 25: {
                    this.jj_consume_token(25);
                    break;
                }
                case 27: {
                    this.jj_consume_token(27);
                    break;
                }
                case 29: {
                    this.jj_consume_token(29);
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
                case 1: 
                case 13: 
                case 15: 
                case 17: 
                case 19: 
                case 20: 
                case 21: 
                case 23: 
                case 25: 
                case 27: 
                case 29: 
                case 34: {
                    continue block17;
                }
            }
            break;
        }
        this.jj_la1[14] = this.jj_gen;
        doc.addText(buf.toString(), format);
    }

    public final void BoldChunk(String leadToken, IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        leadToken = ConfluenceTextChunk.processLeadToken(leadToken, buf);
        boolean closed = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 36: {
                this.jj_consume_token(36);
                format = format.setBold(true);
                closed = true;
                break;
            }
            default: {
                this.jj_la1[19] = this.jj_gen;
                block19: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 35: 
                        case 36: 
                        case 39: 
                        case 41: {
                            break;
                        }
                        default: {
                            this.jj_la1[15] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 35: {
                            this.jj_consume_token(35);
                            break;
                        }
                        case 41: {
                            this.jj_consume_token(41);
                            break;
                        }
                        case 36: {
                            this.jj_consume_token(36);
                            break;
                        }
                        case 39: {
                            this.jj_consume_token(39);
                            break;
                        }
                        default: {
                            this.jj_la1[16] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 37: 
                    case 38: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 37: {
                                this.jj_consume_token(37);
                                buf.append(this.token.image.charAt(0));
                                break;
                            }
                            case 38: {
                                this.jj_consume_token(38);
                                break;
                            }
                            default: {
                                this.jj_la1[17] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        format = format.setBold(true);
                        closed = true;
                        break block0;
                    }
                }
                this.jj_la1[18] = this.jj_gen;
            }
        }
        this.processNestedText(buf, leadToken, closed, doc, format);
    }

    public final void EmphasisChunk(String leadToken, IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        leadToken = ConfluenceTextChunk.processLeadToken(leadToken, buf);
        boolean closed = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 43: {
                this.jj_consume_token(43);
                format = format.setEmphasis(true);
                closed = true;
                break;
            }
            default: {
                this.jj_la1[24] = this.jj_gen;
                block19: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 43: 
                        case 45: 
                        case 46: 
                        case 48: {
                            break;
                        }
                        default: {
                            this.jj_la1[20] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 46: {
                            this.jj_consume_token(46);
                            break;
                        }
                        case 48: {
                            this.jj_consume_token(48);
                            break;
                        }
                        case 43: {
                            this.jj_consume_token(43);
                            break;
                        }
                        case 45: {
                            this.jj_consume_token(45);
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
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 42: 
                    case 44: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 42: {
                                this.jj_consume_token(42);
                                buf.append(this.token.image.charAt(0));
                                break;
                            }
                            case 44: {
                                this.jj_consume_token(44);
                                break;
                            }
                            default: {
                                this.jj_la1[22] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        format = format.setEmphasis(true);
                        closed = true;
                        break block0;
                    }
                }
                this.jj_la1[23] = this.jj_gen;
            }
        }
        this.processNestedText(buf, leadToken, closed, doc, format);
    }

    public final void CitationChunk(String leadToken, IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        leadToken = ConfluenceTextChunk.processLeadToken(leadToken, buf);
        boolean closed = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 50: {
                this.jj_consume_token(50);
                format = format.setCitation(true);
                closed = true;
                break;
            }
            default: {
                this.jj_la1[29] = this.jj_gen;
                block19: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 50: 
                        case 52: 
                        case 53: 
                        case 55: {
                            break;
                        }
                        default: {
                            this.jj_la1[25] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 53: {
                            this.jj_consume_token(53);
                            break;
                        }
                        case 55: {
                            this.jj_consume_token(55);
                            break;
                        }
                        case 50: {
                            this.jj_consume_token(50);
                            break;
                        }
                        case 52: {
                            this.jj_consume_token(52);
                            break;
                        }
                        default: {
                            this.jj_la1[26] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 49: 
                    case 51: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 49: {
                                this.jj_consume_token(49);
                                buf.append(this.token.image.charAt(0));
                                break;
                            }
                            case 51: {
                                this.jj_consume_token(51);
                                break;
                            }
                            default: {
                                this.jj_la1[27] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        format = format.setCitation(true);
                        closed = true;
                        break block0;
                    }
                }
                this.jj_la1[28] = this.jj_gen;
            }
        }
        this.processNestedText(buf, leadToken, closed, doc, format);
    }

    public final void StrikeChunk(String leadToken, IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        leadToken = ConfluenceTextChunk.processLeadToken(leadToken, buf);
        boolean closed = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 57: {
                this.jj_consume_token(57);
                format = format.setStrikethrough(true);
                closed = true;
                break;
            }
            default: {
                this.jj_la1[34] = this.jj_gen;
                block19: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 57: 
                        case 59: 
                        case 60: 
                        case 62: {
                            break;
                        }
                        default: {
                            this.jj_la1[30] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 60: {
                            this.jj_consume_token(60);
                            break;
                        }
                        case 62: {
                            this.jj_consume_token(62);
                            break;
                        }
                        case 57: {
                            this.jj_consume_token(57);
                            break;
                        }
                        case 59: {
                            this.jj_consume_token(59);
                            break;
                        }
                        default: {
                            this.jj_la1[31] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 56: 
                    case 58: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 56: {
                                this.jj_consume_token(56);
                                buf.append(this.token.image.charAt(0));
                                break;
                            }
                            case 58: {
                                this.jj_consume_token(58);
                                break;
                            }
                            default: {
                                this.jj_la1[32] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        format = format.setStrikethrough(true);
                        closed = true;
                        break block0;
                    }
                }
                this.jj_la1[33] = this.jj_gen;
            }
        }
        this.processNestedText(buf, leadToken, closed, doc, format);
    }

    public final void UnderlineChunk(String leadToken, IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        leadToken = ConfluenceTextChunk.processLeadToken(leadToken, buf);
        boolean closed = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 64: {
                this.jj_consume_token(64);
                format = format.setUnderline(true);
                closed = true;
                break;
            }
            default: {
                this.jj_la1[39] = this.jj_gen;
                block19: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 64: 
                        case 66: 
                        case 67: 
                        case 69: {
                            break;
                        }
                        default: {
                            this.jj_la1[35] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 67: {
                            this.jj_consume_token(67);
                            break;
                        }
                        case 69: {
                            this.jj_consume_token(69);
                            break;
                        }
                        case 64: {
                            this.jj_consume_token(64);
                            break;
                        }
                        case 66: {
                            this.jj_consume_token(66);
                            break;
                        }
                        default: {
                            this.jj_la1[36] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 63: 
                    case 65: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 63: {
                                this.jj_consume_token(63);
                                buf.append(this.token.image.charAt(0));
                                break;
                            }
                            case 65: {
                                this.jj_consume_token(65);
                                break;
                            }
                            default: {
                                this.jj_la1[37] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        format = format.setUnderline(true);
                        closed = true;
                        break block0;
                    }
                }
                this.jj_la1[38] = this.jj_gen;
            }
        }
        this.processNestedText(buf, leadToken, closed, doc, format);
    }

    public final void SuperscriptChunk(String leadToken, IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        leadToken = ConfluenceTextChunk.processLeadToken(leadToken, buf);
        boolean closed = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 71: {
                this.jj_consume_token(71);
                format = format.setSuperscript(true);
                closed = true;
                break;
            }
            default: {
                this.jj_la1[44] = this.jj_gen;
                block19: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 71: 
                        case 73: 
                        case 74: 
                        case 76: {
                            break;
                        }
                        default: {
                            this.jj_la1[40] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 74: {
                            this.jj_consume_token(74);
                            break;
                        }
                        case 76: {
                            this.jj_consume_token(76);
                            break;
                        }
                        case 71: {
                            this.jj_consume_token(71);
                            break;
                        }
                        case 73: {
                            this.jj_consume_token(73);
                            break;
                        }
                        default: {
                            this.jj_la1[41] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 70: 
                    case 72: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 70: {
                                this.jj_consume_token(70);
                                buf.append(this.token.image.charAt(0));
                                break;
                            }
                            case 72: {
                                this.jj_consume_token(72);
                                break;
                            }
                            default: {
                                this.jj_la1[42] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        format = format.setSuperscript(true);
                        closed = true;
                        break block0;
                    }
                }
                this.jj_la1[43] = this.jj_gen;
            }
        }
        this.processNestedText(buf, leadToken, closed, doc, format);
    }

    public final void SubscriptChunk(String leadToken, IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        leadToken = ConfluenceTextChunk.processLeadToken(leadToken, buf);
        boolean closed = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 78: {
                this.jj_consume_token(78);
                format = format.setSubscript(true);
                closed = true;
                break;
            }
            default: {
                this.jj_la1[49] = this.jj_gen;
                block19: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 78: 
                        case 80: 
                        case 81: 
                        case 83: {
                            break;
                        }
                        default: {
                            this.jj_la1[45] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 81: {
                            this.jj_consume_token(81);
                            break;
                        }
                        case 83: {
                            this.jj_consume_token(83);
                            break;
                        }
                        case 78: {
                            this.jj_consume_token(78);
                            break;
                        }
                        case 80: {
                            this.jj_consume_token(80);
                            break;
                        }
                        default: {
                            this.jj_la1[46] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 77: 
                    case 79: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 77: {
                                this.jj_consume_token(77);
                                buf.append(this.token.image.charAt(0));
                                break;
                            }
                            case 79: {
                                this.jj_consume_token(79);
                                break;
                            }
                            default: {
                                this.jj_la1[47] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        format = format.setSubscript(true);
                        closed = true;
                        break block0;
                    }
                }
                this.jj_la1[48] = this.jj_gen;
            }
        }
        this.processNestedText(buf, leadToken, closed, doc, format);
    }

    public final void MonoChunk(String leadToken, IWikiDocument doc, TextFormat format) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        leadToken = ConfluenceTextChunk.processLeadToken(leadToken, buf);
        boolean closed = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 85: {
                this.jj_consume_token(85);
                format = format.setMono(true);
                closed = true;
                break;
            }
            default: {
                this.jj_la1[54] = this.jj_gen;
                block18: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 85: 
                        case 87: 
                        case 89: {
                            break;
                        }
                        default: {
                            this.jj_la1[50] = this.jj_gen;
                            break block18;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 89: {
                            this.jj_consume_token(89);
                            break;
                        }
                        case 85: {
                            this.jj_consume_token(85);
                            break;
                        }
                        case 87: {
                            this.jj_consume_token(87);
                            break;
                        }
                        default: {
                            this.jj_la1[51] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 84: 
                    case 86: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 84: {
                                this.jj_consume_token(84);
                                buf.append(this.token.image.charAt(0));
                                break;
                            }
                            case 86: {
                                this.jj_consume_token(86);
                                break;
                            }
                            default: {
                                this.jj_la1[52] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        format = format.setMono(true);
                        closed = true;
                        break block0;
                    }
                }
                this.jj_la1[53] = this.jj_gen;
            }
        }
        this.processNestedText(buf, leadToken, closed, doc, format);
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{2, 0x2AA2A000, 0x2AA2A000, -1026, 16388, 65544, 262160, 0x400020, 0x1000040, 0x4000080, 0x10000100, 0x40000200, -1026, 716873730, 716873730, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_1() {
        jj_la1_1 = new int[]{0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5, 4, 4, 664, 664, 96, 96, 16, 92160, 92160, 5120, 5120, 2048, 0xB40000, 0xB40000, 655360, 655360, 262144, 0x5A000000, 0x5A000000, 0x5000000, 0x5000000, 0x2000000, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_2() {
        jj_la1_2 = new int[]{0, 0, 0, 0xC000000, 0, 0, 0, 0, 0, 0, 0, 0, 0xC000000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 45, 45, 2, 2, 1, 5760, 5760, 320, 320, 128, 737280, 737280, 40960, 40960, 16384, 0x2A00000, 0x2A00000, 0x500000, 0x500000, 0x200000};
    }

    public ConfluenceTextChunk(InputStream stream) {
        this(stream, null);
    }

    public ConfluenceTextChunk(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new ConfluenceTextChunkTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 55; ++i) {
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
        for (int i = 0; i < 55; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public ConfluenceTextChunk(Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ConfluenceTextChunkTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 55; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 55; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public ConfluenceTextChunk(ConfluenceTextChunkTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 55; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(ConfluenceTextChunkTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 55; ++i) {
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
        boolean[] la1tokens = new boolean[92];
        for (i = 0; i < 92; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 55; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) != 0) {
                    la1tokens[32 + j] = true;
                }
                if ((jj_la1_2[i] & 1 << j) == 0) continue;
                la1tokens[64 + j] = true;
            }
        }
        for (i = 0; i < 92; ++i) {
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
        ConfluenceTextChunk.jj_la1_0();
        ConfluenceTextChunk.jj_la1_1();
        ConfluenceTextChunk.jj_la1_2();
    }
}

