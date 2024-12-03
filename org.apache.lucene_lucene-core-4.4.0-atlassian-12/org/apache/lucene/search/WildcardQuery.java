/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.util.ArrayList;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.AutomatonQuery;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.BasicAutomata;
import org.apache.lucene.util.automaton.BasicOperations;

public class WildcardQuery
extends AutomatonQuery {
    public static final char WILDCARD_STRING = '*';
    public static final char WILDCARD_CHAR = '?';
    public static final char WILDCARD_ESCAPE = '\\';

    public WildcardQuery(Term term) {
        super(term, WildcardQuery.toAutomaton(term));
    }

    public static Automaton toAutomaton(Term wildcardquery) {
        int length;
        ArrayList<Automaton> automata = new ArrayList<Automaton>();
        String wildcardText = wildcardquery.text();
        block5: for (int i = 0; i < wildcardText.length(); i += length) {
            int c = wildcardText.codePointAt(i);
            length = Character.charCount(c);
            switch (c) {
                case 42: {
                    automata.add(BasicAutomata.makeAnyString());
                    continue block5;
                }
                case 63: {
                    automata.add(BasicAutomata.makeAnyChar());
                    continue block5;
                }
                case 92: {
                    if (i + length < wildcardText.length()) {
                        int nextChar = wildcardText.codePointAt(i + length);
                        length += Character.charCount(nextChar);
                        automata.add(BasicAutomata.makeChar(nextChar));
                        continue block5;
                    }
                }
                default: {
                    automata.add(BasicAutomata.makeChar(c));
                }
            }
        }
        return BasicOperations.concatenate(automata);
    }

    public Term getTerm() {
        return this.term;
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.getField().equals(field)) {
            buffer.append(this.getField());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
}

