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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Form;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="file", tldTagClass="org.apache.struts2.views.jsp.ui.FileTag", description="Render a file input field", allowDynamicAttributes=true)
public class File
extends UIBean {
    private static final Logger LOG = LogManager.getLogger(File.class);
    public static final String TEMPLATE = "file";
    protected String accept;
    protected String size;

    public File(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateParams() {
        super.evaluateParams();
        Form form = (Form)this.findAncestor(Form.class);
        if (form != null) {
            String method;
            String encType = (String)form.getParameters().get("enctype");
            if (!"multipart/form-data".equals(encType)) {
                LOG.warn("Struts has detected a file upload UI tag (s:file) being used without a form set to enctype 'multipart/form-data'. This is probably an error!");
            }
            if (!"post".equalsIgnoreCase(method = (String)form.getParameters().get("method"))) {
                LOG.warn("Struts has detected a file upload UI tag (s:file) being used without a form set to method 'POST'. This is probably an error!");
            }
        }
        if (this.accept != null) {
            this.addParameter("accept", this.findString(this.accept));
        }
        if (this.size != null) {
            this.addParameter("size", this.findString(this.size));
        }
    }

    @StrutsTagAttribute(description="HTML accept attribute to indicate accepted file mimetypes")
    public void setAccept(String accept) {
        this.accept = accept;
    }

    @StrutsTagAttribute(description="HTML size attribute", required=false, type="Integer")
    public void setSize(String size) {
        this.size = size;
    }
}

