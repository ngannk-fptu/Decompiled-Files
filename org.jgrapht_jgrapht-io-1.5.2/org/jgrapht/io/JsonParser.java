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
import org.jgrapht.io.JsonListener;

class JsonParser
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
    public static final int STRING = 10;
    public static final int NUMBER = 11;
    public static final int WS = 12;
    public static final int RULE_json = 0;
    public static final int RULE_obj = 1;
    public static final int RULE_pair = 2;
    public static final int RULE_array = 3;
    public static final int RULE_value = 4;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u0001\f8\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001\u0011\b\u0001\n\u0001\f\u0001\u0014\t\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u001a\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003$\b\u0003\n\u0003\f\u0003'\t\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003-\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u00046\b\u0004\u0001\u0004\u0000\u0000\u0005\u0000\u0002\u0004\u0006\b\u0000\u0000<\u0000\n\u0001\u0000\u0000\u0000\u0002\u0019\u0001\u0000\u0000\u0000\u0004\u001b\u0001\u0000\u0000\u0000\u0006,\u0001\u0000\u0000\u0000\b5\u0001\u0000\u0000\u0000\n\u000b\u0003\b\u0004\u0000\u000b\u0001\u0001\u0000\u0000\u0000\f\r\u0005\u0001\u0000\u0000\r\u0012\u0003\u0004\u0002\u0000\u000e\u000f\u0005\u0002\u0000\u0000\u000f\u0011\u0003\u0004\u0002\u0000\u0010\u000e\u0001\u0000\u0000\u0000\u0011\u0014\u0001\u0000\u0000\u0000\u0012\u0010\u0001\u0000\u0000\u0000\u0012\u0013\u0001\u0000\u0000\u0000\u0013\u0015\u0001\u0000\u0000\u0000\u0014\u0012\u0001\u0000\u0000\u0000\u0015\u0016\u0005\u0003\u0000\u0000\u0016\u001a\u0001\u0000\u0000\u0000\u0017\u0018\u0005\u0001\u0000\u0000\u0018\u001a\u0005\u0003\u0000\u0000\u0019\f\u0001\u0000\u0000\u0000\u0019\u0017\u0001\u0000\u0000\u0000\u001a\u0003\u0001\u0000\u0000\u0000\u001b\u001c\u0005\n\u0000\u0000\u001c\u001d\u0005\u0004\u0000\u0000\u001d\u001e\u0003\b\u0004\u0000\u001e\u0005\u0001\u0000\u0000\u0000\u001f \u0005\u0005\u0000\u0000 %\u0003\b\u0004\u0000!\"\u0005\u0002\u0000\u0000\"$\u0003\b\u0004\u0000#!\u0001\u0000\u0000\u0000$'\u0001\u0000\u0000\u0000%#\u0001\u0000\u0000\u0000%&\u0001\u0000\u0000\u0000&(\u0001\u0000\u0000\u0000'%\u0001\u0000\u0000\u0000()\u0005\u0006\u0000\u0000)-\u0001\u0000\u0000\u0000*+\u0005\u0005\u0000\u0000+-\u0005\u0006\u0000\u0000,\u001f\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000-\u0007\u0001\u0000\u0000\u0000.6\u0005\n\u0000\u0000/6\u0005\u000b\u0000\u000006\u0003\u0002\u0001\u000016\u0003\u0006\u0003\u000026\u0005\u0007\u0000\u000036\u0005\b\u0000\u000046\u0005\t\u0000\u00005.\u0001\u0000\u0000\u00005/\u0001\u0000\u0000\u000050\u0001\u0000\u0000\u000051\u0001\u0000\u0000\u000052\u0001\u0000\u0000\u000053\u0001\u0000\u0000\u000054\u0001\u0000\u0000\u00006\t\u0001\u0000\u0000\u0000\u0005\u0012\u0019%,5";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"json", "obj", "pair", "array", "value"};
    }

    private static String[] makeLiteralNames() {
        return new String[]{null, "'{'", "','", "'}'", "':'", "'['", "']'", "'true'", "'false'", "'null'"};
    }

    private static String[] makeSymbolicNames() {
        return new String[]{null, null, null, null, null, null, null, null, null, null, "STRING", "NUMBER", "WS"};
    }

    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    public String getGrammarFileName() {
        return "Json.g4";
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

    public JsonParser(TokenStream input) {
        super(input);
        this._interp = new ParserATNSimulator((Parser)this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public final JsonContext json() throws RecognitionException {
        JsonContext _localctx = new JsonContext(this._ctx, this.getState());
        this.enterRule(_localctx, 0, 0);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(10);
            this.value();
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final ObjContext obj() throws RecognitionException {
        ObjContext _localctx = new ObjContext(this._ctx, this.getState());
        this.enterRule(_localctx, 2, 1);
        try {
            this.setState(25);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 1, this._ctx)) {
                case 1: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(12);
                    this.match(1);
                    this.setState(13);
                    this.pair();
                    this.setState(18);
                    this._errHandler.sync((Parser)this);
                    int _la = this._input.LA(1);
                    while (true) {
                        if (_la != 2) {
                            this.setState(21);
                            this.match(3);
                            return _localctx;
                        }
                        this.setState(14);
                        this.match(2);
                        this.setState(15);
                        this.pair();
                        this.setState(20);
                        this._errHandler.sync((Parser)this);
                        _la = this._input.LA(1);
                    }
                }
                case 2: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(23);
                    this.match(1);
                    this.setState(24);
                    this.match(3);
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

    public final PairContext pair() throws RecognitionException {
        PairContext _localctx = new PairContext(this._ctx, this.getState());
        this.enterRule(_localctx, 4, 2);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(27);
            this.match(10);
            this.setState(28);
            this.match(4);
            this.setState(29);
            this.value();
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final ArrayContext array() throws RecognitionException {
        ArrayContext _localctx = new ArrayContext(this._ctx, this.getState());
        this.enterRule(_localctx, 6, 3);
        try {
            this.setState(44);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 3, this._ctx)) {
                case 1: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(31);
                    this.match(5);
                    this.setState(32);
                    this.value();
                    this.setState(37);
                    this._errHandler.sync((Parser)this);
                    int _la = this._input.LA(1);
                    while (true) {
                        if (_la != 2) {
                            this.setState(40);
                            this.match(6);
                            return _localctx;
                        }
                        this.setState(33);
                        this.match(2);
                        this.setState(34);
                        this.value();
                        this.setState(39);
                        this._errHandler.sync((Parser)this);
                        _la = this._input.LA(1);
                    }
                }
                case 2: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(42);
                    this.match(5);
                    this.setState(43);
                    this.match(6);
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final ValueContext value() throws RecognitionException {
        ValueContext _localctx = new ValueContext(this._ctx, this.getState());
        this.enterRule(_localctx, 8, 4);
        try {
            this.setState(53);
            this._errHandler.sync((Parser)this);
            switch (this._input.LA(1)) {
                case 10: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(46);
                    this.match(10);
                    return _localctx;
                }
                case 11: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(47);
                    this.match(11);
                    return _localctx;
                }
                case 1: {
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(48);
                    this.obj();
                    return _localctx;
                }
                case 5: {
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(49);
                    this.array();
                    return _localctx;
                }
                case 7: {
                    this.enterOuterAlt(_localctx, 5);
                    this.setState(50);
                    this.match(7);
                    return _localctx;
                }
                case 8: {
                    this.enterOuterAlt(_localctx, 6);
                    this.setState(51);
                    this.match(8);
                    return _localctx;
                }
                case 9: {
                    this.enterOuterAlt(_localctx, 7);
                    this.setState(52);
                    this.match(9);
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
        ruleNames = JsonParser.makeRuleNames();
        _LITERAL_NAMES = JsonParser.makeLiteralNames();
        _SYMBOLIC_NAMES = JsonParser.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            JsonParser.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                JsonParser.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            JsonParser.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            JsonParser._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public static class JsonContext
    extends ParserRuleContext {
        public ValueContext value() {
            return (ValueContext)this.getRuleContext(ValueContext.class, 0);
        }

        public JsonContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 0;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).enterJson(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).exitJson(this);
            }
        }
    }

    public static class ValueContext
    extends ParserRuleContext {
        public TerminalNode STRING() {
            return this.getToken(10, 0);
        }

        public TerminalNode NUMBER() {
            return this.getToken(11, 0);
        }

        public ObjContext obj() {
            return (ObjContext)this.getRuleContext(ObjContext.class, 0);
        }

        public ArrayContext array() {
            return (ArrayContext)this.getRuleContext(ArrayContext.class, 0);
        }

        public ValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 4;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).enterValue(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).exitValue(this);
            }
        }
    }

    public static class ObjContext
    extends ParserRuleContext {
        public List<PairContext> pair() {
            return this.getRuleContexts(PairContext.class);
        }

        public PairContext pair(int i) {
            return (PairContext)this.getRuleContext(PairContext.class, i);
        }

        public ObjContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 1;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).enterObj(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).exitObj(this);
            }
        }
    }

    public static class PairContext
    extends ParserRuleContext {
        public TerminalNode STRING() {
            return this.getToken(10, 0);
        }

        public ValueContext value() {
            return (ValueContext)this.getRuleContext(ValueContext.class, 0);
        }

        public PairContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 2;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).enterPair(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).exitPair(this);
            }
        }
    }

    public static class ArrayContext
    extends ParserRuleContext {
        public List<ValueContext> value() {
            return this.getRuleContexts(ValueContext.class);
        }

        public ValueContext value(int i) {
            return (ValueContext)this.getRuleContext(ValueContext.class, i);
        }

        public ArrayContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 3;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).enterArray(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof JsonListener) {
                ((JsonListener)listener).exitArray(this);
            }
        }
    }
}

