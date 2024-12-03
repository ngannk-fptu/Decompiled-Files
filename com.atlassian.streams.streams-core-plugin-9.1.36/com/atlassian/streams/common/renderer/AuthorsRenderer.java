/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.spi.renderer.Renderers
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.streams.common.renderer;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.common.renderer.CompoundStatementRenderer;
import com.atlassian.streams.spi.renderer.Renderers;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

final class AuthorsRenderer
implements Function<Iterable<UserProfile>, Html> {
    private final Function<Iterable<UserProfile>, Option<Html>> compoundRenderer;
    private final I18nResolver i18nResolver;

    public AuthorsRenderer(I18nResolver i18nResolver, TemplateRenderer templateRenderer, boolean authorStyle) {
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.compoundRenderer = new CompoundStatementRenderer<UserProfile>(i18nResolver, new UserProfileRenderer((TemplateRenderer)Preconditions.checkNotNull((Object)templateRenderer, (Object)"templateRenderer"), authorStyle));
    }

    @HtmlSafe
    public Html apply(Iterable<UserProfile> authors) {
        return (Html)((Option)this.compoundRenderer.apply(authors)).getOrElse((Object)this.renderUnknownAuthor());
    }

    private Html renderUnknownAuthor() {
        return new Html(this.i18nResolver.getText("streams.authors.unknown"));
    }

    private final class UserProfileRenderer
    implements Function<UserProfile, Option<Html>> {
        private final TemplateRenderer templateRenderer;
        private final boolean authorStyle;

        private UserProfileRenderer(TemplateRenderer templateRenderer, boolean authorStyle) {
            this.templateRenderer = templateRenderer;
            this.authorStyle = authorStyle;
        }

        public Option<Html> apply(UserProfile userProfile) {
            return Option.some((Object)new Html(Renderers.render((TemplateRenderer)this.templateRenderer, (String)"user-profile-link.vm", (Map)ImmutableMap.of((Object)"userProfile", (Object)userProfile, (Object)"authorStyle", (Object)this.authorStyle))));
        }
    }
}

