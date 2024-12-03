/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.fugue.Pair
 *  io.atlassian.fugue.Pair
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.fugue.Pair;

public interface WelcomeMessageService {
    public static final String WELCOME_MESSAGE_TEMPLATE_NAME = "Default Welcome Message";
    public static final String WELCOME_MESSAGE_TEMPLATE_KEY = "com.atlassian.confluence.plugins.system-templates:welcome-message";

    @HtmlSafe
    public String getWelcomeMessage();

    @Deprecated(forRemoval=true)
    @HtmlSafe
    public Pair<String, WebResourceDependenciesRecorder.RecordedResources> getWelcomeMessageResource();

    @Deprecated(forRemoval=true)
    @HtmlSafe
    default public io.atlassian.fugue.Pair<String, WebResourceDependenciesRecorder.RecordedResources> getResourceForWelcomeMessage() {
        return FugueConversionUtil.toIoPair(this.getWelcomeMessageResource());
    }

    public void saveWelcomeMessage(String var1);
}

