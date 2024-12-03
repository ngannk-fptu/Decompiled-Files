/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view;

import com.atlassian.confluence.content.render.xhtml.model.links.CreatePageLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.ModelToRenderedClassMapper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xhtml.api.Link;

public class ViewModelToRenderedClassMapper
implements ModelToRenderedClassMapper {
    private static final String USER_MENTION_CLASSES = "confluence-userlink user-mention";
    private static final String CURRENT_USER_MENTION_CLASSES = "confluence-userlink user-mention current-user-mention";

    @Override
    public String getRenderedClass(Link link) {
        if (link instanceof CreatePageLink) {
            return "createlink";
        }
        if (link instanceof UnresolvedLink) {
            return "unresolved";
        }
        if (link.getDestinationResourceIdentifier() instanceof UserResourceIdentifier) {
            UserResourceIdentifier userResourceIdentifier = (UserResourceIdentifier)link.getDestinationResourceIdentifier();
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            if (user != null && user.getKey().equals((Object)userResourceIdentifier.getUserKey())) {
                return CURRENT_USER_MENTION_CLASSES;
            }
            return USER_MENTION_CLASSES;
        }
        return null;
    }
}

