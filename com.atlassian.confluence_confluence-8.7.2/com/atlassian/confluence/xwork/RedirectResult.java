/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  org.apache.struts2.result.ServletRedirectResult
 */
package com.atlassian.confluence.xwork;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.result.ServletRedirectResult;

public class RedirectResult
extends ServletRedirectResult {
    protected void doExecute(String s, ActionInvocation actionInvocation) throws Exception {
        int indexOfHash = s.lastIndexOf(35);
        int indexOfParams = s.indexOf("?");
        if (indexOfParams != -1 && indexOfHash != -1) {
            s = new StringBuilder(s).insert(indexOfHash, "&").toString();
        }
        super.doExecute(s, actionInvocation);
    }
}

