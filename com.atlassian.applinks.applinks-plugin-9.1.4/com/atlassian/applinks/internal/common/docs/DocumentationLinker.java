/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.docs;

import java.net.URI;
import java.util.Map;
import javax.annotation.Nonnull;

public interface DocumentationLinker {
    @Nonnull
    public URI getLink(String var1);

    @Nonnull
    public URI getLink(String var1, String var2);

    @Nonnull
    public String getOAuth2HelpLink(String var1);

    @Nonnull
    public URI getDocumentationBaseUrl();

    @Nonnull
    public Map<String, String> getAllLinkMappings();
}

