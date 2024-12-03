/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.BodyTagSupport
 *  javax.servlet.jsp.tagext.Tag
 */
package com.opensymphony.module.sitemesh.taglib.page;

import com.opensymphony.module.sitemesh.taglib.page.ApplyDecoratorTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

public class ParamTag
extends BodyTagSupport {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public int doAfterBody() {
        Tag parent = ParamTag.findAncestorWithClass((Tag)this, ApplyDecoratorTag.class);
        if (parent instanceof ApplyDecoratorTag) {
            ApplyDecoratorTag t = (ApplyDecoratorTag)parent;
            t.addParam(this.name, this.getBodyContent().getString());
        }
        return 0;
    }
}

