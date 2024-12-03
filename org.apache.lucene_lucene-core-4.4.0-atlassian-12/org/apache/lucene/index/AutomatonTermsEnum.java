/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.automaton.ByteRunAutomaton;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.automaton.Transition;

class AutomatonTermsEnum
extends FilteredTermsEnum {
    private final ByteRunAutomaton runAutomaton;
    private final BytesRef commonSuffixRef;
    private final boolean finite;
    private final Transition[][] allTransitions;
    private final long[] visited;
    private long curGen;
    private final BytesRef seekBytesRef = new BytesRef(10);
    private boolean linear = false;
    private final BytesRef linearUpperBound = new BytesRef(10);
    private final Comparator<BytesRef> termComp;
    private final IntsRef savedStates = new IntsRef(10);

    public AutomatonTermsEnum(TermsEnum tenum, CompiledAutomaton compiled) {
        super(tenum);
        this.finite = compiled.finite;
        this.runAutomaton = compiled.runAutomaton;
        assert (this.runAutomaton != null);
        this.commonSuffixRef = compiled.commonSuffixRef;
        this.allTransitions = compiled.sortedTransitions;
        this.visited = new long[this.runAutomaton.getSize()];
        this.termComp = this.getComparator();
    }

    @Override
    protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
        if (this.commonSuffixRef == null || StringHelper.endsWith(term, this.commonSuffixRef)) {
            if (this.runAutomaton.run(term.bytes, term.offset, term.length)) {
                return this.linear ? FilteredTermsEnum.AcceptStatus.YES : FilteredTermsEnum.AcceptStatus.YES_AND_SEEK;
            }
            return this.linear && this.termComp.compare(term, this.linearUpperBound) < 0 ? FilteredTermsEnum.AcceptStatus.NO : FilteredTermsEnum.AcceptStatus.NO_AND_SEEK;
        }
        return this.linear && this.termComp.compare(term, this.linearUpperBound) < 0 ? FilteredTermsEnum.AcceptStatus.NO : FilteredTermsEnum.AcceptStatus.NO_AND_SEEK;
    }

    @Override
    protected BytesRef nextSeekTerm(BytesRef term) throws IOException {
        if (term == null) {
            assert (this.seekBytesRef.length == 0);
            if (this.runAutomaton.isAccept(this.runAutomaton.getInitialState())) {
                return this.seekBytesRef;
            }
        } else {
            this.seekBytesRef.copyBytes(term);
        }
        if (this.nextString()) {
            return this.seekBytesRef;
        }
        return null;
    }

    private void setLinear(int position) {
        int length;
        int i;
        assert (!this.linear);
        int state = this.runAutomaton.getInitialState();
        int maxInterval = 255;
        for (i = 0; i < position; ++i) {
            state = this.runAutomaton.step(state, this.seekBytesRef.bytes[i] & 0xFF);
            assert (state >= 0) : "state=" + state;
        }
        for (i = 0; i < this.allTransitions[state].length; ++i) {
            Transition t = this.allTransitions[state][i];
            if (t.getMin() > (this.seekBytesRef.bytes[position] & 0xFF) || (this.seekBytesRef.bytes[position] & 0xFF) > t.getMax()) continue;
            maxInterval = t.getMax();
            break;
        }
        if (maxInterval != 255) {
            ++maxInterval;
        }
        if (this.linearUpperBound.bytes.length < (length = position + 1)) {
            this.linearUpperBound.bytes = new byte[length];
        }
        System.arraycopy(this.seekBytesRef.bytes, 0, this.linearUpperBound.bytes, 0, position);
        this.linearUpperBound.bytes[position] = (byte)maxInterval;
        this.linearUpperBound.length = length;
        this.linear = true;
    }

    private boolean nextString() {
        int pos = 0;
        this.savedStates.grow(this.seekBytesRef.length + 1);
        int[] states = this.savedStates.ints;
        states[0] = this.runAutomaton.getInitialState();
        while (true) {
            ++this.curGen;
            this.linear = false;
            int state = states[pos];
            while (pos < this.seekBytesRef.length) {
                this.visited[state] = this.curGen;
                int nextState = this.runAutomaton.step(state, this.seekBytesRef.bytes[pos] & 0xFF);
                if (nextState == -1) break;
                states[pos + 1] = nextState;
                if (!this.finite && !this.linear && this.visited[nextState] == this.curGen) {
                    this.setLinear(pos);
                }
                state = nextState;
                ++pos;
            }
            if (this.nextString(state, pos)) {
                return true;
            }
            if ((pos = this.backtrack(pos)) < 0) {
                return false;
            }
            int newState = this.runAutomaton.step(states[pos], this.seekBytesRef.bytes[pos] & 0xFF);
            if (newState >= 0 && this.runAutomaton.isAccept(newState)) {
                return true;
            }
            if (this.finite) continue;
            pos = 0;
        }
    }

    private boolean nextString(int state, int position) {
        int c = 0;
        if (position < this.seekBytesRef.length) {
            c = this.seekBytesRef.bytes[position] & 0xFF;
            if (c++ == 255) {
                return false;
            }
        }
        this.seekBytesRef.length = position;
        this.visited[state] = this.curGen;
        Transition[] transitions = this.allTransitions[state];
        for (int i = 0; i < transitions.length; ++i) {
            Transition transition = transitions[i];
            if (transition.getMax() < c) continue;
            int nextChar = Math.max(c, transition.getMin());
            this.seekBytesRef.grow(this.seekBytesRef.length + 1);
            ++this.seekBytesRef.length;
            this.seekBytesRef.bytes[this.seekBytesRef.length - 1] = (byte)nextChar;
            state = transition.getDest().getNumber();
            while (this.visited[state] != this.curGen && !this.runAutomaton.isAccept(state)) {
                this.visited[state] = this.curGen;
                transition = this.allTransitions[state][0];
                state = transition.getDest().getNumber();
                this.seekBytesRef.grow(this.seekBytesRef.length + 1);
                ++this.seekBytesRef.length;
                this.seekBytesRef.bytes[this.seekBytesRef.length - 1] = (byte)transition.getMin();
                if (this.finite || this.linear || this.visited[state] != this.curGen) continue;
                this.setLinear(this.seekBytesRef.length - 1);
            }
            return true;
        }
        return false;
    }

    private int backtrack(int position) {
        while (position-- > 0) {
            int nextChar = this.seekBytesRef.bytes[position] & 0xFF;
            if (nextChar++ == 255) continue;
            this.seekBytesRef.bytes[position] = (byte)nextChar;
            this.seekBytesRef.length = position + 1;
            return position;
        }
        return -1;
    }
}

