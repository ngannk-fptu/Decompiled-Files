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
import org.apache.struts2.components.Text;
import org.apache.struts2.views.jsp.ContextBeanTag;

public class TextTag
extends ContextBeanTag {
    private static final long serialVersionUID = -3075088084198264581L;
    protected String name;
    private boolean escapeHtml = false;
    private boolean escapeJavaScript = false;
    private boolean escapeXml = false;
    private boolean escapeCsv = false;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Text(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Text text = (Text)this.component;
        text.setName(this.name);
        text.setEscapeHtml(this.escapeHtml);
        text.setEscapeJavaScript(this.escapeJavaScript);
        text.setEscapeXml(this.escapeXml);
        text.setEscapeCsv(this.escapeCsv);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEscapeHtml(boolean escapeHtml) {
        this.escapeHtml = escapeHtml;
    }

    public void setEscapeJavaScript(boolean escapeJavaScript) {
        this.escapeJavaScript = escapeJavaScript;
    }

    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    public void setEscapeCsv(boolean escapeCsv) {
        this.escapeCsv = escapeCsv;
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
        this.escapeHtml = false;
        this.escapeJavaScript = false;
        this.escapeXml = false;
        this.escapeCsv = false;
    }
}

