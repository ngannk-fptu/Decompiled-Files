/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

public class ActionChainResult
implements Result {
    private static final Logger LOG = LogManager.getLogger(ActionChainResult.class);
    public static final String DEFAULT_PARAM = "actionName";
    private static final String CHAIN_HISTORY = "CHAIN_HISTORY";
    private ActionProxy proxy;
    private String actionName;
    private String namespace;
    private String methodName;
    private String skipActions;
    private ActionProxyFactory actionProxyFactory;

    public ActionChainResult() {
    }

    public ActionChainResult(String namespace, String actionName, String methodName) {
        this.namespace = namespace;
        this.actionName = actionName;
        this.methodName = methodName;
    }

    public ActionChainResult(String namespace, String actionName, String methodName, String skipActions) {
        this.namespace = namespace;
        this.actionName = actionName;
        this.methodName = methodName;
        this.skipActions = skipActions;
    }

    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setSkipActions(String actions) {
        this.skipActions = actions;
    }

    public void setMethod(String method) {
        this.methodName = method;
    }

    public ActionProxy getProxy() {
        return this.proxy;
    }

    public static LinkedList<String> getChainHistory() {
        LinkedList chainHistory = (LinkedList)ActionContext.getContext().get(CHAIN_HISTORY);
        if (chainHistory == null) {
            chainHistory = new LinkedList();
            ActionContext.getContext().put(CHAIN_HISTORY, chainHistory);
        }
        return chainHistory;
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        String finalMethodName;
        if (invocation == null) {
            throw new IllegalArgumentException("Invocation cannot be null!");
        }
        String finalNamespace = this.namespace != null ? this.translateVariables(this.namespace) : invocation.getProxy().getNamespace();
        String finalActionName = this.translateVariables(this.actionName);
        String string = finalMethodName = this.methodName != null ? this.translateVariables(this.methodName) : null;
        if (this.isInChainHistory(finalNamespace, finalActionName, finalMethodName)) {
            this.addToHistory(finalNamespace, finalActionName, finalMethodName);
            throw new StrutsException("Infinite recursion detected: " + ActionChainResult.getChainHistory());
        }
        if (ActionChainResult.getChainHistory().isEmpty() && invocation.getProxy() != null) {
            this.addToHistory(finalNamespace, invocation.getProxy().getActionName(), invocation.getProxy().getMethod());
        }
        this.addToHistory(finalNamespace, finalActionName, finalMethodName);
        Map<String, Object> extraContext = ActionContext.of().withValueStack(invocation.getInvocationContext().getValueStack()).withParameters(invocation.getInvocationContext().getParameters()).with(CHAIN_HISTORY, ActionChainResult.getChainHistory()).getContextMap();
        LOG.debug("Chaining to action {}", (Object)finalActionName);
        this.proxy = this.actionProxyFactory.createActionProxy(finalNamespace, finalActionName, finalMethodName, extraContext);
        this.proxy.execute();
    }

    protected String translateVariables(String text) {
        return TextParseUtil.translateVariables(text, ActionContext.getContext().getValueStack());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ActionChainResult that = (ActionChainResult)o;
        return Objects.equals(this.actionName, that.actionName) && Objects.equals(this.methodName, that.methodName) && Objects.equals(this.namespace, that.namespace);
    }

    public int hashCode() {
        int result = this.actionName != null ? this.actionName.hashCode() : 0;
        result = 31 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
        result = 31 * result + (this.methodName != null ? this.methodName.hashCode() : 0);
        return result;
    }

    private boolean isInChainHistory(String namespace, String actionName, String methodName) {
        LinkedList<String> chainHistory = ActionChainResult.getChainHistory();
        HashSet<String> skipActionsList = new HashSet<String>();
        if (this.skipActions != null && this.skipActions.length() > 0) {
            String finalSkipActions = this.translateVariables(this.skipActions);
            skipActionsList.addAll(TextParseUtil.commaDelimitedStringToSet(finalSkipActions));
        }
        if (!skipActionsList.contains(actionName)) {
            return chainHistory.contains(this.makeKey(namespace, actionName, methodName));
        }
        return false;
    }

    private void addToHistory(String namespace, String actionName, String methodName) {
        LinkedList<String> chainHistory = ActionChainResult.getChainHistory();
        chainHistory.add(this.makeKey(namespace, actionName, methodName));
    }

    private String makeKey(String namespace, String actionName, String methodName) {
        return namespace + "/" + actionName + (methodName != null ? "!" + methodName : "");
    }
}

