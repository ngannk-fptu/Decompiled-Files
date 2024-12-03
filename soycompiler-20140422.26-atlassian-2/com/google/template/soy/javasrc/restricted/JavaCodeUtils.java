/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 */
package com.google.template.soy.javasrc.restricted;

import com.google.common.base.Joiner;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NumberData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.javasrc.restricted.JavaExpr;
import java.util.regex.Pattern;

public class JavaCodeUtils {
    public static final String UTILS_LIB = "com.google.template.soy.javasrc.codedeps.SoyUtils";
    public static final String NULL_DATA_INSTANCE = "com.google.template.soy.data.restricted.NullData.INSTANCE";
    private static final Pattern NUMBER_IN_PARENS = Pattern.compile("^[(]([0-9]+(?:[.][0-9]+)?)[)]$");

    private JavaCodeUtils() {
    }

    public static String genMaybeProtect(JavaExpr expr, int minSafePrecedence) {
        return expr.getPrecedence() >= minSafePrecedence ? expr.getText() : "(" + expr.getText() + ")";
    }

    public static String genNewBooleanData(String innerExprText) {
        return "com.google.template.soy.data.restricted.BooleanData.forValue(" + innerExprText + ")";
    }

    public static String genNewIntegerData(String innerExprText) {
        return "com.google.template.soy.data.restricted.IntegerData.forValue(" + innerExprText + ")";
    }

    public static String genNewFloatData(String innerExprText) {
        return "com.google.template.soy.data.restricted.FloatData.forValue(" + innerExprText + ")";
    }

    public static String genNewStringData(String innerExprText) {
        return "com.google.template.soy.data.restricted.StringData.forValue(" + innerExprText + ")";
    }

    public static String genNewListData(String innerExprText) {
        return "new com.google.template.soy.data.SoyListData(" + innerExprText + ")";
    }

    public static String genNewMapData(String innerExprText) {
        return "new com.google.template.soy.data.SoyMapData(" + innerExprText + ")";
    }

    public static String genNewSanitizedContent(String innerExprText, SanitizedContent.ContentKind contentKind) {
        return "new " + SanitizedContent.class.getCanonicalName() + "(" + innerExprText + ", " + SanitizedContent.ContentKind.class.getCanonicalName() + "." + contentKind.name() + ")";
    }

    public static String genCoerceBoolean(JavaExpr expr) {
        String exprText = expr.getText();
        if (exprText.startsWith("com.google.template.soy.data.restricted.BooleanData.forValue(")) {
            return exprText.substring("com.google.template.soy.data.restricted.BooleanData.forValue".length());
        }
        return JavaCodeUtils.genMaybeProtect(expr, Integer.MAX_VALUE) + ".toBoolean()";
    }

    public static String genCoerceString(JavaExpr expr) {
        String exprText = expr.getText();
        if (exprText.startsWith("com.google.template.soy.data.restricted.StringData.forValue(")) {
            return exprText.substring("com.google.template.soy.data.restricted.StringData.forValue".length());
        }
        return JavaCodeUtils.genMaybeProtect(expr, Integer.MAX_VALUE) + ".toString()";
    }

    public static String genBooleanValue(JavaExpr expr) {
        String exprText = expr.getText();
        if (exprText.startsWith("com.google.template.soy.data.restricted.BooleanData.forValue(")) {
            return exprText.substring("com.google.template.soy.data.restricted.BooleanData.forValue".length());
        }
        return JavaCodeUtils.genMaybeProtect(expr, Integer.MAX_VALUE) + ".booleanValue()";
    }

    public static String genIntegerValue(JavaExpr expr) {
        String exprText = expr.getText();
        if (exprText.startsWith("com.google.template.soy.data.restricted.IntegerData.forValue(")) {
            String result = exprText.substring("com.google.template.soy.data.restricted.IntegerData.forValue".length());
            if (NUMBER_IN_PARENS.matcher(result).matches()) {
                result = result.substring(1, result.length() - 1);
            }
            return result;
        }
        return JavaCodeUtils.genMaybeProtect(expr, Integer.MAX_VALUE) + ".integerValue()";
    }

    public static String genFloatValue(JavaExpr expr) {
        String exprText = expr.getText();
        if (exprText.startsWith("com.google.template.soy.data.restricted.FloatData.forValue(")) {
            String result = exprText.substring("com.google.template.soy.data.restricted.FloatData.forValue".length());
            if (NUMBER_IN_PARENS.matcher(result).matches()) {
                result = result.substring(1, result.length() - 1);
            }
            return result;
        }
        return JavaCodeUtils.genMaybeProtect(expr, Integer.MAX_VALUE) + ".floatValue()";
    }

