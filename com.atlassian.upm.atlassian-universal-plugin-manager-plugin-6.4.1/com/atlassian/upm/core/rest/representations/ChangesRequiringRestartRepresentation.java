/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.core.Change;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.representations.DefaultLinkBuilder;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ChangesRequiringRestartRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final List<ChangeRepresentation> changes;

    @JsonCreator
    public ChangesRequiringRestartRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="changes") List<ChangeRepresentation> changes) {
        this.links = ImmutableMap.copyOf(links);
        this.changes = ImmutableList.copyOf(changes);
    }

    ChangesRequiringRestartRepresentation(Iterable<Change> restartChanges, BaseUriBuilder uriBuilder, DefaultLinkBuilder linkBuilder) {
        this.links = linkBuilder.buildLinksFor(uriBuilder.buildChangesRequiringRestartUri(), false).build();
        Function<Change, ChangeRepresentation> toChangeRepresentation = change -> new ChangeRepresentation((Change)change, uriBuilder);
        this.changes = ImmutableList.copyOf((Collection)StreamSupport.stream(restartChanges.spliterator(), false).map(toChangeRepresentation).filter(arg_0 -> ((Predicate)Predicates.notNull()).apply(arg_0)).collect(Collectors.toList()));
    }

    public URI getSelf() {
        return this.links.get("self");
    }

    public List<ChangeRepresentation> getChanges() {
        return this.changes;
    }

    public static final class ChangeRepresentation {
        @JsonProperty
        private final Map<String, URI> links;
        @JsonProperty
        private final String name;
        @JsonProperty
        private final String key;
        @JsonProperty
        private final String action;

        @JsonCreator
        public ChangeRepresentation(@JsonProperty(value="name") String name, @JsonProperty(value="key") String key, @JsonProperty(value="action") String action, @JsonProperty(value="links") Map<String, URI> links) {
            this.name = name;
            this.key = key;
            this.action = action;
            this.links = ImmutableMap.copyOf(links);
        }

        public ChangeRepresentation(Change restartChange, BaseUriBuilder uriBuilder) {
            Plugin plugin = restartChange.getPlugin();
            this.name = plugin.getName();
            this.key = plugin.getKey();
            this.action = restartChange.getAction();
            this.links = ImmutableMap.of((Object)"self", (Object)uriBuilder.buildChangeRequiringRestart(plugin.getKey()), (Object)"plugin-icon", (Object)uriBuilder.buildPluginIconLocationUri(plugin.getKey()), (Object)"plugin-logo", (Object)uriBuilder.buildPluginLogoLocationUri(plugin.getKey()));
        }

        public URI getSelf() {
            return this.links.get("self");
        }

        public String getName() {
            return this.name;
        }

        public String getKey() {
            return this.key;
        }

        public String getAction() {
            return this.action;
        }

        public URI getPluginIconLink() {
            return this.links.get("plugin-icon");
        }

        public URI getPluginLogoLink() {
            return this.links.get("plugin-logo");
        }

        public String toString() {
            return "Change<" + this.getKey() + ", " + this.getAction() + ">";
        }
    }
}

