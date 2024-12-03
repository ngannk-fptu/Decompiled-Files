/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.style.BackgroundPosition;
import org.xhtmlrenderer.css.style.BackgroundSize;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.FontSizeHelper;
import org.xhtmlrenderer.css.style.Length;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.DerivedValueFactory;
import org.xhtmlrenderer.css.style.derived.FunctionValue;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.ListValue;
import org.xhtmlrenderer.css.style.derived.NumberValue;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.XRRuntimeException;

public class CalculatedStyle {
    private CalculatedStyle _parent;
    private BorderPropertySet _border;
    private RectPropertySet _margin;
    private RectPropertySet _padding;
    private float _lineHeight;
    private boolean _lineHeightResolved;
    private FSFont _FSFont;
    private FSFontMetrics _FSFontMetrics;
    private boolean _marginsAllowed = true;
    private boolean _paddingAllowed = true;
    private boolean _bordersAllowed = true;
    private BackgroundSize _backgroundSize;
    private final HashMap _childCache = new HashMap();
    private final FSDerivedValue[] _derivedValuesById = new FSDerivedValue[CSSName.countCSSNames()];
    private FontSpecification _font;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    protected CalculatedStyle() {
    }

    private CalculatedStyle(CalculatedStyle parent, CascadedStyle matched) {
        this();
        this._parent = parent;
        this.derive(matched);
        this.checkPaddingAllowed();
        this.checkMarginsAllowed();
        this.checkBordersAllowed();
    }

    private void checkPaddingAllowed() {
        IdentValue v = this.getIdent(CSSName.DISPLAY);
        if (v == IdentValue.TABLE_HEADER_GROUP || v == IdentValue.TABLE_ROW_GROUP || v == IdentValue.TABLE_FOOTER_GROUP || v == IdentValue.TABLE_ROW) {
            this._paddingAllowed = false;
        } else if ((v == IdentValue.TABLE || v == IdentValue.INLINE_TABLE) && this.isCollapseBorders()) {
            this._paddingAllowed = false;
        }
    }

    private void checkMarginsAllowed() {
        IdentValue v = this.getIdent(CSSName.DISPLAY);
        if (v == IdentValue.TABLE_HEADER_GROUP || v == IdentValue.TABLE_ROW_GROUP || v == IdentValue.TABLE_FOOTER_GROUP || v == IdentValue.TABLE_ROW || v == IdentValue.TABLE_CELL) {
            this._marginsAllowed = false;
        }
    }

    private void checkBordersAllowed() {
        IdentValue v = this.getIdent(CSSName.DISPLAY);
        if (v == IdentValue.TABLE_HEADER_GROUP || v == IdentValue.TABLE_ROW_GROUP || v == IdentValue.TABLE_FOOTER_GROUP || v == IdentValue.TABLE_ROW) {
            this._bordersAllowed = false;
        }
    }

    public synchronized CalculatedStyle deriveStyle(CascadedStyle matched) {
        String fingerprint = matched.getFingerprint();
        CalculatedStyle cs = (CalculatedStyle)this._childCache.get(fingerprint);
        if (cs == null) {
            cs = new CalculatedStyle(this, matched);
            this._childCache.put(fingerprint, cs);
        }
        return cs;
    }

    public int countAssigned() {
        int c = 0;
        for (int i = 0; i < this._derivedValuesById.length; ++i) {
            if (this._derivedValuesById[i] == null) continue;
            ++c;
        }
        return c;
    }

    public CalculatedStyle getParent() {
        return this._parent;
    }

    public String toString() {
        return this.genStyleKey();
    }

    public FSColor asColor(CSSName cssName) {
        FSDerivedValue prop = this.valueByName(cssName);
        if (prop == IdentValue.TRANSPARENT) {
            return FSRGBColor.TRANSPARENT;
        }
        return prop.asColor();
    }

    public float asFloat(CSSName cssName) {
        return this.valueByName(cssName).asFloat();
    }

    public String asString(CSSName cssName) {
        return this.valueByName(cssName).asString();
    }

    public String[] asStringArray(CSSName cssName) {
        return this.valueByName(cssName).asStringArray();
    }

    public void setDefaultValue(CSSName cssName, FSDerivedValue fsDerivedValue) {
        if (this._derivedValuesById[cssName.FS_ID] == null) {
            this._derivedValuesById[cssName.FS_ID] = fsDerivedValue;
        }
    }

