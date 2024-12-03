/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.ButtonField;
import org.xhtmlrenderer.simple.extend.form.CheckboxField;
import org.xhtmlrenderer.simple.extend.form.FileField;
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.simple.extend.form.HiddenField;
import org.xhtmlrenderer.simple.extend.form.ImageField;
import org.xhtmlrenderer.simple.extend.form.PasswordField;
import org.xhtmlrenderer.simple.extend.form.RadioButtonField;
import org.xhtmlrenderer.simple.extend.form.ResetField;
import org.xhtmlrenderer.simple.extend.form.SelectField;
import org.xhtmlrenderer.simple.extend.form.SubmitField;
import org.xhtmlrenderer.simple.extend.form.TextAreaField;
import org.xhtmlrenderer.simple.extend.form.TextField;

public class FormFieldFactory {
    private FormFieldFactory() {
    }

    public static FormField create(XhtmlForm form, LayoutContext context, BlockBox box) {
        String typeKey = null;
        Element e = box.getElement();
        if (e.getNodeName().equals("input")) {
            typeKey = e.getAttribute("type");
        } else if (e.getNodeName().equals("textarea")) {
            typeKey = "textarea";
        } else if (e.getNodeName().equals("select")) {
            typeKey = "select";
        } else {
            return null;
        }
        if (typeKey.equals("submit")) {
            return new SubmitField(e, form, context, box);
        }
        if (typeKey.equals("reset")) {
            return new ResetField(e, form, context, box);
        }
        if (typeKey.equals("button")) {
            return new ButtonField(e, form, context, box);
        }
        if (typeKey.equals("image")) {
            return new ImageField(e, form, context, box);
        }
        if (typeKey.equals("hidden")) {
            return new HiddenField(e, form, context, box);
        }
        if (typeKey.equals("password")) {
            return new PasswordField(e, form, context, box);
        }
        if (typeKey.equals("checkbox")) {
            return new CheckboxField(e, form, context, box);
        }
        if (typeKey.equals("radio")) {
            return new RadioButtonField(e, form, context, box);
        }
        if (typeKey.equals("file")) {
            return new FileField(e, form, context, box);
        }
        if (typeKey.equals("textarea")) {
            return new TextAreaField(e, form, context, box);
        }
        if (typeKey.equals("select")) {
            return new SelectField(e, form, context, box);
        }
        return new TextField(e, form, context, box);
    }
}

