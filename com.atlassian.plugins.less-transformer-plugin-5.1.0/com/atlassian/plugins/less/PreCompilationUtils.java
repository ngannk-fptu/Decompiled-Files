/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.plugins.less;

import com.atlassian.lesscss.spi.UriResolver;
import com.google.common.annotations.VisibleForTesting;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PreCompilationUtils {
    private static final String PROP_USE_PRE_COMPILE = "atlassian.lesscss.use.precompiled";

    private PreCompilationUtils() {
        throw new UnsupportedOperationException();
    }

    public static URI resolvePreCompiledUri(UriResolver uriResolver, URI unCompiledUri) {
        if (!PreCompilationUtils.isPreCompileEnabled()) {
            return null;
        }
        for (URI uri : PreCompilationUtils.getPrecompiledAlternatives(unCompiledUri)) {
            if (!uriResolver.exists(uri)) continue;
            return uri;
        }
        return null;
    }

    @VisibleForTesting
    static List<URI> getPrecompiledAlternatives(URI unCompiledUri) {
        String unCompiledLocation = unCompiledUri.getSchemeSpecificPart();
        int lastDot = unCompiledLocation.lastIndexOf(46);
        if (lastDot == -1) {
            return Collections.emptyList();
        }
        String locationWithoutExtension = unCompiledLocation.substring(0, lastDot);
        LinkedList<URI> precompiledUris = new LinkedList<URI>();
        for (String extension : Arrays.asList(".less.css", "-less.css")) {
            try {
                String newSchemeSpecificPart = locationWithoutExtension + extension;
                URI uri = new URI(unCompiledUri.getScheme(), newSchemeSpecificPart, unCompiledUri.getFragment());
                precompiledUris.add(unCompiledUri.resolve(uri));
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return precompiledUris;
    }

    private static boolean isPreCompileEnabled() {
        String prop = System.getProperty(PROP_USE_PRE_COMPILE);
        Boolean usePreCompiled = prop != null ? Boolean.valueOf(Boolean.parseBoolean(prop)) : null;
        return !Boolean.getBoolean("atlassian.dev.mode") || Boolean.TRUE.equals(usePreCompiled) || Boolean.FALSE.equals(usePreCompiled);
    }
}

