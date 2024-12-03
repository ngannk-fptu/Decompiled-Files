/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.util.XRRuntimeException;

public final class Idents {
    private static final String RCSS_NUMBER = "(-)?((\\d){1,10}((\\.)(\\d){1,10})?)";
    private static final String RCSS_LENGTH = "((0$)|(((-)?((\\d){1,10}((\\.)(\\d){1,10})?))+((em)|(ex)|(px)|(cm)|(mm)|(in)|(pt)|(pc)|(%))))";
    private static final Pattern CSS_NUMBER_PATTERN = Pattern.compile("(-)?((\\d){1,10}((\\.)(\\d){1,10})?)");
    private static final Pattern CSS_LENGTH_PATTERN = Pattern.compile("((0$)|(((-)?((\\d){1,10}((\\.)(\\d){1,10})?))+((em)|(ex)|(px)|(cm)|(mm)|(in)|(pt)|(pc)|(%))))");
    private static final Pattern COLOR_HEX_PATTERN = Pattern.compile("#((((\\d)|[a-fA-F]){6})|(((\\d)|[a-fA-F]){3}))");
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("^-?[_a-z][_a-z0-9-]+\\(");
    private static final Map COLOR_MAP = new HashMap();
    private static final Map FONT_SIZES;
    private static final Map FONT_WEIGHTS;
    private static final Map BORDER_WIDTHS;
    private static final Map BACKGROUND_POSITIONS;
    private static final List BACKGROUND_REPEATS;
    private static final List BORDER_STYLES;
    private static final List LIST_TYPES;
    private static final List FONT_STYLES;
    private static final List BACKGROUND_POSITIONS_IDENTS;

    public static String convertIdent(CSSName cssName, String ident) {
        if (ident.equals("inherit")) {
            return ident;
        }
        String val = ident;
        if (cssName == CSSName.FONT_SIZE) {
            String size = (String)FONT_SIZES.get(ident);
            val = size == null ? ident : size;
        } else if (cssName == CSSName.FONT_WEIGHT) {
            String size = (String)FONT_WEIGHTS.get(ident);
            val = size == null ? ident : size;
        } else if (cssName == CSSName.BACKGROUND_POSITION) {
            String pos = (String)BACKGROUND_POSITIONS.get(ident);
            val = pos == null ? ident : pos;
        } else if (cssName == CSSName.BORDER_BOTTOM_WIDTH || cssName == CSSName.BORDER_LEFT_WIDTH || cssName == CSSName.BORDER_RIGHT_WIDTH || cssName == CSSName.BORDER_WIDTH_SHORTHAND || cssName == CSSName.BORDER_TOP_WIDTH) {
            String size = (String)BORDER_WIDTHS.get(ident);
            val = size == null ? ident : size;
        } else if ((cssName == CSSName.BORDER_BOTTOM_COLOR || cssName == CSSName.BORDER_LEFT_COLOR || cssName == CSSName.BORDER_RIGHT_COLOR || cssName == CSSName.BORDER_COLOR_SHORTHAND || cssName == CSSName.BORDER_TOP_COLOR || cssName == CSSName.BACKGROUND_COLOR || cssName == CSSName.COLOR || cssName == CSSName.OUTLINE_COLOR) && (val = Idents.getColorHex(ident)) == null) {
            String fallback = CSSName.initialValue(cssName);
            if (fallback.startsWith("=")) {
                fallback = CSSName.initialValue(CSSName.getByPropertyName(fallback.substring(1)));
            }
            val = Idents.getColorHex(fallback);
        }
        return val;
    }

    public static boolean looksLikeABorderStyle(String val) {
        return BORDER_STYLES.contains(val);
    }

    public static boolean looksLikeAColor(String val) {
        return COLOR_MAP.get(val) != null || val.startsWith("#") && (val.length() == 7 || val.length() == 4) || val.startsWith("rgb");
    }

    public static boolean looksLikeALength(String val) {
        return CSS_LENGTH_PATTERN.matcher(val).matches();
    }

    public static boolean looksLikeAURI(String val) {
        return val.startsWith("url(") && val.endsWith(")");
    }

    public static boolean looksLikeAFunction(String value) {
        return FUNCTION_PATTERN.matcher(value).find();
    }

    public static boolean looksLikeABGRepeat(String val) {
        return BACKGROUND_REPEATS.indexOf(val) >= 0;
    }

