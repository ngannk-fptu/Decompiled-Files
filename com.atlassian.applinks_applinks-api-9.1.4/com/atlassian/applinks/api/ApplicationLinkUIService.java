/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.ApplicationLink;

public interface ApplicationLinkUIService {
    public MessageBuilder authorisationRequest(ApplicationLink var1);

    public static enum MessageFormat {
        INLINE,
        BANNER;

    }

    public static interface MessageBuilder {
        public MessageBuilder format(MessageFormat var1);

        public MessageBuilder contentHtml(String var1);

        public String getHtml();
    }
}

