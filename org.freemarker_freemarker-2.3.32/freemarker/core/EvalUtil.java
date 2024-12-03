/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ArithmeticEngine;
import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.FlowControlException;
import freemarker.core.InvalidReferenceException;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.NonStringException;
import freemarker.core.NonStringOrTemplateOutputException;
import freemarker.core.TemplateDateFormat;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateObject;
import freemarker.core.TemplateValueFormatException;
import freemarker.core._DelayedAOrAn;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedGetCanonicalForm;
import freemarker.core._DelayedJQuote;
import freemarker.core._DelayedToString;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._MessageUtil;
import freemarker.core._MiscTemplateException;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans._BeansAPI;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._VersionInts;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

class EvalUtil {
    static final int CMP_OP_EQUALS = 1;
    static final int CMP_OP_NOT_EQUALS = 2;
    static final int CMP_OP_LESS_THAN = 3;
    static final int CMP_OP_GREATER_THAN = 4;
    static final int CMP_OP_LESS_THAN_EQUALS = 5;
    static final int CMP_OP_GREATER_THAN_EQUALS = 6;
    private static final String VALUE_OF_THE_COMPARISON_IS_UNKNOWN_DATE_LIKE = "value of the comparison is a date-like value where it's not known if it's a date (no time part), time, or date-time, and thus can't be used in a comparison.";

    private EvalUtil() {
    }

    static String modelToString(TemplateScalarModel model, Expression expr, Environment env) throws TemplateModelException {
        String value = model.getAsString();
        if (value == null) {
            if (env == null) {
                env = Environment.getCurrentEnvironment();
            }
            if (env != null && env.isClassicCompatible()) {
                return "";
            }
            throw EvalUtil.newModelHasStoredNullException(String.class, model, expr);
        }
        return value;
    }

    static Number modelToNumber(TemplateNumberModel model, Expression expr) throws TemplateModelException {
        Number value = model.getAsNumber();
        if (value == null) {
            throw EvalUtil.newModelHasStoredNullException(Number.class, model, expr);
        }
        return value;
    }

    static Date modelToDate(TemplateDateModel model, Expression expr) throws TemplateModelException {
        Date value = model.getAsDate();
        if (value == null) {
            throw EvalUtil.newModelHasStoredNullException(Date.class, model, expr);
        }
        return value;
    }

    static TemplateModelException newModelHasStoredNullException(Class expected, TemplateModel model, Expression expr) {
        return new _TemplateModelException(expr, _TemplateModelException.modelHasStoredNullDescription(expected, model));
    }

    static boolean compare(Expression leftExp, int operator, String operatorString, Expression rightExp, Expression defaultBlamed, Environment env) throws TemplateException {
        TemplateModel ltm = leftExp.eval(env);
        TemplateModel rtm = rightExp.eval(env);
        return EvalUtil.compare(ltm, leftExp, operator, operatorString, rtm, rightExp, defaultBlamed, false, false, false, false, env);
    }

    static boolean compare(TemplateModel leftValue, int operator, TemplateModel rightValue, Environment env) throws TemplateException {
        return EvalUtil.compare(leftValue, null, operator, null, rightValue, null, null, false, false, false, false, env);
    }

    static boolean compareLenient(TemplateModel leftValue, int operator, TemplateModel rightValue, Environment env) throws TemplateException {
        return EvalUtil.compare(leftValue, null, operator, null, rightValue, null, null, false, true, false, false, env);
    }

