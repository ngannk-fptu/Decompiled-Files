/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.util;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.util.StrutsUtil;

public class ContextUtil {
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    public static final String SESSION = "session";
    public static final String BASE = "base";
    public static final String STACK = "stack";
    public static final String STRUTS = "struts";
    public static final String ACTION = "action";

    public static Map<String, Object> getStandardContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(REQUEST, req);
        map.put(RESPONSE, res);
        map.put(SESSION, req.getSession(false));
        map.put(BASE, req.getContextPath());
        map.put(STACK, stack);
        StrutsUtil util = new StrutsUtil(stack, req, res);
        map.put(STRUTS, util);
        ActionInvocation invocation = stack.getActionContext().getActionInvocation();
        if (invocation != null) {
            map.put(ACTION, invocation.getAction());
        }
        return map;
    }

    public static String getTemplateSuffix(Map<String, Object> context) {
        return context.containsKey("templateSuffix") ? (String)context.get("templateSuffix") : null;
    }
}

