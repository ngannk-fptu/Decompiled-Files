/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.AutomatonQuery;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.AutomatonProvider;
import org.apache.lucene.util.automaton.RegExp;

public class RegexpQuery
extends AutomatonQuery {
    private static AutomatonProvider defaultProvider = new AutomatonProvider(){

        @Override
        public Automaton getAutomaton(String name) {
            return null;
        }
    };

    public RegexpQuery(Term term) {
        this(term, 65535);
    }

    public RegexpQuery(Term term, int flags) {
        this(term, flags, defaultProvider);
    }

    public RegexpQuery(Term term, int flags, AutomatonProvider provider) {
        super(term, new RegExp(term.text(), flags).toAutomaton(provider));
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append('/');
        buffer.append(this.term.text());
        buffer.append('/');
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
}

