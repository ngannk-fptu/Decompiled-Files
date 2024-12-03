/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.index.SingleTermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.PrefixTermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.BasicAutomata;
import org.apache.lucene.util.automaton.BasicOperations;
import org.apache.lucene.util.automaton.ByteRunAutomaton;
import org.apache.lucene.util.automaton.SpecialOperations;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.util.automaton.UTF32ToUTF8;

public class CompiledAutomaton {
    public final AUTOMATON_TYPE type;
    public final BytesRef term;
    public final ByteRunAutomaton runAutomaton;
    public final Transition[][] sortedTransitions;
    public final BytesRef commonSuffixRef;
    public final Boolean finite;

    public CompiledAutomaton(Automaton automaton) {
        this(automaton, null, true);
    }

    public CompiledAutomaton(Automaton automaton, Boolean finite, boolean simplify) {
        if (simplify) {
            String singleton;
            String commonPrefix;
            if (BasicOperations.isEmpty(automaton)) {
                this.type = AUTOMATON_TYPE.NONE;
                this.term = null;
                this.commonSuffixRef = null;
                this.runAutomaton = null;
                this.sortedTransitions = null;
                this.finite = null;
                return;
            }
            if (BasicOperations.isTotal(automaton)) {
                this.type = AUTOMATON_TYPE.ALL;
                this.term = null;
                this.commonSuffixRef = null;
                this.runAutomaton = null;
                this.sortedTransitions = null;
                this.finite = null;
                return;
            }
            if (automaton.getSingleton() == null) {
                commonPrefix = SpecialOperations.getCommonPrefix(automaton);
                singleton = commonPrefix.length() > 0 && BasicOperations.sameLanguage(automaton, BasicAutomata.makeString(commonPrefix)) ? commonPrefix : null;
            } else {
                commonPrefix = null;
                singleton = automaton.getSingleton();
            }
            if (singleton != null) {
                this.type = AUTOMATON_TYPE.SINGLE;
                this.term = new BytesRef(singleton);
                this.commonSuffixRef = null;
                this.runAutomaton = null;
                this.sortedTransitions = null;
                this.finite = null;
                return;
            }
            if (BasicOperations.sameLanguage(automaton, BasicOperations.concatenate(BasicAutomata.makeString(commonPrefix), BasicAutomata.makeAnyString()))) {
                this.type = AUTOMATON_TYPE.PREFIX;
                this.term = new BytesRef(commonPrefix);
                this.commonSuffixRef = null;
                this.runAutomaton = null;
                this.sortedTransitions = null;
                this.finite = null;
                return;
            }
        }
        this.type = AUTOMATON_TYPE.NORMAL;
        this.term = null;
        this.finite = finite == null ? Boolean.valueOf(SpecialOperations.isFinite(automaton)) : finite;
        Automaton utf8 = new UTF32ToUTF8().convert(automaton);
        this.commonSuffixRef = this.finite != false ? null : SpecialOperations.getCommonSuffixBytesRef(utf8);
        this.runAutomaton = new ByteRunAutomaton(utf8, true);
        this.sortedTransitions = utf8.getSortedTransitions();
    }

    private BytesRef addTail(int state, BytesRef term, int idx, int leadLabel) {
        Transition maxTransition = null;
        for (Transition transition : this.sortedTransitions[state]) {
            if (transition.min >= leadLabel) continue;
            maxTransition = transition;
        }
        assert (maxTransition != null);
        int floorLabel = maxTransition.max > leadLabel - 1 ? leadLabel - 1 : maxTransition.max;
        if (idx >= term.bytes.length) {
            term.grow(1 + idx);
        }
        term.bytes[idx] = (byte)floorLabel;
        state = maxTransition.to.getNumber();
        ++idx;
        while (true) {
            Transition[] transitions;
            if ((transitions = this.sortedTransitions[state]).length == 0) {
                assert (this.runAutomaton.isAccept(state));
                term.length = idx;
                return term;
            }
            assert (transitions.length != 0);
            Transition lastTransition = transitions[transitions.length - 1];
            if (idx >= term.bytes.length) {
                term.grow(1 + idx);
            }
            term.bytes[idx] = (byte)lastTransition.max;
            state = lastTransition.to.getNumber();
            ++idx;
        }
    }

    public TermsEnum getTermsEnum(Terms terms) throws IOException {
        switch (this.type) {
            case NONE: {
                return TermsEnum.EMPTY;
            }
            case ALL: {
                return terms.iterator(null);
            }
            case SINGLE: {
                return new SingleTermsEnum(terms.iterator(null), this.term);
            }
            case PREFIX: {
                return new PrefixTermsEnum(terms.iterator(null), this.term);
            }
            case NORMAL: {
                return terms.intersect(this, null);
            }
        }
        throw new RuntimeException("unhandled case");
    }

    public BytesRef floor(BytesRef input, BytesRef output) {
        output.offset = 0;
        int state = this.runAutomaton.getInitialState();
        if (input.length == 0) {
            if (this.runAutomaton.isAccept(state)) {
                output.length = 0;
                return output;
            }
            return null;
        }
        ArrayList<Integer> stack = new ArrayList<Integer>();
        int idx = 0;
        while (true) {
            int label = input.bytes[input.offset + idx] & 0xFF;
            int nextState = this.runAutomaton.step(state, label);
            if (idx == input.length - 1) {
                if (nextState != -1 && this.runAutomaton.isAccept(nextState)) {
                    if (idx >= output.bytes.length) {
                        output.grow(1 + idx);
                    }
                    output.bytes[idx] = (byte)label;
                    output.length = input.length;
                    return output;
                }
                nextState = -1;
            }
            if (nextState == -1) {
                while (true) {
                    Transition[] transitions;
                    if ((transitions = this.sortedTransitions[state]).length == 0) {
                        assert (this.runAutomaton.isAccept(state));
                        output.length = idx;
                        return output;
                    }
                    if (label - 1 >= transitions[0].min) break;
                    if (this.runAutomaton.isAccept(state)) {
                        output.length = idx;
                        return output;
                    }
                    if (stack.size() == 0) {
                        return null;
                    }
                    state = (Integer)stack.remove(stack.size() - 1);
                    label = input.bytes[input.offset + --idx] & 0xFF;
                }
                return this.addTail(state, output, idx, label);
            }
            if (idx >= output.bytes.length) {
                output.grow(1 + idx);
            }
            output.bytes[idx] = (byte)label;
            stack.add(state);
            state = nextState;
            ++idx;
        }
    }

    public static enum AUTOMATON_TYPE {
        NONE,
        ALL,
        SINGLE,
        PREFIX,
        NORMAL;

    }
}