    static boolean compare(TemplateModel leftValue, Expression leftExp, int operator, String operatorString, TemplateModel rightValue, Expression rightExp, Expression defaultBlamed, boolean quoteOperandsInErrors, boolean typeMismatchMeansNotEqual, boolean leftNullReturnsFalse, boolean rightNullReturnsFalse, Environment env) throws TemplateException {
        int cmpResult;
        if (leftValue == null) {
            if (env != null && env.isClassicCompatible()) {
                leftValue = TemplateScalarModel.EMPTY_STRING;
            } else {
                if (leftNullReturnsFalse) {
                    return false;
                }
                if (leftExp != null) {
                    throw InvalidReferenceException.getInstance(leftExp, env);
                }
                throw new _MiscTemplateException(defaultBlamed, env, "The left operand of the comparison was undefined or null.");
            }
        }
        if (rightValue == null) {
            if (env != null && env.isClassicCompatible()) {
                rightValue = TemplateScalarModel.EMPTY_STRING;
            } else {
                if (rightNullReturnsFalse) {
                    return false;
                }
                if (rightExp != null) {
                    throw InvalidReferenceException.getInstance(rightExp, env);
                }
                throw new _MiscTemplateException(defaultBlamed, env, "The right operand of the comparison was undefined or null.");
            }
        }
        if (leftValue instanceof TemplateNumberModel && rightValue instanceof TemplateNumberModel) {
            Number leftNum = EvalUtil.modelToNumber((TemplateNumberModel)leftValue, leftExp);
            Number rightNum = EvalUtil.modelToNumber((TemplateNumberModel)rightValue, rightExp);
            ArithmeticEngine ae = env != null ? env.getArithmeticEngine() : (leftExp != null ? leftExp.getTemplate().getArithmeticEngine() : ArithmeticEngine.BIGDECIMAL_ENGINE);
            try {
                cmpResult = ae.compareNumbers(leftNum, rightNum);
            }
            catch (RuntimeException e) {
                throw new _MiscTemplateException(defaultBlamed, (Throwable)e, env, "Unexpected error while comparing two numbers: ", e);
            }
        } else if (leftValue instanceof TemplateDateModel && rightValue instanceof TemplateDateModel) {
            TemplateDateModel leftDateModel = (TemplateDateModel)leftValue;
            TemplateDateModel rightDateModel = (TemplateDateModel)rightValue;
            int leftDateType = leftDateModel.getDateType();
            int rightDateType = rightDateModel.getDateType();
            if (leftDateType == 0 || rightDateType == 0) {
                Expression sideExp;
                String sideName;
                if (leftDateType == 0) {
                    sideName = "left";
                    sideExp = leftExp;
                } else {
                    sideName = "right";
                    sideExp = rightExp;
                }
                throw new _MiscTemplateException(sideExp != null ? sideExp : defaultBlamed, env, "The ", sideName, " ", VALUE_OF_THE_COMPARISON_IS_UNKNOWN_DATE_LIKE);
            }
            if (leftDateType != rightDateType) {
                throw new _MiscTemplateException(defaultBlamed, env, "Can't compare dates of different types. Left date type is ", TemplateDateModel.TYPE_NAMES.get(leftDateType), ", right date type is ", TemplateDateModel.TYPE_NAMES.get(rightDateType), ".");
            }
            Date leftDate = EvalUtil.modelToDate(leftDateModel, leftExp);
            Date rightDate = EvalUtil.modelToDate(rightDateModel, rightExp);
            cmpResult = leftDate.compareTo(rightDate);
        } else if (leftValue instanceof TemplateScalarModel && rightValue instanceof TemplateScalarModel) {
            if (operator != 1 && operator != 2) {
                throw new _MiscTemplateException(defaultBlamed, env, "Can't use operator \"", EvalUtil.cmpOpToString(operator, operatorString), "\" on string values.");
            }
            String leftString = EvalUtil.modelToString((TemplateScalarModel)leftValue, leftExp, env);
            String rightString = EvalUtil.modelToString((TemplateScalarModel)rightValue, rightExp, env);
            cmpResult = env.getCollator().compare(leftString, rightString);
        } else if (leftValue instanceof TemplateBooleanModel && rightValue instanceof TemplateBooleanModel) {
            if (operator != 1 && operator != 2) {
                throw new _MiscTemplateException(defaultBlamed, env, "Can't use operator \"", EvalUtil.cmpOpToString(operator, operatorString), "\" on boolean values.");
            }
            boolean leftBool = ((TemplateBooleanModel)leftValue).getAsBoolean();
            boolean rightBool = ((TemplateBooleanModel)rightValue).getAsBoolean();
            cmpResult = (leftBool ? 1 : 0) - (rightBool ? 1 : 0);
        } else if (env.isClassicCompatible()) {
            String leftSting = leftExp.evalAndCoerceToPlainText(env);
            String rightString = rightExp.evalAndCoerceToPlainText(env);
            cmpResult = env.getCollator().compare(leftSting, rightString);
        } else {
            Object[] objectArray;
            Object[] objectArray2;
            if (typeMismatchMeansNotEqual) {
                if (operator == 1) {
                    return false;
                }
                if (operator == 2) {
                    return true;
                }
            }
            Object[] objectArray3 = new Object[12];
            objectArray3[0] = "Can't compare values of these types. ";
            objectArray3[1] = "Allowed comparisons are between two numbers, two strings, two dates, or two booleans.\n";
            objectArray3[2] = "Left hand operand ";
            if (quoteOperandsInErrors && leftExp != null) {
                Object[] objectArray4 = new Object[3];
                objectArray4[0] = "(";
                objectArray4[1] = new _DelayedGetCanonicalForm(leftExp);
                objectArray2 = objectArray4;
                objectArray4[2] = ") value ";
            } else {
                objectArray2 = "";
            }
            objectArray3[3] = objectArray2;
            objectArray3[4] = "is ";
            objectArray3[5] = new _DelayedAOrAn(new _DelayedFTLTypeDescription(leftValue));
            objectArray3[6] = ".\n";
            objectArray3[7] = "Right hand operand ";
            if (quoteOperandsInErrors && rightExp != null) {
                Object[] objectArray5 = new Object[3];
                objectArray5[0] = "(";
                objectArray5[1] = new _DelayedGetCanonicalForm(rightExp);
                objectArray = objectArray5;
                objectArray5[2] = ") value ";
            } else {
                objectArray = "";
            }
            objectArray3[8] = objectArray;
            objectArray3[9] = "is ";
            objectArray3[10] = new _DelayedAOrAn(new _DelayedFTLTypeDescription(rightValue));
            objectArray3[11] = ".";
            throw new _MiscTemplateException(defaultBlamed, env, objectArray3);
        }
        switch (operator) {
            case 1: {
                return cmpResult == 0;
            }
            case 2: {
                return cmpResult != 0;
            }
            case 3: {
                return cmpResult < 0;
            }
            case 4: {
                return cmpResult > 0;
            }
            case 5: {
                return cmpResult <= 0;
            }
            case 6: {
                return cmpResult >= 0;
            }
        }
        throw new BugException("Unsupported comparator operator code: " + operator);
    }