    public boolean hasAbsoluteUnit(CSSName cssName) {
        boolean isAbs = false;
        try {
            isAbs = this.valueByName(cssName).hasAbsoluteUnit();
        }
        catch (Exception e) {
            XRLog.layout(Level.WARNING, "Property " + cssName + " has an assignment we don't understand, and can't tell if it's an absolute unit or not. Assuming it is not. Exception was: " + e.getMessage());
            isAbs = false;
        }
        return isAbs;
    }

    public boolean isIdent(CSSName cssName, IdentValue val) {
        return this.valueByName(cssName) == val;
    }

    public IdentValue getIdent(CSSName cssName) {
        return this.valueByName(cssName).asIdentValue();
    }

    public FSColor getColor() {
        return this.asColor(CSSName.COLOR);
    }

    public FSColor getBackgroundColor() {
        FSDerivedValue prop = this.valueByName(CSSName.BACKGROUND_COLOR);
        if (prop == IdentValue.TRANSPARENT) {
            return null;
        }
        return this.asColor(CSSName.BACKGROUND_COLOR);
    }

    public BackgroundSize getBackgroundSize() {
        if (this._backgroundSize == null) {
            this._backgroundSize = this.createBackgroundSize();
        }
        return this._backgroundSize;
    }

    private BackgroundSize createBackgroundSize() {
        FSDerivedValue value = this.valueByName(CSSName.BACKGROUND_SIZE);
        if (value instanceof IdentValue) {
            IdentValue ident = (IdentValue)value;
            if (ident == IdentValue.COVER) {
                return new BackgroundSize(false, true, false);
            }
            if (ident == IdentValue.CONTAIN) {
                return new BackgroundSize(true, false, false);
            }
        } else {
            boolean secondAuto;
            ListValue valueList = (ListValue)value;
            List values = valueList.getValues();
            boolean firstAuto = ((PropertyValue)values.get(0)).getIdentValue() == IdentValue.AUTO;
            boolean bl = secondAuto = ((PropertyValue)values.get(1)).getIdentValue() == IdentValue.AUTO;
            if (firstAuto && secondAuto) {
                return new BackgroundSize(false, false, true);
            }
            return new BackgroundSize((PropertyValue)values.get(0), (PropertyValue)values.get(1));
        }
        throw new RuntimeException("internal error");
    }

    public BackgroundPosition getBackgroundPosition() {
        ListValue result = (ListValue)this.valueByName(CSSName.BACKGROUND_POSITION);
        List values = result.getValues();
        return new BackgroundPosition((PropertyValue)values.get(0), (PropertyValue)values.get(1));
    }

    public List getCounterReset() {
        FSDerivedValue value = this.valueByName(CSSName.COUNTER_RESET);
        if (value == IdentValue.NONE) {
            return null;
        }
        return ((ListValue)value).getValues();
    }

    public List getCounterIncrement() {
        FSDerivedValue value = this.valueByName(CSSName.COUNTER_INCREMENT);
        if (value == IdentValue.NONE) {
            return null;
        }
        return ((ListValue)value).getValues();
    }

    public BorderPropertySet getBorder(CssContext ctx) {
        if (!this._bordersAllowed) {
            return BorderPropertySet.EMPTY_BORDER;
        }
        BorderPropertySet b = CalculatedStyle.getBorderProperty(this, ctx);
        return b;
    }

    public FontSpecification getFont(CssContext ctx) {
        if (this._font == null) {
            this._font = new FontSpecification();
            this._font.families = this.valueByName(CSSName.FONT_FAMILY).asStringArray();
            FSDerivedValue fontSize = this.valueByName(CSSName.FONT_SIZE);
            if (fontSize instanceof IdentValue) {
                IdentValue resolved = this.resolveAbsoluteFontSize();
                PropertyValue replacement = resolved != null ? FontSizeHelper.resolveAbsoluteFontSize(resolved, this._font.families) : FontSizeHelper.getDefaultRelativeFontSize((IdentValue)fontSize);
                this._font.size = LengthValue.calcFloatProportionalValue(this, CSSName.FONT_SIZE, replacement.getCssText(), replacement.getFloatValue(), replacement.getPrimitiveType(), 0.0f, ctx);
            } else {
                this._font.size = this.getFloatPropertyProportionalTo(CSSName.FONT_SIZE, 0.0f, ctx);
            }
            this._font.fontWeight = this.getIdent(CSSName.FONT_WEIGHT);
            this._font.fontStyle = this.getIdent(CSSName.FONT_STYLE);
            this._font.variant = this.getIdent(CSSName.FONT_VARIANT);
        }
        return this._font;
    }

    public FontSpecification getFontSpecification() {
        return this._font;
    }

