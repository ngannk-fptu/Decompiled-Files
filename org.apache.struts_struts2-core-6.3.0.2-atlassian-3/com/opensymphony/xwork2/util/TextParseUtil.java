/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class TextParseUtil {
    public static String translateVariables(String expression, ValueStack stack) {
        return TextParseUtil.translateVariables(new char[]{'$', '%'}, expression, stack, String.class, null).toString();
    }

    public static String translateVariables(String expression, ValueStack stack, ParsedValueEvaluator evaluator) {
        return TextParseUtil.translateVariables(new char[]{'$', '%'}, expression, stack, String.class, evaluator).toString();
    }

    public static String translateVariables(char open, String expression, ValueStack stack) {
        return TextParseUtil.translateVariables(open, expression, stack, String.class, null).toString();
    }

    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType) {
        return TextParseUtil.translateVariables(open, expression, stack, asType, null);
    }

    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator) {
        return TextParseUtil.translateVariables(new char[]{open}, expression, stack, asType, evaluator, 1);
    }

    public static Object translateVariables(char[] openChars, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator) {
        return TextParseUtil.translateVariables(openChars, expression, stack, asType, evaluator, 1);
    }

    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator, int maxLoopCount) {
        return TextParseUtil.translateVariables(new char[]{open}, expression, stack, asType, evaluator, maxLoopCount);
    }

    public static Object translateVariables(char[] openChars, String expression, final ValueStack stack, final Class asType, final ParsedValueEvaluator evaluator, int maxLoopCount) {
        ParsedValueEvaluator ognlEval = new ParsedValueEvaluator(){

            @Override
            public Object evaluate(String parsedValue) {
                Object o = stack.findValue(parsedValue, asType);
                if (evaluator != null && o != null) {
                    o = evaluator.evaluate(o.toString());
                }
                return o;
            }
        };
        TextParser parser = stack.getActionContext().getContainer().getInstance(TextParser.class);
        return parser.evaluate(openChars, expression, ognlEval, maxLoopCount);
    }

    public static Collection<String> translateVariablesCollection(String expression, ValueStack stack, boolean excludeEmptyElements, ParsedValueEvaluator evaluator) {
        return TextParseUtil.translateVariablesCollection(new char[]{'$', '%'}, expression, stack, excludeEmptyElements, evaluator, 1);
    }

    public static Collection<String> translateVariablesCollection(char[] openChars, String expression, final ValueStack stack, boolean excludeEmptyElements, ParsedValueEvaluator evaluator, int maxLoopCount) {
        ArrayList<String> resultCol;
        ParsedValueEvaluator ognlEval = new ParsedValueEvaluator(){

            @Override
            public Object evaluate(String parsedValue) {
                return stack.findValue(parsedValue);
            }
        };
        ActionContext actionContext = stack.getActionContext();
        TextParser parser = actionContext.getContainer().getInstance(TextParser.class);
        Object result = parser.evaluate(openChars, expression, ognlEval, maxLoopCount);
        if (result instanceof Collection) {
            Collection casted = (Collection)result;
            resultCol = new ArrayList();
            XWorkConverter conv = actionContext.getContainer().getInstance(XWorkConverter.class);
            for (Object element : casted) {
                String stringElement = (String)conv.convertValue(actionContext.getContextMap(), element, String.class);
                if (!TextParseUtil.shallBeIncluded(stringElement, excludeEmptyElements)) continue;
                if (evaluator != null) {
                    stringElement = evaluator.evaluate(stringElement).toString();
                }
                resultCol.add(stringElement);
            }
        } else {
            resultCol = new ArrayList<String>();
            String resultStr = TextParseUtil.translateVariables(expression, stack, evaluator);
            if (TextParseUtil.shallBeIncluded(resultStr, excludeEmptyElements)) {
                resultCol.add(resultStr);
            }
        }
        return resultCol;
    }

    private static boolean shallBeIncluded(String str, boolean excludeEmptyElements) {
        return !excludeEmptyElements || str != null && !str.isEmpty();
    }

    public static Set<String> commaDelimitedStringToSet(String s) {
        return Arrays.stream(s.split(",")).map(String::trim).filter(s1 -> !s1.isEmpty()).collect(Collectors.toSet());
    }

    public static interface ParsedValueEvaluator {
        public Object evaluate(String var1);
    }
}

