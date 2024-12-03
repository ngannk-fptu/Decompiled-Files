/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Entity
 *  javax.ws.rs.core.UriBuilder
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.audit.uri;

import com.atlassian.confluence.api.impl.service.audit.uri.ResourceUriGenerator;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.Entity;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.UriBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

public class GroupUriGenerator
implements ResourceUriGenerator {
    private final UserAccessor userAccessor;

    public GroupUriGenerator(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Override
    public Map<String, URI> generate(URI baseUrl, Set<String> identifiers) {
        List<String> groupNames = identifiers.stream().collect(Collectors.toList());
        return this.userAccessor.getGroupsByGroupNames(groupNames).stream().map(Entity::getName).filter(Objects::nonNull).filter(identifiers::contains).collect(Collectors.toMap(group -> group, group -> this.groupUri(baseUrl, (String)group)));
    }

    private @NonNull URI groupUri(URI baseUrl, String group) {
        return UriBuilder.fromUri((URI)baseUrl).path("admin").path("users").path("domembersofgroupsearch.action").queryParam("membersOfGroupTerm", new Object[]{group}).build(new Object[0]);
    }
}

