/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="param", tldTagClass="org.apache.struts2.views.jsp.ParamTag", description="Parametrize other tags")
public class Param
extends Component {
    protected String name;
    protected String value;
    protected boolean suppressEmptyParameters;

    public Param(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean end(Writer writer, String body) {
        Component component = this.findAncestor(Component.class);
        if (this.value != null) {
            if (component instanceof UnnamedParametric) {
                ((UnnamedParametric)((Object)component)).addParameter(this.findValue(this.value));
            } else {
                boolean reevaluate;
                String translatedName = this.findString(this.name);
                if (translatedName == null) {
                    throw new StrutsException("No name found for following expression: " + this.name);
                }
                boolean evaluated = !translatedName.equals(this.name);
                boolean bl = reevaluate = !evaluated || this.isAcceptableExpression(translatedName);
                if (!reevaluate) {
                    throw new StrutsException("Excluded or not accepted name found: " + translatedName);
                }
                Object foundValue = this.findValue(this.value);
                if (this.suppressEmptyParameters) {
                    if (foundValue != null && StringUtils.isNotBlank((CharSequence)foundValue.toString())) {
                        component.addParameter(translatedName, foundValue);
                    } else {
                        component.addParameter(translatedName, null);
                    }
                } else if (foundValue == null || StringUtils.isBlank((CharSequence)foundValue.toString())) {
                    component.addParameter(translatedName, "");
                } else {
                    component.addParameter(translatedName, foundValue);
                }
            }
        } else if (component instanceof UnnamedParametric) {
            ((UnnamedParametric)((Object)component)).addParameter(body);
        } else if (!this.suppressEmptyParameters || !StringUtils.isBlank((CharSequence)body)) {
            component.addParameter(this.findString(this.name), body);
        } else {
            component.addParameter(this.findString(this.name), null);
        }
        return super.end(writer, "");
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    @StrutsTagAttribute(description="Name of Parameter to set")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="Value expression for Parameter to set", defaultValue="The value of evaluating provided name against stack")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="Whether to suppress empty parameters", type="Boolean", defaultValue="false")
    public void setSuppressEmptyParameters(boolean suppressEmptyParameters) {
        this.suppressEmptyParameters = suppressEmptyParameters;
    }

    public static interface UnnamedParametric {
        public void addParameter(Object var1);
    }
}

