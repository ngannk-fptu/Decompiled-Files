/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.util.ValueStack
 *  org.apache.struts2.views.velocity.result.VelocityResult
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.VelocityEngine
 */
package com.atlassian.xwork.results;

import com.atlassian.util.profiling.UtilTimerStack;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.velocity.result.VelocityResult;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

public class ProfiledVelocityResult
extends VelocityResult {
    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        UtilTimerStack.push((String)("XW View: doExecute(" + finalLocation + ")"));
        try {
            super.doExecute(finalLocation, invocation);
        }
        finally {
            UtilTimerStack.pop((String)("XW View: doExecute(" + finalLocation + ")"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Template getTemplate(ValueStack stack, VelocityEngine velocity, ActionInvocation invocation, String location, String encoding) throws Exception {
        UtilTimerStack.push((String)("XW View: getTemplate(" + location + ")"));
        try {
            Template template = super.getTemplate(stack, velocity, invocation, location, encoding);
            return template;
        }
        finally {
            UtilTimerStack.pop((String)("XW View: getTemplate(" + location + ")"));
        }
    }
}

