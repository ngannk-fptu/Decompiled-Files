/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import java.util.Map;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.EmptyPredictionContext;
import org.antlr.v4.runtime.atn.LexerATNConfig;
import org.antlr.v4.runtime.atn.LexerActionExecutor;
import org.antlr.v4.runtime.atn.OrderedATNConfigSet;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.atn.RuleStopState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.SingletonPredictionContext;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.Interval;

public class LexerATNSimulator
extends ATNSimulator {
    public static final boolean debug = false;
    public static final boolean dfa_debug = false;
    public static final int MIN_DFA_EDGE = 0;
    public static final int MAX_DFA_EDGE = 127;
    protected final Lexer recog;
    protected int startIndex = -1;
    protected int line = 1;
    protected int charPositionInLine = 0;
    public final DFA[] decisionToDFA;
    protected int mode = 0;
    protected final SimState prevAccept = new SimState();
    public static int match_calls = 0;

    public LexerATNSimulator(ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
        this(null, atn, decisionToDFA, sharedContextCache);
    }

    public LexerATNSimulator(Lexer recog, ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
        super(atn, sharedContextCache);
        this.decisionToDFA = decisionToDFA;
        this.recog = recog;
    }

    public void copyState(LexerATNSimulator simulator) {
        this.charPositionInLine = simulator.charPositionInLine;
        this.line = simulator.line;
        this.mode = simulator.mode;
        this.startIndex = simulator.startIndex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int match(CharStream input, int mode) {
        ++match_calls;
        this.mode = mode;
        int mark = input.mark();
        try {
            this.startIndex = input.index();
            this.prevAccept.reset();
            DFA dfa = this.decisionToDFA[mode];
            if (dfa.s0 == null) {
                int n = this.matchATN(input);
                return n;
            }
            int n = this.execATN(input, dfa.s0);
            return n;
        }
        finally {
            input.release(mark);
        }
    }

    @Override
    public void reset() {
        this.prevAccept.reset();
        this.startIndex = -1;
        this.line = 1;
        this.charPositionInLine = 0;
        this.mode = 0;
    }

    @Override
    public void clearDFA() {
        for (int d = 0; d < this.decisionToDFA.length; ++d) {
            this.decisionToDFA[d] = new DFA(this.atn.getDecisionState(d), d);
        }
    }

    protected int matchATN(CharStream input) {
        ATNState startState = this.atn.modeToStartState.get(this.mode);
        int old_mode = this.mode;
        ATNConfigSet s0_closure = this.computeStartState(input, startState);
        boolean suppressEdge = s0_closure.hasSemanticContext;
        s0_closure.hasSemanticContext = false;
        DFAState next = this.addDFAState(s0_closure);
        if (!suppressEdge) {
            this.decisionToDFA[this.mode].s0 = next;
        }
        int predict = this.execATN(input, next);
        return predict;
    }

    protected int execATN(CharStream input, DFAState ds0) {
        if (ds0.isAcceptState) {
            this.captureSimState(this.prevAccept, input, ds0);
        }
        int t = input.LA(1);
        DFAState s = ds0;
        while (true) {
            DFAState target;
            if ((target = this.getExistingTargetState(s, t)) == null) {
                target = this.computeTargetState(input, s, t);
            }
            if (target == ERROR) break;
            if (t != -1) {
                this.consume(input);
            }
            if (target.isAcceptState) {
                this.captureSimState(this.prevAccept, input, target);
                if (t == -1) break;
            }
            t = input.LA(1);
            s = target;
        }
        return this.failOrAccept(this.prevAccept, input, s.configs, t);
    }

    protected DFAState getExistingTargetState(DFAState s, int t) {
        if (s.edges == null || t < 0 || t > 127) {
            return null;
        }
        DFAState target = s.edges[t - 0];
        return target;
    }

    protected DFAState computeTargetState(CharStream input, DFAState s, int t) {
        OrderedATNConfigSet reach = new OrderedATNConfigSet();
        this.getReachableConfigSet(input, s.configs, reach, t);
        if (reach.isEmpty()) {
            if (!reach.hasSemanticContext) {
                this.addDFAEdge(s, t, ERROR);
            }
            return ERROR;
        }
        return this.addDFAEdge(s, t, reach);
    }

    protected int failOrAccept(SimState prevAccept, CharStream input, ATNConfigSet reach, int t) {
        if (prevAccept.dfaState != null) {
            LexerActionExecutor lexerActionExecutor = prevAccept.dfaState.lexerActionExecutor;
            this.accept(input, lexerActionExecutor, this.startIndex, prevAccept.index, prevAccept.line, prevAccept.charPos);
            return prevAccept.dfaState.prediction;
        }
        if (t == -1 && input.index() == this.startIndex) {
            return -1;
        }
        throw new LexerNoViableAltException(this.recog, input, this.startIndex, reach);
    }

    protected void getReachableConfigSet(CharStream input, ATNConfigSet closure, ATNConfigSet reach, int t) {
        int skipAlt = 0;
        block0: for (ATNConfig c : closure) {
            boolean currentAltReachedAcceptState;
            boolean bl = currentAltReachedAcceptState = c.alt == skipAlt;
            if (currentAltReachedAcceptState && ((LexerATNConfig)c).hasPassedThroughNonGreedyDecision()) continue;
            int n = c.state.getNumberOfTransitions();
            for (int ti = 0; ti < n; ++ti) {
                boolean treatEofAsEpsilon;
                Transition trans = c.state.transition(ti);
                ATNState target = this.getReachableTarget(trans, t);
                if (target == null) continue;
                LexerActionExecutor lexerActionExecutor = ((LexerATNConfig)c).getLexerActionExecutor();
                if (lexerActionExecutor != null) {
                    lexerActionExecutor = lexerActionExecutor.fixOffsetBeforeMatch(input.index() - this.startIndex);
                }
                boolean bl2 = treatEofAsEpsilon = t == -1;
                if (!this.closure(input, new LexerATNConfig((LexerATNConfig)c, target, lexerActionExecutor), reach, currentAltReachedAcceptState, true, treatEofAsEpsilon)) continue;
                skipAlt = c.alt;
                continue block0;
            }
        }
    }

    protected void accept(CharStream input, LexerActionExecutor lexerActionExecutor, int startIndex, int index, int line, int charPos) {
        input.seek(index);
        this.line = line;
        this.charPositionInLine = charPos;
        if (lexerActionExecutor != null && this.recog != null) {
            lexerActionExecutor.execute(this.recog, input, startIndex);
        }
    }

    protected ATNState getReachableTarget(Transition trans, int t) {
        if (trans.matches(t, 0, 0x10FFFF)) {
            return trans.target;
        }
        return null;
    }

    protected ATNConfigSet computeStartState(CharStream input, ATNState p) {
        EmptyPredictionContext initialContext = PredictionContext.EMPTY;
        OrderedATNConfigSet configs = new OrderedATNConfigSet();
        for (int i = 0; i < p.getNumberOfTransitions(); ++i) {
            ATNState target = p.transition((int)i).target;
            LexerATNConfig c = new LexerATNConfig(target, i + 1, (PredictionContext)initialContext);
            this.closure(input, c, configs, false, false, false);
        }
        return configs;
    }

    protected boolean closure(CharStream input, LexerATNConfig config, ATNConfigSet configs, boolean currentAltReachedAcceptState, boolean speculative, boolean treatEofAsEpsilon) {
        if (config.state instanceof RuleStopState) {
            if (config.context == null || config.context.hasEmptyPath()) {
                if (config.context == null || config.context.isEmpty()) {
                    configs.add(config);
                    return true;
                }
                configs.add(new LexerATNConfig(config, config.state, (PredictionContext)PredictionContext.EMPTY));
                currentAltReachedAcceptState = true;
            }
            if (config.context != null && !config.context.isEmpty()) {
                for (int i = 0; i < config.context.size(); ++i) {
                    if (config.context.getReturnState(i) == Integer.MAX_VALUE) continue;
                    PredictionContext newContext = config.context.getParent(i);
                    ATNState returnState = this.atn.states.get(config.context.getReturnState(i));
                    LexerATNConfig c = new LexerATNConfig(config, returnState, newContext);
                    currentAltReachedAcceptState = this.closure(input, c, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon);
                }
            }
            return currentAltReachedAcceptState;
        }
        if (!(config.state.onlyHasEpsilonTransitions() || currentAltReachedAcceptState && config.hasPassedThroughNonGreedyDecision())) {
            configs.add(config);
        }
        ATNState p = config.state;
        for (int i = 0; i < p.getNumberOfTransitions(); ++i) {
            Transition t = p.transition(i);
            LexerATNConfig c = this.getEpsilonTarget(input, config, t, configs, speculative, treatEofAsEpsilon);
            if (c == null) continue;
            currentAltReachedAcceptState = this.closure(input, c, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon);
        }
        return currentAltReachedAcceptState;
    }

    protected LexerATNConfig getEpsilonTarget(CharStream input, LexerATNConfig config, Transition t, ATNConfigSet configs, boolean speculative, boolean treatEofAsEpsilon) {
        LexerATNConfig c = null;
        switch (t.getSerializationType()) {
            case 3: {
                RuleTransition ruleTransition = (RuleTransition)t;
                SingletonPredictionContext newContext = SingletonPredictionContext.create(config.context, ruleTransition.followState.stateNumber);
                c = new LexerATNConfig(config, t.target, (PredictionContext)newContext);
                break;
            }
            case 10: {
                throw new UnsupportedOperationException("Precedence predicates are not supported in lexers.");
            }
            case 4: {
                PredicateTransition pt = (PredicateTransition)t;
                configs.hasSemanticContext = true;
                if (!this.evaluatePredicate(input, pt.ruleIndex, pt.predIndex, speculative)) break;
                c = new LexerATNConfig(config, t.target);
                break;
            }
            case 6: {
                if (config.context == null || config.context.hasEmptyPath()) {
                    LexerActionExecutor lexerActionExecutor = LexerActionExecutor.append(config.getLexerActionExecutor(), this.atn.lexerActions[((ActionTransition)t).actionIndex]);
                    c = new LexerATNConfig(config, t.target, lexerActionExecutor);
                    break;
                }
                c = new LexerATNConfig(config, t.target);
                break;
            }
            case 1: {
                c = new LexerATNConfig(config, t.target);
                break;
            }
            case 2: 
            case 5: 
            case 7: {
                if (!treatEofAsEpsilon || !t.matches(-1, 0, 0x10FFFF)) break;
                c = new LexerATNConfig(config, t.target);
            }
        }
        return c;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean evaluatePredicate(CharStream input, int ruleIndex, int predIndex, boolean speculative) {
        if (this.recog == null) {
            return true;
        }
        if (!speculative) {
            return this.recog.sempred(null, ruleIndex, predIndex);
        }
        int savedCharPositionInLine = this.charPositionInLine;
        int savedLine = this.line;
        int index = input.index();
        int marker = input.mark();
        try {
            this.consume(input);
            boolean bl = this.recog.sempred(null, ruleIndex, predIndex);
            return bl;
        }
        finally {
            this.charPositionInLine = savedCharPositionInLine;
            this.line = savedLine;
            input.seek(index);
            input.release(marker);
        }
    }

    protected void captureSimState(SimState settings, CharStream input, DFAState dfaState) {
        settings.index = input.index();
        settings.line = this.line;
        settings.charPos = this.charPositionInLine;
        settings.dfaState = dfaState;
    }

    protected DFAState addDFAEdge(DFAState from, int t, ATNConfigSet q) {
        boolean suppressEdge = q.hasSemanticContext;
        q.hasSemanticContext = false;
        DFAState to = this.addDFAState(q);
        if (suppressEdge) {
            return to;
        }
        this.addDFAEdge(from, t, to);
        return to;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addDFAEdge(DFAState p, int t, DFAState q) {
        if (t < 0 || t > 127) {
            return;
        }
        DFAState dFAState = p;
        synchronized (dFAState) {
            if (p.edges == null) {
                p.edges = new DFAState[128];
            }
            p.edges[t - 0] = q;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DFAState addDFAState(ATNConfigSet configs) {
        assert (!configs.hasSemanticContext);
        DFAState proposed = new DFAState(configs);
        ATNConfig firstConfigWithRuleStopState = null;
        for (ATNConfig c : configs) {
            if (!(c.state instanceof RuleStopState)) continue;
            firstConfigWithRuleStopState = c;
            break;
        }
        if (firstConfigWithRuleStopState != null) {
            proposed.isAcceptState = true;
            proposed.lexerActionExecutor = ((LexerATNConfig)firstConfigWithRuleStopState).getLexerActionExecutor();
            proposed.prediction = this.atn.ruleToTokenType[firstConfigWithRuleStopState.state.ruleIndex];
        }
        DFA dfa = this.decisionToDFA[this.mode];
        Map<DFAState, DFAState> map = dfa.states;
        synchronized (map) {
            DFAState existing = dfa.states.get(proposed);
            if (existing != null) {
                return existing;
            }
            DFAState newState = proposed;
            newState.stateNumber = dfa.states.size();
            configs.setReadonly(true);
            newState.configs = configs;
            dfa.states.put(newState, newState);
            return newState;
        }
    }

    public final DFA getDFA(int mode) {
        return this.decisionToDFA[mode];
    }

    public String getText(CharStream input) {
        return input.getText(Interval.of(this.startIndex, input.index() - 1));
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getCharPositionInLine() {
        return this.charPositionInLine;
    }

    public void setCharPositionInLine(int charPositionInLine) {
        this.charPositionInLine = charPositionInLine;
    }

    public void consume(CharStream input) {
        int curChar = input.LA(1);
        if (curChar == 10) {
            ++this.line;
            this.charPositionInLine = 0;
        } else {
            ++this.charPositionInLine;
        }
        input.consume();
    }

    public String getTokenName(int t) {
        if (t == -1) {
            return "EOF";
        }
        return "'" + (char)t + "'";
    }

    protected static class SimState {
        protected int index = -1;
        protected int line = 0;
        protected int charPos = -1;
        protected DFAState dfaState;

        protected SimState() {
        }

        protected void reset() {
            this.index = -1;
            this.line = 0;
            this.charPos = -1;
            this.dfaState = null;
        }
    }
}

