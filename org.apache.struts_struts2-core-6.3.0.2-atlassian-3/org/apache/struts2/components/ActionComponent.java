/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.PageContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="action", tldTagClass="org.apache.struts2.views.jsp.ActionTag", description="Execute an action from within a view")
public class ActionComponent
extends ContextBean {
    private static final Logger LOG = LogManager.getLogger(ActionComponent.class);
    protected HttpServletResponse res;
    protected HttpServletRequest req;
    protected ValueStackFactory valueStackFactory;
    protected ActionProxyFactory actionProxyFactory;
    protected ActionProxy proxy;
    protected String name;
    protected String namespace;
    protected boolean executeResult;
    protected boolean ignoreContextParams;
    protected boolean flush = true;
    protected boolean rethrowException;

    public ActionComponent(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
    }

    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Override
    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean end(Writer writer, String body) {
        boolean end = super.end(writer, "", false);
        try {
            if (this.flush) {
                try {
                    writer.flush();
                }
                catch (IOException e) {
                    LOG.warn("error while trying to flush writer ", (Throwable)e);
                }
            }
            this.executeAction();
            if (this.getVar() != null && this.proxy != null) {
                this.getStack().setValue("#attr['" + this.getVar() + "']", this.proxy.getAction());
            }
        }
        finally {
            this.popComponentStack();
        }
        return end;
    }

    protected Map<String, Object> createExtraContext() {
        HttpParameters newParams = this.createParametersForContext();
        ActionContext ctx = this.stack.getActionContext();
        PageContext pageContext = ctx.getPageContext();
        Map<String, Object> session = ctx.getSession();
        Map<String, Object> application = ctx.getApplication();
        Dispatcher du = Dispatcher.getInstance();
        Map<String, Object> extraContext = du.createContextMap(new RequestMap(this.req), newParams, session, application, this.req, this.res);
        ValueStack newStack = this.valueStackFactory.createValueStack(this.stack);
        return ActionContext.of(extraContext).withValueStack(newStack).withPageContext(pageContext).getContextMap();
    }

    protected HttpParameters createParametersForContext() {
        HttpParameters parentParams = null;
        if (!this.ignoreContextParams) {
            parentParams = this.getStack().getActionContext().getParameters();
        }
        HttpParameters.Builder builder = HttpParameters.create().withParent(parentParams);
        if (this.parameters != null) {
            builder = builder.withExtraParams(this.parameters);
        }
        return builder.build();
    }

    public ActionProxy getProxy() {
        return this.proxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void executeAction() {
        String actualName = this.findString(this.name, "name", "Action name is required. Example: updatePerson");
        if (actualName == null) {
            throw new StrutsException("Unable to find value for name " + this.name);
        }
        ActionMapping mapping = this.actionMapper.getMappingFromActionName(actualName);
        String actionName = mapping.getName();
        String methodName = mapping.getMethod();
        String namespace = this.namespace == null ? this.getNamespace(this.getStack()) : this.findString(this.namespace);
        ValueStack stack = this.getStack();
        ActionInvocation inv = ActionContext.getContext().getActionInvocation();
        try {
            this.proxy = this.actionProxyFactory.createActionProxy(namespace, actionName, methodName, this.createExtraContext(), this.executeResult, true);
            this.req.setAttribute("struts.valueStack", (Object)this.proxy.getInvocation().getStack());
            this.req.setAttribute("struts.actiontag.invocation", (Object)Boolean.TRUE);
            this.proxy.execute();
        }
        catch (Exception e) {
            String message = "Could not execute action: " + namespace + "/" + actualName;
            LOG.error(message, (Throwable)e);
            if (this.rethrowException) {
                throw new StrutsException(message, e);
            }
        }
        finally {
            this.req.removeAttribute("struts.actiontag.invocation");
            this.req.setAttribute("struts.valueStack", (Object)stack);
            if (inv != null) {
                ActionContext.getContext().withActionInvocation(inv);
            }
        }
        if (this.getVar() != null && this.proxy != null) {
            this.putInContext(this.proxy.getAction());
        }
    }

    @StrutsTagAttribute(required=true, description="Name of the action to be executed (without the extension suffix eg. .action)")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="Namespace for action to call", defaultValue="namespace from where tag is used")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @StrutsTagAttribute(description="Whether the result of this action (probably a view) should be executed/rendered", type="Boolean", defaultValue="false")
    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    @StrutsTagAttribute(description="Whether the request parameters are to be included when the action is invoked", type="Boolean", defaultValue="false")
    public void setIgnoreContextParams(boolean ignoreContextParams) {
        this.ignoreContextParams = ignoreContextParams;
    }

    @StrutsTagAttribute(description="Whether the writer should be flush upon end of action component tag, default to true", type="Boolean", defaultValue="true")
    public void setFlush(boolean flush) {
        this.flush = flush;
    }

    @StrutsTagAttribute(description="Whether an exception should be rethrown, if the target action throws an exception", type="Boolean", defaultValue="false")
    public void setRethrowException(boolean rethrowException) {
        this.rethrowException = rethrowException;
    }
}

