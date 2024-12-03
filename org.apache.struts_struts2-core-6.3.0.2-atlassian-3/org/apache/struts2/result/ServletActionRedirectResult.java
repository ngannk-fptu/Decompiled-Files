/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import java.util.Arrays;
import java.util.List;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.result.Redirectable;
import org.apache.struts2.result.ServletRedirectResult;

public class ServletActionRedirectResult
extends ServletRedirectResult
implements ReflectionExceptionHandler,
Redirectable {
    private static final long serialVersionUID = -9042425229314584066L;
    public static final String DEFAULT_PARAM = "actionName";
    protected String actionName;
    protected String namespace;
    protected String method;

    public ServletActionRedirectResult() {
    }

    public ServletActionRedirectResult(String actionName) {
        this(null, actionName, null, null);
    }

    public ServletActionRedirectResult(String actionName, String method) {
        this(null, actionName, method, null);
    }

    public ServletActionRedirectResult(String namespace, String actionName, String method) {
        this(namespace, actionName, method, null);
    }

    public ServletActionRedirectResult(String namespace, String actionName, String method, String anchor) {
        super(null, anchor);
        this.namespace = namespace;
        this.actionName = actionName;
        this.method = method;
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        if (invocation == null) {
            throw new IllegalArgumentException("Invocation cannot be null!");
        }
        this.actionName = this.conditionalParse(this.actionName, invocation);
        this.parseLocation = false;
        this.namespace = this.namespace == null ? invocation.getProxy().getNamespace() : this.conditionalParse(this.namespace, invocation);
        this.method = this.method == null ? "" : this.conditionalParse(this.method, invocation);
        String tmpLocation = this.actionMapper.getUriFromActionMapping(new ActionMapping(this.actionName, this.namespace, this.method, null));
        this.setLocation(tmpLocation);
        super.execute(invocation);
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    protected List<String> getProhibitedResultParams() {
        return Arrays.asList(DEFAULT_PARAM, "namespace", "method", "encode", "parse", "location", "prependServletContext", "suppressEmptyParameters", "anchor", "statusCode");
    }
}

