/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.CharStream
 *  org.antlr.v4.runtime.Lexer
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
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

class GmlLexer
extends Lexer {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int T__0 = 1;
    public static final int T__1 = 2;
    public static final int NUMBER = 3;
    public static final int STRING = 4;
    public static final int ID = 5;
    public static final int COMMENT = 6;
    public static final int WS = 7;
    public static String[] channelNames;
    public static String[] modeNames;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u0000\u0007Y\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0003\u0002\u0019\b\u0002\u0001\u0002\u0001\u0002\u0004\u0002\u001d\b\u0002\u000b\u0002\f\u0002\u001e\u0001\u0002\u0004\u0002\"\b\u0002\u000b\u0002\f\u0002#\u0001\u0002\u0001\u0002\u0005\u0002(\b\u0002\n\u0002\f\u0002+\t\u0002\u0003\u0002-\b\u0002\u0003\u0002/\b\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u00059\b\u0005\n\u0005\f\u0005<\t\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006C\b\u0006\n\u0006\f\u0006F\t\u0006\u0001\u0007\u0001\u0007\u0005\u0007J\b\u0007\n\u0007\f\u0007M\t\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0004\bT\b\b\u000b\b\f\bU\u0001\b\u0001\b\u0002:K\u0000\t\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0000\t\u0000\u000b\u0004\r\u0005\u000f\u0006\u0011\u0007\u0001\u0000\u0003\u0001\u000009\u0004\u0000AZ__az\u0080\u00ff\u0003\u0000\t\n\r\r  b\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0001\u0013\u0001\u0000\u0000\u0000\u0003\u0015\u0001\u0000\u0000\u0000\u0005\u0018\u0001\u0000\u0000\u0000\u00070\u0001\u0000\u0000\u0000\t2\u0001\u0000\u0000\u0000\u000b4\u0001\u0000\u0000\u0000\r?\u0001\u0000\u0000\u0000\u000fG\u0001\u0000\u0000\u0000\u0011S\u0001\u0000\u0000\u0000\u0013\u0014\u0005[\u0000\u0000\u0014\u0002\u0001\u0000\u0000\u0000\u0015\u0016\u0005]\u0000\u0000\u0016\u0004\u0001\u0000\u0000\u0000\u0017\u0019\u0005-\u0000\u0000\u0018\u0017\u0001\u0000\u0000\u0000\u0018\u0019\u0001\u0000\u0000\u0000\u0019.\u0001\u0000\u0000\u0000\u001a\u001c\u0005.\u0000\u0000\u001b\u001d\u0003\u0007\u0003\u0000\u001c\u001b\u0001\u0000\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e\u001c\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f/\u0001\u0000\u0000\u0000 \"\u0003\u0007\u0003\u0000! \u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#!\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$,\u0001\u0000\u0000\u0000%)\u0005.\u0000\u0000&(\u0003\u0007\u0003\u0000'&\u0001\u0000\u0000\u0000(+\u0001\u0000\u0000\u0000)'\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000*-\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000,%\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-/\u0001\u0000\u0000\u0000.\u001a\u0001\u0000\u0000\u0000.!\u0001\u0000\u0000\u0000/\u0006\u0001\u0000\u0000\u000001\u0007\u0000\u0000\u00001\b\u0001\u0000\u0000\u000023\u0007\u0001\u0000\u00003\n\u0001\u0000\u0000\u00004:\u0005\"\u0000\u000056\u0005\\\u0000\u000069\u0005\"\u0000\u000079\t\u0000\u0000\u000085\u0001\u0000\u0000\u000087\u0001\u0000\u0000\u00009<\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000;=\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000=>\u0005\"\u0000\u0000>\f\u0001\u0000\u0000\u0000?D\u0003\t\u0004\u0000@C\u0003\t\u0004\u0000AC\u0003\u0007\u0003\u0000B@\u0001\u0000\u0000\u0000BA\u0001\u0000\u0000\u0000CF\u0001\u0000\u0000\u0000DB\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000E\u000e\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000GK\u0005#\u0000\u0000HJ\t\u0000\u0000\u0000IH\u0001\u0000\u0000\u0000JM\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000KI\u0001\u0000\u0000\u0000LN\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000NO\u0005\n\u0000\u0000OP\u0001\u0000\u0000\u0000PQ\u0006\u0007\u0000\u0000Q\u0010\u0001\u0000\u0000\u0000RT\u0007\u0002\u0000\u0000SR\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000US\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000WX\u0006\b\u0000\u0000X\u0012\u0001\u0000\u0000\u0000\r\u0000\u0018\u001e#),.8:BDKU\u0001\u0006\u0000\u0000";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"T__0", "T__1", "NUMBER", "DIGIT", "LETTER", "STRING", "ID", "COMMENT", "WS"};
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

    public GmlLexer(CharStream input) {
        super(input);
        this._interp = new LexerATNSimulator((Lexer)this, _ATN, _decisionToDFA, _sharedContextCache);
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

    public String[] getChannelNames() {
        return channelNames;
    }

    public String[] getModeNames() {
        return modeNames;
    }

    public ATN getATN() {
        return _ATN;
    }

    static {
        int i;
        RuntimeMetaData.checkVersion((String)"4.12.0", (String)"4.12.0");
        _sharedContextCache = new PredictionContextCache();
        channelNames = new String[]{"DEFAULT_TOKEN_CHANNEL", "HIDDEN"};
        modeNames = new String[]{"DEFAULT_MODE"};
        ruleNames = GmlLexer.makeRuleNames();
        _LITERAL_NAMES = GmlLexer.makeLiteralNames();
        _SYMBOLIC_NAMES = GmlLexer.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            GmlLexer.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                GmlLexer.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            GmlLexer.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            GmlLexer._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}

