/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.FormButton;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="submit", tldTagClass="org.apache.struts2.views.jsp.ui.SubmitTag", description="Render a submit button", allowDynamicAttributes=true)
public class Submit
extends FormButton {
    private static final Logger LOG = LogManager.getLogger(Submit.class);
    public static final String OPEN_TEMPLATE = "submit";
    public static final String TEMPLATE = "submit-close";
    protected String src;

    public Submit(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateParams() {
        if (this.key == null && this.value == null) {
            this.value = "Submit";
        }
        if (this.key != null && this.value == null) {
            this.value = "%{getText('" + this.key + "')}";
        }
        super.evaluateParams();
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.src != null) {
            this.addParameter("src", this.findString(this.src));
        }
        this.addParameter("escapeHtmlBody", this.escapeHtmlBody);
    }

    @Override
    protected boolean supportsImageType() {
        return true;
    }

    @StrutsTagAttribute(description="Supply an image src for <i>image</i> type submit button. Will have no effect for types <i>input</i> and <i>button</i>.")
    public void setSrc(String src) {
        this.src = src;
    }

    @StrutsTagAttribute(description="Specifies whether to HTML-escape the tag body or not", type="Boolean", defaultValue="false")
    public void setEscapeHtmlBody(boolean escapeHtmlBody) {
        this.escapeHtmlBody = escapeHtmlBody;
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean end(Writer writer, String body) {
        this.evaluateParams();
        try {
            this.addParameter("body", body);
            this.mergeTemplate(writer, this.buildTemplateName(this.template, this.getDefaultTemplate()));
        }
        catch (Exception e) {
            LOG.error("error when rendering", (Throwable)e);
        }
        finally {
            this.popComponentStack();
        }
        return false;
    }
}

