/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.internal.common.net;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public final class Uris {
    private static final Pattern REDUNDANT_SLASHES = Pattern.compile("//+");

    private Uris() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    @Nonnull
    public static String concatenate(@Nonnull String base, String ... paths) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(paths, "paths");
        return StringUtils.stripEnd((String)base, (String)"/") + Uris.removeRedundantSlashes("/" + StringUtils.join((Object[])paths, (String)"/"));
    }

    @Nonnull
    public static URI concatenate(@Nonnull URI base, String ... paths) throws URISyntaxException {
        Objects.requireNonNull(base, "base");
        return new URI(Uris.concatenate(base.toASCIIString(), paths));
    }

    public static URI uncheckedConcatenate(URI base, String ... components) throws IllegalArgumentException {
        try {
            return Uris.concatenate(base, components);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Failed to concatenate %s to form URI (%s)", base, e.getReason()), e);
        }
    }

    @Nullable
    public static String removeRedundantSlashes(@Nullable String path) {
        return path == null ? null : REDUNDANT_SLASHES.matcher(path).replaceAll("/");
    }

    @Nonnull
    public static Iterable<String> toComponents(@Nullable String uri) {
        String processed = StringUtils.strip((String)Uris.removeRedundantSlashes(uri), (String)"/");
        if (StringUtils.isEmpty((CharSequence)processed)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(processed.split("/")));
    }

    @Nonnull
    public static String utf8Encode(@Nonnull String string) {
        Objects.requireNonNull(string, "string");
        try {
            return URLEncoder.encode(string, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 not installed", e);
        }
    }

    @Nonnull
    public static String utf8Encode(@Nonnull URI uri) {
        return Uris.utf8Encode(uri.toASCIIString());
    }
}

