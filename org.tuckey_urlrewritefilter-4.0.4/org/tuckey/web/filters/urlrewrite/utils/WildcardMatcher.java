/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;
import org.tuckey.web.filters.urlrewrite.utils.WildcardHelper;

public class WildcardMatcher
implements StringMatchingMatcher {
    private static Log log = Log.getLog(WildcardMatcher.class);
    private WildcardHelper wh;
    private int[] compiledPattern;
    private String matchStr;
    private Map resultMap = new HashMap();
    private boolean found = false;
    private static Pattern variablePattern = Pattern.compile("(?<!\\\\)\\$([0-9])");
    private static Pattern escapedVariablePattern = Pattern.compile("\\\\(\\$[0-9])");

    public WildcardMatcher(WildcardHelper wh, String patternStr, String matchStr) {
        this.wh = wh;
        this.compiledPattern = wh.compilePattern(patternStr);
        this.matchStr = matchStr;
    }

    public boolean find() {
        this.found = this.wh.match(this.resultMap, this.matchStr, this.compiledPattern);
        return this.found;
    }

    public boolean isFound() {
        return this.found;
    }

    public String replaceAll(String subjectOfReplacement) {
        this.find();
        int lastCondMatcherGroupCount = this.groupCount();
        Matcher variableMatcher = variablePattern.matcher(subjectOfReplacement);
        StringBuffer sb = new StringBuffer();
        while (variableMatcher.find()) {
            int groupCount = variableMatcher.groupCount();
            if (groupCount < 1) {
                log.error("group count on variable finder regex is not as expected");
                if (!log.isDebugEnabled()) continue;
                log.error("variableMatcher: " + variableMatcher.toString());
                continue;
            }
            String varStr = variableMatcher.group(1);
            boolean validVariable = false;
            int varInt = 0;
            log.debug("found " + varStr);
            try {
                varInt = Integer.parseInt(varStr);
                if (varInt > lastCondMatcherGroupCount) {
                    log.error("variable $" + varInt + " not found");
                    if (log.isDebugEnabled()) {
                        log.debug("wildcard matcher: " + this.toString());
                    }
                } else {
                    validVariable = true;
                }
            }
            catch (NumberFormatException nfe) {
                log.error("could not parse variable " + varStr + " to number");
            }
            String conditionMatch = "";
            if (validVariable) {
                conditionMatch = this.group(varInt);
            }
            if (conditionMatch.contains("$")) {
                conditionMatch = conditionMatch.replace("$", "\\$");
            }
            variableMatcher.appendReplacement(sb, conditionMatch);
        }
        variableMatcher.appendTail(sb);
        if (log.isDebugEnabled()) {
            log.debug("replaced sb is " + sb);
        }
        String result = sb.toString();
        Matcher escapedVariableMatcher = escapedVariablePattern.matcher(result);
        result = escapedVariableMatcher.replaceAll("$1");
        return result;
    }

    public int groupCount() {
        if (this.resultMap == null) {
            return 0;
        }
        return this.resultMap.size() == 0 ? 0 : this.resultMap.size() - 1;
    }

    public String group(int groupId) {
        if (this.resultMap == null) {
            return null;
        }
        return (String)this.resultMap.get("" + groupId);
    }

    public int end() {
        if (this.found) {
            return this.matchStr.length();
        }
        return -1;
    }

    public void reset() {
    }

    public int start() {
        if (this.found) {
            return 0;
        }
        return -1;
    }

    public boolean isMultipleMatchingSupported() {
        return false;
    }

    public String getMatchedString() {
        return this.matchStr;
    }
}

