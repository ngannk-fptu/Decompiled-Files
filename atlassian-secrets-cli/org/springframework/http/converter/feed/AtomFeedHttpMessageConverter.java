/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.atom.Feed
 */
package org.springframework.http.converter.feed;

import com.rometools.rome.feed.atom.Feed;
import org.springframework.http.MediaType;
import org.springframework.http.converter.feed.AbstractWireFeedHttpMessageConverter;

public class AtomFeedHttpMessageConverter
extends AbstractWireFeedHttpMessageConverter<Feed> {
    public AtomFeedHttpMessageConverter() {
        super(new MediaType("application", "atom+xml"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Feed.class.isAssignableFrom(clazz);
    }
}

