/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyTagSupport
 *  javax.servlet.jsp.tagext.Tag
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.Param;
import org.springframework.web.servlet.tags.ParamAware;

public class ParamTag
extends BodyTagSupport {
    private String name = "";
    @Nullable
    private String value;
    private boolean valueSet;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
        this.valueSet = true;
    }

    public int doEndTag() throws JspException {
        Param param = new Param();
        param.setName(this.name);
        if (this.valueSet) {
            param.setValue(this.value);
        } else if (this.getBodyContent() != null) {
            param.setValue(this.getBodyContent().getString().trim());
        }
        ParamAware paramAwareTag = (ParamAware)ParamTag.findAncestorWithClass((Tag)this, ParamAware.class);
        if (paramAwareTag == null) {
            throw new JspException("The param tag must be a descendant of a tag that supports parameters");
        }
        paramAwareTag.addParam(param);
        return 6;
    }

    public void release() {
        super.release();
        this.name = "";
        this.value = null;
        this.valueSet = false;
    }
}

