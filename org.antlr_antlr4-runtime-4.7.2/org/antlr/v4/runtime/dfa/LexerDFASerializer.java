/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFASerializer;

public class LexerDFASerializer
extends DFASerializer {
    public LexerDFASerializer(DFA dfa) {
        super(dfa, VocabularyImpl.EMPTY_VOCABULARY);
    }

    @Override
    protected String getEdgeLabel(int i) {
        return new StringBuilder("'").appendCodePoint(i).append("'").toString();
    }
}

