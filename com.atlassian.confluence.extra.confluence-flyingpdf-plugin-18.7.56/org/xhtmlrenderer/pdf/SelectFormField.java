/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.AbstractFormField;
import org.xhtmlrenderer.pdf.ITextFSFont;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.util.Util;

public class SelectFormField
extends AbstractFormField {
    private static final String FIELD_TYPE = "Select";
    private static final int EMPTY_SPACE_COUNT = 10;
    private static final int EXTRA_SPACE_COUNT = 4;
    private List _options;
    private int _baseline;

    public SelectFormField(LayoutContext c, BlockBox box, int cssWidth, int cssHeight) {
        this._options = this.readOptions(box.getElement());
        this.initDimensions(c, box, cssWidth, cssHeight);
        float fontSize = box.getStyle().getFSFont(c).getSize2D();
        this._baseline = (int)((float)(this.getHeight() / 2) + fontSize * 0.3f);
    }

    private int getSelectedIndex() {
        int result = 0;
        List options = this._options;
        int offset = 0;
        for (Option option : options) {
            if (option.isSelected()) {
                result = offset;
            }
            ++offset;
        }
        return result;
    }

    private String[][] getPDFOptions() {
        List options = this._options;
        String[][] result = new String[options.size()][];
        int offset = 0;
        for (Option option : options) {
            result[offset] = new String[]{option.getValue(), option.getLabel()};
            ++offset;
        }
        return result;
    }

    private int calcDefaultWidth(LayoutContext c, BlockBox box) {
        List options = this._options;
        if (options.size() == 0) {
            return c.getTextRenderer().getWidth(c.getFontContext(), box.getStyle().getFSFont(c), this.spaces(10));
        }
        int maxWidth = 0;
        for (Option option : options) {
            String result = option.getLabel() + this.spaces(4);
            int width = c.getTextRenderer().getWidth(c.getFontContext(), box.getStyle().getFSFont(c), result);
            if (width <= maxWidth) continue;
            maxWidth = width;
        }
        return maxWidth;
    }

    private List readOptions(Element e) {
        ArrayList<Option> result = new ArrayList<Option>();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1 || !n.getNodeName().equalsIgnoreCase("option")) continue;
            Element optionElem = (Element)n;
            String label = this.collectText(optionElem);
            Attr valueAttr = optionElem.getAttributeNode("value");
            String value = valueAttr == null ? label : valueAttr.getValue();
            if (label == null) continue;
            Option option = new Option();
            option.setLabel(label);
            option.setValue(value);
            if (this.isSelected(optionElem)) {
                option.setSelected(true);
            }
            result.add(option);
        }
        return result;
    }

    private String collectText(Element e) {
        StringBuffer result = new StringBuffer();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            short nodeType = n.getNodeType();
            if (nodeType != 3 && nodeType != 4) continue;
            Text t = (Text)n;
            result.append(t.getData());
        }
        return result.length() > 0 ? result.toString() : null;
    }

    @Override
    protected void initDimensions(LayoutContext c, BlockBox box, int cssWidth, int cssHeight) {
        if (cssWidth != -1) {
            this.setWidth(cssWidth);
        } else {
            this.setWidth(this.calcDefaultWidth(c, box));
        }
        if (cssHeight != -1) {
            this.setHeight(cssHeight);
        } else {
            this.setHeight((int)(box.getStyle().getLineHeight(c) * (float)this.getSize(box.getElement())));
        }
    }

    private int getSize(Element elem) {
        int result = 1;
        try {
            int i;
            String v = elem.getAttribute("size").trim();
            if (v.length() > 0 && (i = Integer.parseInt(v)) > 1) {
                result = i;
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return result;
    }

    protected boolean isMultiple(Element e) {
        return !Util.isNullOrEmpty(e.getAttribute("multiple"));
    }

    @Override
    protected String getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public void paint(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box) {
        PdfWriter writer = outputDevice.getWriter();
        String[][] options = this.getPDFOptions();
        int selectedIndex = this.getSelectedIndex();
        PdfFormField field = PdfFormField.createCombo(writer, false, options, selectedIndex);
        field.setWidget(outputDevice.createLocalTargetArea(c, box), PdfAnnotation.HIGHLIGHT_INVERT);
        field.setFieldName(this.getFieldName(outputDevice, box.getElement()));
        if (options.length > 0) {
            field.setValueAsString(options[selectedIndex][0]);
        }
        this.createAppearance(c, outputDevice, box, field);
        if (this.isReadOnly(box.getElement())) {
            field.setFieldFlags(1);
        }
        writer.addAnnotation(field);
    }

    private void createAppearance(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box, PdfFormField field) {
        PdfWriter writer = outputDevice.getWriter();
        ITextFSFont font = (ITextFSFont)box.getStyle().getFSFont(c);
        PdfContentByte cb = writer.getDirectContent();
        float width = outputDevice.getDeviceLength(this.getWidth());
        float height = outputDevice.getDeviceLength(this.getHeight());
        float fontSize = outputDevice.getDeviceLength(font.getSize2D());
        PdfAppearance tp = cb.createAppearance(width, height);
        tp.setFontAndSize(font.getFontDescription().getFont(), fontSize);
        FSColor color = box.getStyle().getColor();
        this.setFillColor(tp, color);
        field.setDefaultAppearanceString(tp);
    }

    @Override
    public int getBaseline() {
        return this._baseline;
    }

    @Override
    public boolean hasBaseline() {
        return true;
    }

    private static final class Option {
        private String _value;
        private String _label;
        private boolean _selected;

        private Option() {
        }

        public String getValue() {
            return this._value;
        }

        public void setValue(String value) {
            this._value = value;
        }

        public String getLabel() {
            return this._label;
        }

        public void setLabel(String label) {
            this._label = label;
        }

        public boolean isSelected() {
            return this._selected;
        }

        public void setSelected(boolean selected) {
            this._selected = selected;
        }
    }
}

