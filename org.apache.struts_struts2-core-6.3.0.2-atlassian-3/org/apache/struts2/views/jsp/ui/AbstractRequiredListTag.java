/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.ListUIBean;
import org.apache.struts2.views.jsp.ui.AbstractListTag;

public abstract class AbstractRequiredListTag
extends AbstractListTag {
    @Override
    protected void populateParams() {
        super.populateParams();
        ListUIBean listUIBean = (ListUIBean)this.component;
        listUIBean.setThrowExceptionOnNullValueAttribute(true);
    }

    @Override
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }
}

