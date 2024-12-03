/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.Conversions;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class BorderPropertyBuilders {

    public static class Border
    extends BorderSidePropertyBuilder {
        @Override
        protected CSSName[][] getProperties() {
            return new CSSName[][]{{CSSName.BORDER_TOP_WIDTH, CSSName.BORDER_RIGHT_WIDTH, CSSName.BORDER_BOTTOM_WIDTH, CSSName.BORDER_LEFT_WIDTH}, {CSSName.BORDER_TOP_STYLE, CSSName.BORDER_RIGHT_STYLE, CSSName.BORDER_BOTTOM_STYLE, CSSName.BORDER_LEFT_STYLE}, {CSSName.BORDER_TOP_COLOR, CSSName.BORDER_RIGHT_COLOR, CSSName.BORDER_BOTTOM_COLOR, CSSName.BORDER_LEFT_COLOR}};
        }
    }

    public static class BorderLeft
    extends BorderSidePropertyBuilder {
        @Override
        protected CSSName[][] getProperties() {
            return new CSSName[][]{{CSSName.BORDER_LEFT_WIDTH}, {CSSName.BORDER_LEFT_STYLE}, {CSSName.BORDER_LEFT_COLOR}};
        }
    }

    public static class BorderBottom
    extends BorderSidePropertyBuilder {
        @Override
        protected CSSName[][] getProperties() {
            return new CSSName[][]{{CSSName.BORDER_BOTTOM_WIDTH}, {CSSName.BORDER_BOTTOM_STYLE}, {CSSName.BORDER_BOTTOM_COLOR}};
        }
    }

    public static class BorderRight
    extends BorderSidePropertyBuilder {
        @Override
        protected CSSName[][] getProperties() {
            return new CSSName[][]{{CSSName.BORDER_RIGHT_WIDTH}, {CSSName.BORDER_RIGHT_STYLE}, {CSSName.BORDER_RIGHT_COLOR}};
        }
    }

    public static class BorderTop
    extends BorderSidePropertyBuilder {
        @Override
        protected CSSName[][] getProperties() {
            return new CSSName[][]{{CSSName.BORDER_TOP_WIDTH}, {CSSName.BORDER_TOP_STYLE}, {CSSName.BORDER_TOP_COLOR}};
        }
    }

    private static abstract class BorderSidePropertyBuilder
    extends AbstractPropertyBuilder {
        private BorderSidePropertyBuilder() {
        }

        protected abstract CSSName[][] getProperties();

        private void addAll(List result, CSSName[] properties, CSSPrimitiveValue value, int origin, boolean important) {
            for (int i = 0; i < properties.length; ++i) {
                result.add(new PropertyDeclaration(properties[i], value, important, origin));
            }
        }

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            CSSName[][] props = this.getProperties();
            ArrayList result = new ArrayList(3);
            if (values.size() == 1 && ((CSSPrimitiveValue)values.get(0)).getCssValueType() == 0) {
                CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
                this.addAll(result, props[0], value, origin, important);
                this.addAll(result, props[1], value, origin, important);
                this.addAll(result, props[2], value, origin, important);
                return result;
            }
            this.checkValueCount(cssName, 1, 3, values.size());
            boolean haveBorderStyle = false;
            boolean haveBorderColor = false;
            boolean haveBorderWidth = false;
            for (CSSPrimitiveValue value : values) {
                CSSPrimitiveValue borderColor;
                this.checkInheritAllowed(value, false);
                boolean matched = false;
                CSSPrimitiveValue borderWidth = this.convertToBorderWidth(value);
                if (borderWidth != null) {
                    if (haveBorderWidth) {
                        throw new CSSParseException("A border width cannot be set twice", -1);
                    }
                    haveBorderWidth = true;
                    matched = true;
                    this.addAll(result, props[0], borderWidth, origin, important);
                }
                if (this.isBorderStyle(value)) {
                    if (haveBorderStyle) {
                        throw new CSSParseException("A border style cannot be set twice", -1);
                    }
                    haveBorderStyle = true;
                    matched = true;
                    this.addAll(result, props[1], value, origin, important);
                }
                if ((borderColor = this.convertToBorderColor(value)) != null) {
                    if (haveBorderColor) {
                        throw new CSSParseException("A border color cannot be set twice", -1);
                    }
                    haveBorderColor = true;
                    matched = true;
                    this.addAll(result, props[2], borderColor, origin, important);
                }
                if (matched) continue;
                throw new CSSParseException(value.getCssText() + " is not a border width, style, or color", -1);
            }
            if (!haveBorderWidth) {
                this.addAll(result, props[0], new PropertyValue(IdentValue.FS_INITIAL_VALUE), origin, important);
            }
            if (!haveBorderStyle) {
                this.addAll(result, props[1], new PropertyValue(IdentValue.FS_INITIAL_VALUE), origin, important);
            }
            if (!haveBorderColor) {
                this.addAll(result, props[2], new PropertyValue(IdentValue.FS_INITIAL_VALUE), origin, important);
            }
            return result;
        }

        private boolean isBorderStyle(CSSPrimitiveValue value) {
            if (value.getPrimitiveType() != 21) {
                return false;
            }
            IdentValue ident = IdentValue.valueOf(value.getCssText());
            if (ident == null) {
                return false;
            }
            return PrimitivePropertyBuilders.BORDER_STYLES.get(ident.FS_ID);
        }

        private CSSPrimitiveValue convertToBorderWidth(CSSPrimitiveValue value) {
            short type = value.getPrimitiveType();
            if (type != 21 && !this.isLength(value)) {
                return null;
            }
            if (this.isLength(value)) {
                return value;
            }
            IdentValue ident = IdentValue.valueOf(value.getStringValue());
            if (ident == null) {
                return null;
            }
            if (PrimitivePropertyBuilders.BORDER_WIDTHS.get(ident.FS_ID)) {
                return Conversions.getBorderWidth(ident.toString());
            }
            return null;
        }

        private CSSPrimitiveValue convertToBorderColor(CSSPrimitiveValue value) {
            short type = value.getPrimitiveType();
            if (type != 21 && type != 25) {
                return null;
            }
            if (type == 25) {
                return value;
            }
            FSRGBColor color = Conversions.getColor(value.getStringValue());
            if (color != null) {
                return new PropertyValue(color);
            }
            IdentValue ident = IdentValue.valueOf(value.getCssText());
            if (ident == null || ident != IdentValue.TRANSPARENT) {
                return null;
            }
            return value;
        }
    }
}

