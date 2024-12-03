/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="elseif", tldTagClass="org.apache.struts2.views.jsp.ElseIfTag", description="Elseif tag")
public class ElseIf
extends Component {
    protected Boolean answer;
    protected String test;

    public ElseIf(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean start(Writer writer) {
        Boolean ifResult = (Boolean)this.stack.getContext().get("struts.if.answer");
        if (ifResult == null || ifResult.booleanValue()) {
            return false;
        }
        this.answer = (Boolean)this.findValue(this.test, Boolean.class);
        if (this.answer == null) {
            this.answer = Boolean.FALSE;
        }
        if (this.answer.booleanValue()) {
            this.stack.getContext().put("struts.if.answer", this.answer);
        }
        return this.answer;
    }

    @Override
    public boolean end(Writer writer, String body) {
        if (this.answer == null) {
            this.answer = Boolean.FALSE;
        }
        if (this.answer.booleanValue()) {
            this.stack.getContext().put("struts.if.answer", this.answer);
        }
        return super.end(writer, "");
    }

    @StrutsTagAttribute(description="Expression to determine if body of tag is to be displayed", type="Boolean", required=true)
    public void setTest(String test) {
        this.test = test;
    }
}

