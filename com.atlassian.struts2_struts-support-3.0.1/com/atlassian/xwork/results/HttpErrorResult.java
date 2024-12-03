/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.Result
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.xwork.results;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import org.apache.struts2.ServletActionContext;

public class HttpErrorResult
implements Result {
    private int errorCode;
    private String errorMessage;

    public void execute(ActionInvocation invocation) throws Exception {
        ServletActionContext.getResponse().sendError(this.errorCode, this.errorMessage);
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

