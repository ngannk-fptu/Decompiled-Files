/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BooleanLiteral;
import freemarker.core.BuiltIn;
import freemarker.core.Dot;
import freemarker.core.DynamicKeyName;
import freemarker.core.Environment;
import freemarker.core.ExistsExpression;
import freemarker.core.Expression;
import freemarker.core.HashLiteral;
import freemarker.core.Identifier;
import freemarker.core.ListLiteral;
import freemarker.core.Macro;
import freemarker.core.MethodCall;
import freemarker.core.NumberLiteral;
import freemarker.core.ParentheticalExpression;
import freemarker.core.StringLiteral;
import freemarker.core.TemplateDateFormat;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateValueFormatException;
import freemarker.core.UnknownDateTypeFormattingUnsupportedException;
import freemarker.core._DelayedAOrAn;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedJQuote;
import freemarker.core._DelayedShortClassName;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._MiscTemplateException;
import freemarker.core._TemplateModelException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.util.ArrayList;

public class _MessageUtil {
    static final String UNKNOWN_DATE_TO_STRING_ERROR_MESSAGE = "Can't convert the date-like value to string because it isn't known if it's a date (no time part), time or date-time value.";
    static final String UNKNOWN_DATE_PARSING_ERROR_MESSAGE = "Can't parse the string to date-like value because it isn't known if it's desired result should be a date (no time part), a time, or a date-time value.";
    static final String UNKNOWN_DATE_TYPE_ERROR_TIP = "Use ?date, ?time, or ?datetime to tell FreeMarker the exact type.";
    static final Object[] UNKNOWN_DATE_TO_STRING_TIPS = new Object[]{"Use ?date, ?time, or ?datetime to tell FreeMarker the exact type.", "If you need a particular format only once, use ?string(pattern), like ?string('dd.MM.yyyy HH:mm:ss'), to specify which fields to display. "};
    static final String EMBEDDED_MESSAGE_BEGIN = "---begin-message---\n";
    static final String EMBEDDED_MESSAGE_END = "\n---end-message---";

    private _MessageUtil() {
    }

    static String formatLocationForSimpleParsingError(Template template, int line, int column) {
        return _MessageUtil.formatLocation("in", template, line, column);
    }

    static String formatLocationForSimpleParsingError(String templateSourceName, int line, int column) {
        return _MessageUtil.formatLocation("in", templateSourceName, line, column);
    }

    static String formatLocationForDependentParsingError(Template template, int line, int column) {
        return _MessageUtil.formatLocation("on", template, line, column);
    }

    static String formatLocationForDependentParsingError(String templateSourceName, int line, int column) {
        return _MessageUtil.formatLocation("on", templateSourceName, line, column);
    }

    static String formatLocationForEvaluationError(Template template, int line, int column) {
        return _MessageUtil.formatLocation("at", template, line, column);
    }

    static String formatLocationForEvaluationError(Macro macro, int line, int column) {
        Template t = macro.getTemplate();
        return _MessageUtil.formatLocation("at", t != null ? t.getSourceName() : null, macro.getName(), macro.isFunction(), line, column);
    }

    static String formatLocationForEvaluationError(String templateSourceName, int line, int column) {
        return _MessageUtil.formatLocation("at", templateSourceName, line, column);
    }

    private static String formatLocation(String preposition, Template template, int line, int column) {
        return _MessageUtil.formatLocation(preposition, template != null ? template.getSourceName() : null, line, column);
    }

    private static String formatLocation(String preposition, String templateSourceName, int line, int column) {
        return _MessageUtil.formatLocation(preposition, templateSourceName, null, false, line, column);
    }

    private static String formatLocation(String preposition, String templateSourceName, String macroOrFuncName, boolean isFunction, int line, int column) {
        String templateDesc;
        if (line < 0) {
            templateDesc = "?eval-ed string";
            macroOrFuncName = null;
        } else {
            templateDesc = templateSourceName != null ? "template " + StringUtil.jQuoteNoXSS(templateSourceName) : "nameless template";
        }
        return "in " + templateDesc + (macroOrFuncName != null ? " in " + (isFunction ? "function " : "macro ") + StringUtil.jQuote(macroOrFuncName) : "") + " " + preposition + " " + _MessageUtil.formatPosition(line, column);
    }

    static String formatPosition(int line, int column) {
        return "line " + (line >= 0 ? line : line - -1000000001) + ", column " + column;
    }

