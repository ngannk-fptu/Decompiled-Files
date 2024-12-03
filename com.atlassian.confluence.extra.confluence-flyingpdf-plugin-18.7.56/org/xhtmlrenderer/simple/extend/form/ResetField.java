/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.AbstractButtonField;
import org.xhtmlrenderer.util.XRLog;

class ResetField
extends AbstractButtonField {
    public ResetField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        String value;
        JButton button = new JButton();
        if (this.hasAttribute("value")) {
            value = this.getAttribute("value");
            if (value.length() == 0) {
                value = " ";
            }
        } else {
            value = "Reset";
        }
        this.applyComponentStyle(button);
        button.setText(value);
        button.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent event) {
                XRLog.layout("Reset pressed: Restore");
                ResetField.this.getParentForm().reset();
            }
        });
        return button;
    }

    @Override
    public boolean includeInSubmission(JComponent source) {
        return false;
    }

    @Override
    protected String[] getFieldValues() {
        return new String[]{this.hasAttribute("value") ? this.getAttribute("value") : "Reset"};
    }
}

