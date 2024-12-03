/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;
import org.xhtmlrenderer.simple.extend.form.InputField;

class CheckboxField
extends InputField {
    public CheckboxField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setText("");
        checkbox.setOpaque(false);
        return checkbox;
    }

    @Override
    protected FormFieldState loadOriginalState() {
        return FormFieldState.fromBoolean(this.getAttribute("checked").equalsIgnoreCase("checked"));
    }

    @Override
    protected void applyOriginalState() {
        JToggleButton button = (JToggleButton)this.getComponent();
        button.setSelected(this.getOriginalState().isChecked());
    }

    @Override
    protected String[] getFieldValues() {
        JToggleButton button = (JToggleButton)this.getComponent();
        if (button.isSelected()) {
            return new String[]{this.hasAttribute("value") ? this.getAttribute("value") : "on"};
        }
        return new String[0];
    }
}

