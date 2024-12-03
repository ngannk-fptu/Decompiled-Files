/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tuckey.web.filters.urlrewrite.ConditionMatch;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilter;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;

public class BackReferenceReplacer
implements SubstitutionFilter {
    private static Log log = Log.getLog(BackReferenceReplacer.class);
    private static Pattern backRefPattern = Pattern.compile("(?<!\\\\)%([0-9])");

    public static boolean containsBackRef(String to) {
        Matcher backRefMatcher = backRefPattern.matcher(to);
        return backRefMatcher.find();
    }

    public String substitute(String subjectOfReplacement, SubstitutionContext ctx, SubstitutionFilterChain nextFilter) {
        int lastCondMatcherGroupCount;
        ConditionMatch lastConditionMatch = ctx.getLastConditionMatch();
        if (lastConditionMatch == null) {
            return nextFilter.substitute(subjectOfReplacement, ctx);
        }
        StringMatchingMatcher lastConditionMatchMatcher = lastConditionMatch.getMatcher();
        if (lastConditionMatchMatcher != null && (lastCondMatcherGroupCount = lastConditionMatchMatcher.groupCount()) > 0) {
            Matcher backRefMatcher = backRefPattern.matcher(subjectOfReplacement);
            StringBuffer sb = new StringBuffer();
            boolean anyMatches = false;
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
                    if (varInt > lastCondMatcherGroupCount) {
                        log.error("backref %" + varInt + " not found in conditon ");
                        if (log.isDebugEnabled()) {
                            log.debug("condition matcher: " + lastConditionMatchMatcher.toString());
                        }
                    } else {
                        validBackref = true;
                    }
                }
                catch (NumberFormatException nfe) {
                    log.error("could not parse backref " + varStr + " to number");
                }
                String conditionMatch = "";
                if (validBackref) {
                    conditionMatch = lastConditionMatchMatcher.group(varInt);
                }
                String stringBeforeMatch = subjectOfReplacement.substring(lastAppendPosition, backRefMatcher.start());
                sb.append(nextFilter.substitute(stringBeforeMatch, ctx));
                sb.append(conditionMatch);
                lastAppendPosition = backRefMatcher.end();
            }
            if (anyMatches) {
                String stringAfterMatch = subjectOfReplacement.substring(lastAppendPosition);
                sb.append(nextFilter.substitute(stringAfterMatch, ctx));
                if (log.isDebugEnabled()) {
                    log.debug("replaced sb is " + sb);
                }
                return sb.toString();
            }
        }
        return nextFilter.substitute(subjectOfReplacement, ctx);
    }
}

