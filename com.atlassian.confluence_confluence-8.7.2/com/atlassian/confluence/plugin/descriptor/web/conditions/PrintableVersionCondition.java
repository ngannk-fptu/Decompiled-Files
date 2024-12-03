/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class PrintableVersionCondition
extends BaseConfluenceCondition {
    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        HttpServletRequest request = ServletActionContext.getRequest();
        if (request == null) {
            return false;
        }
        return StringUtils.equalsIgnoreCase((CharSequence)request.getParameter("decorator"), (CharSequence)"printable");
    }
}

