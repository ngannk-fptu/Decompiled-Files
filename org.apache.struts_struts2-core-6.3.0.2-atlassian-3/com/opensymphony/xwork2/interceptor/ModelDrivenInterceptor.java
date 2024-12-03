/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;

public class ModelDrivenInterceptor
extends AbstractInterceptor {
    protected boolean refreshModelBeforeResult = false;

    public void setRefreshModelBeforeResult(boolean val) {
        this.refreshModelBeforeResult = val;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (action instanceof ModelDriven) {
            ModelDriven modelDriven = (ModelDriven)action;
            ValueStack stack = invocation.getStack();
            Object model = modelDriven.getModel();
            if (model != null) {
                stack.push(model);
            }
            if (this.refreshModelBeforeResult) {
                invocation.addPreResultListener(new RefreshModelBeforeResult(modelDriven, model));
            }
        }
        return invocation.invoke();
    }

    protected static class RefreshModelBeforeResult
    implements PreResultListener {
        private Object originalModel;
        protected ModelDriven action;

        public RefreshModelBeforeResult(ModelDriven action, Object model) {
            this.originalModel = model;
            this.action = action;
        }

        @Override
        public void beforeResult(ActionInvocation invocation, String resultCode) {
            ValueStack stack = invocation.getStack();
            CompoundRoot root = stack.getRoot();
            boolean needsRefresh = true;
            Object newModel = this.action.getModel();
            if (newModel != null) {
                for (Object item : root) {
                    if (item != newModel) continue;
                    needsRefresh = false;
                    break;
                }
            }
            if (needsRefresh) {
                if (this.originalModel != null) {
                    root.remove(this.originalModel);
                }
                if (newModel != null) {
                    stack.push(newModel);
                }
            }
        }
    }
}

