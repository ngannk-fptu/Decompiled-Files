/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.sal.api.message.I18nResolver;

public final class RenderContextBuilderUtils {
    public static RendererContextBuilder createContextBuilder(ApplicationLink applicationLink, I18nResolver i18nResolver, InternalHostApplication internalHostApplication) {
        RendererContextBuilder builder = new RendererContextBuilder().put("localApplicationName", internalHostApplication.getName()).put("localApplicationType", i18nResolver.getText(internalHostApplication.getType().getI18nKey())).put("remoteApplicationName", applicationLink.getName()).put("remoteApplicationType", i18nResolver.getText(applicationLink.getType().getI18nKey()));
        return builder;
    }
}

