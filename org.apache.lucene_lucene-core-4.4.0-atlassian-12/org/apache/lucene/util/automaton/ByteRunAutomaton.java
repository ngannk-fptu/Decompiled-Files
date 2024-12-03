/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.RunAutomaton;
import org.apache.lucene.util.automaton.UTF32ToUTF8;

public class ByteRunAutomaton
extends RunAutomaton {
    public ByteRunAutomaton(Automaton a) {
        this(a, false);
    }

    public ByteRunAutomaton(Automaton a, boolean utf8) {
        super(utf8 ? a : new UTF32ToUTF8().convert(a), 256, true);
    }

    public boolean run(byte[] s, int offset, int length) {
        int p = this.initial;
        int l = offset + length;
        for (int i = offset; i < l; ++i) {
            if ((p = this.step(p, s[i] & 0xFF)) != -1) continue;
            return false;
        }
        return this.accept[p];
    }
}

