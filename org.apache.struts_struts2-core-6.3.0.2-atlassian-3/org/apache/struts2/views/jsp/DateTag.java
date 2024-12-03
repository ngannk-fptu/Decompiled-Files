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
import org.apache.struts2.components.Date;
import org.apache.struts2.views.jsp.ContextBeanTag;

public class DateTag
extends ContextBeanTag {
    private static final long serialVersionUID = -6216963123295613440L;
    protected String name;
    protected String format;
    protected boolean nice;
    protected String timezone;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Date(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Date d = (Date)this.component;
        d.setName(this.name);
        d.setFormat(this.format);
        d.setNice(this.nice);
        d.setTimezone(this.timezone);
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setNice(boolean nice) {
        this.nice = nice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
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
        this.format = null;
        this.nice = false;
        this.timezone = null;
    }
}

