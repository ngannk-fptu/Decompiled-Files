/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.DefaultErrorStrategy
 *  org.antlr.v4.runtime.IntStream
 *  org.antlr.v4.runtime.Parser
 *  org.antlr.v4.runtime.RecognitionException
 *  org.antlr.v4.runtime.Recognizer
 *  org.antlr.v4.runtime.Token
 *  org.antlr.v4.runtime.TokenStream
 *  org.antlr.v4.runtime.atn.ATNState
 *  org.antlr.v4.runtime.atn.ParserATNSimulator
 *  org.antlr.v4.runtime.misc.IntervalSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.csskit.antlr4.CSSLexerState;
import cz.vutbr.web.csskit.antlr4.CSSToken;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSSErrorStrategy
extends DefaultErrorStrategy {
    private Logger logger = LoggerFactory.getLogger(((Object)((Object)this)).getClass());

    public CSSErrorStrategy() {
        this.logger.trace("CssErrorStrategy instantiated");
    }

    public void sync(Parser recognizer) throws RecognitionException {
        ATNState s = (ATNState)((ParserATNSimulator)recognizer.getInterpreter()).atn.states.get(recognizer.getState());
        if (!this.inErrorRecoveryMode(recognizer)) {
            TokenStream tokens = recognizer.getInputStream();
            int la = tokens.LA(1);
            if (!recognizer.getATN().nextTokens(s).contains(la) && la != -1 && !recognizer.isExpectedToken(la)) {
                switch (s.getStateType()) {
                    case 3: 
                    case 4: 
                    case 5: 
                    case 10: {
                        throw new RecognitionException((Recognizer)recognizer, (IntStream)tokens, recognizer.getContext());
                    }
                    case 9: 
                    case 11: {
                        this.reportUnwantedToken(recognizer);
                        throw new RecognitionException((Recognizer)recognizer, (IntStream)tokens, recognizer.getContext());
                    }
                }
            }
        }
    }

    public Token recoverInline(Parser recognizer) throws RecognitionException {
        throw new RecognitionException((Recognizer)recognizer, (IntStream)recognizer.getInputStream(), recognizer.getContext());
    }

    protected void consumeUntilGreedy(Parser recognizer, IntervalSet follow) {
        this.logger.trace("CONSUME UNTIL GREEDY {}", (Object)follow.toString());
        int ttype = recognizer.getInputStream().LA(1);
        while (ttype != -1 && !follow.contains(ttype)) {
            Token t = recognizer.consume();
            this.logger.trace("Skipped greedy: {}", (Object)t.getText());
            ttype = recognizer.getInputStream().LA(1);
        }
        Token t = recognizer.consume();
        this.logger.trace("Skipped greedy: {} follow: {}", (Object)t.getText(), (Object)follow);
    }

    protected void consumeUntilGreedy(Parser recognizer, IntervalSet set, CSSLexerState.RecoveryMode mode) {
        Token next;
        while ((next = recognizer.getInputStream().LT(1)) instanceof CSSToken) {
            CSSToken t = (CSSToken)recognizer.getInputStream().LT(1);
            if (t.getType() == -1) {
                this.logger.trace("token eof ");
                break;
            }
            this.logger.trace("Skipped greedy: {}", (Object)t.getText());
            recognizer.consume();
            if (!t.getLexerState().isBalanced(mode, null, t) || !set.contains(t.getType())) continue;
        }
    }

    public void consumeUntilGreedy(Parser recognizer, IntervalSet follow, CSSLexerState.RecoveryMode mode, CSSLexerState ls) {
        this.consumeUntil(recognizer, follow, mode, ls);
        recognizer.getInputStream().consume();
    }

    public void consumeUntil(Parser recognizer, IntervalSet follow, CSSLexerState.RecoveryMode mode, CSSLexerState ls) {
        Token next;
        TokenStream input = recognizer.getInputStream();
        while ((next = input.LT(1)) instanceof CSSToken) {
            boolean finish;
            CSSToken t = (CSSToken)input.LT(1);
            if (t.getType() == -1) {
                this.logger.trace("token eof ");
                break;
            }
            boolean bl = finish = t.getLexerState().isBalanced(mode, ls, t) && follow.contains(t.getType());
            if (!finish) {
                this.logger.trace("Skipped: {}", (Object)t);
                input.consume();
            }
            if (!finish) continue;
        }
    }
}

