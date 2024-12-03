/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.rest.applink.data;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.webfragment.WebFragmentContext;
import com.atlassian.applinks.core.webfragment.WebFragmentHelper;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.rest.applink.data.AbstractSingleKeyRestApplinkDataProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

public class WebItemsDataProvider
extends AbstractSingleKeyRestApplinkDataProvider {
    public static final String WEB_ITEMS = "webItems";
    private final WebFragmentHelper webFragmentHelper;

    @Autowired
    public WebItemsDataProvider(WebFragmentHelper webFragmentHelper) {
        super(WEB_ITEMS);
        this.webFragmentHelper = webFragmentHelper;
    }

    @Override
    @Nullable
    public Object doProvide(@Nonnull ApplicationLink applink) throws ServiceException {
        return this.webFragmentHelper.getWebItemsForLocation("applinks.application.link.list.operation", new WebFragmentContext.Builder().applicationLink(applink).build()).getItems();
    }
}

