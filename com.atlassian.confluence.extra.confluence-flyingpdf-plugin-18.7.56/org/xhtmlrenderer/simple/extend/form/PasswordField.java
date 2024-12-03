/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.InputField;
import org.xhtmlrenderer.simple.extend.form.SizeLimitedDocument;
import org.xhtmlrenderer.util.GeneralUtil;

class PasswordField
extends InputField {
    public PasswordField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        JPasswordField password = new JPasswordField();
        if (this.hasAttribute("size")) {
            int size = GeneralUtil.parseIntRelaxed(this.getAttribute("size"));
            if (size == 0) {
                password.setColumns(15);
            } else {
                password.setColumns(size);
            }
        } else {
            password.setColumns(15);
        }
        if (this.hasAttribute("maxlength")) {
            password.setDocument(new SizeLimitedDocument(GeneralUtil.parseIntRelaxed(this.getAttribute("maxlength"))));
        }
        if (this.hasAttribute("readonly") && this.getAttribute("readonly").equalsIgnoreCase("readonly")) {
            password.setEditable(false);
        }
        return password;
    }

    @Override
    protected void applyOriginalState() {
        JPasswordField password = (JPasswordField)this.getComponent();
        password.setText(this.getOriginalState().getValue());
    }

    @Override
    protected String[] getFieldValues() {
        JPasswordField textfield = (JPasswordField)this.getComponent();
        return new String[]{new String(textfield.getPassword())};
    }
}

