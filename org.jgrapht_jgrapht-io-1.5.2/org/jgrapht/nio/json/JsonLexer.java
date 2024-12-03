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
package org.jgrapht.nio.json;

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

class JsonLexer
extends Lexer {
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
    public static String[] channelNames;
    public static String[] modeNames;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u0000\f\u0080\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\tE\b\t\n\t\f\tH\t\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0003\nO\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0003\u000e\\\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0004\u000ea\b\u000e\u000b\u000e\f\u000eb\u0003\u000ee\b\u000e\u0001\u000e\u0003\u000eh\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000fm\b\u000f\n\u000f\f\u000fp\t\u000f\u0003\u000fr\b\u000f\u0001\u0010\u0001\u0010\u0003\u0010v\b\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0004\u0011{\b\u0011\u000b\u0011\f\u0011|\u0001\u0011\u0001\u0011\u0000\u0000\u0012\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u0000\u0017\u0000\u0019\u0000\u001b\u0000\u001d\u000b\u001f\u0000!\u0000#\f\u0001\u0000\b\b\u0000\"\"//\\\\bbffnnrrtt\u0003\u000009AFaf\u0003\u0000\u0000\u001f\"\"\\\\\u0001\u000009\u0001\u000019\u0002\u0000EEee\u0002\u0000++--\u0003\u0000\t\n\r\r  \u0084\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0001%\u0001\u0000\u0000\u0000\u0003'\u0001\u0000\u0000\u0000\u0005)\u0001\u0000\u0000\u0000\u0007+\u0001\u0000\u0000\u0000\t-\u0001\u0000\u0000\u0000\u000b/\u0001\u0000\u0000\u0000\r1\u0001\u0000\u0000\u0000\u000f6\u0001\u0000\u0000\u0000\u0011<\u0001\u0000\u0000\u0000\u0013A\u0001\u0000\u0000\u0000\u0015K\u0001\u0000\u0000\u0000\u0017P\u0001\u0000\u0000\u0000\u0019V\u0001\u0000\u0000\u0000\u001bX\u0001\u0000\u0000\u0000\u001d[\u0001\u0000\u0000\u0000\u001fq\u0001\u0000\u0000\u0000!s\u0001\u0000\u0000\u0000#z\u0001\u0000\u0000\u0000%&\u0005{\u0000\u0000&\u0002\u0001\u0000\u0000\u0000'(\u0005,\u0000\u0000(\u0004\u0001\u0000\u0000\u0000)*\u0005}\u0000\u0000*\u0006\u0001\u0000\u0000\u0000+,\u0005:\u0000\u0000,\b\u0001\u0000\u0000\u0000-.\u0005[\u0000\u0000.\n\u0001\u0000\u0000\u0000/0\u0005]\u0000\u00000\f\u0001\u0000\u0000\u000012\u0005t\u0000\u000023\u0005r\u0000\u000034\u0005u\u0000\u000045\u0005e\u0000\u00005\u000e\u0001\u0000\u0000\u000067\u0005f\u0000\u000078\u0005a\u0000\u000089\u0005l\u0000\u00009:\u0005s\u0000\u0000:;\u0005e\u0000\u0000;\u0010\u0001\u0000\u0000\u0000<=\u0005n\u0000\u0000=>\u0005u\u0000\u0000>?\u0005l\u0000\u0000?@\u0005l\u0000\u0000@\u0012\u0001\u0000\u0000\u0000AF\u0005\"\u0000\u0000BE\u0003\u0015\n\u0000CE\u0003\u001b\r\u0000DB\u0001\u0000\u0000\u0000DC\u0001\u0000\u0000\u0000EH\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GI\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000IJ\u0005\"\u0000\u0000J\u0014\u0001\u0000\u0000\u0000KN\u0005\\\u0000\u0000LO\u0007\u0000\u0000\u0000MO\u0003\u0017\u000b\u0000NL\u0001\u0000\u0000\u0000NM\u0001\u0000\u0000\u0000O\u0016\u0001\u0000\u0000\u0000PQ\u0005u\u0000\u0000QR\u0003\u0019\f\u0000RS\u0003\u0019\f\u0000ST\u0003\u0019\f\u0000TU\u0003\u0019\f\u0000U\u0018\u0001\u0000\u0000\u0000VW\u0007\u0001\u0000\u0000W\u001a\u0001\u0000\u0000\u0000XY\b\u0002\u0000\u0000Y\u001c\u0001\u0000\u0000\u0000Z\\\u0005-\u0000\u0000[Z\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000]d\u0003\u001f\u000f\u0000^`\u0005.\u0000\u0000_a\u0007\u0003\u0000\u0000`_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000ce\u0001\u0000\u0000\u0000d^\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000eg\u0001\u0000\u0000\u0000fh\u0003!\u0010\u0000gf\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000h\u001e\u0001\u0000\u0000\u0000ir\u00050\u0000\u0000jn\u0007\u0004\u0000\u0000km\u0007\u0003\u0000\u0000lk\u0001\u0000\u0000\u0000mp\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000or\u0001\u0000\u0000\u0000pn\u0001\u0000\u0000\u0000qi\u0001\u0000\u0000\u0000qj\u0001\u0000\u0000\u0000r \u0001\u0000\u0000\u0000su\u0007\u0005\u0000\u0000tv\u0007\u0006\u0000\u0000ut\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000wx\u0003\u001f\u000f\u0000x\"\u0001\u0000\u0000\u0000y{\u0007\u0007\u0000\u0000zy\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|z\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~\u007f\u0006\u0011\u0000\u0000\u007f$\u0001\u0000\u0000\u0000\f\u0000DFN[bdgnqu|\u0001\u0006\u0000\u0000";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", "STRING", "ESC", "UNICODE", "HEX", "SAFECODEPOINT", "NUMBER", "INT", "EXP", "WS"};
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

    public JsonLexer(CharStream input) {
        super(input);
        this._interp = new LexerATNSimulator((Lexer)this, _ATN, _decisionToDFA, _sharedContextCache);
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
        ruleNames = JsonLexer.makeRuleNames();
        _LITERAL_NAMES = JsonLexer.makeLiteralNames();
        _SYMBOLIC_NAMES = JsonLexer.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            JsonLexer.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                JsonLexer.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            JsonLexer.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            JsonLexer._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}

