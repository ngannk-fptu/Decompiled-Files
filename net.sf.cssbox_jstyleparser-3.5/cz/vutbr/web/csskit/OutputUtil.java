/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermOperator;
import cz.vutbr.web.csskit.CalcArgs;
import java.util.List;

public class OutputUtil {
    public static final String EMPTY_DELIM = "";
    public static final String SPACE_DELIM = " ";
    public static final String DEPTH_DELIM = "\t";
    public static final String QUERY_DELIM = " AND ";
    public static final String RULE_OPENING = " {\n";
    public static final String RULE_CLOSING = "}\n";
    public static final String MEDIA_DELIM = ", ";
    public static final String SELECTOR_DELIM = ", ";
    public static final String IMPORT_KEYWORD = "@import ";
    public static final String URL_OPENING = "url('";
    public static final String URL_CLOSING = "')";
    public static final String LINE_CLOSING = ";\n";
    public static final String NEW_LINE = "\n";
    public static final String MEDIA_KEYWORD = "@media ";
    public static final String KEYFRAMES_KEYWORD = "@keyframes ";
    public static final String RULE_DELIM = "\n";
    public static final String CHARSET_KEYWORD = "@charset ";
    public static final String CHARSET_OPENING = "\"";
    public static final String CHARSET_CLOSING = "\";\n";
    public static final String PROPERTY_OPENING = ": ";
    public static final String PROPERTY_CLOSING = ";\n";
    public static final String IMPORTANT_KEYWORD = "!important";
    public static final String PAGE_KEYWORD = "@page";
    public static final String PSEUDO_OPENING = ":";
    public static final String PAGE_CLOSING = "";
    public static final String VIEWPORT_KEYWORD = "@viewport";
    public static final String FONT_FACE_KEYWORD = "@font-face";
    public static final String FUNCTION_OPENING = "(";
    public static final String FUNCTION_CLOSING = ")";
    public static final String STRING_OPENING = "'";
    public static final String STRING_CLOSING = "'";
    public static final String ATTRIBUTE_OPENING = "[";
    public static final String ATTRIBUTE_CLOSING = "]";
    public static final String PERCENT_SIGN = "%";
    public static final String HASH_SIGN = "#";
    public static final String MARGIN_AREA_OPENING = "@";
    public static final String MEDIA_EXPR_OPENING = "(";
    public static final String MEDIA_EXPR_CLOSING = ")";
    public static final String MEDIA_FEATURE_DELIM = ": ";
    public static final String CALC_KEYWORD = "calc";
    public static final String RECT_KEYWORD = "rect";

    public static StringBuilder appendTimes(StringBuilder sb, String append, int times) {
        while (times > 0) {
            sb.append(append);
            --times;
        }
        return sb;
    }

    public static <T> StringBuilder appendArray(StringBuilder sb, T[] array, String delimiter) {
        boolean firstRun = true;
        for (T elem : array) {
            if (!firstRun) {
                sb.append(delimiter);
            } else {
                firstRun = false;
            }
            sb.append(elem.toString());
        }
        return sb;
    }

    public static <T> StringBuilder appendList(StringBuilder sb, List<T> list, String delimiter) {
        boolean firstRun = true;
        for (T elem : list) {
            if (!firstRun) {
                sb.append(delimiter);
            } else {
                firstRun = false;
            }
            sb.append(elem.toString());
        }
        return sb;
    }

    public static <T extends PrettyOutput> StringBuilder appendList(StringBuilder sb, List<T> list, String delimiter, int depth) {
        boolean firstRun = true;
        for (PrettyOutput elem : list) {
            if (!firstRun) {
                sb.append(delimiter);
            } else {
                firstRun = false;
            }
            sb.append(elem.toString(depth));
        }
        return sb;
    }

    public static StringBuilder appendCalcArgs(StringBuilder sb, CalcArgs args) {
        String astr = args.evaluate(CalcArgs.stringEvaluator);
        if (!astr.startsWith("(")) {
            sb.append("(");
            sb.append(astr);
            sb.append(")");
        } else {
            sb.append(astr);
        }
        return sb;
    }

    public static StringBuilder appendFunctionArgs(StringBuilder sb, List<Term<?>> list) {
        Term<?> prev = null;
        Term<?> pprev = null;
        for (Term<?> elem : list) {
            boolean sep = true;
            if (elem instanceof TermOperator && ((Character)((TermOperator)elem).getValue()).charValue() == ',') {
                sep = false;
            }
            if (prev != null && prev instanceof TermOperator && ((Character)((TermOperator)prev).getValue()).charValue() == '-' && (pprev == null || pprev instanceof TermOperator)) {
                sep = false;
            }
            if (prev != null && sep) {
                sb.append(SPACE_DELIM);
            }
            pprev = prev;
            prev = elem;
            sb.append(elem.toString());
        }
        return sb;
    }
}

