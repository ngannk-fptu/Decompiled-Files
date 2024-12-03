/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.api.common.Option;
import java.net.URI;

public interface FeedRendererContext {
    public String getAnonymousUserName();

    public String getDefaultFeedAuthor();

    public String getDefaultFeedTitle();

    public Iterable<Integer> getDefaultUserPictureSizes();

    public Option<URI> getUserPictureUri(Option<URI> var1, int var2, String var3);

    public boolean isDeveloperMode();
}

