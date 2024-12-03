/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.web.WebSectionView
 *  com.atlassian.confluence.api.service.web.WebViewService
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.api.model.web.WebSectionView;
import com.atlassian.confluence.api.service.web.WebViewService;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;

public class SectionsForLocationFunction
implements SoyServerFunction<Iterable<WebSectionView>> {
    private WebViewService webViewService;

    public SectionsForLocationFunction(WebViewService webViewService) {
        this.webViewService = webViewService;
    }

    public String getName() {
        return "sectionsForLocation";
    }

    public Iterable<WebSectionView> apply(Object ... args) {
        String contentId = args[0] == null ? null : (String)args[0];
        String location = (String)args[1];
        ImmutableMap.Builder contextBuilder = ImmutableMap.builder();
        if (args.length == 3) {
            contextBuilder.putAll((Map)args[2]);
        }
        ImmutableMap additionalContext = contextBuilder.build();
        return this.webViewService.forContent(contentId).getSectionsForLocation(location, (Map)additionalContext);
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)2, (Object)3);
    }
}