    public static String shorten(String s, int maxLength) {
        if (maxLength < 5) {
            maxLength = 5;
        }
        boolean isTruncated = false;
        int brIdx = s.indexOf(10);
        if (brIdx != -1) {
            s = s.substring(0, brIdx);
            isTruncated = true;
        }
        if ((brIdx = s.indexOf(13)) != -1) {
            s = s.substring(0, brIdx);
            isTruncated = true;
        }
        if (s.length() > maxLength) {
            s = s.substring(0, maxLength - 3);
            isTruncated = true;
        }
        if (!isTruncated) {
            return s;
        }
        if (s.endsWith(".")) {
            if (s.endsWith("..")) {
                if (s.endsWith("...")) {
                    return s;
                }
                return s + ".";
            }
            return s + "..";
        }
        return s + "...";
    }

    public static StringBuilder appendExpressionAsUntearable(StringBuilder sb, Expression argExp) {
        boolean needParen;
        boolean bl = needParen = !(argExp instanceof NumberLiteral) && !(argExp instanceof StringLiteral) && !(argExp instanceof BooleanLiteral) && !(argExp instanceof ListLiteral) && !(argExp instanceof HashLiteral) && !(argExp instanceof Identifier) && !(argExp instanceof Dot) && !(argExp instanceof DynamicKeyName) && !(argExp instanceof MethodCall) && !(argExp instanceof BuiltIn) && !(argExp instanceof ExistsExpression) && !(argExp instanceof ParentheticalExpression);
        if (needParen) {
            sb.append('(');
        }
        sb.append(argExp.getCanonicalForm());
        if (needParen) {
            sb.append(')');
        }
        return sb;
    }

    public static TemplateModelException newArgCntError(String methodName, int argCnt, int expectedCnt) {
        return _MessageUtil.newArgCntError(methodName, argCnt, expectedCnt, expectedCnt);
    }

    public static TemplateModelException newArgCntError(String methodName, int argCnt, int minCnt, int maxCnt) {
        ArrayList<Object> desc = new ArrayList<Object>(20);
        desc.add(methodName);
        desc.add("(");
        if (maxCnt != 0) {
            desc.add("...");
        }
        desc.add(") expects ");
        if (minCnt == maxCnt) {
            if (maxCnt == 0) {
                desc.add("no");
            } else {
                desc.add(maxCnt);
            }
        } else if (maxCnt - minCnt == 1) {
            desc.add(minCnt);
            desc.add(" or ");
            desc.add(maxCnt);
        } else {
            desc.add(minCnt);
            if (maxCnt != Integer.MAX_VALUE) {
                desc.add(" to ");
                desc.add(maxCnt);
            } else {
                desc.add(" or more (unlimited)");
            }
        }
        desc.add(" argument");
        if (maxCnt > 1) {
            desc.add("s");
        }
        desc.add(" but has received ");
        if (argCnt == 0) {
            desc.add("none");
        } else {
            desc.add(argCnt);
        }
        desc.add(".");
        return new _TemplateModelException(desc.toArray());
    }

    public static TemplateModelException newMethodArgMustBeStringException(String methodName, int argIdx, TemplateModel arg) {
        return _MessageUtil.newMethodArgUnexpectedTypeException(methodName, argIdx, "string", arg);
    }

    public static TemplateModelException newMethodArgMustBeNumberException(String methodName, int argIdx, TemplateModel arg) {
        return _MessageUtil.newMethodArgUnexpectedTypeException(methodName, argIdx, "number", arg);
    }

    public static TemplateModelException newMethodArgMustBeBooleanException(String methodName, int argIdx, TemplateModel arg) {
        return _MessageUtil.newMethodArgUnexpectedTypeException(methodName, argIdx, "boolean", arg);
    }

    public static TemplateModelException newMethodArgMustBeExtendedHashException(String methodName, int argIdx, TemplateModel arg) {
        return _MessageUtil.newMethodArgUnexpectedTypeException(methodName, argIdx, "extended hash", arg);
    }

    public static TemplateModelException newMethodArgMustBeExtendedHashOrSequnceException(String methodName, int argIdx, TemplateModel arg) {
        return _MessageUtil.newMethodArgUnexpectedTypeException(methodName, argIdx, "extended hash or sequence", arg);
    }

    public static TemplateModelException newMethodArgMustBeSequenceException(String methodName, int argIdx, TemplateModel arg) {
        return _MessageUtil.newMethodArgUnexpectedTypeException(methodName, argIdx, "sequence", arg);
    }

    public static TemplateModelException newMethodArgMustBeSequenceOrCollectionException(String methodName, int argIdx, TemplateModel arg) {
        return _MessageUtil.newMethodArgUnexpectedTypeException(methodName, argIdx, "sequence or collection", arg);
    }

