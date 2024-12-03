/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
package org.jgrapht.nio.gml;

import java.util.List;
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
import org.jgrapht.nio.gml.GmlListener;

class GmlParser
extends Parser {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int T__0 = 1;
    public static final int T__1 = 2;
    public static final int NUMBER = 3;
    public static final int STRING = 4;
    public static final int ID = 5;
    public static final int COMMENT = 6;
    public static final int WS = 7;
    public static final int RULE_gml = 0;
    public static final int RULE_keyValuePair = 1;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u0001\u0007\u001a\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0001\u0000\u0005\u0000\u0006\b\u0000\n\u0000\f\u0000\t\t\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001\u0012\b\u0001\n\u0001\f\u0001\u0015\t\u0001\u0001\u0001\u0003\u0001\u0018\b\u0001\u0001\u0001\u0000\u0000\u0002\u0000\u0002\u0000\u0000\u001b\u0000\u0007\u0001\u0000\u0000\u0000\u0002\u0017\u0001\u0000\u0000\u0000\u0004\u0006\u0003\u0002\u0001\u0000\u0005\u0004\u0001\u0000\u0000\u0000\u0006\t\u0001\u0000\u0000\u0000\u0007\u0005\u0001\u0000\u0000\u0000\u0007\b\u0001\u0000\u0000\u0000\b\u0001\u0001\u0000\u0000\u0000\t\u0007\u0001\u0000\u0000\u0000\n\u000b\u0005\u0005\u0000\u0000\u000b\u0018\u0005\u0004\u0000\u0000\f\r\u0005\u0005\u0000\u0000\r\u0018\u0005\u0003\u0000\u0000\u000e\u000f\u0005\u0005\u0000\u0000\u000f\u0013\u0005\u0001\u0000\u0000\u0010\u0012\u0003\u0002\u0001\u0000\u0011\u0010\u0001\u0000\u0000\u0000\u0012\u0015\u0001\u0000\u0000\u0000\u0013\u0011\u0001\u0000\u0000\u0000\u0013\u0014\u0001\u0000\u0000\u0000\u0014\u0016\u0001\u0000\u0000\u0000\u0015\u0013\u0001\u0000\u0000\u0000\u0016\u0018\u0005\u0002\u0000\u0000\u0017\n\u0001\u0000\u0000\u0000\u0017\f\u0001\u0000\u0000\u0000\u0017\u000e\u0001\u0000\u0000\u0000\u0018\u0003\u0001\u0000\u0000\u0000\u0003\u0007\u0013\u0017";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"gml", "keyValuePair"};
    }

    private static String[] makeLiteralNames() {
        return new String[]{null, "'['", "']'"};
    }

    private static String[] makeSymbolicNames() {
        return new String[]{null, null, null, "NUMBER", "STRING", "ID", "COMMENT", "WS"};
    }

    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    public String getGrammarFileName() {
        return "Gml.g4";
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

    public GmlParser(TokenStream input) {
        super(input);
        this._interp = new ParserATNSimulator((Parser)this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final GmlContext gml() throws RecognitionException {
        GmlContext _localctx = new GmlContext(this._ctx, this.getState());
        this.enterRule(_localctx, 0, 0);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(7);
            this._errHandler.sync((Parser)this);
            int _la = this._input.LA(1);
            while (_la == 5) {
                this.setState(4);
                this.keyValuePair();
                this.setState(9);
                this._errHandler.sync((Parser)this);
                _la = this._input.LA(1);
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final KeyValuePairContext keyValuePair() throws RecognitionException {
        KeyValuePairContext _localctx = new KeyValuePairContext(this._ctx, this.getState());
        this.enterRule(_localctx, 2, 1);
        try {
            this.setState(23);
            this._errHandler.sync((Parser)this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 2, this._ctx)) {
                case 1: {
                    _localctx = new StringKeyValueContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(10);
                    this.match(5);
                    this.setState(11);
                    this.match(4);
                    return _localctx;
                }
                case 2: {
                    _localctx = new NumberKeyValueContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(12);
                    this.match(5);
                    this.setState(13);
                    this.match(3);
                    return _localctx;
                }
                case 3: {
                    _localctx = new ListKeyValueContext(_localctx);
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(14);
                    this.match(5);
                    this.setState(15);
                    this.match(1);
                    this.setState(19);
                    this._errHandler.sync((Parser)this);
                    int _la = this._input.LA(1);
                    while (true) {
                        if (_la != 5) {
                            this.setState(22);
                            this.match(2);
                            return _localctx;
                        }
                        this.setState(16);
                        this.keyValuePair();
                        this.setState(21);
                        this._errHandler.sync((Parser)this);
                        _la = this._input.LA(1);
                    }
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

    static {
        int i;
        RuntimeMetaData.checkVersion((String)"4.12.0", (String)"4.12.0");
        _sharedContextCache = new PredictionContextCache();
        ruleNames = GmlParser.makeRuleNames();
        _LITERAL_NAMES = GmlParser.makeLiteralNames();
        _SYMBOLIC_NAMES = GmlParser.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            GmlParser.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                GmlParser.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            GmlParser.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            GmlParser._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public static class GmlContext
    extends ParserRuleContext {
        public List<KeyValuePairContext> keyValuePair() {
            return this.getRuleContexts(KeyValuePairContext.class);
        }

        public KeyValuePairContext keyValuePair(int i) {
            return (KeyValuePairContext)this.getRuleContext(KeyValuePairContext.class, i);
        }

        public GmlContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 0;
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GmlListener) {
                ((GmlListener)listener).enterGml(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GmlListener) {
                ((GmlListener)listener).exitGml(this);
            }
        }
    }

    public static class KeyValuePairContext
    extends ParserRuleContext {
        public KeyValuePairContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 1;
        }

        public KeyValuePairContext() {
        }

        public void copyFrom(KeyValuePairContext ctx) {
            super.copyFrom((ParserRuleContext)ctx);
        }
    }

    public static class StringKeyValueContext
    extends KeyValuePairContext {
        public TerminalNode ID() {
            return this.getToken(5, 0);
        }

        public TerminalNode STRING() {
            return this.getToken(4, 0);
        }

        public StringKeyValueContext(KeyValuePairContext ctx) {
            this.copyFrom(ctx);
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GmlListener) {
                ((GmlListener)listener).enterStringKeyValue(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GmlListener) {
                ((GmlListener)listener).exitStringKeyValue(this);
            }
        }
    }

    public static class NumberKeyValueContext
    extends KeyValuePairContext {
        public TerminalNode ID() {
            return this.getToken(5, 0);
        }

        public TerminalNode NUMBER() {
            return this.getToken(3, 0);
        }

        public NumberKeyValueContext(KeyValuePairContext ctx) {
            this.copyFrom(ctx);
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GmlListener) {
                ((GmlListener)listener).enterNumberKeyValue(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GmlListener) {
                ((GmlListener)listener).exitNumberKeyValue(this);
            }
        }
    }

    public static class ListKeyValueContext
    extends KeyValuePairContext {
        public TerminalNode ID() {
            return this.getToken(5, 0);
        }

        public List<KeyValuePairContext> keyValuePair() {
            return this.getRuleContexts(KeyValuePairContext.class);
        }

        public KeyValuePairContext keyValuePair(int i) {
            return (KeyValuePairContext)this.getRuleContext(KeyValuePairContext.class, i);
        }

        public ListKeyValueContext(KeyValuePairContext ctx) {
            this.copyFrom(ctx);
        }

        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GmlListener) {
                ((GmlListener)listener).enterListKeyValue(this);
            }
        }

        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GmlListener) {
                ((GmlListener)listener).exitListKeyValue(this);
            }
        }
    }
}

