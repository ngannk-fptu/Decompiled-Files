/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.ArrayList;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.Transition;

public final class UTF32ToUTF8 {
    private static final int[] startCodes = new int[]{0, 128, 2048, 65536};
    private static final int[] endCodes = new int[]{127, 2047, 65535, 0x10FFFF};
    static int[] MASKS = new int[32];
    private final UTF8Sequence startUTF8 = new UTF8Sequence();
    private final UTF8Sequence endUTF8 = new UTF8Sequence();
    private final UTF8Sequence tmpUTF8a = new UTF8Sequence();
    private final UTF8Sequence tmpUTF8b = new UTF8Sequence();
    private State[] utf8States;
    private int utf8StateCount;

    void convertOneEdge(State start, State end, int startCodePoint, int endCodePoint) {
        this.startUTF8.set(startCodePoint);
        this.endUTF8.set(endCodePoint);
        this.build(start, end, this.startUTF8, this.endUTF8, 0);
    }

    private void build(State start, State end, UTF8Sequence startUTF8, UTF8Sequence endUTF8, int upto) {
        if (startUTF8.byteAt(upto) == endUTF8.byteAt(upto)) {
            if (upto == startUTF8.len - 1 && upto == endUTF8.len - 1) {
                start.addTransition(new Transition(startUTF8.byteAt(upto), endUTF8.byteAt(upto), end));
                return;
            }
            assert (startUTF8.len > upto + 1);
            assert (endUTF8.len > upto + 1);
            State n = this.newUTF8State();
            start.addTransition(new Transition(startUTF8.byteAt(upto), n));
            this.build(n, end, startUTF8, endUTF8, 1 + upto);
        } else if (startUTF8.len == endUTF8.len) {
            if (upto == startUTF8.len - 1) {
                start.addTransition(new Transition(startUTF8.byteAt(upto), endUTF8.byteAt(upto), end));
            } else {
                this.start(start, end, startUTF8, upto, false);
                if (endUTF8.byteAt(upto) - startUTF8.byteAt(upto) > 1) {
                    this.all(start, end, startUTF8.byteAt(upto) + 1, endUTF8.byteAt(upto) - 1, startUTF8.len - upto - 1);
                }
                this.end(start, end, endUTF8, upto, false);
            }
        } else {
            this.start(start, end, startUTF8, upto, true);
            int limit = endUTF8.len - upto;
            for (int byteCount = 1 + startUTF8.len - upto; byteCount < limit; ++byteCount) {
                this.tmpUTF8a.set(UTF32ToUTF8.startCodes[byteCount - 1]);
                this.tmpUTF8b.set(UTF32ToUTF8.endCodes[byteCount - 1]);
                this.all(start, end, this.tmpUTF8a.byteAt(0), this.tmpUTF8b.byteAt(0), this.tmpUTF8a.len - 1);
            }
            this.end(start, end, endUTF8, upto, true);
        }
    }

    private void start(State start, State end, UTF8Sequence utf8, int upto, boolean doAll) {
        if (upto == utf8.len - 1) {
            start.addTransition(new Transition(utf8.byteAt(upto), utf8.byteAt(upto) | MASKS[utf8.numBits(upto) - 1], end));
        } else {
            State n = this.newUTF8State();
            start.addTransition(new Transition(utf8.byteAt(upto), n));
            this.start(n, end, utf8, 1 + upto, true);
            int endCode = utf8.byteAt(upto) | MASKS[utf8.numBits(upto) - 1];
            if (doAll && utf8.byteAt(upto) != endCode) {
                this.all(start, end, utf8.byteAt(upto) + 1, endCode, utf8.len - upto - 1);
            }
        }
    }

    private void end(State start, State end, UTF8Sequence utf8, int upto, boolean doAll) {
        if (upto == utf8.len - 1) {
            start.addTransition(new Transition(utf8.byteAt(upto) & ~MASKS[utf8.numBits(upto) - 1], utf8.byteAt(upto), end));
        } else {
            int startCode = utf8.numBits(upto) == 5 ? 194 : utf8.byteAt(upto) & ~MASKS[utf8.numBits(upto) - 1];
            if (doAll && utf8.byteAt(upto) != startCode) {
                this.all(start, end, startCode, utf8.byteAt(upto) - 1, utf8.len - upto - 1);
            }
            State n = this.newUTF8State();
            start.addTransition(new Transition(utf8.byteAt(upto), n));
            this.end(n, end, utf8, 1 + upto, true);
        }
    }

