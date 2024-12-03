/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.Resources
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class Resources {
    private Resources() {
    }

    @Nonnull
    public static List<String> readLinesFromResources(String resourcePath) throws IOException {
        Objects.requireNonNull(resourcePath);
        ArrayList<String> ret = new ArrayList<String>();
        Enumeration<URL> resourceURLs = Resources.class.getClassLoader().getResources(resourcePath);
        while (resourceURLs.hasMoreElements()) {
            ret.addAll(com.google.common.io.Resources.readLines((URL)resourceURLs.nextElement(), (Charset)StandardCharsets.UTF_8));
        }
        return ret;
    }
}

