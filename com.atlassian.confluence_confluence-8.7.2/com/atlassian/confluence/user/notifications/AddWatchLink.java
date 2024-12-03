/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user.notifications;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import java.nio.charset.StandardCharsets;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class AddWatchLink {
    private static final String PAGE_URL = "/users/addpagenotification.action";
    private static final String SPACE_URL = "/users/addspacenotification.action";
    private final Either<AbstractPage, Pair<Space, Maybe<ContentTypeEnum>>> targetContent;

    public AddWatchLink(AbstractPage page) {
        this.targetContent = Either.left((Object)page);
    }

    public AddWatchLink(Space space, @Nullable ContentTypeEnum contentType) {
        this.targetContent = Either.right((Object)Pair.pair((Object)space, (Object)Option.option((Object)((Object)contentType))));
    }

    public String getUrl() {
        return (String)this.targetContent.fold(page -> AddWatchLink.getUrlForPage(page), spaceAndContentType -> AddWatchLink.getUrlForSpace((Space)spaceAndContentType.left(), (Maybe<ContentTypeEnum>)((Maybe)spaceAndContentType.right())));
    }

    private static String getUrlForSpace(Space space, Maybe<ContentTypeEnum> optionalContentType) {
        UrlBuilder builder = new UrlBuilder(SPACE_URL, StandardCharsets.UTF_8).add("spaceKey", space.getKey());
        optionalContentType.foreach(contentType -> builder.add("contentType", contentType.getRepresentation()));
        return builder.toString();
    }

    private static String getUrlForPage(AbstractPage page) {
        return new UrlBuilder(PAGE_URL, StandardCharsets.UTF_8).add("pageId", page.getId()).toString();
    }

    public Addressable getContent() {
        return (Addressable)this.targetContent.fold(page -> page, spaceAndContentType -> (ConfluenceEntityObject)spaceAndContentType.left());
    }

    public String getContentName() {
        return this.getContent().getDisplayTitle();
    }
}

