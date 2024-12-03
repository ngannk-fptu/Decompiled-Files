/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.util.ContainUtil;
import org.apache.struts2.views.annotations.StrutsTag;

@StrutsTag(name="component", tldTagClass="org.apache.struts2.views.jsp.ui.ComponentTag", description="Render a custom ui widget")
public class GenericUIBean
extends UIBean {
    private static final String TEMPLATE = "empty";

    public GenericUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public boolean contains(Object obj1, Object obj2) {
        return ContainUtil.contains(obj1, obj2);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}

