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

public class UseCachedTag
extends TagSupport {
    boolean use = true;
    static /* synthetic */ Class class$com$opensymphony$oscache$web$tag$CacheTag;

    public void setUse(boolean value) {
        this.use = value;
    }

    public int doStartTag() throws JspTagException {
        CacheTag cacheTag = (CacheTag)TagSupport.findAncestorWithClass((Tag)this, (Class)(class$com$opensymphony$oscache$web$tag$CacheTag == null ? (class$com$opensymphony$oscache$web$tag$CacheTag = UseCachedTag.class$("com.opensymphony.oscache.web.tag.CacheTag")) : class$com$opensymphony$oscache$web$tag$CacheTag));
        if (cacheTag == null) {
            throw new JspTagException("A UseCached tag must be nested within a Cache tag");
        }
        cacheTag.setUseBody(!this.use);
        return 0;
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

