/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.struts.action.ActionForm
 */
package org.apache.velocity.tools.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.struts.StrutsUtils;
import org.apache.velocity.tools.view.ViewContext;

@DefaultKey(value="form")
@ValidScope(value={"request"})
public class FormTool {
    protected HttpServletRequest request;
    protected HttpSession session;

    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            this.setRequest(((ViewContext)obj).getRequest());
        }
    }

    public void setRequest(HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("request should not be null");
        }
        this.request = request;
        this.session = request.getSession(false);
    }

    public ActionForm getBean() {
        return StrutsUtils.getActionForm(this.request, this.session);
    }

    public String getName() {
        return StrutsUtils.getActionFormName(this.request, this.session);
    }

    public String getCancelName() {
        return "org.apache.struts.taglib.html.CANCEL";
    }

    public String getToken() {
        return StrutsUtils.getToken(this.session);
    }

    public String getTokenName() {
        return "org.apache.struts.taglib.html.TOKEN";
    }
}

