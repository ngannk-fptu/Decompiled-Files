/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  com.google.common.io.LineReader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.less;

import com.atlassian.plugins.less.UriResolverManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.common.io.LineReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UriDependencyCollector {
    private static final Pattern IMPORT_URI_PATTERN = Pattern.compile("@import(?:-once)?\\s+(?:\\(([^)]+)\\)\\s+)?\"([^\"]+)\";");
    private static final int GROUP_IMPORT_OPTIONS = 1;
    private static final int GROUP_IMPORT_URI = 2;
    private static final Logger log = LoggerFactory.getLogger(UriDependencyCollector.class);
    private final UriResolverManager uriResolverManager;

    public UriDependencyCollector(UriResolverManager uriResolverManager) {
        this.uriResolverManager = uriResolverManager;
    }

    public Set<URI> getDependencies(URI baseUri) {
        LinkedHashSet collector = Sets.newLinkedHashSet();
        try (InputStreamReader reader = new InputStreamReader(this.uriResolverManager.getResolverOrThrow(baseUri).open(baseUri));){
            String line;
            LineReader lineReader = new LineReader((Readable)reader);
            while ((line = lineReader.readLine()) != null) {
                this.collectUris(collector, baseUri, line);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return Collections.unmodifiableSet(collector);
    }

    @VisibleForTesting
    void collectUris(Set<URI> collector, URI baseUri, String chunk) {
        Matcher matcher = IMPORT_URI_PATTERN.matcher(chunk);
        while (matcher.find()) {
            ImportOption importOption = ImportOption.fromString(matcher.group(1));
            String path = matcher.group(2);
            if (path == null) continue;
            URI uri = baseUri.resolve(path = importOption.amendURI(path));
            if (this.uriResolverManager.isUriSupported(uri)) {
                collector.add(uri);
                continue;
            }
            log.warn("Ignoring LESS uri as it is not supported. uri={}", (Object)uri);
        }
    }

    private static enum ImportOption {
        CSS,
        INLINE{

            @Override
            String amendURI(String uri) {
                if (!uri.endsWith(".css")) {
                    uri = uri + ".css";
                }
                return uri;
            }
        }
        ,
        LESS,
        NONE{

            @Override
            String amendURI(String uri) {
                if (!uri.endsWith(".less")) {
                    uri = uri + ".less";
                }
                return uri;
            }
        };


        String amendURI(String uri) {
            return uri;
        }

        private static ImportOption fromString(String value) {
            if (value != null) {
                value = value.toUpperCase(Locale.US);
                for (ImportOption importOption : ImportOption.values()) {
                    if (!importOption.name().equals(value)) continue;
                    return importOption;
                }
            }
            return NONE;
        }
    }
}

