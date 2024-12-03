/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.views.jsp;

import org.apache.struts2.components.ContextBean;
import org.apache.struts2.views.jsp.ComponentTagSupport;

public abstract class ContextBeanTag
extends ComponentTagSupport {
    private String var;

    @Override
    protected void populateParams() {
        super.populateParams();
        ContextBean bean = (ContextBean)this.component;
        bean.setVar(this.var);
    }

    public void setVar(String var) {
        this.var = var;
    }

    @Override
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        super.clearTagStateForTagPoolingServers();
        this.var = null;
    }
}

