/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.JspException
 */
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.IteratorComponent;
import org.apache.struts2.views.jsp.ContextBeanTag;

public class IteratorTag
extends ContextBeanTag {
    private static final long serialVersionUID = -1827978135193581901L;
    protected String statusAttr;
    protected String value;
    protected String begin;
    protected String end;
    protected String step;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new IteratorComponent(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        IteratorComponent tag = (IteratorComponent)this.getComponent();
        tag.setStatus(this.statusAttr);
        tag.setValue(this.value);
        tag.setBegin(this.begin);
        tag.setEnd(this.end);
        tag.setStep(this.step);
    }

    public void setStatus(String status) {
        this.statusAttr = status;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public int doEndTag() throws JspException {
        this.component = null;
        this.clearTagStateForTagPoolingServers();
        return 6;
    }

    public int doAfterBody() throws JspException {
        boolean again = this.component.end((Writer)this.pageContext.getOut(), this.getBody());
        if (again) {
            return 2;
        }
        if (this.bodyContent != null) {
            try {
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
            }
            catch (Exception e) {
                throw new JspException((Throwable)e);
            }
        }
        return 0;
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
        this.statusAttr = null;
        this.value = null;
        this.begin = null;
        this.end = null;
        this.step = null;
    }
}

