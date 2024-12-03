/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.RunAutomaton;

public class CharacterRunAutomaton
extends RunAutomaton {
    public CharacterRunAutomaton(Automaton a) {
        super(a, 0x10FFFF, false);
    }

    public boolean run(String s) {
        int p = this.initial;
        int l = s.length();
        int cp = 0;
        for (int i = 0; i < l; i += Character.charCount(cp)) {
            cp = s.codePointAt(i);
            if ((p = this.step(p, cp)) != -1) continue;
            return false;
        }
        return this.accept[p];
    }

    public boolean run(char[] s, int offset, int length) {
        int p = this.initial;
        int l = offset + length;
        int cp = 0;
        for (int i = offset; i < l; i += Character.charCount(cp)) {
            cp = Character.codePointAt(s, i, l);
            if ((p = this.step(p, cp)) != -1) continue;
            return false;
        }
        return this.accept[p];
    }
}

