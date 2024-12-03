/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.dom.IWikiDocument;
import com.atlassian.plugins.conversion.confluence.dom.MacroInfo;
import com.atlassian.plugins.conversion.confluence.dom.TextFormat;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceDocumentConstants;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceDocumentTokenManager;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceMacro;
import com.atlassian.plugins.conversion.confluence.parser.ConfluencePreParser;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceTextChunk;
import com.atlassian.plugins.conversion.confluence.parser.ParseException;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;

public class ConfluenceDocument
implements ConfluenceDocumentConstants {
    public StringBuffer _macroBody;
    public MacroInfo _info;
    public ConfluenceMacro _openedMacro;
    public String _originalMacro;
    public ConfluenceDocumentTokenManager token_source;
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
    private final int[] jj_la1 = new int[59];
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns = new JJCalls[7];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public static void parse(Reader in, IWikiDocument doc) throws Exception {
        ConfluenceDocument parser = new ConfluenceDocument(ConfluencePreParser.parse(in));
        parser.Document(doc);
    }

    private static void handleNumberedList(IWikiDocument doc, String token) throws Exception {
        doc.startNumberedParagraph(token.trim().length());
    }

    private static void handleBulletedList(IWikiDocument doc, String token) throws Exception {
        doc.startBulletedParagraph(token.trim().length());
    }

    public final void Document(IWikiDocument doc) throws ParseException, Exception {
        block13: {
            doc.startDocument();
            block9: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 0: 
                    case 1: 
                    case 2: 
                    case 3: 
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
                    case 18: {
                        break;
                    }
                    default: {
                        this.jj_la1[0] = this.jj_gen;
                        break block13;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 0: {
                        this.jj_consume_token(0);
                        doc.endDocument();
                        return;
                    }
                }
                this.jj_la1[1] = this.jj_gen;
                if (this.jj_2_1(2)) {
                    this.Table(doc);
                    continue;
                }
                if (this.jj_2_2(3)) {
                    this.HorizontalRule(doc);
                    continue;
                }
                if (this.jj_2_3(2)) {
                    this.EmptyParagraph();
                    continue;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 0: 
                    case 1: 
                    case 2: 
                    case 3: 
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
                    case 18: {
                        this.Paragraph(doc);
                        continue block9;
                    }
                }
                break;
            }
            this.jj_la1[2] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        this.jj_consume_token(0);
        doc.endDocument();
    }

    public final void HorizontalRule(IWikiDocument doc) throws ParseException, Exception {
        doc.horizontalRule();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4: {
                this.jj_consume_token(4);
                break;
            }
            default: {
                this.jj_la1[3] = this.jj_gen;
            }
        }
        this.jj_consume_token(15);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4: {
                this.jj_consume_token(4);
                break;
            }
            default: {
                this.jj_la1[4] = this.jj_gen;
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 3: {
                this.jj_consume_token(3);
                break;
            }
            case 0: {
                this.jj_consume_token(0);
                break;
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void EmptyParagraph() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4: {
                this.jj_consume_token(4);
                break;
            }
            default: {
                this.jj_la1[6] = this.jj_gen;
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 3: {
                this.jj_consume_token(3);
                break;
            }
            case 0: {
                this.jj_consume_token(0);
                break;
            }
            default: {
                this.jj_la1[7] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void Table(IWikiDocument doc) throws ParseException, Exception {
        doc.startTable();
        do {
            this.TableRow(doc);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 0: {
                    this.jj_consume_token(0);
                    doc.endTable();
                    return;
                }
            }
            this.jj_la1[8] = this.jj_gen;
        } while (this.jj_2_4(2));
        doc.endTable();
    }

    public final void TableRow(IWikiDocument doc) throws ParseException, Exception {
        doc.startTableRow();
        boolean header = false;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 4: {
                this.jj_consume_token(4);
                break;
            }
            default: {
                this.jj_la1[9] = this.jj_gen;
            }
        }
        this.jj_consume_token(13);
        block37: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 13: {
                    break;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                    break block37;
                }
            }
            this.jj_consume_token(13);
            header = true;
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 14: 
            case 15: 
            case 16: {
                this.TableCell(doc, header);
                break;
            }
            default: {
                this.jj_la1[11] = this.jj_gen;
            }
        }
        header = false;
        block38: while (true) {
            if (this.jj_2_5(4)) {
                block9 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 13: {
                        this.jj_consume_token(13);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 13: {
                                this.jj_consume_token(13);
                                break;
                            }
                            default: {
                                this.jj_la1[12] = this.jj_gen;
                            }
                        }
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 4: {
                                this.jj_consume_token(4);
                                break block9;
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
                    case 3: {
                        this.jj_consume_token(3);
                        break;
                    }
                    case 0: {
                        this.jj_consume_token(0);
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                doc.endTableRow();
                return;
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 13: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 4: {
                            this.jj_consume_token(4);
                            break;
                        }
                        default: {
                            this.jj_la1[16] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(13);
                    block39: while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 13: {
                                break;
                            }
                            default: {
                                this.jj_la1[17] = this.jj_gen;
                                break block39;
                            }
                        }
                        this.jj_consume_token(13);
                        header = true;
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 1: 
                        case 2: 
                        case 4: 
                        case 5: 
                        case 6: 
                        case 7: 
                        case 8: 
                        case 9: 
                        case 10: 
                        case 11: 
                        case 12: 
                        case 14: 
                        case 15: 
                        case 16: {
                            this.TableCell(doc, header);
                            break;
                        }
                        default: {
                            this.jj_la1[18] = this.jj_gen;
                        }
                    }
                    header = false;
                    break;
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 0: 
                case 3: 
                case 4: 
                case 13: {
                    continue block38;
                }
            }
            break;
        }
        this.jj_la1[20] = this.jj_gen;
    }

    public final void Paragraph(IWikiDocument doc) throws ParseException, Exception {
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 9: {
                this.ParagraphBodyWithMacroAtStart(doc);
                break;
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: {
                this.ParagraphBody(doc);
                doc.endSoftParagraph();
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 17: 
                    case 18: {
                        this.MacroBody(doc);
                        break block0;
                    }
                    case 3: {
                        this.jj_consume_token(3);
                        break block0;
                    }
                    case 11: {
                        this.jj_consume_token(11);
                        break block0;
                    }
                    case 0: {
                        this.jj_consume_token(0);
                        break block0;
                    }
                }
                this.jj_la1[21] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[22] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void TableCell(IWikiDocument doc, boolean isHeader) throws ParseException, Exception {
        doc.startTableCell(isHeader);
        block14: while (true) {
            block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 11: {
                    this.jj_consume_token(11);
                    doc.startParagraph();
                    doc.endSoftParagraph();
                    break;
                }
                case 1: 
                case 2: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 12: 
                case 14: 
                case 15: 
                case 16: {
                    this.TableParagraph(doc);
                    if (!this.jj_2_7(2)) break;
                    if (this.jj_2_6(2)) {
                        this.jj_consume_token(3);
                        this.TableParagraph(doc);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 3: {
                                this.jj_consume_token(3);
                                break block0;
                            }
                        }
                        this.jj_la1[23] = this.jj_gen;
                        break;
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 3: {
                            this.jj_consume_token(3);
                            this.jj_consume_token(11);
                            doc.startParagraph();
                            doc.endSoftParagraph();
                            break block0;
                        }
                        case 11: {
                            this.jj_consume_token(11);
                            break block0;
                        }
                    }
                    this.jj_la1[24] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                default: {
                    this.jj_la1[25] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 14: 
                case 15: 
                case 16: {
                    continue block14;
                }
            }
            break;
        }
        this.jj_la1[26] = this.jj_gen;
        doc.endTableCell();
    }

    public final void TableParagraph(IWikiDocument doc) throws ParseException, Exception {
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 9: {
                this.TableParagraphBodyWithMacroAtStart(doc);
                break;
            }
            case 1: 
            case 2: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 10: 
            case 12: 
            case 14: 
            case 15: 
            case 16: {
                this.TableParagraphBody(doc);
                doc.endSoftParagraph();
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 17: 
                    case 18: {
                        this.MacroBody(doc);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 1: 
                            case 2: 
                            case 4: 
                            case 5: 
                            case 6: 
                            case 7: 
                            case 8: 
                            case 9: 
                            case 10: 
                            case 12: 
                            case 14: 
                            case 15: 
                            case 16: {
                                this.TableParagraphBody(doc);
                                doc.endSoftParagraph();
                                break block0;
                            }
                        }
                        this.jj_la1[27] = this.jj_gen;
                        break block0;
                    }
                }
                this.jj_la1[28] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[29] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void ParagraphBody(IWikiDocument doc) throws ParseException, Exception {
        boolean containsList = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        ConfluenceDocument.handleBulletedList(doc, this.token.image);
                        containsList = true;
                        break block0;
                    }
                    case 2: {
                        this.jj_consume_token(2);
                        ConfluenceDocument.handleNumberedList(doc, this.token.image);
                        containsList = true;
                        break block0;
                    }
                }
                this.jj_la1[30] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[31] = this.jj_gen;
            }
        }
        if (!containsList) {
            doc.startParagraph();
        }
        block7 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 5: 
            case 14: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 5: {
                        this.jj_consume_token(5);
                        doc.setHeading(this.token.image);
                        break block7;
                    }
                    case 14: {
                        this.jj_consume_token(14);
                        doc.setBlockQuote();
                        break block7;
                    }
                }
                this.jj_la1[32] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[33] = this.jj_gen;
            }
        }
        block17: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: {
                    break;
                }
                default: {
                    this.jj_la1[34] = this.jj_gen;
                    break block17;
                }
            }
            StringBuffer buf = new StringBuffer();
            this.TextChunk(doc, buf);
            ConfluenceTextChunk.parse(buf.toString(), doc, new TextFormat());
        }
    }

    public final void ParagraphBodyWithMacroAtStart(IWikiDocument doc) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        this.BigMacro(doc, buf);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 1: 
                    case 2: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 9: 
                    case 10: 
                    case 12: 
                    case 13: 
                    case 14: 
                    case 15: 
                    case 16: {
                        this.TextChunk(doc, buf);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 0: 
                            case 3: 
                            case 11: {
                                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                    case 3: {
                                        this.jj_consume_token(3);
                                        break block0;
                                    }
                                    case 11: {
                                        this.jj_consume_token(11);
                                        break block0;
                                    }
                                    case 0: {
                                        this.jj_consume_token(0);
                                        break block0;
                                    }
                                }
                                this.jj_la1[35] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        this.jj_la1[36] = this.jj_gen;
                        break block0;
                    }
                    case 17: 
                    case 18: {
                        this.MacroBody(doc);
                        break block0;
                    }
                }
                this.jj_la1[37] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[38] = this.jj_gen;
            }
        }
        if (buf.length() > 0) {
            doc.startParagraph();
            ConfluenceTextChunk.parse(buf.toString(), doc, new TextFormat());
            doc.endSoftParagraph();
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 3: {
                this.jj_consume_token(3);
                break;
            }
            default: {
                this.jj_la1[39] = this.jj_gen;
            }
        }
    }

    public final void TableParagraphBodyWithMacroAtStart(IWikiDocument doc) throws ParseException, Exception {
        StringBuffer buf = new StringBuffer();
        this.BigMacro(doc, buf);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 12: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 1: 
                    case 2: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 9: 
                    case 10: 
                    case 12: 
                    case 14: 
                    case 15: 
                    case 16: {
                        this.TableTextChunk(doc, buf);
                        break block0;
                    }
                    case 17: 
                    case 18: {
                        this.MacroBody(doc);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 1: 
                            case 2: 
                            case 4: 
                            case 5: 
                            case 6: 
                            case 7: 
                            case 8: 
                            case 9: 
                            case 10: 
                            case 12: 
                            case 14: 
                            case 15: 
                            case 16: {
                                this.TableTextChunk(doc, buf);
                                break block0;
                            }
                        }
                        this.jj_la1[40] = this.jj_gen;
                        break block0;
                    }
                }
                this.jj_la1[41] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[42] = this.jj_gen;
            }
        }
        if (buf.length() > 0) {
            doc.startParagraph();
            ConfluenceTextChunk.parse(buf.toString(), doc, new TextFormat());
            doc.endSoftParagraph();
        }
    }

    public final void TextChunk(IWikiDocument doc, StringBuffer buf) throws ParseException, Exception {
        block22: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 10: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 16: {
                            this.jj_consume_token(16);
                            break;
                        }
                        case 1: {
                            this.jj_consume_token(1);
                            break;
                        }
                        case 15: {
                            this.jj_consume_token(15);
                            break;
                        }
                        case 4: {
                            this.jj_consume_token(4);
                            break;
                        }
                        case 12: {
                            this.jj_consume_token(12);
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
                        case 13: {
                            this.jj_consume_token(13);
                            break;
                        }
                        case 10: {
                            this.jj_consume_token(10);
                            break;
                        }
                        case 8: {
                            this.jj_consume_token(8);
                            break;
                        }
                        case 2: {
                            this.jj_consume_token(2);
                            break;
                        }
                        case 14: {
                            this.jj_consume_token(14);
                            break;
                        }
                        case 5: {
                            this.jj_consume_token(5);
                            break;
                        }
                        default: {
                            this.jj_la1[43] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                    break;
                }
                case 9: {
                    this.BigMacro(doc, buf);
                    break;
                }
                default: {
                    this.jj_la1[44] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: {
                    continue block22;
                }
            }
            break;
        }
        this.jj_la1[45] = this.jj_gen;
    }

    public final void TableParagraphBody(IWikiDocument doc) throws ParseException, Exception {
        boolean containsList = false;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 1: {
                        this.jj_consume_token(1);
                        ConfluenceDocument.handleBulletedList(doc, this.token.image);
                        containsList = true;
                        break;
                    }
                    case 2: {
                        this.jj_consume_token(2);
                        ConfluenceDocument.handleNumberedList(doc, this.token.image);
                        containsList = true;
                        break;
                    }
                    default: {
                        this.jj_la1[46] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                if (!containsList) {
                    doc.startParagraph();
                }
                block9 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 5: 
                    case 14: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 5: {
                                this.jj_consume_token(5);
                                doc.setHeading(this.token.image);
                                break block9;
                            }
                            case 14: {
                                this.jj_consume_token(14);
                                doc.setBlockQuote();
                                break block9;
                            }
                        }
                        this.jj_la1[47] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                    default: {
                        this.jj_la1[48] = this.jj_gen;
                    }
                }
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 1: 
                        case 2: 
                        case 4: 
                        case 5: 
                        case 6: 
                        case 7: 
                        case 8: 
                        case 9: 
                        case 10: 
                        case 12: 
                        case 14: 
                        case 15: 
                        case 16: {
                            break;
                        }
                        default: {
                            this.jj_la1[49] = this.jj_gen;
                            break block0;
                        }
                    }
                    StringBuffer buf = new StringBuffer();
                    this.TableTextChunk(doc, buf);
                    ConfluenceTextChunk.parse(buf.toString(), doc, new TextFormat());
                }
            }
            case 5: 
            case 14: {
                doc.startParagraph();
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 5: {
                        this.jj_consume_token(5);
                        doc.setHeading(this.token.image);
                        break;
                    }
                    case 14: {
                        this.jj_consume_token(14);
                        doc.setBlockQuote();
                        break;
                    }
                    default: {
                        this.jj_la1[50] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 1: 
                        case 2: 
                        case 4: 
                        case 5: 
                        case 6: 
                        case 7: 
                        case 8: 
                        case 9: 
                        case 10: 
                        case 12: 
                        case 14: 
                        case 15: 
                        case 16: {
                            break;
                        }
                        default: {
                            this.jj_la1[51] = this.jj_gen;
                            break block0;
                        }
                    }
                    StringBuffer buf = new StringBuffer();
                    this.TableTextChunk(doc, buf);
                    ConfluenceTextChunk.parse(buf.toString(), doc, new TextFormat());
                }
            }
            case 4: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 12: 
            case 15: 
            case 16: {
                block31: while (true) {
                    StringBuffer buf = new StringBuffer();
                    doc.startParagraph();
                    this.TableTextChunk(doc, buf);
                    ConfluenceTextChunk.parse(buf.toString(), doc, new TextFormat());
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 1: 
                        case 2: 
                        case 4: 
                        case 5: 
                        case 6: 
                        case 7: 
                        case 8: 
                        case 9: 
                        case 10: 
                        case 12: 
                        case 14: 
                        case 15: 
                        case 16: {
                            continue block31;
                        }
                    }
                    break;
                }
                this.jj_la1[52] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[53] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void TableTextChunk(IWikiDocument doc, StringBuffer buf) throws ParseException, Exception {
        block21: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 10: 
                case 12: 
                case 14: 
                case 15: 
                case 16: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 16: {
                            this.jj_consume_token(16);
                            break;
                        }
                        case 1: {
                            this.jj_consume_token(1);
                            break;
                        }
                        case 15: {
                            this.jj_consume_token(15);
                            break;
                        }
                        case 4: {
                            this.jj_consume_token(4);
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
                        case 12: {
                            this.jj_consume_token(12);
                            break;
                        }
                        case 10: {
                            this.jj_consume_token(10);
                            break;
                        }
                        case 8: {
                            this.jj_consume_token(8);
                            break;
                        }
                        case 14: {
                            this.jj_consume_token(14);
                            break;
                        }
                        case 2: {
                            this.jj_consume_token(2);
                            break;
                        }
                        case 5: {
                            this.jj_consume_token(5);
                            break;
                        }
                        default: {
                            this.jj_la1[54] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    buf.append(this.token.image);
                    break;
                }
                case 9: {
                    this.BigMacro(doc, buf);
                    break;
                }
                default: {
                    this.jj_la1[55] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 12: 
                case 14: 
                case 15: 
                case 16: {
                    continue block21;
                }
            }
            break;
        }
        this.jj_la1[56] = this.jj_gen;
    }

    public final void BigMacro(IWikiDocument doc, StringBuffer buf) throws ParseException, Exception {
        block10: {
            this.jj_consume_token(9);
            try {
                this._openedMacro = ConfluenceMacro.parse(this.token.image, doc);
                this._info = doc.getMacroInfo(this._openedMacro.getName());
                this._originalMacro = this.token.image;
                if (this._info.hasBody() && !this._info.isInline()) {
                    this._macroBody = new StringBuffer();
                    this.token_source.SwitchTo(1);
                    boolean foundEnd = false;
                    int nextIndex = 0;
                    Token t = this.getToken(nextIndex++);
                    while (t.kind != 0) {
                        if (t.kind == 17) {
                            try {
                                ConfluenceMacro macro = ConfluenceMacro.parse(t.image, doc);
                                if (macro.getName().equalsIgnoreCase(this._openedMacro.getName()) && macro.isEmpty()) {
                                    foundEnd = true;
                                    break;
                                }
                            }
                            catch (ParseException macro) {
                                // empty catch block
                            }
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
                        buf.append(this.token.image);
                    }
                    break block10;
                }
                buf.append(this.token.image);
            }
            catch (ParseException e) {
                buf.append(this.token.image);
            }
        }
    }

    public final void MacroBody(IWikiDocument doc) throws ParseException, Exception {
        doc.macroStart(this._originalMacro, this._openedMacro.getName(), this._openedMacro.getDefaultArg(), this._openedMacro.getArgs(), new TextFormat(), false);
        block9: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 17: {
                    this.jj_consume_token(17);
                    try {
                        ConfluenceMacro macro = ConfluenceMacro.parse(this.token.image, doc);
                        if (macro.getName().equalsIgnoreCase(this._openedMacro.getName())) {
                            doc.macroEnd(this._info, macro.getName(), this._macroBody.toString(), this._openedMacro.getDefaultArg(), this._openedMacro.getArgs(), new TextFormat());
                            this.token_source.SwitchTo(0);
                            break;
                        }
                        this._macroBody.append(this.token.image);
                    }
                    catch (ParseException e) {
                        this._macroBody.append(this.token.image);
                    }
                    break;
                }
                case 18: {
                    this.jj_consume_token(18);
                    this._macroBody.append(this.token.image);
                    break;
                }
                default: {
                    this.jj_la1[57] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 17: 
                case 18: {
                    continue block9;
                }
            }
            break;
        }
        this.jj_la1[58] = this.jj_gen;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean jj_2_5(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_5();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(4, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean jj_2_6(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_6();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(5, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean jj_2_7(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_7();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(6, xla);
        }
    }

    private final boolean jj_3R_20() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        return this.jj_scan_token(11);
    }

    private final boolean jj_3R_45() {
        return this.jj_3R_31();
    }

    private final boolean jj_3R_23() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3_5()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_27()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3_5() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_18()) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(3)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(0)) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_27() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(13);
    }

    private final boolean jj_3R_17() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(13)) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_21());
        xsp = this.jj_scanpos = xsp;
        if (this.jj_3R_22()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_3R_23()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_23());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_28() {
        return this.jj_3R_31();
    }

    private final boolean jj_3R_39() {
        return this.jj_scan_token(5);
    }

    private final boolean jj_3_4() {
        return this.jj_3R_17();
    }

    private final boolean jj_3R_14() {
        Token xsp;
        if (this.jj_3_4()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3_4());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_44() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(16)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(1)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(15)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(4)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(6)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(7)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(12)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(10)) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_scan_token(8)) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_scan_token(14)) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_scan_token(2)) {
                                                    this.jj_scanpos = xsp;
                                                    if (this.jj_scan_token(5)) {
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
        return false;
    }

    private final boolean jj_3R_43() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_44()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_45()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_42() {
        Token xsp;
        if (this.jj_3R_43()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_43());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_16() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(3)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(0)) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_41() {
        return this.jj_3R_42();
    }

    private final boolean jj_3R_34() {
        Token xsp;
        if (this.jj_3R_41()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_41());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_15() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(15)) {
            return true;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(3)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(0)) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_33() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_39()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_40()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3_3() {
        return this.jj_3R_16();
    }

    private final boolean jj_3_6() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private final boolean jj_3_2() {
        return this.jj_3R_15();
    }

    private final boolean jj_3_1() {
        return this.jj_3R_14();
    }

    private final boolean jj_3R_37() {
        return this.jj_scan_token(1);
    }

    private final boolean jj_3R_38() {
        return this.jj_scan_token(2);
    }

    private final boolean jj_3R_29() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_32()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_33()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_34()) {
                    return true;
                }
            }
        }
        return false;
    }

    private final boolean jj_3R_32() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_37()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_38()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3_7() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3_6()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_20()) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(11)) {
                    return true;
                }
            }
        }
        return false;
    }

    private final boolean jj_3R_22() {
        return this.jj_3R_26();
    }

    private final boolean jj_3R_40() {
        return this.jj_scan_token(14);
    }

    private final boolean jj_3R_25() {
        return this.jj_3R_29();
    }

    private final boolean jj_3R_19() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_24()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_25()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_24() {
        return this.jj_3R_28();
    }

    private final boolean jj_3R_36() {
        return this.jj_3R_19();
    }

    private final boolean jj_3R_30() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_35()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_36()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_35() {
        return this.jj_scan_token(11);
    }

    private final boolean jj_3R_26() {
        Token xsp;
        if (this.jj_3R_30()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_30());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_21() {
        return this.jj_scan_token(13);
    }

    private final boolean jj_3R_18() {
        if (this.jj_scan_token(13)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(13)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(4)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private final boolean jj_3R_31() {
        return this.jj_scan_token(9);
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{524287, 1, 524287, 16, 16, 9, 16, 9, 1, 16, 8192, 122870, 8192, 16, 8192, 9, 16, 8192, 122870, 8208, 8217, 395273, 524287, 8, 2056, 122870, 122870, 120822, 393216, 120822, 6, 6, 16416, 16416, 129014, 2057, 2057, 522230, 522230, 8, 120822, 514038, 514038, 128502, 129014, 129014, 6, 16416, 16416, 120822, 16416, 120822, 120822, 120822, 120310, 120822, 120822, 393216, 393216};
    }

    public ConfluenceDocument(InputStream stream) {
        this(stream, null);
    }

    public ConfluenceDocument(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new ConfluenceDocumentTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 59; ++i) {
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
        for (i = 0; i < 59; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ConfluenceDocument(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ConfluenceDocumentTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 59; ++i) {
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
        for (i = 0; i < 59; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ConfluenceDocument(ConfluenceDocumentTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 59; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(ConfluenceDocumentTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 59; ++i) {
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
        boolean[] la1tokens = new boolean[19];
        for (i = 0; i < 19; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 59; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 19; ++i) {
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
        for (int i = 0; i < 7; ++i) {
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
                            break;
                        }
                        case 4: {
                            this.jj_3_5();
                            break;
                        }
                        case 5: {
                            this.jj_3_6();
                            break;
                        }
                        case 6: {
                            this.jj_3_7();
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
        ConfluenceDocument.jj_la1_0();
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

