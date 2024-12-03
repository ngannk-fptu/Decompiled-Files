/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class ContentUrlFunction
implements SoyServerFunction<String> {
    private static final LinkType DEFAULT_LINK_TYPE = LinkType.WEB_UI;

    public String apply(Object ... args) {
        Content content = this.extractArgument(args, 0, Content.class);
        LinkType linkType = args.length == 1 ? DEFAULT_LINK_TYPE : this.extractArgument(args, 1, LinkType.class);
        return ((Link)content.getLinks().get(linkType)).getPath();
    }

    private <TYPE> TYPE extractArgument(Object[] arguments, int index, Class<TYPE> type) {
        Object argument = arguments[index];
        if (type.isInstance(argument)) {
            return type.cast(argument);
        }
        throw new IllegalArgumentException(String.format("Expected argument %s to be of type %s, instead got %s", index, type, argument.getClass().getName()));
    }

    public String getName() {
        return "contentUrl";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)1, (Object)2);
    }
}

