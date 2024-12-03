/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  javax.ws.rs.core.UriBuilder
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.audit.uri;

import com.atlassian.confluence.api.impl.service.audit.uri.ResourceUriGenerator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.UriBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserUriGenerator
implements ResourceUriGenerator {
    private final ConfluenceUserResolver userResolver;

    public UserUriGenerator(ConfluenceUserResolver userResolver) {
        this.userResolver = userResolver;
    }

    @Override
    public Map<String, URI> generate(URI baseUrl, Set<String> identifiers) {
        Set uniqueIdentifiers = identifiers.stream().map(UserKey::new).collect(Collectors.toSet());
        Map<String, String> usernameByUserKey = this.userResolver.getUsersByUserKeys((List<UserKey>)ImmutableList.copyOf(uniqueIdentifiers)).stream().filter(u -> u.getLowerName() != null).collect(Collectors.toMap(u -> u.getKey().getStringValue(), ConfluenceUser::getLowerName));
        return Maps.transformValues(usernameByUserKey, userKey -> this.userUri(baseUrl, (String)userKey));
    }

    private @NonNull URI userUri(URI baseUrl, String key) {
        return UriBuilder.fromUri((URI)baseUrl).path("admin").path("users").path("viewuser.action").queryParam("username", new Object[]{key}).build(new Object[0]);
    }
}

