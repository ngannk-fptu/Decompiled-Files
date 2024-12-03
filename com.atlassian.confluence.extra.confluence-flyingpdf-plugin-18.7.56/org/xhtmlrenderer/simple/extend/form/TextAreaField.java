/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicTextAreaUI;
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
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;
import org.xhtmlrenderer.util.GeneralUtil;

class TextAreaField
extends FormField {
    private TextAreaFieldJTextArea _textarea;

    public TextAreaField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        int parsedCols;
        int parsedRows;
        int rows = 4;
        int cols = 10;
        if (this.hasAttribute("rows") && (parsedRows = GeneralUtil.parseIntRelaxed(this.getAttribute("rows"))) > 0) {
            rows = parsedRows;
        }
        if (this.hasAttribute("cols") && (parsedCols = GeneralUtil.parseIntRelaxed(this.getAttribute("cols"))) > 0) {
            cols = parsedCols;
        }
        this._textarea = new TextAreaFieldJTextArea(rows, cols);
        this._textarea.setWrapStyleWord(true);
        this._textarea.setLineWrap(true);
        JScrollPane scrollpane = new JScrollPane(this._textarea);
        scrollpane.setVerticalScrollBarPolicy(20);
        scrollpane.setHorizontalScrollBarPolicy(30);
        this.applyComponentStyle(this._textarea, scrollpane);
        return scrollpane;
    }

    protected void applyComponentStyle(TextAreaFieldJTextArea textArea, JScrollPane scrollpane) {
        FSDerivedValue heightValue;
        int right;
        super.applyComponentStyle(textArea);
        CalculatedStyle style = this.getBox().getStyle();
        BorderPropertySet border = style.getBorder(null);
        boolean disableOSBorder = border.leftStyle() != null && border.rightStyle() != null || border.topStyle() != null || border.bottomStyle() != null;
        RectPropertySet padding = style.getCachedPadding();
        Integer paddingTop = TextAreaField.getLengthValue(style, CSSName.PADDING_TOP);
        Integer paddingLeft = TextAreaField.getLengthValue(style, CSSName.PADDING_LEFT);
        Integer paddingBottom = TextAreaField.getLengthValue(style, CSSName.PADDING_BOTTOM);
        Integer paddingRight = TextAreaField.getLengthValue(style, CSSName.PADDING_RIGHT);
        int top = paddingTop == null ? 2 : Math.max(2, paddingTop);
        int left = paddingLeft == null ? 3 : Math.max(3, paddingLeft);
        int bottom = paddingBottom == null ? 2 : Math.max(2, paddingBottom);
        int n = right = paddingRight == null ? 3 : Math.max(3, paddingRight);
        if (disableOSBorder) {
            BasicTextAreaUI ui = new BasicTextAreaUI();
            textArea.setUI(ui);
            scrollpane.setBorder(null);
        }
        textArea.setMargin(new Insets(top, left, bottom, right));
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
    protected FormFieldState loadOriginalState() {
        return FormFieldState.fromString(XhtmlForm.collectText(this.getElement()));
    }

    @Override
    protected void applyOriginalState() {
        this._textarea.setText(this.getOriginalState().getValue());
    }

    @Override
    protected String[] getFieldValues() {
        JTextArea textarea = (JTextArea)((JScrollPane)this.getComponent()).getViewport().getView();
        return new String[]{textarea.getText()};
    }

    private class TextAreaFieldJTextArea
    extends JTextArea {
        int columnWidth;

        public TextAreaFieldJTextArea(int rows, int columns) {
            super(rows, columns);
            this.columnWidth = 0;
        }

        @Override
        protected int getColumnWidth() {
            if (this.columnWidth == 0) {
                FontMetrics metrics = this.getFontMetrics(this.getFont());
                this.columnWidth = metrics.charWidth('o');
            }
            return this.columnWidth;
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            Dimension size = super.getPreferredScrollableViewportSize();
            size = size == null ? new Dimension(400, 400) : size;
            Insets insets = this.getInsets();
            size.width = this.getColumns() == 0 ? size.width : this.getColumns() * this.getColumnWidth() + insets.left + insets.right;
            size.height = this.getRows() == 0 ? size.height : this.getRows() * this.getRowHeight() + insets.top + insets.bottom;
            return size;
        }
    }
}

