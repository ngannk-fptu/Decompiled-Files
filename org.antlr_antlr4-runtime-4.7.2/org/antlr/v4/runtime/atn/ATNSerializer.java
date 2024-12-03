/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ATNType;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.LexerAction;
import org.antlr.v4.runtime.atn.LexerActionType;
import org.antlr.v4.runtime.atn.LexerChannelAction;
import org.antlr.v4.runtime.atn.LexerCustomAction;
import org.antlr.v4.runtime.atn.LexerModeAction;
import org.antlr.v4.runtime.atn.LexerPushModeAction;
import org.antlr.v4.runtime.atn.LexerTypeAction;
import org.antlr.v4.runtime.atn.LoopEndState;
import org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.atn.RangeTransition;
import org.antlr.v4.runtime.atn.RuleStartState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.SetTransition;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Utils;

public class ATNSerializer {
    public ATN atn;
    private List<String> tokenNames;

    public ATNSerializer(ATN atn) {
        assert (atn.grammarType != null);
        this.atn = atn;
    }

    public ATNSerializer(ATN atn, List<String> tokenNames) {
        assert (atn.grammarType != null);
        this.atn = atn;
        this.tokenNames = tokenNames;
    }

    public IntegerList serialize() {
        int i;
        IntegerList data = new IntegerList();
        data.add(ATNDeserializer.SERIALIZED_VERSION);
        this.serializeUUID(data, ATNDeserializer.SERIALIZED_UUID);
        data.add(this.atn.grammarType.ordinal());
        data.add(this.atn.maxTokenType);
        int nedges = 0;
        LinkedHashMap<IntervalSet, Boolean> sets = new LinkedHashMap<IntervalSet, Boolean>();
        IntegerList nonGreedyStates = new IntegerList();
        IntegerList precedenceStates = new IntegerList();
        data.add(this.atn.states.size());
        for (ATNState s : this.atn.states) {
            if (s == null) {
                data.add(0);
                continue;
            }
            int stateType = s.getStateType();
            if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
                nonGreedyStates.add(s.stateNumber);
            }
            if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
                precedenceStates.add(s.stateNumber);
            }
            data.add(stateType);
            if (s.ruleIndex == -1) {
                data.add(65535);
            } else {
                data.add(s.ruleIndex);
            }
            if (s.getStateType() == 12) {
                data.add(((LoopEndState)s).loopBackState.stateNumber);
            } else if (s instanceof BlockStartState) {
                data.add(((BlockStartState)s).endState.stateNumber);
            }
            if (s.getStateType() != 7) {
                nedges += s.getNumberOfTransitions();
            }
            for (int j = 0; j < s.getNumberOfTransitions(); ++j) {
                Transition t = s.transition(j);
                int edgeType = Transition.serializationTypes.get(t.getClass());
                if (edgeType != 7 && edgeType != 8) continue;
                SetTransition st = (SetTransition)t;
                sets.put(st.set, true);
            }
        }
        data.add(nonGreedyStates.size());
        for (i = 0; i < nonGreedyStates.size(); ++i) {
            data.add(nonGreedyStates.get(i));
        }
        data.add(precedenceStates.size());
        for (i = 0; i < precedenceStates.size(); ++i) {
            data.add(precedenceStates.get(i));
        }
        int nrules = this.atn.ruleToStartState.length;
        data.add(nrules);
        for (int r = 0; r < nrules; ++r) {
            RuleStartState ruleStartState = this.atn.ruleToStartState[r];
            data.add(ruleStartState.stateNumber);
            if (this.atn.grammarType != ATNType.LEXER) continue;
            if (this.atn.ruleToTokenType[r] == -1) {
                data.add(65535);
                continue;
            }
            data.add(this.atn.ruleToTokenType[r]);
        }
        int nmodes = this.atn.modeToStartState.size();
        data.add(nmodes);
        if (nmodes > 0) {
            for (ATNState aTNState : this.atn.modeToStartState) {
                data.add(aTNState.stateNumber);
            }
        }
        ArrayList<IntervalSet> bmpSets = new ArrayList<IntervalSet>();
        ArrayList<IntervalSet> arrayList = new ArrayList<IntervalSet>();
        for (IntervalSet set : sets.keySet()) {
            if (set.getMaxElement() <= 65535) {
                bmpSets.add(set);
                continue;
            }
            arrayList.add(set);
        }
        ATNSerializer.serializeSets(data, bmpSets, new CodePointSerializer(){

            @Override
            public void serializeCodePoint(IntegerList data, int cp) {
                data.add(cp);
            }
        });
        ATNSerializer.serializeSets(data, arrayList, new CodePointSerializer(){

            @Override
            public void serializeCodePoint(IntegerList data, int cp) {
                ATNSerializer.this.serializeInt(data, cp);
            }
        });
        HashMap<IntervalSet, Integer> setIndices = new HashMap<IntervalSet, Integer>();
        int setIndex = 0;
        for (IntervalSet bmpSet : bmpSets) {
            setIndices.put(bmpSet, setIndex++);
        }
        for (IntervalSet smpSet : arrayList) {
            setIndices.put(smpSet, setIndex++);
        }
        data.add(nedges);
        for (ATNState s : this.atn.states) {
            if (s == null || s.getStateType() == 7) continue;
            for (int i3 = 0; i3 < s.getNumberOfTransitions(); ++i3) {
                Transition t = s.transition(i3);
                if (this.atn.states.get(t.target.stateNumber) == null) {
                    throw new IllegalStateException("Cannot serialize a transition to a removed state.");
                }
                int src = s.stateNumber;
                int trg = t.target.stateNumber;
                int edgeType = Transition.serializationTypes.get(t.getClass());
                int arg1 = 0;
                int arg2 = 0;
                int arg3 = 0;
                switch (edgeType) {
                    case 3: {
                        trg = ((RuleTransition)t).followState.stateNumber;
                        arg1 = ((RuleTransition)t).target.stateNumber;
                        arg2 = ((RuleTransition)t).ruleIndex;
                        arg3 = ((RuleTransition)t).precedence;
                        break;
                    }
                    case 10: {
                        PrecedencePredicateTransition ppt = (PrecedencePredicateTransition)t;
                        arg1 = ppt.precedence;
                        break;
                    }
                    case 4: {
                        PredicateTransition pt = (PredicateTransition)t;
                        arg1 = pt.ruleIndex;
                        arg2 = pt.predIndex;
                        arg3 = pt.isCtxDependent ? 1 : 0;
                        break;
                    }
                    case 2: {
                        arg1 = ((RangeTransition)t).from;
                        arg2 = ((RangeTransition)t).to;
                        if (arg1 != -1) break;
                        arg1 = 0;
                        arg3 = 1;
                        break;
                    }
                    case 5: {
                        arg1 = ((AtomTransition)t).label;
                        if (arg1 != -1) break;
                        arg1 = 0;
                        arg3 = 1;
                        break;
                    }
                    case 6: {
                        ActionTransition at = (ActionTransition)t;
                        arg1 = at.ruleIndex;
                        arg2 = at.actionIndex;
                        if (arg2 == -1) {
                            arg2 = 65535;
                        }
                        arg3 = at.isCtxDependent ? 1 : 0;
                        break;
                    }
                    case 7: {
                        arg1 = (Integer)setIndices.get(((SetTransition)t).set);
                        break;
                    }
                    case 8: {
                        arg1 = (Integer)setIndices.get(((SetTransition)t).set);
                        break;
                    }
                }
                data.add(src);
                data.add(trg);
                data.add(edgeType);
                data.add(arg1);
                data.add(arg2);
                data.add(arg3);
            }
        }
        int ndecisions = this.atn.decisionToState.size();
        data.add(ndecisions);
        for (DecisionState decStartState : this.atn.decisionToState) {
            data.add(decStartState.stateNumber);
        }
        if (this.atn.grammarType == ATNType.LEXER) {
            data.add(this.atn.lexerActions.length);
            block32: for (LexerAction action : this.atn.lexerActions) {
                data.add(action.getActionType().ordinal());
                switch (action.getActionType()) {
                    case CHANNEL: {
                        int channel = ((LexerChannelAction)action).getChannel();
                        data.add(channel != -1 ? channel : 65535);
                        data.add(0);
                        continue block32;
                    }
                    case CUSTOM: {
                        int ruleIndex = ((LexerCustomAction)action).getRuleIndex();
                        int actionIndex = ((LexerCustomAction)action).getActionIndex();
                        data.add(ruleIndex != -1 ? ruleIndex : 65535);
                        data.add(actionIndex != -1 ? actionIndex : 65535);
                        continue block32;
                    }
                    case MODE: {
                        int mode = ((LexerModeAction)action).getMode();
                        data.add(mode != -1 ? mode : 65535);
                        data.add(0);
                        continue block32;
                    }
                    case MORE: {
                        data.add(0);
                        data.add(0);
                        continue block32;
                    }
                    case POP_MODE: {
                        data.add(0);
                        data.add(0);
                        continue block32;
                    }
                    case PUSH_MODE: {
                        int mode = ((LexerPushModeAction)action).getMode();
                        data.add(mode != -1 ? mode : 65535);
                        data.add(0);
                        continue block32;
                    }
                    case SKIP: {
                        data.add(0);
                        data.add(0);
                        continue block32;
                    }
                    case TYPE: {
                        int type = ((LexerTypeAction)action).getType();
                        data.add(type != -1 ? type : 65535);
                        data.add(0);
                        continue block32;
                    }
                    default: {
                        String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", new Object[]{action.getActionType()});
                        throw new IllegalArgumentException(message);
                    }
                }
            }
        }
        for (int i4 = 1; i4 < data.size(); ++i4) {
            if (data.get(i4) < 0 || data.get(i4) > 65535) {
                throw new UnsupportedOperationException("Serialized ATN data element " + data.get(i4) + " element " + i4 + " out of range " + 0 + ".." + 65535);
            }
            int value = data.get(i4) + 2 & 0xFFFF;
            data.set(i4, value);
        }
        return data;
    }

    private static void serializeSets(IntegerList data, Collection<IntervalSet> sets, CodePointSerializer codePointSerializer) {
        int nSets = sets.size();
        data.add(nSets);
        for (IntervalSet set : sets) {
            boolean containsEof = set.contains(-1);
            if (containsEof && set.getIntervals().get((int)0).b == -1) {
                data.add(set.getIntervals().size() - 1);
            } else {
                data.add(set.getIntervals().size());
            }
            data.add(containsEof ? 1 : 0);
            for (Interval I : set.getIntervals()) {
                if (I.a == -1) {
                    if (I.b == -1) continue;
                    codePointSerializer.serializeCodePoint(data, 0);
                } else {
                    codePointSerializer.serializeCodePoint(data, I.a);
                }
                codePointSerializer.serializeCodePoint(data, I.b);
            }
        }
    }

    public String decode(char[] data) {
        int version;
        data = (char[])data.clone();
        for (int i = 1; i < data.length; ++i) {
            data[i] = (char)(data[i] - 2);
        }
        StringBuilder buf = new StringBuilder();
        int p = 0;
        if ((version = ATNDeserializer.toInt(data[p++])) != ATNDeserializer.SERIALIZED_VERSION) {
            String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
        }
        UUID uuid = ATNDeserializer.toUUID(data, p);
        p += 8;
        if (!uuid.equals(ATNDeserializer.SERIALIZED_UUID)) {
            String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s).", uuid, ATNDeserializer.SERIALIZED_UUID);
            throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
        }
        int n = ++p;
        int maxType = ATNDeserializer.toInt(data[n]);
        buf.append("max type ").append(maxType).append("\n");
        int n2 = ++p;
        ++p;
        int nstates = ATNDeserializer.toInt(data[n2]);
        for (int i = 0; i < nstates; ++i) {
            int ruleIndex;
            int stype;
            if ((stype = ATNDeserializer.toInt(data[p++])) == 0) continue;
            if ((ruleIndex = ATNDeserializer.toInt(data[p++])) == 65535) {
                ruleIndex = -1;
            }
            String arg = "";
            if (stype == 12) {
                int loopBackStateNumber = ATNDeserializer.toInt(data[p++]);
                arg = " " + loopBackStateNumber;
            } else if (stype == 4 || stype == 5 || stype == 3) {
                int endStateNumber = ATNDeserializer.toInt(data[p++]);
                arg = " " + endStateNumber;
            }
            buf.append(i).append(":").append(ATNState.serializationNames.get(stype)).append(" ").append(ruleIndex).append(arg).append("\n");
        }
        int numNonGreedyStates = ATNDeserializer.toInt(data[p++]);
        for (int i = 0; i < numNonGreedyStates; ++i) {
            int stateNumber = ATNDeserializer.toInt(data[p++]);
        }
        int numPrecedenceStates = ATNDeserializer.toInt(data[p++]);
        for (int i = 0; i < numPrecedenceStates; ++i) {
            int stateNumber = ATNDeserializer.toInt(data[p++]);
        }
        int nrules = ATNDeserializer.toInt(data[p++]);
        for (int i = 0; i < nrules; ++i) {
            int s = ATNDeserializer.toInt(data[p++]);
            if (this.atn.grammarType == ATNType.LEXER) {
                int arg1 = ATNDeserializer.toInt(data[p++]);
                buf.append("rule ").append(i).append(":").append(s).append(" ").append(arg1).append('\n');
                continue;
            }
            buf.append("rule ").append(i).append(":").append(s).append('\n');
        }
        int nmodes = ATNDeserializer.toInt(data[p++]);
        for (int i = 0; i < nmodes; ++i) {
            int s = ATNDeserializer.toInt(data[p++]);
            buf.append("mode ").append(i).append(":").append(s).append('\n');
        }
        int numBMPSets = ATNDeserializer.toInt(data[p++]);
        p = this.appendSets(buf, data, p, numBMPSets, 0, ATNDeserializer.getUnicodeDeserializer(ATNDeserializer.UnicodeDeserializingMode.UNICODE_BMP));
        int numSMPSets = ATNDeserializer.toInt(data[p++]);
        p = this.appendSets(buf, data, p, numSMPSets, numBMPSets, ATNDeserializer.getUnicodeDeserializer(ATNDeserializer.UnicodeDeserializingMode.UNICODE_SMP));
        int nedges = ATNDeserializer.toInt(data[p++]);
        for (int i = 0; i < nedges; ++i) {
            int src = ATNDeserializer.toInt(data[p]);
            int trg = ATNDeserializer.toInt(data[p + 1]);
            int ttype = ATNDeserializer.toInt(data[p + 2]);
            int arg1 = ATNDeserializer.toInt(data[p + 3]);
            int arg2 = ATNDeserializer.toInt(data[p + 4]);
            int arg3 = ATNDeserializer.toInt(data[p + 5]);
            buf.append(src).append("->").append(trg).append(" ").append(Transition.serializationNames.get(ttype)).append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3).append("\n");
            p += 6;
        }
        int ndecisions = ATNDeserializer.toInt(data[p++]);
        for (int i = 0; i < ndecisions; ++i) {
            int s = ATNDeserializer.toInt(data[p++]);
            buf.append(i).append(":").append(s).append("\n");
        }
        if (this.atn.grammarType == ATNType.LEXER) {
            int lexerActionCount = ATNDeserializer.toInt(data[p++]);
            for (int i = 0; i < lexerActionCount; ++i) {
                LexerActionType actionType = LexerActionType.values()[ATNDeserializer.toInt(data[p++])];
                int data1 = ATNDeserializer.toInt(data[p++]);
                int data2 = ATNDeserializer.toInt(data[p++]);
            }
        }
        return buf.toString();
    }

    private int appendSets(StringBuilder buf, char[] data, int p, int nsets, int setIndexOffset, ATNDeserializer.UnicodeDeserializer unicodeDeserializer) {
        for (int i = 0; i < nsets; ++i) {
            boolean containsEof;
            int nintervals = ATNDeserializer.toInt(data[p++]);
            buf.append(i + setIndexOffset).append(":");
            boolean bl = containsEof = data[p++] != '\u0000';
            if (containsEof) {
                buf.append(this.getTokenName(-1));
            }
            for (int j = 0; j < nintervals; ++j) {
                if (containsEof || j > 0) {
                    buf.append(", ");
                }
                int a = unicodeDeserializer.readUnicode(data, p);
                int b = unicodeDeserializer.readUnicode(data, p += unicodeDeserializer.size());
                p += unicodeDeserializer.size();
                buf.append(this.getTokenName(a)).append("..").append(this.getTokenName(b));
            }
            buf.append("\n");
        }
        return p;
    }

    public String getTokenName(int t) {
        if (t == -1) {
            return "EOF";
        }
        if (this.atn.grammarType == ATNType.LEXER && t >= 0 && t <= 65535) {
            switch (t) {
                case 10: {
                    return "'\\n'";
                }
                case 13: {
                    return "'\\r'";
                }
                case 9: {
                    return "'\\t'";
                }
                case 8: {
                    return "'\\b'";
                }
                case 12: {
                    return "'\\f'";
                }
                case 92: {
                    return "'\\\\'";
                }
                case 39: {
                    return "'\\''";
                }
            }
            if (Character.UnicodeBlock.of((char)t) == Character.UnicodeBlock.BASIC_LATIN && !Character.isISOControl((char)t)) {
                return '\'' + Character.toString((char)t) + '\'';
            }
            String hex = Integer.toHexString(t | 0x10000).toUpperCase().substring(1, 5);
            String unicodeStr = "'\\u" + hex + "'";
            return unicodeStr;
        }
        if (this.tokenNames != null && t >= 0 && t < this.tokenNames.size()) {
            return this.tokenNames.get(t);
        }
        return String.valueOf(t);
    }

    public static String getSerializedAsString(ATN atn) {
        return new String(ATNSerializer.getSerializedAsChars(atn));
    }

    public static IntegerList getSerialized(ATN atn) {
        return new ATNSerializer(atn).serialize();
    }

    public static char[] getSerializedAsChars(ATN atn) {
        return Utils.toCharArray(ATNSerializer.getSerialized(atn));
    }

    public static String getDecoded(ATN atn, List<String> tokenNames) {
        IntegerList serialized = ATNSerializer.getSerialized(atn);
        char[] data = Utils.toCharArray(serialized);
        return new ATNSerializer(atn, tokenNames).decode(data);
    }

    private void serializeUUID(IntegerList data, UUID uuid) {
        this.serializeLong(data, uuid.getLeastSignificantBits());
        this.serializeLong(data, uuid.getMostSignificantBits());
    }

    private void serializeLong(IntegerList data, long value) {
        this.serializeInt(data, (int)value);
        this.serializeInt(data, (int)(value >> 32));
    }

    private void serializeInt(IntegerList data, int value) {
        data.add((char)value);
        data.add((char)(value >> 16));
    }

    private static interface CodePointSerializer {
        public void serializeCodePoint(IntegerList var1, int var2);
    }
}

