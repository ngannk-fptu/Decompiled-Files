/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicButtonUI;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.InputField;

public abstract class AbstractButtonField
extends InputField {
    public AbstractButtonField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    protected void applyComponentStyle(JButton button) {
        FSDerivedValue heightValue;
        super.applyComponentStyle(button);
        CalculatedStyle style = this.getBox().getStyle();
        BorderPropertySet border = style.getBorder(null);
        boolean disableOSBorder = border.leftStyle() != null && border.rightStyle() != null || border.topStyle() != null || border.bottomStyle() != null;
        FSColor backgroundColor = style.getBackgroundColor();
        if (disableOSBorder || backgroundColor instanceof FSRGBColor) {
            BasicButtonUI ui = new BasicButtonUI();
            button.setUI(ui);
            if (backgroundColor instanceof FSRGBColor) {
                FSRGBColor rgb = (FSRGBColor)backgroundColor;
                button.setBackground(new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
            }
            if (disableOSBorder) {
                button.setBorder(new BasicBorders.MarginBorder());
            } else {
                button.setBorder(BasicBorders.getButtonBorder());
            }
        }
        Integer paddingTop = AbstractButtonField.getLengthValue(style, CSSName.PADDING_TOP);
        Integer paddingLeft = AbstractButtonField.getLengthValue(style, CSSName.PADDING_LEFT);
        Integer paddingBottom = AbstractButtonField.getLengthValue(style, CSSName.PADDING_BOTTOM);
        Integer paddingRight = AbstractButtonField.getLengthValue(style, CSSName.PADDING_RIGHT);
        int top = paddingTop == null ? 2 : Math.max(2, paddingTop);
        int left = paddingLeft == null ? 12 : Math.max(12, paddingLeft);
        int bottom = paddingBottom == null ? 2 : Math.max(2, paddingBottom);
        int right = paddingRight == null ? 12 : Math.max(12, paddingRight);
        button.setMargin(new Insets(top, left, bottom, right));
        RectPropertySet padding = style.getCachedPadding();
        padding.setRight(0.0f);
        padding.setLeft(0.0f);
        padding.setTop(0.0f);
        padding.setBottom(0.0f);
        FSDerivedValue widthValue = style.valueByName(CSSName.WIDTH);
        if (widthValue instanceof LengthValue) {
            this.intrinsicWidth = new Integer(this.getBox().getContentWidth());
        }
        if ((heightValue = style.valueByName(CSSName.HEIGHT)) instanceof LengthValue) {
            this.intrinsicHeight = new Integer(this.getBox().getHeight());
        }
    }
}

