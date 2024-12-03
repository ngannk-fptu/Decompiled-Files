/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Param;
import org.apache.struts2.views.jsp.ComponentTagSupport;

public class ParamTag
extends ComponentTagSupport {
    private static final long serialVersionUID = -968332732207156408L;
    protected String name;
    protected String value;
    protected boolean suppressEmptyParameters;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Param(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Param param = (Param)this.component;
        param.setName(this.name);
        param.setValue(this.value);
        param.setSuppressEmptyParameters(this.suppressEmptyParameters);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSuppressEmptyParameters(boolean suppressEmptyParameters) {
        this.suppressEmptyParameters = suppressEmptyParameters;
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
        this.name = null;
        this.value = null;
        this.suppressEmptyParameters = false;
    }
}

