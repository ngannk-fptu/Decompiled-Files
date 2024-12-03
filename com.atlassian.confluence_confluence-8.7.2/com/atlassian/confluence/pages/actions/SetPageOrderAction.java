/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.actions.AbstractCommandAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class SetPageOrderAction
extends AbstractCommandAction {
    private PageService pageService;
    private long pageId;
    private String orderedChildIds;

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() {
        return super.execute();
    }

    @Override
    protected ServiceCommand createCommand() {
        String[] idStrs = this.orderedChildIds.split(",");
        ArrayList<Long> childIds = new ArrayList<Long>(idStrs.length);
        for (String idStr : idStrs) {
            if (!StringUtils.isNotBlank((CharSequence)idStr)) continue;
            childIds.add(Long.parseLong(idStr));
        }
        return this.pageService.newSetPageOrderCommand(this.pageService.getIdPageLocator(this.pageId), childIds);
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public void setOrderedChildIds(String orderedChildIds) {
        this.orderedChildIds = orderedChildIds;
    }
}