    public static boolean looksLikeABGAttachment(String val) {
        return "scroll".equals(val) || "fixed".equals(val);
    }

    public static boolean looksLikeABGPosition(String val) {
        return BACKGROUND_POSITIONS_IDENTS.contains(val) || Idents.looksLikeALength(val);
    }

    public static boolean looksLikeAListStyleType(String val) {
        return LIST_TYPES.indexOf(val) >= 0;
    }

    public static boolean looksLikeAListStyleImage(String val) {
        return "none".equals(val) || Idents.looksLikeAURI(val);
    }

    public static boolean looksLikeAListStylePosition(String val) {
        return "inside".equals(val) || "outside".equals(val);
    }

    public static boolean looksLikeAFontStyle(String val) {
        return FONT_STYLES.indexOf(val) >= 0;
    }

    public static boolean looksLikeAFontVariant(String val) {
        return "normal".equals(val) || "small-caps".equals(val);
    }

    public static boolean looksLikeAFontWeight(String val) {
        return FONT_WEIGHTS.get(val) != null;
    }

    public static boolean looksLikeAFontSize(String val) {
        return FONT_SIZES.get(val) != null || Idents.looksLikeALength(val) || "larger".equals(val) || "smaller".equals(val);
    }

    public static boolean looksLikeALineHeight(String val) {
        return "normal".equals(val) || Idents.looksLikeALength(val) || Idents.looksLikeANumber(val);
    }

    public static boolean looksLikeANumber(String val) {
        return CSS_NUMBER_PATTERN.matcher(val).matches();
    }

    public static String getColorHex(String value) {
        if (value == null) {
            throw new XRRuntimeException("value is null on getColorHex()");
        }
        String retval = (String)COLOR_MAP.get(value.toLowerCase());
        if (retval == null) {
            if (value.trim().startsWith("rgb(")) {
                retval = value;
            } else {
                Matcher m = COLOR_HEX_PATTERN.matcher(value);
                if (m.matches()) {
                    retval = value;
                }
            }
        }
        return retval;
    }

    public static boolean looksLikeAQuote(String content) {
        return content.equals("open-quote") || content.equals("close-quote");
    }

    public static boolean looksLikeASkipQuote(String content) {
        return content.equals("no-open-quote") || content.equals("no-close-quote");
    }

