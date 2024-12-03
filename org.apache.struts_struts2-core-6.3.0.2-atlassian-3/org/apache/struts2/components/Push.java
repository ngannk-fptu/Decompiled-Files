/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="push", tldTagClass="org.apache.struts2.views.jsp.PushTag", description="Push value on stack for simplified usage.")
public class Push
extends Component {
    protected String value;
    protected boolean pushed;

    public Push(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        ValueStack stack = this.getStack();
        if (stack != null) {
            stack.push(this.findValue(this.value, "value", "You must specify a value to push on the stack. Example: person"));
            this.pushed = true;
        } else {
            this.pushed = false;
        }
        return result;
    }

    @Override
    public boolean end(Writer writer, String body) {
        ValueStack stack = this.getStack();
        if (this.pushed && stack != null) {
            stack.pop();
        }
        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="Value to push on stack", required=true)
    public void setValue(String value) {
        this.value = value;
    }
}

