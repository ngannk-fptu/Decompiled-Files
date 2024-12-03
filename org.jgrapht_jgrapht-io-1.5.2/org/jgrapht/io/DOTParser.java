/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.NoViableAltException
 *  org.antlr.v4.runtime.Parser
 *  org.antlr.v4.runtime.ParserRuleContext
 *  org.antlr.v4.runtime.RecognitionException
 *  org.antlr.v4.runtime.RuntimeMetaData
 *  org.antlr.v4.runtime.TokenStream
 *  org.antlr.v4.runtime.Vocabulary
 *  org.antlr.v4.runtime.VocabularyImpl
 *  org.antlr.v4.runtime.atn.ATN
 *  org.antlr.v4.runtime.atn.ATNDeserializer
 *  org.antlr.v4.runtime.atn.ParserATNSimulator
 *  org.antlr.v4.runtime.atn.PredictionContextCache
 *  org.antlr.v4.runtime.dfa.DFA
 *  org.antlr.v4.runtime.tree.ParseTreeListener
 *  org.antlr.v4.runtime.tree.TerminalNode
 */
package org.jgrapht.io;

import java.util.List;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jgrapht.io.DOTListener;

class DOTParser
extends Parser {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int T__0 = 1;
    public static final int T__1 = 2;
    public static final int T__2 = 3;
    public static final int T__3 = 4;
    public static final int T__4 = 5;
    public static final int T__5 = 6;
    public static final int T__6 = 7;
    public static final int T__7 = 8;
    public static final int T__8 = 9;
    public static final int T__9 = 10;
    public static final int STRICT = 11;
    public static final int GRAPH = 12;
    public static final int DIGRAPH = 13;
    public static final int NODE = 14;
    public static final int EDGE = 15;
    public static final int SUBGRAPH = 16;
    public static final int Numeral = 17;
    public static final int String = 18;
    public static final int Id = 19;
    public static final int HtmlString = 20;
    public static final int WS = 21;
    public static final int COMMENT = 22;
    public static final int LINE_COMMENT = 23;
    public static final int PREPROC = 24;
    public static final int RULE_graph = 0;
    public static final int RULE_compoundStatement = 1;
    public static final int RULE_graphHeader = 2;
    public static final int RULE_graphIdentifier = 3;
    public static final int RULE_statement = 4;
    public static final int RULE_identifierPairStatement = 5;
    public static final int RULE_attributeStatement = 6;
    public static final int RULE_attributesList = 7;
    public static final int RULE_aList = 8;
    public static final int RULE_edgeStatement = 9;
    public static final int RULE_nodeStatement = 10;
    public static final int RULE_nodeStatementNoAttributes = 11;
    public static final int RULE_nodeIdentifier = 12;
    public static final int RULE_port = 13;
    public static final int RULE_subgraphStatement = 14;
    public static final int RULE_identifierPair = 15;
    public static final int RULE_identifier = 16;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u0001\u0018\u0086\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001)\b\u0001\u0005\u0001+\b\u0001\n\u0001\f\u0001.\t\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0003\u00023\b\u0002\u0001\u0002\u0001\u0002\u0003\u00027\b\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004@\b\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0003\u0007I\b\u0007\u0001\u0007\u0004\u0007L\b\u0007\u000b\u0007\f\u0007M\u0001\b\u0001\b\u0003\bR\b\b\u0004\bT\b\b\u000b\b\f\bU\u0001\t\u0001\t\u0003\tZ\b\t\u0001\t\u0001\t\u0001\t\u0003\t_\b\t\u0004\ta\b\t\u000b\t\f\tb\u0001\t\u0003\tf\b\t\u0001\n\u0001\n\u0003\nj\b\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0003\fp\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0003\rv\b\r\u0001\u000e\u0001\u000e\u0003\u000ez\b\u000e\u0003\u000e|\b\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0000\u0000\u0011\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \u0000\u0005\u0001\u0000\f\r\u0002\u0000\f\f\u000e\u000f\u0002\u0000\u0002\u0002\u0006\u0006\u0001\u0000\u0007\b\u0001\u0000\u0011\u0014\u0089\u0000\"\u0001\u0000\u0000\u0000\u0002%\u0001\u0000\u0000\u0000\u00042\u0001\u0000\u0000\u0000\u00068\u0001\u0000\u0000\u0000\b?\u0001\u0000\u0000\u0000\nA\u0001\u0000\u0000\u0000\fC\u0001\u0000\u0000\u0000\u000eK\u0001\u0000\u0000\u0000\u0010S\u0001\u0000\u0000\u0000\u0012Y\u0001\u0000\u0000\u0000\u0014g\u0001\u0000\u0000\u0000\u0016k\u0001\u0000\u0000\u0000\u0018m\u0001\u0000\u0000\u0000\u001aq\u0001\u0000\u0000\u0000\u001c{\u0001\u0000\u0000\u0000\u001e\u007f\u0001\u0000\u0000\u0000 \u0083\u0001\u0000\u0000\u0000\"#\u0003\u0004\u0002\u0000#$\u0003\u0002\u0001\u0000$\u0001\u0001\u0000\u0000\u0000%,\u0005\u0001\u0000\u0000&(\u0003\b\u0004\u0000')\u0005\u0002\u0000\u0000('\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)+\u0001\u0000\u0000\u0000*&\u0001\u0000\u0000\u0000+.\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-/\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000/0\u0005\u0003\u0000\u00000\u0003\u0001\u0000\u0000\u000013\u0005\u000b\u0000\u000021\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u000046\u0007\u0000\u0000\u000057\u0003\u0006\u0003\u000065\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u00007\u0005\u0001\u0000\u0000\u000089\u0003 \u0010\u00009\u0007\u0001\u0000\u0000\u0000:@\u0003\u0014\n\u0000;@\u0003\u0012\t\u0000<@\u0003\f\u0006\u0000=@\u0003\n\u0005\u0000>@\u0003\u001c\u000e\u0000?:\u0001\u0000\u0000\u0000?;\u0001\u0000\u0000\u0000?<\u0001\u0000\u0000\u0000?=\u0001\u0000\u0000\u0000?>\u0001\u0000\u0000\u0000@\t\u0001\u0000\u0000\u0000AB\u0003\u001e\u000f\u0000B\u000b\u0001\u0000\u0000\u0000CD\u0007\u0001\u0000\u0000DE\u0003\u000e\u0007\u0000E\r\u0001\u0000\u0000\u0000FH\u0005\u0004\u0000\u0000GI\u0003\u0010\b\u0000HG\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JL\u0005\u0005\u0000\u0000KF\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000N\u000f\u0001\u0000\u0000\u0000OQ\u0003\u001e\u000f\u0000PR\u0007\u0002\u0000\u0000QP\u0001\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000RT\u0001\u0000\u0000\u0000SO\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000US\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000V\u0011\u0001\u0000\u0000\u0000WZ\u0003\u0016\u000b\u0000XZ\u0003\u001c\u000e\u0000YW\u0001\u0000\u0000\u0000YX\u0001\u0000\u0000\u0000Z`\u0001\u0000\u0000\u0000[^\u0007\u0003\u0000\u0000\\_\u0003\u0016\u000b\u0000]_\u0003\u001c\u000e\u0000^\\\u0001\u0000\u0000\u0000^]\u0001\u0000\u0000\u0000_a\u0001\u0000\u0000\u0000`[\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000ce\u0001\u0000\u0000\u0000df\u0003\u000e\u0007\u0000ed\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000f\u0013\u0001\u0000\u0000\u0000gi\u0003\u0018\f\u0000hj\u0003\u000e\u0007\u0000ih\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000j\u0015\u0001\u0000\u0000\u0000kl\u0003\u0018\f\u0000l\u0017\u0001\u0000\u0000\u0000mo\u0003 \u0010\u0000np\u0003\u001a\r\u0000on\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000p\u0019\u0001\u0000\u0000\u0000qr\u0005\t\u0000\u0000ru\u0003 \u0010\u0000st\u0005\t\u0000\u0000tv\u0003 \u0010\u0000us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000v\u001b\u0001\u0000\u0000\u0000wy\u0005\u0010\u0000\u0000xz\u0003 \u0010\u0000yx\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z|\u0001\u0000\u0000\u0000{w\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}~\u0003\u0002\u0001\u0000~\u001d\u0001\u0000\u0000\u0000\u007f\u0080\u0003 \u0010\u0000\u0080\u0081\u0005\n\u0000\u0000\u0081\u0082\u0003 \u0010\u0000\u0082\u001f\u0001\u0000\u0000\u0000\u0083\u0084\u0007\u0004\u0000\u0000\u0084!\u0001\u0000\u0000\u0000\u0012(,26?HMQUY^beiouy{";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"graph", "compoundStatement", "graphHeader", "graphIdentifier", "statement", "identifierPairStatement", "attributeStatement", "attributesList", "aList", "edgeStatement", "nodeStatement", "nodeStatementNoAttributes", "nodeIdentifier", "port", "subgraphStatement", "identifierPair", "identifier"};
    }

    private static String[] makeLiteralNames() {
        return new String[]{null, "'{'", "';'", "'}'", "'['", "']'", "','", "'->'", "'--'", "':'", "'='"};
    }

    private static String[] makeSymbolicNames() {
        return new String[]{null, null, null, null, null, null, null, null, null, null, null, "STRICT", "GRAPH", "DIGRAPH", "NODE", "EDGE", "SUBGRAPH", "Numeral", "String", "Id", "HtmlString", "WS", "COMMENT", "LINE_COMMENT", "PREPROC"};
    }

    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    public String getGrammarFileName() {
        return "DOT.g4";
    }

    public String[] getRuleNames() {
        return ruleNames;
    }

    public String getSerializedATN() {
        return _serializedATN;
    }

    public ATN getATN() {
        return _ATN;
    }

    public DOTParser(TokenStream input) {
        super(input);
        this._interp = new ParserATNSimulator((Parser)this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public final GraphContext graph() throws RecognitionException {
        GraphContext _localctx = new GraphContext(this._ctx, this.getState());
        this.enterRule(_localctx, 0, 0);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(34);
            this.graphHeader();
            this.setState(35);
            this.compoundStatement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final CompoundStatementContext compoundStatement() throws RecognitionException {
        CompoundStatementContext _localctx = new CompoundStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 2, 1);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(37);
            this.match(1);
            this.setState(44);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            while ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x1FD002L) != 0L) {
                this.setState(38);
                this.statement();
                this.setState(40);
                this._errHandler.sync((Parser)this);
                _la = this._input.LA(1);
                if (_la == 2) {
                    this.setState(39);
                    this.match(2);
                }
                this.setState(46);
                this._errHandler.sync((Parser)this);
                _la = this._input.LA(1);
            }
            this.setState(47);
            this.match(3);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final GraphHeaderContext graphHeader() throws RecognitionException {
        GraphHeaderContext _localctx = new GraphHeaderContext(this._ctx, this.getState());
        this.enterRule(_localctx, 4, 2);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(50);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            if (_la == 11) {
                this.setState(49);
                this.match(11);
            }
            this.setState(52);
            _la = this._input.LA(1);
            if (_la != 12 && _la != 13) {
                this._errHandler.recoverInline((Parser)this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch((Parser)this);
                this.consume();
            }
            this.setState(54);
            this._errHandler.sync((Parser)this);
            _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x1E0000L) != 0L) {
                this.setState(53);
                this.graphIdentifier();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final GraphIdentifierContext graphIdentifier() throws RecognitionException {
        GraphIdentifierContext _localctx = new GraphIdentifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 6, 3);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(56);
            this.identifier();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final StatementContext statement() throws RecognitionException {
        StatementContext _localctx = new StatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 8, 4);
        try {
            this.setState(63);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 4, this._ctx)) {
                case 1: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(58);
                    this.nodeStatement();
                    return _localctx;
                }
                case 2: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(59);
                    this.edgeStatement();
                    return _localctx;
                }
                case 3: {
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(60);
                    this.attributeStatement();
                    return _localctx;
                }
                case 4: {
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(61);
                    this.identifierPairStatement();
                    return _localctx;
                }
                case 5: {
                    this.enterOuterAlt(_localctx, 5);
                    this.setState(62);
                    this.subgraphStatement();
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final IdentifierPairStatementContext identifierPairStatement() throws RecognitionException {
        IdentifierPairStatementContext _localctx = new IdentifierPairStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 10, 5);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(65);
            this.identifierPair();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final AttributeStatementContext attributeStatement() throws RecognitionException {
        AttributeStatementContext _localctx = new AttributeStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 12, 6);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(67);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0xD000L) == 0L) {
                this._errHandler.recoverInline((Parser)this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch((Parser)this);
                this.consume();
            }
            this.setState(68);
            this.attributesList();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final AttributesListContext attributesList() throws RecognitionException {
        AttributesListContext _localctx = new AttributesListContext(this._ctx, this.getState());
        this.enterRule(_localctx, 14, 7);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(75);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            do {
                this.setState(70);
                this.match(4);
                this.setState(72);
                this._errHandler.sync((Parser)this);
                _la = this._input.LA(1);
                if ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x1E0000L) != 0L) {
                    this.setState(71);
                    this.aList();
                }
                this.setState(74);
                this.match(5);
                this.setState(77);
                this._errHandler.sync((Parser)this);
            } while ((_la = this._input.LA(1)) == 4);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final AListContext aList() throws RecognitionException {
        AListContext _localctx = new AListContext(this._ctx, this.getState());
        this.enterRule(_localctx, 16, 8);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(83);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            do {
                this.setState(79);
                this.identifierPair();
                this.setState(81);
                this._errHandler.sync((Parser)this);
                _la = this._input.LA(1);
                if (_la == 2 || _la == 6) {
                    this.setState(80);
                    _la = this._input.LA(1);
                    if (_la != 2 && _la != 6) {
                        this._errHandler.recoverInline((Parser)this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch((Parser)this);
                        this.consume();
                    }
                }
                this.setState(85);
                this._errHandler.sync((Parser)this);
            } while (((_la = this._input.LA(1)) & 0xFFFFFFC0) == 0 && (1L << _la & 0x1E0000L) != 0L);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final EdgeStatementContext edgeStatement() throws RecognitionException {
        EdgeStatementContext _localctx = new EdgeStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 18, 9);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(89);
            this._errHandler.sync((Parser)this);
            switch (this._input.LA(1)) {
                case 17: 
                case 18: 
                case 19: 
                case 20: {
                    this.setState(87);
                    this.nodeStatementNoAttributes();
                    break;
                }
                case 1: 
                case 16: {
                    this.setState(88);
                    this.subgraphStatement();
                    break;
                }
                default: {
                    throw new NoViableAltException((Parser)this);
                }
            }
            this.setState(96);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            do {
                this.setState(91);
                _la = this._input.LA(1);
                if (_la != 7 && _la != 8) {
                    this._errHandler.recoverInline((Parser)this);
                } else {
                    if (this._input.LA(1) == -1) {
                        this.matchedEOF = true;
                    }
                    this._errHandler.reportMatch((Parser)this);
                    this.consume();
                }
                this.setState(94);
                this._errHandler.sync((Parser)this);
                switch (this._input.LA(1)) {
                    case 17: 
                    case 18: 
                    case 19: 
                    case 20: {
                        this.setState(92);
                        this.nodeStatementNoAttributes();
                        break;
                    }
                    case 1: 
                    case 16: {
                        this.setState(93);
                        this.subgraphStatement();
                        break;
                    }
                    default: {
                        throw new NoViableAltException((Parser)this);
                    }
                }
                this.setState(98);
                this._errHandler.sync((Parser)this);
            } while ((_la = this._input.LA(1)) == 7 || _la == 8);
            this.setState(101);
            this._errHandler.sync((Parser)this);
            _la = this._input.LA(1);
            if (_la == 4) {
                this.setState(100);
                this.attributesList();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final NodeStatementContext nodeStatement() throws RecognitionException {
        NodeStatementContext _localctx = new NodeStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 20, 10);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(103);
            this.nodeIdentifier();
            this.setState(105);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            if (_la == 4) {
                this.setState(104);
                this.attributesList();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final NodeStatementNoAttributesContext nodeStatementNoAttributes() throws RecognitionException {
        NodeStatementNoAttributesContext _localctx = new NodeStatementNoAttributesContext(this._ctx, this.getState());
        this.enterRule(_localctx, 22, 11);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(107);
            this.nodeIdentifier();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final NodeIdentifierContext nodeIdentifier() throws RecognitionException {
        NodeIdentifierContext _localctx = new NodeIdentifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 24, 12);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(109);
            this.identifier();
            this.setState(111);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            if (_la == 9) {
                this.setState(110);
                this.port();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final PortContext port() throws RecognitionException {
        PortContext _localctx = new PortContext(this._ctx, this.getState());
        this.enterRule(_localctx, 26, 13);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(113);
            this.match(9);
            this.setState(114);
            this.identifier();
            this.setState(117);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            if (_la == 9) {
                this.setState(115);
                this.match(9);
                this.setState(116);
                this.identifier();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final SubgraphStatementContext subgraphStatement() throws RecognitionException {
        SubgraphStatementContext _localctx = new SubgraphStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 28, 14);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(123);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            if (_la == 16) {
                this.setState(119);
                this.match(16);
                this.setState(121);
                this._errHandler.sync((Parser)this);
                _la = this._input.LA(1);
                if ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x1E0000L) != 0L) {
                    this.setState(120);
                    this.identifier();
                }
            }
            this.setState(125);
            this.compoundStatement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final IdentifierPairContext identifierPair() throws RecognitionException {
        IdentifierPairContext _localctx = new IdentifierPairContext(this._ctx, this.getState());
        this.enterRule(_localctx, 30, 15);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(127);
            this.identifier();
            this.setState(128);
            this.match(10);
            this.setState(129);
            this.identifier();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final IdentifierContext identifier() throws RecognitionException {
        IdentifierContext _localctx = new IdentifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 32, 16);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(131);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0x1E0000L) == 0L) {
                this._errHandler.recoverInline((Parser)this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch((Parser)this);
                this.consume();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError((Parser)this, re);
            this._errHandler.recover((Parser)this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    static {
        int i;
        RuntimeMetaData.checkVersion((String)"4.12.0", (String)"4.12.0");
        _sharedContextCache = new PredictionContextCache();
        ruleNames = DOTParser.makeRuleNames();
        _LITERAL_NAMES = DOTParser.makeLiteralNames();
        _SYMBOLIC_NAMES = DOTParser.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            DOTParser.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                DOTParser.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            DOTParser.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            DOTParser._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public static class GraphContext
    extends ParserRuleContext {
        public GraphHeaderContext graphHeader() {
            return (GraphHeaderContext)this.getRuleContext(GraphHeaderContext.class, 0);
        }

        public CompoundStatementContext compoundStatement() {
            return (CompoundStatementContext)this.getRuleContext(CompoundStatementContext.class, 0);
        }

        public GraphContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 0;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterGraph(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitGraph(this);
            }
        }
    }

    public static class GraphHeaderContext
    extends ParserRuleContext {
        public TerminalNode GRAPH() {
            return this.getToken(12, 0);
        }

        public TerminalNode DIGRAPH() {
            return this.getToken(13, 0);
        }

        public TerminalNode STRICT() {
            return this.getToken(11, 0);
        }

        public GraphIdentifierContext graphIdentifier() {
            return (GraphIdentifierContext)this.getRuleContext(GraphIdentifierContext.class, 0);
        }

        public GraphHeaderContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 2;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterGraphHeader(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitGraphHeader(this);
            }
        }
    }

    public static class CompoundStatementContext
    extends ParserRuleContext {
        public List<StatementContext> statement() {
            return this.getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return (StatementContext)this.getRuleContext(StatementContext.class, i);
        }

        public CompoundStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 1;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterCompoundStatement(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitCompoundStatement(this);
            }
        }
    }

    public static class StatementContext
    extends ParserRuleContext {
        public NodeStatementContext nodeStatement() {
            return (NodeStatementContext)this.getRuleContext(NodeStatementContext.class, 0);
        }

        public EdgeStatementContext edgeStatement() {
            return (EdgeStatementContext)this.getRuleContext(EdgeStatementContext.class, 0);
        }

        public AttributeStatementContext attributeStatement() {
            return (AttributeStatementContext)this.getRuleContext(AttributeStatementContext.class, 0);
        }

        public IdentifierPairStatementContext identifierPairStatement() {
            return (IdentifierPairStatementContext)this.getRuleContext(IdentifierPairStatementContext.class, 0);
        }

        public SubgraphStatementContext subgraphStatement() {
            return (SubgraphStatementContext)this.getRuleContext(SubgraphStatementContext.class, 0);
        }

        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 4;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterStatement(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitStatement(this);
            }
        }
    }

    public static class GraphIdentifierContext
    extends ParserRuleContext {
        public IdentifierContext identifier() {
            return (IdentifierContext)this.getRuleContext(IdentifierContext.class, 0);
        }

        public GraphIdentifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 3;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterGraphIdentifier(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitGraphIdentifier(this);
            }
        }
    }

    public static class IdentifierContext
    extends ParserRuleContext {
        public TerminalNode Id() {
            return this.getToken(19, 0);
        }

        public TerminalNode String() {
            return this.getToken(18, 0);
        }

        public TerminalNode HtmlString() {
            return this.getToken(20, 0);
        }

        public TerminalNode Numeral() {
            return this.getToken(17, 0);
        }

        public IdentifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 16;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterIdentifier(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitIdentifier(this);
            }
        }
    }

    public static class NodeStatementContext
    extends ParserRuleContext {
        public NodeIdentifierContext nodeIdentifier() {
            return (NodeIdentifierContext)this.getRuleContext(NodeIdentifierContext.class, 0);
        }

        public AttributesListContext attributesList() {
            return (AttributesListContext)this.getRuleContext(AttributesListContext.class, 0);
        }

        public NodeStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 10;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterNodeStatement(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitNodeStatement(this);
            }
        }
    }

    public static class EdgeStatementContext
    extends ParserRuleContext {
        public List<NodeStatementNoAttributesContext> nodeStatementNoAttributes() {
            return this.getRuleContexts(NodeStatementNoAttributesContext.class);
        }

        public NodeStatementNoAttributesContext nodeStatementNoAttributes(int i) {
            return (NodeStatementNoAttributesContext)this.getRuleContext(NodeStatementNoAttributesContext.class, i);
        }

        public List<SubgraphStatementContext> subgraphStatement() {
            return this.getRuleContexts(SubgraphStatementContext.class);
        }

        public SubgraphStatementContext subgraphStatement(int i) {
            return (SubgraphStatementContext)this.getRuleContext(SubgraphStatementContext.class, i);
        }

        public AttributesListContext attributesList() {
            return (AttributesListContext)this.getRuleContext(AttributesListContext.class, 0);
        }

        public EdgeStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 9;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterEdgeStatement(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitEdgeStatement(this);
            }
        }
    }

    public static class AttributeStatementContext
    extends ParserRuleContext {
        public AttributesListContext attributesList() {
            return (AttributesListContext)this.getRuleContext(AttributesListContext.class, 0);
        }

        public TerminalNode GRAPH() {
            return this.getToken(12, 0);
        }

        public TerminalNode NODE() {
            return this.getToken(14, 0);
        }

        public TerminalNode EDGE() {
            return this.getToken(15, 0);
        }

        public AttributeStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 6;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterAttributeStatement(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitAttributeStatement(this);
            }
        }
    }

    public static class IdentifierPairStatementContext
    extends ParserRuleContext {
        public IdentifierPairContext identifierPair() {
            return (IdentifierPairContext)this.getRuleContext(IdentifierPairContext.class, 0);
        }

        public IdentifierPairStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 5;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterIdentifierPairStatement(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitIdentifierPairStatement(this);
            }
        }
    }

    public static class SubgraphStatementContext
    extends ParserRuleContext {
        public CompoundStatementContext compoundStatement() {
            return (CompoundStatementContext)this.getRuleContext(CompoundStatementContext.class, 0);
        }

        public TerminalNode SUBGRAPH() {
            return this.getToken(16, 0);
        }

        public IdentifierContext identifier() {
            return (IdentifierContext)this.getRuleContext(IdentifierContext.class, 0);
        }

        public SubgraphStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 14;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterSubgraphStatement(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitSubgraphStatement(this);
            }
        }
    }

    public static class IdentifierPairContext
    extends ParserRuleContext {
        public List<IdentifierContext> identifier() {
            return this.getRuleContexts(IdentifierContext.class);
        }

        public IdentifierContext identifier(int i) {
            return (IdentifierContext)this.getRuleContext(IdentifierContext.class, i);
        }

        public IdentifierPairContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 15;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterIdentifierPair(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitIdentifierPair(this);
            }
        }
    }

    public static class AttributesListContext
    extends ParserRuleContext {
        public List<AListContext> aList() {
            return this.getRuleContexts(AListContext.class);
        }

        public AListContext aList(int i) {
            return (AListContext)this.getRuleContext(AListContext.class, i);
        }

        public AttributesListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 7;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterAttributesList(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitAttributesList(this);
            }
        }
    }

    public static class AListContext
    extends ParserRuleContext {
        public List<IdentifierPairContext> identifierPair() {
            return this.getRuleContexts(IdentifierPairContext.class);
        }

        public IdentifierPairContext identifierPair(int i) {
            return (IdentifierPairContext)this.getRuleContext(IdentifierPairContext.class, i);
        }

        public AListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 8;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterAList(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitAList(this);
            }
        }
    }

    public static class NodeStatementNoAttributesContext
    extends ParserRuleContext {
        public NodeIdentifierContext nodeIdentifier() {
            return (NodeIdentifierContext)this.getRuleContext(NodeIdentifierContext.class, 0);
        }

        public NodeStatementNoAttributesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 11;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterNodeStatementNoAttributes(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitNodeStatementNoAttributes(this);
            }
        }
    }

    public static class NodeIdentifierContext
    extends ParserRuleContext {
        public IdentifierContext identifier() {
            return (IdentifierContext)this.getRuleContext(IdentifierContext.class, 0);
        }

        public PortContext port() {
            return (PortContext)this.getRuleContext(PortContext.class, 0);
        }

        public NodeIdentifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 12;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterNodeIdentifier(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitNodeIdentifier(this);
            }
        }
    }

    public static class PortContext
    extends ParserRuleContext {
        public List<IdentifierContext> identifier() {
            return this.getRuleContexts(IdentifierContext.class);
        }

        public IdentifierContext identifier(int i) {
            return (IdentifierContext)this.getRuleContext(IdentifierContext.class, i);
        }

        public PortContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 13;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).enterPort(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DOTListener) {
                ((DOTListener)listener).exitPort(this);
            }
        }
    }
}