    public static TemplateModelException newMethodArgMustBeStringOrMarkupOutputException(String methodName, int argIdx, TemplateModel arg) {
        return _MessageUtil.newMethodArgUnexpectedTypeException(methodName, argIdx, "string or markup output", arg);
    }

    public static TemplateModelException newMethodArgUnexpectedTypeException(String methodName, int argIdx, String expectedType, TemplateModel arg) {
        return new _TemplateModelException(methodName, "(...) expects ", new _DelayedAOrAn(expectedType), " as argument #", argIdx + 1, ", but received ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(arg)), ".");
    }

    public static TemplateModelException newMethodArgInvalidValueException(String methodName, int argIdx, Object ... details) {
        return new _TemplateModelException(methodName, "(...) argument #", argIdx + 1, " had invalid value: ", details);
    }

    public static TemplateModelException newMethodArgsInvalidValueException(String methodName, Object ... details) {
        return new _TemplateModelException(methodName, "(...) arguments have invalid value: ", details);
    }

    public static TemplateException newInstantiatingClassNotAllowedException(String className, Environment env) {
        return new _MiscTemplateException(env, "Instantiating ", className, " is not allowed in the template for security reasons.");
    }

    public static _TemplateModelException newCantFormatUnknownTypeDateException(Expression dateSourceExpr, UnknownDateTypeFormattingUnsupportedException cause) {
        return new _TemplateModelException((Throwable)cause, null, new _ErrorDescriptionBuilder(UNKNOWN_DATE_TO_STRING_ERROR_MESSAGE).blame(dateSourceExpr).tips(UNKNOWN_DATE_TO_STRING_TIPS));
    }

    public static TemplateException newCantFormatDateException(TemplateDateFormat format, Expression dataSrcExp, TemplateValueFormatException e, boolean useTempModelExc) {
        _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Failed to format date/time/datetime with format ", new _DelayedJQuote(format.getDescription()), ": ", e.getMessage()).blame(dataSrcExp);
        return useTempModelExc ? new _TemplateModelException((Throwable)e, null, desc) : new _MiscTemplateException((Throwable)e, null, desc);
    }

    public static TemplateException newCantFormatNumberException(TemplateNumberFormat format, Expression dataSrcExp, TemplateValueFormatException e, boolean useTempModelExc) {
        _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Failed to format number with format ", new _DelayedJQuote(format.getDescription()), ": ", e.getMessage()).blame(dataSrcExp);
        return useTempModelExc ? new _TemplateModelException((Throwable)e, null, desc) : new _MiscTemplateException((Throwable)e, null, desc);
    }

    public static TemplateModelException newKeyValuePairListingNonStringKeyExceptionMessage(TemplateModel key, TemplateHashModelEx listedHashEx) {
        return new _TemplateModelException(new _ErrorDescriptionBuilder("When listing key-value pairs of traditional hash implementations, all keys must be strings, but one of them was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(key)), ".").tip("The listed value's TemplateModel class was ", new _DelayedShortClassName(listedHashEx.getClass()), ", which doesn't implement ", new _DelayedShortClassName(TemplateHashModelEx2.class), ", which leads to this restriction."));
    }

    public static TemplateException newLazilyGeneratedCollectionMustBeSequenceException(Expression blamed) {
        return new _MiscTemplateException(blamed, "The result is a listable value with lazy transformation(s) applied on it, but it's not an FTL sequence (it's not a List-like value, but an Iterator-like value). The place doesn't support such values due to technical limitations. So either pass it to a construct that supports such values (like ", "<#list transformedListable as x>", "), or, if you know that you don't have too many elements, use transformedListable?sequence to allow it to be treated as an FTL sequence.");
    }

    public static String getAOrAn(String s) {
        char sc;
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return "";
        }
        char fc = Character.toLowerCase(s.charAt(0));
        if (fc == 'a' || fc == 'e' || fc == 'i') {
            return "an";
        }
        if (fc == 'h') {
            String ls = s.toLowerCase();
            if (ls.startsWith("has") || ls.startsWith("hi")) {
                return "a";
            }
            if (ls.startsWith("ht")) {
                return "an";
            }
            return "a(n)";
        }
        if (fc == 'u' || fc == 'o') {
            return "a(n)";
        }
        char c = sc = s.length() > 1 ? s.charAt(1) : (char)'\u0000';
        if (fc == 'x' && sc != 'a' && sc != 'e' && sc != 'i' && sc != 'a' && sc != 'o' && sc != 'u') {
            return "an";
        }
        return "a";
    }
}

