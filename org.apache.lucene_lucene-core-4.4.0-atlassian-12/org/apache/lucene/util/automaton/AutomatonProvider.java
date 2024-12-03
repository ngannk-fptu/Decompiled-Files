/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.io.IOException;
import org.apache.lucene.util.automaton.Automaton;

public interface AutomatonProvider {
    public Automaton getAutomaton(String var1) throws IOException;
}

