/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.ValidationAware
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.result.StrutsResultSupport
 */
package com.atlassian.confluence.json;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.result.StrutsResultSupport;

@Deprecated
public class JsonResult
extends StrutsResultSupport {
    private Jsonator<Object> jsonator;

    public JsonResult() {
        this.setLocation("");
    }

    protected void doExecute(String finalDestination, ActionInvocation actionInvocation) throws Exception {
        String jsonString = this.getJsonString(actionInvocation);
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print(jsonString);
        writer.flush();
    }

    private Jsonator<Object> getJsonator() {
        if (this.jsonator == null) {
            this.jsonator = (Jsonator)ContainerManager.getComponent((String)"jsonator");
        }
        return this.jsonator;
    }

    public void setJsonator(Jsonator<Object> jsonator) {
        this.jsonator = jsonator;
    }

    public String getJsonString(ActionInvocation actionInvocation) {
        String jsonString;
        ValidationAware validationAware;
        Action action = (Action)actionInvocation.getAction();
        if (action instanceof ValidationAware && (validationAware = (ValidationAware)action).hasErrors()) {
            Json json = this.getJsonator().convert(action);
            String jsonString2 = json.serialize();
            return jsonString2;
        }
        if (action instanceof Beanable) {
            Beanable beanable = (Beanable)action;
            Object bean = beanable.getBean();
            Json json = this.getJsonator().convert(bean);
            jsonString = json.serialize();
        } else {
            ValueStack stack = actionInvocation.getStack();
            jsonString = (String)stack.findValue("JSONString");
        }
        return jsonString;
    }
}

