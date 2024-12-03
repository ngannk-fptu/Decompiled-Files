/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.BaseErrorListener
 *  org.antlr.v4.runtime.Parser
 *  org.antlr.v4.runtime.RecognitionException
 *  org.antlr.v4.runtime.Recognizer
 *  org.antlr.v4.runtime.atn.ATNConfigSet
 *  org.antlr.v4.runtime.dfa.DFA
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.csskit.antlr4;

import java.util.BitSet;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSSErrorListener
extends BaseErrorListener {
    private Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());

    public void syntaxError(Recognizer<?, ?> recognizer, Object o, int i, int i1, String s, RecognitionException e) {
        this.log.error("CSSErrorListener syntaxError | " + s);
    }

    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
        this.log.error("CSSErrorListener ambiguity | ");
    }

    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
        this.log.error("CSSErrorListener full context | ");
    }

    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
        this.log.error("CSSErrorListener context sensitivity | ");
    }
}

