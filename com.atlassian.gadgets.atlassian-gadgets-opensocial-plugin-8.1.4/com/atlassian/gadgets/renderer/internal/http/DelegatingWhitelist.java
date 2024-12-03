/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.spi.Whitelist
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.gadgets.renderer.internal.http;

import com.atlassian.gadgets.opensocial.spi.Whitelist;
import com.atlassian.sal.api.user.UserKey;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DelegatingWhitelist
implements Whitelist {
    private final Iterable<Whitelist> optionalWhitelists;
    private final Whitelist messageBundleWhiteList;

    public DelegatingWhitelist(Whitelist messageBundleWhiteList, Iterable<Whitelist> optionalWhitelists) {
        this.messageBundleWhiteList = Objects.requireNonNull(messageBundleWhiteList, "messageBundleWhiteList");
        this.optionalWhitelists = Objects.requireNonNull(optionalWhitelists, "optionalWhitelists");
    }

    public boolean allows(URI uri, UserKey userKey) {
        Objects.requireNonNull(uri, "uri");
        return Stream.concat(Stream.of(this.messageBundleWhiteList), StreamSupport.stream(this.optionalWhitelists.spliterator(), false)).anyMatch(whitelist -> whitelist.allows(uri, userKey));
    }
}