    static {
        COLOR_MAP.put("aqua", "#00ffff");
        COLOR_MAP.put("black", "#000000");
        COLOR_MAP.put("blue", "#0000ff");
        COLOR_MAP.put("fuchsia", "#ff00ff");
        COLOR_MAP.put("gray", "#808080");
        COLOR_MAP.put("green", "#008000");
        COLOR_MAP.put("lime", "#00ff00");
        COLOR_MAP.put("maroon", "#800000");
        COLOR_MAP.put("navy", "#000080");
        COLOR_MAP.put("olive", "#808000");
        COLOR_MAP.put("orange", "#ffa500");
        COLOR_MAP.put("purple", "#800080");
        COLOR_MAP.put("red", "#ff0000");
        COLOR_MAP.put("silver", "#c0c0c0");
        COLOR_MAP.put("teal", "#008080");
        COLOR_MAP.put("transparent", "transparent");
        COLOR_MAP.put("white", "#ffffff");
        COLOR_MAP.put("yellow", "#ffff00");
        FONT_SIZES = new HashMap();
        FONT_SIZES.put("xx-small", "6.9pt");
        FONT_SIZES.put("x-small", "8.3pt");
        FONT_SIZES.put("small", "10pt");
        FONT_SIZES.put("medium", "12pt");
        FONT_SIZES.put("large", "14.4pt");
        FONT_SIZES.put("x-large", "17.3pt");
        FONT_SIZES.put("xx-large", "20.7pt");
        FONT_SIZES.put("smaller", "0.8em");
        FONT_SIZES.put("larger", "1.2em");
        FONT_WEIGHTS = new HashMap();
        FONT_WEIGHTS.put("normal", "400");
        FONT_WEIGHTS.put("bold", "700");
        FONT_WEIGHTS.put("100", "100");
        FONT_WEIGHTS.put("200", "200");
        FONT_WEIGHTS.put("300", "300");
        FONT_WEIGHTS.put("400", "400");
        FONT_WEIGHTS.put("500", "500");
        FONT_WEIGHTS.put("600", "600");
        FONT_WEIGHTS.put("700", "700");
        FONT_WEIGHTS.put("800", "800");
        FONT_WEIGHTS.put("900", "900");
        FONT_WEIGHTS.put("bolder", "bolder");
        FONT_WEIGHTS.put("lighter", "lighter");
        BORDER_WIDTHS = new HashMap();
        BORDER_WIDTHS.put("thin", "1px");
        BORDER_WIDTHS.put("medium", "2px");
        BORDER_WIDTHS.put("thick", "3px");
        BACKGROUND_POSITIONS_IDENTS = new ArrayList();
        BACKGROUND_POSITIONS_IDENTS.add("top");
        BACKGROUND_POSITIONS_IDENTS.add("center");
        BACKGROUND_POSITIONS_IDENTS.add("bottom");
        BACKGROUND_POSITIONS_IDENTS.add("right");
        BACKGROUND_POSITIONS_IDENTS.add("left");
        BACKGROUND_POSITIONS = new HashMap();
        BACKGROUND_POSITIONS.put("top left", "0% 0%");
        BACKGROUND_POSITIONS.put("left top", "0% 0%");
        BACKGROUND_POSITIONS.put("top center", "50% 0%");
        BACKGROUND_POSITIONS.put("center top", "50% 0%");
        BACKGROUND_POSITIONS.put("right top", "100% 0%");
        BACKGROUND_POSITIONS.put("top right", "100% 0%");
        BACKGROUND_POSITIONS.put("left center", "0% 50%");
        BACKGROUND_POSITIONS.put("center left", "0% 50%");
        BACKGROUND_POSITIONS.put("center", "50% 50%");
        BACKGROUND_POSITIONS.put("center center", "50% 50%");
        BACKGROUND_POSITIONS.put("right center", "100% 50%");
        BACKGROUND_POSITIONS.put("center right", "100% 50%");
        BACKGROUND_POSITIONS.put("bottom left", "0% 100%");
        BACKGROUND_POSITIONS.put("left bottom", "0% 100%");
        BACKGROUND_POSITIONS.put("bottom center", "50% 100%");
        BACKGROUND_POSITIONS.put("center bottom", "50% 100%");
        BACKGROUND_POSITIONS.put("bottom right", "100% 100%");
        BACKGROUND_POSITIONS.put("right bottom", "100% 100%");
        BACKGROUND_REPEATS = new ArrayList();
        BACKGROUND_REPEATS.add("repeat");
        BACKGROUND_REPEATS.add("repeat-x");
        BACKGROUND_REPEATS.add("repeat-y");
        BACKGROUND_REPEATS.add("no-repeat");
        BORDER_STYLES = new ArrayList();
        BORDER_STYLES.add("none");
        BORDER_STYLES.add("hidden");
        BORDER_STYLES.add("dotted");
        BORDER_STYLES.add("dashed");
        BORDER_STYLES.add("solid");
        BORDER_STYLES.add("double");
        BORDER_STYLES.add("groove");
        BORDER_STYLES.add("ridge");
        BORDER_STYLES.add("inset");
        BORDER_STYLES.add("outset");
        LIST_TYPES = new ArrayList();
        LIST_TYPES.add("disc");
        LIST_TYPES.add("circle");
        LIST_TYPES.add("square");
        LIST_TYPES.add("decimal");
        LIST_TYPES.add("decimal-leading-zero");
        LIST_TYPES.add("lower-roman");
        LIST_TYPES.add("upper-roman");
        LIST_TYPES.add("lower-greek");
        LIST_TYPES.add("lower-alpha");
        LIST_TYPES.add("lower-latin");
        LIST_TYPES.add("upper-alpha");
        LIST_TYPES.add("upper-latin");
        LIST_TYPES.add("hebrew");
        LIST_TYPES.add("armenian");
        LIST_TYPES.add("georgian");
        LIST_TYPES.add("cjk-ideographic");
        LIST_TYPES.add("hiragana");
        LIST_TYPES.add("katakana");
        LIST_TYPES.add("hiragana-iroha");
        LIST_TYPES.add("katakana-iroha");
        LIST_TYPES.add("none");
        FONT_STYLES = new ArrayList();
        FONT_STYLES.add("normal");
        FONT_STYLES.add("italic");
        FONT_STYLES.add("oblique");
    }
}

