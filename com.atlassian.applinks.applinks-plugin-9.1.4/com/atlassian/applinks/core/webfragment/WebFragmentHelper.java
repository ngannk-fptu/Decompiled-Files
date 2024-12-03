/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.core.webfragment;

import com.atlassian.applinks.core.rest.model.WebItemEntityList;
import com.atlassian.applinks.core.rest.model.WebPanelEntityList;
import com.atlassian.applinks.core.webfragment.WebFragmentContext;

public interface WebFragmentHelper {
    public static final String APPLICATION_LINK_LIST_OPERATION = "applinks.application.link.list.operation";
    public static final String ENTITY_LINK_LIST_OPERATION = "applinks.entity.link.list.operation";

    public WebItemEntityList getWebItemsForLocation(String var1, WebFragmentContext var2);

    public WebPanelEntityList getWebPanelsForLocation(String var1, WebFragmentContext var2);
}