    private IdentValue resolveAbsoluteFontSize() {
        FSDerivedValue fontSize = this.valueByName(CSSName.FONT_SIZE);
        if (!(fontSize instanceof IdentValue)) {
            return null;
        }
        IdentValue fontSizeIdent = (IdentValue)fontSize;
        if (PrimitivePropertyBuilders.ABSOLUTE_FONT_SIZES.get(fontSizeIdent.FS_ID)) {
            return fontSizeIdent;
        }
        IdentValue parent = this.getParent().resolveAbsoluteFontSize();
        if (parent != null) {
            if (fontSizeIdent == IdentValue.SMALLER) {
                return FontSizeHelper.getNextSmaller(parent);
            }
            if (fontSize == IdentValue.LARGER) {
                return FontSizeHelper.getNextLarger(parent);
            }
        }
        return null;
    }

    public float getFloatPropertyProportionalTo(CSSName cssName, float baseValue, CssContext ctx) {
        return this.valueByName(cssName).getFloatProportionalTo(cssName, baseValue, ctx);
    }

    public float getFloatPropertyProportionalWidth(CSSName cssName, float parentWidth, CssContext ctx) {
        return this.valueByName(cssName).getFloatProportionalTo(cssName, parentWidth, ctx);
    }

    public float getFloatPropertyProportionalHeight(CSSName cssName, float parentHeight, CssContext ctx) {
        return this.valueByName(cssName).getFloatProportionalTo(cssName, parentHeight, ctx);
    }

    public float getLineHeight(CssContext ctx) {
        if (!this._lineHeightResolved) {
            if (this.isIdent(CSSName.LINE_HEIGHT, IdentValue.NORMAL)) {
                float lineHeight1 = this.getFont((CssContext)ctx).size * 1.1f;
                FSFontMetrics metrics = this.getFSFontMetrics(ctx);
                float lineHeight2 = (float)Math.ceil(metrics.getDescent() + metrics.getAscent());
                this._lineHeight = Math.max(lineHeight1, lineHeight2);
            } else {
                this._lineHeight = this.isLength(CSSName.LINE_HEIGHT) ? this.getFloatPropertyProportionalHeight(CSSName.LINE_HEIGHT, 0.0f, ctx) : this.getFont((CssContext)ctx).size * this.valueByName(CSSName.LINE_HEIGHT).asFloat();
            }
            this._lineHeightResolved = true;
        }
        return this._lineHeight;
    }

    public RectPropertySet getMarginRect(float cbWidth, CssContext ctx) {
        return this.getMarginRect(cbWidth, ctx, true);
    }

    public RectPropertySet getMarginRect(float cbWidth, CssContext ctx, boolean useCache) {
        if (!this._marginsAllowed) {
            return RectPropertySet.ALL_ZEROS;
        }
        return CalculatedStyle.getMarginProperty(this, CSSName.MARGIN_SHORTHAND, CSSName.MARGIN_SIDE_PROPERTIES, cbWidth, ctx, useCache);
    }

    public RectPropertySet getPaddingRect(float cbWidth, CssContext ctx, boolean useCache) {
        if (!this._paddingAllowed) {
            return RectPropertySet.ALL_ZEROS;
        }
        return CalculatedStyle.getPaddingProperty(this, CSSName.PADDING_SHORTHAND, CSSName.PADDING_SIDE_PROPERTIES, cbWidth, ctx, useCache);
    }

    public RectPropertySet getPaddingRect(float cbWidth, CssContext ctx) {
        return this.getPaddingRect(cbWidth, ctx, true);
    }

    public String getStringProperty(CSSName cssName) {
        return this.valueByName(cssName).asString();
    }

    public boolean isLength(CSSName cssName) {
        FSDerivedValue val = this.valueByName(cssName);
        return val instanceof LengthValue;
    }

    public boolean isLengthOrNumber(CSSName cssName) {
        FSDerivedValue val = this.valueByName(cssName);
        return val instanceof NumberValue || val instanceof LengthValue;
    }

    public FSDerivedValue valueByName(CSSName cssName) {
        boolean needInitialValue;
        FSDerivedValue val = this._derivedValuesById[cssName.FS_ID];
        boolean bl = needInitialValue = val == IdentValue.FS_INITIAL_VALUE;
        if (val == null || needInitialValue) {
            if (needInitialValue || !CSSName.propertyInherits(cssName) || this._parent == null || (val = this._parent.valueByName(cssName)) == null) {
                String initialValue = CSSName.initialValue(cssName);
                if (initialValue == null) {
                    throw new XRRuntimeException("Property '" + cssName + "' has no initial values assigned. Check CSSName declarations.");
                }
                if (initialValue.charAt(0) == '=') {
                    CSSName ref = CSSName.getByPropertyName(initialValue.substring(1));
                    val = this.valueByName(ref);
                } else {
                    val = CSSName.initialDerivedValue(cssName);
                }
            }
            this._derivedValuesById[cssName.FS_ID] = val;
        }
        return val;
    }

