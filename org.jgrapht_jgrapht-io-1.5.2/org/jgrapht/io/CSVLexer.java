/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.CharStream
 *  org.antlr.v4.runtime.Lexer
 *  org.antlr.v4.runtime.RuleContext
 *  org.antlr.v4.runtime.RuntimeMetaData
 *  org.antlr.v4.runtime.Vocabulary
 *  org.antlr.v4.runtime.VocabularyImpl
 *  org.antlr.v4.runtime.atn.ATN
 *  org.antlr.v4.runtime.atn.ATNDeserializer
 *  org.antlr.v4.runtime.atn.LexerATNSimulator
 *  org.antlr.v4.runtime.atn.PredictionContextCache
 *  org.antlr.v4.runtime.dfa.DFA
 */
package org.jgrapht.io;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

class CSVLexer
extends Lexer {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int T__0 = 1;
    public static final int T__1 = 2;
    public static final int SEPARATOR = 3;
    public static final int TEXT = 4;
    public static final int STRING = 5;
    public static String[] channelNames;
    public static String[] modeNames;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    char sep = (char)44;
    public static final String _serializedATN = "\u0004\u0000\u0005'\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0004\u0003\u0016\b\u0003\u000b\u0003\f\u0003\u0017\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005!\b\u0005\n\u0005\f\u0005$\t\u0005\u0001\u0005\u0001\u0005\u0000\u0000\u0006\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0000\u000b\u0005\u0001\u0000\u0001\u0001\u0000\"\"(\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0001\r\u0001\u0000\u0000\u0000\u0003\u000f\u0001\u0000\u0000\u0000\u0005\u0011\u0001\u0000\u0000\u0000\u0007\u0015\u0001\u0000\u0000\u0000\t\u0019\u0001\u0000\u0000\u0000\u000b\u001c\u0001\u0000\u0000\u0000\r\u000e\u0005\r\u0000\u0000\u000e\u0002\u0001\u0000\u0000\u0000\u000f\u0010\u0005\n\u0000\u0000\u0010\u0004\u0001\u0000\u0000\u0000\u0011\u0012\u0004\u0002\u0000\u0000\u0012\u0013\t\u0000\u0000\u0000\u0013\u0006\u0001\u0000\u0000\u0000\u0014\u0016\u0003\t\u0004\u0000\u0015\u0014\u0001\u0000\u0000\u0000\u0016\u0017\u0001\u0000\u0000\u0000\u0017\u0015\u0001\u0000\u0000\u0000\u0017\u0018\u0001\u0000\u0000\u0000\u0018\b\u0001\u0000\u0000\u0000\u0019\u001a\u0004\u0004\u0001\u0000\u001a\u001b\t\u0000\u0000\u0000\u001b\n\u0001\u0000\u0000\u0000\u001c\"\u0005\"\u0000\u0000\u001d\u001e\u0005\"\u0000\u0000\u001e!\u0005\"\u0000\u0000\u001f!\b\u0000\u0000\u0000 \u001d\u0001\u0000\u0000\u0000 \u001f\u0001\u0000\u0000\u0000!$\u0001\u0000\u0000\u0000\" \u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#%\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000%&\u0005\"\u0000\u0000&\f\u0001\u0000\u0000\u0000\u0004\u0000\u0017 \"\u0000";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"T__0", "T__1", "SEPARATOR", "TEXT", "TEXTCHAR", "STRING"};
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

    public void setSep(char sep) {
        this.sep = sep;
    }

    private char getSep() {
        return this.sep;
    }

    public CSVLexer(CharStream input) {
        super(input);
        this._interp = new LexerATNSimulator((Lexer)this, _ATN, _decisionToDFA, _sharedContextCache);
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

    public String[] getChannelNames() {
        return channelNames;
    }

    public String[] getModeNames() {
        return modeNames;
    }

    public ATN getATN() {
        return _ATN;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 2: {
                return this.SEPARATOR_sempred(_localctx, predIndex);
            }
            case 4: {
                return this.TEXTCHAR_sempred(_localctx, predIndex);
            }
        }
        return true;
    }

    private boolean SEPARATOR_sempred(RuleContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0: {
                return this._input.LA(1) == this.sep;
            }
        }
        return true;
    }

    private boolean TEXTCHAR_sempred(RuleContext _localctx, int predIndex) {
        switch (predIndex) {
            case 1: {
                return this._input.LA(1) != this.sep && this._input.LA(1) != 10 && this._input.LA(1) != 13 && this._input.LA(1) != 34;
            }
        }
        return true;
    }

    static {
        int i;
        RuntimeMetaData.checkVersion((String)"4.12.0", (String)"4.12.0");
        _sharedContextCache = new PredictionContextCache();
        channelNames = new String[]{"DEFAULT_TOKEN_CHANNEL", "HIDDEN"};
        modeNames = new String[]{"DEFAULT_MODE"};
        ruleNames = CSVLexer.makeRuleNames();
        _LITERAL_NAMES = CSVLexer.makeLiteralNames();
        _SYMBOLIC_NAMES = CSVLexer.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            CSVLexer.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                CSVLexer.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            CSVLexer.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            CSVLexer._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}

