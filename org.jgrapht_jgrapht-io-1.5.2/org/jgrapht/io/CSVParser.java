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
import org.jgrapht.io.CSVListener;

class CSVParser
extends Parser {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int T__0 = 1;
    public static final int T__1 = 2;
    public static final int SEPARATOR = 3;
    public static final int TEXT = 4;
    public static final int STRING = 5;
    public static final int RULE_file = 0;
    public static final int RULE_header = 1;
    public static final int RULE_record = 2;
    public static final int RULE_field = 3;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u0001\u0005#\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0001\u0000\u0001\u0000\u0004\u0000\u000b\b\u0000\u000b\u0000\f\u0000\f\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002\u0014\b\u0002\n\u0002\f\u0002\u0017\t\u0002\u0001\u0002\u0003\u0002\u001a\b\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003!\b\u0003\u0001\u0003\u0000\u0000\u0004\u0000\u0002\u0004\u0006\u0000\u0000#\u0000\b\u0001\u0000\u0000\u0000\u0002\u000e\u0001\u0000\u0000\u0000\u0004\u0010\u0001\u0000\u0000\u0000\u0006 \u0001\u0000\u0000\u0000\b\n\u0003\u0002\u0001\u0000\t\u000b\u0003\u0004\u0002\u0000\n\t\u0001\u0000\u0000\u0000\u000b\f\u0001\u0000\u0000\u0000\f\n\u0001\u0000\u0000\u0000\f\r\u0001\u0000\u0000\u0000\r\u0001\u0001\u0000\u0000\u0000\u000e\u000f\u0003\u0004\u0002\u0000\u000f\u0003\u0001\u0000\u0000\u0000\u0010\u0015\u0003\u0006\u0003\u0000\u0011\u0012\u0005\u0003\u0000\u0000\u0012\u0014\u0003\u0006\u0003\u0000\u0013\u0011\u0001\u0000\u0000\u0000\u0014\u0017\u0001\u0000\u0000\u0000\u0015\u0013\u0001\u0000\u0000\u0000\u0015\u0016\u0001\u0000\u0000\u0000\u0016\u0019\u0001\u0000\u0000\u0000\u0017\u0015\u0001\u0000\u0000\u0000\u0018\u001a\u0005\u0001\u0000\u0000\u0019\u0018\u0001\u0000\u0000\u0000\u0019\u001a\u0001\u0000\u0000\u0000\u001a\u001b\u0001\u0000\u0000\u0000\u001b\u001c\u0005\u0002\u0000\u0000\u001c\u0005\u0001\u0000\u0000\u0000\u001d!\u0005\u0004\u0000\u0000\u001e!\u0005\u0005\u0000\u0000\u001f!\u0001\u0000\u0000\u0000 \u001d\u0001\u0000\u0000\u0000 \u001e\u0001\u0000\u0000\u0000 \u001f\u0001\u0000\u0000\u0000!\u0007\u0001\u0000\u0000\u0000\u0004\f\u0015\u0019 ";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"file", "header", "record", "field"};
    }

    private static String[] makeLiteralNames() {
        return new String[]{null, "'\\r'", "'\\n'"};
    }

    private static String[] makeSymbolicNames() {
        return new String[]{null, null, null, "SEPARATOR", "TEXT", "STRING"};
    }

    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    public String getGrammarFileName() {
        return "CSV.g4";
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

    public CSVParser(TokenStream input) {
        super(input);
        this._interp = new ParserATNSimulator((Parser)this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(this._ctx, this.getState());
        this.enterRule(_localctx, 0, 0);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(8);
            this.header();
            this.setState(10);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            do {
                this.setState(9);
                this.record();
                this.setState(12);
                this._errHandler.sync((Parser)this);
            } while (((_la = this._input.LA(1)) & 0xFFFFFFC0) == 0 && (1L << _la & 0x3EL) != 0L);
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

    public final HeaderContext header() throws RecognitionException {
        HeaderContext _localctx = new HeaderContext(this._ctx, this.getState());
        this.enterRule(_localctx, 2, 1);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(14);
            this.record();
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
    public final RecordContext record() throws RecognitionException {
        RecordContext _localctx = new RecordContext(this._ctx, this.getState());
        this.enterRule(_localctx, 4, 2);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(16);
            this.field();
            this.setState(21);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            while (_la == 3) {
                this.setState(17);
                this.match(3);
                this.setState(18);
                this.field();
                this.setState(23);
                this._errHandler.sync((Parser)this);
                _la = this._input.LA(1);
            }
            this.setState(25);
            this._errHandler.sync((Parser)this);
            _la = this._input.LA(1);
            if (_la == 1) {
                this.setState(24);
                this.match(1);
            }
            this.setState(27);
            this.match(2);
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final FieldContext field() throws RecognitionException {
        FieldContext _localctx = new FieldContext(this._ctx, this.getState());
        this.enterRule(_localctx, 6, 3);
        try {
            this.setState(32);
            this._errHandler.sync((Parser)this);
            switch (this._input.LA(1)) {
                case 4: {
                    _localctx = new TextFieldContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(29);
                    this.match(4);
                    return _localctx;
                }
                case 5: {
                    _localctx = new StringFieldContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(30);
                    this.match(5);
                    return _localctx;
                }
                case 1: 
                case 2: 
                case 3: {
                    _localctx = new EmptyFieldContext(_localctx);
                    this.enterOuterAlt(_localctx, 3);
                    return _localctx;
                }
                default: {
                    throw new NoViableAltException((Parser)this);
                }
            }
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

    static {
        int i;
        RuntimeMetaData.checkVersion((String)"4.12.0", (String)"4.12.0");
        _sharedContextCache = new PredictionContextCache();
        ruleNames = CSVParser.makeRuleNames();
        _LITERAL_NAMES = CSVParser.makeLiteralNames();
        _SYMBOLIC_NAMES = CSVParser.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            CSVParser.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                CSVParser.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            CSVParser.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            CSVParser._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public static class FileContext
    extends ParserRuleContext {
        public HeaderContext header() {
            return (HeaderContext)this.getRuleContext(HeaderContext.class, 0);
        }

        public List<RecordContext> record() {
            return this.getRuleContexts(RecordContext.class);
        }

        public RecordContext record(int i) {
            return (RecordContext)this.getRuleContext(RecordContext.class, i);
        }

        public FileContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 0;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).enterFile(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).exitFile(this);
            }
        }
    }

    public static class HeaderContext
    extends ParserRuleContext {
        public RecordContext record() {
            return (RecordContext)this.getRuleContext(RecordContext.class, 0);
        }

        public HeaderContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 1;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).enterHeader(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).exitHeader(this);
            }
        }
    }

    public static class RecordContext
    extends ParserRuleContext {
        public List<FieldContext> field() {
            return this.getRuleContexts(FieldContext.class);
        }

        public FieldContext field(int i) {
            return (FieldContext)this.getRuleContext(FieldContext.class, i);
        }

        public List<TerminalNode> SEPARATOR() {
            return this.getTokens(3);
        }

        public TerminalNode SEPARATOR(int i) {
            return this.getToken(3, i);
        }

        public RecordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 2;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).enterRecord(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).exitRecord(this);
            }
        }
    }

    public static class FieldContext
    extends ParserRuleContext {
        public FieldContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 3;
        }

        public FieldContext() {
        }

        public void copyFrom(FieldContext ctx) {
            super.copyFrom((ParserRuleContext)ctx);
        }
    }

    public static class TextFieldContext
    extends FieldContext {
        public TerminalNode TEXT() {
            return this.getToken(4, 0);
        }

        public TextFieldContext(FieldContext ctx) {
            this.copyFrom(ctx);
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).enterTextField(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).exitTextField(this);
            }
        }
    }

    public static class StringFieldContext
    extends FieldContext {
        public TerminalNode STRING() {
            return this.getToken(5, 0);
        }

        public StringFieldContext(FieldContext ctx) {
            this.copyFrom(ctx);
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).enterStringField(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).exitStringField(this);
            }
        }
    }

    public static class EmptyFieldContext
    extends FieldContext {
        public EmptyFieldContext(FieldContext ctx) {
            this.copyFrom(ctx);
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).enterEmptyField(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CSVListener) {
                ((CSVListener)listener).exitEmptyField(this);
            }
        }
    }
}

