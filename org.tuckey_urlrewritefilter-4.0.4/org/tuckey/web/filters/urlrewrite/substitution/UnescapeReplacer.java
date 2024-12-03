/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilter;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;

public class UnescapeReplacer
implements SubstitutionFilter {
    public String substitute(String from, SubstitutionContext ctx, SubstitutionFilterChain nextFilter) {
        String unescaped = from.replaceAll("(?<!\\\\)\\\\", "");
        return nextFilter.substitute(unescaped, ctx);
    }
}

