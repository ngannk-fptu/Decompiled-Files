/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletContext;
import org.tuckey.web.filters.urlrewrite.substitution.BackReferenceReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.FunctionReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.MatcherReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.PatternReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilter;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.substitution.UnescapeReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.VariableReplacer;

public class ChainedSubstitutionFilters
implements SubstitutionFilterChain {
    private List filters;
    private int nextFilter = 0;

    public ChainedSubstitutionFilters(List filters) {
        this.filters = filters;
    }

    public String substitute(String string, SubstitutionContext ctx) {
        if (this.nextFilter >= this.filters.size()) {
            return string;
        }
        String ret = ((SubstitutionFilter)this.filters.get(this.nextFilter++)).substitute(string, ctx, this);
        --this.nextFilter;
        return ret;
    }

    public static String substitute(String string, SubstitutionFilter singleFilter) {
        ArrayList<SubstitutionFilter> list = new ArrayList<SubstitutionFilter>(1);
        list.add(singleFilter);
        ChainedSubstitutionFilters filterChain = new ChainedSubstitutionFilters(list);
        return filterChain.substitute(string, null);
    }

    public static SubstitutionFilterChain getDefaultSubstitutionChain(boolean withPattern, boolean withFunction, boolean withVariable, boolean withBackReference) {
        return ChainedSubstitutionFilters.getDefaultSubstitutionChain(withPattern, withFunction, withVariable, withBackReference, null);
    }

    public static SubstitutionFilterChain getDefaultSubstitutionChain(boolean withPattern, boolean withFunction, boolean withVariable, boolean withBackReference, ServletContext sc) {
        LinkedList<SubstitutionFilter> substitutionFilters = new LinkedList<SubstitutionFilter>();
        if (withPattern) {
            substitutionFilters.add(new PatternReplacer());
        }
        if (withFunction) {
            substitutionFilters.add(new FunctionReplacer());
        }
        if (withVariable) {
            substitutionFilters.add(sc == null ? new VariableReplacer() : new VariableReplacer(sc));
        }
        if (withBackReference) {
            substitutionFilters.add(new BackReferenceReplacer());
        }
        substitutionFilters.add(new MatcherReplacer());
        substitutionFilters.add(new UnescapeReplacer());
        return new ChainedSubstitutionFilters(substitutionFilters);
    }
}

