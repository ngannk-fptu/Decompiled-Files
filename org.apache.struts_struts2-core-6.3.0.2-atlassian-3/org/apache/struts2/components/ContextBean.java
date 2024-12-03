/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

public abstract class ContextBean
extends Component {
    protected String var;

    public ContextBean(ValueStack stack) {
        super(stack);
    }

    protected void putInContext(Object value) {
        if (StringUtils.isNotBlank((CharSequence)this.var)) {
            this.stack.getContext().put(this.var, value);
        }
    }

    @StrutsTagAttribute(description="Name used to reference the value pushed into the Value Stack (scope: action).")
    public void setVar(String var) {
        if (var != null) {
            this.var = this.findString(var);
        }
    }

    protected String getVar() {
        return this.var;
    }
}

