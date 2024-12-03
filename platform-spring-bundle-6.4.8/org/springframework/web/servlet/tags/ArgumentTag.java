/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyTagSupport
 *  javax.servlet.jsp.tagext.Tag
 */
package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.ArgumentAware;

public class ArgumentTag
extends BodyTagSupport {
    @Nullable
    private Object value;
    private boolean valueSet;

    public void setValue(Object value) {
        this.value = value;
        this.valueSet = true;
    }

    public int doEndTag() throws JspException {
        Object argument = null;
        if (this.valueSet) {
            argument = this.value;
        } else if (this.getBodyContent() != null) {
            argument = this.getBodyContent().getString().trim();
        }
        ArgumentAware argumentAwareTag = (ArgumentAware)ArgumentTag.findAncestorWithClass((Tag)this, ArgumentAware.class);
        if (argumentAwareTag == null) {
            throw new JspException("The argument tag must be a descendant of a tag that supports arguments");
        }
        argumentAwareTag.addArgument(argument);
        return 6;
    }

    public void release() {
        super.release();
        this.value = null;
        this.valueSet = false;
    }
}

