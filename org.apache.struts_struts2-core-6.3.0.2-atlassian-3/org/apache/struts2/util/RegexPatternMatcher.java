/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.util.PatternMatcher;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.util.RegexPatternMatcherExpression;

public class RegexPatternMatcher
implements PatternMatcher<RegexPatternMatcherExpression> {
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    @Override
    public RegexPatternMatcherExpression compilePattern(String data) {
        HashMap<Integer, String> params = new HashMap<Integer, String>();
        Matcher matcher = PATTERN.matcher(data);
        int count = 0;
        while (matcher.find()) {
            String expression = matcher.group(1);
            int index = expression.indexOf(58);
            if (index > 0) {
                String paramName = expression.substring(0, index);
                String regex = StringUtils.substring((String)expression, (int)(index + 1));
                if (StringUtils.isBlank((CharSequence)regex)) {
                    throw new IllegalArgumentException("invalid expression [" + expression + "], named parameter regular exression must be in the format {PARAM_NAME:REGEX}");
                }
                params.put(++count, paramName);
                continue;
            }
            params.put(++count, expression);
        }
        String newPattern = data.replaceAll("(\\{[^\\}]*?:(.*?)\\})", "($2)");
        newPattern = newPattern.replaceAll("(\\{.*?\\})", "(.*?)");
        return new RegexPatternMatcherExpression(Pattern.compile(newPattern), params);
    }

    @Override
    public boolean isLiteral(String pattern) {
        return pattern == null || pattern.indexOf(123) == -1;
    }

    @Override
    public boolean match(Map<String, String> map, String data, RegexPatternMatcherExpression expr) {
        Matcher matcher = expr.getPattern().matcher(data);
        Map<Integer, String> params = expr.getParams();
        if (matcher.matches()) {
            map.put("0", data);
            for (int i = 1; i <= matcher.groupCount(); ++i) {
                String paramName = params.get(i);
                String value = matcher.group(i);
                map.put(paramName, value);
                map.put(String.valueOf(i), value);
            }
            return true;
        }
        return false;
    }
}

