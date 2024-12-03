/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializationOptions;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ATNType;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.BasicBlockStartState;
import org.antlr.v4.runtime.atn.BasicState;
import org.antlr.v4.runtime.atn.BlockEndState;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.EpsilonTransition;
import org.antlr.v4.runtime.atn.LexerAction;
import org.antlr.v4.runtime.atn.LexerActionType;
import org.antlr.v4.runtime.atn.LexerChannelAction;
import org.antlr.v4.runtime.atn.LexerCustomAction;
import org.antlr.v4.runtime.atn.LexerModeAction;
import org.antlr.v4.runtime.atn.LexerMoreAction;
import org.antlr.v4.runtime.atn.LexerPopModeAction;
import org.antlr.v4.runtime.atn.LexerPushModeAction;
import org.antlr.v4.runtime.atn.LexerSkipAction;
import org.antlr.v4.runtime.atn.LexerTypeAction;
import org.antlr.v4.runtime.atn.LoopEndState;
import org.antlr.v4.runtime.atn.NotSetTransition;
import org.antlr.v4.runtime.atn.PlusBlockStartState;
import org.antlr.v4.runtime.atn.PlusLoopbackState;
import org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.atn.RangeTransition;
import org.antlr.v4.runtime.atn.RuleStartState;
import org.antlr.v4.runtime.atn.RuleStopState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.SetTransition;
import org.antlr.v4.runtime.atn.StarBlockStartState;
import org.antlr.v4.runtime.atn.StarLoopEntryState;
import org.antlr.v4.runtime.atn.StarLoopbackState;
import org.antlr.v4.runtime.atn.TokensStartState;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.atn.WildcardTransition;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;

public class ATNDeserializer {
    public static final int SERIALIZED_VERSION = 3;
    private static final UUID BASE_SERIALIZED_UUID = UUID.fromString("33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3");
    private static final UUID ADDED_PRECEDENCE_TRANSITIONS = UUID.fromString("1DA0C57D-6C06-438A-9B27-10BCB3CE0F61");
    private static final UUID ADDED_LEXER_ACTIONS = UUID.fromString("AADB8D7E-AEEF-4415-AD2B-8204D6CF042E");
    private static final UUID ADDED_UNICODE_SMP = UUID.fromString("59627784-3BE5-417A-B9EB-8131A7286089");
    private static final List<UUID> SUPPORTED_UUIDS = new ArrayList<UUID>();
    public static final UUID SERIALIZED_UUID;
    private final ATNDeserializationOptions deserializationOptions;

