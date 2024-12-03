/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.factory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.DefaultActionProxy;
import java.util.Locale;
import org.apache.struts2.ServletActionContext;

public class StrutsActionProxy
extends DefaultActionProxy {
    private static final long serialVersionUID = -2434901249671934080L;

    public StrutsActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
        super(inv, namespace, actionName, methodName, executeResult, cleanupContext);
    }

    @Override
    public String execute() throws Exception {
        ActionContext previous = ActionContext.getContext();
        ActionContext.bind(this.invocation.getInvocationContext());
        try {
            String string = this.invocation.invoke();
            return string;
        }
        finally {
            if (this.cleanupContext) {
                ActionContext.bind(previous);
            }
        }
    }

    @Override
    protected void prepare() {
        super.prepare();
    }

    @Override
    protected String getErrorMessage() {
        if (this.namespace != null && this.namespace.trim().length() > 0) {
            String contextPath = ServletActionContext.getRequest().getContextPath();
            return this.localizedTextProvider.findDefaultText("struts.exception.missing-package-action.with-context", Locale.getDefault(), new String[]{this.namespace, this.actionName, contextPath});
        }
        return super.getErrorMessage();
    }
}

