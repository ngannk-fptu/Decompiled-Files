/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Splitter
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.jssrc.internal.JsSrcUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import java.util.Deque;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class V1JsExprTranslator {
    public static final String VAR_OR_REF_RE = "\\$([a-zA-Z0-9_]+)((?:\\.[a-zA-Z0-9_]+)*)";
    public static final Pattern VAR_OR_REF = Pattern.compile("\\$([a-zA-Z0-9_]+)((?:\\.[a-zA-Z0-9_]+)*)");
    private static final String SOY_FUNCTION_RE = "(isFirst|isLast|index)\\(\\s*\\$([a-zA-Z0-9_]+)\\s*\\)";
    private static final Pattern SOY_FUNCTION = Pattern.compile("(isFirst|isLast|index)\\(\\s*\\$([a-zA-Z0-9_]+)\\s*\\)");
    private static final Pattern BOOL_OP_RE = Pattern.compile("\\b(not|and|or)\\b");
    private static final Pattern VAR_OR_REF_OR_BOOL_OP_OR_SOY_FUNCTION = Pattern.compile("\\$([a-zA-Z0-9_]+)((?:\\.[a-zA-Z0-9_]+)*)|" + BOOL_OP_RE + "|" + "(isFirst|isLast|index)\\(\\s*\\$([a-zA-Z0-9_]+)\\s*\\)");
    private static final Pattern NUMBER = Pattern.compile("[0-9]+");
    private static final Pattern OP_TOKEN_CHAR = Pattern.compile("[-?|&=!<>+*/%]");

    V1JsExprTranslator() {
    }

    public static JsExpr translateToJsExpr(String soyExpr, Deque<Map<String, JsExpr>> localVarTranslations) throws SoySyntaxException {
        soyExpr = CharMatcher.whitespace().collapseFrom((CharSequence)soyExpr, ' ');
        StringBuffer jsExprTextSb = new StringBuffer();
        Matcher matcher = VAR_OR_REF_OR_BOOL_OP_OR_SOY_FUNCTION.matcher(soyExpr);
        while (matcher.find()) {
            String group = matcher.group();
            if (VAR_OR_REF.matcher(group).matches()) {
                matcher.appendReplacement(jsExprTextSb, Matcher.quoteReplacement(V1JsExprTranslator.translateVarOrRef(group, localVarTranslations)));
                continue;
            }
            if (BOOL_OP_RE.matcher(group).matches()) {
                matcher.appendReplacement(jsExprTextSb, Matcher.quoteReplacement(V1JsExprTranslator.translateBoolOp(group)));
                continue;
            }
            matcher.appendReplacement(jsExprTextSb, Matcher.quoteReplacement(V1JsExprTranslator.translateFunction(group, localVarTranslations)));
        }
        matcher.appendTail(jsExprTextSb);
        String jsExprText = jsExprTextSb.toString();
        jsExprText = JsSrcUtils.escapeUnicodeFormatChars(jsExprText);
        int jsExprPrec = V1JsExprTranslator.guessJsExprPrecedence(jsExprText);
        return new JsExpr(jsExprText, jsExprPrec);
    }

    private static String translateVarOrRef(String varOrRefText, Deque<Map<String, JsExpr>> localVarTranslations) throws SoySyntaxException {
        Matcher matcher = VAR_OR_REF.matcher(varOrRefText);
        if (!matcher.matches()) {
            throw SoySyntaxException.createWithoutMetaInfo("Variable or data reference \"" + varOrRefText + "\" is malformed.");
        }
        String firstPart = matcher.group(1);
        String rest = matcher.group(2);
        StringBuilder exprTextSb = new StringBuilder();
        String translation = V1JsExprTranslator.getLocalVarTranslation(firstPart, localVarTranslations);
        if (translation != null) {
            exprTextSb.append(translation);
        } else {
            exprTextSb.append("opt_data.").append(firstPart);
        }
        if (rest != null && rest.length() > 0) {
            for (String part : Splitter.on((char)'.').split((CharSequence)rest.substring(1))) {
                if (NUMBER.matcher(part).matches()) {
                    exprTextSb.append("[").append(part).append("]");
                    continue;
                }
                exprTextSb.append(".").append(part);
            }
        }
        return exprTextSb.toString();
    }

    private static String translateBoolOp(String boolOp) {
        if (boolOp.equals("not")) {
            return "!";
        }
        if (boolOp.equals("and")) {
            return "&&";
        }
        if (boolOp.equals("or")) {
            return "||";
        }
        throw new AssertionError();
    }

    private static String translateFunction(String functionText, Deque<Map<String, JsExpr>> localVarTranslations) throws SoySyntaxException {
        Matcher matcher = SOY_FUNCTION.matcher(functionText);
        if (!matcher.matches()) {
            throw SoySyntaxException.createWithoutMetaInfo("Soy function call \"" + functionText + "\" is malformed.");
        }
        String funcName = matcher.group(1);
        String varName = matcher.group(2);
        return V1JsExprTranslator.getLocalVarTranslation(varName + "__" + funcName, localVarTranslations);
    }

    private static int guessJsExprPrecedence(String jsExprText) {
        int prec = Integer.MAX_VALUE;
        Matcher matcher = OP_TOKEN_CHAR.matcher(jsExprText);
        block11: while (matcher.find()) {
            switch (matcher.group().charAt(0)) {
                case '?': {
                    prec = Math.min(prec, Operator.CONDITIONAL.getPrecedence());
                    continue block11;
                }
                case '|': {
                    prec = Math.min(prec, Operator.OR.getPrecedence());
                    continue block11;
                }
                case '&': {
                    prec = Math.min(prec, Operator.AND.getPrecedence());
                    continue block11;
                }
                case '=': {
                    prec = Math.min(prec, Operator.EQUAL.getPrecedence());
                    continue block11;
                }
                case '!': {
                    if (jsExprText.contains("!=")) {
                        prec = Math.min(prec, Operator.NOT_EQUAL.getPrecedence());
                        continue block11;
                    }
                    prec = Math.min(prec, Operator.NOT.getPrecedence());
                    continue block11;
                }
                case '<': 
                case '>': {
                    prec = Math.min(prec, Operator.LESS_THAN.getPrecedence());
                    continue block11;
                }
                case '+': {
                    prec = Math.min(prec, Operator.PLUS.getPrecedence());
                    continue block11;
                }
                case '-': {
                    if (matcher.start() == 0) {
                        prec = Math.min(prec, Operator.NEGATIVE.getPrecedence());
                        continue block11;
                    }
                    prec = Math.min(prec, Operator.MINUS.getPrecedence());
                    continue block11;
                }
                case '%': 
                case '*': 
                case '/': {
                    prec = Math.min(prec, Operator.TIMES.getPrecedence());
                    continue block11;
                }
            }
            throw new AssertionError();
        }
        return prec;
    }

    private static String getLocalVarTranslation(String ident, Deque<Map<String, JsExpr>> localVarTranslations) {
        for (Map<String, JsExpr> localVarTranslationsFrame : localVarTranslations) {
            JsExpr translation = localVarTranslationsFrame.get(ident);
            if (translation == null) continue;
            if (translation.getPrecedence() != Integer.MAX_VALUE) {
                return "(" + translation.getText() + ")";
            }
            return translation.getText();
        }
        return null;
    }
}

