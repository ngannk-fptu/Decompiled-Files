/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Bean;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ContextBeanTag;

public class BeanTag
extends ContextBeanTag {
    private static final long serialVersionUID = -3863152522071209267L;
    protected static Logger LOG = LogManager.getLogger(BeanTag.class);
    protected String name;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Bean(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        ((Bean)this.component).setName(this.name);
    }

    public void setName(String name) {
        this.name = name;
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
    }
}