    static UnicodeDeserializer getUnicodeDeserializer(UnicodeDeserializingMode mode) {
        if (mode == UnicodeDeserializingMode.UNICODE_BMP) {
            return new UnicodeDeserializer(){

                @Override
                public int readUnicode(char[] data, int p) {
                    return ATNDeserializer.toInt(data[p]);
                }

                @Override
                public int size() {
                    return 1;
                }
            };
        }
        return new UnicodeDeserializer(){

            @Override
            public int readUnicode(char[] data, int p) {
                return ATNDeserializer.toInt32(data, p);
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    public ATNDeserializer() {
        this(ATNDeserializationOptions.getDefaultOptions());
    }

    public ATNDeserializer(ATNDeserializationOptions deserializationOptions) {
        if (deserializationOptions == null) {
            deserializationOptions = ATNDeserializationOptions.getDefaultOptions();
        }
        this.deserializationOptions = deserializationOptions;
    }

    protected static boolean isFeatureSupported(UUID feature, UUID actualUuid) {
        int featureIndex = SUPPORTED_UUIDS.indexOf(feature);
        if (featureIndex < 0) {
            return false;
        }
        return SUPPORTED_UUIDS.indexOf(actualUuid) >= featureIndex;
    }

    /*
     * WARNING - void declaration
     */
    public ATN deserialize(char[] data) {
        int i;
        void var14_22;
        int version;
        data = (char[])data.clone();
        for (int i3 = 1; i3 < data.length; ++i3) {
            data[i3] = (char)(data[i3] - 2);
        }
        int p = 0;
        if ((version = ATNDeserializer.toInt(data[p++])) != SERIALIZED_VERSION) {
            String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with version %d (expected %d).", version, SERIALIZED_VERSION);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
        }
        UUID uuid = ATNDeserializer.toUUID(data, p);
        p += 8;
        if (!SUPPORTED_UUIDS.contains(uuid)) {
            String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s or a legacy UUID).", uuid, SERIALIZED_UUID);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
        }
        boolean supportsPrecedencePredicates = ATNDeserializer.isFeatureSupported(ADDED_PRECEDENCE_TRANSITIONS, uuid);
        boolean supportsLexerActions = ATNDeserializer.isFeatureSupported(ADDED_LEXER_ACTIONS, uuid);
        ATNType grammarType = ATNType.values()[ATNDeserializer.toInt(data[p++])];
        int maxTokenType = ATNDeserializer.toInt(data[p++]);
        ATN atn = new ATN(grammarType, maxTokenType);
        ArrayList<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Pair<LoopEndState, Integer>>();
        ArrayList<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList<Pair<BlockStartState, Integer>>();
        int nstates = ATNDeserializer.toInt(data[p++]);
        for (int i4 = 0; i4 < nstates; ++i4) {
            int ruleIndex;
            int n;
            if ((n = ATNDeserializer.toInt(data[p++])) == 0) {
                atn.addState(null);
                continue;
            }
            if ((ruleIndex = ATNDeserializer.toInt(data[p++])) == 65535) {
                ruleIndex = -1;
            }
            ATNState s = this.stateFactory(n, ruleIndex);
            if (n == 12) {
                int loopBackStateNumber = ATNDeserializer.toInt(data[p++]);
                loopBackStateNumbers.add(new Pair<LoopEndState, Integer>((LoopEndState)s, loopBackStateNumber));
            } else if (s instanceof BlockStartState) {
                int endStateNumber = ATNDeserializer.toInt(data[p++]);
                endStateNumbers.add(new Pair<BlockStartState, Integer>((BlockStartState)s, endStateNumber));
            }
            atn.addState(s);
        }
        for (Pair pair : loopBackStateNumbers) {
            ((LoopEndState)pair.a).loopBackState = atn.states.get((Integer)pair.b);
        }
        for (Pair pair : endStateNumbers) {
            ((BlockStartState)pair.a).endState = (BlockEndState)atn.states.get((Integer)pair.b);
        }
        int numNonGreedyStates = ATNDeserializer.toInt(data[p++]);
        boolean bl = false;
        while (var14_22 < numNonGreedyStates) {
            int stateNumber = ATNDeserializer.toInt(data[p++]);
            ((DecisionState)atn.states.get((int)stateNumber)).nonGreedy = true;
            ++var14_22;
        }
        if (supportsPrecedencePredicates) {
            int n = ATNDeserializer.toInt(data[p++]);
            for (int i2 = 0; i2 < n; ++i2) {
                int stateNumber = ATNDeserializer.toInt(data[p++]);
                ((RuleStartState)atn.states.get((int)stateNumber)).isLeftRecursiveRule = true;
            }
        }
        int n = ATNDeserializer.toInt(data[p++]);
        if (atn.grammarType == ATNType.LEXER) {
            atn.ruleToTokenType = new int[n];
        }
        atn.ruleToStartState = new RuleStartState[n];
        for (int i2 = 0; i2 < n; ++i2) {
            int tokenType;
            RuleStartState startState;
            int s = ATNDeserializer.toInt(data[p++]);
            atn.ruleToStartState[i2] = startState = (RuleStartState)atn.states.get(s);
            if (atn.grammarType != ATNType.LEXER) continue;
            if ((tokenType = ATNDeserializer.toInt(data[p++])) == 65535) {
                tokenType = -1;
            }
            atn.ruleToTokenType[i2] = tokenType;
            if (ATNDeserializer.isFeatureSupported(ADDED_LEXER_ACTIONS, uuid)) continue;
            int actionIndexIgnored = ATNDeserializer.toInt(data[p++]);
        }
        atn.ruleToStopState = new RuleStopState[n];
        for (ATNState state : atn.states) {
            RuleStopState stopState;
            if (!(state instanceof RuleStopState)) continue;
            atn.ruleToStopState[state.ruleIndex] = stopState = (RuleStopState)state;
            atn.ruleToStartState[state.ruleIndex].stopState = stopState;
        }
        int nmodes = ATNDeserializer.toInt(data[p++]);
        for (int i6 = 0; i6 < nmodes; ++i6) {
            int s = ATNDeserializer.toInt(data[p++]);
            atn.modeToStartState.add((TokensStartState)atn.states.get(s));
        }
        ArrayList<IntervalSet> sets = new ArrayList<IntervalSet>();
        p = this.deserializeSets(data, p, sets, ATNDeserializer.getUnicodeDeserializer(UnicodeDeserializingMode.UNICODE_BMP));
        if (ATNDeserializer.isFeatureSupported(ADDED_UNICODE_SMP, uuid)) {
            p = this.deserializeSets(data, p, sets, ATNDeserializer.getUnicodeDeserializer(UnicodeDeserializingMode.UNICODE_SMP));
        }
        int nedges = ATNDeserializer.toInt(data[p++]);
        for (int i7 = 0; i7 < nedges; ++i7) {
            int src = ATNDeserializer.toInt(data[p]);
            int trg = ATNDeserializer.toInt(data[p + 1]);
            int ttype = ATNDeserializer.toInt(data[p + 2]);
            int arg1 = ATNDeserializer.toInt(data[p + 3]);
            int arg2 = ATNDeserializer.toInt(data[p + 4]);
            int arg3 = ATNDeserializer.toInt(data[p + 5]);
            Transition trans = this.edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
            ATNState srcState = atn.states.get(src);
            srcState.addTransition(trans);
            p += 6;
        }
        for (ATNState state : atn.states) {
            for (int i8 = 0; i8 < state.getNumberOfTransitions(); ++i8) {
                Transition t = state.transition(i8);
                if (!(t instanceof RuleTransition)) continue;
                RuleTransition ruleTransition = (RuleTransition)t;
                int outermostPrecedenceReturn = -1;
                if (atn.ruleToStartState[ruleTransition.target.ruleIndex].isLeftRecursiveRule && ruleTransition.precedence == 0) {
                    outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
                }
                EpsilonTransition returnTransition = new EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn);
                atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(returnTransition);
            }
        }
        for (ATNState state : atn.states) {
            ATNState target;
            ATNState loopbackState;
            if (state instanceof BlockStartState) {
                if (((BlockStartState)state).endState == null) {
                    throw new IllegalStateException();
                }
                if (((BlockStartState)state).endState.startState != null) {
                    throw new IllegalStateException();
                }
                ((BlockStartState)state).endState.startState = (BlockStartState)state;
            }
            if (state instanceof PlusLoopbackState) {
                loopbackState = (PlusLoopbackState)state;
                for (int i9 = 0; i9 < loopbackState.getNumberOfTransitions(); ++i9) {
                    target = loopbackState.transition((int)i9).target;
                    if (!(target instanceof PlusBlockStartState)) continue;
                    ((PlusBlockStartState)target).loopBackState = loopbackState;
                }
                continue;
            }
            if (!(state instanceof StarLoopbackState)) continue;
            loopbackState = (StarLoopbackState)state;
            for (int i3 = 0; i3 < loopbackState.getNumberOfTransitions(); ++i3) {
                target = loopbackState.transition((int)i3).target;
                if (!(target instanceof StarLoopEntryState)) continue;
                ((StarLoopEntryState)target).loopBackState = loopbackState;
            }
        }
        int ndecisions = ATNDeserializer.toInt(data[p++]);
        for (i = 1; i <= ndecisions; ++i) {
            int s = ATNDeserializer.toInt(data[p++]);
            DecisionState decState = (DecisionState)atn.states.get(s);
            atn.decisionToState.add(decState);
            decState.decision = i - 1;
        }
        if (atn.grammarType == ATNType.LEXER) {
            if (supportsLexerActions) {
                atn.lexerActions = new LexerAction[ATNDeserializer.toInt(data[p++])];
                for (i = 0; i < atn.lexerActions.length; ++i) {
                    LexerAction lexerAction;
                    int data2;
                    int data1;
                    LexerActionType actionType = LexerActionType.values()[ATNDeserializer.toInt(data[p++])];
                    if ((data1 = ATNDeserializer.toInt(data[p++])) == 65535) {
                        data1 = -1;
                    }
                    if ((data2 = ATNDeserializer.toInt(data[p++])) == 65535) {
                        data2 = -1;
                    }
                    atn.lexerActions[i] = lexerAction = this.lexerActionFactory(actionType, data1, data2);
                }
            } else {
                ArrayList<LexerCustomAction> legacyLexerActions = new ArrayList<LexerCustomAction>();
                for (ATNState state : atn.states) {
                    for (int i10 = 0; i10 < state.getNumberOfTransitions(); ++i10) {
                        Transition transition = state.transition(i10);
                        if (!(transition instanceof ActionTransition)) continue;
                        int ruleIndex = ((ActionTransition)transition).ruleIndex;
                        int actionIndex = ((ActionTransition)transition).actionIndex;
                        LexerCustomAction lexerAction = new LexerCustomAction(ruleIndex, actionIndex);
                        state.setTransition(i10, new ActionTransition(transition.target, ruleIndex, legacyLexerActions.size(), false));
                        legacyLexerActions.add(lexerAction);
                    }
                }
                atn.lexerActions = legacyLexerActions.toArray(new LexerAction[legacyLexerActions.size()]);
            }
        }
        this.markPrecedenceDecisions(atn);
        if (this.deserializationOptions.isVerifyATN()) {
            this.verifyATN(atn);
        }
        if (this.deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType.PARSER) {
            atn.ruleToTokenType = new int[atn.ruleToStartState.length];
            for (int i11 = 0; i11 < atn.ruleToStartState.length; ++i11) {
                atn.ruleToTokenType[i11] = atn.maxTokenType + i11 + 1;
            }
            for (int i4 = 0; i4 < atn.ruleToStartState.length; ++i4) {
                ATNState endState;
                BasicBlockStartState bypassStart = new BasicBlockStartState();
                bypassStart.ruleIndex = i4;
                atn.addState(bypassStart);
                BlockEndState bypassStop = new BlockEndState();
                bypassStop.ruleIndex = i4;
                atn.addState(bypassStop);
                bypassStart.endState = bypassStop;
                atn.defineDecisionState(bypassStart);
                bypassStop.startState = bypassStart;
                Transition excludeTransition = null;
                if (atn.ruleToStartState[i4].isLeftRecursiveRule) {
                    endState = null;
                    for (ATNState state : atn.states) {
                        ATNState maybeLoopEndState;
                        if (state.ruleIndex != i4 || !(state instanceof StarLoopEntryState) || !((maybeLoopEndState = state.transition((int)(state.getNumberOfTransitions() - 1)).target) instanceof LoopEndState) || !maybeLoopEndState.epsilonOnlyTransitions || !(maybeLoopEndState.transition((int)0).target instanceof RuleStopState)) continue;
                        endState = state;
                        break;
                    }
                    if (endState == null) {
                        throw new UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");
                    }
                    excludeTransition = ((StarLoopEntryState)endState).loopBackState.transition(0);
                } else {
                    endState = atn.ruleToStopState[i4];
                }
                for (ATNState state : atn.states) {
                    for (Transition transition : state.transitions) {
                        if (transition == excludeTransition || transition.target != endState) continue;
                        transition.target = bypassStop;
                    }
                }
                while (atn.ruleToStartState[i4].getNumberOfTransitions() > 0) {
                    Transition transition = atn.ruleToStartState[i4].removeTransition(atn.ruleToStartState[i4].getNumberOfTransitions() - 1);
                    bypassStart.addTransition(transition);
                }
                atn.ruleToStartState[i4].addTransition(new EpsilonTransition(bypassStart));
                bypassStop.addTransition(new EpsilonTransition(endState));
                BasicState matchState = new BasicState();
                atn.addState(matchState);
                matchState.addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i4]));
                bypassStart.addTransition(new EpsilonTransition(matchState));
            }
            if (this.deserializationOptions.isVerifyATN()) {
                this.verifyATN(atn);
            }
        }
        return atn;
    }

    private int deserializeSets(char[] data, int p, List<IntervalSet> sets, UnicodeDeserializer unicodeDeserializer) {
        int nsets = ATNDeserializer.toInt(data[p++]);
        for (int i = 0; i < nsets; ++i) {
            boolean containsEof;
            int nintervals = ATNDeserializer.toInt(data[p]);
            IntervalSet set = new IntervalSet(new int[0]);
            sets.add(set);
            int n = ++p;
            ++p;
            boolean bl = containsEof = ATNDeserializer.toInt(data[n]) != 0;
            if (containsEof) {
                set.add(-1);
            }
            for (int j = 0; j < nintervals; ++j) {
                int a = unicodeDeserializer.readUnicode(data, p);
                int b = unicodeDeserializer.readUnicode(data, p += unicodeDeserializer.size());
                p += unicodeDeserializer.size();
                set.add(a, b);
            }
        }
        return p;
    }

    protected void markPrecedenceDecisions(ATN atn) {
        for (ATNState state : atn.states) {
            ATNState maybeLoopEndState;
            if (!(state instanceof StarLoopEntryState) || !atn.ruleToStartState[state.ruleIndex].isLeftRecursiveRule || !((maybeLoopEndState = state.transition((int)(state.getNumberOfTransitions() - 1)).target) instanceof LoopEndState) || !maybeLoopEndState.epsilonOnlyTransitions || !(maybeLoopEndState.transition((int)0).target instanceof RuleStopState)) continue;
            ((StarLoopEntryState)state).isPrecedenceDecision = true;
        }
    }

    protected void verifyATN(ATN atn) {
        for (ATNState state : atn.states) {
            if (state == null) continue;
            this.checkCondition(state.onlyHasEpsilonTransitions() || state.getNumberOfTransitions() <= 1);
            if (state instanceof PlusBlockStartState) {
                this.checkCondition(((PlusBlockStartState)state).loopBackState != null);
            }
            if (state instanceof StarLoopEntryState) {
                StarLoopEntryState starLoopEntryState = (StarLoopEntryState)state;
                this.checkCondition(starLoopEntryState.loopBackState != null);
                this.checkCondition(starLoopEntryState.getNumberOfTransitions() == 2);
                if (starLoopEntryState.transition((int)0).target instanceof StarBlockStartState) {
                    this.checkCondition(starLoopEntryState.transition((int)1).target instanceof LoopEndState);
                    this.checkCondition(!starLoopEntryState.nonGreedy);
                } else if (starLoopEntryState.transition((int)0).target instanceof LoopEndState) {
                    this.checkCondition(starLoopEntryState.transition((int)1).target instanceof StarBlockStartState);
                    this.checkCondition(starLoopEntryState.nonGreedy);
                } else {
                    throw new IllegalStateException();
                }
            }
            if (state instanceof StarLoopbackState) {
                this.checkCondition(state.getNumberOfTransitions() == 1);
                this.checkCondition(state.transition((int)0).target instanceof StarLoopEntryState);
            }
            if (state instanceof LoopEndState) {
                this.checkCondition(((LoopEndState)state).loopBackState != null);
            }
            if (state instanceof RuleStartState) {
                this.checkCondition(((RuleStartState)state).stopState != null);
            }
            if (state instanceof BlockStartState) {
                this.checkCondition(((BlockStartState)state).endState != null);
            }
            if (state instanceof BlockEndState) {
                this.checkCondition(((BlockEndState)state).startState != null);
            }
            if (state instanceof DecisionState) {
                DecisionState decisionState = (DecisionState)state;
                this.checkCondition(decisionState.getNumberOfTransitions() <= 1 || decisionState.decision >= 0);
                continue;
            }
            this.checkCondition(state.getNumberOfTransitions() <= 1 || state instanceof RuleStopState);
        }
    }

    protected void checkCondition(boolean condition) {
        this.checkCondition(condition, null);
    }

    protected void checkCondition(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    protected static int toInt(char c) {
        return c;
    }

    protected static int toInt32(char[] data, int offset) {
        return data[offset] | data[offset + 1] << 16;
    }

    protected static long toLong(char[] data, int offset) {
        long lowOrder = (long)ATNDeserializer.toInt32(data, offset) & 0xFFFFFFFFL;
        return lowOrder | (long)ATNDeserializer.toInt32(data, offset + 2) << 32;
    }

    protected static UUID toUUID(char[] data, int offset) {
        long leastSigBits = ATNDeserializer.toLong(data, offset);
        long mostSigBits = ATNDeserializer.toLong(data, offset + 4);
        return new UUID(mostSigBits, leastSigBits);
    }

    protected Transition edgeFactory(ATN atn, int type, int src, int trg, int arg1, int arg2, int arg3, List<IntervalSet> sets) {
        ATNState target = atn.states.get(trg);
        switch (type) {
            case 1: {
                return new EpsilonTransition(target);
            }
            case 2: {
                if (arg3 != 0) {
                    return new RangeTransition(target, -1, arg2);
                }
                return new RangeTransition(target, arg1, arg2);
            }
            case 3: {
                RuleTransition rt = new RuleTransition((RuleStartState)atn.states.get(arg1), arg2, arg3, target);
                return rt;
            }
            case 4: {
                PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
                return pt;
            }
            case 10: {
                return new PrecedencePredicateTransition(target, arg1);
            }
            case 5: {
                if (arg3 != 0) {
                    return new AtomTransition(target, -1);
                }
                return new AtomTransition(target, arg1);
            }
            case 6: {
                ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
                return a;
            }
            case 7: {
                return new SetTransition(target, sets.get(arg1));
            }
            case 8: {
                return new NotSetTransition(target, sets.get(arg1));
            }
            case 9: {
                return new WildcardTransition(target);
            }
        }
        throw new IllegalArgumentException("The specified transition type is not valid.");
    }

    protected ATNState stateFactory(int type, int ruleIndex) {
        ATNState s;
        switch (type) {
            case 0: {
                return null;
            }
            case 1: {
                s = new BasicState();
                break;
            }
            case 2: {
                s = new RuleStartState();
                break;
            }
            case 3: {
                s = new BasicBlockStartState();
                break;
            }
            case 4: {
                s = new PlusBlockStartState();
                break;
            }
            case 5: {
                s = new StarBlockStartState();
                break;
            }
            case 6: {
                s = new TokensStartState();
                break;
            }
            case 7: {
                s = new RuleStopState();
                break;
            }
            case 8: {
                s = new BlockEndState();
                break;
            }
            case 9: {
                s = new StarLoopbackState();
                break;
            }
            case 10: {
                s = new StarLoopEntryState();
                break;
            }
            case 11: {
                s = new PlusLoopbackState();
                break;
            }
            case 12: {
                s = new LoopEndState();
                break;
            }
            default: {
                String message = String.format(Locale.getDefault(), "The specified state type %d is not valid.", type);
                throw new IllegalArgumentException(message);
            }
        }
        s.ruleIndex = ruleIndex;
        return s;
    }

    protected LexerAction lexerActionFactory(LexerActionType type, int data1, int data2) {
        switch (type) {
            case CHANNEL: {
                return new LexerChannelAction(data1);
            }
            case CUSTOM: {
                return new LexerCustomAction(data1, data2);
            }
            case MODE: {
                return new LexerModeAction(data1);
            }
            case MORE: {
                return LexerMoreAction.INSTANCE;
            }
            case POP_MODE: {
                return LexerPopModeAction.INSTANCE;
            }
            case PUSH_MODE: {
                return new LexerPushModeAction(data1);
            }
            case SKIP: {
                return LexerSkipAction.INSTANCE;
            }
            case TYPE: {
                return new LexerTypeAction(data1);
            }
        }
        String message = String.format(Locale.getDefault(), "The specified lexer action type %d is not valid.", new Object[]{type});
        throw new IllegalArgumentException(message);
    }

    static {
        SUPPORTED_UUIDS.add(BASE_SERIALIZED_UUID);
        SUPPORTED_UUIDS.add(ADDED_PRECEDENCE_TRANSITIONS);
        SUPPORTED_UUIDS.add(ADDED_LEXER_ACTIONS);
        SUPPORTED_UUIDS.add(ADDED_UNICODE_SMP);
        SERIALIZED_UUID = ADDED_UNICODE_SMP;
    }

    static enum UnicodeDeserializingMode {
        UNICODE_BMP,
        UNICODE_SMP;

    }

    static interface UnicodeDeserializer {
        public int readUnicode(char[] var1, int var2);

        public int size();
    }
}

