/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public class DefaultActionSupport
extends ActionSupport {
    private static final long serialVersionUID = -2426166391283746095L;
    private String successResultValue;

    @Override
    public String execute() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String requestedUrl = request.getPathInfo();
        if (this.successResultValue == null) {
            this.successResultValue = requestedUrl;
        }
        return "success";
    }

    public String getSuccessResultValue() {
        return this.successResultValue;
    }

    public void setSuccessResultValue(String successResultValue) {
        this.successResultValue = successResultValue;
    }
}

