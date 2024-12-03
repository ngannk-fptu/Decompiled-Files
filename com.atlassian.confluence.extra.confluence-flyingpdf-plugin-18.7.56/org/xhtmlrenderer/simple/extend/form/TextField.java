/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.FontMetrics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTextFieldUI;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.InputField;
import org.xhtmlrenderer.simple.extend.form.SizeLimitedDocument;
import org.xhtmlrenderer.util.GeneralUtil;

class TextField
extends InputField {
    public TextField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        TextFieldJTextField textfield = new TextFieldJTextField();
        if (this.hasAttribute("size")) {
            int size = GeneralUtil.parseIntRelaxed(this.getAttribute("size"));
            if (size == 0) {
                textfield.setColumns(15);
            } else {
                textfield.setColumns(size);
            }
        } else {
            textfield.setColumns(15);
        }
        if (this.hasAttribute("maxlength")) {
            textfield.setDocument(new SizeLimitedDocument(GeneralUtil.parseIntRelaxed(this.getAttribute("maxlength"))));
        }
        if (this.hasAttribute("readonly") && this.getAttribute("readonly").equalsIgnoreCase("readonly")) {
            textfield.setEditable(false);
        }
        this.applyComponentStyle(textfield);
        return textfield;
    }

    @Override
    protected void applyComponentStyle(JComponent component) {
        FSDerivedValue heightValue;
        int right;
        super.applyComponentStyle(component);
        TextFieldJTextField field = (TextFieldJTextField)component;
        CalculatedStyle style = this.getBox().getStyle();
        BorderPropertySet border = style.getBorder(null);
        boolean disableOSBorder = border.leftStyle() != null && border.rightStyle() != null || border.topStyle() != null || border.bottomStyle() != null;
        RectPropertySet padding = style.getCachedPadding();
        Integer paddingTop = TextField.getLengthValue(style, CSSName.PADDING_TOP);
        Integer paddingLeft = TextField.getLengthValue(style, CSSName.PADDING_LEFT);
        Integer paddingBottom = TextField.getLengthValue(style, CSSName.PADDING_BOTTOM);
        Integer paddingRight = TextField.getLengthValue(style, CSSName.PADDING_RIGHT);
        int top = paddingTop == null ? 2 : Math.max(2, paddingTop);
        int left = paddingLeft == null ? 3 : Math.max(3, paddingLeft);
        int bottom = paddingBottom == null ? 2 : Math.max(2, paddingBottom);
        int n = right = paddingRight == null ? 3 : Math.max(3, paddingRight);
        if (disableOSBorder) {
            BasicTextFieldUI ui = new BasicTextFieldUI();
            field.setUI(ui);
            Border fieldBorder = BorderFactory.createEmptyBorder(top, left, bottom, right);
            field.setBorder(fieldBorder);
        } else {
            field.setMargin(new Insets(top, left, bottom, right));
        }
        padding.setRight(0.0f);
        padding.setLeft(0.0f);
        padding.setTop(0.0f);
        padding.setBottom(0.0f);
        FSDerivedValue widthValue = style.valueByName(CSSName.WIDTH);
        if (widthValue instanceof LengthValue) {
            this.intrinsicWidth = new Integer(this.getBox().getContentWidth() + left + right);
        }
        if ((heightValue = style.valueByName(CSSName.HEIGHT)) instanceof LengthValue) {
            this.intrinsicHeight = new Integer(this.getBox().getHeight() + top + bottom);
        }
    }

    @Override
    protected void applyOriginalState() {
        JTextField textfield = (JTextField)this.getComponent();
        textfield.setText(this.getOriginalState().getValue());
        textfield.setCaretPosition(0);
    }

    @Override
    protected String[] getFieldValues() {
        JTextField textfield = (JTextField)this.getComponent();
        return new String[]{textfield.getText()};
    }

    private static class TextFieldJTextField
    extends JTextField {
        int columnWidth = 0;

        private TextFieldJTextField() {
        }

        @Override
        protected int getColumnWidth() {
            if (this.columnWidth == 0) {
                FontMetrics metrics = this.getFontMetrics(this.getFont());
                this.columnWidth = metrics.charWidth('o');
            }
            return this.columnWidth;
        }
    }
}

