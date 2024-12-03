/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.BasicOperations;
import org.apache.lucene.util.automaton.CompiledAutomaton;

public class AutomatonQuery
extends MultiTermQuery {
    protected final Automaton automaton;
    protected final CompiledAutomaton compiled;
    protected final Term term;

    public AutomatonQuery(Term term, Automaton automaton) {
        super(term.field());
        this.term = term;
        this.automaton = automaton;
        this.compiled = new CompiledAutomaton(automaton);
    }

    @Override
    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        return this.compiled.getTermsEnum(terms);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        if (this.automaton != null) {
            int automatonHashCode = this.automaton.getNumberOfStates() * 3 + this.automaton.getNumberOfTransitions() * 2;
            if (automatonHashCode == 0) {
                automatonHashCode = 1;
            }
            result = 31 * result + automatonHashCode;
        }
        result = 31 * result + (this.term == null ? 0 : this.term.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AutomatonQuery other = (AutomatonQuery)obj;
        if (this.automaton == null ? other.automaton != null : !BasicOperations.sameLanguage(this.automaton, other.automaton)) {
            return false;
        }
        return !(this.term == null ? other.term != null : !this.term.equals(other.term));
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.getClass().getSimpleName());
        buffer.append(" {");
        buffer.append('\n');
        buffer.append(this.automaton.toString());
        buffer.append("}");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
}

