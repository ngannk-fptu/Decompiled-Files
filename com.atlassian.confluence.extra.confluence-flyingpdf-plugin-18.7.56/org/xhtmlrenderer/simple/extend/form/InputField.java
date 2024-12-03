/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;

public abstract class InputField
extends FormField {
    public InputField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public abstract JComponent create();

    @Override
    protected FormFieldState loadOriginalState() {
        return FormFieldState.fromString(this.getAttribute("value"));
    }

    @Override
    protected String[] getFieldValues() {
        return new String[]{this.hasAttribute("value") ? this.getAttribute("value") : ""};
    }
}

