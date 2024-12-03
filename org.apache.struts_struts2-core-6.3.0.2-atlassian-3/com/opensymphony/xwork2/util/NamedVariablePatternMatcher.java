/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.PatternMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class NamedVariablePatternMatcher
implements PatternMatcher<CompiledPattern> {
    @Override
    public boolean isLiteral(String pattern) {
        return pattern == null || pattern.indexOf(123) == -1;
    }

    @Override
    public CompiledPattern compilePattern(String data) {
        if (StringUtils.isEmpty((CharSequence)data)) {
            return null;
        }
        int len = data.length();
        StringBuilder regex = new StringBuilder();
        ArrayList<String> varNames = new ArrayList<String>();
        int s = 0;
        while (s < len) {
            int e = data.indexOf(123, s);
            if (e < 0 && data.indexOf(125, s) > -1) {
                throw new IllegalArgumentException("Missing opening '{' in [" + data + "]!");
            }
            if (e < 0) {
                regex.append(Pattern.quote(data.substring(s)));
                break;
            }
            if (e > s) {
                regex.append(Pattern.quote(data.substring(s, e)));
            }
            if ((e = data.indexOf(125, s = e + 1)) < 0) {
                return null;
            }
            String varName = data.substring(s, e);
            if (StringUtils.isEmpty((CharSequence)varName)) {
                throw new IllegalArgumentException("Missing variable name in [" + data + "]!");
            }
            varNames.add(varName);
            regex.append("([^/]+)");
            s = e + 1;
        }
        return new CompiledPattern(Pattern.compile(regex.toString()), varNames);
    }

    @Override
    public boolean match(Map<String, String> map, String data, CompiledPattern expr) {
        Matcher matcher;
        if (data != null && data.length() > 0 && (matcher = expr.getPattern().matcher(data)).matches()) {
            for (int x = 0; x < expr.getVariableNames().size(); ++x) {
                map.put(expr.getVariableNames().get(x), matcher.group(x + 1));
            }
            return true;
        }
        return false;
    }

    public static class CompiledPattern {
        private final Pattern pattern;
        private final List<String> variableNames;

        public CompiledPattern(Pattern pattern, List<String> variableNames) {
            this.pattern = pattern;
            this.variableNames = variableNames;
        }

        public Pattern getPattern() {
            return this.pattern;
        }

        public List<String> getVariableNames() {
            return this.variableNames;
        }
    }
}

