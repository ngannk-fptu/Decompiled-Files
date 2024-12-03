/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.Token;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.Conversions;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class PrimitivePropertyBuilders {
    public static final BitSet BORDER_STYLES = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.NONE, IdentValue.HIDDEN, IdentValue.DOTTED, IdentValue.DASHED, IdentValue.SOLID, IdentValue.DOUBLE, IdentValue.GROOVE, IdentValue.RIDGE, IdentValue.INSET, IdentValue.OUTSET});
    public static final BitSet BORDER_WIDTHS = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.THIN, IdentValue.MEDIUM, IdentValue.THICK});
    public static final BitSet FONT_VARIANTS = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.NORMAL, IdentValue.SMALL_CAPS});
    public static final BitSet FONT_STYLES = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.NORMAL, IdentValue.ITALIC, IdentValue.OBLIQUE});
    public static final BitSet FONT_WEIGHTS = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.NORMAL, IdentValue.BOLD, IdentValue.BOLDER, IdentValue.LIGHTER});
    public static final BitSet PAGE_ORIENTATIONS = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.AUTO, IdentValue.PORTRAIT, IdentValue.LANDSCAPE});
    public static final BitSet LIST_STYLE_POSITIONS = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.INSIDE, IdentValue.OUTSIDE});
    public static final BitSet LIST_STYLE_TYPES = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.DISC, IdentValue.CIRCLE, IdentValue.SQUARE, IdentValue.DECIMAL, IdentValue.DECIMAL_LEADING_ZERO, IdentValue.LOWER_ROMAN, IdentValue.UPPER_ROMAN, IdentValue.LOWER_GREEK, IdentValue.LOWER_LATIN, IdentValue.UPPER_LATIN, IdentValue.ARMENIAN, IdentValue.GEORGIAN, IdentValue.LOWER_ALPHA, IdentValue.UPPER_ALPHA, IdentValue.NONE});
    public static final BitSet BACKGROUND_REPEATS = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.REPEAT, IdentValue.REPEAT_X, IdentValue.REPEAT_Y, IdentValue.NO_REPEAT});
    public static final BitSet BACKGROUND_ATTACHMENTS = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.SCROLL, IdentValue.FIXED});
    public static final BitSet BACKGROUND_POSITIONS = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.LEFT, IdentValue.RIGHT, IdentValue.TOP, IdentValue.BOTTOM, IdentValue.CENTER});
    public static final BitSet ABSOLUTE_FONT_SIZES = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.XX_SMALL, IdentValue.X_SMALL, IdentValue.SMALL, IdentValue.MEDIUM, IdentValue.LARGE, IdentValue.X_LARGE, IdentValue.XX_LARGE});
    public static final BitSet RELATIVE_FONT_SIZES = PrimitivePropertyBuilders.setFor(new IdentValue[]{IdentValue.SMALLER, IdentValue.LARGER});
    public static final PropertyBuilder COLOR = new GenericColor();
    public static final PropertyBuilder BORDER_STYLE = new GenericBorderStyle();
    public static final PropertyBuilder BORDER_WIDTH = new GenericBorderWidth();
    public static final PropertyBuilder BORDER_RADIUS = new NonNegativeLengthLike();
    public static final PropertyBuilder MARGIN = new LengthLikeWithAuto();
    public static final PropertyBuilder PADDING = new NonNegativeLengthLike();

    private static BitSet setFor(IdentValue[] values) {
        BitSet result = new BitSet(IdentValue.getIdentCount());
        for (int i = 0; i < values.length; ++i) {
            IdentValue ident = values[i];
            result.set(ident.FS_ID);
        }
        return result;
    }

    private static List createTwoValueResponse(CSSName cssName, CSSPrimitiveValue value1, CSSPrimitiveValue value2, int origin, boolean important) {
        ArrayList<CSSPrimitiveValue> values = new ArrayList<CSSPrimitiveValue>(2);
        values.add(value1);
        values.add(value2);
        PropertyDeclaration result = new PropertyDeclaration(cssName, new PropertyValue(values), important, origin);
        return Collections.singletonList(result);
    }

    static /* synthetic */ BitSet access$500(IdentValue[] x0) {
        return PrimitivePropertyBuilders.setFor(x0);
    }

    public static class ZIndex
    extends AbstractPropertyBuilder {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO});

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentOrIntegerType(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, ALLOWED, ident);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }

    public static class WordSpacing
    extends LengthWithNormal {
    }

    public static class Width
    extends LengthLikeWithAuto {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class Widows
    extends PlainInteger {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class BoxSizing
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.BORDER_BOX, IdentValue.CONTENT_BOX});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Hyphens
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NONE, IdentValue.MANUAL, IdentValue.AUTO});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class WordWrap
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NORMAL, IdentValue.BREAK_WORD});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class WhiteSpace
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NORMAL, IdentValue.PRE, IdentValue.NOWRAP, IdentValue.PRE_WRAP, IdentValue.PRE_LINE});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Visibility
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.VISIBLE, IdentValue.HIDDEN, IdentValue.COLLAPSE});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class VerticalAlign
    extends LengthLikeWithIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.BASELINE, IdentValue.SUB, IdentValue.SUPER, IdentValue.TOP, IdentValue.TEXT_TOP, IdentValue.MIDDLE, IdentValue.BOTTOM, IdentValue.TEXT_BOTTOM});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class TextTransform
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.CAPITALIZE, IdentValue.UPPERCASE, IdentValue.LOWERCASE, IdentValue.NONE});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class TextIndent
    extends LengthLike {
    }

    public static class TextDecoration
    extends AbstractPropertyBuilder {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.UNDERLINE, IdentValue.OVERLINE, IdentValue.LINE_THROUGH});

        private BitSet getAllowed() {
            return ALLOWED;
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            IdentValue ident;
            if (values.size() == 1) {
                CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
                boolean goWithSingle = false;
                if (value.getCssValueType() == 0) {
                    goWithSingle = true;
                } else {
                    this.checkIdentType(CSSName.TEXT_DECORATION, value);
                    ident = this.checkIdent(cssName, value);
                    if (ident == IdentValue.NONE) {
                        goWithSingle = true;
                    }
                }
                if (goWithSingle) {
                    return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
                }
            }
            for (PropertyValue value : values) {
                this.checkInheritAllowed(value, false);
                this.checkIdentType(cssName, value);
                ident = this.checkIdent(cssName, value);
                if (ident == IdentValue.NONE) {
                    throw new CSSParseException("Value none may not be used in this position", -1);
                }
                this.checkValidity(cssName, this.getAllowed(), ident);
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, new PropertyValue(values), important, origin));
        }
    }

    public static class TextAlign
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.LEFT, IdentValue.RIGHT, IdentValue.CENTER, IdentValue.JUSTIFY});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class TableLayout
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO, IdentValue.FIXED});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Top
    extends LengthLikeWithAuto {
    }

    public static class TabSize
    extends PlainInteger {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class Src
    extends GenericURIWithNone {
    }

    public static class Right
    extends LengthLikeWithAuto {
    }

    public static class Position
    extends AbstractPropertyBuilder {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.STATIC, IdentValue.RELATIVE, IdentValue.ABSOLUTE, IdentValue.FIXED});

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() == 0) return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
            if (value.getPrimitiveType() == 21) {
                this.checkIdentType(cssName, value);
                IdentValue ident = this.checkIdent(cssName, value);
                this.checkValidity(cssName, this.getAllowed(), ident);
                return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
            } else {
                if (value.getPropertyValueType() != 7) throw new CSSParseException("Value for " + cssName + " must be an identifier or function", -1);
                FSFunction function = value.getFunction();
                if (!function.getName().equals("running")) throw new CSSParseException("Only the running function is supported here", -1);
                List params = function.getParameters();
                if (params.size() != 1) throw new CSSParseException("The running function takes one parameter", -1);
                PropertyValue param = (PropertyValue)params.get(0);
                if (param.getPrimitiveType() == 21) return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
                throw new CSSParseException("The running function takes an identifier as a parameter", -1);
            }
        }

        private BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class PageBreakInside
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AVOID, IdentValue.AUTO});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class PageBreakAfter
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO, IdentValue.ALWAYS, IdentValue.AVOID, IdentValue.LEFT, IdentValue.RIGHT});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Page
    extends AbstractPropertyBuilder {
        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentType(cssName, value);
                if (!value.getStringValue().equals("auto")) {
                    value = new PropertyValue(19, value.getStringValue(), value.getCssText());
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }

    public static class PageBreakBefore
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO, IdentValue.ALWAYS, IdentValue.AVOID, IdentValue.LEFT, IdentValue.RIGHT});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class PaddingLeft
    extends NonNegativeLengthLike {
    }

    public static class PaddingBottom
    extends NonNegativeLengthLike {
    }

    public static class PaddingRight
    extends NonNegativeLengthLike {
    }

    public static class PaddingTop
    extends NonNegativeLengthLike {
    }

    public static class Overflow
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.VISIBLE, IdentValue.HIDDEN});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Orphans
    extends PlainInteger {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class MinWidth
    extends NonNegativeLengthLike {
    }

    public static class MinHeight
    extends NonNegativeLengthLike {
    }

    public static class MaxWidth
    extends LengthLikeWithNone {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class MaxHeight
    extends LengthLikeWithNone {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class MarginLeft
    extends LengthLikeWithAuto {
    }

    public static class MarginBottom
    extends LengthLikeWithAuto {
    }

    public static class MarginRight
    extends LengthLikeWithAuto {
    }

    public static class MarginTop
    extends LengthLikeWithAuto {
    }

    public static class ListStyleType
    extends SingleIdent {
        @Override
        protected BitSet getAllowed() {
            return LIST_STYLE_TYPES;
        }
    }

    public static class ListStylePosition
    extends SingleIdent {
        @Override
        protected BitSet getAllowed() {
            return LIST_STYLE_POSITIONS;
        }
    }

    public static class ListStyleImage
    extends GenericURIWithNone {
    }

    public static class LineHeight
    extends AbstractPropertyBuilder {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NORMAL});

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentLengthNumberOrPercentType(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, ALLOWED, ident);
                } else if ((double)value.getFloatValue() < 0.0) {
                    throw new CSSParseException("line-height may not be negative", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }

    public static class LetterSpacing
    extends LengthWithNormal {
    }

    public static class Left
    extends LengthLikeWithAuto {
    }

    public static class FSNamedDestination
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NONE, IdentValue.CREATE});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class FSKeepWithInline
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO, IdentValue.KEEP});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class FSDynamicAutoWidth
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.DYNAMIC, IdentValue.STATIC});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Height
    extends LengthLikeWithAuto {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class FSFitImagesToWidth
    extends LengthLikeWithAuto {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class FSTextDecorationExtent
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.LINE, IdentValue.BLOCK});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class FSTablePaginate
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.PAGINATE, IdentValue.AUTO});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class FSTableCellRowspan
    extends ColOrRowSpan {
    }

    public static class FSTableCellColspan
    extends ColOrRowSpan {
    }

    public static class FSPDFFontEncoding
    extends AbstractPropertyBuilder {
        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentOrString(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    return Collections.singletonList(new PropertyDeclaration(cssName, new PropertyValue(19, value.getStringValue(), value.getCssText()), important, origin));
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }

    public static class FSPDFFontEmbed
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO, IdentValue.EMBED});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class FSPageOrientation
    extends SingleIdent {
        @Override
        protected BitSet getAllowed() {
            return PAGE_ORIENTATIONS;
        }
    }

    public static class FSPageSequence
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.START, IdentValue.AUTO});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class FSPageWidth
    extends LengthLikeWithAuto {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class FSPageHeight
    extends LengthLikeWithAuto {
        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    public static class FSFontMetricSrc
    extends GenericURIWithNone {
    }

    public static class FSBorderSpacingVertical
    extends Length {
    }

    public static class FSBorderSpacingHorizontal
    extends Length {
    }

    public static class FontWeight
    extends AbstractPropertyBuilder {
        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentOrNumberType(cssName, value);
                short type = value.getPrimitiveType();
                if (type == 21) {
                    this.checkIdentType(cssName, value);
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, this.getAllowed(), ident);
                } else if (type == 1) {
                    IdentValue weight = Conversions.getNumericFontWeight(value.getFloatValue());
                    if (weight == null) {
                        throw new CSSParseException(value + " is not a valid font weight", -1);
                    }
                    PropertyValue replacement = new PropertyValue(21, weight.toString(), weight.toString());
                    replacement.setIdentValue(weight);
                    return Collections.singletonList(new PropertyDeclaration(cssName, replacement, important, origin));
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }

        private BitSet getAllowed() {
            return FONT_WEIGHTS;
        }
    }

    public static class FontVariant
    extends SingleIdent {
        @Override
        protected BitSet getAllowed() {
            return FONT_VARIANTS;
        }
    }

    public static class FontStyle
    extends SingleIdent {
        @Override
        protected BitSet getAllowed() {
            return FONT_STYLES;
        }
    }

    public static class FontSize
    extends AbstractPropertyBuilder {
        private static final BitSet ALLOWED = new BitSet(IdentValue.getIdentCount());

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentLengthOrPercentType(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, ALLOWED, ident);
                } else if (value.getFloatValue() < 0.0f) {
                    throw new CSSParseException("font-size may not be negative", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }

        static {
            ALLOWED.or(ABSOLUTE_FONT_SIZES);
            ALLOWED.or(RELATIVE_FONT_SIZES);
        }
    }

    public static class FontFamily
    extends AbstractPropertyBuilder {
        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            if (values.size() == 1) {
                CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
                this.checkInheritAllowed(value, inheritAllowed);
                if (value.getCssValueType() == 0) {
                    return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
                }
            }
            ArrayList<String> consecutiveIdents = new ArrayList<String>();
            ArrayList<String> normalized = new ArrayList<String>(values.size());
            for (PropertyValue value : values) {
                Token operator = value.getOperator();
                if (operator != null && operator != Token.TK_COMMA) {
                    throw new CSSParseException("Invalid font-family definition", -1);
                }
                if (operator != null && consecutiveIdents.size() > 0) {
                    normalized.add(this.concat(consecutiveIdents, ' '));
                    consecutiveIdents.clear();
                }
                this.checkInheritAllowed(value, false);
                short type = value.getPrimitiveType();
                if (type == 19) {
                    if (consecutiveIdents.size() > 0) {
                        normalized.add(this.concat(consecutiveIdents, ' '));
                        consecutiveIdents.clear();
                    }
                    normalized.add(value.getStringValue());
                    continue;
                }
                if (type == 21) {
                    consecutiveIdents.add(value.getStringValue());
                    continue;
                }
                throw new CSSParseException("Invalid font-family definition", -1);
            }
            if (consecutiveIdents.size() > 0) {
                normalized.add(this.concat(consecutiveIdents, ' '));
            }
            String text = this.concat(normalized, ',');
            PropertyValue result = new PropertyValue(19, text, text);
            result.setStringArrayValue(normalized.toArray(new String[normalized.size()]));
            return Collections.singletonList(new PropertyDeclaration(cssName, result, important, origin));
        }

        private String concat(List strings, char separator) {
            StringBuffer buf = new StringBuffer(64);
            Iterator i = strings.iterator();
            while (i.hasNext()) {
                String s = (String)i.next();
                buf.append(s);
                if (!i.hasNext()) continue;
                buf.append(separator);
            }
            return buf.toString();
        }
    }

    public static class Float
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.LEFT, IdentValue.RIGHT, IdentValue.NONE});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class EmptyCells
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.SHOW, IdentValue.HIDE});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Display
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.INLINE, IdentValue.BLOCK, IdentValue.LIST_ITEM, IdentValue.INLINE_BLOCK, IdentValue.TABLE, IdentValue.INLINE_TABLE, IdentValue.TABLE_ROW_GROUP, IdentValue.TABLE_HEADER_GROUP, IdentValue.TABLE_FOOTER_GROUP, IdentValue.TABLE_ROW, IdentValue.TABLE_COLUMN_GROUP, IdentValue.TABLE_COLUMN, IdentValue.TABLE_CELL, IdentValue.TABLE_CAPTION, IdentValue.NONE});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Cursor
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO, IdentValue.CROSSHAIR, IdentValue.DEFAULT, IdentValue.POINTER, IdentValue.MOVE, IdentValue.E_RESIZE, IdentValue.NE_RESIZE, IdentValue.NW_RESIZE, IdentValue.N_RESIZE, IdentValue.SE_RESIZE, IdentValue.SW_RESIZE, IdentValue.S_RESIZE, IdentValue.W_RESIZE, IdentValue.TEXT, IdentValue.WAIT, IdentValue.HELP, IdentValue.PROGRESS});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Color
    extends GenericColor {
    }

    public static class Clear
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NONE, IdentValue.LEFT, IdentValue.RIGHT, IdentValue.BOTH});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class CaptionSide
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.TOP, IdentValue.BOTTOM});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class Bottom
    extends LengthLikeWithAuto {
    }

    public static class BorderBottomLeftRadius
    extends GenericBorderCornerRadius {
    }

    public static class BorderBottomRightRadius
    extends GenericBorderCornerRadius {
    }

    public static class BorderTopRightRadius
    extends GenericBorderCornerRadius {
    }

    public static class BorderTopLeftRadius
    extends GenericBorderCornerRadius {
    }

    public static class BorderLeftWidth
    extends GenericBorderWidth {
    }

    public static class BorderBottomWidth
    extends GenericBorderWidth {
    }

    public static class BorderRightWidth
    extends GenericBorderWidth {
    }

    public static class BorderTopWidth
    extends GenericBorderWidth {
    }

    public static class BorderLeftStyle
    extends GenericBorderStyle {
    }

    public static class BorderBottomStyle
    extends GenericBorderStyle {
    }

    public static class BorderRightStyle
    extends GenericBorderStyle {
    }

    public static class BorderTopStyle
    extends GenericBorderStyle {
    }

    public static class BorderLeftColor
    extends GenericColor {
    }

    public static class BorderBottomColor
    extends GenericColor {
    }

    public static class BorderRightColor
    extends GenericColor {
    }

    public static class BorderTopColor
    extends GenericColor {
    }

    public static class BorderCollapse
    extends SingleIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.COLLAPSE, IdentValue.SEPARATE});

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    public static class BackgroundRepeat
    extends SingleIdent {
        @Override
        protected BitSet getAllowed() {
            return BACKGROUND_REPEATS;
        }
    }

    public static class BackgroundPosition
    extends AbstractPropertyBuilder {
        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, 2, values.size());
            CSSPrimitiveValue first = (CSSPrimitiveValue)values.get(0);
            CSSPrimitiveValue second = null;
            if (values.size() == 2) {
                second = (CSSPrimitiveValue)values.get(1);
            }
            this.checkInheritAllowed(first, inheritAllowed);
            if (values.size() == 1 && first.getCssValueType() == 0) {
                return Collections.singletonList(new PropertyDeclaration(cssName, first, important, origin));
            }
            if (second != null) {
                this.checkInheritAllowed(second, false);
            }
            this.checkIdentLengthOrPercentType(cssName, first);
            if (second == null) {
                if (this.isLength(first) || first.getPrimitiveType() == 2) {
                    ArrayList<CSSPrimitiveValue> responseValues = new ArrayList<CSSPrimitiveValue>(2);
                    responseValues.add(first);
                    responseValues.add(new PropertyValue(2, 50.0f, "50%"));
                    return Collections.singletonList(new PropertyDeclaration(CSSName.BACKGROUND_POSITION, new PropertyValue(responseValues), important, origin));
                }
            } else {
                this.checkIdentLengthOrPercentType(cssName, second);
            }
            IdentValue firstIdent = null;
            if (first.getPrimitiveType() == 21) {
                firstIdent = this.checkIdent(cssName, first);
                this.checkValidity(cssName, this.getAllowed(), firstIdent);
            }
            IdentValue secondIdent = null;
            if (second == null) {
                secondIdent = IdentValue.CENTER;
            } else if (second.getPrimitiveType() == 21) {
                secondIdent = this.checkIdent(cssName, second);
                this.checkValidity(cssName, this.getAllowed(), secondIdent);
            }
            if (firstIdent == null && secondIdent == null) {
                return Collections.singletonList(new PropertyDeclaration(CSSName.BACKGROUND_POSITION, new PropertyValue(values), important, origin));
            }
            if (firstIdent != null && secondIdent != null) {
                if (firstIdent == IdentValue.TOP || firstIdent == IdentValue.BOTTOM || secondIdent == IdentValue.LEFT || secondIdent == IdentValue.RIGHT) {
                    IdentValue temp = firstIdent;
                    firstIdent = secondIdent;
                    secondIdent = temp;
                }
                this.checkIdentPosition(cssName, firstIdent, secondIdent);
                return this.createTwoPercentValueResponse(this.getPercentForIdent(firstIdent), this.getPercentForIdent(secondIdent), important, origin);
            }
            this.checkIdentPosition(cssName, firstIdent, secondIdent);
            ArrayList<CSSPrimitiveValue> responseValues = new ArrayList<CSSPrimitiveValue>(2);
            if (firstIdent == null) {
                responseValues.add(first);
                responseValues.add(this.createValueForIdent(secondIdent));
            } else {
                responseValues.add(this.createValueForIdent(firstIdent));
                responseValues.add(second);
            }
            return Collections.singletonList(new PropertyDeclaration(CSSName.BACKGROUND_POSITION, new PropertyValue(responseValues), important, origin));
        }

        private void checkIdentPosition(CSSName cssName, IdentValue firstIdent, IdentValue secondIdent) {
            if (firstIdent == IdentValue.TOP || firstIdent == IdentValue.BOTTOM || secondIdent == IdentValue.LEFT || secondIdent == IdentValue.RIGHT) {
                throw new CSSParseException("Invalid combination of keywords in " + cssName, -1);
            }
        }

        private float getPercentForIdent(IdentValue ident) {
            float percent = 0.0f;
            if (ident == IdentValue.CENTER) {
                percent = 50.0f;
            } else if (ident == IdentValue.BOTTOM || ident == IdentValue.RIGHT) {
                percent = 100.0f;
            }
            return percent;
        }

        private PropertyValue createValueForIdent(IdentValue ident) {
            float percent = this.getPercentForIdent(ident);
            return new PropertyValue(2, percent, percent + "%");
        }

        private List createTwoPercentValueResponse(float percent1, float percent2, boolean important, int origin) {
            PropertyValue value1 = new PropertyValue(2, percent1, percent1 + "%");
            PropertyValue value2 = new PropertyValue(2, percent2, percent2 + "%");
            ArrayList<PropertyValue> values = new ArrayList<PropertyValue>(2);
            values.add(value1);
            values.add(value2);
            PropertyDeclaration result = new PropertyDeclaration(CSSName.BACKGROUND_POSITION, new PropertyValue(values), important, origin);
            return Collections.singletonList(result);
        }

        private BitSet getAllowed() {
            return BACKGROUND_POSITIONS;
        }
    }

    public static class BackgroundSize
    extends AbstractPropertyBuilder {
        private static final BitSet ALL_ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO, IdentValue.CONTAIN, IdentValue.COVER});

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, 2, values.size());
            CSSPrimitiveValue first = (CSSPrimitiveValue)values.get(0);
            CSSPrimitiveValue second = null;
            if (values.size() == 2) {
                second = (CSSPrimitiveValue)values.get(1);
            }
            this.checkInheritAllowed(first, inheritAllowed);
            if (values.size() == 1 && first.getCssValueType() == 0) {
                return Collections.singletonList(new PropertyDeclaration(cssName, first, important, origin));
            }
            if (second != null) {
                this.checkInheritAllowed(second, false);
            }
            this.checkIdentLengthOrPercentType(cssName, first);
            if (second == null) {
                if (first.getPrimitiveType() == 21) {
                    IdentValue firstIdent = this.checkIdent(cssName, first);
                    this.checkValidity(cssName, ALL_ALLOWED, firstIdent);
                    if (firstIdent == IdentValue.CONTAIN || firstIdent == IdentValue.COVER) {
                        return Collections.singletonList(new PropertyDeclaration(cssName, first, important, origin));
                    }
                    return PrimitivePropertyBuilders.createTwoValueResponse(CSSName.BACKGROUND_SIZE, first, first, origin, important);
                }
                return PrimitivePropertyBuilders.createTwoValueResponse(CSSName.BACKGROUND_SIZE, first, new PropertyValue(IdentValue.AUTO), origin, important);
            }
            this.checkIdentLengthOrPercentType(cssName, second);
            if (first.getPrimitiveType() == 21) {
                IdentValue firstIdent = this.checkIdent(cssName, first);
                if (firstIdent != IdentValue.AUTO) {
                    throw new CSSParseException("The only ident value allowed here is 'auto'", -1);
                }
            } else if (((PropertyValue)first).getFloatValue() < 0.0f) {
                throw new CSSParseException(cssName + " values cannot be negative", -1);
            }
            if (second.getPrimitiveType() == 21) {
                IdentValue secondIdent = this.checkIdent(cssName, second);
                if (secondIdent != IdentValue.AUTO) {
                    throw new CSSParseException("The only ident value allowed here is 'auto'", -1);
                }
            } else if (((PropertyValue)second).getFloatValue() < 0.0f) {
                throw new CSSParseException(cssName + " values cannot be negative", -1);
            }
            return PrimitivePropertyBuilders.createTwoValueResponse(CSSName.BACKGROUND_SIZE, first, second, origin, important);
        }
    }

    public static class BackgroundImage
    extends GenericURIWithNone {
    }

    public static class BackgroundColor
    extends GenericColor {
    }

    public static class BackgroundAttachment
    extends SingleIdent {
        @Override
        protected BitSet getAllowed() {
            return BACKGROUND_ATTACHMENTS;
        }
    }

    private static class GenericURIWithNone
    extends AbstractPropertyBuilder {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NONE});

        private GenericURIWithNone() {
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentOrURIType(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, ALLOWED, ident);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }

    private static class LengthLikeWithNone
    extends LengthLikeWithIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NONE});

        private LengthLikeWithNone() {
        }

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    private static class LengthWithNormal
    extends LengthWithIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.NORMAL});

        private LengthWithNormal() {
        }

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    private static class LengthLikeWithAuto
    extends LengthLikeWithIdent {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.AUTO});

        private LengthLikeWithAuto() {
        }

        @Override
        protected BitSet getAllowed() {
            return ALLOWED;
        }
    }

    private static class Length
    extends AbstractPropertyBuilder {
        private Length() {
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkLengthType(cssName, value);
                if (!this.isNegativeValuesAllowed() && value.getFloatValue() < 0.0f) {
                    throw new CSSParseException(cssName + " may not be negative", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }

        protected boolean isNegativeValuesAllowed() {
            return true;
        }
    }

    private static class PlainInteger
    extends AbstractPropertyBuilder {
        private PlainInteger() {
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkInteger(cssName, value);
                if (!this.isNegativeValuesAllowed() && value.getFloatValue() < 0.0f) {
                    throw new CSSParseException(cssName + " may not be negative", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }

        protected boolean isNegativeValuesAllowed() {
            return true;
        }
    }

    private static class ColOrRowSpan
    extends AbstractPropertyBuilder {
        private ColOrRowSpan() {
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkNumberType(cssName, value);
                if (value.getFloatValue() < 1.0f) {
                    throw new CSSParseException("colspan/rowspan must be greater than zero", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }

    private static class NonNegativeLengthLike
    extends LengthLike {
        private NonNegativeLengthLike() {
        }

        @Override
        protected boolean isNegativeValuesAllowed() {
            return false;
        }
    }

    private static class LengthLike
    extends AbstractPropertyBuilder {
        private LengthLike() {
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkLengthOrPercentType(cssName, value);
                if (!this.isNegativeValuesAllowed() && value.getFloatValue() < 0.0f) {
                    throw new CSSParseException(cssName + " may not be negative", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }

        protected boolean isNegativeValuesAllowed() {
            return true;
        }
    }

    private static abstract class LengthLikeWithIdent
    extends AbstractPropertyBuilder {
        private LengthLikeWithIdent() {
        }

        protected abstract BitSet getAllowed();

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentLengthOrPercentType(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, this.getAllowed(), ident);
                } else if (!this.isNegativeValuesAllowed() && value.getFloatValue() < 0.0f) {
                    throw new CSSParseException(cssName + " may not be negative", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }

        protected boolean isNegativeValuesAllowed() {
            return true;
        }
    }

    private static abstract class LengthWithIdent
    extends AbstractPropertyBuilder {
        private LengthWithIdent() {
        }

        protected abstract BitSet getAllowed();

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentOrLengthType(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, this.getAllowed(), ident);
                } else if (!this.isNegativeValuesAllowed() && value.getFloatValue() < 0.0f) {
                    throw new CSSParseException(cssName + " may not be negative", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }

        protected boolean isNegativeValuesAllowed() {
            return true;
        }
    }

    private static class GenericBorderCornerRadius
    extends AbstractPropertyBuilder {
        private GenericBorderCornerRadius() {
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, 2, values.size());
            PropertyValue first = (PropertyValue)values.get(0);
            PropertyValue second = null;
            if (values.size() == 2) {
                second = (PropertyValue)values.get(1);
            }
            this.checkInheritAllowed(first, inheritAllowed);
            if (second != null) {
                this.checkInheritAllowed(second, false);
            }
            this.checkLengthOrPercentType(cssName, first);
            if (second == null) {
                return PrimitivePropertyBuilders.createTwoValueResponse(cssName, first, first, origin, important);
            }
            this.checkLengthOrPercentType(cssName, second);
            return PrimitivePropertyBuilders.createTwoValueResponse(cssName, first, second, origin, important);
        }
    }

    private static class GenericBorderWidth
    extends AbstractPropertyBuilder {
        private GenericBorderWidth() {
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentOrLengthType(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, BORDER_WIDTHS, ident);
                    return Collections.singletonList(new PropertyDeclaration(cssName, Conversions.getBorderWidth(ident.toString()), important, origin));
                }
                if (value.getFloatValue() < 0.0f) {
                    throw new CSSParseException(cssName + " may not be negative", -1);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }

    private static class GenericBorderStyle
    extends SingleIdent {
        private GenericBorderStyle() {
        }

        @Override
        protected BitSet getAllowed() {
            return BORDER_STYLES;
        }
    }

    private static class GenericColor
    extends AbstractPropertyBuilder {
        private static final BitSet ALLOWED = PrimitivePropertyBuilders.access$500(new IdentValue[]{IdentValue.TRANSPARENT});

        private GenericColor() {
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentOrColorType(cssName, value);
                if (value.getPrimitiveType() == 21) {
                    FSRGBColor color = Conversions.getColor(value.getStringValue());
                    if (color != null) {
                        return Collections.singletonList(new PropertyDeclaration(cssName, new PropertyValue(color), important, origin));
                    }
                    IdentValue ident = this.checkIdent(cssName, value);
                    this.checkValidity(cssName, ALLOWED, ident);
                }
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }

    private static abstract class SingleIdent
    extends AbstractPropertyBuilder {
        private SingleIdent() {
        }

        protected abstract BitSet getAllowed();

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            this.checkValueCount(cssName, 1, values.size());
            CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() != 0) {
                this.checkIdentType(cssName, value);
                IdentValue ident = this.checkIdent(cssName, value);
                this.checkValidity(cssName, this.getAllowed(), ident);
            }
            return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
        }
    }
}

