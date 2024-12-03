/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="property", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.PropertyTag", description="Print out expression which evaluates against the stack")
public class Property
extends Component {
    private static final Logger LOG = LogManager.getLogger(Property.class);
    private String defaultValue;
    private String value;
    private boolean escapeHtml = true;
    private boolean escapeJavaScript = false;
    private boolean escapeXml = false;
    private boolean escapeCsv = false;

    public Property(ValueStack stack) {
        super(stack);
    }

    @StrutsTagAttribute(description="The default value to be used if <u>value</u> attribute is null")
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @StrutsTagAttribute(description="Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscapeHtml(boolean escape) {
        this.escapeHtml = escape;
    }

    @StrutsTagAttribute(description="Whether to escape Javascript", type="Boolean", defaultValue="false")
    public void setEscapeJavaScript(boolean escapeJavaScript) {
        this.escapeJavaScript = escapeJavaScript;
    }

    @StrutsTagAttribute(description="Value to be displayed", type="Object", defaultValue="&lt;top of stack&gt;")
    public void setValue(String value) {
        this.value = value;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @StrutsTagAttribute(description="Whether to escape CSV (useful to escape a value for a column)", type="Boolean", defaultValue="false")
    public void setEscapeCsv(boolean escapeCsv) {
        this.escapeCsv = escapeCsv;
    }

    @StrutsTagAttribute(description="Whether to escape XML", type="Boolean", defaultValue="false")
    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    @Override
    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        String actualValue = null;
        this.value = this.value == null ? "top" : this.stripExpression(this.value);
        actualValue = (String)this.getStack().findValue(this.value, String.class, this.throwExceptionOnELFailure);
        try {
            if (actualValue != null) {
                writer.write(this.prepare(actualValue));
            } else if (this.defaultValue != null) {
                writer.write(this.prepare(this.defaultValue));
            }
        }
        catch (IOException e) {
            LOG.info("Could not print out value '{}'", (Object)this.value, (Object)e);
        }
        return result;
    }

    private String prepare(String value) {
        String result = value;
        if (this.escapeHtml) {
            result = StringEscapeUtils.escapeHtml4((String)result);
        }
        if (this.escapeJavaScript) {
            result = StringEscapeUtils.escapeEcmaScript((String)result);
        }
        if (this.escapeXml) {
            result = StringEscapeUtils.escapeXml10((String)result);
        }
        if (this.escapeCsv) {
            result = StringEscapeUtils.escapeCsv((String)result);
        }
        return result;
    }
}