    private void derive(CascadedStyle matched) {
        if (matched == null) {
            return;
        }
        Iterator mProps = matched.getCascadedPropertyDeclarations();
        while (mProps.hasNext()) {
            FSDerivedValue val;
            PropertyDeclaration pd = (PropertyDeclaration)mProps.next();
            this._derivedValuesById[pd.getCSSName().FS_ID] = val = this.deriveValue(pd.getCSSName(), pd.getValue());
        }
    }

    private FSDerivedValue deriveValue(CSSName cssName, CSSPrimitiveValue value) {
        return DerivedValueFactory.newDerivedValue(this, cssName, (PropertyValue)value);
    }

    private String genStyleKey() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this._derivedValuesById.length; ++i) {
            CSSName name = CSSName.getByID(i);
            FSDerivedValue val = this._derivedValuesById[i];
            if (val != null) {
                sb.append(name.toString());
            } else {
                sb.append("(no prop assigned in this pos)");
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    public RectPropertySet getCachedPadding() {
        if (this._padding == null) {
            throw new XRRuntimeException("No padding property cached yet; should have called getPropertyRect() at least once before.");
        }
        return this._padding;
    }

    public RectPropertySet getCachedMargin() {
        if (this._margin == null) {
            throw new XRRuntimeException("No margin property cached yet; should have called getMarginRect() at least once before.");
        }
        return this._margin;
    }

    private static RectPropertySet getPaddingProperty(CalculatedStyle style, CSSName shorthandProp, CSSName.CSSSideProperties sides, float cbWidth, CssContext ctx, boolean useCache) {
        if (!useCache) {
            return CalculatedStyle.newRectInstance(style, shorthandProp, sides, cbWidth, ctx);
        }
        if (style._padding == null) {
            RectPropertySet result = CalculatedStyle.newRectInstance(style, shorthandProp, sides, cbWidth, ctx);
            boolean allZeros = result.isAllZeros();
            if (allZeros) {
                result = RectPropertySet.ALL_ZEROS;
            }
            style._padding = result;
            if (!allZeros && style._padding.hasNegativeValues()) {
                style._padding.resetNegativeValues();
            }
        }
        return style._padding;
    }

    private static RectPropertySet getMarginProperty(CalculatedStyle style, CSSName shorthandProp, CSSName.CSSSideProperties sides, float cbWidth, CssContext ctx, boolean useCache) {
        if (!useCache) {
            return CalculatedStyle.newRectInstance(style, shorthandProp, sides, cbWidth, ctx);
        }
        if (style._margin == null) {
            RectPropertySet result = CalculatedStyle.newRectInstance(style, shorthandProp, sides, cbWidth, ctx);
            if (result.isAllZeros()) {
                result = RectPropertySet.ALL_ZEROS;
            }
            style._margin = result;
        }
        return style._margin;
    }

    private static RectPropertySet newRectInstance(CalculatedStyle style, CSSName shorthand, CSSName.CSSSideProperties sides, float cbWidth, CssContext ctx) {
        RectPropertySet rect = RectPropertySet.newInstance(style, shorthand, sides, cbWidth, ctx);
        return rect;
    }

    private static BorderPropertySet getBorderProperty(CalculatedStyle style, CssContext ctx) {
        if (style._border == null) {
            BorderPropertySet result = BorderPropertySet.newInstance(style, ctx);
            boolean allZeros = result.isAllZeros();
            if (allZeros && !result.hasHidden() && !result.hasBorderRadius()) {
                result = BorderPropertySet.EMPTY_BORDER;
            }
            style._border = result;
            if (!allZeros && style._border.hasNegativeValues()) {
                style._border.resetNegativeValues();
            }
        }
        return style._border;
    }

    public int getMarginBorderPadding(CssContext cssCtx, int cbWidth, int which) {
        BorderPropertySet border = this.getBorder(cssCtx);
        RectPropertySet margin = this.getMarginRect(cbWidth, cssCtx);
        RectPropertySet padding = this.getPaddingRect(cbWidth, cssCtx);
        switch (which) {
            case 1: {
                return (int)(margin.left() + border.left() + padding.left());
            }
            case 2: {
                return (int)(margin.right() + border.right() + padding.right());
            }
            case 3: {
                return (int)(margin.top() + border.top() + padding.top());
            }
            case 4: {
                return (int)(margin.bottom() + border.bottom() + padding.bottom());
            }
        }
        throw new IllegalArgumentException();
    }

    public IdentValue getWhitespace() {
        return this.getIdent(CSSName.WHITE_SPACE);
    }

    public FSFont getFSFont(CssContext cssContext) {
        if (this._FSFont == null) {
            this._FSFont = cssContext.getFont(this.getFont(cssContext));
        }
        return this._FSFont;
    }

    public FSFontMetrics getFSFontMetrics(CssContext c) {
        if (this._FSFontMetrics == null) {
            this._FSFontMetrics = c.getFSFontMetrics(this.getFSFont(c));
        }
        return this._FSFontMetrics;
    }

    public IdentValue getWordWrap() {
        return this.getIdent(CSSName.WORD_WRAP);
    }

    public IdentValue getHyphens() {
        return this.getIdent(CSSName.HYPHENS);
    }

    public boolean isClearLeft() {
        IdentValue clear = this.getIdent(CSSName.CLEAR);
        return clear == IdentValue.LEFT || clear == IdentValue.BOTH;
    }

    public boolean isClearRight() {
        IdentValue clear = this.getIdent(CSSName.CLEAR);
        return clear == IdentValue.RIGHT || clear == IdentValue.BOTH;
    }

    public boolean isCleared() {
        return !this.isIdent(CSSName.CLEAR, IdentValue.NONE);
    }

    public IdentValue getBackgroundRepeat() {
        return this.getIdent(CSSName.BACKGROUND_REPEAT);
    }

    public IdentValue getBackgroundAttachment() {
        return this.getIdent(CSSName.BACKGROUND_ATTACHMENT);
    }

    public boolean isFixedBackground() {
        return this.getIdent(CSSName.BACKGROUND_ATTACHMENT) == IdentValue.FIXED;
    }

    public boolean isInline() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.INLINE) && !this.isFloated() && !this.isAbsolute() && !this.isFixed() && !this.isRunning();
    }

    public boolean isInlineBlock() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.INLINE_BLOCK);
    }

    public boolean isTable() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.TABLE);
    }

    public boolean isInlineTable() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.INLINE_TABLE);
    }

    public boolean isTableCell() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.TABLE_CELL);
    }

    public boolean isTableSection() {
        IdentValue display = this.getIdent(CSSName.DISPLAY);
        return display == IdentValue.TABLE_ROW_GROUP || display == IdentValue.TABLE_HEADER_GROUP || display == IdentValue.TABLE_FOOTER_GROUP;
    }

    public boolean isTableCaption() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.TABLE_CAPTION);
    }

    public boolean isTableHeader() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.TABLE_HEADER_GROUP);
    }

    public boolean isTableFooter() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.TABLE_FOOTER_GROUP);
    }

    public boolean isTableRow() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.TABLE_ROW);
    }

    public boolean isDisplayNone() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.NONE);
    }

    public boolean isSpecifiedAsBlock() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.BLOCK);
    }

    public boolean isBlockEquivalent() {
        if (this.isFloated() || this.isAbsolute() || this.isFixed()) {
            return true;
        }
        IdentValue display = this.getIdent(CSSName.DISPLAY);
        if (display == IdentValue.INLINE) {
            return false;
        }
        return display == IdentValue.BLOCK || display == IdentValue.LIST_ITEM || display == IdentValue.RUN_IN || display == IdentValue.INLINE_BLOCK || display == IdentValue.TABLE || display == IdentValue.INLINE_TABLE;
    }

    public boolean isLayedOutInInlineContext() {
        if (this.isFloated() || this.isAbsolute() || this.isFixed() || this.isRunning()) {
            return true;
        }
        IdentValue display = this.getIdent(CSSName.DISPLAY);
        return display == IdentValue.INLINE || display == IdentValue.INLINE_BLOCK || display == IdentValue.INLINE_TABLE;
    }

    public boolean isNeedAutoMarginResolution() {
        return !this.isAbsolute() && !this.isFixed() && !this.isFloated() && !this.isInlineBlock();
    }

    public boolean isAbsolute() {
        return this.isIdent(CSSName.POSITION, IdentValue.ABSOLUTE);
    }

    public boolean isFixed() {
        return this.isIdent(CSSName.POSITION, IdentValue.FIXED);
    }

    public boolean isFloated() {
        IdentValue floatVal = this.getIdent(CSSName.FLOAT);
        return floatVal == IdentValue.LEFT || floatVal == IdentValue.RIGHT;
    }

    public boolean isFloatedLeft() {
        return this.isIdent(CSSName.FLOAT, IdentValue.LEFT);
    }

    public boolean isFloatedRight() {
        return this.isIdent(CSSName.FLOAT, IdentValue.RIGHT);
    }

    public boolean isRelative() {
        return this.isIdent(CSSName.POSITION, IdentValue.RELATIVE);
    }

    public boolean isPostionedOrFloated() {
        return this.isAbsolute() || this.isFixed() || this.isFloated() || this.isRelative();
    }

    public boolean isPositioned() {
        return this.isAbsolute() || this.isFixed() || this.isRelative();
    }

    public boolean isAutoWidth() {
        return this.isIdent(CSSName.WIDTH, IdentValue.AUTO);
    }

    public boolean isAbsoluteWidth() {
        return this.valueByName(CSSName.WIDTH).hasAbsoluteUnit();
    }

    public boolean isAutoHeight() {
        return this.isIdent(CSSName.HEIGHT, IdentValue.AUTO);
    }

    public boolean isAutoLeftMargin() {
        return this.isIdent(CSSName.MARGIN_LEFT, IdentValue.AUTO);
    }

    public boolean isAutoRightMargin() {
        return this.isIdent(CSSName.MARGIN_RIGHT, IdentValue.AUTO);
    }

    public boolean isAutoZIndex() {
        return this.isIdent(CSSName.Z_INDEX, IdentValue.AUTO);
    }

    public boolean establishesBFC() {
        FSDerivedValue value = this.valueByName(CSSName.POSITION);
        if (value instanceof FunctionValue) {
            return false;
        }
        IdentValue display = this.getIdent(CSSName.DISPLAY);
        IdentValue position = (IdentValue)value;
        return this.isFloated() || position == IdentValue.ABSOLUTE || position == IdentValue.FIXED || display == IdentValue.INLINE_BLOCK || display == IdentValue.TABLE_CELL || !this.isIdent(CSSName.OVERFLOW, IdentValue.VISIBLE);
    }

    public boolean requiresLayer() {
        FSDerivedValue value = this.valueByName(CSSName.POSITION);
        if (value instanceof FunctionValue) {
            return false;
        }
        IdentValue position = this.getIdent(CSSName.POSITION);
        if (position == IdentValue.ABSOLUTE || position == IdentValue.RELATIVE || position == IdentValue.FIXED) {
            return true;
        }
        IdentValue overflow = this.getIdent(CSSName.OVERFLOW);
        return (overflow == IdentValue.SCROLL || overflow == IdentValue.AUTO) && this.isOverflowApplies();
    }

    public boolean isRunning() {
        FSDerivedValue value = this.valueByName(CSSName.POSITION);
        return value instanceof FunctionValue;
    }

    public String getRunningName() {
        FunctionValue value = (FunctionValue)this.valueByName(CSSName.POSITION);
        FSFunction function = value.getFunction();
        PropertyValue param = (PropertyValue)function.getParameters().get(0);
        return param.getStringValue();
    }

    public boolean isOverflowApplies() {
        IdentValue display = this.getIdent(CSSName.DISPLAY);
        return display == IdentValue.BLOCK || display == IdentValue.LIST_ITEM || display == IdentValue.TABLE || display == IdentValue.INLINE_BLOCK || display == IdentValue.TABLE_CELL;
    }

    public boolean isOverflowVisible() {
        return this.valueByName(CSSName.OVERFLOW) == IdentValue.VISIBLE;
    }

    public boolean isHorizontalBackgroundRepeat() {
        IdentValue value = this.getIdent(CSSName.BACKGROUND_REPEAT);
        return value == IdentValue.REPEAT_X || value == IdentValue.REPEAT;
    }

    public boolean isVerticalBackgroundRepeat() {
        IdentValue value = this.getIdent(CSSName.BACKGROUND_REPEAT);
        return value == IdentValue.REPEAT_Y || value == IdentValue.REPEAT;
    }

    public boolean isTopAuto() {
        return this.isIdent(CSSName.TOP, IdentValue.AUTO);
    }

    public boolean isBottomAuto() {
        return this.isIdent(CSSName.BOTTOM, IdentValue.AUTO);
    }

    public boolean isListItem() {
        return this.isIdent(CSSName.DISPLAY, IdentValue.LIST_ITEM);
    }

    public boolean isVisible() {
        return this.isIdent(CSSName.VISIBILITY, IdentValue.VISIBLE);
    }

    public boolean isForcePageBreakBefore() {
        IdentValue val = this.getIdent(CSSName.PAGE_BREAK_BEFORE);
        return val == IdentValue.ALWAYS || val == IdentValue.LEFT || val == IdentValue.RIGHT;
    }

    public boolean isForcePageBreakAfter() {
        IdentValue val = this.getIdent(CSSName.PAGE_BREAK_AFTER);
        return val == IdentValue.ALWAYS || val == IdentValue.LEFT || val == IdentValue.RIGHT;
    }

    public boolean isAvoidPageBreakInside() {
        return this.isIdent(CSSName.PAGE_BREAK_INSIDE, IdentValue.AVOID);
    }

    public CalculatedStyle createAnonymousStyle(IdentValue display) {
        return this.deriveStyle(CascadedStyle.createAnonymousStyle(display));
    }

    public boolean mayHaveFirstLine() {
        IdentValue display = this.getIdent(CSSName.DISPLAY);
        return display == IdentValue.BLOCK || display == IdentValue.LIST_ITEM || display == IdentValue.RUN_IN || display == IdentValue.TABLE || display == IdentValue.TABLE_CELL || display == IdentValue.TABLE_CAPTION || display == IdentValue.INLINE_BLOCK;
    }

    public boolean mayHaveFirstLetter() {
        IdentValue display = this.getIdent(CSSName.DISPLAY);
        return display == IdentValue.BLOCK || display == IdentValue.LIST_ITEM || display == IdentValue.TABLE_CELL || display == IdentValue.TABLE_CAPTION || display == IdentValue.INLINE_BLOCK;
    }

    public boolean isNonFlowContent() {
        return this.isFloated() || this.isAbsolute() || this.isFixed() || this.isRunning();
    }

    public boolean isMayCollapseMarginsWithChildren() {
        return this.isIdent(CSSName.OVERFLOW, IdentValue.VISIBLE) && !this.isFloated() && !this.isAbsolute() && !this.isFixed() && !this.isInlineBlock();
    }

    public boolean isAbsFixedOrInlineBlockEquiv() {
        return this.isAbsolute() || this.isFixed() || this.isInlineBlock() || this.isInlineTable();
    }

    public boolean isMaxWidthNone() {
        return this.isIdent(CSSName.MAX_WIDTH, IdentValue.NONE);
    }

    public boolean isMaxHeightNone() {
        return this.isIdent(CSSName.MAX_HEIGHT, IdentValue.NONE);
    }

    public boolean isBorderBox() {
        return this.isIdent(CSSName.BOX_SIZING, IdentValue.BORDER_BOX);
    }

    public int getMinWidth(CssContext c, int cbWidth) {
        return (int)this.getFloatPropertyProportionalTo(CSSName.MIN_WIDTH, cbWidth, c);
    }

    public int getMaxWidth(CssContext c, int cbWidth) {
        return (int)this.getFloatPropertyProportionalTo(CSSName.MAX_WIDTH, cbWidth, c);
    }

    public int getMinHeight(CssContext c, int cbHeight) {
        return (int)this.getFloatPropertyProportionalTo(CSSName.MIN_HEIGHT, cbHeight, c);
    }

    public int getMaxHeight(CssContext c, int cbHeight) {
        return (int)this.getFloatPropertyProportionalTo(CSSName.MAX_HEIGHT, cbHeight, c);
    }

    public boolean isCollapseBorders() {
        return this.isIdent(CSSName.BORDER_COLLAPSE, IdentValue.COLLAPSE);
    }

    public int getBorderHSpacing(CssContext c) {
        return this.isCollapseBorders() ? 0 : (int)this.getFloatPropertyProportionalTo(CSSName.FS_BORDER_SPACING_HORIZONTAL, 0.0f, c);
    }

    public int getBorderVSpacing(CssContext c) {
        return this.isCollapseBorders() ? 0 : (int)this.getFloatPropertyProportionalTo(CSSName.FS_BORDER_SPACING_VERTICAL, 0.0f, c);
    }

    public int getRowSpan() {
        int result = (int)this.asFloat(CSSName.FS_ROWSPAN);
        return result > 0 ? result : 1;
    }

    public int getColSpan() {
        int result = (int)this.asFloat(CSSName.FS_COLSPAN);
        return result > 0 ? result : 1;
    }

    public Length asLength(CssContext c, CSSName cssName) {
        Length result = new Length();
        FSDerivedValue value = this.valueByName(cssName);
        if (value instanceof LengthValue || value instanceof NumberValue) {
            if (value.hasAbsoluteUnit()) {
                result.setValue((int)value.getFloatProportionalTo(cssName, 0.0f, c));
                result.setType(2);
            } else {
                result.setValue((int)value.asFloat());
                result.setType(3);
            }
        }
        return result;
    }

    public boolean isShowEmptyCells() {
        return this.isCollapseBorders() || this.isIdent(CSSName.EMPTY_CELLS, IdentValue.SHOW);
    }

    public boolean isHasBackground() {
        return !this.isIdent(CSSName.BACKGROUND_COLOR, IdentValue.TRANSPARENT) || !this.isIdent(CSSName.BACKGROUND_IMAGE, IdentValue.NONE);
    }

    public List getTextDecorations() {
        FSDerivedValue value = this.valueByName(CSSName.TEXT_DECORATION);
        if (value == IdentValue.NONE) {
            return null;
        }
        List idents = ((ListValue)value).getValues();
        ArrayList<FSDerivedValue> result = new ArrayList<FSDerivedValue>(idents.size());
        Iterator i = idents.iterator();
        while (i.hasNext()) {
            result.add(DerivedValueFactory.newDerivedValue(this, CSSName.TEXT_DECORATION, (PropertyValue)i.next()));
        }
        return result;
    }

    public Cursor getCursor() {
        FSDerivedValue value = this.valueByName(CSSName.CURSOR);
        if (value == IdentValue.AUTO || value == IdentValue.DEFAULT) {
            return Cursor.getDefaultCursor();
        }
        if (value == IdentValue.CROSSHAIR) {
            return Cursor.getPredefinedCursor(1);
        }
        if (value == IdentValue.POINTER) {
            return Cursor.getPredefinedCursor(12);
        }
        if (value == IdentValue.MOVE) {
            return Cursor.getPredefinedCursor(13);
        }
        if (value == IdentValue.E_RESIZE) {
            return Cursor.getPredefinedCursor(11);
        }
        if (value == IdentValue.NE_RESIZE) {
            return Cursor.getPredefinedCursor(7);
        }
        if (value == IdentValue.NW_RESIZE) {
            return Cursor.getPredefinedCursor(6);
        }
        if (value == IdentValue.N_RESIZE) {
            return Cursor.getPredefinedCursor(8);
        }
        if (value == IdentValue.SE_RESIZE) {
            return Cursor.getPredefinedCursor(5);
        }
        if (value == IdentValue.SW_RESIZE) {
            return Cursor.getPredefinedCursor(4);
        }
        if (value == IdentValue.S_RESIZE) {
            return Cursor.getPredefinedCursor(9);
        }
        if (value == IdentValue.W_RESIZE) {
            return Cursor.getPredefinedCursor(10);
        }
        if (value == IdentValue.TEXT) {
            return Cursor.getPredefinedCursor(2);
        }
        if (value == IdentValue.WAIT) {
            return Cursor.getPredefinedCursor(3);
        }
        if (value == IdentValue.HELP) {
            return Cursor.getPredefinedCursor(0);
        }
        if (value == IdentValue.PROGRESS) {
            return Cursor.getPredefinedCursor(3);
        }
        return null;
    }

    public boolean isPaginateTable() {
        return this.isIdent(CSSName.FS_TABLE_PAGINATE, IdentValue.PAGINATE);
    }

    public boolean isTextJustify() {
        return this.isIdent(CSSName.TEXT_ALIGN, IdentValue.JUSTIFY) && !this.isIdent(CSSName.WHITE_SPACE, IdentValue.PRE) && !this.isIdent(CSSName.WHITE_SPACE, IdentValue.PRE_LINE);
    }

    public boolean isListMarkerInside() {
        return this.isIdent(CSSName.LIST_STYLE_POSITION, IdentValue.INSIDE);
    }

    public boolean isKeepWithInline() {
        return this.isIdent(CSSName.FS_KEEP_WITH_INLINE, IdentValue.KEEP);
    }

    public boolean isDynamicAutoWidth() {
        return this.isIdent(CSSName.FS_DYNAMIC_AUTO_WIDTH, IdentValue.DYNAMIC);
    }

    public boolean isDynamicAutoWidthApplicable() {
        return this.isDynamicAutoWidth() && this.isAutoWidth() && !this.isCanBeShrunkToFit();
    }

    public boolean isCanBeShrunkToFit() {
        return this.isInlineBlock() || this.isFloated() || this.isAbsolute() || this.isFixed();
    }
}