    private void all(State start, State end, int startCode, int endCode, int left) {
        if (left == 0) {
            start.addTransition(new Transition(startCode, endCode, end));
        } else {
            State lastN = this.newUTF8State();
            start.addTransition(new Transition(startCode, endCode, lastN));
            while (left > 1) {
                State n = this.newUTF8State();
                lastN.addTransition(new Transition(128, 191, n));
                --left;
                lastN = n;
            }
            lastN.addTransition(new Transition(128, 191, end));
        }
    }

    public Automaton convert(Automaton utf32) {
        if (utf32.isSingleton()) {
            utf32 = utf32.cloneExpanded();
        }
        State[] map = new State[utf32.getNumberedStates().length];
        ArrayList<State> pending = new ArrayList<State>();
        State utf32State = utf32.getInitialState();
        pending.add(utf32State);
        Automaton utf8 = new Automaton();
        utf8.setDeterministic(false);
        State utf8State = utf8.getInitialState();
        this.utf8States = new State[5];
        utf8State.number = this.utf8StateCount = 0;
        this.utf8States[this.utf8StateCount] = utf8State;
        ++this.utf8StateCount;
        utf8State.setAccept(utf32State.isAccept());
        map[utf32State.number] = utf8State;
        while (pending.size() != 0) {
            utf32State = (State)pending.remove(pending.size() - 1);
            utf8State = map[utf32State.number];
            for (int i = 0; i < utf32State.numTransitions; ++i) {
                Transition t = utf32State.transitionsArray[i];
                State destUTF32 = t.to;
                State destUTF8 = map[destUTF32.number];
                if (destUTF8 == null) {
                    destUTF8 = this.newUTF8State();
                    destUTF8.accept = destUTF32.accept;
                    map[destUTF32.number] = destUTF8;
                    pending.add(destUTF32);
                }
                this.convertOneEdge(utf8State, destUTF8, t.min, t.max);
            }
        }
        utf8.setNumberedStates(this.utf8States, this.utf8StateCount);
        return utf8;
    }

    private State newUTF8State() {
        State s = new State();
        if (this.utf8StateCount == this.utf8States.length) {
            State[] newArray = new State[ArrayUtil.oversize(1 + this.utf8StateCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.utf8States, 0, newArray, 0, this.utf8StateCount);
            this.utf8States = newArray;
        }
        this.utf8States[this.utf8StateCount] = s;
        s.number = this.utf8StateCount++;
        return s;
    }

    static {
        int v = 2;
        for (int i = 0; i < 32; ++i) {
            UTF32ToUTF8.MASKS[i] = v - 1;
            v *= 2;
        }
    }

    private static class UTF8Sequence {
        private final UTF8Byte[] bytes = new UTF8Byte[4];
        private int len;

        public UTF8Sequence() {
            for (int i = 0; i < 4; ++i) {
                this.bytes[i] = new UTF8Byte();
            }
        }

        public int byteAt(int idx) {
            return this.bytes[idx].value;
        }

        public int numBits(int idx) {
            return this.bytes[idx].bits;
        }

        private void set(int code) {
            if (code < 128) {
                this.bytes[0].value = code;
                this.bytes[0].bits = (byte)7;
                this.len = 1;
            } else if (code < 2048) {
                this.bytes[0].value = 0xC0 | code >> 6;
                this.bytes[0].bits = (byte)5;
                this.setRest(code, 1);
                this.len = 2;
            } else if (code < 65536) {
                this.bytes[0].value = 0xE0 | code >> 12;
                this.bytes[0].bits = (byte)4;
                this.setRest(code, 2);
                this.len = 3;
            } else {
                this.bytes[0].value = 0xF0 | code >> 18;
                this.bytes[0].bits = (byte)3;
                this.setRest(code, 3);
                this.len = 4;
            }
        }

        private void setRest(int code, int numBytes) {
            for (int i = 0; i < numBytes; ++i) {
                this.bytes[numBytes - i].value = 0x80 | code & MASKS[5];
                this.bytes[numBytes - i].bits = (byte)6;
                code >>= 6;
            }
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < this.len; ++i) {
                if (i > 0) {
                    b.append(' ');
                }
                b.append(Integer.toBinaryString(this.bytes[i].value));
            }
            return b.toString();
        }
    }

    private static class UTF8Byte {
        int value;
        byte bits;

        private UTF8Byte() {
        }
    }
}

