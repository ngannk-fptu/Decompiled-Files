/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public abstract class AbstractClosingTag
extends AbstractUITag {
    protected String openTemplate;

    @Override
    protected void populateParams() {
        super.populateParams();
        ((ClosingUIBean)this.component).setOpenTemplate(this.openTemplate);
    }

    public void setOpenTemplate(String openTemplate) {
        this.openTemplate = openTemplate;
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
        this.openTemplate = null;
    }
}

