/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilter;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;

public class PatternReplacer
implements SubstitutionFilter {
    public String substitute(String from, SubstitutionContext ctx, SubstitutionFilterChain nextFilter) {
        StringMatchingMatcher conditionMatcher = ctx.getMatcher();
        conditionMatcher.reset();
        StringBuffer sb = new StringBuffer();
        int lastMatchEnd = 0;
        while (conditionMatcher.find()) {
            String notMatched = from.substring(lastMatchEnd, conditionMatcher.start());
            sb.append(notMatched);
            String substitutedReplacement = nextFilter.substitute(ctx.getReplacePattern(), ctx);
            sb.append(substitutedReplacement);
            lastMatchEnd = conditionMatcher.end();
            if (conditionMatcher.isMultipleMatchingSupported()) continue;
            break;
        }
        if (lastMatchEnd < from.length()) {
            sb.append(from.substring(lastMatchEnd));
        }
        return sb.toString();
    }
}

