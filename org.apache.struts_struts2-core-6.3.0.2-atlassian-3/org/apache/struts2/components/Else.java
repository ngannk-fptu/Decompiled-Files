/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import java.util.Map;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTag;

@StrutsTag(name="else", tldTagClass="org.apache.struts2.views.jsp.ElseTag", description="Else tag")
public class Else
extends Component {
    public Else(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean start(Writer writer) {
        Map<String, Object> context = this.stack.getContext();
        Boolean ifResult = (Boolean)context.get("struts.if.answer");
        context.remove("struts.if.answer");
        return ifResult != null && ifResult == false;
    }
}

