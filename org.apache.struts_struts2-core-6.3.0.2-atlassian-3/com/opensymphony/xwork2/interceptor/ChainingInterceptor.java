/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.Unchainable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ProxyUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChainingInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(ChainingInterceptor.class);
    private static final String ACTION_ERRORS = "actionErrors";
    private static final String FIELD_ERRORS = "fieldErrors";
    private static final String ACTION_MESSAGES = "actionMessages";
    private boolean copyMessages = false;
    private boolean copyErrors = false;
    private boolean copyFieldErrors = false;
    protected Collection<String> excludes;
    protected Collection<String> includes;
    protected ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    @Inject(value="struts.chaining.copyErrors", required=false)
    public void setCopyErrors(String copyErrors) {
        this.copyErrors = "true".equalsIgnoreCase(copyErrors);
    }

    @Inject(value="struts.chaining.copyFieldErrors", required=false)
    public void setCopyFieldErrors(String copyFieldErrors) {
        this.copyFieldErrors = "true".equalsIgnoreCase(copyFieldErrors);
    }

    @Inject(value="struts.chaining.copyMessages", required=false)
    public void setCopyMessages(String copyMessages) {
        this.copyMessages = "true".equalsIgnoreCase(copyMessages);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ValueStack stack = invocation.getStack();
        CompoundRoot root = stack.getRoot();
        if (this.shouldCopyStack(invocation, root)) {
            this.copyStack(invocation, root);
        }
        return invocation.invoke();
    }

    private void copyStack(ActionInvocation invocation, CompoundRoot root) {
        List list = this.prepareList(root);
        Map<String, Object> ctxMap = invocation.getInvocationContext().getContextMap();
        for (Object object : list) {
            if (!this.shouldCopy(object)) continue;
            Object action = invocation.getAction();
            Class<?> editable = null;
            if (ProxyUtil.isProxy(action)) {
                editable = ProxyUtil.ultimateTargetClass(action);
            }
            this.reflectionProvider.copy(object, action, ctxMap, this.prepareExcludes(), this.includes, editable);
        }
    }

    private Collection<String> prepareExcludes() {
        Collection<String> localExcludes = this.excludes;
        if (!(this.copyErrors && this.copyMessages && this.copyFieldErrors || localExcludes != null)) {
            localExcludes = new HashSet<String>();
            if (!this.copyErrors) {
                localExcludes.add(ACTION_ERRORS);
            }
            if (!this.copyMessages) {
                localExcludes.add(ACTION_MESSAGES);
            }
            if (!this.copyFieldErrors) {
                localExcludes.add(FIELD_ERRORS);
            }
        }
        return localExcludes;
    }

    private boolean shouldCopy(Object o) {
        return o != null && !(o instanceof Unchainable);
    }

    private List prepareList(CompoundRoot root) {
        ArrayList<Object> list = new ArrayList<Object>(root);
        list.remove(0);
        Collections.reverse(list);
        return list;
    }

    private boolean shouldCopyStack(ActionInvocation invocation, CompoundRoot root) throws Exception {
        Result result = invocation.getResult();
        return root.size() > 1 && (result == null || ActionChainResult.class.isAssignableFrom(result.getClass()));
    }

    public Collection<String> getExcludes() {
        return this.excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = TextParseUtil.commaDelimitedStringToSet(excludes);
    }

    public void setExcludesCollection(Collection<String> excludes) {
        this.excludes = excludes;
    }

    public Collection<String> getIncludes() {
        return this.includes;
    }

    public void setIncludes(String includes) {
        this.includes = TextParseUtil.commaDelimitedStringToSet(includes);
    }

    public void setIncludesCollection(Collection<String> includes) {
        this.includes = includes;
    }
}

