/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.AbstractButtonField;

class ButtonField
extends AbstractButtonField {
    public ButtonField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        JButton button = new JButton();
        String value = this.getAttribute("value");
        if (value == null || value.length() == 0) {
            value = " ";
        }
        this.applyComponentStyle(button);
        button.setText(value);
        button.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "<input type=\"button\" .../> doesn't make much sense without <script>! (Volunteers wanted)", "We need <script> support!", 1);
            }
        });
        return button;
    }

    @Override
    public boolean includeInSubmission(JComponent source) {
        return false;
    }
}

