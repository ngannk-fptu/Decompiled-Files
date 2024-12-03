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
import org.apache.struts2.components.Script;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class ScriptTag
extends AbstractUITag {
    protected String async;
    protected String charset;
    protected String defer;
    protected String src;
    protected String type;
    protected String referrerpolicy;
    protected String nomodule;
    protected String integrity;
    protected String crossorigin;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Script(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Script script = (Script)this.component;
        script.setAsync(this.async);
        script.setCharset(this.charset);
        script.setDefer(this.defer);
        script.setSrc(this.src);
        script.setType(this.type);
        script.setReferrerpolicy(this.referrerpolicy);
        script.setNomodule(this.nomodule);
        script.setIntegrity(this.integrity);
        script.setCrossorigin(this.crossorigin);
    }

    public void setAsync(String async) {
        this.async = async;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setDefer(String defer) {
        this.defer = defer;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    public void setNomodule(String nomodule) {
        this.nomodule = nomodule;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }
}

