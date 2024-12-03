/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.opensymphony.xwork2.ActionInvocation
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.result.StrutsResultSupport
 */
package com.atlassian.confluence.json;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.json.json.JsonObject;
import com.opensymphony.xwork2.ActionInvocation;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.result.StrutsResultSupport;

@Deprecated
public class ReadOnlyJsonResult
extends StrutsResultSupport {
    public ReadOnlyJsonResult() {
        this.setLocation("");
    }

    protected void doExecute(String finalDestination, ActionInvocation actionInvocation) throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setStatus(405);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        JsonObject result = new JsonObject();
        result.setProperty("reason", AccessMode.READ_ONLY.name());
        writer.print(result.serialize());
        writer.flush();
    }
}

