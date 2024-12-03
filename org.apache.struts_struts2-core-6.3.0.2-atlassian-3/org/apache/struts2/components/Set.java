/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="set", tldBodyContent="JSP", tldTagClass="org.apache.struts2.views.jsp.SetTag", description="Assigns a value to a variable in a specified scope")
public class Set
extends ContextBean {
    protected String scope;
    protected String value;
    protected boolean trimBody = true;

    public Set(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean end(Writer writer, String body) {
        ValueStack stack = this.getStack();
        Object o = this.value == null ? (body == null ? this.findValue("top") : body) : this.findValue(this.value);
        body = "";
        if ("application".equalsIgnoreCase(this.scope)) {
            stack.setValue("#application['" + this.getVar() + "']", o);
        } else if ("session".equalsIgnoreCase(this.scope)) {
            stack.setValue("#session['" + this.getVar() + "']", o);
        } else if ("request".equalsIgnoreCase(this.scope)) {
            stack.setValue("#request['" + this.getVar() + "']", o);
        } else if ("page".equalsIgnoreCase(this.scope)) {
            stack.setValue("#attr['" + this.getVar() + "']", o, false);
        } else {
            stack.getContext().put(this.getVar(), o);
            stack.setValue("#attr['" + this.getVar() + "']", o, false);
        }
        return super.end(writer, body);
    }

    @Override
    @StrutsTagAttribute(required=true, description="Name used to reference the value pushed into the Value Stack (default scope: action,<em>override</em> with the scope attribute).")
    public void setVar(String var) {
        super.setVar(var);
    }

    @StrutsTagAttribute(description="The scope in which to assign the variable. Can be <b>application</b>, <b>session</b>, <b>request</b>, <b>page</b>, or <b>action</b> (action scope <em>also</em> adds it to the page scope).", defaultValue="action")
    public void setScope(String scope) {
        this.scope = scope;
    }

    @StrutsTagAttribute(description="The value that is assigned to the variable named <i>name</i>")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="Set to false to prevent the default whitespace-trim of this tag's body content", type="Boolean", defaultValue="true")
    public void setTrimBody(boolean trimBody) {
        this.trimBody = trimBody;
    }

    @Override
    public boolean usesBody() {
        return true;
    }
}

