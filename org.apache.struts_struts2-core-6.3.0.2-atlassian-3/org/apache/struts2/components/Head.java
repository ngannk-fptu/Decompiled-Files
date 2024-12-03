/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTag;

@StrutsTag(name="head", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.ui.HeadTag", description="Render a chunk of HEAD for your HTML file", allowDynamicAttributes=true)
public class Head
extends UIBean {
    public static final String TEMPLATE = "head";
    private String encoding;

    public Head(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Inject(value="struts.i18n.encoding")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public void evaluateParams() {
        super.evaluateParams();
        this.addParameter("encoding", this.encoding);
    }
}

