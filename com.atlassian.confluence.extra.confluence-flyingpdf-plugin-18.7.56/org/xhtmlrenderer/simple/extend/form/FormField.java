/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.simple.extend.URLUTF8Encoder;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;
import org.xhtmlrenderer.swing.AWTFSFont;

public abstract class FormField {
    private XhtmlForm _parentForm;
    private Element _element;
    private FormFieldState _originalState;
    private JComponent _component;
    private LayoutContext context;
    private BlockBox box;
    protected Integer intrinsicWidth;
    protected Integer intrinsicHeight;

    public FormField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        this._element = e;
        this._parentForm = form;
        this.context = context;
        this.box = box;
        this.initialize();
    }

    protected Element getElement() {
        return this._element;
    }

    public JComponent getComponent() {
        return this._component;
    }

    public XhtmlForm getParentForm() {
        return this._parentForm;
    }

    public Dimension getIntrinsicSize() {
        int width = this.intrinsicWidth == null ? 0 : this.intrinsicWidth;
        int height = this.intrinsicHeight == null ? 0 : this.intrinsicHeight;
        return new Dimension(width, height);
    }

    public void reset() {
        this.applyOriginalState();
    }

    protected UserAgentCallback getUserAgentCallback() {
        return this._parentForm.getUserAgentCallback();
    }

    protected FormFieldState getOriginalState() {
        if (this._originalState == null) {
            this._originalState = this.loadOriginalState();
        }
        return this._originalState;
    }

    protected boolean hasAttribute(String attributeName) {
        return this.getElement().getAttribute(attributeName).length() > 0;
    }

    protected String getAttribute(String attributeName) {
        return this.getElement().getAttribute(attributeName);
    }

    private void initialize() {
        this._component = this.create();
        if (this._component != null) {
            if (this.intrinsicWidth == null) {
                this.intrinsicWidth = new Integer(this._component.getPreferredSize().width);
            }
            if (this.intrinsicHeight == null) {
                this.intrinsicHeight = new Integer(this._component.getPreferredSize().height);
            }
            this._component.setSize(this.getIntrinsicSize());
            String d = this._element.getAttribute("disabled");
            if (d.equalsIgnoreCase("disabled")) {
                this._component.setEnabled(false);
            }
        }
        this.applyOriginalState();
    }

    public abstract JComponent create();

    protected FormFieldState loadOriginalState() {
        return FormFieldState.fromString("");
    }

    protected void applyOriginalState() {
    }

    public boolean includeInSubmission(JComponent source) {
        return true;
    }

    public String[] getFormDataStrings() {
        if (!this.hasAttribute("name")) {
            return new String[0];
        }
        String name = this.getAttribute("name");
        String[] values = this.getFieldValues();
        for (int i = 0; i < values.length; ++i) {
            values[i] = URLUTF8Encoder.encode(name) + "=" + URLUTF8Encoder.encode(values[i]);
        }
        return values;
    }

    protected abstract String[] getFieldValues();

    public BlockBox getBox() {
        return this.box;
    }

    public LayoutContext getContext() {
        return this.context;
    }

    public CalculatedStyle getStyle() {
        return this.getBox().getStyle();
    }

    protected void applyComponentStyle(JComponent comp) {
        FSColor background;
        CalculatedStyle style;
        FSColor foreground;
        Font font = this.getFont();
        if (font != null) {
            comp.setFont(font);
        }
        if ((foreground = (style = this.getStyle()).getColor()) != null) {
            comp.setForeground(FormField.toColor(foreground));
        }
        if ((background = style.getBackgroundColor()) != null) {
            comp.setBackground(FormField.toColor(background));
        }
    }

    private static Color toColor(FSColor color) {
        if (color instanceof FSRGBColor) {
            FSRGBColor rgb = (FSRGBColor)color;
            return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
        }
        throw new RuntimeException("internal error: unsupported color class " + color.getClass().getName());
    }

    public Font getFont() {
        FSFont font = this.getStyle().getFSFont(this.getContext());
        if (font instanceof AWTFSFont) {
            return ((AWTFSFont)font).getAWTFont();
        }
        return null;
    }

    protected static Integer getLengthValue(CalculatedStyle style, CSSName cssName) {
        FSDerivedValue widthValue = style.valueByName(cssName);
        if (widthValue instanceof LengthValue) {
            return new Integer((int)widthValue.asFloat());
        }
        return null;
    }
}

