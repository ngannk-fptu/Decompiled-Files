/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.xstream.security;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamInternal;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class XStreamSecurityConfigurator {
    private static final Logger log = LoggerFactory.getLogger(XStreamSecurityConfigurator.class);
    private static final String ALLOW_LIST_EXTRA_PROPERTY = "xstream.allowlist.extra";
    private static final String WHITE_LIST_EXTRA_DEPRECATED_PROPERTY = "xstream.whitelist.extra";
    private static final Pattern CSV_PATTERN = Pattern.compile("\\s*,\\s*");
    private static final Set<String> ALLOWED_TYPES = ImmutableSet.of((Object)"com.atlassian.core.util.PairType", (Object)"javax.mail.internet.InternetAddress");
    private static final Set<String> ALLOWED_PACKAGES = ImmutableSet.of((Object)"com.atlassian.**", (Object)"java.util.concurrent.atomic.**", (Object)"com.gliffy.plugin.confluence.**", (Object)"com.balsamiq.mockups.**", (Object)"com.adaptavist.confluence.**", (Object)"org.swift.confluence.**", (Object[])new String[0]);
    private final Map<String, Set<String>> types = new HashMap<String, Set<String>>();
    private final Map<String, Set<String>> typesByRegExps = new HashMap<String, Set<String>>();
    private final Map<String, Set<String>> wildcards = new HashMap<String, Set<String>>();
    private final Set<String> userConfiguredAllowedPackages = this.getUserConfiguredAllowedPackages();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void configureXStreamSecurity(ConfluenceXStreamInternal confluenceXStream) {
        confluenceXStream.setUpDefaultSecurity();
        confluenceXStream.allowTypes(ALLOWED_TYPES.toArray(new String[0]));
        confluenceXStream.allowTypesByWildcard(ALLOWED_PACKAGES.toArray(new String[0]));
        confluenceXStream.allowTypesByWildcard(this.userConfiguredAllowedPackages.toArray(new String[0]));
        XStreamSecurityConfigurator xStreamSecurityConfigurator = this;
        synchronized (xStreamSecurityConfigurator) {
            confluenceXStream.allowTypes(this.mergeSets(this.types).toArray(new String[0]));
            confluenceXStream.allowTypesByRegExp(this.mergeSets(this.typesByRegExps).toArray(new String[0]));
            confluenceXStream.allowTypesByWildcard(this.mergeSets(this.wildcards).toArray(new String[0]));
        }
    }

    public synchronized void addAllowTypes(String pluginKey, Set<String> types) {
        this.types.computeIfAbsent(pluginKey, k -> new HashSet()).addAll(types);
    }

    public synchronized void addAllowTypesByRegExps(String pluginKey, Set<String> typesByRegExps) {
        this.typesByRegExps.computeIfAbsent(pluginKey, k -> new HashSet()).addAll(typesByRegExps);
    }

    public synchronized void addAllowTypesByWildcard(String pluginKey, Set<String> wildcards) {
        this.wildcards.computeIfAbsent(pluginKey, k -> new HashSet()).addAll(wildcards);
    }

    public synchronized void clearPluginSecurityData(String pluginKey) {
        this.types.remove(pluginKey);
        this.typesByRegExps.remove(pluginKey);
        this.wildcards.remove(pluginKey);
    }

    private @NonNull Set<String> getUserConfiguredAllowedPackages() {
        String allowListExtra = System.getProperty(ALLOW_LIST_EXTRA_PROPERTY, this.getWhiteListExtra());
        return Optional.ofNullable(allowListExtra).map(s -> CSV_PATTERN.splitAsStream(allowListExtra).map(st -> st.trim()).filter(f -> StringUtils.isNotBlank((CharSequence)f)).map(string -> string.concat(".**")).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet))).orElse(Collections.emptySet());
    }

    private @NonNull Set<String> mergeSets(Map<String, Set<String>> stringSets) {
        HashSet<String> strings = new HashSet<String>();
        stringSets.values().stream().forEach(strings::addAll);
        return strings;
    }

    @Deprecated
    private String getWhiteListExtra() {
        String extraWhiteList = System.getProperty(WHITE_LIST_EXTRA_DEPRECATED_PROPERTY);
        if (extraWhiteList != null) {
            log.warn("xstream.whitelist.extra usage is deprecated. Please use 'xstream.allowlist.extra' instead.");
        }
        return extraWhiteList;
    }
}

