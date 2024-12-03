/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.constants;

import java.util.HashMap;
import java.util.Map;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.util.XRRuntimeException;

public class IdentValue
implements FSDerivedValue {
    private static int maxAssigned = 0;
    private final String ident;
    public final int FS_ID;
    public static final IdentValue ABSOLUTE = IdentValue.addValue("absolute");
    public static final IdentValue ALWAYS = IdentValue.addValue("always");
    public static final IdentValue ARMENIAN = IdentValue.addValue("armenian");
    public static final IdentValue AUTO = IdentValue.addValue("auto");
    public static final IdentValue AVOID = IdentValue.addValue("avoid");
    public static final IdentValue BASELINE = IdentValue.addValue("baseline");
    public static final IdentValue BLINK = IdentValue.addValue("blink");
    public static final IdentValue BLOCK = IdentValue.addValue("block");
    public static final IdentValue BOLD = IdentValue.addValue("bold");
    public static final IdentValue BOLDER = IdentValue.addValue("bolder");
    public static final IdentValue BORDER_BOX = IdentValue.addValue("border-box");
    public static final IdentValue BOTH = IdentValue.addValue("both");
    public static final IdentValue BOTTOM = IdentValue.addValue("bottom");
    public static final IdentValue CAPITALIZE = IdentValue.addValue("capitalize");
    public static final IdentValue CENTER = IdentValue.addValue("center");
    public static final IdentValue CIRCLE = IdentValue.addValue("circle");
    public static final IdentValue CJK_IDEOGRAPHIC = IdentValue.addValue("cjk-ideographic");
    public static final IdentValue CLOSE_QUOTE = IdentValue.addValue("close-quote");
    public static final IdentValue COLLAPSE = IdentValue.addValue("collapse");
    public static final IdentValue COMPACT = IdentValue.addValue("compact");
    public static final IdentValue CONTAIN = IdentValue.addValue("contain");
    public static final IdentValue CONTENT_BOX = IdentValue.addValue("content-box");
    public static final IdentValue COVER = IdentValue.addValue("cover");
    public static final IdentValue CREATE = IdentValue.addValue("create");
    public static final IdentValue DASHED = IdentValue.addValue("dashed");
    public static final IdentValue DECIMAL = IdentValue.addValue("decimal");
    public static final IdentValue DECIMAL_LEADING_ZERO = IdentValue.addValue("decimal-leading-zero");
    public static final IdentValue DISC = IdentValue.addValue("disc");
    public static final IdentValue DOTTED = IdentValue.addValue("dotted");
    public static final IdentValue DOUBLE = IdentValue.addValue("double");
    public static final IdentValue DYNAMIC = IdentValue.addValue("dynamic");
    public static final IdentValue FIXED = IdentValue.addValue("fixed");
    public static final IdentValue FONT_WEIGHT_100 = IdentValue.addValue("100");
    public static final IdentValue FONT_WEIGHT_200 = IdentValue.addValue("200");
    public static final IdentValue FONT_WEIGHT_300 = IdentValue.addValue("300");
    public static final IdentValue FONT_WEIGHT_400 = IdentValue.addValue("400");
    public static final IdentValue FONT_WEIGHT_500 = IdentValue.addValue("500");
    public static final IdentValue FONT_WEIGHT_600 = IdentValue.addValue("600");
    public static final IdentValue FONT_WEIGHT_700 = IdentValue.addValue("700");
    public static final IdentValue FONT_WEIGHT_800 = IdentValue.addValue("800");
    public static final IdentValue FONT_WEIGHT_900 = IdentValue.addValue("900");
    public static final IdentValue FS_CONTENT_PLACEHOLDER = IdentValue.addValue("-fs-content-placeholder");
    public static final IdentValue FS_INITIAL_VALUE = IdentValue.addValue("-fs-initial-value");
    public static final IdentValue GEORGIAN = IdentValue.addValue("georgian");
    public static final IdentValue GROOVE = IdentValue.addValue("groove");
    public static final IdentValue HEBREW = IdentValue.addValue("hebrew");
    public static final IdentValue HIDDEN = IdentValue.addValue("hidden");
    public static final IdentValue HIDE = IdentValue.addValue("hide");
    public static final IdentValue HIRAGANA = IdentValue.addValue("hiragana");
    public static final IdentValue HIRAGANA_IROHA = IdentValue.addValue("hiragana-iroha");
    public static final IdentValue INHERIT = IdentValue.addValue("inherit");
    public static final IdentValue INLINE = IdentValue.addValue("inline");
    public static final IdentValue INLINE_BLOCK = IdentValue.addValue("inline-block");
    public static final IdentValue INLINE_TABLE = IdentValue.addValue("inline-table");
    public static final IdentValue INSET = IdentValue.addValue("inset");
    public static final IdentValue INSIDE = IdentValue.addValue("inside");
    public static final IdentValue ITALIC = IdentValue.addValue("italic");
    public static final IdentValue JUSTIFY = IdentValue.addValue("justify");
    public static final IdentValue KATAKANA = IdentValue.addValue("katakana");
    public static final IdentValue KATAKANA_IROHA = IdentValue.addValue("katakana-iroha");
    public static final IdentValue KEEP = IdentValue.addValue("keep");
    public static final IdentValue LANDSCAPE = IdentValue.addValue("landscape");
    public static final IdentValue LEFT = IdentValue.addValue("left");
    public static final IdentValue LIGHTER = IdentValue.addValue("lighter");
    public static final IdentValue LINE = IdentValue.addValue("line");
    public static final IdentValue LINE_THROUGH = IdentValue.addValue("line-through");
    public static final IdentValue LIST_ITEM = IdentValue.addValue("list-item");
    public static final IdentValue LOWER_ALPHA = IdentValue.addValue("lower-alpha");
    public static final IdentValue LOWER_GREEK = IdentValue.addValue("lower-greek");
    public static final IdentValue LOWER_LATIN = IdentValue.addValue("lower-latin");
    public static final IdentValue LOWER_ROMAN = IdentValue.addValue("lower-roman");
    public static final IdentValue LOWERCASE = IdentValue.addValue("lowercase");
    public static final IdentValue LTR = IdentValue.addValue("ltr");
    public static final IdentValue MARKER = IdentValue.addValue("marker");
    public static final IdentValue MIDDLE = IdentValue.addValue("middle");
    public static final IdentValue NO_CLOSE_QUOTE = IdentValue.addValue("no-close-quote");
    public static final IdentValue NO_OPEN_QUOTE = IdentValue.addValue("no-open-quote");
    public static final IdentValue NO_REPEAT = IdentValue.addValue("no-repeat");
    public static final IdentValue NONE = IdentValue.addValue("none");
    public static final IdentValue NORMAL = IdentValue.addValue("normal");
    public static final IdentValue NOWRAP = IdentValue.addValue("nowrap");
    public static final IdentValue BREAK_WORD = IdentValue.addValue("break-word");
    public static final IdentValue OBLIQUE = IdentValue.addValue("oblique");
    public static final IdentValue OPEN_QUOTE = IdentValue.addValue("open-quote");
    public static final IdentValue OUTSET = IdentValue.addValue("outset");
    public static final IdentValue OUTSIDE = IdentValue.addValue("outside");
    public static final IdentValue OVERLINE = IdentValue.addValue("overline");
    public static final IdentValue PAGINATE = IdentValue.addValue("paginate");
    public static final IdentValue POINTER = IdentValue.addValue("pointer");
    public static final IdentValue PORTRAIT = IdentValue.addValue("portrait");
    public static final IdentValue PRE = IdentValue.addValue("pre");
    public static final IdentValue PRE_LINE = IdentValue.addValue("pre-line");
    public static final IdentValue PRE_WRAP = IdentValue.addValue("pre-wrap");
    public static final IdentValue RELATIVE = IdentValue.addValue("relative");
    public static final IdentValue REPEAT = IdentValue.addValue("repeat");
    public static final IdentValue REPEAT_X = IdentValue.addValue("repeat-x");
    public static final IdentValue REPEAT_Y = IdentValue.addValue("repeat-y");
    public static final IdentValue RIDGE = IdentValue.addValue("ridge");
    public static final IdentValue RIGHT = IdentValue.addValue("right");
    public static final IdentValue RUN_IN = IdentValue.addValue("run-in");
    public static final IdentValue SCROLL = IdentValue.addValue("scroll");
    public static final IdentValue SEPARATE = IdentValue.addValue("separate");
    public static final IdentValue SHOW = IdentValue.addValue("show");
    public static final IdentValue SMALL_CAPS = IdentValue.addValue("small-caps");
    public static final IdentValue SOLID = IdentValue.addValue("solid");
    public static final IdentValue SQUARE = IdentValue.addValue("square");
    public static final IdentValue STATIC = IdentValue.addValue("static");
    public static final IdentValue SUB = IdentValue.addValue("sub");
    public static final IdentValue SUPER = IdentValue.addValue("super");
    public static final IdentValue TABLE = IdentValue.addValue("table");
    public static final IdentValue TABLE_CAPTION = IdentValue.addValue("table-caption");
    public static final IdentValue TABLE_CELL = IdentValue.addValue("table-cell");
    public static final IdentValue TABLE_COLUMN = IdentValue.addValue("table-column");
    public static final IdentValue TABLE_COLUMN_GROUP = IdentValue.addValue("table-column-group");
    public static final IdentValue TABLE_FOOTER_GROUP = IdentValue.addValue("table-footer-group");
    public static final IdentValue TABLE_HEADER_GROUP = IdentValue.addValue("table-header-group");
    public static final IdentValue TABLE_ROW = IdentValue.addValue("table-row");
    public static final IdentValue TABLE_ROW_GROUP = IdentValue.addValue("table-row-group");
    public static final IdentValue TEXT_BOTTOM = IdentValue.addValue("text-bottom");
    public static final IdentValue TEXT_TOP = IdentValue.addValue("text-top");
    public static final IdentValue THICK = IdentValue.addValue("thick");
    public static final IdentValue THIN = IdentValue.addValue("thin");
    public static final IdentValue TOP = IdentValue.addValue("top");
    public static final IdentValue TRANSPARENT = IdentValue.addValue("transparent");
    public static final IdentValue UNDERLINE = IdentValue.addValue("underline");
    public static final IdentValue UPPER_ALPHA = IdentValue.addValue("upper-alpha");
    public static final IdentValue UPPER_LATIN = IdentValue.addValue("upper-latin");
    public static final IdentValue UPPER_ROMAN = IdentValue.addValue("upper-roman");
    public static final IdentValue UPPERCASE = IdentValue.addValue("uppercase");
    public static final IdentValue VISIBLE = IdentValue.addValue("visible");
    public static final IdentValue CROSSHAIR = IdentValue.addValue("crosshair");
    public static final IdentValue DEFAULT = IdentValue.addValue("default");
    public static final IdentValue EMBED = IdentValue.addValue("embed");
    public static final IdentValue E_RESIZE = IdentValue.addValue("e-resize");
    public static final IdentValue HELP = IdentValue.addValue("help");
    public static final IdentValue LARGE = IdentValue.addValue("large");
    public static final IdentValue LARGER = IdentValue.addValue("larger");
    public static final IdentValue MEDIUM = IdentValue.addValue("medium");
    public static final IdentValue MOVE = IdentValue.addValue("move");
    public static final IdentValue N_RESIZE = IdentValue.addValue("n-resize");
    public static final IdentValue NE_RESIZE = IdentValue.addValue("ne-resize");
    public static final IdentValue NW_RESIZE = IdentValue.addValue("nw-resize");
    public static final IdentValue PROGRESS = IdentValue.addValue("progress");
    public static final IdentValue S_RESIZE = IdentValue.addValue("s-resize");
    public static final IdentValue SE_RESIZE = IdentValue.addValue("se-resize");
    public static final IdentValue SMALL = IdentValue.addValue("small");
    public static final IdentValue SMALLER = IdentValue.addValue("smaller");
    public static final IdentValue START = IdentValue.addValue("start");
    public static final IdentValue SW_RESIZE = IdentValue.addValue("sw-resize");
    public static final IdentValue TEXT = IdentValue.addValue("text");
    public static final IdentValue W_RESIZE = IdentValue.addValue("w-resize");
    public static final IdentValue WAIT = IdentValue.addValue("wait");
    public static final IdentValue X_LARGE = IdentValue.addValue("x-large");
    public static final IdentValue X_SMALL = IdentValue.addValue("x-small");
    public static final IdentValue XX_LARGE = IdentValue.addValue("xx-large");
    public static final IdentValue XX_SMALL = IdentValue.addValue("xx-small");
    public static final IdentValue MANUAL = IdentValue.addValue("manual");
    private static Map ALL_IDENT_VALUES;

    private IdentValue(String ident) {
        this.ident = ident;
        this.FS_ID = maxAssigned++;
    }

    public String toString() {
        return this.ident;
    }

    public static IdentValue getByIdentString(String ident) {
        IdentValue val = (IdentValue)ALL_IDENT_VALUES.get(ident);
        if (val == null) {
            throw new XRRuntimeException("Ident named " + ident + " has no IdentValue instance assigned to it.");
        }
        return val;
    }

    public static boolean looksLikeIdent(String ident) {
        return (IdentValue)ALL_IDENT_VALUES.get(ident) != null;
    }

    public static IdentValue valueOf(String ident) {
        return (IdentValue)ALL_IDENT_VALUES.get(ident);
    }

    public static int getIdentCount() {
        return ALL_IDENT_VALUES.size();
    }

    private static final synchronized IdentValue addValue(String ident) {
        if (ALL_IDENT_VALUES == null) {
            ALL_IDENT_VALUES = new HashMap();
        }
        IdentValue val = new IdentValue(ident);
        ALL_IDENT_VALUES.put(ident, val);
        return val;
    }

    @Override
    public boolean isDeclaredInherit() {
        return this == INHERIT;
    }

    public FSDerivedValue computedValue() {
        return this;
    }

    @Override
    public float asFloat() {
        throw new XRRuntimeException("Ident value is never a float; wrong class used for derived value.");
    }

    @Override
    public FSColor asColor() {
        throw new XRRuntimeException("Ident value is never a color; wrong class used for derived value.");
    }

    @Override
    public float getFloatProportionalTo(CSSName cssName, float baseValue, CssContext ctx) {
        throw new XRRuntimeException("Ident value (" + this.toString() + ") is never a length; wrong class used for derived value.");
    }

    @Override
    public String asString() {
        return this.toString();
    }

    @Override
    public String[] asStringArray() {
        throw new XRRuntimeException("Ident value is never a string array; wrong class used for derived value.");
    }

    @Override
    public IdentValue asIdentValue() {
        return this;
    }

    @Override
    public boolean hasAbsoluteUnit() {
        throw new XRRuntimeException("Ident value is never an absolute unit; wrong class used for derived value; this ident value is a " + this.asString());
    }

    @Override
    public boolean isIdent() {
        return true;
    }

    @Override
    public boolean isDependentOnFontSize() {
        return false;
    }
}

