/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.audit.uri;

import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.util.GeneralUtil;
import java.net.URI;
import java.net.URISyntaxException;
import org.checkerframework.checker.nullness.qual.NonNull;

class UriGeneratorHelper {
    UriGeneratorHelper() {
    }

    static @NonNull URI contentUri(@NonNull URI baseUri, @NonNull Addressable content) {
        try {
            String contentUrl = content instanceof AbstractPage ? GeneralUtil.getIdBasedPageUrl((AbstractPage)content) : content.getUrlPath();
            return new URI(baseUri + contentUrl);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Unable to generate content URI", e);
        }
    }
}

