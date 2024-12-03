/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.jsp.JspTagException
 *  javax.servlet.jsp.tagext.TagSupport
 */
package com.opensymphony.oscache.web.tag;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.web.ServletCacheAdministrator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class FlushTag
extends TagSupport {
    ServletCacheAdministrator admin = null;
    String group = null;
    String key = null;
    String pattern = null;
    String scope = null;
    int cacheScope = -1;
    private String language = null;

    public void setGroup(String group) {
        this.group = group;
    }

    public void setKey(String value) {
        this.key = value;
    }

    public void setLanguage(String value) {
        this.language = value;
    }

    public void setPattern(String value) {
        this.pattern = value;
    }

    public void setScope(String value) {
        if (value != null) {
            if (value.equalsIgnoreCase("session")) {
                this.cacheScope = 3;
            } else if (value.equalsIgnoreCase("application")) {
                this.cacheScope = 4;
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public int doStartTag() throws JspTagException {
        if (this.admin == null) {
            this.admin = ServletCacheAdministrator.getInstance(this.pageContext.getServletContext());
        }
        if (this.group != null) {
            if (this.cacheScope < 0) throw new JspTagException("A cache group was specified for flushing, but the scope wasn't supplied or was invalid");
            Cache cache = this.admin.getCache((HttpServletRequest)this.pageContext.getRequest(), this.cacheScope);
            cache.flushGroup(this.group);
            return 0;
        } else if (this.pattern != null) {
            if (this.cacheScope < 0) throw new JspTagException("A pattern was specified for flushing, but the scope wasn't supplied or was invalid");
            Cache cache = this.admin.getCache((HttpServletRequest)this.pageContext.getRequest(), this.cacheScope);
            cache.flushPattern(this.pattern);
            return 0;
        } else if (this.key == null) {
            if (this.cacheScope >= 0) {
                this.admin.setFlushTime(this.cacheScope);
                return 0;
            } else {
                this.admin.flushAll();
            }
            return 0;
        } else {
            if (this.cacheScope < 0) throw new JspTagException("A cache key was specified for flushing, but the scope wasn't supplied or was invalid");
            String actualKey = this.admin.generateEntryKey(this.key, (HttpServletRequest)this.pageContext.getRequest(), this.cacheScope, this.language);
            Cache cache = this.admin.getCache((HttpServletRequest)this.pageContext.getRequest(), this.cacheScope);
            cache.flushEntry(actualKey);
        }
        return 0;
    }
}

