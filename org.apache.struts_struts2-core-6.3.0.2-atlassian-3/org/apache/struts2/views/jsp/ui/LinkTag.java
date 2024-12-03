/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Link;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class LinkTag
extends AbstractUITag {
    protected String href;
    protected String hreflang;
    protected String rel;
    protected String media;
    protected String referrerpolicy;
    protected String sizes;
    protected String crossorigin;
    protected String type;
    protected String as;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Link(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Link link = (Link)this.component;
        link.setHref(this.href);
        link.setHreflang(this.hreflang);
        link.setRel(this.rel);
        link.setDisabled(this.disabled);
        link.setMedia(this.media);
        link.setReferrerpolicy(this.referrerpolicy);
        link.setSizes(this.sizes);
        link.setCrossorigin(this.crossorigin);
        link.setType(this.type);
        link.setAs(this.as);
        link.setTitle(this.title);
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }
}

