/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspTagException
 *  javax.servlet.jsp.tagext.Tag
 *  javax.servlet.jsp.tagext.TagSupport
 */
package com.opensymphony.oscache.web.tag;

import com.opensymphony.oscache.web.tag.CacheTag;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public class GroupTag
extends TagSupport {
    private Object group = null;
    static /* synthetic */ Class class$com$opensymphony$oscache$web$tag$CacheTag;

    public int doStartTag() throws JspTagException {
        CacheTag ancestorCacheTag = (CacheTag)TagSupport.findAncestorWithClass((Tag)this, (Class)(class$com$opensymphony$oscache$web$tag$CacheTag == null ? (class$com$opensymphony$oscache$web$tag$CacheTag = GroupTag.class$("com.opensymphony.oscache.web.tag.CacheTag")) : class$com$opensymphony$oscache$web$tag$CacheTag));
        if (ancestorCacheTag == null) {
            throw new JspTagException("GroupTag cannot be used from outside a CacheTag");
        }
        ancestorCacheTag.addGroup(String.valueOf(this.group));
        return 1;
    }

    public void setGroup(Object group) {
        this.group = group;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

