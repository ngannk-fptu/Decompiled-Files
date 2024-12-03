/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.util.MapUtil;
import com.hazelcast.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class Parser {
    private static final String SPLIT_EXPRESSION = " ";
    private static final int PARENTHESIS_PRECEDENCE = 15;
    private static final int NOT_PRECEDENCE = 8;
    private static final int EQUAL_PRECEDENCE = 10;
    private static final int GREATER_PRECEDENCE = 10;
    private static final int LESS_PRECEDENCE = 10;
    private static final int GREATER_EQUAL_PRECEDENCE = 10;
    private static final int LESS_EQUAL_PRECEDENCE = 10;
    private static final int ASSIGN_PRECEDENCE = 10;
    private static final int NOT_EQUAL_PRECEDENCE = 10;
    private static final int BETWEEN_PRECEDENCE = 10;
    private static final int IN_PRECEDENCE = 10;
    private static final int LIKE_PRECEDENCE = 10;
    private static final int ILIKE_PRECEDENCE = 10;
    private static final int REGEX_PRECEDENCE = 10;
    private static final int AND_PRECEDENCE = 5;
    private static final int OR_PRECEDENCE = 3;
    private static final Map<String, Integer> PRECEDENCE;
    private static final List<String> CHAR_OPERATORS;
    private static final int NO_INDEX = -1;
    private static final String IN_LOWER = " in ";
    private static final String IN_LOWER_P = " in(";
    private static final String IN_UPPER = " IN ";
    private static final String IN_UPPER_P = " IN(";

    public List<String> toPrefix(String in) {
        List<String> tokens = this.buildTokens(this.alignINClause(in));
        ArrayList<String> output = new ArrayList<String>();
        ArrayList<String> stack = new ArrayList<String>();
        for (String token : tokens) {
            if (this.isOperand(token)) {
                if (token.equals(")")) {
                    while (this.openParanthesesFound(stack)) {
                        output.add((String)stack.remove(stack.size() - 1));
                    }
                    if (stack.size() <= 0) continue;
                    stack.remove(stack.size() - 1);
                    continue;
                }
                while (this.openParanthesesFound(stack) && !this.hasHigherPrecedence(token, (String)stack.get(stack.size() - 1))) {
                    output.add((String)stack.remove(stack.size() - 1));
                }
                stack.add(token);
                continue;
            }
            output.add(token);
        }
        while (stack.size() > 0) {
            output.add((String)stack.remove(stack.size() - 1));
        }
        return output;
    }

    private List<String> buildTokens(String in) {
        List<String> tokens = this.split(in);
        if (tokens.contains("between") || tokens.contains("BETWEEN")) {
            int i;
            boolean found = true;
            boolean dirty = false;
            block0: while (found) {
                for (i = 0; i < tokens.size(); ++i) {
                    if (!"between".equalsIgnoreCase(tokens.get(i))) continue;
                    tokens.set(i, "betweenAnd");
                    tokens.remove(i + 2);
                    dirty = true;
                    continue block0;
                }
                found = false;
            }
            if (dirty) {
                for (i = 0; i < tokens.size(); ++i) {
                    if (!"betweenAnd".equals(tokens.get(i))) continue;
                    tokens.set(i, "between");
                }
            }
        }
        return tokens;
    }

    public List<String> split(String in) {
        StringBuilder result = new StringBuilder();
        char[] chars = in.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (CHAR_OPERATORS.contains(String.valueOf(c))) {
                if (i < chars.length - 2 && CHAR_OPERATORS.contains(String.valueOf(chars[i + 1])) && !"(".equals(String.valueOf(chars[i + 1])) && !")".equals(String.valueOf(chars[i + 1]))) {
                    result.append(SPLIT_EXPRESSION).append(c).append(chars[i + 1]).append(SPLIT_EXPRESSION);
                    ++i;
                    continue;
                }
                result.append(SPLIT_EXPRESSION).append(c).append(SPLIT_EXPRESSION);
                continue;
            }
            result.append(c);
        }
        String[] tokens = result.toString().split(SPLIT_EXPRESSION);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = tokens[i].trim();
            if (tokens[i].equals("")) continue;
            list.add(tokens[i]);
        }
        return list;
    }

    boolean hasHigherPrecedence(String operator1, String operator2) {
        return PRECEDENCE.get(StringUtil.lowerCaseInternal(operator1)) > PRECEDENCE.get(StringUtil.lowerCaseInternal(operator2));
    }

    boolean isOperand(String string) {
        return PRECEDENCE.containsKey(StringUtil.lowerCaseInternal(string));
    }

    private boolean openParanthesesFound(List<String> stack) {
        return stack.size() > 0 && !stack.get(stack.size() - 1).equals("(");
    }

    private String alignINClause(String in) {
        String paramIn = in;
        int indexLowerIn = paramIn.indexOf(IN_LOWER);
        int indexLowerInWithParentheses = paramIn.indexOf(IN_LOWER_P);
        int indexUpperIn = paramIn.indexOf(IN_UPPER);
        int indexUpperInWithParentheses = paramIn.indexOf(IN_UPPER_P);
        int indexIn = this.findMinIfNot(indexUpperInWithParentheses, this.findMinIfNot(indexUpperIn, this.findMinIfNot(indexLowerIn, indexLowerInWithParentheses, -1), -1), -1);
        if (indexIn > -1 && (indexIn == indexLowerInWithParentheses || indexIn == indexUpperInWithParentheses)) {
            paramIn = paramIn.substring(0, indexIn + 3) + SPLIT_EXPRESSION + paramIn.substring(indexIn + 3);
        }
        String sql = paramIn;
        if (indexIn != -1) {
            int indexOpen = paramIn.indexOf(40, indexIn);
            int indexClose = paramIn.indexOf(41, indexOpen);
            String sub = paramIn.substring(indexOpen, indexClose + 1);
            sub = sub.replaceAll(SPLIT_EXPRESSION, "");
            sql = paramIn.substring(0, indexOpen) + sub + this.alignINClause(paramIn.substring(indexClose + 1));
        }
        return sql;
    }

    private int findMinIfNot(int a, int b, int notMin) {
        if (a <= notMin) {
            return b;
        }
        if (b <= notMin) {
            return a;
        }
        return Math.min(a, b);
    }

    static {
        Map<String, Integer> precedence = MapUtil.createHashMap(18);
        precedence.put("(", 15);
        precedence.put(")", 15);
        precedence.put("not", 8);
        precedence.put("=", 10);
        precedence.put(">", 10);
        precedence.put("<", 10);
        precedence.put(">=", 10);
        precedence.put("<=", 10);
        precedence.put("==", 10);
        precedence.put("!=", 10);
        precedence.put("<>", 10);
        precedence.put("between", 10);
        precedence.put("in", 10);
        precedence.put("like", 10);
        precedence.put("ilike", 10);
        precedence.put("regex", 10);
        precedence.put("and", 5);
        precedence.put("or", 3);
        PRECEDENCE = Collections.unmodifiableMap(precedence);
        CHAR_OPERATORS = Arrays.asList("(", ")", " + ", " - ", "=", "<", ">", " * ", " / ", "!");
    }
}

