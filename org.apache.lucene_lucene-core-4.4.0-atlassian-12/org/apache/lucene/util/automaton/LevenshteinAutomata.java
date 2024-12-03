/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.Iterator;
import java.util.TreeSet;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.BasicAutomata;
import org.apache.lucene.util.automaton.Lev1ParametricDescription;
import org.apache.lucene.util.automaton.Lev1TParametricDescription;
import org.apache.lucene.util.automaton.Lev2ParametricDescription;
import org.apache.lucene.util.automaton.Lev2TParametricDescription;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.Transition;

public class LevenshteinAutomata {
    public static final int MAXIMUM_SUPPORTED_DISTANCE = 2;
    final int[] word;
    final int[] alphabet;
    final int alphaMax;
    final int[] rangeLower;
    final int[] rangeUpper;
    int numRanges = 0;
    ParametricDescription[] descriptions;

    public LevenshteinAutomata(String input, boolean withTranspositions) {
        this(LevenshteinAutomata.codePoints(input), 0x10FFFF, withTranspositions);
    }

    public LevenshteinAutomata(int[] word, int alphaMax, boolean withTranspositions) {
        this.word = word;
        this.alphaMax = alphaMax;
        TreeSet<Integer> set = new TreeSet<Integer>();
        for (int i = 0; i < word.length; ++i) {
            int v = word[i];
            if (v > alphaMax) {
                throw new IllegalArgumentException("alphaMax exceeded by symbol " + v + " in word");
            }
            set.add(v);
        }
        this.alphabet = new int[set.size()];
        Iterator iterator = set.iterator();
        for (int i = 0; i < this.alphabet.length; ++i) {
            this.alphabet[i] = (Integer)iterator.next();
        }
        this.rangeLower = new int[this.alphabet.length + 2];
        this.rangeUpper = new int[this.alphabet.length + 2];
        int lower = 0;
        for (int i = 0; i < this.alphabet.length; ++i) {
            int higher = this.alphabet[i];
            if (higher > lower) {
                this.rangeLower[this.numRanges] = lower;
                this.rangeUpper[this.numRanges] = higher - 1;
                ++this.numRanges;
            }
            lower = higher + 1;
        }
        if (lower <= alphaMax) {
            this.rangeLower[this.numRanges] = lower;
            this.rangeUpper[this.numRanges] = alphaMax;
            ++this.numRanges;
        }
        this.descriptions = new ParametricDescription[]{null, withTranspositions ? new Lev1TParametricDescription(word.length) : new Lev1ParametricDescription(word.length), withTranspositions ? new Lev2TParametricDescription(word.length) : new Lev2ParametricDescription(word.length)};
    }

    private static int[] codePoints(String input) {
        int length = Character.codePointCount(input, 0, input.length());
        int[] word = new int[length];
        int j = 0;
        int cp = 0;
        for (int i = 0; i < input.length(); i += Character.charCount(cp)) {
            word[j++] = cp = input.codePointAt(i);
        }
        return word;
    }

    public Automaton toAutomaton(int n) {
        if (n == 0) {
            return BasicAutomata.makeString(this.word, 0, this.word.length);
        }
        if (n >= this.descriptions.length) {
            return null;
        }
        int range = 2 * n + 1;
        ParametricDescription description = this.descriptions[n];
        State[] states = new State[description.size()];
        for (int i = 0; i < states.length; ++i) {
            states[i] = new State();
            states[i].number = i;
            states[i].setAccept(description.isAccept(i));
        }
        for (int k = 0; k < states.length; ++k) {
            int xpos = description.getPosition(k);
            if (xpos < 0) continue;
            int end = xpos + Math.min(this.word.length - xpos, range);
            for (int x = 0; x < this.alphabet.length; ++x) {
                int ch = this.alphabet[x];
                int cvec = this.getVector(ch, xpos, end);
                int dest = description.transition(k, xpos, cvec);
                if (dest < 0) continue;
                states[k].addTransition(new Transition(ch, states[dest]));
            }
            int dest = description.transition(k, xpos, 0);
            if (dest < 0) continue;
            for (int r = 0; r < this.numRanges; ++r) {
                states[k].addTransition(new Transition(this.rangeLower[r], this.rangeUpper[r], states[dest]));
            }
        }
        Automaton a = new Automaton(states[0]);
        a.setDeterministic(true);
        a.reduce();
        return a;
    }

    int getVector(int x, int pos, int end) {
        int vector = 0;
        for (int i = pos; i < end; ++i) {
            vector <<= 1;
            if (this.word[i] != x) continue;
            vector |= 1;
        }
        return vector;
    }

    static abstract class ParametricDescription {
        protected final int w;
        protected final int n;
        private final int[] minErrors;
        private static final long[] MASKS = new long[]{1L, 3L, 7L, 15L, 31L, 63L, 127L, 255L, 511L, 1023L, 2047L, 4095L, 8191L, 16383L, 32767L, 65535L, 131071L, 262143L, 524287L, 1048575L, 0x1FFFFFL, 0x3FFFFFL, 0x7FFFFFL, 0xFFFFFFL, 0x1FFFFFFL, 0x3FFFFFFL, 0x7FFFFFFL, 0xFFFFFFFL, 0x1FFFFFFFL, 0x3FFFFFFFL, Integer.MAX_VALUE, 0xFFFFFFFFL, 0x1FFFFFFFFL, 0x3FFFFFFFFL, 0x7FFFFFFFFL, 0xFFFFFFFFFL, 0x1FFFFFFFFFL, 0x3FFFFFFFFFL, 0x7FFFFFFFFFL, 0xFFFFFFFFFFL, 0x1FFFFFFFFFFL, 0x3FFFFFFFFFFL, 0x7FFFFFFFFFFL, 0xFFFFFFFFFFFL, 0x1FFFFFFFFFFFL, 0x3FFFFFFFFFFFL, 0x7FFFFFFFFFFFL, 0xFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFL, 0x3FFFFFFFFFFFFL, 0x7FFFFFFFFFFFFL, 0xFFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFFL, 0x3FFFFFFFFFFFFFL, 0x7FFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFFFL, 0x3FFFFFFFFFFFFFFL, 0x7FFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFFFFL, 0x3FFFFFFFFFFFFFFFL, Long.MAX_VALUE};

        ParametricDescription(int w, int n, int[] minErrors) {
            this.w = w;
            this.n = n;
            this.minErrors = minErrors;
        }

        int size() {
            return this.minErrors.length * (this.w + 1);
        }

        boolean isAccept(int absState) {
            int state = absState / (this.w + 1);
            int offset = absState % (this.w + 1);
            assert (offset >= 0);
            return this.w - offset + this.minErrors[state] <= this.n;
        }

        int getPosition(int absState) {
            return absState % (this.w + 1);
        }

        abstract int transition(int var1, int var2, int var3);

        protected int unpack(long[] data, int index, int bitsPerValue) {
            long bitLoc = bitsPerValue * index;
            int dataLoc = (int)(bitLoc >> 6);
            int bitStart = (int)(bitLoc & 0x3FL);
            if (bitStart + bitsPerValue <= 64) {
                return (int)(data[dataLoc] >> bitStart & MASKS[bitsPerValue - 1]);
            }
            int part = 64 - bitStart;
            return (int)((data[dataLoc] >> bitStart & MASKS[part - 1]) + ((data[1 + dataLoc] & MASKS[bitsPerValue - part - 1]) << part));
        }
    }
}

