/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.TextParser;
import org.apache.commons.lang3.StringUtils;

public class OgnlTextParser
implements TextParser {
    @Override
    public Object evaluate(char[] openChars, String expression, TextParseUtil.ParsedValueEvaluator evaluator, int maxLoopCount) {
        expression = expression == null ? "" : expression;
        Object result = expression;
        int pos = 0;
        block0: for (char open : openChars) {
            int loopCount = 1;
            String lookupChars = open + "{";
            while (true) {
                int start;
                if ((start = expression.indexOf(lookupChars, pos)) == -1) {
                    ++loopCount;
                    start = expression.indexOf(lookupChars);
                }
                if (loopCount > maxLoopCount) continue block0;
                int length = expression.length();
                int x = start + 2;
                int count = 1;
                while (start != -1 && x < length && count != 0) {
                    char c;
                    if ((c = expression.charAt(x++)) == '{') {
                        ++count;
                        continue;
                    }
                    if (c != '}') continue;
                    --count;
                }
                int end = x - 1;
                if (start == -1 || end == -1 || count != 0) continue block0;
                String var = expression.substring(start + 2, end);
                Object o = evaluator.evaluate(var);
                String left = expression.substring(0, start);
                String right = expression.substring(end + 1);
                String middle = null;
                if (o != null) {
                    middle = o.toString();
                    result = StringUtils.isEmpty((CharSequence)left) ? o : left.concat(middle);
                    if (StringUtils.isNotEmpty((CharSequence)right)) {
                        result = result.toString().concat(right);
                    }
                    expression = left.concat(middle).concat(right);
                } else {
                    expression = left.concat(right);
                    result = expression;
                }
                pos = (left != null && left.length() > 0 ? left.length() - 1 : 0) + (middle != null && middle.length() > 0 ? middle.length() - 1 : 0) + 1;
                pos = Math.max(pos, 1);
            }
        }
        return result;
    }
}

