/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharelinks.widgetconnector;

import com.atlassian.confluence.plugins.sharelinks.widgetconnector.WidgetConnectorSupport;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class WidgetConnectorSupportImpl
implements WidgetConnectorSupport {
    private final Set<String> supportedDomains = ImmutableSet.of((Object)"www.youtube.com", (Object)"vids.myspace.com", (Object)"video.yahoo.com", (Object)"www.dailymotion.com", (Object)"app.episodic.com", (Object)"www.vimeo.com", (Object[])new String[]{"www.metacafe.com", "blip.tv", "www.viddler.com", "twitter.com"});

    @Override
    public boolean isSupported(String domain) {
        return this.supportedDomains.contains(domain);
    }
}

