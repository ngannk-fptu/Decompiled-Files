/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.Tag
 *  javax.servlet.jsp.tagext.TagSupport
 *  org.springframework.lang.Nullable
 *  org.springframework.web.util.TagUtils
 */
package org.springframework.web.servlet.tags;

import java.beans.PropertyEditor;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.EditorAwareTag;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.util.TagUtils;

public class TransformTag
extends HtmlEscapingAwareTag {
    @Nullable
    private Object value;
    @Nullable
    private String var;
    private String scope = "page";

    public void setValue(Object value) {
        this.value = value;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    protected final int doStartTagInternal() throws JspException {
        if (this.value != null) {
            EditorAwareTag tag = (EditorAwareTag)TagSupport.findAncestorWithClass((Tag)this, EditorAwareTag.class);
            if (tag == null) {
                throw new JspException("TransformTag can only be used within EditorAwareTag (e.g. BindTag)");
            }
            String result = null;
            PropertyEditor editor = tag.getEditor();
            if (editor != null) {
                editor.setValue(this.value);
                result = editor.getAsText();
            } else {
                result = this.value.toString();
            }
            result = this.htmlEscape(result);
            if (this.var != null) {
                this.pageContext.setAttribute(this.var, (Object)result, TagUtils.getScope((String)this.scope));
            } else {
                try {
                    this.pageContext.getOut().print(result);
                }
                catch (IOException ex) {
                    throw new JspException((Throwable)ex);
                }
            }
        }
        return 0;
    }
}

