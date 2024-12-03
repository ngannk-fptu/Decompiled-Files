/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriTemplateHandler;

public interface UriBuilderFactory
extends UriTemplateHandler {
    public UriBuilder uriString(String var1);

    public UriBuilder builder();
}

