/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;
import org.xhtmlrenderer.simple.extend.form.InputField;

class RadioButtonField
extends InputField {
    public RadioButtonField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        JRadioButton radio = new JRadioButton();
        radio.setText("");
        radio.setOpaque(false);
        String groupName = null;
        if (this.hasAttribute("name")) {
            groupName = this.getAttribute("name");
        }
        this.getParentForm().addButtonToGroup(groupName, radio);
        return radio;
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
            return new String[]{this.hasAttribute("value") ? this.getAttribute("value") : ""};
        }
        return new String[0];
    }
}

