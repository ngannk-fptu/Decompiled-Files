/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="if", tldTagClass="org.apache.struts2.views.jsp.IfTag", description="If tag")
public class If
extends Component {
    public static final String ANSWER = "struts.if.answer";
    Boolean answer;
    String test;

    @StrutsTagAttribute(description="Expression to determine if body of tag is to be displayed", type="Boolean", required=true)
    public void setTest(String test) {
        this.test = test;
    }

    public If(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean start(Writer writer) {
        this.answer = (Boolean)this.findValue(this.test, Boolean.class);
        if (this.answer == null) {
            this.answer = Boolean.FALSE;
        }
        this.stack.getContext().put(ANSWER, this.answer);
        return this.answer;
    }

    @Override
    public boolean end(Writer writer, String body) {
        this.stack.getContext().put(ANSWER, this.answer);
        return super.end(writer, body);
    }
}