    public static String genNumberValue(JavaExpr expr) {
        String exprText = expr.getText();
        String result = null;
        if (exprText.startsWith("com.google.template.soy.data.restricted.IntegerData.forValue(")) {
            result = exprText.substring("com.google.template.soy.data.restricted.IntegerData.forValue".length());
        }
        if (exprText.startsWith("com.google.template.soy.data.restricted.FloatData.forValue(")) {
            result = exprText.substring("com.google.template.soy.data.restricted.FloatData.forValue".length());
        }
        if (result != null) {
            if (NUMBER_IN_PARENS.matcher(result).matches()) {
                result = result.substring(1, result.length() - 1);
            }
            return result;
        }
        return JavaCodeUtils.genMaybeProtect(expr, Integer.MAX_VALUE) + ".numberValue()";
    }

    public static String genStringValue(JavaExpr expr) {
        String exprText = expr.getText();
        if (exprText.startsWith("com.google.template.soy.data.restricted.StringData.forValue(")) {
            return exprText.substring("com.google.template.soy.data.restricted.StringData.forValue".length());
        }
        return JavaCodeUtils.genMaybeProtect(expr, Integer.MAX_VALUE) + ".stringValue()";
    }

    public static String genMaybeCast(JavaExpr expr, Class<? extends SoyData> class0) {
        if (class0.isAssignableFrom(expr.getType())) {
            return expr.getText();
        }
        return "(" + class0.getName() + ") " + JavaCodeUtils.genMaybeProtect(expr, Integer.MAX_VALUE);
    }

    public static String genConditional(String condExprText, String thenExprText, String elseExprText) {
        return condExprText + " ? " + thenExprText + " : " + elseExprText;
    }

    public static boolean isAlwaysInteger(JavaExpr expr) {
        return IntegerData.class.isAssignableFrom(expr.getType());
    }

    public static boolean isAlwaysFloat(JavaExpr expr) {
        return FloatData.class.isAssignableFrom(expr.getType());
    }

    public static boolean isAlwaysString(JavaExpr expr) {
        return StringData.class.isAssignableFrom(expr.getType());
    }

    public static boolean isAlwaysNumber(JavaExpr expr) {
        return NumberData.class.isAssignableFrom(expr.getType());
    }

    public static boolean isAlwaysTwoIntegers(JavaExpr expr0, JavaExpr expr1) {
        return JavaCodeUtils.isAlwaysInteger(expr0) && JavaCodeUtils.isAlwaysInteger(expr1);
    }

    public static boolean isAlwaysTwoFloatsOrOneFloatOneInteger(JavaExpr expr0, JavaExpr expr1) {
        return JavaCodeUtils.isAlwaysFloat(expr0) && JavaCodeUtils.isAlwaysNumber(expr1) || JavaCodeUtils.isAlwaysFloat(expr1) && JavaCodeUtils.isAlwaysNumber(expr0);
    }

    public static boolean isAlwaysAtLeastOneFloat(JavaExpr expr0, JavaExpr expr1) {
        return JavaCodeUtils.isAlwaysFloat(expr0) || JavaCodeUtils.isAlwaysFloat(expr1);
    }

    public static boolean isAlwaysAtLeastOneString(JavaExpr expr0, JavaExpr expr1) {
        return JavaCodeUtils.isAlwaysString(expr0) || JavaCodeUtils.isAlwaysString(expr1);
    }

    public static String genUnaryOp(String operatorExprText, String operandExprText) {
        return operatorExprText + " " + operandExprText;
    }

    public static String genBinaryOp(String operatorExprText, String operand0ExprText, String operand1ExprText) {
        return operand0ExprText + " " + operatorExprText + " " + operand1ExprText;
    }

    public static String genFunctionCall(String functionNameExprText, String ... functionArgsExprTexts) {
        return functionNameExprText + "(" + Joiner.on((String)", ").join((Object[])functionArgsExprTexts) + ")";
    }

    public static JavaExpr genJavaExprForNumberToNumberBinaryFunction(String javaFunctionName, String utilsLibFunctionName, JavaExpr arg0, JavaExpr arg1) {
        if (JavaCodeUtils.isAlwaysTwoIntegers(arg0, arg1)) {
            String exprText = JavaCodeUtils.genNewIntegerData(JavaCodeUtils.genFunctionCall(javaFunctionName, JavaCodeUtils.genIntegerValue(arg0), JavaCodeUtils.genIntegerValue(arg1)));
            return new JavaExpr(exprText, IntegerData.class, Integer.MAX_VALUE);
        }
        if (JavaCodeUtils.isAlwaysAtLeastOneFloat(arg0, arg1)) {
            String exprText = JavaCodeUtils.genNewFloatData(JavaCodeUtils.genFunctionCall(javaFunctionName, JavaCodeUtils.genFloatValue(arg0), JavaCodeUtils.genFloatValue(arg1)));
            return new JavaExpr(exprText, FloatData.class, Integer.MAX_VALUE);
        }
        String exprText = JavaCodeUtils.genFunctionCall("com.google.template.soy.javasrc.codedeps.SoyUtils." + utilsLibFunctionName, JavaCodeUtils.genMaybeCast(arg0, NumberData.class), JavaCodeUtils.genMaybeCast(arg1, NumberData.class));
        return new JavaExpr(exprText, NumberData.class, Integer.MAX_VALUE);
    }
}

