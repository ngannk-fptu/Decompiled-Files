/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.File;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class FileTag
extends AbstractUITag {
    private static final long serialVersionUID = -2154950640215144864L;
    protected String accept;
    protected String size;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new File(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        File file = (File)this.component;
        file.setAccept(this.accept);
        file.setSize(this.size);
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setSize(String size) {
        this.size = size;
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
        this.accept = null;
        this.size = null;
    }
}

