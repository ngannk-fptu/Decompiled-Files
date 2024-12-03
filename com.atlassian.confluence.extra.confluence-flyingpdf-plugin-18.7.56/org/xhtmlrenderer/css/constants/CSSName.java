/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.constants;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.xhtmlrenderer.css.parser.CSSErrorHandler;
import org.xhtmlrenderer.css.parser.CSSParser;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.BackgroundPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.BorderPropertyBuilders;
import org.xhtmlrenderer.css.parser.property.BorderSpacingPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.ContentPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.CounterPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.FontPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.ListStylePropertyBuilder;
import org.xhtmlrenderer.css.parser.property.OneToFourPropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.parser.property.QuotesPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.SizePropertyBuilder;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.DerivedValueFactory;
import org.xhtmlrenderer.util.XRLog;

public final class CSSName
implements Comparable {
    private static final Integer PRIMITIVE = new Integer(0);
    private static final Integer SHORTHAND = new Integer(1);
    private static final Integer INHERITS = new Integer(2);
    private static final Integer NOT_INHERITED = new Integer(3);
    private static int maxAssigned;
    private final String propName;
    private final String initialValue;
    private final boolean propertyInherits;
    private FSDerivedValue initialDerivedValue;
    private final boolean implemented;
    private final PropertyBuilder builder;
    public final int FS_ID;
    private static final CSSName[] ALL_PROPERTIES;
    private static final Map ALL_PROPERTY_NAMES;
    private static final Map ALL_PRIMITIVE_PROPERTY_NAMES;
    public static final CSSName COLOR;
    public static final CSSName BACKGROUND_COLOR;
    public static final CSSName BACKGROUND_IMAGE;
    public static final CSSName BACKGROUND_REPEAT;
    public static final CSSName BACKGROUND_ATTACHMENT;
    public static final CSSName BACKGROUND_POSITION;
    public static final CSSName BACKGROUND_SIZE;
    public static final CSSName BORDER_COLLAPSE;
    public static final CSSName FS_BORDER_SPACING_HORIZONTAL;
    public static final CSSName FS_BORDER_SPACING_VERTICAL;
    public static final CSSName FS_DYNAMIC_AUTO_WIDTH;
    public static final CSSName FS_FONT_METRIC_SRC;
    public static final CSSName FS_KEEP_WITH_INLINE;
    public static final CSSName FS_PAGE_WIDTH;
    public static final CSSName FS_PAGE_HEIGHT;
    public static final CSSName FS_PAGE_SEQUENCE;
    public static final CSSName FS_PDF_FONT_EMBED;
    public static final CSSName FS_PDF_FONT_ENCODING;
    public static final CSSName FS_PAGE_ORIENTATION;
    public static final CSSName FS_TABLE_PAGINATE;
    public static final CSSName FS_TEXT_DECORATION_EXTENT;
    public static final CSSName FS_FIT_IMAGES_TO_WIDTH;
    public static final CSSName FS_NAMED_DESTINATION;
    public static final CSSName BOTTOM;
    public static final CSSName CAPTION_SIDE;
    public static final CSSName CLEAR;
    public static final CSSName CLIP;
    public static final CSSName CONTENT;
    public static final CSSName COUNTER_INCREMENT;
    public static final CSSName COUNTER_RESET;
    public static final CSSName CURSOR;
    public static final CSSName DIRECTION;
    public static final CSSName DISPLAY;
    public static final CSSName EMPTY_CELLS;
    public static final CSSName FLOAT;
    public static final CSSName FONT_STYLE;
    public static final CSSName FONT_VARIANT;
    public static final CSSName FONT_WEIGHT;
    public static final CSSName FONT_SIZE;
    public static final CSSName LINE_HEIGHT;
    public static final CSSName FONT_FAMILY;
    public static final CSSName FS_COLSPAN;
    public static final CSSName FS_ROWSPAN;
    public static final CSSName HEIGHT;
    public static final CSSName LEFT;
    public static final CSSName LETTER_SPACING;
    public static final CSSName LIST_STYLE_TYPE;
    public static final CSSName LIST_STYLE_POSITION;
    public static final CSSName LIST_STYLE_IMAGE;
    public static final CSSName MAX_HEIGHT;
    public static final CSSName MAX_WIDTH;
    public static final CSSName MIN_HEIGHT;
    public static final CSSName MIN_WIDTH;
    public static final CSSName ORPHANS;
    public static final CSSName OUTLINE_COLOR;
    public static final CSSName OUTLINE_STYLE;
    public static final CSSName OUTLINE_WIDTH;
    public static final CSSName OVERFLOW;
    public static final CSSName PAGE;
    public static final CSSName PAGE_BREAK_AFTER;
    public static final CSSName PAGE_BREAK_BEFORE;
    public static final CSSName PAGE_BREAK_INSIDE;
    public static final CSSName POSITION;
    public static final CSSName QUOTES;
    public static final CSSName RIGHT;
    public static final CSSName SRC;
    public static final CSSName TAB_SIZE;
    public static final CSSName TABLE_LAYOUT;
    public static final CSSName TEXT_ALIGN;
    public static final CSSName TEXT_DECORATION;
    public static final CSSName TEXT_INDENT;
    public static final CSSName TEXT_TRANSFORM;
    public static final CSSName TOP;
    public static final CSSName UNICODE_BIDI;
    public static final CSSName VERTICAL_ALIGN;
    public static final CSSName VISIBILITY;
    public static final CSSName WHITE_SPACE;
    public static final CSSName WORD_WRAP;
    public static final CSSName HYPHENS;
    public static final CSSName WIDOWS;
    public static final CSSName WIDTH;
    public static final CSSName WORD_SPACING;
    public static final CSSName Z_INDEX;
    public static final CSSName BORDER_TOP_COLOR;
    public static final CSSName BORDER_RIGHT_COLOR;
    public static final CSSName BORDER_BOTTOM_COLOR;
    public static final CSSName BORDER_LEFT_COLOR;
    public static final CSSName BORDER_TOP_STYLE;
    public static final CSSName BORDER_RIGHT_STYLE;
    public static final CSSName BORDER_BOTTOM_STYLE;
    public static final CSSName BORDER_LEFT_STYLE;
    public static final CSSName BORDER_TOP_WIDTH;
    public static final CSSName BORDER_RIGHT_WIDTH;
    public static final CSSName BORDER_BOTTOM_WIDTH;
    public static final CSSName BORDER_LEFT_WIDTH;
    public static final CSSName BORDER_TOP_LEFT_RADIUS;
    public static final CSSName BORDER_TOP_RIGHT_RADIUS;
    public static final CSSName BORDER_BOTTOM_RIGHT_RADIUS;
    public static final CSSName BORDER_BOTTOM_LEFT_RADIUS;
    public static final CSSName MARGIN_TOP;
    public static final CSSName MARGIN_RIGHT;
    public static final CSSName MARGIN_BOTTOM;
    public static final CSSName MARGIN_LEFT;
    public static final CSSName PADDING_TOP;
    public static final CSSName PADDING_RIGHT;
    public static final CSSName PADDING_BOTTOM;
    public static final CSSName PADDING_LEFT;
    public static final CSSName BACKGROUND_SHORTHAND;
    public static final CSSName BORDER_RADIUS_SHORTHAND;
    public static final CSSName BORDER_WIDTH_SHORTHAND;
    public static final CSSName BORDER_STYLE_SHORTHAND;
    public static final CSSName BORDER_SHORTHAND;
    public static final CSSName BORDER_TOP_SHORTHAND;
    public static final CSSName BORDER_RIGHT_SHORTHAND;
    public static final CSSName BORDER_BOTTOM_SHORTHAND;
    public static final CSSName BORDER_LEFT_SHORTHAND;
    public static final CSSName BORDER_COLOR_SHORTHAND;
    public static final CSSName BORDER_SPACING;
    public static final CSSName FONT_SHORTHAND;
    public static final CSSName LIST_STYLE_SHORTHAND;
    public static final CSSName MARGIN_SHORTHAND;
    public static final CSSName OUTLINE_SHORTHAND;
    public static final CSSName PADDING_SHORTHAND;
    public static final CSSName SIZE_SHORTHAND;
    public static final CSSName BOX_SIZING;
    public static final CSSSideProperties MARGIN_SIDE_PROPERTIES;
    public static final CSSSideProperties PADDING_SIDE_PROPERTIES;
    public static final CSSSideProperties BORDER_SIDE_PROPERTIES;
    public static final CSSSideProperties BORDER_STYLE_PROPERTIES;
    public static final CSSSideProperties BORDER_COLOR_PROPERTIES;

    private CSSName(String propName, String initialValue, boolean inherits, boolean implemented, PropertyBuilder builder) {
        this.propName = propName;
        this.FS_ID = maxAssigned++;
        this.initialValue = initialValue;
        this.propertyInherits = inherits;
        this.implemented = implemented;
        this.builder = builder;
    }

    public String toString() {
        return this.propName;
    }

    public static int countCSSNames() {
        return maxAssigned;
    }

    public static int countCSSPrimitiveNames() {
        return ALL_PRIMITIVE_PROPERTY_NAMES.size();
    }

    public static Iterator allCSS2PropertyNames() {
        return ALL_PROPERTY_NAMES.keySet().iterator();
    }

    public static Iterator allCSS2PrimitivePropertyNames() {
        return ALL_PRIMITIVE_PROPERTY_NAMES.keySet().iterator();
    }

    public static boolean propertyInherits(CSSName cssName) {
        return cssName.propertyInherits;
    }

    public static String initialValue(CSSName cssName) {
        return cssName.initialValue;
    }

    public static FSDerivedValue initialDerivedValue(CSSName cssName) {
        return cssName.initialDerivedValue;
    }

    public static boolean isImplemented(CSSName cssName) {
        return cssName.implemented;
    }

    public static PropertyBuilder getPropertyBuilder(CSSName cssName) {
        return cssName.builder;
    }

    public static CSSName getByPropertyName(String propName) {
        return (CSSName)ALL_PROPERTY_NAMES.get(propName);
    }

    public static CSSName getByID(int id) {
        return ALL_PROPERTIES[id];
    }

    private static synchronized CSSName addProperty(String propName, Object type, String initialValue, Object inherit, PropertyBuilder builder) {
        return CSSName.addProperty(propName, type, initialValue, inherit, true, builder);
    }

    private static synchronized CSSName addProperty(String propName, Object type, String initialValue, Object inherit, boolean implemented, PropertyBuilder builder) {
        CSSName cssName = new CSSName(propName, initialValue, inherit == INHERITS, implemented, builder);
        ALL_PROPERTY_NAMES.put(propName, cssName);
        if (type == PRIMITIVE) {
            ALL_PRIMITIVE_PROPERTY_NAMES.put(propName, cssName);
        }
        return cssName;
    }

    public int compareTo(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return this.FS_ID - ((CSSName)object).FS_ID;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CSSName)) {
            return false;
        }
        CSSName cssName = (CSSName)o;
        return this.FS_ID == cssName.FS_ID;
    }

    public int hashCode() {
        return this.FS_ID;
    }

    static {
        ALL_PROPERTY_NAMES = new TreeMap();
        ALL_PRIMITIVE_PROPERTY_NAMES = new TreeMap();
        COLOR = CSSName.addProperty("color", PRIMITIVE, "black", INHERITS, new PrimitivePropertyBuilders.Color());
        BACKGROUND_COLOR = CSSName.addProperty("background-color", PRIMITIVE, "transparent", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundColor());
        BACKGROUND_IMAGE = CSSName.addProperty("background-image", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundImage());
        BACKGROUND_REPEAT = CSSName.addProperty("background-repeat", PRIMITIVE, "repeat", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundRepeat());
        BACKGROUND_ATTACHMENT = CSSName.addProperty("background-attachment", PRIMITIVE, "scroll", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundAttachment());
        BACKGROUND_POSITION = CSSName.addProperty("background-position", PRIMITIVE, "0% 0%", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundPosition());
        BACKGROUND_SIZE = CSSName.addProperty("background-size", PRIMITIVE, "auto auto", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundSize());
        BORDER_COLLAPSE = CSSName.addProperty("border-collapse", PRIMITIVE, "separate", INHERITS, new PrimitivePropertyBuilders.BorderCollapse());
        FS_BORDER_SPACING_HORIZONTAL = CSSName.addProperty("-fs-border-spacing-horizontal", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.FSBorderSpacingHorizontal());
        FS_BORDER_SPACING_VERTICAL = CSSName.addProperty("-fs-border-spacing-vertical", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.FSBorderSpacingVertical());
        FS_DYNAMIC_AUTO_WIDTH = CSSName.addProperty("-fs-dynamic-auto-width", PRIMITIVE, "static", NOT_INHERITED, new PrimitivePropertyBuilders.FSDynamicAutoWidth());
        FS_FONT_METRIC_SRC = CSSName.addProperty("-fs-font-metric-src", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.FSFontMetricSrc());
        FS_KEEP_WITH_INLINE = CSSName.addProperty("-fs-keep-with-inline", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSKeepWithInline());
        FS_PAGE_WIDTH = CSSName.addProperty("-fs-page-width", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPageWidth());
        FS_PAGE_HEIGHT = CSSName.addProperty("-fs-page-height", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPageHeight());
        FS_PAGE_SEQUENCE = CSSName.addProperty("-fs-page-sequence", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPageSequence());
        FS_PDF_FONT_EMBED = CSSName.addProperty("-fs-pdf-font-embed", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPDFFontEmbed());
        FS_PDF_FONT_ENCODING = CSSName.addProperty("-fs-pdf-font-encoding", PRIMITIVE, "Cp1252", NOT_INHERITED, new PrimitivePropertyBuilders.FSPDFFontEncoding());
        FS_PAGE_ORIENTATION = CSSName.addProperty("-fs-page-orientation", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPageOrientation());
        FS_TABLE_PAGINATE = CSSName.addProperty("-fs-table-paginate", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSTablePaginate());
        FS_TEXT_DECORATION_EXTENT = CSSName.addProperty("-fs-text-decoration-extent", PRIMITIVE, "line", NOT_INHERITED, new PrimitivePropertyBuilders.FSTextDecorationExtent());
        FS_FIT_IMAGES_TO_WIDTH = CSSName.addProperty("-fs-fit-images-to-width", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSFitImagesToWidth());
        FS_NAMED_DESTINATION = CSSName.addProperty("-fs-named-destination", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.FSNamedDestination());
        BOTTOM = CSSName.addProperty("bottom", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Bottom());
        CAPTION_SIDE = CSSName.addProperty("caption-side", PRIMITIVE, "top", INHERITS, new PrimitivePropertyBuilders.CaptionSide());
        CLEAR = CSSName.addProperty("clear", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.Clear());
        CLIP = CSSName.addProperty("clip", PRIMITIVE, "auto", NOT_INHERITED, false, null);
        CONTENT = CSSName.addProperty("content", PRIMITIVE, "normal", NOT_INHERITED, new ContentPropertyBuilder());
        COUNTER_INCREMENT = CSSName.addProperty("counter-increment", PRIMITIVE, "none", NOT_INHERITED, true, new CounterPropertyBuilder.CounterIncrement());
        COUNTER_RESET = CSSName.addProperty("counter-reset", PRIMITIVE, "none", NOT_INHERITED, true, new CounterPropertyBuilder.CounterReset());
        CURSOR = CSSName.addProperty("cursor", PRIMITIVE, "auto", INHERITS, true, new PrimitivePropertyBuilders.Cursor());
        DIRECTION = CSSName.addProperty("direction", PRIMITIVE, "ltr", INHERITS, false, null);
        DISPLAY = CSSName.addProperty("display", PRIMITIVE, "inline", NOT_INHERITED, new PrimitivePropertyBuilders.Display());
        EMPTY_CELLS = CSSName.addProperty("empty-cells", PRIMITIVE, "show", INHERITS, new PrimitivePropertyBuilders.EmptyCells());
        FLOAT = CSSName.addProperty("float", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.Float());
        FONT_STYLE = CSSName.addProperty("font-style", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.FontStyle());
        FONT_VARIANT = CSSName.addProperty("font-variant", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.FontVariant());
        FONT_WEIGHT = CSSName.addProperty("font-weight", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.FontWeight());
        FONT_SIZE = CSSName.addProperty("font-size", PRIMITIVE, "medium", INHERITS, new PrimitivePropertyBuilders.FontSize());
        LINE_HEIGHT = CSSName.addProperty("line-height", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.LineHeight());
        FONT_FAMILY = CSSName.addProperty("font-family", PRIMITIVE, "serif", INHERITS, new PrimitivePropertyBuilders.FontFamily());
        FS_COLSPAN = CSSName.addProperty("-fs-table-cell-colspan", PRIMITIVE, "1", NOT_INHERITED, new PrimitivePropertyBuilders.FSTableCellColspan());
        FS_ROWSPAN = CSSName.addProperty("-fs-table-cell-rowspan", PRIMITIVE, "1", NOT_INHERITED, new PrimitivePropertyBuilders.FSTableCellRowspan());
        HEIGHT = CSSName.addProperty("height", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Height());
        LEFT = CSSName.addProperty("left", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Left());
        LETTER_SPACING = CSSName.addProperty("letter-spacing", PRIMITIVE, "normal", INHERITS, true, new PrimitivePropertyBuilders.LetterSpacing());
        LIST_STYLE_TYPE = CSSName.addProperty("list-style-type", PRIMITIVE, "disc", INHERITS, new PrimitivePropertyBuilders.ListStyleType());
        LIST_STYLE_POSITION = CSSName.addProperty("list-style-position", PRIMITIVE, "outside", INHERITS, new PrimitivePropertyBuilders.ListStylePosition());
        LIST_STYLE_IMAGE = CSSName.addProperty("list-style-image", PRIMITIVE, "none", INHERITS, new PrimitivePropertyBuilders.ListStyleImage());
        MAX_HEIGHT = CSSName.addProperty("max-height", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.MaxHeight());
        MAX_WIDTH = CSSName.addProperty("max-width", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.MaxWidth());
        MIN_HEIGHT = CSSName.addProperty("min-height", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MinHeight());
        MIN_WIDTH = CSSName.addProperty("min-width", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MinWidth());
        ORPHANS = CSSName.addProperty("orphans", PRIMITIVE, "2", INHERITS, true, new PrimitivePropertyBuilders.Orphans());
        OUTLINE_COLOR = CSSName.addProperty("outline-color", PRIMITIVE, "black", NOT_INHERITED, false, null);
        OUTLINE_STYLE = CSSName.addProperty("outline-style", PRIMITIVE, "none", NOT_INHERITED, false, null);
        OUTLINE_WIDTH = CSSName.addProperty("outline-width", PRIMITIVE, "medium", NOT_INHERITED, false, null);
        OVERFLOW = CSSName.addProperty("overflow", PRIMITIVE, "visible", NOT_INHERITED, new PrimitivePropertyBuilders.Overflow());
        PAGE = CSSName.addProperty("page", PRIMITIVE, "auto", INHERITS, new PrimitivePropertyBuilders.Page());
        PAGE_BREAK_AFTER = CSSName.addProperty("page-break-after", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.PageBreakAfter());
        PAGE_BREAK_BEFORE = CSSName.addProperty("page-break-before", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.PageBreakBefore());
        PAGE_BREAK_INSIDE = CSSName.addProperty("page-break-inside", PRIMITIVE, "auto", INHERITS, new PrimitivePropertyBuilders.PageBreakInside());
        POSITION = CSSName.addProperty("position", PRIMITIVE, "static", NOT_INHERITED, new PrimitivePropertyBuilders.Position());
        QUOTES = CSSName.addProperty("quotes", PRIMITIVE, "none", INHERITS, new QuotesPropertyBuilder());
        RIGHT = CSSName.addProperty("right", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Right());
        SRC = CSSName.addProperty("src", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.Src());
        TAB_SIZE = CSSName.addProperty("tab-size", PRIMITIVE, "8", INHERITS, new PrimitivePropertyBuilders.TabSize());
        TABLE_LAYOUT = CSSName.addProperty("table-layout", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.TableLayout());
        TEXT_ALIGN = CSSName.addProperty("text-align", PRIMITIVE, "left", INHERITS, new PrimitivePropertyBuilders.TextAlign());
        TEXT_DECORATION = CSSName.addProperty("text-decoration", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.TextDecoration());
        TEXT_INDENT = CSSName.addProperty("text-indent", PRIMITIVE, "0", INHERITS, new PrimitivePropertyBuilders.TextIndent());
        TEXT_TRANSFORM = CSSName.addProperty("text-transform", PRIMITIVE, "none", INHERITS, new PrimitivePropertyBuilders.TextTransform());
        TOP = CSSName.addProperty("top", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Top());
        UNICODE_BIDI = CSSName.addProperty("unicode-bidi", PRIMITIVE, "normal", NOT_INHERITED, false, null);
        VERTICAL_ALIGN = CSSName.addProperty("vertical-align", PRIMITIVE, "baseline", NOT_INHERITED, new PrimitivePropertyBuilders.VerticalAlign());
        VISIBILITY = CSSName.addProperty("visibility", PRIMITIVE, "visible", INHERITS, new PrimitivePropertyBuilders.Visibility());
        WHITE_SPACE = CSSName.addProperty("white-space", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.WhiteSpace());
        WORD_WRAP = CSSName.addProperty("word-wrap", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.WordWrap());
        HYPHENS = CSSName.addProperty("hyphens", PRIMITIVE, "none", INHERITS, new PrimitivePropertyBuilders.Hyphens());
        WIDOWS = CSSName.addProperty("widows", PRIMITIVE, "2", INHERITS, true, new PrimitivePropertyBuilders.Widows());
        WIDTH = CSSName.addProperty("width", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Width());
        WORD_SPACING = CSSName.addProperty("word-spacing", PRIMITIVE, "normal", INHERITS, true, new PrimitivePropertyBuilders.WordSpacing());
        Z_INDEX = CSSName.addProperty("z-index", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.ZIndex());
        BORDER_TOP_COLOR = CSSName.addProperty("border-top-color", PRIMITIVE, "=color", NOT_INHERITED, new PrimitivePropertyBuilders.BorderTopColor());
        BORDER_RIGHT_COLOR = CSSName.addProperty("border-right-color", PRIMITIVE, "=color", NOT_INHERITED, new PrimitivePropertyBuilders.BorderLeftColor());
        BORDER_BOTTOM_COLOR = CSSName.addProperty("border-bottom-color", PRIMITIVE, "=color", NOT_INHERITED, new PrimitivePropertyBuilders.BorderBottomColor());
        BORDER_LEFT_COLOR = CSSName.addProperty("border-left-color", PRIMITIVE, "=color", NOT_INHERITED, new PrimitivePropertyBuilders.BorderLeftColor());
        BORDER_TOP_STYLE = CSSName.addProperty("border-top-style", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BorderTopStyle());
        BORDER_RIGHT_STYLE = CSSName.addProperty("border-right-style", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BorderRightStyle());
        BORDER_BOTTOM_STYLE = CSSName.addProperty("border-bottom-style", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BorderBottomStyle());
        BORDER_LEFT_STYLE = CSSName.addProperty("border-left-style", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BorderLeftStyle());
        BORDER_TOP_WIDTH = CSSName.addProperty("border-top-width", PRIMITIVE, "medium", NOT_INHERITED, new PrimitivePropertyBuilders.BorderTopWidth());
        BORDER_RIGHT_WIDTH = CSSName.addProperty("border-right-width", PRIMITIVE, "medium", NOT_INHERITED, new PrimitivePropertyBuilders.BorderRightWidth());
        BORDER_BOTTOM_WIDTH = CSSName.addProperty("border-bottom-width", PRIMITIVE, "medium", NOT_INHERITED, new PrimitivePropertyBuilders.BorderBottomWidth());
        BORDER_LEFT_WIDTH = CSSName.addProperty("border-left-width", PRIMITIVE, "medium", NOT_INHERITED, new PrimitivePropertyBuilders.BorderLeftWidth());
        BORDER_TOP_LEFT_RADIUS = CSSName.addProperty("border-top-left-radius", PRIMITIVE, "0 0", NOT_INHERITED, true, new PrimitivePropertyBuilders.BorderTopLeftRadius());
        BORDER_TOP_RIGHT_RADIUS = CSSName.addProperty("border-top-right-radius", PRIMITIVE, "0 0", NOT_INHERITED, true, new PrimitivePropertyBuilders.BorderTopRightRadius());
        BORDER_BOTTOM_RIGHT_RADIUS = CSSName.addProperty("border-bottom-right-radius", PRIMITIVE, "0 0", NOT_INHERITED, true, new PrimitivePropertyBuilders.BorderBottomRightRadius());
        BORDER_BOTTOM_LEFT_RADIUS = CSSName.addProperty("border-bottom-left-radius", PRIMITIVE, "0 0", NOT_INHERITED, true, new PrimitivePropertyBuilders.BorderBottomLeftRadius());
        MARGIN_TOP = CSSName.addProperty("margin-top", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MarginTop());
        MARGIN_RIGHT = CSSName.addProperty("margin-right", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MarginRight());
        MARGIN_BOTTOM = CSSName.addProperty("margin-bottom", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MarginBottom());
        MARGIN_LEFT = CSSName.addProperty("margin-left", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MarginLeft());
        PADDING_TOP = CSSName.addProperty("padding-top", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.PaddingTop());
        PADDING_RIGHT = CSSName.addProperty("padding-right", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.PaddingRight());
        PADDING_BOTTOM = CSSName.addProperty("padding-bottom", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.PaddingBottom());
        PADDING_LEFT = CSSName.addProperty("padding-left", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.PaddingLeft());
        BACKGROUND_SHORTHAND = CSSName.addProperty("background", SHORTHAND, "transparent none repeat scroll 0% 0%", NOT_INHERITED, new BackgroundPropertyBuilder());
        BORDER_RADIUS_SHORTHAND = CSSName.addProperty("border-radius", SHORTHAND, "0px", NOT_INHERITED, true, new OneToFourPropertyBuilders.BorderRadius());
        BORDER_WIDTH_SHORTHAND = CSSName.addProperty("border-width", SHORTHAND, "medium", NOT_INHERITED, new OneToFourPropertyBuilders.BorderWidth());
        BORDER_STYLE_SHORTHAND = CSSName.addProperty("border-style", SHORTHAND, "none", NOT_INHERITED, new OneToFourPropertyBuilders.BorderStyle());
        BORDER_SHORTHAND = CSSName.addProperty("border", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.Border());
        BORDER_TOP_SHORTHAND = CSSName.addProperty("border-top", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.BorderTop());
        BORDER_RIGHT_SHORTHAND = CSSName.addProperty("border-right", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.BorderRight());
        BORDER_BOTTOM_SHORTHAND = CSSName.addProperty("border-bottom", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.BorderBottom());
        BORDER_LEFT_SHORTHAND = CSSName.addProperty("border-left", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.BorderLeft());
        BORDER_COLOR_SHORTHAND = CSSName.addProperty("border-color", SHORTHAND, "black", NOT_INHERITED, new OneToFourPropertyBuilders.BorderColor());
        BORDER_SPACING = CSSName.addProperty("border-spacing", SHORTHAND, "0", INHERITS, new BorderSpacingPropertyBuilder());
        FONT_SHORTHAND = CSSName.addProperty("font", SHORTHAND, "", INHERITS, new FontPropertyBuilder());
        LIST_STYLE_SHORTHAND = CSSName.addProperty("list-style", SHORTHAND, "disc outside none", INHERITS, new ListStylePropertyBuilder());
        MARGIN_SHORTHAND = CSSName.addProperty("margin", SHORTHAND, "0", NOT_INHERITED, new OneToFourPropertyBuilders.Margin());
        OUTLINE_SHORTHAND = CSSName.addProperty("outline", SHORTHAND, "invert none medium", NOT_INHERITED, false, null);
        PADDING_SHORTHAND = CSSName.addProperty("padding", SHORTHAND, "0", NOT_INHERITED, new OneToFourPropertyBuilders.Padding());
        SIZE_SHORTHAND = CSSName.addProperty("size", SHORTHAND, "auto", NOT_INHERITED, new SizePropertyBuilder());
        BOX_SIZING = CSSName.addProperty("box-sizing", PRIMITIVE, "content-box", NOT_INHERITED, new PrimitivePropertyBuilders.BoxSizing());
        MARGIN_SIDE_PROPERTIES = new CSSSideProperties(MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT);
        PADDING_SIDE_PROPERTIES = new CSSSideProperties(PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT);
        BORDER_SIDE_PROPERTIES = new CSSSideProperties(BORDER_TOP_WIDTH, BORDER_RIGHT_WIDTH, BORDER_BOTTOM_WIDTH, BORDER_LEFT_WIDTH);
        BORDER_STYLE_PROPERTIES = new CSSSideProperties(BORDER_TOP_STYLE, BORDER_RIGHT_STYLE, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE);
        BORDER_COLOR_PROPERTIES = new CSSSideProperties(BORDER_TOP_COLOR, BORDER_RIGHT_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR);
        Iterator iter = ALL_PROPERTY_NAMES.values().iterator();
        ALL_PROPERTIES = new CSSName[ALL_PROPERTY_NAMES.size()];
        while (iter.hasNext()) {
            CSSName name;
            CSSName.ALL_PROPERTIES[name.FS_ID] = name = (CSSName)iter.next();
        }
        CSSParser parser = new CSSParser(new CSSErrorHandler(){

            @Override
            public void error(String uri, String message) {
                XRLog.cssParse("(" + uri + ") " + message);
            }
        });
        for (CSSName cssName : ALL_PRIMITIVE_PROPERTY_NAMES.values()) {
            if (cssName.initialValue.charAt(0) == '=' || !cssName.implemented) continue;
            PropertyValue value = parser.parsePropertyValue(cssName, 0, cssName.initialValue);
            if (value == null) {
                XRLog.exception("Unable to derive initial value for " + cssName);
                continue;
            }
            cssName.initialDerivedValue = DerivedValueFactory.newDerivedValue(null, cssName, value);
        }
    }

    public static class CSSSideProperties {
        public final CSSName top;
        public final CSSName right;
        public final CSSName bottom;
        public final CSSName left;

        public CSSSideProperties(CSSName top, CSSName right, CSSName bottom, CSSName left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }
    }
}

