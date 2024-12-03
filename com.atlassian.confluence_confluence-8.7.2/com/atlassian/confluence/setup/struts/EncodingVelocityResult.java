/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.results.ProfiledVelocityResult
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.setup.struts;

import com.atlassian.confluence.setup.struts.OutputAwareStrutsVelocityContext;
import com.atlassian.confluence.themes.ThemeContext;
import com.atlassian.xwork.results.ProfiledVelocityResult;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.context.Context;

public class EncodingVelocityResult
extends ProfiledVelocityResult {
    protected Context createContext(VelocityManager velocityManager, ValueStack stack, HttpServletRequest request, HttpServletResponse response, String location) {
        Context ctx = super.createContext(velocityManager, stack, request, response, location);
        if (ctx instanceof OutputAwareStrutsVelocityContext) {
            OutputAwareStrutsVelocityContext outputAwareContext = (OutputAwareStrutsVelocityContext)ctx;
            outputAwareContext.setOutputMimeType(this.getContentType(location));
        }
        return ctx;
    }

    public void doExecute(String location, ActionInvocation invocation) throws Exception {
        ThemeContext themeContext = ThemeContext.get((ServletRequest)ServletActionContext.getRequest());
        if (themeContext.getAppliedTheme() != null) {
            location = themeContext.getAppliedTheme().getXworkVelocityPath(invocation.getProxy().getConfig().getPackageName(), invocation.getProxy().getActionName(), invocation.getResultCode(), location);
        }
        super.doExecute(location, invocation);
    }
}

