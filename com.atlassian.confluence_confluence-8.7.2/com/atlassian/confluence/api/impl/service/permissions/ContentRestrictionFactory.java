/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.Subject
 *  com.atlassian.confluence.api.model.people.SubjectType
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction$ContentRestrictionBuilder
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestList$Builder
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.common.collect.ImmutableSet
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.permissions;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.people.SubjectType;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.fugue.Option;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContentRestrictionFactory
extends ModelFactory<ContentPermissionSet, ContentRestriction> {
    private final PersonFactory personFactory;
    private final Supplier<ContentFactory> contentFactorySupplier;
    private final NavigationService navigationService;
    private static final ImmutableBiMap<OperationKey, String> opKeyToPermissionBiMap = ImmutableBiMap.of((Object)OperationKey.READ, (Object)"View", (Object)OperationKey.UPDATE, (Object)"Edit");

    public ContentRestrictionFactory(PersonFactory personFactory, Supplier<ContentFactory> contentFactorySupplier, NavigationService navigationService) {
        this.personFactory = personFactory;
        this.contentFactorySupplier = contentFactorySupplier;
        this.navigationService = navigationService;
    }

    public ContentRestriction buildFrom(@NonNull ContentPermissionSet set, LimitedRequest limitedRequest, Expansions expansions) {
        Preconditions.checkNotNull((Object)set);
        OperationKey operationKey = ContentRestrictionFactory.determineOpKey(set.getType());
        Fauxpansions contentExpansions = Fauxpansions.fauxpansions(expansions, "content");
        Fauxpansions restrictionsExpansions = Fauxpansions.fauxpansions(expansions, "restrictions");
        ContentEntityObject ceo = set.getOwningContent();
        Reference contentRef = contentExpansions.canExpand() ? Reference.to((Object)((ContentFactory)this.contentFactorySupplier.get()).buildFrom(ceo, contentExpansions.getSubExpansions())) : (ceo instanceof ContentConvertible && ((ContentConvertible)((Object)ceo)).shouldConvertToContent() ? Content.buildReference((ContentSelector)ceo.getSelector()) : Content.buildReference((ContentSelector)ContentSelector.from((Content)Content.builder().id(ceo.getContentId()).build())));
        ContentRestriction.ContentRestrictionBuilder restrictionBuilder = ContentRestriction.builder().content(contentRef).operation(operationKey);
        if (restrictionsExpansions.canExpand()) {
            Navigation.Builder navBuilder = this.navigationService.createNavigation().content(contentRef).restrictionByOperation().operation(operationKey);
            restrictionBuilder.restrictions(this.buildRestrictions(set, limitedRequest, restrictionsExpansions.getSubExpansions(), navBuilder));
        } else {
            restrictionBuilder.restrictions(BuilderUtils.collapsedMap());
        }
        return restrictionBuilder.build();
    }

    private Map<SubjectType, PageResponse<Subject>> buildRestrictions(ContentPermissionSet set, LimitedRequest limitedRequest, Expansions expansions, Navigation.Builder navBuilder) {
        List subjects;
        int limit = limitedRequest.getLimit();
        int start = limitedRequest.getStart();
        HashMap<SubjectType, List> rawRestrictions = new HashMap<SubjectType, List>();
        for (ContentPermission contentPermission : set) {
            SubjectType subjectType = contentPermission.isUserPermission() ? SubjectType.USER : SubjectType.GROUP;
            if (!expansions.canExpand(subjectType.toString()) || (subjects = rawRestrictions.computeIfAbsent(subjectType, st -> new ArrayList())).size() == limit + 1) continue;
            if (SubjectType.USER.equals((Object)subjectType) && contentPermission.getUserSubject() != null) {
                subjects.add(this.personFactory.forUser(contentPermission.getUserSubject()));
                continue;
            }
            if (SubjectType.GROUP.equals((Object)subjectType) && contentPermission.getGroupName() != null) {
                subjects.add(new Group(contentPermission.getGroupName()));
                continue;
            }
            String err = "unsupported SubjectType or malformed contentPermissionObject";
            throw new ServiceException("unsupported SubjectType or malformed contentPermissionObject", (Throwable)new IllegalArgumentException("unsupported SubjectType or malformed contentPermissionObject"));
        }
        ModelMapBuilder restrictionsModelMapBuilder = ModelMapBuilder.newInstance();
        for (SubjectType subjectType : SubjectType.VALUES) {
            if (expansions.canExpand(subjectType.toString())) {
                subjects = (List)rawRestrictions.get(subjectType);
                RestList.Builder pageResponseBuilder = RestList.newRestList();
                pageResponseBuilder.pageRequest((PageRequest)new SimplePageRequest(limitedRequest)).navigationAware(navBuilder);
                if (subjects != null && start < subjects.size()) {
                    List subList = subjects.subList(start, Math.min(start + limit, subjects.size()));
                    boolean hasMore = subjects.size() > start + limit;
                    pageResponseBuilder.results(subList, hasMore);
                }
                restrictionsModelMapBuilder.put((Object)subjectType, (Object)pageResponseBuilder.build());
                continue;
            }
            restrictionsModelMapBuilder.addCollapsedEntry((Object)subjectType);
        }
        return restrictionsModelMapBuilder.build();
    }

    @Override
    public ContentRestriction buildFrom(ContentPermissionSet hibernateObject, Expansions expansions) {
        return this.buildFrom(hibernateObject, LimitedRequestImpl.create((int)PaginationLimits.restrictionSubjects()), expansions);
    }

    public static OperationKey determineOpKey(String permissionType) {
        OperationKey opKeyFromPreDefinedMap = (OperationKey)opKeyToPermissionBiMap.inverse().get((Object)permissionType);
        if (opKeyFromPreDefinedMap != null) {
            return opKeyFromPreDefinedMap;
        }
        return OperationKey.valueOf((String)permissionType);
    }

    @Deprecated
    public static Option<String> determinePermissionType(OperationKey key) {
        return Option.option((Object)((String)opKeyToPermissionBiMap.get((Object)key)));
    }

    public static Optional<String> extractPermissionType(OperationKey key) {
        return Optional.ofNullable((String)opKeyToPermissionBiMap.get((Object)key));
    }

    public static ImmutableSet<OperationKey> getSupportedOperationKeys() {
        return opKeyToPermissionBiMap.keySet();
    }
}

