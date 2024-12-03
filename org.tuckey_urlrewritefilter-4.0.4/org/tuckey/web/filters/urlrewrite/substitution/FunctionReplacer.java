/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.substitution;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tuckey.web.filters.urlrewrite.functions.StringFunctions;
import org.tuckey.web.filters.urlrewrite.substitution.ChainedSubstitutionFilters;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilter;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.substitution.VariableReplacer;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class FunctionReplacer
implements SubstitutionFilter {
    private static Log log = Log.getLog(VariableReplacer.class);
    private static Pattern functionPattern = Pattern.compile("(?<!\\\\)\\$\\{(.*)\\}");

    public static boolean containsFunction(String to) {
        Matcher functionMatcher = functionPattern.matcher(to);
        return functionMatcher.find();
    }

    public static String replace(String subjectOfReplacement) {
        return new FunctionReplacer().substitute(subjectOfReplacement, null, new ChainedSubstitutionFilters(Collections.EMPTY_LIST));
    }

    public String substitute(String subjectOfReplacement, SubstitutionContext ctx, SubstitutionFilterChain nextFilter) {
        Matcher functionMatcher = functionPattern.matcher(subjectOfReplacement);
        StringBuffer sb = new StringBuffer();
        boolean anyMatches = false;
        int lastAppendPosition = 0;
        while (functionMatcher.find()) {
            anyMatches = true;
            int groupCount = functionMatcher.groupCount();
            if (groupCount < 1) {
                log.error("group count on function finder regex is not as expected");
                if (!log.isDebugEnabled()) continue;
                log.error("functionMatcher: " + functionMatcher.toString());
                continue;
            }
            String varStr = functionMatcher.group(1);
            String varValue = "";
            if (varStr != null) {
                varValue = this.functionReplace(varStr, ctx, nextFilter);
                if (log.isDebugEnabled()) {
                    log.debug("resolved to: " + varValue);
                }
            } else if (log.isDebugEnabled()) {
                log.debug("variable reference is null " + functionMatcher);
            }
            String stringBeforeMatch = subjectOfReplacement.substring(lastAppendPosition, functionMatcher.start());
            sb.append(nextFilter.substitute(stringBeforeMatch, ctx));
            sb.append(varValue);
            lastAppendPosition = functionMatcher.end();
        }
        if (anyMatches) {
            String stringAfterMatch = subjectOfReplacement.substring(lastAppendPosition);
            sb.append(nextFilter.substitute(stringAfterMatch, ctx));
            log.debug("replaced sb is " + sb);
            return sb.toString();
        }
        return nextFilter.substitute(subjectOfReplacement, ctx);
    }

    private String functionReplace(String originalVarStr, SubstitutionContext ctx, final SubstitutionFilterChain nextFilter) {
        String varType;
        String varSubName = null;
        int colonIdx = originalVarStr.indexOf(":");
        if (colonIdx != -1 && colonIdx + 1 < originalVarStr.length()) {
            varSubName = originalVarStr.substring(colonIdx + 1);
            varType = originalVarStr.substring(0, colonIdx);
            if (log.isDebugEnabled()) {
                log.debug("function ${" + originalVarStr + "} type: " + varType + ", name: '" + varSubName + "'");
            }
        } else {
            varType = originalVarStr;
            if (log.isDebugEnabled()) {
                log.debug("function ${" + originalVarStr + "} type: " + varType);
            }
        }
        String functionResult = "";
        SubstitutionFilterChain redoFunctionFilter = new SubstitutionFilterChain(){

            public String substitute(String string, SubstitutionContext ctx) {
                return FunctionReplacer.this.substitute(string, ctx, nextFilter);
            }
        };
        if ("replace".equalsIgnoreCase(varType) || "replaceAll".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.replaceAll(varSubName, redoFunctionFilter, ctx);
        } else if ("replaceFirst".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.replaceFirst(varSubName, redoFunctionFilter, ctx);
        } else if ("escape".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.escape(varSubName, redoFunctionFilter, ctx);
        } else if ("escapePath".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.escapePath(varSubName, redoFunctionFilter, ctx);
        } else if ("unescape".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.unescape(varSubName, redoFunctionFilter, ctx);
        } else if ("unescapePath".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.unescapePath(varSubName, redoFunctionFilter, ctx);
        } else if ("lower".equalsIgnoreCase(varType) || "toLower".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.toLower(varSubName, redoFunctionFilter, ctx);
        } else if ("upper".equalsIgnoreCase(varType) || "toUpper".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.toUpper(varSubName, redoFunctionFilter, ctx);
        } else if ("trim".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.trim(varSubName, redoFunctionFilter, ctx);
        } else if ("length".equalsIgnoreCase(varType)) {
            functionResult = StringFunctions.length(varSubName, redoFunctionFilter, ctx);
        } else {
            log.error("function ${" + originalVarStr + "} type '" + varType + "' not a valid type");
        }
        return functionResult;
    }
}

