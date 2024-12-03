/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.web.WebItemView
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.web.WebViewService
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.web.WebViewService;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;

public class ItemsForSectionFunction
implements SoyServerFunction<Iterable<WebItemView>> {
    private WebViewService webViewService;

    public ItemsForSectionFunction(WebViewService webViewService) {
        this.webViewService = webViewService;
    }

    public String getName() {
        return "itemsForSection";
    }

    public Iterable<WebItemView> apply(Object ... args) {
        Maybe<ContentId> contentId = this.computeContentId(args[0]);
        String section = (String)args[1];
        ImmutableMap.Builder contextBuilder = ImmutableMap.builder();
        if (args.length == 3) {
            contextBuilder.putAll((Map)args[2]);
        }
        ImmutableMap additionalContext = contextBuilder.build();
        return this.webViewService.forContent((ContentId)contentId.getOrNull(), (Map)additionalContext).getItemsForSection(section, (Map)additionalContext);
    }

    private Maybe<ContentId> computeContentId(Object arg) {
        ContentId contentIdObject;
        if (arg == null) {
            return Option.none();
        }
        if (arg instanceof String) {
            String contentId = (String)arg;
            try {
                contentIdObject = ContentId.deserialise((String)contentId);
            }
            catch (BadRequestException e) {
                throw new RuntimeException("Error deserializing contentId [" + contentId + "]", e);
            }
        } else if (arg instanceof ContentId) {
            contentIdObject = (ContentId)arg;
        } else {
            throw new IllegalArgumentException("argument 0 to soy function '" + this.getName() + "' must be of type String, or [" + ContentId.class.getName() + "]. Got [" + arg.getClass().getName() + "]");
        }
        return Option.some((Object)contentIdObject);
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)2, (Object)3);
    }
}

