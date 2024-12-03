/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.people.SubjectType;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class ContentRestriction
implements NavigationAware {
    @JsonIgnore
    private static final Map<SubjectType, PageResponse<Subject>> RESTRICTIONS_EMPTY_MAP_COLLAPSED_ENTRIES = ModelMapBuilder.newInstance().addCollapsedEntries(SubjectType.VALUES).build();
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Content.class)
    @JsonProperty
    private final Reference<Content> content;
    @JsonProperty
    private final OperationKey operation;
    @JsonProperty
    @JsonDeserialize(as=ExpandedReference.class, contentAs=SubjectRestrictions.class)
    private final Reference<SubjectRestrictions> restrictions;

    @JsonCreator
    private ContentRestriction() {
        this(ContentRestriction.builder());
    }

    private ContentRestriction(ContentRestrictionBuilder builder) {
        this.content = builder.content;
        this.operation = builder.operation;
        this.restrictions = builder.restrictionsAsInnerTypeReference();
    }

    public static ContentRestrictionBuilder builder() {
        return new ContentRestrictionBuilder();
    }

    public static ContentRestrictionBuilder builder(ContentRestriction restriction) {
        return new ContentRestrictionBuilder(restriction);
    }

    public Reference<Content> getContent() {
        return this.content;
    }

    public OperationKey getOperation() {
        return this.operation;
    }

    public Map<SubjectType, PageResponse<Subject>> getRestrictions() {
        if (this.restrictions == null || !this.restrictions.isExpanded()) {
            return BuilderUtils.collapsedMap();
        }
        SubjectRestrictions subjectRestrictions = this.restrictions.get();
        if (subjectRestrictions == null) {
            return RESTRICTIONS_EMPTY_MAP_COLLAPSED_ENTRIES;
        }
        return ModelMapBuilder.newInstance().copy(subjectRestrictions.innerMapOfTruth).build();
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return navigationService.createNavigation().content(this.content).restrictionByOperation().operation(this.operation);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ContentRestriction)) {
            return false;
        }
        ContentRestriction that = (ContentRestriction)obj;
        return Objects.equals(this.content, that.content) && Objects.equals(this.operation, that.operation) && Objects.equals(this.restrictions, that.restrictions);
    }

    public int hashCode() {
        return Objects.hash(this.content, this.operation, this.restrictions);
    }

    public static class Expansions {
        public static final String CONTENT = "content";
        public static final String RESTRICTIONS = "restrictions";
    }

    public static class ContentRestrictionBuilder {
        private Reference<Content> content = Reference.empty(Content.class);
        private OperationKey operation = null;
        private Optional<Map<SubjectType, PageResponse<Subject>>> maybeRestrictions = Optional.empty();

        private ContentRestrictionBuilder() {
        }

        private ContentRestrictionBuilder(ContentRestriction restriction) {
            this.content(restriction.getContent());
            this.operation(restriction.getOperation());
            this.restrictions(restriction.getRestrictions());
        }

        public ContentRestrictionBuilder content(Reference<Content> content) {
            this.content = content;
            return this;
        }

        public ContentRestrictionBuilder operation(OperationKey operation) {
            this.operation = operation;
            return this;
        }

        public ContentRestrictionBuilder restrictions(@Nullable Map<SubjectType, PageResponse<Subject>> restrictions) {
            this.maybeRestrictions = Optional.ofNullable(restrictions).map(r -> {
                ModelMapBuilder builder = ModelMapBuilder.newInstance();
                return builder.copy(r).build();
            });
            return this;
        }

        public ContentRestriction build() {
            return new ContentRestriction(this);
        }

        private @NonNull Reference<SubjectRestrictions> restrictionsAsInnerTypeReference() {
            return this.maybeRestrictions.map(r -> {
                if (r instanceof Collapsed) {
                    return Reference.collapsed(SubjectRestrictions.class);
                }
                return Reference.to(new SubjectRestrictions((Map)r));
            }).orElse(Reference.empty(SubjectRestrictions.class));
        }
    }

    @RestEnrichable
    @ExperimentalApi
    @JsonIgnoreProperties(ignoreUnknown=true)
    private static class SubjectRestrictions {
        @JsonProperty(value="user")
        private final PageResponse<User> user;
        @JsonProperty(value="group")
        private final PageResponse<Group> group;
        @JsonIgnore
        private final Map<SubjectType, PageResponse<Subject>> innerMapOfTruth;

        @JsonCreator
        private SubjectRestrictions(@JsonProperty(value="user") PageResponse<User> users, @JsonProperty(value="group") PageResponse<Group> groups) {
            this.user = users == null ? BuilderUtils.collapsedPageResponse(null) : users;
            this.group = groups == null ? BuilderUtils.collapsedPageResponse(null) : groups;
            ModelMapBuilder<SubjectType, PageResponseImpl<Subject>> innerMapBuilder = ModelMapBuilder.newInstance();
            if (users != null && !(users instanceof Collapsed)) {
                innerMapBuilder.put(SubjectType.USER, PageResponseImpl.transformResponse(users, u -> u));
            } else {
                innerMapBuilder.addCollapsedEntry(SubjectType.USER);
            }
            if (groups != null && !(groups instanceof Collapsed)) {
                innerMapBuilder.put(SubjectType.GROUP, PageResponseImpl.transformResponse(groups, g -> g));
            } else {
                innerMapBuilder.addCollapsedEntry(SubjectType.GROUP);
            }
            this.innerMapOfTruth = innerMapBuilder.build();
        }

        private SubjectRestrictions(@NonNull Map<SubjectType, PageResponse<Subject>> restrictionsMap) {
            Set collapsedItems;
            if (restrictionsMap instanceof Collapsed) {
                String err = "issue while building ContentRestriction. CollapsedMap passed where not expected.";
                throw new ServiceException("issue while building ContentRestriction. CollapsedMap passed where not expected.", new IllegalArgumentException("issue while building ContentRestriction. CollapsedMap passed where not expected."));
            }
            this.innerMapOfTruth = restrictionsMap;
            Set<Object> set = collapsedItems = restrictionsMap instanceof EnrichableMap ? ((EnrichableMap)restrictionsMap).getCollapsedEntries() : Collections.emptySet();
            if (collapsedItems.contains(SubjectType.USER) || restrictionsMap.get(SubjectType.USER) == null) {
                this.user = BuilderUtils.collapsedPageResponse(null);
            } else {
                PageResponse<Subject> shouldBeAllUsers = restrictionsMap.get(SubjectType.USER);
                this.user = shouldBeAllUsers;
            }
            if (collapsedItems.contains(SubjectType.GROUP) || restrictionsMap.get(SubjectType.GROUP) == null) {
                this.group = BuilderUtils.collapsedPageResponse(null);
            } else {
                PageResponse<Subject> shouldBeAllGroups = restrictionsMap.get(SubjectType.GROUP);
                this.group = shouldBeAllGroups;
            }
        }

        public static enum IdProperties {
            user,
            group;

        }
    }
}