    private static String cmpOpToString(int operator, String operatorString) {
        if (operatorString != null) {
            return operatorString;
        }
        switch (operator) {
            case 1: {
                return "equals";
            }
            case 2: {
                return "not-equals";
            }
            case 3: {
                return "less-than";
            }
            case 4: {
                return "greater-than";
            }
            case 5: {
                return "less-than-equals";
            }
            case 6: {
                return "greater-than-equals";
            }
        }
        return "???";
    }

    static int mirrorCmpOperator(int operator) {
        switch (operator) {
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 4;
            }
            case 4: {
                return 3;
            }
            case 5: {
                return 6;
            }
            case 6: {
                return 5;
            }
        }
        throw new BugException("Unsupported comparator operator code: " + operator);
    }

    static Object coerceModelToStringOrMarkup(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
        return EvalUtil.coerceModelToStringOrMarkup(tm, exp, false, seqTip, env);
    }

    static Object coerceModelToStringOrMarkup(TemplateModel tm, Expression exp, boolean returnNullOnNonCoercableType, String seqTip, Environment env) throws TemplateException {
        if (tm instanceof TemplateNumberModel) {
            TemplateNumberModel tnm = (TemplateNumberModel)tm;
            TemplateNumberFormat format = env.getTemplateNumberFormat(exp, false);
            try {
                return EvalUtil.assertFormatResultNotNull(format.format(tnm));
            }
            catch (TemplateValueFormatException e) {
                throw _MessageUtil.newCantFormatNumberException(format, exp, e, false);
            }
        }
        if (tm instanceof TemplateDateModel) {
            TemplateDateModel tdm = (TemplateDateModel)tm;
            TemplateDateFormat format = env.getTemplateDateFormat(tdm, exp, false);
            try {
                return EvalUtil.assertFormatResultNotNull(format.format(tdm));
            }
            catch (TemplateValueFormatException e) {
                throw _MessageUtil.newCantFormatDateException(format, exp, e, false);
            }
        }
        if (tm instanceof TemplateMarkupOutputModel) {
            return tm;
        }
        return EvalUtil.coerceModelToTextualCommon(tm, exp, seqTip, true, returnNullOnNonCoercableType, env);
    }

    static String coerceModelToStringOrUnsupportedMarkup(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
        if (tm instanceof TemplateNumberModel) {
            TemplateNumberModel tnm = (TemplateNumberModel)tm;
            TemplateNumberFormat format = env.getTemplateNumberFormat(exp, false);
            try {
                return EvalUtil.ensureFormatResultString(format.format(tnm), exp, env);
            }
            catch (TemplateValueFormatException e) {
                throw _MessageUtil.newCantFormatNumberException(format, exp, e, false);
            }
        }
        if (tm instanceof TemplateDateModel) {
            TemplateDateModel tdm = (TemplateDateModel)tm;
            TemplateDateFormat format = env.getTemplateDateFormat(tdm, exp, false);
            try {
                return EvalUtil.ensureFormatResultString(format.format(tdm), exp, env);
            }
            catch (TemplateValueFormatException e) {
                throw _MessageUtil.newCantFormatDateException(format, exp, e, false);
            }
        }
        return EvalUtil.coerceModelToTextualCommon(tm, exp, seqTip, false, false, env);
    }

    static String coerceModelToPlainText(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
        if (tm instanceof TemplateNumberModel) {
            return EvalUtil.assertFormatResultNotNull(env.formatNumberToPlainText((TemplateNumberModel)tm, exp, false));
        }
        if (tm instanceof TemplateDateModel) {
            return EvalUtil.assertFormatResultNotNull(env.formatDateToPlainText((TemplateDateModel)tm, exp, false));
        }
        return EvalUtil.coerceModelToTextualCommon(tm, exp, seqTip, false, false, env);
    }

    private static String coerceModelToTextualCommon(TemplateModel tm, Expression exp, String seqHint, boolean supportsTOM, boolean returnNullOnNonCoercableType, Environment env) throws TemplateModelException, InvalidReferenceException, TemplateException, NonStringOrTemplateOutputException, NonStringException {
        if (tm instanceof TemplateScalarModel) {
            return EvalUtil.modelToString((TemplateScalarModel)tm, exp, env);
        }
        if (tm == null) {
            if (env.isClassicCompatible()) {
                return "";
            }
            if (exp != null) {
                throw InvalidReferenceException.getInstance(exp, env);
            }
            throw new InvalidReferenceException("Null/missing value (no more information available)", env);
        }
        if (tm instanceof TemplateBooleanModel) {
            boolean booleanValue = ((TemplateBooleanModel)tm).getAsBoolean();
            int compatMode = env.getClassicCompatibleAsInt();
            if (compatMode == 0) {
                return env.formatBoolean(booleanValue, false);
            }
            if (compatMode == 1) {
                return booleanValue ? "true" : "";
            }
            if (compatMode == 2) {
                if (tm instanceof BeanModel) {
                    return _BeansAPI.getAsClassicCompatibleString((BeanModel)tm);
                }
                return booleanValue ? "true" : "";
            }
            throw new BugException("Unsupported classic_compatible variation: " + compatMode);
        }
        if (env.isClassicCompatible() && tm instanceof BeanModel) {
            return _BeansAPI.getAsClassicCompatibleString((BeanModel)tm);
        }
        if (returnNullOnNonCoercableType) {
            return null;
        }
        if (seqHint != null && (tm instanceof TemplateSequenceModel || tm instanceof TemplateCollectionModel)) {
            if (supportsTOM) {
                throw new NonStringOrTemplateOutputException(exp, tm, seqHint, env);
            }
            throw new NonStringException(exp, tm, seqHint, env);
        }
        if (supportsTOM) {
            throw new NonStringOrTemplateOutputException(exp, tm, env);
        }
        throw new NonStringException(exp, tm, env);
    }

    private static String ensureFormatResultString(Object formatResult, Expression exp, Environment env) throws NonStringException {
        if (formatResult instanceof String) {
            return (String)formatResult;
        }
        EvalUtil.assertFormatResultNotNull(formatResult);
        TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)formatResult;
        _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Value was formatted to convert it to string, but the result was markup of ouput format ", new _DelayedJQuote(mo.getOutputFormat()), ".").tip("Use value?string to force formatting to plain text.").blame(exp);
        throw new NonStringException(null, desc);
    }

    static String assertFormatResultNotNull(String r) {
        if (r != null) {
            return r;
        }
        throw new NullPointerException("TemplateValueFormatter result can't be null");
    }

    static Object assertFormatResultNotNull(Object r) {
        if (r != null) {
            return r;
        }
        throw new NullPointerException("TemplateValueFormatter result can't be null");
    }

    static TemplateMarkupOutputModel concatMarkupOutputs(TemplateObject parent, TemplateMarkupOutputModel leftMO, TemplateMarkupOutputModel rightMO) throws TemplateException {
        MarkupOutputFormat<TemplateMarkupOutputModel> leftOF = leftMO.getOutputFormat();
        MarkupOutputFormat<TemplateMarkupOutputModel> rightOF = rightMO.getOutputFormat();
        if (rightOF != leftOF) {
            String rightPT = rightOF.getSourcePlainText(rightMO);
            if (rightPT != null) {
                return leftOF.concat(leftMO, (TemplateMarkupOutputModel)leftOF.fromPlainTextByEscaping(rightPT));
            }
            String leftPT = leftOF.getSourcePlainText(leftMO);
            if (leftPT != null) {
                return rightOF.concat((TemplateMarkupOutputModel)rightOF.fromPlainTextByEscaping(leftPT), rightMO);
            }
            Object[] message = new Object[]{"Concatenation left hand operand is in ", new _DelayedToString(leftOF), " format, while the right hand operand is in ", new _DelayedToString(rightOF), ". Conversion to common format wasn't possible."};
            if (parent instanceof Expression) {
                throw new _MiscTemplateException((Expression)parent, message);
            }
            throw new _MiscTemplateException(message);
        }
        return leftOF.concat(leftMO, rightMO);
    }

    static ArithmeticEngine getArithmeticEngine(Environment env, TemplateObject tObj) {
        return env != null ? env.getArithmeticEngine() : tObj.getTemplate().getParserConfiguration().getArithmeticEngine();
    }

    static boolean shouldWrapUncheckedException(Throwable e, Environment env) {
        if (FlowControlException.class.isInstance(e)) {
            return false;
        }
        if (env.getWrapUncheckedExceptions()) {
            return true;
        }
        if (env.getConfiguration().getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_27) {
            Class<?> c = e.getClass();
            return c == NullPointerException.class || c == ClassCastException.class || c == IndexOutOfBoundsException.class || c == InvocationTargetException.class;
        }
        return false;
    }
}

