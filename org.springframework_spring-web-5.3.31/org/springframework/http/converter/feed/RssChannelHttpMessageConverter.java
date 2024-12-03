/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.rss.Channel
 */
package org.springframework.http.converter.feed;

import com.rometools.rome.feed.rss.Channel;
import org.springframework.http.MediaType;
import org.springframework.http.converter.feed.AbstractWireFeedHttpMessageConverter;

public class RssChannelHttpMessageConverter
extends AbstractWireFeedHttpMessageConverter<Channel> {
    public RssChannelHttpMessageConverter() {
        super(MediaType.APPLICATION_RSS_XML);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Channel.class.isAssignableFrom(clazz);
    }
}

