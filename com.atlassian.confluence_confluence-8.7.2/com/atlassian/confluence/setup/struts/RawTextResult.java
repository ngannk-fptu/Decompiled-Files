/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.result.StrutsResultSupport
 */
package com.atlassian.confluence.setup.struts;

import com.opensymphony.xwork2.ActionInvocation;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.result.StrutsResultSupport;

public class RawTextResult
extends StrutsResultSupport {
    protected void doExecute(String finalLocation, ActionInvocation actionInvocation) throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        response.getWriter().write(actionInvocation.getResultCode());
        response.flushBuffer();
    }
}

