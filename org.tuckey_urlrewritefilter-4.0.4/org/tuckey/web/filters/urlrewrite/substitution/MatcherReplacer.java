/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilter;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;

public class MatcherReplacer
implements SubstitutionFilter {
    private static Log log = Log.getLog(MatcherReplacer.class);
    private static Pattern backRefPattern = Pattern.compile("(?<!\\\\)\\$([0-9])");

    public String substitute(String replacePattern, SubstitutionContext ctx, SubstitutionFilterChain nextFilter) {
        if (replacePattern == null) {
            return null;
        }
        StringMatchingMatcher conditionMatcher = ctx.getMatcher();
        int conditionMatcherGroupCount = conditionMatcher.groupCount();
        Matcher backRefMatcher = backRefPattern.matcher(replacePattern);
        boolean anyMatches = false;
        StringBuffer sb = new StringBuffer();
        int lastAppendPosition = 0;
        while (backRefMatcher.find()) {
            anyMatches = true;
            int groupCount = backRefMatcher.groupCount();
            if (groupCount < 1) {
                log.error("group count on backref finder regex is not as expected");
                if (!log.isDebugEnabled()) continue;
                log.error("backRefMatcher: " + backRefMatcher.toString());
                continue;
            }
            String varStr = backRefMatcher.group(1);
            boolean validBackref = false;
            int varInt = 0;
            log.debug("found " + varStr);
            try {
                varInt = Integer.parseInt(varStr);
                if (varInt > conditionMatcherGroupCount) {
                    log.error("backref %" + varInt + " not found in conditon ");
                    if (log.isDebugEnabled()) {
                        log.debug("condition matcher: " + conditionMatcher.toString());
                    }
                } else {
                    validBackref = true;
                }
            }
            catch (NumberFormatException nfe) {
                log.error("could not parse backref " + varStr + " to number");
            }
            String conditionMatch = null;
            if (validBackref) {
                conditionMatch = conditionMatcher.group(varInt);
            }
            String stringBeforeMatch = replacePattern.substring(lastAppendPosition, backRefMatcher.start());
            sb.append(nextFilter.substitute(stringBeforeMatch, ctx));
            if (conditionMatch != null) {
                sb.append(conditionMatch);
            }
            lastAppendPosition = backRefMatcher.end();
        }
        if (anyMatches) {
            String stringAfterMatch = replacePattern.substring(lastAppendPosition);
            sb.append(nextFilter.substitute(stringAfterMatch, ctx));
            if (log.isDebugEnabled()) {
                log.debug("replaced sb is " + sb);
            }
            return sb.toString();
        }
        return nextFilter.substitute(replacePattern, ctx);
    }
}

