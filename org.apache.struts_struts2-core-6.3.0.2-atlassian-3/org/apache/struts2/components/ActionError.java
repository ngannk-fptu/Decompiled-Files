/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="actionerror", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.ui.ActionErrorTag", description="Render action errors if they exists")
public class ActionError
extends UIBean {
    public static final String TEMPLATE = "actionerror";
    private boolean escape = true;

    public ActionError(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        boolean isEmptyList = true;
        List actionMessages = (List)this.findValue("actionErrors");
        if (actionMessages != null) {
            for (String message : actionMessages) {
                if (!StringUtils.isNotBlank((CharSequence)message)) continue;
                isEmptyList = false;
                break;
            }
        }
        this.addParameter("isEmptyList", isEmptyList);
        this.addParameter("escape", this.escape);
    }

    @StrutsTagAttribute(description=" Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}

