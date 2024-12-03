/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public abstract class OneToFourPropertyBuilders {

    public static class Padding
    extends OneToFourPropertyBuilder {
        @Override
        protected CSSName[] getProperties() {
            return new CSSName[]{CSSName.PADDING_TOP, CSSName.PADDING_RIGHT, CSSName.PADDING_BOTTOM, CSSName.PADDING_LEFT};
        }

        @Override
        protected PropertyBuilder getPropertyBuilder() {
            return PrimitivePropertyBuilders.PADDING;
        }
    }

    public static class Margin
    extends OneToFourPropertyBuilder {
        @Override
        protected CSSName[] getProperties() {
            return new CSSName[]{CSSName.MARGIN_TOP, CSSName.MARGIN_RIGHT, CSSName.MARGIN_BOTTOM, CSSName.MARGIN_LEFT};
        }

        @Override
        protected PropertyBuilder getPropertyBuilder() {
            return PrimitivePropertyBuilders.MARGIN;
        }
    }

    public static class BorderRadius
    extends OneToFourPropertyBuilder {
        @Override
        protected CSSName[] getProperties() {
            return new CSSName[]{CSSName.BORDER_TOP_LEFT_RADIUS, CSSName.BORDER_TOP_RIGHT_RADIUS, CSSName.BORDER_BOTTOM_RIGHT_RADIUS, CSSName.BORDER_BOTTOM_LEFT_RADIUS};
        }

        @Override
        protected PropertyBuilder getPropertyBuilder() {
            return PrimitivePropertyBuilders.BORDER_RADIUS;
        }
    }

    public static class BorderWidth
    extends OneToFourPropertyBuilder {
        @Override
        protected CSSName[] getProperties() {
            return new CSSName[]{CSSName.BORDER_TOP_WIDTH, CSSName.BORDER_RIGHT_WIDTH, CSSName.BORDER_BOTTOM_WIDTH, CSSName.BORDER_LEFT_WIDTH};
        }

        @Override
        protected PropertyBuilder getPropertyBuilder() {
            return PrimitivePropertyBuilders.BORDER_WIDTH;
        }
    }

    public static class BorderStyle
    extends OneToFourPropertyBuilder {
        @Override
        protected CSSName[] getProperties() {
            return new CSSName[]{CSSName.BORDER_TOP_STYLE, CSSName.BORDER_RIGHT_STYLE, CSSName.BORDER_BOTTOM_STYLE, CSSName.BORDER_LEFT_STYLE};
        }

        @Override
        protected PropertyBuilder getPropertyBuilder() {
            return PrimitivePropertyBuilders.BORDER_STYLE;
        }
    }

    public static class BorderColor
    extends OneToFourPropertyBuilder {
        @Override
        protected CSSName[] getProperties() {
            return new CSSName[]{CSSName.BORDER_TOP_COLOR, CSSName.BORDER_RIGHT_COLOR, CSSName.BORDER_BOTTOM_COLOR, CSSName.BORDER_LEFT_COLOR};
        }

        @Override
        protected PropertyBuilder getPropertyBuilder() {
            return PrimitivePropertyBuilders.COLOR;
        }
    }

    private static abstract class OneToFourPropertyBuilder
    extends AbstractPropertyBuilder {
        private OneToFourPropertyBuilder() {
        }

        protected abstract CSSName[] getProperties();

        protected abstract PropertyBuilder getPropertyBuilder();

        @Override
        public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
            ArrayList<PropertyDeclaration> result = new ArrayList<PropertyDeclaration>(4);
            this.checkValueCount(cssName, 1, 4, values.size());
            PropertyBuilder builder = this.getPropertyBuilder();
            CSSName[] props = this.getProperties();
            switch (values.size()) {
                case 1: {
                    PropertyDeclaration decl1 = (PropertyDeclaration)builder.buildDeclarations(cssName, values, origin, important).get(0);
                    result.add(this.copyOf(decl1, props[0]));
                    result.add(this.copyOf(decl1, props[1]));
                    result.add(this.copyOf(decl1, props[2]));
                    result.add(this.copyOf(decl1, props[3]));
                    break;
                }
                case 2: {
                    PropertyDeclaration decl1 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(0, 1), origin, important, false).get(0);
                    PropertyDeclaration decl2 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(1, 2), origin, important, false).get(0);
                    result.add(this.copyOf(decl1, props[0]));
                    result.add(this.copyOf(decl2, props[1]));
                    result.add(this.copyOf(decl1, props[2]));
                    result.add(this.copyOf(decl2, props[3]));
                    break;
                }
                case 3: {
                    PropertyDeclaration decl1 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(0, 1), origin, important, false).get(0);
                    PropertyDeclaration decl2 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(1, 2), origin, important, false).get(0);
                    PropertyDeclaration decl3 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(2, 3), origin, important, false).get(0);
                    result.add(this.copyOf(decl1, props[0]));
                    result.add(this.copyOf(decl2, props[1]));
                    result.add(this.copyOf(decl3, props[2]));
                    result.add(this.copyOf(decl2, props[3]));
                    break;
                }
                case 4: {
                    PropertyDeclaration decl1 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(0, 1), origin, important, false).get(0);
                    PropertyDeclaration decl2 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(1, 2), origin, important, false).get(0);
                    PropertyDeclaration decl3 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(2, 3), origin, important, false).get(0);
                    PropertyDeclaration decl4 = (PropertyDeclaration)builder.buildDeclarations(cssName, values.subList(3, 4), origin, important, false).get(0);
                    result.add(this.copyOf(decl1, props[0]));
                    result.add(this.copyOf(decl2, props[1]));
                    result.add(this.copyOf(decl3, props[2]));
                    result.add(this.copyOf(decl4, props[3]));
                }
            }
            return result;
        }
    }
}

